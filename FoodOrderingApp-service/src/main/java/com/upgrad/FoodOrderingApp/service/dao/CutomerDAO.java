package com.upgrad.FoodOrderingApp.service.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import org.springframework.stereotype.Repository;

import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;

@Repository
public class CutomerDAO {

	@PersistenceContext
	private EntityManager entityManager;

	public CustomerEntity saveCustomer(CustomerEntity customer) {
		entityManager.persist(customer);
		return customer;
	}

	public CustomerEntity getUserByPhoneNumber(final String phoneNumber) {
		try {
			return entityManager.createNamedQuery("customerByPhoneNumber", CustomerEntity.class)
					.setParameter("contact_number", phoneNumber).getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}

	public CustomerAuthEntity getUserAuthToken(final String accessToken) {
		try {
			return entityManager.createNamedQuery("userAuthTokenByAccessToken", CustomerAuthEntity.class)
					.setParameter("accessToken", accessToken).getSingleResult();
		} catch (NoResultException nre) {

			return null;
		}
	}

	public CustomerAuthEntity getCustomerAuthEntityTokenByUUID(final String UUID) {
		try {
			return entityManager.createNamedQuery("userAuthTokenByUUID", CustomerAuthEntity.class)
					.setParameter("uuid", UUID).getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}

	public CustomerEntity authenticateUser(final String phone, final String password) {
		try {
			return entityManager.createNamedQuery("authenticateUserQuery", CustomerEntity.class)
					.setParameter("contactNumber", phone).setParameter("password", password).getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}

	public CustomerAuthEntity createAuthToken(final CustomerAuthEntity customerAuthEntity) {
		entityManager.persist(customerAuthEntity);
		return customerAuthEntity;
	}

	public CustomerAuthEntity updateUserLogOut(final CustomerAuthEntity customerAuthEntity) {
		try {
			return entityManager.merge(customerAuthEntity);
		} catch (NoResultException nre) {
			return null;
		}
	}

	public CustomerAuthEntity updateLoginInfo(final CustomerAuthEntity userAuthTokenEntity) {
		try {
			return entityManager.merge(userAuthTokenEntity);
		} catch (NoResultException nre) {
			return null;
		}
	}

	public CustomerEntity updateCustomerDetails(CustomerEntity customer) {
		try {
			return entityManager.merge(customer);

		} catch (NoResultException nre) {
			return null;
		}
	}

}
