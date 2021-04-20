package com.upgrad.FoodOrderingApp.api.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.upgrad.FoodOrderingApp.service.businness.CustomerService;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

	@Autowired
	CustomerService customerService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		System.out.println("--- Auth pre method---" + request.getHeader("authorization"));
		String accessToken = request.getHeader("authorization");
		String bearerToken = null;
		try {
			bearerToken = accessToken.split("Bearer ")[1];
		} catch (ArrayIndexOutOfBoundsException e) {
			bearerToken = accessToken;
		}
		customerService.checkCustomerEntityValidity(bearerToken);
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// System.out.println("---method executed---");
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// System.out.println("---Request Completed---");
	}
}
