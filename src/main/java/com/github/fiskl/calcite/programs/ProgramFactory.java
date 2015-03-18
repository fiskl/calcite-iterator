package com.github.fiskl.calcite.programs;

import org.apache.calcite.tools.Program;
import org.apache.calcite.tools.Programs;

public final class ProgramFactory {
	
	public static Program create() {
		return Programs.sequence(
				new ExplainProgram("PRE (optimization)"),
				Programs.standard(),
				new ExplainProgram("POST (optimization)"),
				new MutatorProgram(),
				new ExplainProgram("FINAL"));
	}
	
	private ProgramFactory() {
	}

}