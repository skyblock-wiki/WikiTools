package org.hsw.wikitools.feature.copy_data_tags;

import org.jetbrains.annotations.Nullable;

public class EntityDataTags {

    public static String getEntityDataTags(String serializedDataTags, @Nullable String gameProfileProperties) {
        validateSerializedDataTags(serializedDataTags);

        if (gameProfileProperties != null) {
            serializedDataTags = insertGameProfileDataTag(serializedDataTags, gameProfileProperties);
        }

        validateSerializedDataTags(serializedDataTags);

        return serializedDataTags;
    }

    private static void validateSerializedDataTags(String serializedDataTags) {
        assert serializedDataTags.startsWith("{");
        assert serializedDataTags.endsWith("}");
    }

    private static String insertGameProfileDataTag(String serializedDataTags, String gameProfileProperties) {
        validateSerializedDataTags(serializedDataTags);

        String withoutClosingBrace = serializedDataTags.substring(0, serializedDataTags.length() - 1);
        String sb = withoutClosingBrace + ",__gameProfile:" + gameProfileProperties + "}";

        validateSerializedDataTags(serializedDataTags);
        return sb;
    }
}
