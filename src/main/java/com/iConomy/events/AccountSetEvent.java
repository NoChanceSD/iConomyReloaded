package com.iConomy.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AccountSetEvent extends Event {
	private final String account;
	private final double balance;

	public AccountSetEvent(final String account, final double balance) {
		super();
		this.account = account;
		this.balance = balance;
	}

	public final String getAccountName() {
		return account;
	}

	public final double getBalance() {
		return balance;
	}

	@Override
	public final HandlerList getHandlers() {
		// TODO Auto-generated method stub
		return null;
	}
}
