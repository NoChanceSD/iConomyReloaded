package com.iConomy.system;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.iConomy.iConomy;
import com.iConomy.util.Constants;

/**
 * Banks, holder of all banks.
 *
 * @author Nijikokun
 */
@SuppressWarnings("resource")
public class Banks {

	/**
	 * Check and see if the bank exists through id.
	 *
	 * @param id
	 * @return Boolean
	 */
	public final boolean exists(final int id) {
		if (!Constants.Banking)
			return false;

		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		boolean exists = false;

		try {
			conn = iConomy.getiCoDatabase().getConnection();
			ps = conn.prepareStatement("SELECT * FROM " + Constants.SQLTable + "_Banks WHERE id = ? LIMIT 1");
			ps.setInt(1, id);
			rs = ps.executeQuery();
			exists = rs.next();
		} catch (final Exception e) {
			exists = false;
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

		return exists;
	}

	/**
	 * Check and see if the bank actually exists, through name.
	 *
	 * @param name
	 * @return Boolean
	 */
	public final boolean exists(final String name) {
		if (!Constants.Banking)
			return false;

		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		boolean exists = false;

		try {
			conn = iConomy.getiCoDatabase().getConnection();
			ps = conn.prepareStatement("SELECT * FROM " + Constants.SQLTable + "_Banks WHERE name = ? LIMIT 1");
			ps.setString(1, name);
			rs = ps.executeQuery();
			exists = rs.next();
		} catch (final Exception e) {
			exists = false;
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

		return exists;
	}

	/**
	 * Fetch the id through the name, no questions.
	 *
	 * @param name
	 * @return Integer
	 */
	private int getId(final String name) {
		if (!Constants.Banking)
			return -1;

		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		int id = 0;

		try {
			conn = iConomy.getiCoDatabase().getConnection();
			ps = conn.prepareStatement("SELECT id FROM " + Constants.SQLTable + "_Banks WHERE name = ? LIMIT 1");
			ps.setString(1, name);
			rs = ps.executeQuery();

			if (rs.next()) {
				id = rs.getInt("id");
			}
		} catch (final Exception e) {
			id = 0;
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

		return id;
	}

	/**
	 * Uses the default settings for a bank upon creation with a different name.
	 *
	 * @param name
	 * @return Bank
	 */
	public final Bank create(final String name) {
		if (!Constants.Banking)
			return null;

		if (!exists(name)) {
			Connection conn = null;
			final ResultSet rs = null;
			PreparedStatement ps = null;

			try {
				conn = iConomy.getiCoDatabase().getConnection();
				ps = conn.prepareStatement("INSERT INTO " + Constants.SQLTable + "_Banks(name, major, minor, initial, fee) VALUES (?, ?, ?, ?, ?)");

				ps.setString(1, name);
				ps.setString(2, Constants.BankMajor.get(0) + "," + Constants.BankMajor.get(1));
				ps.setString(3, Constants.BankMinor.get(0) + "," + Constants.BankMinor.get(1));
				ps.setDouble(4, Constants.BankHoldings);
				ps.setDouble(5, Constants.BankFee);

				ps.executeUpdate();
			} catch (final Exception e) {
				System.out.println("[iConomy] Failed to set holdings balance: " + e);
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

		return new Bank(name);
	}

	public final int count() {
		if (!Constants.Banking)
			return -1;

		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		int count = -1;

		try {
			conn = iConomy.getiCoDatabase().getConnection();
			ps = conn.prepareStatement("SELECT COUNT(id) AS count FROM " + Constants.SQLTable + "_Banks");
			rs = ps.executeQuery();

			if (rs.next()) {
				count = rs.getInt("count");
			}
		} catch (final Exception e) {
			return count;
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

		return count;
	}

	/**
	 * Count the number of accounts a person has.
	 *
	 * @param name
	 * @return Integer - Account count.
	 */
	public final int count(final String name) {
		if (!Constants.Banking)
			return -1;

		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		int count = -1;

		try {
			conn = iConomy.getiCoDatabase().getConnection();
			ps = conn.prepareStatement("SELECT COUNT(id) AS count FROM " + Constants.SQLTable + "_BankRelations WHERE account_name = ?");
			ps.setString(1, name);
			rs = ps.executeQuery();

			if (rs.next()) {
				count = rs.getInt("count");
			}
		} catch (final Exception e) {
			return count;
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

		return count;
	}

	/**
	 * Grab the bank, if it doesn't exist, return null.
	 *
	 * @param name
	 *            Name of bank to grab
	 * @return Bank object
	 */
	public final Bank get(final String name) {
		if (!Constants.Banking)
			return null;

		if (exists(name))
			return new Bank(name);
		else {
			return null;
		}
	}

	/**
	 * Grab the bank, if it doesn't exist, return null.
	 *
	 * @param id
	 *            Bank id
	 * @return Bank object
	 */
	public final Bank get(final int id) {
		if (!Constants.Banking)
			return null;

		if (exists(id))
			return new Bank(id);
		else {
			return null;
		}
	}

	/**
	 * Grab a list of all the balances / holdings inside banks.
	 *
	 * Allows us to utilize the entire economic status of banks for statistics.
	 *
	 * @return List<Double>
	 */
	public final List<Double> values() {
		if (!Constants.Banking)
			return null;

		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		final List<Double> Values = new ArrayList<>();

		try {
			conn = iConomy.getiCoDatabase().getConnection();
			ps = conn.prepareStatement("SELECT holdings FROM " + Constants.SQLTable + "_BankRelations");
			rs = ps.executeQuery();

			while (rs.next()) {
				Values.add(rs.getDouble("holdings"));
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
}
