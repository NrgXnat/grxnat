package org.nrg.xnat.pogo.plugins

import io.restassured.RestAssured
import io.restassured.path.json.JsonPath

import java.util.function.Function

class BitbucketDownloadsDerivation implements Function<String, String> {

    String bitbucketUser
    String repo
    
    BitbucketDownloadsDerivation(String bitbucketUser, String repo) {
        this.bitbucketUser = bitbucketUser
        this.repo = repo
    }
    
    BitbucketDownloadsDerivation() {
        
    }

    @Override
    String apply(String version) {
        queryUrlForVersion("https://api.bitbucket.org/2.0/repositories/${bitbucketUser}/${repo}/downloads", version)
    }
    
    String queryUrlForVersion(String url, String version) {
        final JsonPath response = RestAssured.given().get(url).jsonPath()
        final List<Map<String, Object>> artifacts = response.getList('values')
        final Map<String, Object> found = artifacts.find { artifact ->
            artifactMatchesSuppliedVersion(artifact.get('name') as String, version)
        }
        if (found != null) {
            found.links.self.href
        } else {
            final String next = response.getString('next')
            if (next == null) {
                throw new RuntimeException("Could not find specified version ${version}")
            } else {
                queryUrlForVersion(next, version)
            }
        }
    }
    
    boolean artifactMatchesSuppliedVersion(String artifact, String version) {
        artifact.endsWith("-${version}.jar") || artifact.endsWith("-${version}-fat.jar") || artifact.endsWith("-${version}.war")
    }
    
}
