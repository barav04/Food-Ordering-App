package com.upgrad.FoodOrderingApp.service.business;

import com.upgrad.FoodOrderingApp.service.common.AppConstants;
import com.upgrad.FoodOrderingApp.service.common.UnexpectedException;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

import static com.upgrad.FoodOrderingApp.service.common.GenericErrorCode.*;

@Service
public class CustomerService {

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity saveCustomer(final CustomerEntity customerEntity) throws SignUpRestrictedException {

        if (!isValidEmail(customerEntity.getEmail())) {
            throw new SignUpRestrictedException(SGR_002.getCode(), SGR_002.getDefaultMessage());
        }

        if (!isValidContactNumber(customerEntity.getContactNumber())) {
            throw new SignUpRestrictedException(SGR_003.getCode(), SGR_003.getDefaultMessage());
        }

        if (!isStrongPassword(customerEntity.getPassword())) {
            throw new SignUpRestrictedException(SGR_004.getCode(), SGR_004.getDefaultMessage());
        }

        final String[] encryptedText = passwordCryptographyProvider.encrypt(customerEntity.getPassword());
        customerEntity.setSalt(encryptedText[0]);
        customerEntity.setPassword(encryptedText[1]);
        try {
            return customerDao.saveCustomer(customerEntity);
        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
            if (dataIntegrityViolationException.getCause() instanceof ConstraintViolationException) {
                String constraintName = ((ConstraintViolationException) dataIntegrityViolationException.getCause()).getConstraintName();

                if (StringUtils.containsIgnoreCase(constraintName, "customer_contact_number_key")) {
                    throw new SignUpRestrictedException(SGR_001.getCode(), SGR_001.getDefaultMessage());
                } else {
                    throw new UnexpectedException(GEN_001, dataIntegrityViolationException);
                }
            } else {
                throw new UnexpectedException(GEN_001, dataIntegrityViolationException);
            }
        } catch (Exception exception) {
            throw new UnexpectedException(GEN_001, exception);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity authenticate(final String contactNumber, final String password) throws AuthenticationFailedException {

        final CustomerEntity customerEntity = getCustomerByContactNumber(contactNumber);

        if (customerEntity == null) {
            throw new AuthenticationFailedException(ATH_001.getCode(), ATH_001.getDefaultMessage());
        }

        final String encryptedPassword = PasswordCryptographyProvider.encrypt(password, customerEntity.getSalt());

        if (encryptedPassword != null && encryptedPassword.equals(customerEntity.getPassword())) {

            // JWT
            final JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            final CustomerAuthEntity customerAuthEntity = new CustomerAuthEntity();
            customerAuthEntity.setCustomer(customerEntity);
            customerAuthEntity.setUuid(UUID.randomUUID().toString());
            final ZonedDateTime loginAt = ZonedDateTime.now();
            final ZonedDateTime expiresAt = loginAt.plusHours(AppConstants.EIGHT_8);
            customerAuthEntity.setLoginAt(loginAt.toLocalDateTime());
            customerAuthEntity.setExpiresAt(expiresAt.toLocalDateTime());
            customerAuthEntity.setAccessToken(jwtTokenProvider.generateToken(customerEntity.getUuid(), loginAt, expiresAt));
            return customerDao.saveCustomerAuthentication(customerAuthEntity);

        }

        else {
            throw new AuthenticationFailedException(ATH_002.getCode(), ATH_002.getDefaultMessage());
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity logout(final String accessToken) throws AuthorizationFailedException {
        final CustomerAuthEntity customerAuthEntity = getCustomerAuthenticationByAccessToken(accessToken);
        customerAuthEntity.setLogoutAt(LocalDateTime.now());
        return customerDao.saveCustomerAuthentication(customerAuthEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity getCustomer(final String accessToken) throws AuthorizationFailedException {
        final CustomerAuthEntity customerAuthEntity = getCustomerAuthenticationByAccessToken(accessToken);
        return customerAuthEntity.getCustomer();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity updateCustomer(final CustomerEntity customerEntity) {
        return customerDao.updateCustomer(customerEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity updateCustomerPassword(final String oldPassword, final String newPassword, final CustomerEntity customerEntity) throws UpdateCustomerException {
        if (!isStrongPassword(newPassword)) {
            throw new UpdateCustomerException(UCR_001.getCode(), UCR_001.getDefaultMessage());
        } else {
            final String encryptedOldPassword = PasswordCryptographyProvider.encrypt(oldPassword, customerEntity.getSalt());

            if (encryptedOldPassword != null && encryptedOldPassword.equals(customerEntity.getPassword())) {

                final String[] encryptedText = passwordCryptographyProvider.encrypt(newPassword);
                customerEntity.setSalt(encryptedText[0]);
                customerEntity.setPassword(encryptedText[1]);

                return customerDao.updateCustomer(customerEntity);
            } else {
                throw new UpdateCustomerException(UCR_004.getCode(), UCR_004.getDefaultMessage());
            }
        }
    }

    public CustomerAuthEntity getCustomerAuthenticationByAccessToken(final String accessToken) throws AuthorizationFailedException {

        final CustomerAuthEntity customerAuthEntity = customerDao.getCustomerAuthenticationByAccessToken(accessToken);
        if (customerAuthEntity != null) {

            if (customerAuthEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
                throw new AuthorizationFailedException(ATHR_003.getCode(), ATHR_003.getDefaultMessage());
            } else {

                if (customerAuthEntity.getLogoutAt() != null) {
                    throw new AuthorizationFailedException(ATHR_002.getCode(), ATHR_002.getDefaultMessage());
                } else {

                    return customerAuthEntity;
                }
            }
        }

        else {
            throw new AuthorizationFailedException(ATHR_001.getCode(), ATHR_001.getDefaultMessage());
        }
    }

    private CustomerEntity getCustomerByContactNumber(final String contactNumber) {
        return customerDao.getCustomerByContactNumber(contactNumber);
    }


    private boolean isStrongPassword(final String password) {
        return password.matches(AppConstants.REG_EXP_PASSWD_UPPER_CASE_CHAR) && password.matches(AppConstants.REG_EXP_PASSWD_SPECIAL_CHAR) && password.matches(AppConstants.REG_EXP_PASSWD_DIGIT) && (password.length() > AppConstants.SEVEN_7);
    }

    private boolean isValidContactNumber(final String contactNumber) {
        return StringUtils.isNumeric(contactNumber) && (contactNumber.length() == AppConstants.NUMBER_10);
    }

    private boolean isValidEmail(final String email) {
        return email.matches(AppConstants.REG_EXP_VALID_EMAIL);
    }


}