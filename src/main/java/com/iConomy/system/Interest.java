package com.iConomy.system;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;

import org.bukkit.entity.Player;

import com.iConomy.iConomy;
import com.iConomy.util.Constants;
import com.iConomy.util.Messaging;
import com.iConomy.util.Template;

public class Interest extends TimerTask {
	final Template Template;

	public Interest(final String directory) {
		Template = new Template(directory, "Messages.yml");
	}

	@Override
	public final void run() {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		final DecimalFormat DecimalFormat = new DecimalFormat("#.##");
		final List<String> players = new ArrayList<>();
		final HashMap<String, Integer> bankPlayers = new HashMap<>();

		if (Constants.InterestOnline) {
			final Collection<? extends Player> player = iConomy.getBukkitServer().getOnlinePlayers();

			if (Constants.InterestType.equalsIgnoreCase("players") || !Constants.Banking) {
				for (final Player p : player) {
					players.add(p.getName());
				}
			} else {
				for (final Player p : player) {
					final Account account = iConomy.getAccount(p.getName());

					if (account != null) {
						for (final BankAccount baccount : account.getBankAccounts()) {
							bankPlayers.put(p.getName(), baccount.getBankId());
						}
					}
				}
			}
		} else {
			conn = iConomy.getiCoDatabase().getConnection();

			try {
				if (Constants.InterestType.equalsIgnoreCase("players") || !Constants.Banking)
					ps = conn.prepareStatement("SELECT * FROM " + Constants.SQLTable);
				else {
					ps = conn.prepareStatement("SELECT account_name,bank_id FROM " + Constants.SQLTable + "_BankRelations group by bank_id");
				}

				rs = ps.executeQuery();

				while (rs.next()) {
					if (Constants.InterestType.equalsIgnoreCase("players") || !Constants.Banking)
						players.add(rs.getString("username"));
					else {
						bankPlayers.put(rs.getString("account_name"), rs.getInt("bank_id"));
					}
				}
			} catch (final Exception E) {
				System.out.println("[iConomy] Error executing query for interest: " + E.getMessage());
			} finally {
				if (conn != null)
					conn = null;

				if (ps != null)
					ps = null;

				if (rs != null)
					rs = null;
			}
		}

		final double cutoff = Constants.InterestCutoff;
		double amount = 0.0;
		boolean percentage = false;

		if (Constants.InterestPercentage != 0.0) {
			percentage = true;
		} else {
			final double min = Constants.InterestMin;
			final double max = Constants.InterestMax;

			try {
				if (min != max)
					amount = Double.valueOf(DecimalFormat.format(Math.random() * (max - min) + min));
				else {
					amount = max;
				}
			} catch (final NumberFormatException e) {
				System.out.println("[iConomy] Invalid Interest: " + e);
			}
		}

		try {
			conn = iConomy.getiCoDatabase().getConnection();
			conn.setAutoCommit(false);

			if (Constants.InterestType.equalsIgnoreCase("players") || !Constants.Banking) {
				final String updateSQL = "UPDATE " + Constants.SQLTable + " SET balance = ? WHERE username = ?";
				ps = conn.prepareStatement(updateSQL);

				for (final String name : players) {
					final Account account = iConomy.getAccount(name);

					if (account != null) {
						final Holdings holdings = account.getHoldings();

						if (holdings != null) {
							final double balance = holdings.balance();
							final double original = balance;

							if (cutoff > 0.0) {
								if (original >= cutoff) {
									continue;
								}
							} else if (cutoff < 0.0) {
								if (original <= cutoff) {
									continue;
								}
							}

							if (percentage) {
								amount = Math.round(Constants.InterestPercentage * balance / 100);
							}

							ps.setDouble(1, balance + amount);
							ps.setString(2, name);
							ps.addBatch();

							if (Constants.InterestAnn && Constants.InterestOnline) {
								Messaging.send(
										iConomy.getBukkitServer().getPlayer(name),
										Template.parse("interest.announcement", new String[] { "+amount,+money,+interest,+a,+m,+i" },
												new Object[] { iConomy.format(amount) }));
							}

							if (amount < 0.0)
								iConomy.getTransactions().insert("[System Interest]", name, 0.0, original, 0.0, 0.0, amount);
							else {
								iConomy.getTransactions().insert("[System Interest]", name, 0.0, original, 0.0, amount, 0.0);
							}
						}
					}
				}
			} else {
				final String updateSQL = "UPDATE " + Constants.SQLTable + "_BankRelations SET holdings = ? WHERE account_name = ? AND bank_id = ?";
				ps = conn.prepareStatement(updateSQL);

				for (final String name : bankPlayers.keySet()) {
					final Account account = iConomy.getAccount(name);

					if (account != null) {
						final Holdings holdings = account.getBankHoldings(bankPlayers.get(name));

						if (holdings != null) {
							final double balance = holdings.balance();
							final double original = balance;

							if (cutoff > 0.0) {
								if (original >= cutoff) {
									continue;
								}
							} else if (cutoff < 0.0) {
								if (original <= cutoff) {
									continue;
								}
							}

							if (percentage) {
								amount = Math.round(Constants.InterestPercentage * balance / 100);
							}

							ps.setDouble(1, balance + amount);
							ps.setString(2, name);
							ps.setInt(3, bankPlayers.get(name));
							ps.addBatch();

							if (Constants.InterestAnn && Constants.InterestOnline) {
								Messaging.send(
										iConomy.getBukkitServer().getPlayer(name),
										Template.parse("interest.announcement", new String[] { "+amount,+money,+interest,+a,+m,+i" },
												new Object[] { iConomy.format(amount) }));
							}

							if (amount < 0.0)
								iConomy.getTransactions().insert("[System Interest]", name, 0.0, original, 0.0, 0.0, amount);
							else {
								iConomy.getTransactions().insert("[System Interest]", name, 0.0, original, 0.0, amount, 0.0);
							}
						}
					}
				}
			}

			// Execute the batch.
			ps.executeBatch();

			// Commit
			conn.commit();

			ps.clearBatch();
		} catch (final BatchUpdateException e) {
			System.out.println(e);
		} catch (final SQLException e) {
			System.out.println(e);
		} finally {
			if (ps != null)
				try {
					ps.close();
				} catch (final SQLException ex) {
				}

			if (conn != null)
				iConomy.getiCoDatabase().close(conn);
		}
	}
}
