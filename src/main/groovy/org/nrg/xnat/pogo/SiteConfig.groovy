package org.nrg.xnat.pogo

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

import static org.nrg.xnat.pogo.SiteConfigProps.*

@JsonInclude(JsonInclude.Include.NON_NULL)
class SiteConfig implements ArbitraryPropertySupport {

    @JsonProperty(AUTOARCHIVE_IDLE_TIME)
    Integer sessionXmlRebuilderInterval

    @JsonProperty(AUTOARCHIVE_IDLE_SCHEDULE)
    Integer sessionXmlRebuilderSchedule

    @JsonProperty(DEFAULT_IMPORTER)
    String uiDefaultCompressedUploaderImporter

    @JsonProperty(ENABLE_SITEWIDE_ANONYMIZATION_SCRIPT)
    Boolean enableSiteAnonScript

    @JsonProperty(SITEWIDE_ANONYMIZATION_SCRIPT)
    String siteAnonScript

    @JsonProperty(SITE_SERIES_IMPORT_FILTER_ENABLED)
    Boolean enableSitewideSeriesImportFilter

    @JsonProperty(SITE_SERIES_IMPORT_FILTER_MODE)
    String sitewideSeriesImportFilterMode

    @JsonProperty(SITE_SERIES_IMPORT_FILTER_BODY)
    String sitewideSeriesImportFilter

    @JsonProperty(PET_MR_SETTING)
    String petMrSetting

    @JsonProperty(LOGIN_REQUIRED)
    Boolean requireLogin

    @JsonProperty(AUTO_ENABLE)
    Boolean autoEnableUsers

    @JsonProperty(REQUIRE_EMAIL_VERIFICATION)
    Boolean requireEmailVerification

    @JsonProperty(ALLOW_NON_ADMIN_PROJECT_CREATION)
    Boolean allowNonadminProjectCreation

    @JsonProperty(RESTRICT_USER_LIST_TO_ADMIN)
    Boolean restrictUserListToAdmins

    @JsonProperty(PASSWORD_COMPLEXITY)
    String passwordComplexityRegex

    @JsonProperty(PASSWORD_COMPLEXITY_MESSAGE)
    String passwordComplexityMessage

    @JsonProperty(SESSION_TIMEOUT)
    String sessionTimeout

    @JsonProperty(PREVENT_CROSS_MODALITY_MERGE)
    Boolean preventCrossModalityMerge

    @JsonProperty(USE_SOP_INSTANCE_UID_TO_UNIQUELY_ID_DICOM)
    Boolean useSopInstanceUidToUniquelyIdentifyDicom

    @JsonProperty(DICOM_FILE_NAME_TEMPLATE)
    String dicomFileNameTemplate

    @JsonProperty(BACKUP_DELETED_TO_CACHE)
    Boolean backupDeletedToCache

    @JsonProperty(MAINTAIN_FILE_HISTORY)
    Boolean maintainFileHistory

    @JsonProperty(INITIALIZED)
    Boolean initialized

    @JsonProperty(POST_ARCHIVE_PROJECT_ANON)
    String rerunProjectAnonOnRename

}
