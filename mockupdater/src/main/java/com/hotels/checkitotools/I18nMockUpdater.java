package com.hotels.checkitotools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hotels.checkitotools.model.I18nMessagesLocalisationServiceModel;

/**
 * Updates the internationalisation mock in Checkito from the localisation service with en_US values.
 * Helper utility to fulfill the ACs of CKO-603.
 *
 * @author Marton_Kadar
 */
public class I18nMockUpdater {
    private static final Logger LOGGER = LoggerFactory.getLogger(I18nMockUpdater.class);

    private static final int MILLIS_TO_WAIT_BETWEEN_SERVICE_CALLS = 100;
    private static final String LOCALISATION_SERVICE_STAGING_URL = "http://localisationsvc.staging.hcom/messages/";

    /**
     * @param args The first parameter should be the input json file.
     *
     * @throws IOException File not found, or something IO went wrong.
     * @throws InterruptedException Thread.sleep can throw this, but the service went down when we tried it without wait.
     */
    public static void main(final String[] args) throws IOException, InterruptedException {
        if (args.length < 1) {
            LOGGER.error("Mandatory parameter missing.\n"
                + "\t\tUsage:\tjava -jar target/mockupdater-{version}-jar-with-dependencies.jar \"{path to the i18n_messages.json in the Checkout "
                + "repository}\"");
        } else {
            new I18nMockUpdater().update(args[0]);
        }
    }

    private void update(final String jsonFile) throws IOException, InterruptedException {
        final Map<String, String> i18nMessages = loadJsonFileToMap(jsonFile);
        final TreeMap<String, String> newI18nMessages = new TreeMap<>();

        final int totalMessageCount = i18nMessages.size();
        int processed = 0;
        int nullValueCounter = 0;
        int sameAsBeforeCounter = 0;

        LOGGER.info("Updating mock data from localisation service started.");
        for (final Map.Entry messageEntry : i18nMessages.entrySet()) {
            final String key = messageEntry.getKey().toString();

            Thread.sleep(MILLIS_TO_WAIT_BETWEEN_SERVICE_CALLS);

            LOGGER.debug("[{} / {}] Getting {} from localisation service...", processed + 1, totalMessageCount, messageEntry.getKey());
            final String newLocalisationValue = getMockDataFromLocalisationService(key);

            LOGGER.debug("[{} / {}] New value for {} is '{}'", processed++, totalMessageCount, messageEntry.getKey(), newLocalisationValue);
            newI18nMessages.put(key, newLocalisationValue);

            if (null == newLocalisationValue) {
                LOGGER.warn("Null value found for key: {}", messageEntry.getKey());
                nullValueCounter++;
            }
            if (Objects.equals(messageEntry.getValue(), newLocalisationValue)) {
                LOGGER.debug("Value in localisation service has not changed since last update for key: {}", messageEntry.getKey());
                sameAsBeforeCounter++;
            }
        }

        LOGGER.info("Updating mock data from localisation service finished.");
        LOGGER.info("Total calls: {}, unchanged values: {}, null values in service: {}.", totalMessageCount, sameAsBeforeCounter, nullValueCounter);

        createJsonFileFromMap(newI18nMessages, jsonFile);
    }

    private String getMockDataFromLocalisationService(final String key) throws IOException {
        final URL url = new URL(LOCALISATION_SERVICE_STAGING_URL + key);
        LOGGER.debug("Opening connection to {}.", url);

        final URLConnection urlConnection = url.openConnection();
        final InputStream inputStream = urlConnection.getInputStream();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        LOGGER.debug("Parsing data from service...");
        final String mockData = new Gson().fromJson(reader, I18nMessagesLocalisationServiceModel.class).getEnUS();

        LOGGER.debug("Closing readers...");
        reader.close();
        inputStream.close();

        return mockData;
    }

    private void createJsonFileFromMap(final TreeMap<String, String> mapToSaveAsJson, final String fileName) {
        final Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
        final File file = new File(fileName);

        try (Writer writer = new FileWriter(file)) {
            LOGGER.info("Writing file to {}.", file.getAbsolutePath());
            gson.toJson(mapToSaveAsJson, writer);
        } catch (final IOException e) {
            LOGGER.error("File writing failed to {}.\nCause: {}", file.getAbsolutePath(), e);
        }
    }

    private Map<String, String> loadJsonFileToMap(final String jsonFile) {
        LOGGER.debug("Loading JSON file from {} to map.", jsonFile);
        final Type mapType = new TypeToken<Map<String, String>>() {
        }.getType();

        Map<String, String> resultMap = new HashMap<>();
        try {
            resultMap = new Gson().fromJson(new FileReader(jsonFile), mapType);
        } catch (final FileNotFoundException e) {
            LOGGER.error("File not found {}.\nCause: {}", jsonFile, e);
        }

        return resultMap;
    }

}
