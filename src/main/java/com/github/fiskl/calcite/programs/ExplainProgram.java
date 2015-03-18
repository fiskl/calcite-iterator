package com.github.fiskl.calcite.programs;

import java.io.PrintWriter;

import org.apache.calcite.plan.RelOptPlanner;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.externalize.RelWriterImpl;
import org.apache.calcite.sql.SqlExplainLevel;
import org.apache.calcite.tools.Program;

final class ExplainProgram implements Program {
	
	private final String tag;
	
	public ExplainProgram(String tag) {
		this.tag = tag;
	}

	@Override
	public RelNode run(RelOptPlanner planner, RelNode rel, RelTraitSet requiredOutputTraits) {
		System.out.println(tag + " [");
		rel.explain(new RelWriterImpl(new PrintWriter(System.out)) {
			@Override
			public SqlExplainLevel getDetailLevel() {
				return SqlExplainLevel.ALL_ATTRIBUTES;
			}
		});
		System.out.println("]");
		return rel;
	}
	
}