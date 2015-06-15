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

	public final String getAccountName() {
		return account;
	}

	public final double getAmount() {
		return amount;
	}

	public final void setAmount(final double amount) {
		this.amount = amount;
		this.balance = previous + amount;
	}

	public final double getPrevious() {
		return previous;
	}

	public final double getBalance() {
		return balance;
	}

	public final boolean isCancelled() {
		return cancelled;
	}

	public final void setCancelled(final boolean cancelled) {
		this.cancelled = cancelled;
	}

	@Override
	public final HandlerList getHandlers() {
		// TODO Auto-generated method stub
		return null;
	}
}
