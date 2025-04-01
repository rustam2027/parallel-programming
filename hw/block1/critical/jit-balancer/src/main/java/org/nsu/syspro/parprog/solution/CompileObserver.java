package org.nsu.syspro.parprog.solution;

import org.nsu.syspro.parprog.external.CompilationEngine;
import org.nsu.syspro.parprog.external.CompiledMethod;
import org.nsu.syspro.parprog.external.MethodID;

import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CompileObserver {

    private final HashSet<MethodID> compiling = new HashSet<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final CompilationData data;

    CompileObserver(CompilationData data) {
        this.data = data;
    }

    /**
     * Submits a method for level 2 (L2) compilation if it is not already being compiled.
     * <p>
     * The compilation task runs asynchronously and, upon completion, updates the method
     * information with the compiled method and marks it as executable at L2.
     * </p>
     *
     * @param id       the identifier of the method to be compiled
     * @param compiler the compilation engine responsible for compiling the method
     */
    public void submit(MethodID id, CompilationEngine compiler) {
        Runnable task = () -> {
            CompiledMethod method = compiler.compile_l2(id);
            data.updateMethodInformation(id, method, ExecutionType.EXECUTE_L2);
            synchronized (compiling) {
                compiling.remove(id);
            }
        };

        synchronized (compiling) {
            if (compiling.contains(id)) {
                return;
            }

            compiling.add(id);
            executor.submit(task);
        }
    }
}
