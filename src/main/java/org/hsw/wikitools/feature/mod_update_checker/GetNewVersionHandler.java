package org.hsw.wikitools.feature.mod_update_checker;

import com.github.zafarkhaja.semver.Version;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class GetNewVersionHandler {

    private final FindModVersion findModVersion;

    public GetNewVersionHandler(FindModVersion findModVersion) {
        this.findModVersion = findModVersion;
    }

    public CompletableFuture<GetNewVersionResponse> getNewVersion(GetNewVersionRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            String currentVersionName = request.currentVersion;
            Optional<Version> currentVersion = getVersion(currentVersionName);
            if (!currentVersion.isPresent()) {
                return GetNewVersionResponse.failure("Version Parse Failure (" + currentVersionName + ")");
            }

            FindModVersion.FindModVersionResult latestVersionResult = findModVersion.findLatestVersion();
            if (!latestVersionResult.success || !latestVersionResult.version.isPresent()) {
                return GetNewVersionResponse.failure(latestVersionResult.message.orElse("Unknown Error"));
            }

            String latestVersionName = latestVersionResult.version.get();

            Optional<Version> latestVersion = getVersion(latestVersionName);
            if (!latestVersion.isPresent()) {
                return GetNewVersionResponse.failure("Version Parse Failure (" + latestVersionName + ")");
            }

            return GetNewVersionResponse.success(
                    new GetNewVersionResult(
                            checkIfModNeedsUpdating(currentVersion.get(), latestVersion.get()),
                            latestVersion.get().toString()
                    )
            );
        });
    }

    private static boolean checkIfModNeedsUpdating(Version currentVersion, Version latestVersion) {
        // This assumes the current version to be any valid version using SemVer
        // (Correct: 2.0.0, 2.0.0-beta.1)

        // This assumes the latest version to have no prerelease tag
        // (Correct: 2.0.0; Incorrect: 2.0.0-beta.1)

        // Hence we assume no case where the latest version is a higher prerelease version
        // and which we do not want to remind users of.

        return latestVersion.isHigherThan(currentVersion);
    }

    private static Optional<Version> getVersion(String versionName) {
        return Version.tryParse(versionName);
    }

    public static class GetNewVersionRequest {
        public final String currentVersion;

        public GetNewVersionRequest(String currentVersion) {
            this.currentVersion = currentVersion;
        }
    }

    public static class GetNewVersionResponse {
        public final boolean success;
        public final Optional<String> message;
        public final Optional<GetNewVersionResult> result;

        public GetNewVersionResponse(boolean success, Optional<String> message, Optional<GetNewVersionResult> result) {
            this.success = success;
            this.message = message;
            this.result = result;
        }

        public static GetNewVersionResponse success(GetNewVersionResult result) {
            return new GetNewVersionResponse(true, Optional.empty(), Optional.of(result));
        }

        public static GetNewVersionResponse failure(String message) {
            return new GetNewVersionResponse(false, Optional.of(message), Optional.empty());
        }
    }

    public static class GetNewVersionResult {
        public final boolean hasNewRelease;
        public final String latestVersion;

        public GetNewVersionResult(boolean hasNewRelease, String latestVersion) {
            this.hasNewRelease = hasNewRelease;
            this.latestVersion = latestVersion;
        }
    }

}
