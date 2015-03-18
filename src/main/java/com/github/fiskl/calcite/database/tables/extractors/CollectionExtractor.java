package com.github.fiskl.calcite.database.tables.extractors;

import java.util.Collection;
import java.util.Map;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;

public interface CollectionExtractor {

	RelDataType getRowType(RelDataTypeFactory typeFactory);

	Collection<Object[]> extractAll(String id, Map<String, Object> data, int[] projects);

}
