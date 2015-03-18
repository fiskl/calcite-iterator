package com.github.fiskl.calcite.database.tables.extractors;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.github.fiskl.calcite.database.tables.TablePath;

final class NavigatorMapLeaf extends Navigator {

	interface Callback {

		Object[] convert(final List<Object> keys, final Map<String, Object> object, final int[] projects);

	}

	private final Callback callback;

	NavigatorMapLeaf(final TablePath path, final Callback callback) {
		super(path);
		this.callback = callback;
	}

	@Override
	Collection<Object[]> extract0(final List<Object> keys, final Map<String, Object> data, final int[] projects) {
		return Collections.singletonList(this.callback.convert(keys, data, projects));
	}
}