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
    private static final Integer MOCK_SERVER_PORT = 1080;
    private static final String MOCK_SERVER_BASE_URL = "http://localhost:" + MOCK_SERVER_PORT;
    private static final String LATEST_RELEASE_PATH = "/repos/skyblock-wiki/wikitools/releases/latest";

    private static ClientAndServer mockServer;

    @BeforeAll
    public static void startServer() {
        mockServer = startClientAndServer(MOCK_SERVER_PORT);
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
                        .withPath(LATEST_RELEASE_PATH)
        ).respond(
                response()
                        .withStatusCode(200)
                        .withBody(responseBody)
        );

        var classUnderTest = new GithubLatestReleaseFinder(MOCK_SERVER_BASE_URL);
        var result = classUnderTest.findLatestVersion();
        assertTrue(result.success);
        assertNotNull(result.version);
        assertEquals("2.6.6", result.version);
    }

    @Test
    public void shouldCorrectlyReportNotFound() {
        String responseBody = getTestPayload("github_latest_release_not_found.json");
        mockServer.reset();
        mockServer.when(
                request()
                        .withMethod("GET")
                        .withPath(LATEST_RELEASE_PATH)
        ).respond(
                response()
                        .withStatusCode(404)
                        .withBody(responseBody)
        );

        var classUnderTest = new GithubLatestReleaseFinder(MOCK_SERVER_BASE_URL);
        var result = classUnderTest.findLatestVersion();
        assertFalse(result.success);
        assertNotNull(result.message);
        assertTrue(result.message.contains("Not Found"));
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
