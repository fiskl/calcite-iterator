package com.github.fiskl.calcite.database.tables;

import org.apache.calcite.sql.type.SqlTypeName;

public final class TableFieldType {

	private final String alias;
	private final String path;
	private final SqlTypeName sqlType;

	public TableFieldType(final String alias, final String path, final SqlTypeName sqlType) {
		this.alias = alias;
		this.path = path;
		this.sqlType = sqlType;
	}

	public String getAlias() {
		return this.alias;
	}

	public SqlTypeName getSqlType() {
		return this.sqlType;
	}

	public String getPath() {
		return this.path;
	}

}
