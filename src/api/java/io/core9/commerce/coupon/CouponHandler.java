package io.core9.commerce.coupon;

import io.core9.commerce.cart.Cart;
import io.core9.plugin.server.request.Request;

public interface CouponHandler {

	Coupon handle(Request req, Coupon coupon, Cart cart);
	
	boolean couponsAreAvailable(Request req, Cart cart);

}

