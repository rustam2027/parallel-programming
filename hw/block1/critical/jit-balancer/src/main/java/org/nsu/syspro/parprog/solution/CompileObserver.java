package org.nsu.syspro.parprog.solution;

import org.nsu.syspro.parprog.external.CompilationEngine;
import org.nsu.syspro.parprog.external.CompiledMethod;
import org.nsu.syspro.parprog.external.MethodID;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CompileObserver {

    private final CompilingContext compilingContext = new CompilingContext();
    private final ExecutorService executor = Executors.newFixedThreadPool(3);
    private final CompilationData data = new CompilationData();

    /**
     * Submits a method for level 1 (L1) or level 2 (L2) compilation if it is not already being compiled.
     * <p>
     * The compilation task runs asynchronously and, upon completion, updates the method
     * information with the compiled method and marks it as executable at L1/L2.
     * </p>
     *
     * @param id       the identifier of the method to be compiled
     * @param compiler the compilation engine responsible for compiling the method
     */
    public void submit(MethodID id, CompilationEngine compiler, ExecutionType currentType) {
        Set<MethodID> compiling = currentType.getCompilingSet(compilingContext);

        Runnable task = () -> {
            try {
                CompiledMethod method = currentType.compile(id, compiler);
                data.updateMethodInformation(id, method, currentType.next());
                data.updateVersion();
            } finally {
                synchronized (compiling) {
                    compiling.remove(id);
                }
            }
        };

        synchronized (compiling) {
            if (compiling.contains(id)) {
                return;
            }
            compiling.add(id);
        }
        executor.submit(task);
    }

    public long getDataVersion() {
        return data.getVersion();
    }

    public CompilationData getData() {
        return data.copy();
    }
}
