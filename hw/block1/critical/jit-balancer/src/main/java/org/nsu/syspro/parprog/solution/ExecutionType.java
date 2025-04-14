package org.nsu.syspro.parprog.solution;

import org.nsu.syspro.parprog.external.CompilationEngine;
import org.nsu.syspro.parprog.external.CompiledMethod;
import org.nsu.syspro.parprog.external.MethodID;

import java.util.Set;

public enum ExecutionType {
    INTERPRET {
        @Override
        public CompiledMethod compile(MethodID id, CompilationEngine compiler) {
            return compiler.compile_l1(id);
        }

        @Override
        public ExecutionType next() {
            return EXECUTE_L1;
        }

        @Override
        public Set<MethodID> getCompilingSet(CompilingContext context) {
            return context.getCompilingL1();
        }
    },
    EXECUTE_L1 {
        @Override
        public CompiledMethod compile(MethodID id, CompilationEngine compiler) {
            return compiler.compile_l2(id);
        }

        @Override
        public ExecutionType next() {
            return EXECUTE_L2;
        }

        @Override
        public Set<MethodID> getCompilingSet(CompilingContext context) {
            return context.getCompilingL2();
        }
    },
    EXECUTE_L2 {
        @Override
        public CompiledMethod compile(MethodID id, CompilationEngine compiler) {
            throw new UnsupportedOperationException("EXECUTE_L2 does not support compilation.");
        }

        @Override
        public ExecutionType next() {
            throw new UnsupportedOperationException("EXECUTE_L2 does not support next.");
        }

        @Override
        public Set<MethodID> getCompilingSet(CompilingContext context) {
            throw new UnsupportedOperationException("EXECUTE_L2 does not use CompilingContext.");
        }
    };

    public abstract CompiledMethod compile(MethodID id, CompilationEngine compiler);
    public abstract ExecutionType next();
    public abstract Set<MethodID> getCompilingSet(CompilingContext context);
}