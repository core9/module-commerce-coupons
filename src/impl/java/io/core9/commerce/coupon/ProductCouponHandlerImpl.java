package io.core9.commerce.coupon;

import io.core9.commerce.cart.Cart;
import io.core9.commerce.cart.lineitem.LineItem;
import io.core9.plugin.server.request.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.injections.InjectPlugin;

/**
 * Accepts Coupon.handlerOptions.sku as List<String>;
 * TODO: Should use some kind of composition
 * 
 * @author mark
 *
 */
@PluginImplementation
public class ProductCouponHandlerImpl implements ProductCouponHandler {
	
	private static final String HANDLER_IDENTIFIER = "product";
	
	@InjectPlugin
	private CouponDataHandler<?> coupons;

	@Override
	public Coupon handle(Request req, final Coupon coupon, final Cart cart) {
		Map<String,Object> options = coupon.getHandlerOptions();
		@SuppressWarnings("unchecked")
		List<String> skus = (List<String>) options.get("skus");
		for(String sku : skus) {
			LineItem item;
			if((item = cart.getItems().get(sku)) != null) {
				LinkedLineItem linked;
				if(item instanceof LinkedLineItem) {
					linked = (LinkedLineItem) item;
				} else {
					linked = new LinkedLineItem(item, new ArrayList<String>());
					cart.getItems().put(linked.getId(), linked);
				}
				linked.getLinkedLineItems().add(coupon.getId());
				if(coupon.getPercentage() > 0) {
					cart.getItems().put(coupon.getId(), new CouponLineItem(coupon.getId(),  -1 * item.getPrice() * (coupon.getPercentage()/100), "Coupon: " + item.getDescription(), null, null));
				} else if(coupon.getAmount() > 0) {
					cart.getItems().put(coupon.getId(), new CouponLineItem(coupon.getId(), -1 * coupon.getAmount(), "Coupon: " + item.getDescription(), null, null));
				}
				coupon.decrement();
			}
		}
		return coupon;
	}

	@Override
	public void execute() {
		coupons.addCouponHandler(HANDLER_IDENTIFIER, this);
	}

	@Override
	public boolean couponsAreAvailable(Request req, Cart cart) {
		Map<String,Object> query = new HashMap<String, Object>(3);
		Map<String,Object> array = new HashMap<String, Object>(1);
		array.put("$in", cart.getItems().keySet());
		query.put("handlerOptions.skus", array);
		query.put("handler", HANDLER_IDENTIFIER);
		query.put("active", true);
		return coupons.getCouponRepository().query(req.getVirtualHost(), query).size() > 0;
	}

}
