package com.iConomy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.Permissions;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Timer;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.iConomy.entity.Players;
import com.iConomy.net.Database;
import com.iConomy.system.Account;
import com.iConomy.system.Accounts;
import com.iConomy.system.Bank;
import com.iConomy.system.Banks;
import com.iConomy.system.Interest;
import com.iConomy.system.Transactions;
import com.iConomy.util.Constants;
import com.iConomy.util.Downloader;
import com.iConomy.util.FileManager;
import com.iConomy.util.Misc;

/**
 * iConomy by Team iCo
 *
 * @copyright Copyright AniGaiku LLC (C) 2010-2011
 * @author Nijikokun <nijikokun@gmail.com>
 * @author Coelho <robertcoelho@live.com>
 * @author ShadowDrakken <shadowdrakken@gmail.com>
 *
 *         This program is free software: you can redistribute it and/or modify it under the terms
 *         of the GNU General Public License as published by the Free Software Foundation, either
 *         version 2 of the License, or (at your option) any later version.
 *
 *         This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *         without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *         See the GNU General Public License for more details.
 *
 *         You should have received a copy of the GNU General Public License along with this
 *         program. If not, see <http://www.gnu.org/licenses/>.
 */
public class iConomy extends JavaPlugin {
	public static Banks Banks = null;
	public static Accounts Accounts = null;

	private static Server Server = null;
	private static Database Database = null;
	private static Transactions Transactions = null;
	private static Permissions Permissions = null;
	private static Players playerListener = null;
	private static Timer Interest_Timer = null;

	@Override
	public void onEnable() {
		Locale.setDefault(Locale.US);

		// Get the server
		Server = getServer();

		// Lib Directory
		new File("lib" + File.separator).mkdir();
		new File("lib" + File.separator).setWritable(true);
		new File("lib" + File.separator).setExecutable(true);

		// Plugin Directory
		getDataFolder().mkdir();
		getDataFolder().setWritable(true);
		getDataFolder().setExecutable(true);

		// Setup the path.
		Constants.Plugin_Directory = getDataFolder().getPath();

		// Grab plugin details
		final PluginDescriptionFile pdfFile = this.getDescription();

		// Versioning File
		final FileManager file = new FileManager(getDataFolder().getPath(), "VERSION", false);

		// Default Files
		extract("Config.yml");
		extract("Template.yml");

		try {
			new YamlConfiguration();
			Constants.load(YamlConfiguration.loadConfiguration(new File(getDataFolder(), "Config.yml")));
		} catch (final Exception e) {
			Server.getPluginManager().disablePlugin(this);
			System.out.println("[iConomy] Failed to retrieve configuration from directory.");
			System.out.println("[iConomy] Please back up your current settings and let iConomy recreate it.");
			return;
		}

		if (Misc.is(Constants.DatabaseType, new String[] { "sqlite", "h2", "h2sql", "h2db" })) {
			if (!new File("lib" + File.separator, "h2.jar").exists()) {
				Downloader.install(Constants.H2_Jar_Location, "h2.jar");
			}
		} else {
			if (!new File("lib" + File.separator, "mysql-connector-java-bin.jar").exists()) {
				Downloader.install(Constants.MySQL_Jar_Location, "mysql-connector-java-bin.jar");
			}
		}

		try {
			Database = new Database();
			Database.setupAccountTable();

			if (Constants.Banking) {
				Database.setupBankTable();
				Database.setupBankRelationTable();
			}
		} catch (final Exception e) {
			System.out.println("[iConomy] Database initialization failed: " + e);
			Server.getPluginManager().disablePlugin(this);
			return;

		}

		try {
			Transactions = new Transactions();
			Database.setupTransactionTable();
		} catch (final Exception e) {
			System.out.println("[iConomy] Could not load transaction logger: " + e);
		}

		// Check version details before the system loads
		update(file, Double.valueOf(pdfFile.getVersion()));

		// Initialize default systems
		Accounts = new Accounts();

		// Initialize the banks
		if (Constants.Banking)
			Banks = new Banks();

		try {
			if (Constants.Interest) {
				final long time = Constants.InterestSeconds * 1000L;

				Interest_Timer = new Timer();
				Interest_Timer.scheduleAtFixedRate(new Interest(getDataFolder().getPath()), time, time);
			}
		} catch (final Exception e) {
			System.out.println("[iConomy] Failed to start interest system: " + e);
			Server.getPluginManager().disablePlugin(this);
			return;
		}

		// Initializing Listeners
		playerListener = new Players(getDataFolder().getPath());

		// Console Detail
		System.out.println("[iConomy] v" + pdfFile.getVersion() + " (" + Constants.Codename + ") loaded.");
		System.out.println("[iConomy] Developed by: " + pdfFile.getAuthors());
	}

