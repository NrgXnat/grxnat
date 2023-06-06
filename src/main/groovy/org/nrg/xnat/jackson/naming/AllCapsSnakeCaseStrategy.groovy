package org.nrg.xnat.jackson.naming

import com.fasterxml.jackson.databind.PropertyNamingStrategies

class AllCapsSnakeCaseStrategy extends PropertyNamingStrategies.SnakeCaseStrategy {

    @Override
    String translate(String input) {
        super.translate(input).toUpperCase()
    }

}
