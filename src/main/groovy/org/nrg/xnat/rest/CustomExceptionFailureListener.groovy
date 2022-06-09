package org.nrg.xnat.rest

import io.restassured.listener.ResponseValidationFailureListener
import io.restassured.response.Response
import io.restassured.specification.RequestSpecification
import io.restassured.specification.ResponseSpecification

trait CustomExceptionFailureListener implements ResponseValidationFailureListener {

    abstract Class<? extends Exception> getExceptionClass()

    abstract int getMappedStatusCode()

    @Override
    void onFailure(RequestSpecification requestSpecification, ResponseSpecification responseSpecification, Response response) {
        if (response.statusCode() == getMappedStatusCode()) {
            throw getExceptionClass().newInstance()
        }
    }

}
