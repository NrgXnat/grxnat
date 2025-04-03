package org.nrg.xnat.pogo.plugins

import io.restassured.RestAssured
import io.restassured.path.json.JsonPath

import java.util.function.Function

class GithubReleaseDerivation implements Function<String, String> {

    String githubUser
    String repo
    
    GithubReleaseDerivation(String githubUser, String repo) {
        this.githubUser = githubUser
        this.repo = repo
    }
    
    GithubReleaseDerivation() {
        
    }

    @Override
    String apply(String version) {
        final JsonPath response = RestAssured.given().get("https://api.github.com/repos/${githubUser}/${repo}/releases").jsonPath()
        final Map<String, Object> foundRelease = response.getMap("find { it.name == '${version}' }")
        if (foundRelease != null) {
            foundRelease.assets[0].browser_download_url
        } else {
            throw new RuntimeException("Could not find specified version ${version}")
        }
    }
    
}
