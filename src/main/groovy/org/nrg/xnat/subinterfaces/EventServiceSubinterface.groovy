package org.nrg.xnat.subinterfaces

import io.restassured.http.ContentType
import io.restassured.response.Response
import org.apache.commons.lang3.time.StopWatch
import org.nrg.testing.TimeUtils
import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.pogo.paginated_api.PaginatedRequest
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.events.Action
import org.nrg.xnat.pogo.events.DeliveredEvent
import org.nrg.xnat.pogo.events.Event
import org.nrg.xnat.pogo.events.EventStepStatus
import org.nrg.xnat.pogo.events.Subscription
import org.nrg.xnat.rest.SerializationUtils

class EventServiceSubinterface extends CoreXnatFunctionalitySubinterface {

    @Override
    List<String> getHandledEndpoints() {
        [
                '/xapi/events/allactions',
                '/xapi/events/delivered',
                '/xapi/events/delivered/{id}',
                '/xapi/events/events',
                '/xapi/events/prefs',
                '/xapi/events/subscription',
                '/xapi/events/subscriptions',
                '/xapi/events/subscription/{id}',
                '/xapi/events/subscription/{id}/activate',
                '/xapi/events/subscription/{id}/deactivate',
                '/xapi/projects/{project}/events/actionsbyevent',
                '/xapi/projects/{project}/events/delivered',
                '/xapi/projects/{project}/events/delivered/{id}',
                '/xapi/projects/{project}/events/events',
                '/xapi/projects/{project}/events/subscription',
                '/xapi/projects/{project}/events/subscriptions',
                '/xapi/projects/{project}/events/subscription/{id}',
                '/xapi/projects/{project}/events/subscription/{id}/activate',
                '/xapi/projects/{project}/events/subscription/{id}/deactivate'
        ]
    }

    boolean readEventServiceEnabled() {
        queryBase().get(formatXapiUrl('/events/prefs')).jsonPath().getBoolean('enabled')
    }

    XnatInterface setEventServiceStatus(boolean enable) {
        queryBase().contentType(ContentType.JSON).body(['enabled': enable]).put(formatXapiUrl('/events/prefs')).then().assertThat().statusCode(200)
        xnatInterface
    }

    XnatInterface enableEventService() {
        setEventServiceStatus(true)
    }

    XnatInterface disableEventService() {
        setEventServiceStatus(false)
    }

    List<Event> readAvailableSiteEvents() {
        readEventsAtUrl('/events/events')
    }

    List<Event> readAvailableProjectEvents(Project project) {
        readEventsAtUrl("/projects/${project}/events/events")
    }

    List<Action> readAllActions() {
        readActionsFromResponse(queryBase().get(formatXapiUrl('/events/allactions')))
    }

    List<Action> readSiteActionsForEvent(String eventType) {
        readActionsFromResponse(queryBase().queryParam('event-type', eventType).get(formatXapiUrl('/events/actionsbyevent')))
    }

    List<Action> readSiteActionsForEvent(Event event) {
        readSiteActionsForEvent(event.type)
    }

    List<Action> readProjectActionsForEvent(Project project, String eventType) {
        readActionsFromResponse(queryBase().queryParam('event-type', eventType).get(formatXapiUrl("/projects/${project}/events/actionsbyevent")))
    }

    List<Action> readProjectActionsForEvent(Project project, Event event) {
        readProjectActionsForEvent(project, event.type)
    }

    List<Subscription> readSubscriptions() {
        readSubscriptionsAtUrl('/events/subscriptions')
    }

    List<Subscription> readProjectSubscriptions(Project project) {
        readSubscriptionsAtUrl("/projects/${project}/events/subscriptions")
    }

    Subscription readSubscription(int id) {
        queryBase().get(formatXapiUrl("/events/subscription/${id}")).as(Subscription)
    }

    Subscription readProjectSubscription(Project project, int id) {
        queryBase().get(formatXapiUrl("/projects/${project}/events/subscription/${id}")).as(Subscription)
    }

    XnatInterface createSubscription(Subscription subscription) {
        createSubscriptionAtUrl('/events/subscription', subscription)
    }

    XnatInterface createProjectSubscription(Project project, Subscription subscription) {
        createSubscriptionAtUrl("/projects/${project}/events/subscription", subscription)
    }

    XnatInterface updateSubscription(Subscription subscription) {
        updateSubscriptionAtUrl("/events/subscription/${subscription.id}", subscription)
    }

    XnatInterface updateProjectSubscription(Project project, Subscription subscription) {
        updateSubscriptionAtUrl("/projects/${project}/events/subscription/${subscription.id}", subscription)
    }

    XnatInterface deleteSubscription(Subscription subscription) {
        queryBase().delete(formatXapiUrl("/events/subscription/${subscription.id}")).then().assertThat().statusCode(204)
        xnatInterface
    }

    XnatInterface deleteProjectSubscription(Project project, Subscription subscription) {
        queryBase().delete(formatXapiUrl("/projects/${project}/events/subscription/${subscription.id}")).then().assertThat().statusCode(204)
        xnatInterface
    }

