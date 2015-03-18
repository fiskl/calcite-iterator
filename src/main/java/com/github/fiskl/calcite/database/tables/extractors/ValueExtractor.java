package com.github.fiskl.calcite.database.tables.extractors;

import java.util.Map;

final class ValueExtractor {

	private final String[] parts;

	ValueExtractor(final String name) {
		this.parts = name.split("\\.");
	}

	@SuppressWarnings("unchecked")
	Object extract(final Map<String, Object> input) {
		Map<String, Object> current = input;
		for (int n = 0; n < this.parts.length - 1; n++) {
			final Object candidate = current.get(this.parts[n]);
			if (candidate instanceof Map) {
				current = (Map<String, Object>) candidate;
			} else {
				return null;
			}
		}
		return current.get(this.parts[this.parts.length - 1]);
	}

}