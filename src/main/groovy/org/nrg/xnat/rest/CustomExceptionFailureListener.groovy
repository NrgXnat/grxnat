package org.nrg.xnat.rest

import io.restassured.listener.ResponseValidationFailureListener
import io.restassured.response.Response
import io.restassured.specification.RequestSpecification
import io.restassured.specification.ResponseSpecification

trait CustomExceptionFailureListener implements ResponseValidationFailureListener {

    abstract Class<? extends HttpStatusException> getExceptionClass()

    int getMappedStatusCode() {
        getExceptionClass().newInstance().statusCode
    }

    @Override
    void onFailure(RequestSpecification requestSpecification, ResponseSpecification responseSpecification, Response response) {
        if (response.statusCode() == getMappedStatusCode()) {
            throw getExceptionClass().newInstance()
        }
    }

}
