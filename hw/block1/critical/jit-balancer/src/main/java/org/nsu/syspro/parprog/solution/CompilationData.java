package org.nsu.syspro.parprog.solution;

import org.nsu.syspro.parprog.external.CompiledMethod;
import org.nsu.syspro.parprog.external.MethodID;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CompilationData {
    private final HashMap<MethodID, CompiledMethod> compiledMethods = new HashMap<>();
    private final HashMap<MethodID, ExecutionInformation> executionType = new HashMap<>();

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public CompiledMethod getCompiledMethod(MethodID id) {
        try {
            lock.readLock().lock();
            return compiledMethods.get(id);
        } finally {
            lock.readLock().unlock();
        }
    }

    public ExecutionInformation getExecutionInformation(MethodID id) {
        try {
            lock.readLock().lock();
            return executionType.get(id);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void updateMethodInformation(MethodID id, CompiledMethod method, ExecutionInformation information) {
        try {
            lock.writeLock().lock();
            compiledMethods.put(id, method);
            executionType.put(id, information);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean containsInformation(MethodID id) {
        try {
            lock.readLock().lock();
            return executionType.containsKey(id) && compiledMethods.containsKey(id);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void putExecutionInformation(MethodID id, ExecutionInformation information) {
        try {
            lock.writeLock().lock();
            executionType.put(id, information);
        } finally {
            lock.writeLock().unlock();
        }
    }
}
