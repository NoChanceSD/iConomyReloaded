package com.iConomy.system;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;

import com.iConomy.iConomy;
import com.iConomy.events.AccountResetEvent;
import com.iConomy.events.AccountSetEvent;
import com.iConomy.events.AccountUpdateEvent;
import com.iConomy.util.Constants;
import com.iConomy.util.Misc;

/**
 * Controls player Holdings, and Bank Account holdings.
 *
 * @author Nijikokun
 */
public class Holdings {
	private final String name;
	private final boolean bank;
	private final int bankId;

	public Holdings(final int id, final String name, final boolean bank) {
		this.bank = bank;
		this.bankId = id;
		this.name = name;
	}

	public final boolean isBank() {
		return bank;
	}

	public final double balance() {
		return get();
	}

	private double get() {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		Double balance = Constants.Holdings;

		try {
			conn = iConomy.getiCoDatabase().getConnection();

			if (this.bankId == 0) {
				ps = conn.prepareStatement("SELECT * FROM " + Constants.SQLTable + " WHERE username = ? LIMIT 1");
				ps.setString(1, this.name);
			} else {
				ps = conn.prepareStatement("SELECT * FROM " + Constants.SQLTable + "_BankRelations WHERE account_name = ? AND bank_id = ? LIMIT 1");
				ps.setString(1, this.name);
				ps.setInt(2, this.bankId);
			}

			rs = ps.executeQuery();

			if (rs.next()) {
				balance = this.bankId == 0 ? rs.getDouble("balance") : rs.getDouble("holdings");
			}
		} catch (final Exception e) {
			System.out.println("[iConomy] Failed to grab holdings: " + e);
		} finally {
			if (ps != null)
				try {
					ps.close();
				} catch (final SQLException ex) {
				}

			if (rs != null)
				try {
					rs.close();
				} catch (final SQLException ex) {
				}

			if (conn != null)
				try {
					conn.close();
				} catch (final SQLException ex) {
				}
		}

		return balance;
	}

	public final void set(final double balance) {
		final AccountSetEvent Event = new AccountSetEvent(this.name, balance);
		iConomy.getBukkitServer().getPluginManager().callEvent(Event);

		Connection conn = null;
		final ResultSet rs = null;
		PreparedStatement ps = null;

		try {
			conn = iConomy.getiCoDatabase().getConnection();

			if (bankId == 0) {
				ps = conn.prepareStatement("UPDATE " + Constants.SQLTable + " SET balance = ? WHERE username = ?");
				ps.setDouble(1, balance);
				ps.setString(2, this.name);
			} else {
				ps = conn.prepareStatement("UPDATE " + Constants.SQLTable + "_BankRelations SET holdings = ? WHERE account_name = ? AND bank_id = ?");
				ps.setDouble(1, balance);
				ps.setString(2, this.name);
				ps.setInt(3, this.bankId);
			}

			ps.executeUpdate();
		} catch (final Exception e) {
			System.out.println("[iConomy] Failed to set holdings: " + e);
		} finally {
			if (ps != null)
				try {
					ps.close();
				} catch (final SQLException ex) {
				}

			if (conn != null)
				try {
					conn.close();
				} catch (final SQLException ex) {
				}
		}
	}

	public final void add(final double amount) {
		final double balance = this.get();
		final double ending = balance + amount;

		this.math(amount, balance, ending);
	}

	public final void subtract(final double amount) {
		final double balance = this.get();
		final double ending = balance - amount;

		this.math(amount, balance, ending);
	}

	public final void reset() {
		final AccountResetEvent Event = new AccountResetEvent(this.name);
		iConomy.getBukkitServer().getPluginManager().callEvent(Event);

		if (!Event.isCancelled())
			this.set(Constants.Holdings);
	}

	private void math(final double amount, final double balance, final double ending) {
		final AccountUpdateEvent Event = new AccountUpdateEvent(this.name, balance, ending, amount);
		iConomy.getBukkitServer().getPluginManager().callEvent(Event);

		if (!Event.isCancelled())
			this.set(ending);
	}

	public final boolean isNegative() {
		return this.get() < 0.0;
	}

	public final boolean hasEnough(final double amount) {
		return amount <= this.get();
	}

	@Override
	public final String toString() {
		final DecimalFormat formatter = new DecimalFormat("#,##0.00");
		final Double balance = this.get();
		String formatted = formatter.format(balance);

		if (formatted.endsWith(".")) {
			formatted = formatted.substring(0, formatted.length() - 1);
		}

		if (bankId == 0) {
			return Misc.formatted(formatted, Constants.Major, Constants.Minor);
		}

		final Bank b = new Bank(this.bankId);
		return Misc.formatted(formatted, b.getMajor(), b.getMinor());
	}
}
