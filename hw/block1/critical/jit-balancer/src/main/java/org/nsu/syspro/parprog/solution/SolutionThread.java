package org.nsu.syspro.parprog.solution;

import org.nsu.syspro.parprog.UserThread;
import org.nsu.syspro.parprog.external.*;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SolutionThread extends UserThread {

    private static final int MAX_INTERPRET = 20;
    private static final int MAX_L1 = 900;

    private static final CompilationData data = new CompilationData();
    private final HashMap<MethodID, Integer> uses = new HashMap<>();
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    public SolutionThread(int compilationThreadBound, ExecutionEngine exec, CompilationEngine compiler, Runnable r) {
        super(compilationThreadBound, exec, compiler, r);
    }

    /**
     * Checks which type of execution suits for given methods.
     * <ul>
     *   <li> If method is not yet compiled, then if it was interpreted enough times
     *   (more then {@link SolutionThread#MAX_INTERPRET}) it would bew compiled by L1 compiler;
     *   <li>If method is compiled by L1 compiler then if it was executed enough times (more than {@link SolutionThread#MAX_L1})
     *   then it would be compiled by L2 compiler.
     * </ul>
     *
     * @param id method for checks
     * @return Type of execution for given method
     */
    private ExecutionType checkMethod(MethodID id) {
        if (!uses.containsKey(id)) {
            uses.put(id, 0);
        }

        if (data.containsInformation(id)) {
            return checkCompiled(id);
        }

        return checkInterpret(id);
    }

    /**
     * Method checks which type of execution suits for given method.
     * If method is interpreted enough times (more then {@link SolutionThread#MAX_INTERPRET})
     * then it will be compiled by L1 compiler in current thread.
     * Otherwise, it will be interpreted.
     *
     * @param id Method to check
     * @return Type of execution for given method
     */
    private ExecutionType checkInterpret(MethodID id) {
        int currentUses = uses.get(id) + 1;
        uses.put(id, currentUses);

        if (currentUses > MAX_INTERPRET) {
            CompiledMethod compiledMethod = compiler.compile_l1(id);
            data.updateMethodInformation(id, compiledMethod, ExecutionInformation.create(ExecutionType.EXECUTE_L1));
            uses.put(id, 0);
            return ExecutionType.EXECUTE_L1;
        }
        return ExecutionType.INTERPRET;

    }

    /**
     * Methods checks which type of execution suits for given method.
     * IF method executed enough times on L1 optimisation (more then {@link SolutionThread#MAX_L1}) then
     * it will be recompiled with L2 compiler in new {@link Thread}. But current execution will be done
     * with L1 optimisation.
     * <p>
     * Otherwise, method would be executed on L1 optimisation.
     *
     * @param id Method id
     * @return Type of execution for given method
     */
    private ExecutionType checkCompiled(MethodID id) {
        ExecutionInformation methodInformation = data.getExecutionInformation(id);
        if (methodInformation.currentType == ExecutionType.EXECUTE_L1) {
            int currentUses = uses.get(id) + 1;
            uses.put(id, currentUses);
            if (currentUses > MAX_L1 && !methodInformation.isCompiling) {
                data.putExecutionInformation(id, ExecutionInformation.create(ExecutionType.EXECUTE_L1, true));
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
        Runnable task = () -> {
            CompiledMethod compiledMethod = compiler.compile_l2(id);
            data.updateMethodInformation(id, compiledMethod, ExecutionInformation.create(ExecutionType.EXECUTE_L2));
        };

        executor.submit(task);
    }

    @Override
    public ExecutionResult executeMethod(MethodID id) {
        ExecutionType type;
        CompiledMethod method;

        type = checkMethod(id);
        method = data.getCompiledMethod(id);

        if (type == ExecutionType.INTERPRET) {
            return exec.interpret(id);
        }

        return exec.execute(method);
    }
}