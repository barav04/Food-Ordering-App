package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.common.UnexpectedException;
import com.upgrad.FoodOrderingApp.service.dao.AddressDAO;
import com.upgrad.FoodOrderingApp.service.dao.CutomerDAO;
import com.upgrad.FoodOrderingApp.service.dao.OrderDAO;
import com.upgrad.FoodOrderingApp.service.dao.StateDAO;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.regex.Pattern;

import javax.persistence.NoResultException;

import static com.upgrad.FoodOrderingApp.service.common.GenericErrorCode.*;

@Service
public class AddressService {

    @Autowired
    AddressDAO addressDAO;

    @Autowired
    StateDAO stateDAO;

    @Autowired
    OrderDAO orderDAO;

    @Autowired
    CutomerDAO customerDAO;


    @Transactional(propagation = Propagation.REQUIRED)

    public AddressEntity saveAddress(AddressEntity address, String uuId, String authorization) throws SaveAddressException,  AuthorizationFailedException {
        CustomerAuthEntity customerAuthEntity = customerDAO.getUserAuthToken(authorization);
        if(customerAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001","Customer is not Logged in.");
        } else if(customerAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002","Customer is logged out. Log in again to access this endpoint.");
        } else if(customerAuthEntity.getExpiresAt().isBefore(ZonedDateTime.now())) {
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        } else if(!isPinCodeValid(address.getPincode())) {
            throw new SaveAddressException("SAR-002","Invalid pincode");
        }

        StateEntity stateEntity = getStateById(uuId);

        if(stateEntity==null)
            throw new SaveAddressException("ANF-002",
                    "No state by this id");

        address.setState(stateEntity);
        return addressDAO.saveAddress(address);

    }

    public AddressEntity saveAddress(AddressEntity address, StateEntity state) throws SaveAddressException {

        /*
        if (!isAddressFieldsEmpty(address))
            throw new SaveAddressException(SAR_001.getCode(), SAR_001.getDefaultMessage());


         */
        if(!isPinCodeValid(address.getPincode())) {
            throw new SaveAddressException("SAR-002","Invalid pincode");
        }

        address.setState(state);
        try {
            return addressDAO.saveAddress(address);
        } catch (Exception dataIntegrityViolationException) {
            throw new UnexpectedException(GEN_001, dataIntegrityViolationException);
        }
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity deleteAddressById(String addressId) throws AddressNotFoundException {
        if(addressId.isEmpty())
            throw new AddressNotFoundException("ANF-005",
                    "Address id can not be empty");

        AddressEntity addressEntity = getAddressById(addressId);

        if(addressEntity==null)
            throw new AddressNotFoundException("ANF-003",
                    "No address by this id");


        /**
         * id this address is not used in any orders
         *
         * delete the address
         *
         * else archive it
         * */

        List<OrderEntity> ordersByAddressId = orderDAO.fetchOrderByAddress(addressEntity);
       // ordersByAddressId.add(new OrderEntity());
        if(ordersByAddressId.size()>0) {
        	addressEntity.setActive(0);
        	 return addressDAO.archiveAddressById(addressEntity);
        }
        else {
        	  addressDAO.deleteAddressById(addressEntity);
        	  return addressEntity;
        	  
        }
      
    }

    public AddressEntity getAddressById(String addressId){
    	try {
    		return addressDAO.getAddressById(addressId);
    	} catch (NoResultException nre) {
    		return null;
    	}
    }



    /**
     *
     * util methods for validation checks
     *
     * on address entity
     *
     * */


    //this will return true is pin code is valid
    private boolean isPinCodeValid(String pinCode){
        Pattern digitPattern = Pattern.compile("\\d{6}");
        return digitPattern.matcher(pinCode).matches();
    }

    //this will return state if it's present in the database
    private StateEntity getStateById(String uuId){
        return stateDAO.getStateById(uuId);
    }


    public StateEntity getStateByUUID(String testUUID) {
        return stateDAO.getStateById(testUUID);
    }

    public AddressEntity getAddressByUUID(String s, CustomerEntity customerEntity) throws AddressNotFoundException {
        return addressDAO.getAddressById(s);
    }

    public AddressEntity deleteAddress(AddressEntity addressEntity) throws AddressNotFoundException {
    	return deleteAddressById(addressEntity.getUuid());
    }

    public List<AddressEntity> getAllAddress(CustomerEntity customerEntity) {
        return customerEntity.getAddress();
    }

    public List<StateEntity> getAllStates() {
        return stateDAO.getAllStates();
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAddressEntity addEntrytoCustomerAddress(CustomerEntity customerEntity, AddressEntity addressEntity) {
        CustomerAddressEntity customerAddressEntity = new CustomerAddressEntity();
        customerAddressEntity.setAddress(addressEntity);
        customerAddressEntity.setCustomer(customerEntity);
        return addressDAO.addEntrytoCustomerAddress(customerAddressEntity);
    }
}
