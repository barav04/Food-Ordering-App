package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class RestaurantDao {

    @PersistenceContext
    private EntityManager entityManager;

    public List<RestaurantEntity> getAllRestaurants() {
        return entityManager.createNamedQuery("allRestaurants", RestaurantEntity.class).getResultList();
    }

    public List<RestaurantEntity> getAllRestaurantsByName(String restName) {
        return  entityManager.createNamedQuery("restbyName", RestaurantEntity.class).setParameter("restaurant_name", "%" + restName + "%").getResultList();
    }

    public RestaurantEntity restaurantByUUID(String restUuid) {
        try {
            return entityManager.createNamedQuery("restByUuid", RestaurantEntity.class).setParameter("uuid", restUuid).getSingleResult();
        } catch(NoResultException nre) {
            return null;
        }
    }

    public CustomerAuthEntity getUserAuthToken(final String accessToken) {
        try {
            return entityManager.createNamedQuery("userAuthTokenByAccessToken", CustomerAuthEntity.class).setParameter("accessToken", accessToken).getSingleResult();
        } catch(NoResultException nre) {
            return null;
        }
    }

    public RestaurantEntity updateRestaurantDetails(RestaurantEntity restaurantEntity) {
        return entityManager.merge(restaurantEntity);
    }

    public List<RestaurantEntity> restaurantsByRating() {
        try {
            List<RestaurantEntity> restaurantEntities = entityManager.createNamedQuery("restaurantsByRating", RestaurantEntity.class).getResultList();
            return restaurantEntities;
        } catch (NoResultException nre) {
            return null;
        }
    }

    public RestaurantEntity updateRestaurantRating(RestaurantEntity restaurantEntity) {
        entityManager.merge(restaurantEntity);
        return restaurantEntity;
    }

}
