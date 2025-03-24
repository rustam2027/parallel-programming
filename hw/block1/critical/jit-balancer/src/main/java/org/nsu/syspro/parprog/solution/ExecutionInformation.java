package org.nsu.syspro.parprog.solution;

public class ExecutionInformation {
    public final ExecutionType currentType;
    /**
     * Flag that shows if method is compiling now.
     */
    public final boolean isCompiling; // still DELETE?

    /**
     * Creates new instance of {@link ExecutionInformation}, with given {@link ExecutionInformation#isCompiling} flag.
     *
     * @param b isCompiling flag for new instance
     */
    public static ExecutionInformation create(ExecutionType executionType, boolean b) {
        return new ExecutionInformation(executionType, b);
    }

    /**
     * Create instance of {@link ExecutionInformation}, with false {@link ExecutionInformation#isCompiling} flag.
     */
    public static ExecutionInformation create(ExecutionType executionType) {
        return create(executionType, false);
    }

    private ExecutionInformation(ExecutionType executionType, boolean b) {
        currentType = executionType;
        isCompiling = b;
    }
}

