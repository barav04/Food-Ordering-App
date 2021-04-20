package com.upgrad.FoodOrderingApp.service.businness;

import java.util.LinkedList;
import java.util.List;

import com.upgrad.FoodOrderingApp.service.dao.*;
import com.upgrad.FoodOrderingApp.service.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;

@Service
public class ItemService {

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private RestaurantDao restaurantDao;

   // @Autowired
  //  private RestaurantItemDao restaurantItemDao;

    @Autowired
    private CategoryDao categoryDao;

  //  @Autowired
  //  private CategoryItemDao categoryItemDao;



    public ItemEntity getItemById(String uuid) {
        return itemDao.getItemEntityById(uuid);
    }

    public List<OrderEntity> getOrdersByRestaurant(RestaurantEntity restaurantEntity) {
        return itemDao.getOrdersByRestaurant(restaurantEntity);
    }

    public List<ItemEntity> getMostPopularItems(String resId) throws RestaurantNotFoundException {
    	RestaurantEntity restaurantEntity = restaurantDao.restaurantByUUID(resId);
    	if(restaurantEntity==null)
    			throw new RestaurantNotFoundException("RNF-001","No restaurant by this id");
    	return itemDao.getPopularOrders(restaurantEntity);
    }

    public List<ItemEntity> getItemsByPopularity(RestaurantEntity restaurantEntity1) throws RestaurantNotFoundException {
        RestaurantEntity restaurantEntity = restaurantEntity1;
        if(restaurantEntity==null)
            throw new RestaurantNotFoundException("RNF-001","No restaurant by this id");
        return itemDao.getPopularOrders(restaurantEntity);
    }

    /*
    public List<ItemEntity> getItemsByCategoryAndRestaurant(String restaurantUuid, String categoryUuid) {

        //Gets restaurant entity by restaurantUuid
        RestaurantEntity restaurantEntity = restaurantDao.restaurantByUUID(restaurantUuid);

        //Gets CategoryEntity  by CategoryUUID
        CategoryEntity categoryEntity = categoryDao.getCategoryById(categoryUuid);

        //Gets RestaurantItemEntity for given restaurantEntity
        List<RestaurantItemEntity> restaurantItemEntities = restaurantItemDao.getItemsByRestaurant(restaurantEntity);

        //Gets categoryItemEntities for given categoryEntity
        List<CategoryItemEntity> categoryItemEntities = categoryItemDao.getItemsByCategory(categoryEntity);

        List<ItemEntity> itemEntities = new LinkedList<>();
        restaurantItemEntities.forEach(restaurantItemEntity -> {
            categoryItemEntities.forEach(categoryItemEntity -> {
                if (restaurantItemEntity.getItemId().equals(categoryItemEntity.getItemId())) {
                    itemEntities.add(restaurantItemEntity.getItemId());
                }
            });
        });
        return itemEntities;
    }

     */
}
