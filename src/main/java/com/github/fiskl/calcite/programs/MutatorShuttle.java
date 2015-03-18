package com.github.fiskl.calcite.programs;

import java.util.List;

import org.apache.calcite.adapter.enumerable.EnumerableConvention;
import org.apache.calcite.rel.RelHomogeneousShuttle;
import org.apache.calcite.rel.RelNode;

import com.github.fiskl.calcite.rel.EnumerableDocumentIteratorRel;

/**
 * The {@link MutatorShuttle} finds the first join in the plan, or the last
 * enumerable node, and replaces it with a document iterator.
 */
final class MutatorShuttle extends RelHomogeneousShuttle {

	@Override
	public RelNode visit(final RelNode other) {
		final List<RelNode> inputs = other.getInputs();
		if (inputs.size() != 1) {
			return new EnumerableDocumentIteratorRel(other.getCluster(),
					other.getTraitSet(), other);
		}

		final RelNode relNode = inputs.get(0);
		if (relNode.getConvention() != EnumerableConvention.INSTANCE) {
			return new EnumerableDocumentIteratorRel(other.getCluster(),
					other.getTraitSet(), other);
		}

		return super.visit(other);
	}

}