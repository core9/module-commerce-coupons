package io.core9.commerce.coupon;

import io.core9.plugin.database.repository.AbstractCrudEntity;
import io.core9.plugin.database.repository.Collection;
import io.core9.plugin.database.repository.CrudEntity;

import java.util.Map;

@Collection("core.coupons")
public class Coupon extends AbstractCrudEntity implements CrudEntity {

	private byte percentage;
	private int amount;
	private int redemptions;
	private boolean active;
	private String handler;
	private Map<String,Object> handlerOptions;

	public byte getPercentage() {
		return percentage;
	}

	public void setPercentage(byte percentage) {
		this.percentage = percentage;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getRedemptions() {
		return redemptions;
	}

	public void setRedemptions(int redemptions) {
		this.redemptions = redemptions;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getHandler() {
		return handler;
	}

	public void setHandler(String handler) {
		this.handler = handler;
	}

	public Map<String,Object> getHandlerOptions() {
		return handlerOptions;
	}

	public void setHandlerOptions(Map<String,Object> handlerOptions) {
		this.handlerOptions = handlerOptions;
	}

	public void decrement() {
		this.redemptions--;
		if(this.redemptions < 1) {
			this.active = false;
		}
	}
	
	public void increment() {
		this.redemptions++;
		if(this.redemptions > 0) {
			this.active = true;
		}
	}

}