    XnatInterface activateSubscription(Subscription subscription) {
        activateSubscriptionAtUrl("/events/subscription/${subscription.id}/activate", subscription)
    }

    XnatInterface activateProjectSubscription(Project project, Subscription subscription) {
        activateSubscriptionAtUrl("/projects/${project}/events/subscription/${subscription.id}/activate", subscription)
    }

    XnatInterface deactivateSubscription(Subscription subscription) {
        deactivateSubscriptionAtUrl("/events/subscription/${subscription.id}/deactivate", subscription)
    }

    XnatInterface deactivateProjectSubscription(Project project, Subscription subscription) {
        deactivateSubscriptionAtUrl("/projects/${project}/events/subscription/${subscription.id}/deactivate", subscription)
    }

    List<DeliveredEvent> queryDeliveredEvents(PaginatedRequest request = new PaginatedRequest(), Integer expectedNumEvents = null, boolean waitForEvents = true) {
        queryDeliveredEventsAtUrl('/events/delivered', request, expectedNumEvents, waitForEvents)
    }

    List<DeliveredEvent> queryProjectDeliveredEvents(Project project, PaginatedRequest request = new PaginatedRequest(), Integer expectedNumEvents = null, boolean waitForEvents = true) {
        queryDeliveredEventsAtUrl("/projects/${project}/events/delivered", request, expectedNumEvents, waitForEvents)
    }

    DeliveredEvent readDeliveredEvent(long id) {
        readDeliveredEventAtUrl("/events/delivered/${id}")
    }

    DeliveredEvent readProjectDeliveredEvent(Project project, long id) {
        readDeliveredEventAtUrl("/projects/${project}/events/delivered/${id}")
    }

    private List<Event> readEventsAtUrl(String subUrl) {
        SerializationUtils.deserializeList(
                queryBase().get(formatXapiUrl(subUrl)).then().assertThat().statusCode(200).and().extract().as(List),
                Event
        )
    }

    private List<Action> readActionsFromResponse(Response response) {
        SerializationUtils.deserializeList(
                response.then().assertThat().statusCode(200).and().extract().as(List),
                Action
        )
    }

    private List<Subscription> readSubscriptionsAtUrl(String subUrl) {
        SerializationUtils.deserializeList(
                queryBase().get(formatXapiUrl(subUrl)).then().assertThat().statusCode(200).and().extract().as(List),
                Subscription
        )
    }

    private XnatInterface createSubscriptionAtUrl(String subUrl, Subscription subscription) {
        final List<String> parsedResponse = queryBase().contentType(ContentType.JSON).body(subscription).post(formatXapiUrl(subUrl)).
                then().assertThat().statusCode(201).and().extract().asString().split(':')
        subscription.setName(parsedResponse[0])
        subscription.setId(Integer.parseInt(parsedResponse[1]))
        xnatInterface
    }

    private XnatInterface updateSubscriptionAtUrl(String subUrl, Subscription subscription) {
        queryBase().contentType(ContentType.JSON).body(subscription).put(formatXapiUrl(subUrl)).then().assertThat().statusCode(200)
        xnatInterface
    }

    private XnatInterface activateSubscriptionAtUrl(String subUrl, Subscription subscription) {
        queryBase().post(formatXapiUrl(subUrl)).then().assertThat().statusCode(200)
        subscription.setActive(true)
        xnatInterface
    }

    private XnatInterface deactivateSubscriptionAtUrl(String subUrl, Subscription subscription) {
        queryBase().post(formatXapiUrl(subUrl)).then().assertThat().statusCode(200)
        subscription.setActive(false)
        xnatInterface
    }

    private List<DeliveredEvent> queryDeliveredEventsAtUrl(String subUrl, PaginatedRequest request, Integer expectedNumEvents, boolean waitForEventsComplete) {
        final StopWatch timer = TimeUtils.launchStopWatch();
        while (true) {
            final List<DeliveredEvent> events = SerializationUtils.deserializeList(
                    queryBase().contentType(ContentType.JSON).body(request).post(formatXapiUrl(subUrl)).
                            then().assertThat().statusCode(200).extract().as(List),
                    DeliveredEvent
            )
            TimeUtils.checkStopWatch(timer, 60, "Delivered Event query didn't return the exepected (${expectedNumEvents}) number of events (or events did not complete in time). For the last attempt, ${events.size()} events were returned.")
            if (expectedNumEvents == null || expectedNumEvents == events.size()) {
                final boolean eventsComplete = !events.any { event ->
                    event.status.last().status != EventStepStatus.ACTION_COMPLETE || event.status.last().message != event.statusMessage
                }
                if (!waitForEventsComplete || eventsComplete) {
                    return events
                }
            }
        }
    }

    private DeliveredEvent readDeliveredEventAtUrl(String subUrl) {
        queryBase().get(formatXapiUrl(subUrl)).as(DeliveredEvent)
    }

}
