package org.nrg.xnat.pogo.plugins

import java.util.function.Function

class DerivationWithFallback implements Function<String, String> {
    
    List<Function<String, String>> functions
    
    DerivationWithFallback(List<Function<String, String>> functions) {
        this.functions = functions
    }

    @Override
    String apply(String version) {
        for (Function<String, String> function : functions) {
            try {
                return function.apply(version)
            } catch (Exception ignored) {}
        }
        throw new RuntimeException("Could not find a proper artifact from provided version ${version}")
    }
    
}
