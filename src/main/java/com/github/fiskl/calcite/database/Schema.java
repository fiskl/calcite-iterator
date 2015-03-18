package com.github.fiskl.calcite.database;

import java.util.Map;

import org.apache.calcite.schema.Table;
import org.apache.calcite.schema.impl.AbstractSchema;

public class Schema extends AbstractSchema {
	private final Map<String, Table> tables;

	public Schema(final Map<String, Table> tables) {
		this.tables = tables;
	}

	@Override
	public Map<String, Table> getTableMap() {
		return this.tables;
	}

	@Override
	public boolean isMutable() {
		return false;
	}

}
