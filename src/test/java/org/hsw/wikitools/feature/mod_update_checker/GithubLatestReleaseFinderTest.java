package org.hsw.wikitools.feature.mod_update_checker;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class GithubLatestReleaseFinderTest {
    private static final Integer mockServerPort = 1080;
    private static final String mockServerBaseUrl = "http://localhost:" + mockServerPort;
    private static final String latestReleasePath = "/repos/skyblock-wiki/wikitools/releases/latest";

    private static ClientAndServer mockServer;

    @BeforeAll
    public static void startServer() {
        mockServer = startClientAndServer(mockServerPort);
    }

    @AfterAll
    public static void stopServer() {
        mockServer.stop();
    }

    @Test
    public void shouldCorrectlyGetVersion() {
        String responseBody = getTestPayload("github_latest_release.json");
        mockServer.reset();
        mockServer.when(
                request()
                        .withMethod("GET")
                        .withPath(latestReleasePath)
        ).respond(
                response()
                        .withStatusCode(200)
                        .withBody(responseBody)
        );

        var classUnderTest = new GithubLatestReleaseFinder(mockServerBaseUrl);
        var result = classUnderTest.findLatestVersion();
        assertTrue(result.success);
        assertTrue(result.version.isPresent());
        assertEquals("2.6.6", result.version.get());
    }

    @Test
    public void shouldCorrectlyReportNotFound() {
        String responseBody = getTestPayload("github_latest_release_not_found.json");
        mockServer.reset();
        mockServer.when(
                request()
                        .withMethod("GET")
                        .withPath(latestReleasePath)
        ).respond(
                response()
                        .withStatusCode(404)
                        .withBody(responseBody)
        );

        var classUnderTest = new GithubLatestReleaseFinder(mockServerBaseUrl);
        var result = classUnderTest.findLatestVersion();
        assertFalse(result.success);
        assertTrue(result.message.isPresent());
        assertTrue(result.message.get().contains("Not Found"));
    }

    private String getTestPayload(String fileName) {
        try (var responseContent = getClass().getResourceAsStream("/test_payloads/" + fileName)) {
            assertNotNull(responseContent);
            return new String(responseContent.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
