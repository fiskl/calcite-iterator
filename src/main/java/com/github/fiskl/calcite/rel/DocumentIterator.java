package com.github.fiskl.calcite.rel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.github.fiskl.calcite.database.tables.DataReciever;

/**
 * This class is a placeholder for the real document iterator, just injecting some made-up data.
 */
public class DocumentIterator implements Iterator<Void> {
	private static final Map<String, Object> DATA = complicatedMap();

	private final Iterator<String> documentIterator;

	private final Set<DataReciever> recievers;

	public DocumentIterator(final Set<DataReciever> recievers) {
		this.recievers = recievers;
		this.documentIterator = Arrays.<String> asList("injected-1", "injected-2", "injected-3").iterator();
	}

	@Override
	public boolean hasNext() {
		return this.documentIterator.hasNext();
	}

	@Override
	public Void next() {
		// Update in-memory tables with document values
		populate(this.documentIterator.next(), DATA, this.recievers);
		return null;
	}

	@Override
	public void remove() {
		this.documentIterator.remove();
	}

	private static void populate(final String id, final Map<String, Object> data, final Set<DataReciever> recievers) {
		for (final DataReciever reciever : recievers) {
			reciever.populate(id, data);
		}
	}

	private static Map<String, Object> complicatedMap() {
		return map("primary1", "Hello",
				"primary2", "there",
				"primary3", 5L,
				"kvArray", Arrays.asList(5.0, 2.4, 1.7),
				"tblArray", map("inner", Arrays.asList(map("value", true), map("value",false))));
	}

	private static Map<String, Object> map(final Object... kv) {
		if ((kv.length % 2) != 0) {
			throw new IllegalArgumentException();
		}
		final Map<String, Object> output = new HashMap<String, Object>();
		for (int n = 0; n < kv.length; n += 2) {
			output.put(String.valueOf(kv[n]), kv[n + 1]);
		}
		return output;
	}
}
