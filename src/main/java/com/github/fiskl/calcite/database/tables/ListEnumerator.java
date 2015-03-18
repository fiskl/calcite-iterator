package com.github.fiskl.calcite.database.tables;

import java.util.Collection;
import java.util.Iterator;

import org.apache.calcite.linq4j.Enumerator;

final class ListEnumerator implements Enumerator<Object[]> {

	private final Collection<Object[]> data;
	private Iterator<Object[]> iterator;
	private Object[] current;

	ListEnumerator(final Collection<Object[]> data) {
		this.data = data;
		this.iterator = data.iterator();
	}

	@Override
	public Object[] current() {
		return this.current;
	}

	@Override
	public boolean moveNext() {
		if (this.iterator.hasNext()) {
			this.current = this.iterator.next();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void reset() {
		this.iterator = this.data.iterator();
	}

	@Override
	public void close() {
		this.iterator = null;
	}

}