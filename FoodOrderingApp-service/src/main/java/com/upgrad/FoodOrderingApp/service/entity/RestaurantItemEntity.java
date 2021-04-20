package com.upgrad.FoodOrderingApp.service.entity;
/*
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


@SuppressWarnings("ALL")
@Entity
@Table(name = "restaurant_item")
@NamedQueries({
        @NamedQuery(name = "getItemsByRestaurant", query = "SELECT r FROM RestaurantItemEntity r WHERE r.restaurantId = :restaurant ORDER BY LOWER(r.itemId.itemName) ASC "),
})
public class RestaurantItemEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @NotNull
    private ItemEntity itemId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "restaurant_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @NotNull
    private RestaurantEntity restaurantId;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public ItemEntity getItemId() {
        return itemId;
    }

    public void setItemsId(ItemEntity itemId) {
        this.itemId = itemId;
    }


    public RestaurantEntity getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(RestaurantEntity restaurantId) {
        this.restaurantId = restaurantId;
    }

} */