package com.iConomy.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AccountRemoveEvent extends Event {
	private final String account;
	private boolean cancelled = false;

	public AccountRemoveEvent(final String account) {
		super();
		this.account = account;
	}

	public final String getAccountName() {
		return account;
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
