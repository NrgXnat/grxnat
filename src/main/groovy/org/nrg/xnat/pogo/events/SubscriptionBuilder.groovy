package org.nrg.xnat.pogo.events

import org.nrg.xnat.pogo.Project
import org.nrg.xnat.util.RandomUtils

class SubscriptionBuilder {

    private String name = ''
    private boolean active = true
    private String actionKey
    private Map<String, Object> attributes
    private boolean actAsEventUser = false
    private String eventType
    private List<String> projects
    private EventStatus status
    private String payloadFilter

    SubscriptionBuilder name(String name) {
        this.name = name
        this
    }

    SubscriptionBuilder inactive() {
        active = false
        this
    }

    SubscriptionBuilder actionKey(String actionKey) {
        this.actionKey = actionKey
        this
    }

    SubscriptionBuilder actionKey(Action action) {
        actionKey(action.actionKey)
    }

    SubscriptionBuilder attributes(Map<String, Object> attributes) {
        this.attributes = attributes
        this
    }

    SubscriptionBuilder actAsEventUser() {
        actAsEventUser = true
        this
    }

    SubscriptionBuilder event(String eventType, EventStatus status) {
        this.eventType = eventType
        this.status = status
        this
    }

    SubscriptionBuilder event(Event event, EventStatus status) {
        event(event.type, status)
    }

    SubscriptionBuilder projectIds(Collection<String> projectIds) {
        projects = projectIds
        this
    }

    SubscriptionBuilder projects(List<Project> projects) {
        projectIds(projects*.id)
    }

    SubscriptionBuilder forProject(Project project) {
        projects([project])
    }

    SubscriptionBuilder filter(String filter) {
        payloadFilter = filter
        this
    }

    Subscription build() {
        if (actionKey == null) {
            throw new UnsupportedOperationException('actionKey must be provided')
        }
        if (eventType == null) {
            throw new UnsupportedOperationException('eventType must be provided')
        }
        if (status == null) {
            throw new UnsupportedOperationException('status must be provided')
        }
        new Subscription(
                name: name ?: RandomUtils.randomID(),
                active: active,
                actionKey: actionKey,
                attributes: attributes,
                actAsEventUser: actAsEventUser,
                eventFilter: new EventFilter(
                        eventType: eventType,
                        projectIds: projects,
                        status: status,
                        payloadFilter: payloadFilter
                )
        )
    }

}
