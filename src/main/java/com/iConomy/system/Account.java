package com.iConomy.system;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.iConomy.iConomy;
import com.iConomy.util.Constants;

public class Account {
	private final String name;

	public Account(final String name) {
		this.name = name;
	}

	public final int getId() {
		int id = -1;

		try (Connection conn = iConomy.getiCoDatabase().getConnection();
				PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + Constants.SQLTable + " WHERE username = ? LIMIT 1");
				ResultSet rs = ps.executeQuery()) {

			ps.setString(1, name);

			if (rs.next()) {
				id = rs.getInt("id");
			}
		} catch (final Exception e) {
			id = -1;
		}

		return id;
	}

	public final String getName() {
		return name;
	}

	public final Holdings getHoldings() {
		return new Holdings(0, this.name, false);
	}

	public final void setMainBank(final String name) {
		final Bank bank = iConomy.Banks.get(name);
		final int id = iConomy.Banks.get(name).getId();

		if (bank.hasAccount(this.name)) {
			setMainBank(id);
		}
	}

	public final void setMainBank(final int id) {
		if (!Constants.Banking)
			return;

		if (!iConomy.Banks.get(id).hasAccount(this.name))
			return;

		Connection conn = null;
		final ResultSet rs = null;
		PreparedStatement ps = null;
		final Bank bank = null;

		try {
			conn = iConomy.getiCoDatabase().getConnection();

			ps = conn.prepareStatement("UPDATE " + Constants.SQLTable + "_BankRelations SET main = 0 WHERE account_name = ? AND main = 1");
			ps.setString(1, this.name);
			ps.executeUpdate();
			ps.clearParameters();

			ps = conn.prepareStatement("UPDATE " + Constants.SQLTable + "_BankRelations SET main = 1 WHERE account_name = ? AND bank_id = ?");
			ps.setString(1, this.name);
			ps.setInt(2, id);
			ps.executeUpdate();
		} catch (final Exception e) {
			System.out.println(e.getMessage());
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

	public final Bank getMainBank() {
		if (!Constants.Banking)
			return null;

		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		Bank bank = null;

		try {
			conn = iConomy.getiCoDatabase().getConnection();

			ps = conn.prepareStatement("SELECT * FROM " + Constants.SQLTable + "_BankRelations WHERE account_name = ? AND main = 1 LIMIT 1");
			ps.setString(1, this.name);
			rs = ps.executeQuery();

			if (rs.next()) {
				bank = new Bank(rs.getInt("bank_id"));
			}
		} catch (final Exception e) {
			System.out.println(e.getMessage());
			return null;
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

		return bank;
	}

	public final BankAccount getMainBankAccount() {
		if (!Constants.Banking)
			return null;

		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		BankAccount account = null;

		try {
			conn = iConomy.getiCoDatabase().getConnection();

			ps = conn.prepareStatement("SELECT * FROM " + Constants.SQLTable + "_BankRelations WHERE account_name = ? AND main = 1 LIMIT 1");
			ps.setString(1, this.name);
			rs = ps.executeQuery();

			if (rs.next()) {
				final Bank bank = new Bank(rs.getInt("bank_id"));
				account = new BankAccount(bank.getName(), rs.getInt("bank_id"), this.name);
			}
		} catch (final Exception e) {
			return null;
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

		return account;
	}

	public final ArrayList<BankAccount> getBankAccounts() {
		if (!Constants.Banking)
			return null;

		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		final ArrayList<BankAccount> banks = new ArrayList<>();

		try {
			conn = iConomy.getiCoDatabase().getConnection();

			ps = conn.prepareStatement("SELECT * FROM " + Constants.SQLTable + "_BankRelations WHERE account_name = ?");
			ps.setString(1, this.name);
			rs = ps.executeQuery();

			while (rs.next()) {
				final Bank bank = new Bank(rs.getInt("bank_id"));
				banks.add(new BankAccount(bank.getName(), rs.getInt("bank_id"), this.name));
			}
		} catch (final Exception e) {
			return null;
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

		return banks;
	}

	public final Holdings getBankHoldings(final int id) {
		if (!Constants.Banking)
			return null;

		return new Holdings(id, this.name, id != 0);
	}

	public final boolean isHidden() {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;

		try {
			conn = iConomy.getiCoDatabase().getConnection();
			ps = conn.prepareStatement("SELECT hidden FROM " + Constants.SQLTable + " WHERE username = ? LIMIT 1");
			ps.setString(1, this.name);
			rs = ps.executeQuery();

			if (rs != null) {
				if (rs.next()) {
					return rs.getBoolean("hidden");
				}
			}
		} catch (final Exception e) {
			System.out.println("[iConomy] Failed to check status: " + e);
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
				iConomy.getiCoDatabase().close(conn);
		}

		return false;
	}

	public final boolean setHidden(final boolean hidden) {
		Connection conn = null;
		final ResultSet rs = null;
		PreparedStatement ps = null;

		try {
			conn = iConomy.getiCoDatabase().getConnection();

			ps = conn.prepareStatement("UPDATE " + Constants.SQLTable + " SET hidden = ? WHERE username = ?");
			ps.setBoolean(1, hidden);
			ps.setString(2, this.name);

			ps.executeUpdate();
		} catch (final Exception e) {
			System.out.println("[iConomy] Failed to update status: " + e);
		} finally {
			if (ps != null)
				try {
					ps.close();
				} catch (final SQLException ex) {
				}

			if (conn != null)
				iConomy.getiCoDatabase().close(conn);
		}

		return true;
	}

	/**
	 * Returns the ranking number of an account
	 *
	 * @param name
	 * @return Integer
	 */
	public final int getRank() {
		int i = 1;

		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;

		try {
			conn = iConomy.getiCoDatabase().getConnection();
			ps = conn.prepareStatement("SELECT * FROM " + Constants.SQLTable + " WHERE hidden = 0 ORDER BY balance DESC");
			rs = ps.executeQuery();

			while (rs.next()) {
				if (rs.getString("username").equalsIgnoreCase(this.name)) {
					return i;
				} else {
					i++;
				}
			}
		} catch (final Exception e) {
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

			iConomy.getiCoDatabase().close(conn);
		}

		return -1;
	}

}
