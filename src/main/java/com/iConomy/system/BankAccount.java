/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iConomy.system;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.iConomy.iConomy;
import com.iConomy.util.Constants;

/**
 *
 * @author Nijikokun
 */
public class BankAccount {
	private final String BankName;
	private final int BankId;
	private final String AccountName;

	public BankAccount(final String BankName, final int BankId, final String AccountName) {
		this.BankName = BankName;
		this.BankId = BankId;
		this.AccountName = AccountName;
	}

	public final String getBankName() {
		return this.BankName;
	}

	public final int getBankId() {
		return this.BankId;
	}

	public final Holdings getHoldings() {
		return new Holdings(this.BankId, this.AccountName, true);
	}

	public final void remove() {
		Connection conn = null;
		final ResultSet rs = null;
		PreparedStatement ps = null;

		try {
			conn = iConomy.getiCoDatabase().getConnection();
			ps = conn.prepareStatement("DELETE FROM " + Constants.SQLTable + "_BankRelations WHERE bank_id = ? AND account_name = ?");
			ps.setInt(1, BankId);
			ps.setString(2, AccountName);
			ps.executeUpdate();
		} catch (final Exception e) {
			return;
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

		return;
	}
}
