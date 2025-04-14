package org.nsu.syspro.parprog.solution;

import org.nsu.syspro.parprog.external.MethodID;

import java.util.HashSet;

public class CompilingContext {
    private final HashSet<MethodID> compilingL1 = new HashSet<>();
    private final HashSet<MethodID> compilingL2 = new HashSet<>();

    public HashSet<MethodID> getCompilingL1() {
        return compilingL1;
    }

    public HashSet<MethodID> getCompilingL2() {
        return compilingL2;
    }
}
