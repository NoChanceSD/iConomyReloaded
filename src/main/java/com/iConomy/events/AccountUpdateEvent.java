package com.iConomy.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AccountUpdateEvent extends Event {
	private final String account;
	private double balance;
	private final double previous;
	private double amount;
	private boolean cancelled = false;

	public AccountUpdateEvent(final String account, final double previous, final double balance, final double amount) {
		super();
		this.account = account;
		this.previous = previous;
		this.balance = balance;
		this.amount = amount;
	}

	public String getAccountName() {
		return account;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(final double amount) {
		this.amount = amount;
		this.balance = previous+amount;
	}

	public double getPrevious() {
		return previous;
	}

	public double getBalance() {
		return balance;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(final boolean cancelled) {
		this.cancelled = cancelled;
	}

	@Override
	public HandlerList getHandlers() {
		// TODO Auto-generated method stub
		return null;
	}
}
