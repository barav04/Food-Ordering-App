package com.upgrad.FoodOrderingApp.service.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;

@Repository
public class PaymentDAO {
	@PersistenceContext
	private EntityManager entityManager;
	
	public List<PaymentEntity> getAllPaymentMethods() {
		return entityManager.createNamedQuery("getAllPaymentMethods").getResultList();
	}

	public PaymentEntity getPaymentById(String uuid) {
		return entityManager.createNamedQuery("getPaymentById", PaymentEntity.class).setParameter("paymentUuid",uuid).getSingleResult();
	}
}
