package com.iConomy.system;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.iConomy.iConomy;
import com.iConomy.util.Constants;

/**
 * Manage Account.
 *
 * @author Nijikokun
 */
public class Accounts {

	public Accounts() {
	}

	public final boolean exists(final String name) {
		boolean exists;

		try (Connection conn = iConomy.getiCoDatabase().getConnection();
				PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + Constants.SQLTable + " WHERE username = ? LIMIT 1");
				ResultSet rs = ps.executeQuery()) {
			ps.setString(1, name);
			exists = rs.next();
		} catch (final Exception e) {
			exists = false;
		}

		return exists;
	}

	public final boolean create(final String name) {
		Connection conn = null;
		final ResultSet rs = null;
		PreparedStatement ps = null;

		try {
			conn = iConomy.getiCoDatabase().getConnection();
			ps = conn.prepareStatement("INSERT INTO " + Constants.SQLTable + "(username, balance, hidden) VALUES (?, ?, 0)");
			ps.setString(1, name);
			ps.setDouble(2, Constants.Holdings);
			ps.executeUpdate();
		} catch (final Exception e) {
			return false;
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

		return true;
	}

	public final boolean remove(final String name) {
		Connection conn = null;
		final ResultSet rs = null;
		PreparedStatement ps = null;

		try {
			conn = iConomy.getiCoDatabase().getConnection();
			ps = conn.prepareStatement("DELETE FROM " + Constants.SQLTable + " WHERE username = ? LIMIT 1");
			ps.setString(1, name);
			ps.executeUpdate();
		} catch (final Exception e) {
			return false;
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

		return true;
	}

	public final boolean purge() {
		Connection conn = null;
		final ResultSet rs = null;
		PreparedStatement ps = null;

		try {
			conn = iConomy.getiCoDatabase().getConnection();
			ps = conn.prepareStatement("DELETE FROM " + Constants.SQLTable + " WHERE balance = ?");
			ps.setDouble(1, Constants.Holdings);
			ps.executeUpdate();
		} catch (final Exception e) {
			return false;
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

		return true;
	}

	/**
	 * Removes all accounts from the database. Do not use this.
	 *
	 * @return
	 */
	public final boolean emptyDatabase() {
		Connection conn = null;
		Statement ps = null;

		try {
			conn = iConomy.getiCoDatabase().getConnection();
			ps = conn.createStatement();
			ps.execute("TRUNCATE TABLE " + Constants.SQLTable);
		} catch (final Exception e) {
			return false;
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

		return true;
	}

	public final List<Double> values() {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		final List<Double> Values = new ArrayList<>();

		try {
			conn = iConomy.getiCoDatabase().getConnection();
			ps = conn.prepareStatement("SELECT balance FROM " + Constants.SQLTable);
			rs = ps.executeQuery();

			while (rs.next()) {
				Values.add(rs.getDouble("balance"));
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

		return Values;
	}

	public final LinkedHashMap<String, Double> ranking(final int amount) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		final LinkedHashMap<String, Double> Ranking = new LinkedHashMap<>();

		try {
			conn = iConomy.getiCoDatabase().getConnection();
			ps = conn.prepareStatement("SELECT username,balance FROM " + Constants.SQLTable + " WHERE hidden = 0 ORDER BY balance DESC LIMIT ?");
			ps.setInt(1, amount);
			rs = ps.executeQuery();

			while (rs.next()) {
				Ranking.put(rs.getString("username"), rs.getDouble("balance"));
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

		return Ranking;
	}

	public final Account get(final String name) {
		if (exists(name)) {
			return new Account(name);
		} else {
			if (!create(name)) {
				return null;
			}
		}

		return new Account(name);
	}
}
