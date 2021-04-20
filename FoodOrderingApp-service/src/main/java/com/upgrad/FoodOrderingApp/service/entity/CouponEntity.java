package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@NamedQueries(
		{
			@NamedQuery(name = "getCouponByCouponName", query = "select c from CouponEntity c where c.couponName = :coupon_name"),
			@NamedQuery(name = "getCouponById", query = "select c from CouponEntity c where c.uuid=:couponUuid")
		})

@Entity
@Table(name = "coupon")
public class CouponEntity {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "uuid")
	@NotNull
	private String uuid;

	@Column(name = "coupon_name")
	private String couponName;

	@Column(name = "percent")
	private int percent;

	public CouponEntity() {

	}

	public CouponEntity(String couponId, String myCoupon, int percent) {
		this.uuid = couponId;
		this.couponName = myCoupon;
		this.percent = percent;
	}


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}


	public String getCouponName() {
		return couponName;
	}

	public void setCouponName(String couponName) {
		this.couponName = couponName;
	}

	public int getPercent() {
		return percent;
	}

	public void setPercent(int percent) {
		this.percent = percent;
	}

}
