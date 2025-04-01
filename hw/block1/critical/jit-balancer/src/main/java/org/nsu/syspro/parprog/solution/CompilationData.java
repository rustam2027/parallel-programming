package org.nsu.syspro.parprog.solution;

import org.nsu.syspro.parprog.external.CompiledMethod;
import org.nsu.syspro.parprog.external.MethodID;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CompilationData {
    private final HashMap<MethodID, CompiledMethod> compiledMethods = new HashMap<>();
    private final HashMap<MethodID, ExecutionType> executionType = new HashMap<>();

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Retrieves the compiled method associated with the given method identifier.
     *
     * @param id the identifier of the method
     * @return the compiled method if it exists, or {@code null} if the method has not been compiled
     */
    public CompiledMethod getCompiledMethod(MethodID id) {
        try {
            lock.readLock().lock();
            return compiledMethods.get(id);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Retrieves the execution type of the given method.
     *
     * @param id the identifier of the method
     * @return the execution type of the method, or {@code null} if it has not been recorded
     */
    public ExecutionType getExecutionType(MethodID id) {
        try {
            lock.readLock().lock();
            return executionType.get(id);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Updates the compiled method and execution type for the given method identifier.
     *
     * @param id     the identifier of the method
     * @param method the compiled method instance
     * @param type   the execution type to be associated with the method
     */
    public void updateMethodInformation(MethodID id, CompiledMethod method, ExecutionType type) {
        try {
            lock.writeLock().lock();
            compiledMethods.put(id, method);
            executionType.put(id, type);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Checks if the given method has been compiled.
     *
     * @param id the identifier of the method
     * @return {@code true} if the method has been compiled, {@code false} otherwise
     */
    public boolean isCompiled(MethodID id) {
        try {
            lock.readLock().lock();
            assert(executionType.containsKey(id) || !compiledMethods.containsKey(id));
            return executionType.containsKey(id) && compiledMethods.containsKey(id);
        } finally {
            lock.readLock().unlock();
        }
    }
}
