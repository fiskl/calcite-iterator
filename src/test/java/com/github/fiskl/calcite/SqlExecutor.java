package com.github.fiskl.calcite;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.calcite.prepare.Prepare.Materialization;
import org.apache.calcite.runtime.Hook;
import org.apache.calcite.tools.Program;
import org.apache.calcite.util.Holder;
import org.apache.calcite.util.Pair;

import com.github.fiskl.calcite.programs.ProgramFactory;
import com.google.common.base.Function;

public final class SqlExecutor {

	public static void executeSql(final Connection connection, final String sql) throws SQLException {
		System.out.println("SQL [" + sql + "]");

		// Use of this hook is the key issue with this project
		final Hook.Closeable closeable = Hook.PROGRAM.addThread(new Function<Pair<List<Materialization>, Holder<Program>>, Void>() {
			@Override
			public Void apply(final Pair<List<Materialization>, Holder<Program>> in) {
				final Program program = ProgramFactory.create();
				in.getValue().set(program);
				return null;
			}
		});

		try (
			final Statement statement = connection.createStatement();
			final ResultSet rs = statement.executeQuery(sql)) {
			while (rs.next()) {
				final ResultSetMetaData metaData = rs.getMetaData();
				for (int n = 1; n <= metaData.getColumnCount(); n++) {
					System.out.printf("%s=%s, ", metaData.getColumnLabel(n), rs.getObject(n));
				}
				System.out.println();
			}
		} finally {
            closeable.close();
        }
	}

	private SqlExecutor() {
	}

}
