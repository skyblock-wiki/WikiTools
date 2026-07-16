package org.hsw.wikitools.feature.copy_data_tags;

import java.util.Optional;

public class EntityDataTags {
    public final String serialized;

    public EntityDataTags(String serializedDataTags, Optional<String> gameProfileProperties) {
        validateSerializedDataTags(serializedDataTags);

        if (gameProfileProperties.isPresent()) {
            serializedDataTags = insertCustomStringDataTag(serializedDataTags, "__gameProfile", gameProfileProperties.get());
        }

        validateSerializedDataTags(serializedDataTags);

        this.serialized = serializedDataTags;
    }

    private static void validateSerializedDataTags(String serializedDataTags) {
        assert serializedDataTags.startsWith("{");
        assert serializedDataTags.endsWith("}");
    }

    private static String insertCustomStringDataTag(String serializedDataTags, String tag, String content) {
        validateSerializedDataTags(serializedDataTags);

        String withoutClosingBrace = serializedDataTags.substring(0, serializedDataTags.length() - 1);
        StringBuilder sb = new StringBuilder();
        sb.append(withoutClosingBrace).append(",");
        sb.append(tag).append(":").append(content);
        sb.append("}");

        validateSerializedDataTags(serializedDataTags);
        return sb.toString();
    }
}
