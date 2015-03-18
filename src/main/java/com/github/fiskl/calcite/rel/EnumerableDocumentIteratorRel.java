package com.github.fiskl.calcite.rel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.calcite.adapter.enumerable.EnumerableRel;
import org.apache.calcite.adapter.enumerable.EnumerableRelImplementor;
import org.apache.calcite.adapter.enumerable.PhysType;
import org.apache.calcite.adapter.enumerable.PhysTypeImpl;
import org.apache.calcite.adapter.java.JavaTypeFactory;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.tree.BlockBuilder;
import org.apache.calcite.linq4j.tree.Expression;
import org.apache.calcite.linq4j.tree.Expressions;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelHomogeneousShuttle;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.SingleRel;

import com.github.fiskl.calcite.database.tables.DataReciever;

public class EnumerableDocumentIteratorRel extends SingleRel implements EnumerableRel {

	public EnumerableDocumentIteratorRel(final RelOptCluster cluster, final RelTraitSet traitSet, final RelNode input) {
		super(cluster, traitSet, input);
	}

	@Override
	public Result implement(final EnumerableRelImplementor implementor, final Prefer pref) {
		final JavaTypeFactory typeFactory = implementor.getTypeFactory();
		final BlockBuilder builder = new BlockBuilder();
		final EnumerableRel child = (EnumerableRel) getInput();
		final Result result = implementor.visitChild(this, 0, child, pref);

		final RecieverShuttle shuttle = new RecieverShuttle();

		child.accept(shuttle);

		final PhysType physType =
				PhysTypeImpl.of(typeFactory, getRowType(), pref.prefer(result.format));

	    final Expression inputEnumerable =
	            builder.append(
	                "inputEnumerable", result.block, false);

		builder.add(Expressions.return_(
				null,
				Expressions.call(
						implementor.stash(this, EnumerableDocumentIteratorRel.class),
						"callback",
						Expressions.list(
								Expressions.convert_(inputEnumerable, Enumerable.class),
								implementor.stash(shuttle.recievers, Set.class)))));

		return implementor.result(physType, builder.toBlock());
	}

	private static final class RecieverShuttle extends RelHomogeneousShuttle {

		private final Set<DataReciever> recievers = new HashSet<>();

		@Override
		public RelNode visit(final RelNode node) {
			final RelOptTable table = node.getTable();
			if (table != null) {
				final DataReciever reciever = table.unwrap(DataReciever.class);
				if (reciever != null) {
					this.recievers.add(reciever);
				}
			}
			return super.visit(node);
		}

	}

	@Override
	public RelNode copy(final RelTraitSet traitSet, final List<RelNode> inputs) {
		return new EnumerableDocumentIteratorRel(getCluster(), traitSet, sole(inputs));
	}

	/**
	 * This method is accessed from code generated in the method {@link #implement(EnumerableRelImplementor, org.apache.calcite.adapter.enumerable.EnumerableRel.Prefer)}
	 */
	public Enumerable<?> callback(final Enumerable<?> enumerable, final Set<DataReciever> recievers) {
		return new EnumerableDocumentIterator<>(enumerable, new DocumentIterator(recievers));
	}

}