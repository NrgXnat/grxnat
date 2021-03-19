package org.nrg.xnat.subinterfaces

import org.nrg.xnat.pogo.Investigator

import static com.jayway.restassured.http.ContentType.JSON

class InvestigatorSubinterface extends XnatFunctionalitySubinterface {

    @Override
    List<String> getHandledEndpoints() {
        ['/xapi/investigators']
    }

    List<Investigator> readInvestigators() {
        queryBase().get(formatXapiUrl('investigators')).as(Investigator[].class) as List<Investigator>
    }

    void createInvestigators(List<Investigator> investigators) {
        final List<Investigator> knownInvestigators = readInvestigators()

        investigators.each { investigator ->
            final Investigator extant = knownInvestigators.find { it == investigator }
            if (extant == null) {
                createInvestigator(investigator)
            } else {
                investigator.id(extant.getXnatInvestigatordataId())
            }
        }
    }

    void createInvestigator(Investigator investigator) {
        investigator.setXnatInvestigatordataId(
                queryBase().contentType(JSON).body(investigator).post(formatXapiUrl('investigators')).jsonPath().getInt('xnatInvestigatordataId')
        )
    }

}
