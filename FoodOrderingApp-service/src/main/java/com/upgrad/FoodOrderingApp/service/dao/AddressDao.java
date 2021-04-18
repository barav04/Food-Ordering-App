package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class AddressDao {

    @PersistenceContext
    private EntityManager entityManager;


    public AddressEntity saveAddress(final AddressEntity address) {
        entityManager.persist(address);
        return address;
    }


    public AddressEntity getAddressByAddressId(final String addressId) {
        try {
            return entityManager.createNamedQuery("fetchAddressById", AddressEntity.class)
                    .setParameter("addressId", addressId).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }


    public AddressEntity deleteAddress(final AddressEntity address) {
        entityManager.remove(address);
        return address;
    }


    public AddressEntity deactivateAddress(final AddressEntity address) {
        entityManager.merge(address);
        return address;
    }
}
