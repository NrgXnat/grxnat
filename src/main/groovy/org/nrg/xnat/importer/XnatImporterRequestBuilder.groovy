package org.nrg.xnat.importer

import org.nrg.xnat.enums.ImportHandler
import org.nrg.xnat.enums.MergeBehavior

class XnatImporterRequestBuilder {

    private ImportHandler importHandler
    private String customImportHandler
    private File file
    private String dest
    private Boolean triggerPipelines
    private MergeBehavior mergeBehavior
    private String httpSessionListener
    private String project
    private String subject
    private String session
    // what about all of the params for inbox?


}
