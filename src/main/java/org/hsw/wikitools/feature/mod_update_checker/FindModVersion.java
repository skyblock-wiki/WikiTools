package org.hsw.wikitools.feature.mod_update_checker;

import java.util.Optional;

public interface FindModVersion {
    FindModVersionResult findLatestVersion();

    class FindModVersionResult {
        public final boolean success;
        public final Optional<String> message;
        public final Optional<String> version;

        private FindModVersionResult(boolean success, Optional<String> message, Optional<String> version) {
            this.success = success;
            this.message = message;
            this.version = version;
        }

        public static FindModVersionResult success(String version) {
            return new FindModVersionResult(true, Optional.empty(), Optional.of(version));
        }

        public static FindModVersionResult failure(String message) {
            return new FindModVersionResult(false, Optional.of(message), Optional.empty());
        }
    }
}
