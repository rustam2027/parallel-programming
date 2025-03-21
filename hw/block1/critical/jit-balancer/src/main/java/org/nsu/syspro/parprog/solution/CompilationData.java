package org.nsu.syspro.parprog.solution;

import org.nsu.syspro.parprog.external.CompiledMethod;
import org.nsu.syspro.parprog.external.MethodID;

import java.util.HashMap;

public class CompilationData { // Using ReadWriteLock
    public final HashMap<MethodID, Integer> uses = new HashMap<>(); // Global!! -> ThreadLocal | delete global Counter
    public final HashMap<MethodID, CompiledMethod> compiledMethods = new HashMap<>();
    public final HashMap<MethodID, ExecutionInformation> executionType = new HashMap<>();
}
