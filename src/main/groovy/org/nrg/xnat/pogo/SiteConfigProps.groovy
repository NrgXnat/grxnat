package org.nrg.xnat.pogo

class SiteConfigProps {

    public static final String AUTOARCHIVE_IDLE_TIME = 'sessionXmlRebuilderInterval'
    public static final String AUTOARCHIVE_IDLE_SCHEDULE = 'sessionXmlRebuilderRepeat'
    public static final String ENABLE_SITEWIDE_ANONYMIZATION_SCRIPT = 'enableSitewideAnonymizationScript'
    public static final String SITEWIDE_ANONYMIZATION_SCRIPT = 'sitewideAnonymizationScript'
    public static final String SITE_SERIES_IMPORT_FILTER_ENABLED = 'enableSitewideSeriesImportFilter'
    public static final String SITE_SERIES_IMPORT_FILTER_MODE = 'sitewideSeriesImportFilterMode'
    public static final String SITE_SERIES_IMPORT_FILTER_BODY = 'sitewideSeriesImportFilter'
    public static final String PET_MR_SETTING = 'sitewidePetMr'
    public static final String LOGIN_REQUIRED = 'requireLogin'
    public static final String AUTO_ENABLE = 'userRegistration'
    public static final String REQUIRE_EMAIL_VERIFICATION = 'emailVerification'
    public static final String ALLOW_NON_ADMIN_PROJECT_CREATION = 'uiAllowNonAdminProjectCreation'
    public static final String RESTRICT_USER_LIST_TO_ADMIN = 'restrictUserListAccessToAdmins'
    public static final String PASSWORD_COMPLEXITY = 'passwordComplexity'
    public static final String PASSWORD_COMPLEXITY_MESSAGE = 'passwordComplexityMessage'
    public static final String SESSION_TIMEOUT = 'sessionTimeout'
    public static final String PREVENT_CROSS_MODALITY_MERGE = 'preventCrossModalityMerge'
    public static final String USE_SOP_INSTANCE_UID_TO_UNIQUELY_ID_DICOM = 'useSopInstanceUidToUniquelyIdentifyDicom'
    public static final String DICOM_FILE_NAME_TEMPLATE = 'dicomFileNameTemplate'
    public static final String INITIALIZED = 'initialized'
    public static final String BACKUP_DELETED_TO_CACHE = 'backupDeletedToCache'
    public static final String MAINTAIN_FILE_HISTORY = 'maintainFileHistory'

}
