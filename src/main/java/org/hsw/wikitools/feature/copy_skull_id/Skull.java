package org.hsw.wikitools.feature.copy_skull_id;
import com.google.gson.Gson;

import java.util.Base64;

public class Skull {
    String textureId;

    private Skull(String textureId) {
        this.textureId = textureId;
    }

    public static Skull ofTextureValue(String base64TextureValue) {
        byte[] decodedBytes = Base64.getDecoder().decode(base64TextureValue);
        TextureData textureData = new Gson().fromJson(new String(decodedBytes), TextureData.class);
        String textureId = textureData.textures.SKIN.url.split("/")[4];

        return new Skull(textureId);
    }

    private static class TextureData {
        public Textures textures;

        static class Textures {
            public Skin SKIN;

            static class Skin {
                public String url;
            }
        }
    }
}
