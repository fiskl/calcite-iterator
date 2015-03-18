package com.github.fiskl.calcite.database.tables.extractors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.util.Pair;

import com.github.fiskl.calcite.database.tables.TableFieldType;
import com.github.fiskl.calcite.database.tables.TablePath;

public final class CollectionExtractorFactory {

	private static final String ID = "_id";

	private static final class PrimaryExtractor implements CollectionExtractor {

		private final List<TableFieldType> fields;
		private final ValuesExtractor extractor;

		PrimaryExtractor(final List<TableFieldType> fields) {
			this.fields = fields;
			this.extractor = new ValuesExtractor(fields);
		}

		@Override
		public Collection<Object[]> extractAll(
				final String id,
				final Map<String, Object> data,
				final int[] projects) {
			return Collections.singletonList(this.extractor.convert(
					Collections.<Object>singletonList(id), data, projects));
		}

		@Override
		public RelDataType getRowType(final RelDataTypeFactory typeFactory) {
			final List<String> names = new ArrayList<>();
			final List<RelDataType> types = new ArrayList<>();
			names.add(ID);
			types.add(typeFactory.createSqlType(SqlTypeName.VARCHAR));
			for (final TableFieldType field : this.fields) {
				names.add(field.getAlias());
				types.add(typeFactory.createSqlType(field.getSqlType()));
			}
			return typeFactory.createStructType(Pair.zip(names, types));
		}

	}

	private static final class SyntheticExtractor implements CollectionExtractor {

		private final List<TableFieldType> fields;
		private final ValuesExtractor extractor;
		private Navigator navigator;
		private int indexCount;

		public SyntheticExtractor(
				final TablePath path,
				final List<TableFieldType> fields) {
			this.fields = fields;
			this.indexCount = path.getPaths().length;
			this.extractor = new ValuesExtractor(fields);
			this.navigator = new NavigatorMapLeaf(path, new NavigatorMapLeaf.Callback() {

				@Override
				public Object[] convert(
						final List<Object> keys,
						final Map<String, Object> object,
						final int[] projects) {
					return SyntheticExtractor.this.extractor.convert(keys, object, projects);
				}

			});
		}

		@Override
		public RelDataType getRowType(final RelDataTypeFactory typeFactory) {
			final List<String> names = new ArrayList<>();
			final List<RelDataType> types = new ArrayList<>();
			names.add(ID);
			types.add(typeFactory.createSqlType(SqlTypeName.VARCHAR));
			for (int i = 0; i < this.indexCount; i++) {
				names.add(ID + i);
				types.add(typeFactory.createSqlType(SqlTypeName.INTEGER));
			}
			for (final TableFieldType field : this.fields) {
				names.add(field.getAlias());
				types.add(typeFactory.createSqlType(field.getSqlType()));
			}
			return typeFactory.createStructType(Pair.zip(names, types));
		}

		@Override
		public Collection<Object[]> extractAll(final String id, final Map<String, Object> data, final int[] projects) {
			return this.navigator.extractAll(id, data, projects);
		}

	}
	private static final class SyntheticKVExtractor implements CollectionExtractor {

		private final TableFieldType field;
		private Navigator navigator;
		private int indexCount;

		public SyntheticKVExtractor(
				final TablePath path,
				final TableFieldType field) {
			this.field = field;
			this.indexCount = path.getPaths().length;
			this.navigator = new ArrayNavigator(path, new ArrayNavigator.Callback() {

				@Override
				public Object[] convert(
						final List<Object> keys,
						final Object object,
						final int[] projects) {
					return ValuesExtractor.convert(keys, object, projects);
				}

			}, field.getPath());
		}

		@Override
		public RelDataType getRowType(final RelDataTypeFactory typeFactory) {
			final List<String> names = new ArrayList<>();
			final List<RelDataType> types = new ArrayList<>();
			names.add(ID);
			types.add(typeFactory.createSqlType(SqlTypeName.VARCHAR));
			for (int i = 0; i < this.indexCount + 1; i++) {
				names.add(ID + i);
				types.add(typeFactory.createSqlType(SqlTypeName.INTEGER));
			}
			names.add(this.field.getAlias());
			types.add(typeFactory.createSqlType(this.field.getSqlType()));
			return typeFactory.createStructType(Pair.zip(names, types));
		}

		@Override
		public Collection<Object[]> extractAll(final String id, final Map<String, Object> data, final int[] projects) {
			return this.navigator.extractAll(id, data, projects);
		}

	}

	public static CollectionExtractor create(final List<TableFieldType> fieldTypes) {
		return new PrimaryExtractor(fieldTypes);
	}

	public static CollectionExtractor create(final TablePath path, final List<TableFieldType> fieldTypes) {
		return new SyntheticExtractor(path, fieldTypes);
	}

	public static CollectionExtractor create(final TablePath path, final TableFieldType fieldType) {
		return new SyntheticKVExtractor(path, fieldType);
	}

	private CollectionExtractorFactory() {

	}

}
