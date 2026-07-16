package org.hsw.wikitools;

import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ModProperties {
    private static final ConfigReader configReader = new ConfigReader("config.properties");

    public static final String MOD_ID = configReader.getProperty("MOD_ID");
    public static final String MOD_VERSION = configReader.getProperty("MOD_VERSION");

    public static final String GITHUB_API_BASE_URL = configReader.getProperty("GITHUB_API_BASE_URL");
    public static final String LATEST_RELEASE_PATH = configReader.getProperty("LATEST_RELEASE_PATH");
    public static final String LATEST_RELEASE_DOWNLOAD_URL = configReader.getProperty("LATEST_RELEASE_DOWNLOAD_URL");

    public static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID, "main"));

    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


    private static class ConfigReader {
        private final Properties properties = new Properties();
        private final String propertiesFileName;

        public ConfigReader(String propertiesFileName) {
            this.propertiesFileName = propertiesFileName;

            try (InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream(propertiesFileName)) {
                if (input == null) {
                    throw new IllegalStateException("[WikiTools] Missing properties file " + propertiesFileName);
                } else {
                    properties.load(input);
                }
            } catch (IOException ex) {
                throw new IllegalStateException("[WikiTools] Failed to load properties file " + propertiesFileName, ex);
            }
        }

        public String getProperty(String key) {
            String property = properties.getProperty(key);
            if (property == null) {
                throw new IllegalStateException("[WikiTools] Missing required property in " + propertiesFileName + ": " + key);
            }
            return property;
        }
    }

}