	@Override
	public void onDisable() {
		try {
			if (Misc.is(Constants.DatabaseType, new String[] { "sqlite", "h2", "h2sql", "h2db" })) {
				Database.connectionPool().dispose();
			}

			System.out.println("[iConomy] Plugin disabled.");
		} catch (final Exception e) {
			System.out.println("[iConomy] Plugin disabled.");
		} finally {
			if (Interest_Timer != null) {
				Interest_Timer.cancel();
			}

			Server = null;
			Banks = null;
			Accounts = null;
			Database = null;
			Permissions = null;
			Transactions = null;
			playerListener = null;
			Interest_Timer = null;
		}
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String commandLabel, final String[] args) {
		final String[] split = new String[args.length + 1];
		split[0] = cmd.getName().toLowerCase();
		System.arraycopy(args, 0, split, 1, args.length);

		playerListener.onPlayerCommand(sender, split);
		return false;
	}

	private void update(final FileManager file, final double version) {
		if (file.exists()) {
			file.read();

			try {
				final double current = Double.parseDouble(file.getSource());
				final LinkedList<String> MySQL = new LinkedList<String>();
				final LinkedList<String> GENERIC = new LinkedList<String>();
				LinkedList<String> SQL = new LinkedList<String>();

				if (current != version) {
					if (current < 4.64) {
						MySQL.add("ALTER TABLE " + Constants.SQLTable + " ADD hidden boolean DEFAULT '0';");
						GENERIC.add("ALTER TABLE " + Constants.SQLTable + " ADD HIDDEN BOOLEAN DEFAULT '0';");
					}

					if (current < 4.62) {
						MySQL.add("ALTER IGNORE TABLE " + Constants.SQLTable + " ADD UNIQUE INDEX(username(32));");
						GENERIC.add("ALTER TABLE " + Constants.SQLTable + " ADD UNIQUE(username);");
					}

					if (!MySQL.isEmpty() && !GENERIC.isEmpty()) {
						Connection conn = null;
						final ResultSet rs = null;
						Statement stmt = null;

						try {
							conn = iConomy.getiCoDatabase().getConnection();
							stmt = null;

							System.out.println(" - Updating " + Constants.DatabaseType + " Database for latest iConomy");

							int i = 1;
							SQL = Constants.DatabaseType.equalsIgnoreCase("mysql") ? MySQL : GENERIC;

							for (final String Query : SQL) {
								stmt = conn.createStatement();
								stmt.execute(Query);

								System.out.println("   Executing SQL Query #" + i + " of " + SQL.size());
								++i;
							}

							file.write(version);

							System.out.println(" + Database Update Complete.");
						} catch (final SQLException e) {
							System.out.println("[iConomy] Error updating database: " + e);
						} finally {
							if (stmt != null)
								try {
									stmt.close();
								} catch (final SQLException ex) {
								}

							if (rs != null)
								try {
									rs.close();
								} catch (final SQLException ex) {
								}

							iConomy.getiCoDatabase().close(conn);
						}
					}
				} else {
					file.write(version);
				}
			} catch (final Exception e) {
				System.out.println("[iConomy] Error on version check: ");
				e.printStackTrace();
				file.delete();
			}
		} else {
			if (!Constants.DatabaseType.equalsIgnoreCase("flatfile")) {
				String[] SQL = {};

				final String[] MySQL = { "DROP TABLE " + Constants.SQLTable + ";", "RENAME TABLE ibalances TO " + Constants.SQLTable + ";",
						"ALTER TABLE " + Constants.SQLTable + " CHANGE  player  username TEXT NOT NULL, CHANGE balance balance DECIMAL(64, 2) NOT NULL;" };

				final String[] SQLite = { "DROP TABLE " + Constants.SQLTable + ";",
						"CREATE TABLE '" + Constants.SQLTable + "' ('id' INT ( 10 ) PRIMARY KEY , 'username' TEXT , 'balance' DECIMAL ( 64 , 2 ));",
						"INSERT INTO " + Constants.SQLTable + "(id, username, balance) SELECT id, player, balance FROM ibalances;", "DROP TABLE ibalances;" };

				Connection conn = null;
				ResultSet rs = null;
				PreparedStatement ps = null;

				try {
					conn = iConomy.getiCoDatabase().getConnection();
					final DatabaseMetaData dbm = conn.getMetaData();
					rs = dbm.getTables(null, null, "ibalances", null);
					ps = null;

					if (rs.next()) {
						System.out.println(" - Updating " + Constants.DatabaseType + " Database for latest iConomy");

						int i = 1;
						SQL = Constants.DatabaseType.equalsIgnoreCase("mysql") ? MySQL : SQLite;

						for (final String Query : SQL) {
							ps = conn.prepareStatement(Query);
							ps.executeQuery(Query);

							System.out.println("   Executing SQL Query #" + i + " of " + SQL.length);
							++i;
						}

						System.out.println(" + Database Update Complete.");
					}

					file.write(version);
				} catch (final SQLException e) {
					System.out.println("[iConomy] Error updating database: " + e);
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
			}

			file.create();
			file.write(version);
		}
	}

	private void extract(final String name) {
		final File actual = new File(getDataFolder(), name);
		if (!actual.exists()) {
			final InputStream input = this.getClass().getResourceAsStream("/default/" + name);
			if (input != null) {
				FileOutputStream output = null;

				try {
					output = new FileOutputStream(actual);
					final byte[] buf = new byte[8192];
					int length = 0;

					while ((length = input.read(buf)) > 0) {
						output.write(buf, 0, length);
					}

					System.out.println("[iConomy] Default setup file written: " + name);
				} catch (final Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if (input != null) {
							input.close();
						}
					} catch (final Exception e) {
					}
					try {
						if (output != null) {
							output.close();
						}
					} catch (final Exception e) {
					}
				}
			}
		}
	}

