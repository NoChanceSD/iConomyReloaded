package com.iConomy.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AccountResetEvent extends Event {
	private final String account;
	private boolean cancelled = false;

	public AccountResetEvent(final String account) {
		super();
		this.account = account;
	}

	public String getAccountName() {
		return account;
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

