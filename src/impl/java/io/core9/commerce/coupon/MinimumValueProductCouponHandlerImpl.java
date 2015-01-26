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

import org.apache.log4j.Logger;

/**
 * TODO: Should probably use some form of composition 
 * @author mark
 *
 */
@PluginImplementation
public class MinimumValueProductCouponHandlerImpl implements MinimumValueProductCouponHandler {
	
	private static final String HANDLER_IDENTIFIER = "minvalproduct";
	private static final Logger LOG = Logger.getLogger(MinimumValueProductCouponHandlerImpl.class);
	
	@InjectPlugin
	private CouponDataHandler<?> coupons;

	@Override
	public Coupon handle(Request req, Coupon coupon, Cart cart) {
		Map<String,Object> options = coupon.getHandlerOptions();
		final int minimum = (int) coupon.getHandlerOptions().get("minimum");
		
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
				LineItem couponLineItem = null;
				if(coupon.getPercentage() > 0) {
					couponLineItem = new MinimumValueCouponLineItem(coupon.getId(),  -1 * item.getPrice() * (coupon.getPercentage()/100), "Coupon: " + item.getDescription(), null, null, minimum); 
				} else if(coupon.getAmount() > 0) {
					couponLineItem = new MinimumValueCouponLineItem(coupon.getId(), -1 * coupon.getAmount(), "Coupon: " + item.getDescription(), null, null, minimum);
				}
				if(couponLineItem != null) {
					if(cart.getTotal() + couponLineItem.getPrice() <= minimum) {
						couponLineItem.setPrice(0);
					}
					cart.getItems().put(coupon.getId(), couponLineItem);
					coupon.decrement();
				} else {
					LOG.error("Coupon " + coupon.getId() + " doesn't have any amount or percentage set.");
				}
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
