package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
//import com.upgrad.FoodOrderingApp.service.dao.RestaurantCategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
//import com.upgrad.FoodOrderingApp.service.entity.RestaurantCategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.InvalidRatingException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RestaurantService {

    @Autowired
    RestaurantDao restaurantDao;

   // @Autowired
    //private RestaurantCategoryDao restaurantCategoryDao;


    @Autowired
    CategoryDao categoryDao;
/*
    public List<RestaurantEntity> restaurantByCategory(String categoryId) throws CategoryNotFoundException {

        if (categoryId == null || categoryId == "") { //Checking for categoryId to be null or empty to throw exception.
            throw new CategoryNotFoundException("CNF-001", "Category id field should not be empty");
        }

        //Calls getCategoryByUuid of categoryDao to get list of CategoryEntity
        CategoryEntity categoryEntity = categoryDao.getCategoryById(categoryId);

        if (categoryEntity == null) {//Checking for categoryEntity to be null or empty to throw exception.
            throw new CategoryNotFoundException("CNF-002", "No category by this id");
        }

        //Calls getRestaurantByCategory of restaurantCategoryDao to get list of RestaurantCategoryEntity
        List<RestaurantCategoryEntity> restaurantCategoryEntities = restaurantCategoryDao.getRestaurantByCategory(categoryEntity);

        //Creating new restaurantEntity List and add only the restaurant for the corresponding category.
        List<RestaurantEntity> restaurantEntities = new LinkedList<>();
        restaurantCategoryEntities.forEach(restaurantCategoryEntity -> {
            restaurantEntities.add(restaurantCategoryEntity.getRestaurantId());
        });
        return restaurantEntities;
    }
    */

    public List<RestaurantEntity> restaurantsByRating() {

        //Calls restaurantsByRating of restaurantDao to get list of RestaurantEntity
        List<RestaurantEntity> restaurantEntities = restaurantDao.restaurantsByRating();
        return restaurantEntities;
    }

    public List<RestaurantEntity> getAllRestaurants() {
        return restaurantDao.getAllRestaurants();
    }

    public List<RestaurantEntity> restaurantsByName(String restName)throws RestaurantNotFoundException {
        if(restName.trim() == "") {
            throw new RestaurantNotFoundException("RNF-003","Restaurant name field should not be empty");
        } else {
            return restaurantDao.getAllRestaurantsByName(restName);
        }
    }

    public RestaurantEntity restaurantByUUID(String restUuid) throws RestaurantNotFoundException {
        if (restUuid.trim() == "") {
            throw new RestaurantNotFoundException("RNF-002","Restaurant id field should not be empty");
        } else {
            RestaurantEntity restEntity = restaurantDao.restaurantByUUID(restUuid);
            if(restEntity == null) {
                throw new RestaurantNotFoundException("RNF-001","No restaurant by this id");
            } else {
                return restEntity;
            }
        }
    }



    @Transactional(propagation = Propagation.REQUIRED)
    public RestaurantEntity updateRestaurantDetails(RestaurantEntity restaurantEntity, String authorization) throws RestaurantNotFoundException,InvalidRatingException,AuthorizationFailedException {
        CustomerAuthEntity userAuthToken = restaurantDao.getUserAuthToken(authorization);
        RestaurantEntity existingRestaurantEntity =  restaurantDao.restaurantByUUID(restaurantEntity.getUuid());
        if(userAuthToken == null) {
            throw new AuthorizationFailedException("ATHR-001","Customer is not Logged in.");
        } else if(userAuthToken.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002","Customer is logged out. Log in again to access this endpoint.");
        } else if(userAuthToken.getExpiresAt().isBefore(ZonedDateTime.now())) {
            throw new AuthorizationFailedException("ATHR-003","Your session is expired. Log in again to access this endpoint.");
        } else if(restaurantEntity.getUuid() == "") {
            throw new RestaurantNotFoundException("RNF-002", "Restaurant id field should not be empty");
        } else if(existingRestaurantEntity == null) {
            throw new RestaurantNotFoundException("RNF-001","No restaurant by this id");
        } else if(restaurantEntity.getCustomer_rating() == null || !(restaurantEntity.getCustomer_rating().compareTo(new Double(0)) > 0 && restaurantEntity.getCustomer_rating().compareTo(new Double(6)) < 0 ) ) {
            throw new InvalidRatingException("IRE-001","Restaurant should be in the range of 1 to 5");
        }
        int numOfCustomersRated = existingRestaurantEntity.getNumber_of_customers_rated() + 1;
        Double avgCustRating1 = existingRestaurantEntity.getCustomer_rating() + (restaurantEntity.getCustomer_rating());
        Double avgCustRating = avgCustRating1/(new Double(2));

        restaurantEntity.setCustomerRating(avgCustRating);
        restaurantEntity.setNumberCustomersRated(numOfCustomersRated);
        restaurantEntity.setAddress(existingRestaurantEntity.getAddress());
        restaurantEntity.setAvgPrice(existingRestaurantEntity.getAverage_price_for_two());
        restaurantEntity.setId(existingRestaurantEntity.getId());
        restaurantEntity.setPhotoUrl(existingRestaurantEntity.getPhoto_url());
        restaurantEntity.setCategories(existingRestaurantEntity.getCategories());
        restaurantEntity.setRestaurantName(existingRestaurantEntity.getRestaurant_name());
        return restaurantDao.updateRestaurantDetails(restaurantEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public RestaurantEntity updateRestaurantRating(RestaurantEntity restaurantEntity, Double customerRating) throws AuthorizationFailedException, InvalidRatingException, RestaurantNotFoundException {
        if (!isValidCustomerRating(customerRating.toString())) { //Checking for the rating to be valid
            throw new InvalidRatingException("IRE-001", "Restaurant should be in the range of 1 to 5");
        }

        DecimalFormat format = new DecimalFormat("##.0"); //keeping format to one decimal
        double restaurantRating = restaurantEntity.getCustomer_rating();
        Integer restaurantNoOfCustomerRated = restaurantEntity.getNumber_of_customers_rated();
        restaurantEntity.setNumberCustomersRated(restaurantNoOfCustomerRated + 1);

        //calculating the new customer rating as per the given data and formula
        double newCustomerRating = (restaurantRating * (restaurantNoOfCustomerRated.doubleValue()) + customerRating) / restaurantEntity.getNumber_of_customers_rated();

        restaurantEntity.setCustomerRating(Double.parseDouble(format.format(newCustomerRating)));

        //Updating the restautant in the db using the method updateRestaurantRating of restaurantDao.
        RestaurantEntity updatedRestaurantEntity = restaurantDao.updateRestaurantRating(restaurantEntity);

        return updatedRestaurantEntity;

    }


    public boolean isValidCustomerRating(String cutomerRating) {
        if (cutomerRating.equals("5.0")) {
            return true;
        }
        Pattern p = Pattern.compile("[1-4].[0-9]");
        Matcher m = p.matcher(cutomerRating);
        return (m.find() && m.group().equals(cutomerRating));
    }

}
