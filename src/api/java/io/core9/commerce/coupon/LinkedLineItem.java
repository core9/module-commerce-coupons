package io.core9.commerce.coupon;

import io.core9.commerce.cart.CartException;
import io.core9.commerce.cart.lineitem.LineItem;
import io.core9.commerce.cart.lineitem.StandardLineItem;

import java.util.List;

public class LinkedLineItem extends StandardLineItem implements LineItem {

	private static final long serialVersionUID = -4391511070861579303L;
	
	private List<String> linkedLineItems;

	public List<String> getLinkedLineItems() {
		return linkedLineItems;
	}

	public void setLinkedLineItems(List<String> linkedLineItems) {
		this.linkedLineItems = linkedLineItems;
	}
	
	public LinkedLineItem(LineItem item, List<String> linkedLineItems) throws CartException {
		super(item);
		this.linkedLineItems = linkedLineItems;
	}

}
