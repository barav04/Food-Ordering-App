package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.InvalidRatingException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private CategoryService categoryService;

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, path = "/restaurant",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse>getAllRestaurants() {
        final List<RestaurantEntity> restaurantEntities = restaurantService.getAllRestaurants();
        List<RestaurantList> restaurantLists = new ArrayList<>();
        RestaurantListResponse restaurantListResponse = new RestaurantListResponse();
        for(int i=0; i < restaurantEntities.size(); i++) {
            List<CategoryEntity> categoryEntities = restaurantEntities.get(i).getCategories();
            String categoryValues = "";
            for(int j = 0; j< categoryEntities.size(); j++){
                categoryValues = categoryValues + categoryEntities.get(j).getCategory_name() + ",";
                categoryValues = categoryValues.replace(",$", "");
            }
            String arr[] = categoryValues.split(",");
            Arrays.sort(arr);
            Arrays.toString(arr);
            String joinedSortedCategoriesValues = String.join(",",arr);
            restaurantLists.add(
                    new RestaurantList()
                            .id(UUID.fromString(restaurantEntities.get(i).getUuid()))
                            .restaurantName(restaurantEntities.get(i).getRestaurant_name())
                            .photoURL(restaurantEntities.get(i).getPhoto_url())
                            .customerRating(restaurantEntities.get(i).getCustomer_rating())
                            .averagePrice(restaurantEntities.get(i).getAverage_price_for_two())
                            .numberCustomersRated(restaurantEntities.get(i).getNumber_of_customers_rated())
                            .id(UUID.fromString(restaurantEntities.get(i).getUuid()))
                            .restaurantName(restaurantEntities.get(i).getRestaurant_name())
                            .address(new RestaurantDetailsResponseAddress()
                                    .id(UUID.fromString(restaurantEntities.get(i).getAddress().getUuid()))
                                    .flatBuildingName(restaurantEntities.get(i).getAddress().getFlatBuilNo())
                                    .locality(restaurantEntities.get(i).getAddress().getLocality())
                                    .city(restaurantEntities.get(i).getAddress().getCity())
                                    .pincode(restaurantEntities.get(i).getAddress().getPincode())
                                    .state(new RestaurantDetailsResponseAddressState().id(UUID.fromString(restaurantEntities.get(i).getAddress().getState().getUuid()))
                                            .stateName(restaurantEntities.get(i).getAddress().getState().getState_name())
                                    )
                            )
                            .categories(joinedSortedCategoriesValues)
            );
        }
        restaurantListResponse.setRestaurants(restaurantLists);
        return new ResponseEntity(restaurantListResponse, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, path = "/restaurant/name/{restaurant_name}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> restaurantsByName(@PathVariable("restaurant_name") final String restaurantName)throws RestaurantNotFoundException {
        String lowerrestaurantName = restaurantName.toLowerCase();
        final List<RestaurantEntity> restbyNameEntitiesList = restaurantService.restaurantsByName(lowerrestaurantName);
        List<RestaurantList> restaurantLists = new ArrayList<>();
        RestaurantListResponse restaurantListResponse = new RestaurantListResponse();
        for(int i=0; i < restbyNameEntitiesList.size(); i++) {
            List<CategoryEntity> categoryEntities = restbyNameEntitiesList.get(i).getCategories();
            String categoryValues = "";
            for(int j = 0; j< categoryEntities.size(); j++){
                categoryValues = categoryValues + categoryEntities.get(j).getCategory_name() + ",";
                categoryValues = categoryValues.replace(",$", "");
            }
            String arr[] = categoryValues.split(",");
            Arrays.sort(arr);
            Arrays.toString(arr);
            String joinedSortedCategoriesValues = String.join(",",arr);

            restaurantLists.add(
                    new RestaurantList()
                            .id(UUID.fromString(restbyNameEntitiesList.get(i).getUuid()))
                            .restaurantName(restbyNameEntitiesList.get(i).getRestaurant_name())
                            .photoURL(restbyNameEntitiesList.get(i).getPhoto_url())
                            .customerRating(restbyNameEntitiesList.get(i).getCustomer_rating())
                            .averagePrice(restbyNameEntitiesList.get(i).getAverage_price_for_two())
                            .numberCustomersRated(restbyNameEntitiesList.get(i).getNumber_of_customers_rated())
                            .address(new RestaurantDetailsResponseAddress()
                                    .id(UUID.fromString(restbyNameEntitiesList.get(i).getAddress().getUuid()))
                                    .flatBuildingName(restbyNameEntitiesList.get(i).getAddress().getFlatBuilNo())
                                    .locality(restbyNameEntitiesList.get(i).getAddress().getLocality())
                                    .city(restbyNameEntitiesList.get(i).getAddress().getCity())
                                    .pincode(restbyNameEntitiesList.get(i).getAddress().getPincode())
                                    .state(new RestaurantDetailsResponseAddressState().id(UUID.fromString(restbyNameEntitiesList.get(i).getAddress().getState().getUuid()))
                                            .stateName(restbyNameEntitiesList.get(i).getAddress().getState().getState_name())
                                    )
                            )
                            .categories(joinedSortedCategoriesValues)
            );
        }
        restaurantListResponse.setRestaurants(restaurantLists);
        return new ResponseEntity(restaurantListResponse, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, path = "/restaurant/category/{category_id}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getRestaurantsByCategoryId(@PathVariable final String category_id) throws CategoryNotFoundException {
        CategoryEntity categoryEntity = categoryService.getCategoryById(category_id);
        List<RestaurantEntity> restaurantEntities = categoryEntity.getRestaurant();
        List<RestaurantDetailsResponse> restaurantDetailsResponses = new ArrayList<RestaurantDetailsResponse>();
        List<RestaurantList> restaurantLists = new ArrayList<>();
        RestaurantListResponse restaurantListResponse = new RestaurantListResponse();
        for(int i=0; i < restaurantEntities.size(); i++) {
            List<CategoryEntity> categoryEntities = restaurantEntities.get(i).getCategories();
            String categoryValues = "";
            for (int j=0; j < categoryEntities.size(); j++) {
                categoryValues = categoryValues + categoryEntities.get(j).getCategory_name() + ",";
                categoryValues = categoryValues.replace(",$", "");
            }
            String arr[] = categoryValues.split(",");
            Arrays.sort(arr);
            Arrays.toString(arr);
            String joinedSortedCategoriesValues = String.join(",",arr);
            restaurantLists.add(
                    new RestaurantList()
                            .id(UUID.fromString(restaurantEntities.get(i).getUuid()))
                            .restaurantName(restaurantEntities.get(i).getRestaurant_name())
                            .photoURL(restaurantEntities.get(i).getPhoto_url())
                            .customerRating(restaurantEntities.get(i).getCustomer_rating())
                            .averagePrice(restaurantEntities.get(i).getAverage_price_for_two())
                            .numberCustomersRated(restaurantEntities.get(i).getNumber_of_customers_rated())
                            .id(UUID.fromString(restaurantEntities.get(i).getUuid()))
                            .restaurantName(restaurantEntities.get(i).getRestaurant_name())
                            .address(new RestaurantDetailsResponseAddress()
                                    .id(UUID.fromString(restaurantEntities.get(i).getAddress().getUuid()))
                                    .flatBuildingName(restaurantEntities.get(i).getAddress().getFlatBuilNo())
                                    .locality(restaurantEntities.get(i).getAddress().getLocality())
                                    .city(restaurantEntities.get(i).getAddress().getCity())
                                    .pincode(restaurantEntities.get(i).getAddress().getPincode())
                                    .state(new RestaurantDetailsResponseAddressState().id(UUID.fromString(restaurantEntities.get(i).getAddress().getState().getUuid()))
                                            .stateName(restaurantEntities.get(i).getAddress().getState().getState_name())
                                    )
                            )
                            .categories(joinedSortedCategoriesValues)
            );
        }
        restaurantListResponse.setRestaurants(restaurantLists);
        return new ResponseEntity<RestaurantListResponse>(restaurantListResponse, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, path = "/restaurant/{restaurant_id}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity restaurantByUUID(@PathVariable final String restaurant_id) throws RestaurantNotFoundException {
        RestaurantEntity restaurantEntity = restaurantService.restaurantByUUID(restaurant_id);

        List<CategoryEntity> categoryEntities = restaurantEntity.getCategories();
        Collections.sort(categoryEntities, CategoryEntity.CatNameComparator);
        List<CategoryList> categoriesList = new ArrayList<CategoryList>();
        for (int j=0; j < categoryEntities.size(); j++) {
            CategoryList catList = new CategoryList();
            catList.setCategoryName(categoryEntities.get(j).getCategory_name());
            catList.setId(UUID.fromString(categoryEntities.get(j).getUuid()));

            List<ItemEntity> itemEntities = categoryEntities.get(j).getItem();
            List<ItemList> itemLists = new ArrayList<ItemList>();
            for (int k = 0; k < itemEntities.size(); k++) {
                ItemList itemList = new ItemList();
                itemList.setId(UUID.fromString(itemEntities.get(k).getUuid()));
                itemList.setItemName(itemEntities.get(k).getItem_name());
                ItemList.ItemTypeEnum itemType = ItemList.ItemTypeEnum.valueOf(itemEntities.get(k).getType().toString());

                itemList.setItemType(itemType);
                itemList.setPrice(itemEntities.get(k).getPrice());
                itemLists.add(itemList);
            }
            catList.setItemList(itemLists);
            categoriesList.add(catList);
        }
        RestaurantDetailsResponse restResponse = new RestaurantDetailsResponse()
                .id(UUID.fromString(restaurantEntity.getUuid()))
                .restaurantName(restaurantEntity.getRestaurant_name())
                .photoURL(restaurantEntity.getPhoto_url())
                .customerRating(restaurantEntity.getCustomer_rating())
                .averagePrice(restaurantEntity.getAverage_price_for_two())
                .numberCustomersRated(restaurantEntity.getNumber_of_customers_rated())
                .address(new RestaurantDetailsResponseAddress()
                        .id(UUID.fromString(restaurantEntity.getAddress().getUuid()))
                        .flatBuildingName(restaurantEntity.getAddress().getFlatBuilNo())
                        .locality(restaurantEntity.getAddress().getLocality())
                        .city(restaurantEntity.getAddress().getCity())
                        .pincode(restaurantEntity.getAddress().getPincode())
                        .state(new RestaurantDetailsResponseAddressState().id(UUID.fromString(restaurantEntity.getAddress().getState().getUuid()))
                                .stateName(restaurantEntity.getAddress().getState().getState_name())
                        )
                )
                .categories(categoriesList);
        return new ResponseEntity<RestaurantDetailsResponse>(restResponse, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.PUT, path = "/restaurant/{restaurant_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantUpdatedResponse> updateRestaurantDetails(@RequestParam BigDecimal customerRating , @PathVariable("restaurant_id") final String restaurant_id, @RequestHeader("authorization") final String authorization) throws RestaurantNotFoundException, InvalidRatingException, AuthorizationFailedException {
        RestaurantEntity restaurantEntity = new RestaurantEntity();
        restaurantEntity.setUuid(restaurant_id);
        String bearerToken = null;
        try {
            bearerToken = authorization.split("Bearer ")[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            bearerToken = authorization;
        }
        restaurantEntity.setCustomerRating(customerRating);
        RestaurantEntity updatedRestaurantEntity = restaurantService.updateRestaurantDetails(restaurantEntity,bearerToken);
        RestaurantUpdatedResponse restUpdateResponse = new RestaurantUpdatedResponse()
                .id(UUID.fromString(updatedRestaurantEntity.getUuid()))
                .status("RESTAURANT RATING UPDATED SUCCESSFULLY");
        return new ResponseEntity<RestaurantUpdatedResponse>(restUpdateResponse, HttpStatus.OK);
    }
}
