package io.core9.commerce.coupon;

import io.core9.commerce.cart.Cart;
import io.core9.commerce.cart.CartException;
import io.core9.commerce.cart.lineitem.LineItem;
import io.core9.commerce.cart.lineitem.SingularLineItem;
import io.core9.plugin.server.request.Request;

public class CouponLineItem extends SingularLineItem {
	
	private static final long serialVersionUID = 7271873568037827041L;
	
	public CouponLineItem(LineItem item) throws CartException {
		super(item);
	}
	
	public CouponLineItem() {
		
	}
	
	@Override
	public boolean validates(Request req, Cart cart) {
		return true;
	}

}
