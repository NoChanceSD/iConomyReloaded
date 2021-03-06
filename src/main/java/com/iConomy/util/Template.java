package com.iConomy.util;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Template {

	private final File tplFile;
	private final FileConfiguration tpl;

	public Template(final String directory, final String filename) {
		this.tplFile = new File(directory, filename);
		this.tpl = YamlConfiguration.loadConfiguration(tplFile);
		try {
			upgrade();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public final void upgrade() throws IOException {
		final LinkedHashMap<String, String> nodes = new LinkedHashMap<>();

		if (this.tpl.getString("error.bank.exists") == null) {
			nodes.put("tag.money", "<green>[<white>Money<green>] ");
			nodes.put("tag.bank", "<green>[<white>Bank<green>] ");
			nodes.put("banks.create", "<green>Created bank <white>+name<green>.");
			nodes.put("banks.remove", "<rose>Deleted bank <white>+name<rose>.");
			nodes.put("banks.purge.bank", "<rose>Bank <white>+name<rose> was purged of inactive accounts.");
			nodes.put("banks.purge.all", "<rose>All banks were purged of inactive accounts.");
			nodes.put("error.bank.fee", "<rose>Sorry, this banks fee is more than you are holding.");
			nodes.put("error.bank.exists", "<rose>Sorry, that bank already exists.");
			nodes.put("error.bank.doesnt", "<rose>Sorry, that bank doesn't exist.");
			nodes.put("error.bank.couldnt", "<rose>Sorry, bank <white>+name <rose>couldn't be created.");
			nodes.put("error.bank.account.none", "<rose>Sorry, you do not have any bank accounts.");
			nodes.put("error.bank.account.exists", "<rose>Sorry, an account like that already exists with us.");
			nodes.put("error.bank.account.doesnt", "<rose>Sorry, you do not have an account with <white>+name<rose>.");
			nodes.put("error.bank.account.maxed", "<rose>Sorry, you already have a bank account.");
			nodes.put("error.bank.account.failed", "<rose>Sorry, failed to create account. Try again...");
			nodes.put("error.bank.account.none", "<rose>Sorry, no accounts found.");
			nodes.put("accounts.bank.create", "<green>Created account for <white>+name<green> with <white>+bank<green>.");
			nodes.put("accounts.bank.remove", "<green>Deleted account <white>+name<green> from <white>+bank<green>.");
			nodes.put("personal.bank.charge", "<green>Created account for <white>+name<green> with <white>+bank<green>.");
			nodes.put("personal.bank.sent", "<green>You sent <white>+amount<green> from <white>+bank<green> to <white>+name<green>.");
			nodes.put("personal.bank.transfer",
					"<green>Transferred <white>+amount<green> from <white>+bank<green> to <white>+name<green> at <white>+bankAlt<green>.");
			nodes.put("personal.bank.between", "<green>Transferred <white>+amount<green> from <white>+bank<green> to <white>+bankAlt<green>.");
			nodes.put("personal.bank.change", "<green>Changed main bank to <white>+bankAlt</green>.");
			nodes.put("list.banks.opening",
					"<green>Page #<white>+amount<green> of <white>+total<green> pages (<white>F: Fee<green>, <white>I: Initial Holdings<green>)");
			nodes.put("list.banks.empty", "<white>   No Banks Exist.");
			nodes.put("list.banks.all-entry", "<green> +name [F: <white>+fee<green>] [I: <white>+initial<green>] [<white>+major<green>/<white>+minor<green>]");
			nodes.put("list.banks.fee-major-entry", "<green> +name [F: <white>+fee<green>] [I: <white>+initial<green>] [<white>+major<green>]");
			nodes.put("list.banks.major-entry", "<green> +name [I: <white>+initial<green>] [<white>+major<green>]");
			nodes.put("accounts.empty", "<rose>Deleted <white>all<rose> accounts.");
			nodes.put("accounts.purge", "<rose>All inactive accounts were purged.");
			nodes.put("accounts.remove-total", "<green>Fully deleted account <white>+name<green>.");
		}

		if (this.tpl.getString("accounts.create") == null) {
			nodes.put("accounts.create", "<green>Created account with the name: <white>+name<green>.");
			nodes.put("accounts.remove", "<green>Deleted account: <white>+name<green>.");
			nodes.put("error.exists", "<rose>Account already exists.");
		}

		if (this.tpl.getString("accounts.status") == null) {
			nodes.put("error.online", "<rose>Sorry, nobody else is online.");
			nodes.put("accounts.status", "<green>Account status is now: <white>+status<green>.");
		}

		if (this.tpl.getString("interest.announcement") == null) {
			nodes.put("interest.announcement", "+amount <green>interest gained.");
		}

		if (!nodes.isEmpty()) {
			System.out.println(" - Upgrading Template.yml");
			int count = 1;

			for (final String node : nodes.keySet()) {
				System.out.println("   Adding node [" + node + "] #" + count + " of " + nodes.size());
				this.tpl.set(node, nodes.get(node));
				count++;
			}

			this.tpl.save(tplFile);
			System.out.println(" + Messages Upgrade Complete.");
		}
	}

	/**
	 * Grab the raw template line by the key, and don't save anything.
	 *
	 * @param key
	 *            The template key we wish to grab.
	 *
	 * @return <code>String</code> - Template line / string.
	 */
	public final String raw(final String key) {
		return this.tpl.getString(key);
	}

	public final String color(final String key) {
		return Messaging.parse(Messaging.colorize(this.raw(key)));
	}

	public final String parse(final String key, final Object[] argument, final Object[] points) {
		return Messaging.parse(Messaging.colorize(Messaging.argument(this.raw(key), argument, points)));
	}

}
