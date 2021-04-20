package com.upgrad.FoodOrderingApp.service.entity;
/*
import javax.persistence.*;
import javax.validation.constraints.NotNull;


@SuppressWarnings("ALL")
@Entity
@Table(name = "category_item")
@NamedQueries({

        @NamedQuery(name = "getItemsByCategory", query = "SELECT c FROM CategoryItemEntity c WHERE c.categoryId = :category ORDER BY LOWER(c.itemId.itemName) ASC "),
})
public class CategoryItemEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "item_id")
    @NotNull
    private ItemEntity itemId;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @NotNull
    private CategoryEntity categoryId;


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


    public CategoryEntity getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(CategoryEntity categoryId) {
        this.categoryId = categoryId;
    }

}
*/
