package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class OrderDAO {

	@PersistenceContext
	private EntityManager entityManager;

	public CouponEntity getCouponDetailsByName(String name) {
		try {
			return entityManager.createNamedQuery("getCouponByCouponName", CouponEntity.class)
					.setParameter("coupon_name", name).getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}

	public CouponEntity getCouponById(String uuid) {
		try {
			return entityManager.createNamedQuery("getCouponById", CouponEntity.class).setParameter("couponUuid", uuid).getSingleResult();
		} catch(NoResultException nre) {
			return null;
		}
	}

	public OrderEntity postOrder(OrderEntity orderEntity) {
		entityManager.persist(orderEntity);
		return orderEntity;
	}

    public CouponEntity getCouponDetailsById(int id) {
        try {
            return entityManager.createNamedQuery("getCouponDetailById", CouponEntity.class)
                    .setParameter("id", id).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

	public List<OrderEntity> fetchOrdersByCustomer(CustomerEntity customerEntity) {
		try {
			return entityManager.createNamedQuery("fetchAllOrders", OrderEntity.class)
					.setParameter("custEntity", customerEntity).getResultList();
		} catch (NoResultException nre) {
			return null;
		}
	}

	public OrderItemEntity postOrderItem(OrderItemEntity orderItemEntity) {
		entityManager.persist(orderItemEntity);
		return orderItemEntity;
	}

	public OrderItemEntity fetchItemDetails(OrderEntity orderEntity){
        try {
            return entityManager.createNamedQuery("fetchItemDetails", OrderItemEntity.class)
                    .setParameter("orderEntity", orderEntity).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<OrderEntity> fetchOrderByAddress(AddressEntity addressEntity){
        try {
            return entityManager.createNamedQuery("fetchOrderByAddress", OrderEntity.class)
                    .setParameter("addrEntity", addressEntity).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }


}
