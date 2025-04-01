package org.nsu.syspro.parprog.solution;

import org.nsu.syspro.parprog.external.MethodID;

import java.util.HashMap;

public class Uses {

    static final int MAX_INTERPRET = 20;
    static final int MAX_L1 = 900;

    private final HashMap<MethodID, Integer> uses = new HashMap<>();

    /**
     * Initialize uses information for method with given ID.
     * Can be used for nullify information.
     */
    public void initialize(MethodID id) {
        uses.put(id, 0);
    }

    /**
     * Checks if the given method has been initialized.
     *
     * @param id the identifier of the method
     * @return {@code true} if the method has been initialized, {@code false} otherwise
     */
    public boolean isInitialized(MethodID id) {
        return uses.containsKey(id);
    }

    /**
     * Increments the usage count for the given method.
     *
     * @param id the identifier of the method
     * @throws NullPointerException if the method ID is not already initialized in the map
     */
    public void incrementFor(MethodID id) {
        uses.put(id, uses.get(id) + 1);
    }

    /**
     * Determines whether the given method requires level 1 (L1) compilation.
     *
     * @param id the identifier of the method
     * @return {@code true} if the method's usage count exceeds the L1 compilation threshold, {@code false} otherwise
     * @throws NullPointerException if the method ID is not initialized in the map
     */
    public boolean needsCompilationL1(MethodID id) {
        return uses.get(id) > MAX_INTERPRET;
    }

    /**
     * Determines whether the given method requires level 2 (L2) compilation.
     *
     * @param id the identifier of the method
     * @return {@code true} if the method's usage count exceeds the L2 compilation threshold, {@code false} otherwise
     * @throws NullPointerException if the method ID is not initialized in the map
     */
    public boolean needsCompilationL2(MethodID id) {
        return uses.get(id) > MAX_L1;
    }
}