package org.hsw.wikitools.feature.mod_update_checker;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

class GetNewVersionHandlerTest {

    private static GetNewVersionHandler.GetNewVersionResponse getNewVersion(String currentVersion, String latestVersion) {
        LatestReleaseFinderStub latestReleaseFinderStub = new LatestReleaseFinderStub(
                FindModVersion.FindModVersionResult.success(latestVersion)
        );
        GetNewVersionHandler getNewVersionHandler = new GetNewVersionHandler(latestReleaseFinderStub);
        Future<GetNewVersionHandler.GetNewVersionResponse> result =
                getNewVersionHandler.getNewVersion(new GetNewVersionHandler.GetNewVersionRequest(currentVersion));
        return assertDoesNotThrow(() -> result.get());
    }

    @Nested
    class ShowsFailure {

        @Test
        void whenCurrentVersionIsNotValidSemVer() {
            GetNewVersionHandler.GetNewVersionResponse response = getNewVersion("2.0", "v1.0.0");
            assertFalse(response.success);
            assertEquals(response.message, Optional.of("Version Parse Failure (2.0)"));
        }

        @Test
        void whenLatestVersionIsNotValidSemVer() {
            GetNewVersionHandler.GetNewVersionResponse response = getNewVersion("1.0.0", "2.0");
            assertFalse(response.success);
            assertEquals(response.message, Optional.of("Version Parse Failure (2.0)"));
        }

        @Test
        void whenFetchOrParseFailed() {
            LatestReleaseFinderStub latestReleaseFinderStub = new LatestReleaseFinderStub(
                    FindModVersion.FindModVersionResult.failure("Latest Release Fetch/Parse Failure (HTTP/1.1 404 Not Found)")
            );
            GetNewVersionHandler getNewVersionHandler = new GetNewVersionHandler(latestReleaseFinderStub);
            Future<GetNewVersionHandler.GetNewVersionResponse> result =
                    getNewVersionHandler.getNewVersion(new GetNewVersionHandler.GetNewVersionRequest("1.0.0"));
            GetNewVersionHandler.GetNewVersionResponse response = assertDoesNotThrow(() -> result.get());
            assertFalse(response.success);
            assertEquals(response.message, Optional.of("Latest Release Fetch/Parse Failure (HTTP/1.1 404 Not Found)"));
        }

    }

    @Nested
    class VersionNameCanBeUsed {

        @ParameterizedTest
        @ValueSource(strings = {
                "1.9.0", "1.10.0",
                "1.0.0-alpha", "1.0.0-beta.1",
                "1.0.0-alpha+1.8.9", "1.0.0-beta.1+1.8.9"
        })
        void whenIsValidSemVer(String versionName) {
            GetNewVersionHandler.GetNewVersionResponse response = getNewVersion(versionName, versionName);
            assertTrue(response.success);
            assertTrue(response.result.isPresent());
            assertEquals(response.result.get().latestVersion, versionName);
        }

    }

    @Nested
    class DetectsNoNewRelease {

        @Test
        void whenVersionsAreEqual() {
            GetNewVersionHandler.GetNewVersionResponse response = getNewVersion("2.0.0", "2.0.0");
            assertTrue(response.success);
            assertTrue(response.result.isPresent());
            assertFalse(response.result.get().hasNewRelease);
        }

        @ParameterizedTest
        @CsvSource({
                "1.10.10-beta.1, 1.10.9",
                "1.10.10, 1.10.9"
        })
        void whenLatestPatchVersionIsSmaller(String currentVersion, String latestVersion) {
            GetNewVersionHandler.GetNewVersionResponse response = getNewVersion(currentVersion, latestVersion);
            assertTrue(response.success);
            assertTrue(response.result.isPresent());
            assertFalse(response.result.get().hasNewRelease);
        }

        @Test
        void whenLatestMinorVersionIsSmaller() {
            GetNewVersionHandler.GetNewVersionResponse response = getNewVersion("2.1.0", "2.0.10");
            assertTrue(response.success);
            assertTrue(response.result.isPresent());
            assertFalse(response.result.get().hasNewRelease);
        }

        @Test
        void whenLatestMajorVersionIsSmaller() {
            GetNewVersionHandler.GetNewVersionResponse response = getNewVersion("2.0.0", "1.10.10");
            assertTrue(response.success);
            assertTrue(response.result.isPresent());
            assertFalse(response.result.get().hasNewRelease);
        }

    }

    @Nested
    class DetectsNewRelease {

        @Test
        void whenPrereleaseIsReleased() {
            GetNewVersionHandler.GetNewVersionResponse response = getNewVersion("1.10.10-beta.10", "1.10.10");
            assertTrue(response.success);
            assertTrue(response.result.isPresent());
            assertTrue(response.result.get().hasNewRelease);
        }

        @ParameterizedTest
        @CsvSource({
                "1.10.10-beta.1, 1.10.11",
                "1.10.9, 1.10.10,"
        })
        void whenLatestPatchVersionIsLarger(String currentVersion, String latestVersion) {
            GetNewVersionHandler.GetNewVersionResponse response = getNewVersion(currentVersion, latestVersion);
            assertTrue(response.success);
            assertTrue(response.result.isPresent());
            assertTrue(response.result.get().hasNewRelease);
        }

        @Test
        void whenLatestMinorVersionIsLarger() {
            GetNewVersionHandler.GetNewVersionResponse response = getNewVersion("2.0.10", "2.1.0");
            assertTrue(response.success);
            assertTrue(response.result.isPresent());
            assertTrue(response.result.get().hasNewRelease);
        }

        @Test
        void whenLatestMajorVersionIsLarger() {
            GetNewVersionHandler.GetNewVersionResponse response = getNewVersion("1.10.10", "2.0.0");
            assertTrue(response.success);
            assertTrue(response.result.isPresent());
            assertTrue(response.result.get().hasNewRelease);
        }

    }

}
