package com.github.fiskl.calcite.database.tables;

import java.util.List;
import java.util.Map;

import org.apache.calcite.DataContext;
import org.apache.calcite.linq4j.AbstractEnumerable;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Enumerator;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.schema.ProjectableFilterableTable;
import org.apache.calcite.schema.impl.AbstractTable;

import com.github.fiskl.calcite.database.tables.extractors.CollectionExtractor;
import com.github.fiskl.calcite.database.tables.extractors.CollectionExtractorFactory;

public class SyntheticTable extends AbstractTable implements ProjectableFilterableTable, DataReciever {
	private final CollectionExtractor extractor;
	private String id;
	private Map<String, Object> data;

	public SyntheticTable(final TablePath path, final List<TableFieldType> fieldTypes) {
		this.extractor = CollectionExtractorFactory.create(path, fieldTypes);
	}

	@Override
	public RelDataType getRowType(final RelDataTypeFactory typeFactory) {
		return this.extractor.getRowType(typeFactory);
	}

	@Override
	public void populate(final String id, final Map<String, Object> data) {
		this.id = id;
		this.data = data;
	}

	@Override
	public Enumerable<Object[]> scan(
			final DataContext root,
			final List<RexNode> filters,
			final int[] projects) {
		return new AbstractEnumerable<Object[]>() {
			@Override
			public Enumerator<Object[]> enumerator() {
				return new ListEnumerator(SyntheticTable.this.extractor.extractAll(
						SyntheticTable.this.id,
						SyntheticTable.this.data,
						projects));
			}
		};
	}

}