package org.nrg.xnat.pogo

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import org.nrg.xnat.enums.PetMrProcessingSetting

import static org.nrg.xnat.pogo.SiteConfigProps.*

@JsonInclude(JsonInclude.Include.NON_NULL)
class SiteConfig {

    @JsonProperty(AUTOARCHIVE_IDLE_TIME)
    Integer sessionXmlRebuilderInterval

    @JsonProperty(AUTOARCHIVE_IDLE_SCHEDULE)
    Integer sessionXmlRebuilderSchedule

    @JsonProperty(ENABLE_SITEWIDE_ANONYMIZATION_SCRIPT)
    Boolean enableSiteAnonScript

    @JsonProperty(SITEWIDE_ANONYMIZATION_SCRIPT)
    String siteAnonScript

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

    @JsonProperty(INITIALIZED)
    Boolean initialized

    Map<String, Object> untrackedProperties = [:]

    @JsonAnyGetter
    Map<String, Object> getUntrackedProperties() {
        untrackedProperties
    }

    @JsonAnySetter
    void add(String key, Object value) {
        untrackedProperties.put(key, value);
    }

}
