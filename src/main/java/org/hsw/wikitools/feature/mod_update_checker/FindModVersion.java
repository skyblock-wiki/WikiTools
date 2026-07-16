package org.hsw.wikitools.feature.mod_update_checker;

public interface FindModVersion {
    FindModVersionResult findLatestVersion();

    class FindModVersionResult {
        public final boolean success;
        public final String message;
        public final String version;

        private FindModVersionResult(boolean success, String message, String version) {
            this.success = success;
            this.message = message;
            this.version = version;
        }

        public static FindModVersionResult success(String version) {
            return new FindModVersionResult(true, null, version);
        }

        public static FindModVersionResult failure(String message) {
            return new FindModVersionResult(false, message, null);
        }
    }
}
