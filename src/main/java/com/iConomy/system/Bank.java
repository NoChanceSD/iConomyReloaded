package com.iConomy.system;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import com.iConomy.iConomy;
import com.iConomy.util.Constants;

public class Bank {
	private final int id;
	private String name = "";

	public Bank(final String name) {
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

		this.id = id;
		this.name = name;
	}

	public Bank(final int id) {
		this.id = id;

		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;

		try {
			conn = iConomy.getiCoDatabase().getConnection();
			ps = conn.prepareStatement("SELECT name FROM " + Constants.SQLTable + "_Banks WHERE id = ? LIMIT 1");
			ps.setInt(1, id);
			rs = ps.executeQuery();

			if (rs.next()) {
				this.name = rs.getString("name");
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

			if (conn != null)
				try {
					conn.close();
				} catch (final SQLException ex) {
				}
		}
	}

	public final int getId() {
		return this.id;
	}

	public final String getName() {
		return this.name;
	}

	public final List<String> getMinor() {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		final List<String> minor = Constants.Minor;
		String asString = Constants.Minor.get(0) + "," + Constants.Minor.get(1);

		try {
			conn = iConomy.getiCoDatabase().getConnection();
			ps = conn.prepareStatement("SELECT minor FROM " + Constants.SQLTable + "_Banks WHERE id = ? LIMIT 1");
			ps.setInt(1, this.id);
			rs = ps.executeQuery();

			if (rs.next()) {
				asString = rs.getString("minor");

				final String[] denoms = asString.split(",");
				minor.set(0, denoms[0]);
				minor.set(1, denoms[1]);
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

			if (conn != null)
				try {
					conn.close();
				} catch (final SQLException ex) {
				}
		}

		return minor;
	}

	public final List<String> getMajor() {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		final List<String> major = Constants.Major;
		String asString = Constants.Major.get(0) + "," + Constants.Major.get(1);

		try {
			conn = iConomy.getiCoDatabase().getConnection();
			ps = conn.prepareStatement("SELECT major FROM " + Constants.SQLTable + "_Banks WHERE id = ? LIMIT 1");
			ps.setInt(1, this.id);
			rs = ps.executeQuery();

			if (rs.next()) {
				asString = rs.getString("major");

				final String[] denoms = asString.split(",");
				major.set(0, denoms[0]);
				major.set(1, denoms[1]);
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

			if (conn != null)
				try {
					conn.close();
				} catch (final SQLException ex) {
				}
		}

		return major;
	}

	public final double getInitialHoldings() {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		double initial = Constants.BankHoldings;

		try {
			conn = iConomy.getiCoDatabase().getConnection();
			ps = conn.prepareStatement("SELECT initial FROM " + Constants.SQLTable + "_Banks WHERE id = ? LIMIT 1");
			ps.setInt(1, this.id);
			rs = ps.executeQuery();

			if (rs.next()) {
				initial = rs.getDouble("initial");
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

			if (conn != null)
				try {
					conn.close();
				} catch (final SQLException ex) {
				}
		}

		return initial;
	}

	public final double getFee() {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		double fee = Constants.BankFee;

		try {
			conn = iConomy.getiCoDatabase().getConnection();
			ps = conn.prepareStatement("SELECT fee FROM " + Constants.SQLTable + "_Banks WHERE id = ? LIMIT 1");
			ps.setInt(1, this.id);
			rs = ps.executeQuery();

			if (rs.next()) {
				fee = rs.getDouble("fee");
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

			if (conn != null)
				try {
					conn.close();
				} catch (final SQLException ex) {
				}
		}

		return fee;
	}

	public final void setName(final String name) {
		Connection conn = null;
		final ResultSet rs = null;
		PreparedStatement ps = null;

		try {
			conn = iConomy.getiCoDatabase().getConnection();

			ps = conn.prepareStatement("UPDATE " + Constants.SQLTable + "_Banks SET name = ? WHERE id = ?");
			ps.setString(1, name);
			ps.setInt(2, this.id);
			ps.executeUpdate();

			this.name = name;
		} catch (final Exception e) {
			System.out.println("[iConomy] Failed to update bank name: ");
			e.printStackTrace();
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

	public final void setMajor(final String singular, final String plural) {
		Connection conn = null;
		final ResultSet rs = null;
		PreparedStatement ps = null;

		try {
			conn = iConomy.getiCoDatabase().getConnection();

			ps = conn.prepareStatement("UPDATE " + Constants.SQLTable + "_Banks SET major = ? WHERE id = ?");
			ps.setString(1, singular + "," + plural);
			ps.setInt(2, this.id);

			ps.executeUpdate();
		} catch (final Exception e) {
			System.out.println("[iConomy] Failed to update bank major: ");
			e.printStackTrace();
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

	public final void setMinor(final String singular, final String plural) {
		Connection conn = null;
		final ResultSet rs = null;
		PreparedStatement ps = null;

		try {
			conn = iConomy.getiCoDatabase().getConnection();

			ps = conn.prepareStatement("UPDATE " + Constants.SQLTable + "_Banks SET minor = ? WHERE id = ?");
			ps.setString(1, singular + "," + plural);
			ps.setInt(2, this.id);

			ps.executeUpdate();
		} catch (final Exception e) {
			System.out.println("[iConomy] Failed to update bank minor: ");
			e.printStackTrace();
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

	public final void setInitialHoldings(final double amount) {
		Connection conn = null;
		final ResultSet rs = null;
		PreparedStatement ps = null;

		try {
			conn = iConomy.getiCoDatabase().getConnection();

			ps = conn.prepareStatement("UPDATE " + Constants.SQLTable + "_Banks SET initial = ? WHERE id = ?");
			ps.setDouble(1, amount);
			ps.setInt(2, this.id);

			ps.executeUpdate();
		} catch (final Exception e) {
			System.out.println("[iConomy] Failed to update bank initial amount: ");
			e.printStackTrace();
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

	public final void setFee(final double amount) {
		Connection conn = null;
		final ResultSet rs = null;
		PreparedStatement ps = null;

		try {
			conn = iConomy.getiCoDatabase().getConnection();

			ps = conn.prepareStatement("UPDATE " + Constants.SQLTable + "_Banks SET fee = ? WHERE id = ?");
			ps.setDouble(1, amount);
			ps.setInt(2, this.id);
			ps.executeUpdate();
		} catch (final Exception e) {
			System.out.println("[iConomy] Failed to update bank fee: ");
			e.printStackTrace();
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

	/**
	 * Does the bank have record of the account in question? hasAccount or accountExists ?
	 * 
	 * @param account
	 *            The account in question
	 * @return boolean - Does the account exist?
	 */
	public final boolean hasAccount(final String account) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		boolean exists = false;

		try {
			conn = iConomy.getiCoDatabase().getConnection();
			ps = conn.prepareStatement("SELECT * FROM " + Constants.SQLTable + "_BankRelations WHERE account_name = ? AND bank_id = ? LIMIT 1");
			ps.setString(1, account);
			ps.setInt(2, id);
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

	public final HashMap<String, Double> getAccounts() {
		final HashMap<String, Double> accounts = new HashMap<>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;

		try {
			conn = iConomy.getiCoDatabase().getConnection();
			ps = conn.prepareStatement("SELECT * FROM " + Constants.SQLTable + "_BankRelations WHERE id = ? ORDER BY balance DESC");
			ps.setInt(1, this.id);
			rs = ps.executeQuery();

			while (rs.next()) {
				accounts.put(rs.getString("username"), rs.getDouble("balance"));
			}
		} catch (final Exception e) {
			return accounts;
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

		return accounts;
	}

	/**
	 * Fetch the account, Does not check for existance. Do that prior to using this to prevent null
	 * errors or any other issues.
	 *
	 * @param account
	 *            The account to grab
	 * @return Account (If it exists, null if not)
	 */
	public final BankAccount getAccount(final String account) {
		if (hasAccount(account)) {
			return new BankAccount(this.name, this.id, account);
		} else {
			return null;
		}
	}

	/**
	 * Add an account to the bank, if it already exists return false.
	 *
	 * @param account
	 *            Name of the account being created
	 */
	public final boolean createAccount(final String account) {
		if (!this.hasAccount(account)) {
			Connection conn = null;
			final ResultSet rs = null;
			PreparedStatement ps = null;

			try {
				conn = iConomy.getiCoDatabase().getConnection();
				ps = conn.prepareStatement("INSERT INTO " + Constants.SQLTable + "_BankRelations(account_name, bank_id, holdings) VALUES (?, ?, ?)");
				ps.setString(1, account);
				ps.setInt(2, this.id);
				ps.setDouble(3, getInitialHoldings());
				ps.executeUpdate();
			} catch (final Exception e) {
				System.out.println("[iConomy] Error inserting bank account: " + e);
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

		return false;
	}

	/**
	 * Completely remove an account from the bank and the database.
	 *
	 * @param account
	 */
	public final void removeAccount(final String account) {
		if (hasAccount(account)) {
			new BankAccount(this.name, this.id, account).remove();
		}
	}
}
