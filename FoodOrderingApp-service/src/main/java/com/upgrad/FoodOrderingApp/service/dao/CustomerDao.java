package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class CustomerDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CustomerEntity saveCustomer(final CustomerEntity customerEntity) {
        entityManager.persist(customerEntity);
        System.out.println(customerEntity.toString());
        return customerEntity;
    }

    public CustomerAuthEntity saveCustomerAuthentication(final CustomerAuthEntity customerAuthEntity) {
        entityManager.persist(customerAuthEntity);
        return customerAuthEntity;
    }

    public CustomerEntity getCustomerByContactNumber(final String contactNumber) {
        try {
            return entityManager.createNamedQuery("Customer.ByContact", CustomerEntity.class)
                    .setParameter("contactNumber", contactNumber)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public CustomerAuthEntity getCustomerAuthenticationByAccessToken(String accessToken) {
        try {
            return entityManager.createNamedQuery("Customer.ByAuthToken", CustomerAuthEntity.class)
                    .setParameter("accessToken", accessToken)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public CustomerEntity updateCustomer(final CustomerEntity customerEntity) {
        return entityManager.merge(customerEntity);
    }
}
