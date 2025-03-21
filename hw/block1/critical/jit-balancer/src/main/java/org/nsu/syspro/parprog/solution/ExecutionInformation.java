package org.nsu.syspro.parprog.solution;

public class ExecutionInformation {
    public final ExecutionType currentType;
    public final boolean isCompiling; // DELETE ->

    public ExecutionInformation(ExecutionType executionType, boolean b) {
        currentType = executionType;
        isCompiling = b;
    }
}

