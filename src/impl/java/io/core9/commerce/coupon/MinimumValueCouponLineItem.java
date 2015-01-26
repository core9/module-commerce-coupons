package io.core9.commerce.coupon;

import io.core9.commerce.cart.Cart;
import io.core9.plugin.server.request.Request;
import io.core9.plugin.server.request.RequestUtils;

public class MinimumValueCouponLineItem extends CouponLineItem {
	
	private int minimumvalue;
	private int initialPrice;
	private String description;

	private static final long serialVersionUID = -3667985358539557887L;

	public MinimumValueCouponLineItem(String id, int price, String description, String image, String link, int minimumval) {
		super(id, price, description, image, link);
		this.minimumvalue = minimumval;
		this.description = description;
		this.initialPrice = price;
	}
	
	@Override
	public boolean validates(Request req, Cart cart) {
		int total = cart.getTotal();
		if(this.getPrice() == 0) {
			total += initialPrice;
		}
		if(total >= minimumvalue) {
			this.setPrice(initialPrice);
			this.setDescription(description);
		} else {
			this.setPrice(0);
			this.setDescription("Coupon: " + RequestUtils.getLocalizedMessage(req, "Your order value is too low, minimum value is %,.2f", ((double) minimumvalue) / 100));
			RequestUtils.addMessage(req, "Your order value is too low, minimum value is %,.2f", ((double) minimumvalue) / 100);
		}
		return super.validates(req, cart);
	}

	public int getMinimumvalue() {
		return minimumvalue;
	}

	public void setMinimumvalue(int minimumvalue) {
		this.minimumvalue = minimumvalue;
	}

}
