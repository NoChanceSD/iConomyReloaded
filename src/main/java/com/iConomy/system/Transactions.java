package com.iConomy.system;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.iConomy.iConomy;
import com.iConomy.util.Constants;

public class Transactions {

	/**
	 * Inserts data into transaction without using seperate methods, direct method.
	 *
	 * @param from
	 * @param to
	 * @param gain
	 * @param loss
	 */
	public final void insert(final String from, final String to, final double from_balance, final double to_balance, final double set, final double gain,
			final double loss) {
		if (!Constants.Logging)
			return;

		int i = 1;
		final long timestamp = System.currentTimeMillis() / 1000;

		final Object[] data = new Object[] { from, to, from_balance, to_balance, timestamp, set, gain, loss };

		Connection conn = null;
		final ResultSet rs = null;
		PreparedStatement ps = null;

		try {
			conn = iConomy.getiCoDatabase().getConnection();
			ps = conn
					.prepareStatement("INSERT INTO "
							+ Constants.SQLTable
							+ "_Transactions(account_from, account_to, account_from_balance, account_to_balance, `timestamp`, `set`, gain, loss) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");

			for (final Object obj : data) {
				ps.setObject(i, obj);
				i++;
			}

			ps.executeUpdate();
		} catch (final SQLException e) {
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
	}
}
