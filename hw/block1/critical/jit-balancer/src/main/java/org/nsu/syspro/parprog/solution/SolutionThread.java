package org.nsu.syspro.parprog.solution;

import org.nsu.syspro.parprog.UserThread;
import org.nsu.syspro.parprog.external.*;

import java.util.concurrent.ExecutorService;

public class SolutionThread extends UserThread {

    private static final CompilationData data = new CompilationData();
    private static final CompileObserver compileObserver = new CompileObserver(data);

    private final Uses uses = new Uses();

    public SolutionThread(int compilationThreadBound, ExecutionEngine exec, CompilationEngine compiler, Runnable r) {
        super(compilationThreadBound, exec, compiler, r);
    }

    /**
     * Checks which type of execution suits for given methods.
     * <ul>
     *   <li> If method is not yet compiled, then if it was interpreted enough times
     *   (more then {@link Uses#MAX_INTERPRET}) it would bew compiled by L1 compiler;
     *   <li>If method is compiled by L1 compiler then if it was executed enough times (more than {@link Uses#MAX_L1})
     *   then it would be compiled by L2 compiler.
     * </ul>
     *
     * @param id method for checks
     * @return Type of execution for given method
     */
    private ExecutionType checkMethod(MethodID id) {
        if (!uses.isInitialized(id)) {
            uses.initialize(id);
        }

        if (data.isCompiled(id)) {
            return dispatchForCompiledMethod(id);
        }

        return checkInterpret(id);
    }

    /**
     * Method checks which type of execution suits for given method.
     * If method is interpreted enough times (more then {@link Uses#MAX_INTERPRET})
     * then it will be compiled by L1 compiler in current thread.
     * Otherwise, it will be interpreted.
     *
     * @param id Method to check
     * @return Type of execution for given method
     */
    private ExecutionType checkInterpret(MethodID id) {
        uses.incrementFor(id);

        if (uses.needsCompilationL1(id)) {
            CompiledMethod compiledMethod = compiler.compile_l1(id);
            data.updateMethodInformation(id, compiledMethod, ExecutionType.EXECUTE_L1);
            uses.initialize(id);
            return ExecutionType.EXECUTE_L1;
        }
        return ExecutionType.INTERPRET;

    }

    /**
     * Methods checks which type of execution suits for given method.
     * IF method executed enough times on L1 optimisation (more then {@link Uses#MAX_L1}) then
     * it will be recompiled with L2 compiler in new {@link Thread}. But current execution will be done
     * with L1 optimisation.
     * <p>
     * Otherwise, method would be executed on L1 optimisation.
     *
     * @param id Method id
     * @return Type of execution for given method
     */
    private ExecutionType dispatchForCompiledMethod(MethodID id) {
        ExecutionType executionType = data.getExecutionType(id);

        if (executionType == ExecutionType.EXECUTE_L1) {
            uses.incrementFor(id);

            if (uses.needsCompilationL2(id)) {
                compileL2(id);
            }
            return ExecutionType.EXECUTE_L1;
        }
        return ExecutionType.EXECUTE_L2;
    }

    /**
     * Compiles given method with L2 optimisation in using {@link ExecutorService}.
     *
     * @param id Method to compile
     */
    private void compileL2(MethodID id) {
        compileObserver.submit(id, compiler);
    }

    @Override
    public ExecutionResult executeMethod(MethodID id) {
        ExecutionType type;
        CompiledMethod method;

        type = checkMethod(id);

        if (type == ExecutionType.INTERPRET) {
            return exec.interpret(id);
        }

        method = data.getCompiledMethod(id);
        assert(method != null);
        return exec.execute(method);
    }
}