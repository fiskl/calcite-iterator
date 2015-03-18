package com.github.fiskl.calcite.database.tables.extractors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.fiskl.calcite.database.tables.TableFieldType;

final class ValuesExtractor {

	private final List<ValueExtractor> valueExtractors = new ArrayList<>();

	public ValuesExtractor(final List<TableFieldType> fields) {
		for (final TableFieldType field : fields) {
			this.valueExtractors.add(new ValueExtractor(field.getPath()));
		}
	}

	ValuesExtractor(final String... fields) {
		for (final String field : fields) {
			this.valueExtractors.add(new ValueExtractor(field));
		}
	}

	private Object[] convert(final List<Object> keys, final Map<String, Object> object) {
		final Object[] output = new Object[this.valueExtractors.size() + keys.size()];
		int i = 0;
		for (final Object key : keys) {
			output[i] = key;
			i++;
		}
		for (int n = 0; n < this.valueExtractors.size(); n++) {
			output[i + n] = this.valueExtractors.get(n).extract(object);
		}
		return output;
	}

	Object[] convert(final List<Object> keys, final Map<String, Object> object, final int[] projects) {
		if (projects == null) {
			return convert(keys, object);
		}
		final int offset = keys.size();
		final Object[] output = new Object[projects.length];
		for (int n = 0; n < output.length; n++) {
			final int index = projects[n];
			if (index < offset) {
				output[n] = keys.get(index);
			} else {
				output[n] = this.valueExtractors.get(projects[n - offset]).extract(object);
			}
		}
		return output;
	}

	static Object[] convert(final List<Object> keys, final Object object, final int[] projects) {
		if (projects == null) {
			return convert(keys, object);
		}
		final int offset = keys.size();
		final Object[] output = new Object[projects.length];
		for (int n = 0; n < output.length; n++) {
			final int index = projects[n];
			if (index < offset) {
				output[n] = keys.get(index);
			} else {
				output[n] = object;
			}
		}
		return output;
	}

	private static Object[] convert(final List<Object> keys, final Object object) {
		final Object[] output = new Object[keys.size() + 1];
		int i = 0;
		for (final Object key : keys) {
			output[i] = key;
			i++;
		}
		output[i] = object;
		return output;
	}

}