	/**
	 * Formats the holding balance in a human readable form with the currency attached:<br />
	 * <br />
	 * 20000.53 = 20,000.53 Coin<br />
	 * 20000.00 = 20,000 Coin
	 *
	 * @param account
	 *            The name of the account you wish to be formatted
	 * @return String
	 */
	public static String format(final String account) {
		return getAccount(account).getHoldings().toString();
	}

	/**
	 * Formats the balance in a human readable form with the currency attached:<br />
	 * <br />
	 * 20000.53 = 20,000.53 Coin<br />
	 * 20000.00 = 20,000 Coin
	 *
	 * @param account
	 *            The name of the account you wish to be formatted
	 * @return String
	 */
	public static String format(final String bank, final String account) {
		return new Bank(bank).getAccount(account).getHoldings().toString();
	}

	/**
	 * Formats the money in a human readable form with the currency attached:<br />
	 * <br />
	 * 20000.53 = 20,000.53 Coin<br />
	 * 20000.00 = 20,000 Coin
	 *
	 * @param amount
	 *            double
	 * @return String
	 */
	public static String format(final double amount) {
		final DecimalFormat formatter = new DecimalFormat("#,##0.00");
		String formatted = formatter.format(amount);

		if (formatted.endsWith(".")) {
			formatted = formatted.substring(0, formatted.length() - 1);
		}

		return Misc.formatted(formatted, Constants.Major, Constants.Minor);
	}

	/**
	 * Grab an account, if it doesn't exist, create it.
	 *
	 * @param name
	 * @return Account or null
	 */
	public static Account getAccount(final String name) {
		return Accounts.get(name);
	}

	public static boolean hasAccount(final String name) {
		return Accounts.exists(name);
	}

	/**
	 * Grab the bank to modify and access bank accounts.
	 *
	 * @return Bank
	 */
	public static Bank getBank(final String name) {
		return Banks.get(name);
	}

	/**
	 * Grab the bank to modify and access bank accounts.
	 *
	 * @return Bank
	 */
	public static Bank getBank(final int id) {
		return Banks.get(id);
	}

	/**
	 * Grabs Database controller.
	 *
	 * @return iDatabase
	 */
	public static Database getiCoDatabase() {
		return Database;
	}

	/**
	 * Grabs Transaction Log Controller.
	 *
	 * Used to log transactions between a player and anything. Such as the system or another player
	 * or just enviroment.
	 *
	 * @return T
	 */
	public static Transactions getTransactions() {
		return Transactions;
	}

	/**
	 * Get the PermissionHandler
	 *
	 * @return PermissionHandler or Null depending on Plugin state.
	 */
	public static Permissions getPermissions() {
		return Permissions;
	}

	/**
	 * Check and see if the sender has the permission as designated by node.
	 *
	 * @param sender
	 * @param node
	 * @return boolean
	 */
	public static boolean hasPermissions(final CommandSender sender, final String node) {
		if (sender instanceof Player) {
			final Player player = (Player) sender;
			if (Permissions != null)
				return player.hasPermission(node);
			return player.isOp();
		}

		return true;
	}

	/**
	 * Grab the server so we can do various activities if needed.
	 *
	 * @return Server
	 */
	public static Server getBukkitServer() {
		return Server;
	}

}
