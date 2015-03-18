package com.github.fiskl.calcite.rel;

import java.util.Iterator;

import org.apache.calcite.linq4j.AbstractEnumerable;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Enumerator;

public final class EnumerableDocumentIterator<T> extends AbstractEnumerable<T> {

	private final EnumerableSnowflakeEnumerator<T> enumerator;

	public EnumerableDocumentIterator(final Enumerable<T> input, final Iterator<Void> documentIterator) {
		this.enumerator = new EnumerableSnowflakeEnumerator<>(input, documentIterator);
	}

	@Override
	public Enumerator<T> enumerator() {
		return this.enumerator;
	}

	static final class EnumerableSnowflakeEnumerator<T> implements Enumerator<T> {

		private final Enumerable<T> delegate;

		private final Iterator<Void> documentIterator;

		private Enumerator<T> currentEnumerable;

		private T current;

		public EnumerableSnowflakeEnumerator(
				final Enumerable<T> delegate,
				final Iterator<Void> documentIterator) {
			this.documentIterator = documentIterator;
			this.delegate = delegate;
		}

		@Override
		public T current() {
			return this.current;
		}

		@Override
		public boolean moveNext() {
			// If we have an enumerable, and there is another element, then set it to be the current
			if (this.currentEnumerable != null && this.currentEnumerable.moveNext()) {
				this.current = this.currentEnumerable.current();
				return true;
			}

			// At this point we either do not have an enumerable, or it has completed, try and obtain the next
			// enumerable over the next document
			while (this.documentIterator.hasNext()) {
				this.documentIterator.next();
				// This would trigger a reload and thus a reloaded enumerator
				this.currentEnumerable = this.delegate.enumerator();

				if (this.currentEnumerable.moveNext()) {
					this.current = this.currentEnumerable.current();
					return true;
				}
			}

			// No more documents, and all enumerable instances have been exhausted
			return false;
		}


		@Override
		public void reset() {

		}

		@Override
		public void close() {

		}

	}

}