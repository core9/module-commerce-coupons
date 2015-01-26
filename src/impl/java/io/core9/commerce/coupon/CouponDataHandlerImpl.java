package io.core9.commerce.coupon;

import io.core9.commerce.CommerceDataHandlerConfig;
import io.core9.commerce.CommerceDataHandlerHelper;
import io.core9.commerce.cart.Cart;
import io.core9.commerce.cart.lineitem.LineItem;
import io.core9.plugin.database.repository.CrudRepository;
import io.core9.plugin.database.repository.NoCollectionNamePresentException;
import io.core9.plugin.database.repository.RepositoryFactory;
import io.core9.plugin.server.request.Request;
import io.core9.plugin.server.request.RequestUtils;
import io.core9.plugin.widgets.datahandler.ContextualDataHandler;
import io.core9.plugin.widgets.datahandler.DataHandler;
import io.core9.plugin.widgets.datahandler.DataHandlerDefaultConfig;
import io.core9.plugin.widgets.datahandler.DataHandlerFactoryConfig;

import java.util.HashMap;
import java.util.Map;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.events.PluginLoaded;
import net.xeoh.plugins.base.annotations.injections.InjectPlugin;

@PluginImplementation
public class CouponDataHandlerImpl<T extends DataHandlerDefaultConfig> implements CouponDataHandler<T> {
	
	private final Map<String, CouponHandler> COUPON_HANDLERS = new HashMap<String, CouponHandler>();
	
	@InjectPlugin
	private CommerceDataHandlerHelper helper;
	
	private CrudRepository<Coupon> coupons;
	
	@PluginLoaded
	public void onRepositoryFactory(RepositoryFactory factory) throws NoCollectionNamePresentException {
		coupons = factory.getRepository(Coupon.class);
	}
	
	@Override
	public String getName() {
		return "Commerce-Coupon";
	}

	@Override
	public Class<? extends DataHandlerFactoryConfig> getConfigClass() {
		return CommerceDataHandlerConfig.class;
	}

	@Override
	public DataHandler<T> createDataHandler(DataHandlerFactoryConfig options) {
		return new ContextualDataHandler<T>() {
			
			@Override
			public Map<String, Object> handle(Request req, Map<String,Object> context) {
				Map<String,Object> result = new HashMap<String, Object>();
				Cart cart = helper.getCart(req);
				handleCouponCall(req, cart, context);
				result.put("available", couponsAreApplicable(req, cart));
				result.put("activated", cartContainsCoupon(cart));
				return result;
			}

			@SuppressWarnings("unchecked")
			@Override
			public T getOptions() {
				return (T) options;
			}
		};
	}
	
	protected boolean couponsAreApplicable(Request req, Cart cart) {
		if(cartContainsCoupon(cart)) {
			return false;
		}
		for(CouponHandler handler : COUPON_HANDLERS.values()) {
			if(handler.couponsAreAvailable(req, cart)) {
				return true;
			}
		}
		return false;
	}

	protected void handleCouponCall(Request req, Cart cart, Map<String, Object> context) {
		if(context == null || (context.get("handled") != null && (Boolean) context.get("handled"))) {
			return;
		}
		String code = (String) context.get("code");
		Coupon coupon;
		if(context.get("code") == null || (coupon = coupons.read(req.getVirtualHost(), code)) == null) {
			RequestUtils.addMessage(req, "This is an unknown coupon value");
		} else if(cartContainsCoupon(cart)) {
			RequestUtils.addMessage(req, "Your cart already contains a coupon, only one coupon per order allowed.");
		} else {
			applyCouponToCart(req, cart, coupon);
		}
		context.put("handled", true);
	}

	public static boolean cartContainsCoupon(Cart cart) {
		for(LineItem item : cart.getItems().values()) {
			if(item instanceof CouponLineItem) {
				return true;
			}
		}
		return false;
	}

	public void applyCouponToCart(Request req, Cart cart, Coupon coupon) {
		CouponHandler handler = COUPON_HANDLERS.get(coupon.getHandler());
		if(handler == null) {
			throw new UnsupportedOperationException("The Coupon handler " + coupon.getHandler() + " is not available.");
		}
		coupon = handler.handle(req, coupon, cart);
		coupons.update(req.getVirtualHost(), coupon);
		helper.saveCart(req, cart);
	}
	
	@Override
	public void addCouponHandler(String couponType, CouponHandler handler) {
		COUPON_HANDLERS.put(couponType, handler);
	}

	@Override
	public CrudRepository<Coupon> getCouponRepository() {
		return coupons;
	}

}
