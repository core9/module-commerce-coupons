package io.core9.commerce.coupon;

import io.core9.core.plugin.Core9Plugin;
import io.core9.plugin.database.repository.CrudRepository;
import io.core9.plugin.widgets.datahandler.DataHandlerDefaultConfig;
import io.core9.plugin.widgets.datahandler.DataHandlerFactory;

public interface CouponDataHandler<T extends DataHandlerDefaultConfig> extends DataHandlerFactory<T>, Core9Plugin {
	
	void addCouponHandler(String couponType, CouponHandler handler);
	
	CrudRepository<Coupon> getCouponRepository();

}
