package com.github.fiskl.calcite.programs;

import org.apache.calcite.plan.RelOptPlanner;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.tools.Program;

final class MutatorProgram implements Program {

	public MutatorProgram() {
	}

	@Override
	public RelNode run(final RelOptPlanner planner, final RelNode rel, final RelTraitSet requiredOutputTraits) {
		final MutatorShuttle insertingShuttle = new MutatorShuttle();
		return rel.accept(insertingShuttle);
	}

}