package com.github.fiskl.calcite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.calcite.jdbc.CalciteConnection;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.schema.Table;
import org.apache.calcite.sql.type.SqlTypeName;

import com.github.fiskl.calcite.database.Schema;
import com.github.fiskl.calcite.database.tables.BaseTable;
import com.github.fiskl.calcite.database.tables.SyntheticKVTable;
import com.github.fiskl.calcite.database.tables.SyntheticTable;
import com.github.fiskl.calcite.database.tables.TableFieldType;
import com.github.fiskl.calcite.database.tables.TablePath;

/**
 * Main testing class - please run!
 */
public class GrokkingCalcite {

	private static final String INDEX = "SchemaTest";

	public static void main(final String[] args) throws Exception {
		// Java naming convention
		final Properties info = new Properties();
		info.put("lex", "JAVA");

		// Connection and schema preparation
		final Connection connection = DriverManager.getConnection("jdbc:calcite:", info);
		final CalciteConnection conn = connection.unwrap(CalciteConnection.class);
		final SchemaPlus root = conn.getRootSchema();
		final Schema schema = new Schema(buildTables());
		root.add(INDEX, schema);
		conn.setSchema(INDEX);

		System.setProperty("calcite.debug", "true");

		// Execution of this class requires a debug hook to be used.
		SqlExecutor.executeSql(
				connection,
				"SELECT b._id, b.primary1, b.primary2, b.primary3, kv._id0, kv.kvArray FROM base b "
				+ "JOIN kv ON b._id=kv._id ");
	}

	private static Map<String, Table> buildTables() {
		final Map<String, Table> tables = new HashMap<>();
		tables.put("base", new BaseTable(Arrays.asList(
				field("primary1","primary1",SqlTypeName.VARCHAR),
				field("primary2","primary2",SqlTypeName.VARCHAR),
				field("primary3","primary3",SqlTypeName.BIGINT))));
		tables.put("kv", new SyntheticKVTable(path(),field("kvArray","kvArray",SqlTypeName.DOUBLE)));
		tables.put("secondary", new SyntheticTable(path("tblArray.inner"), 
				Arrays.asList(field("value", "value", SqlTypeName.BOOLEAN))));
		return tables;
	}

	private static TableFieldType field(final String alias, final String path, final SqlTypeName sqlType) {
		return new TableFieldType(alias, path, sqlType);
	}

	private static TablePath path(final String... paths) {
		return new TablePath(paths);
	}

}
