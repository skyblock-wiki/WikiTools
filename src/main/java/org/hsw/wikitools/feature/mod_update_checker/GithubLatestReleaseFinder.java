package org.hsw.wikitools.feature.mod_update_checker;

import com.google.gson.Gson;
import org.hsw.wikitools.ModProperties;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GithubLatestReleaseFinder implements FindModVersion {

    private final String githubApiBaseUrl;

    public GithubLatestReleaseFinder(String githubApiBaseUrl) {
        this.githubApiBaseUrl = githubApiBaseUrl;
    }

    public FindModVersionResult findLatestVersion() {
        try (HttpClient client = HttpClient.newHttpClient()) {
            String latestReleasePath = ModProperties.LATEST_RELEASE_PATH;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(githubApiBaseUrl + latestReleasePath))
                    .header("Accept", "application/vnd.github+json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            Release release = new Gson().fromJson(response.body(), Release.class);

            if (release == null || release.tag_name == null) {
                return FindModVersionResult.failure("Latest Release Fetch/Parse Failure (" + response.body() + ")");
            }

            String latestVersionName = release.tag_name;

            return createValidVersionResult(latestVersionName);
        } catch (IOException | InterruptedException ignored) {
            return FindModVersionResult.failure("Latest Release Fetch Failure");
        }
    }

    private static FindModVersionResult createValidVersionResult(String latestVersionName) {
        if (latestVersionName.startsWith("v")) {
            latestVersionName = latestVersionName.substring(1);
        }

        return FindModVersionResult.success(latestVersionName);
    }

    private static class Release {
        public String tag_name;
    }

}
