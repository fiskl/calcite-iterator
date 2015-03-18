package com.github.fiskl.calcite.database.tables.extractors;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;

import org.apache.calcite.util.Stacks;

import com.github.fiskl.calcite.database.tables.TablePath;

abstract class Navigator {

	private final Deque<String[]> paths = new ArrayDeque<>();
	private final List<Object> keys = new ArrayList<>();

	Navigator(final TablePath path) {
		for (final String entry : path.getPaths()) {
			this.paths.add(entry.split("\\."));
		}
	}

	final Collection<Object[]> extractAll(final String id, final Map<String, Object> data, final int[] projects) {
		Stacks.push(this.keys, id);
		try {
			return extract(data, projects);
		} finally {
			Stacks.pop(this.keys, id);
		}
	}

	@SuppressWarnings("unchecked")
	final Collection<Object[]> extract(final Map<String, Object> source, final int[] projects) {
		if (this.paths.isEmpty()) {
			return extract0(this.keys, source, projects);
		}
		final String[] currentPath = this.paths.poll();
		try {
			final Object step = navigateTo(source, currentPath);
			Integer index = 0;
			if (step instanceof Map) {
				Stacks.push(this.keys, index);
				try {
					return extract((Map<String, Object>) step, projects);
				} finally {
					Stacks.pop(this.keys, index);
				}
			} else if (step instanceof List) {
				final Collection<Object[]> rows = new ArrayList<>();
				for (final Object instance : (List<Object>) step) {
					Stacks.push(this.keys, index);
					try {
						if (instance instanceof Map) {
							rows.addAll(extract((Map<String, Object>) instance, projects));
						}
					} finally {
						Stacks.pop(this.keys, index);
					}
					index += 1;
				}
				return rows;
			} else {
				return Collections.emptyList();
			}

		} finally {
			this.paths.push(currentPath);
		}
	}

	abstract Collection<Object[]> extract0(List<Object> keys, Map<String, Object> data, int[] projects);

	@SuppressWarnings("unchecked")
	private Object navigateTo(final Map<String, Object> source, final String[] currentPath) {
		if (currentPath == null || currentPath.length == 0) {
			return source;
		} else {
			Object current = source.get(currentPath[0]);
			for (int i = 1; i < currentPath.length; i++) {
				if (current instanceof Map) {
					current = ((Map<String, Object>) current).get(currentPath[i]);
				} else {
					return null;
				}
			}
			return current;
		}
	}
}