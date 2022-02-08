package org.nrg.xnat.pogo.events

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import groovy.transform.EqualsAndHashCode

@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy)
@EqualsAndHashCode
class Event {

    public static final String PROJECT_EVENT_TYPE = 'org.nrg.xnat.eventservice.events.ProjectEvent'
    public static final String SUBJECT_EVENT_TYPE = 'org.nrg.xnat.eventservice.events.SubjectEvent'
    public static final String SESSION_EVENT_TYPE = 'org.nrg.xnat.eventservice.events.SessionEvent'
    public static final String SUBJECT_ASSESSOR_TYPE = 'org.nrg.xnat.eventservice.events.SubjectAssessorEvent'
    public static final String PROJECT_ASSET_TYPE = 'org.nrg.xnat.eventservice.events.ProjectAssetEvent'
    public static final String IMAGE_ASSESSOR_TYPE = 'org.nrg.xnat.eventservice.events.ImageAssessorEvent'
    public static final String SCAN_EVENT_TYPE = 'org.nrg.xnat.eventservice.events.ScanEvent'
    public static final String SCHEDULED_EVENT = 'org.nrg.xnat.eventservice.events.ScheduledEvent'

    String type
    List<EventStatus> statuses = []
    String displayName
    String description
    List<EventScope> eventScope = []
    boolean isXsiType

}
