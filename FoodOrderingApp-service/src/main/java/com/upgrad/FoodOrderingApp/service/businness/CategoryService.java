package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
//import com.upgrad.FoodOrderingApp.service.dao.CategoryItemDao;
//import com.upgrad.FoodOrderingApp.service.dao.RestaurantCategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
//import com.upgrad.FoodOrderingApp.service.entity.RestaurantCategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class CategoryService {

  //  @Autowired
 //   private RestaurantCategoryDao restaurantCategoryDao;

    //Handles all data related to the RestaurantEntity
    @Autowired
    private RestaurantDao restaurantDao;

    @Autowired
    private CategoryDao categoryDao;

//    @Autowired
  //  private CategoryItemDao categoryItemDao;

/*
    public List<CategoryEntity> getCategoriesByRestaurant(String restaurantUuid) {

        //Getting restaurant entity by using restaurant id
        RestaurantEntity restaurantEntity = restaurantDao.restaurantByUUID(restaurantUuid);

        //Getting all the category in a restaurant
        List<RestaurantCategoryEntity> restaurantCategoryEntities = restaurantCategoryDao.getCategoriesByRestaurant(restaurantEntity);

        //Creating the list of the Category entity which will be returned.
        List<CategoryEntity> categoryEntities = new LinkedList<>();
        restaurantCategoryEntities.forEach(restaurantCategoryEntity -> {
            categoryEntities.add(restaurantCategoryEntity.getCategoryId());
        });
        return categoryEntities;
    }

    /* This method returns all the category in DB with ordered by name
    public List<CategoryEntity> getAllCategoriesOrderedByName() {
        //Calls getAllCategoriesOrderedByName of categoryDao to get list of CategoryEntity
        List<CategoryEntity> categoryEntities = categoryDao.getAllCategoriesOrderedByName();
        return categoryEntities;
    }*/


    public List<CategoryEntity> getAllCategories() {
        return categoryDao.getAllCategories();
    }

    public CategoryEntity getCategoryById(String uuid) throws CategoryNotFoundException {
        CategoryEntity categoryEntity = categoryDao.getCategoryById(uuid);
        if(uuid == "") {
            throw new CategoryNotFoundException("CNF-001","Category id field should not be empty");
        } else if(categoryEntity == null) {
            throw new CategoryNotFoundException("CNF-002","No category by this id");
        } else {
            return categoryEntity;
        }
    }

    public List<CategoryEntity> getAllCategoriesOrderedByName() {
        //Calls getAllCategoriesOrderedByName of categoryDao to get list of CategoryEntity
        List<CategoryEntity> categoryEntities = categoryDao.getAllCategoriesOrderedByName();
        return categoryEntities;
    }

}
