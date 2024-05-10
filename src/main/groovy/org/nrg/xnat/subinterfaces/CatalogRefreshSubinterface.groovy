package org.nrg.xnat.subinterfaces

import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.pogo.experiments.Scan
import org.nrg.xnat.pogo.experiments.SubjectAssessor

class CatalogRefreshSubinterface extends CoreXnatFunctionalitySubinterface {

    @Override
    List<String> getHandledEndpoints() {
        ['/services/refresh/catalog']
    }

    XnatInterface refreshCatalog(SubjectAssessor subjectAssessor) {
        refreshCatalogForResource(subjectAssessor.uri)
    }

    XnatInterface refreshCatalog(Scan scan) {
        refreshCatalogForResource(scan.uri)
    }

    protected XnatInterface refreshCatalogForResource(String resource) {
        queryBase().formParam('options', 'populateStats,append,delete,checksum').formParam('resource', resource)
                .post(formatRestUrl('/services/refresh/catalog')).then().assertThat().statusCode(200)
        xnatInterface
    }

}
