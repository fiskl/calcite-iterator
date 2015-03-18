package com.github.fiskl.calcite.database.tables;

public final class TablePath {

	private final String[] paths;

	public TablePath(final String... paths) {
		this.paths = paths;
	}

	public String[] getPaths() {
		return this.paths;
	}

}
