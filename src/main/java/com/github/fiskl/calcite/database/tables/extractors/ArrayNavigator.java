package com.github.fiskl.calcite.database.tables.extractors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.calcite.util.Stacks;

import com.github.fiskl.calcite.database.tables.TablePath;

final class ArrayNavigator extends Navigator {

	interface Callback {

		Object[] convert(final List<Object> keys, Object object, final int[] projects);

	}

	private final Callback callback;
	private final String field;

	ArrayNavigator(final TablePath path, final Callback callback, final String field) {
		super(path);
		this.callback = callback;
		this.field = field;
	}

	@Override
	Collection<Object[]> extract0(final List<Object> keys, final Map<String, Object> data, final int[] projects) {
		final Object step = data.get(this.field);
		final Collection<Object[]> rows = new ArrayList<>();
		if (step instanceof List) {
			@SuppressWarnings("rawtypes")
			final List list = (List) step;
			Integer index = 0;
			for (final Object entry : list) {
				Stacks.push(keys, index);
				try {
					rows.add(this.callback.convert(keys, entry, projects));
				} finally {
					Stacks.pop(keys, index);
				}
				index++;
			}
		}
		return rows;
	}

}