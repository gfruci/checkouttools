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
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hotels.checkitotools.model.I18nMessagesLocalisationServiceModel;

/**
 * Created by Marton_Kadar on 2017-06-26.
 *
 * Updates the internationalisation mock in Checkito from the localisation service with en_US values.
 * Helper class to fulfill the ACs of CKO-603.
 */
public class I18nMockUpdater {
    private static final Logger LOGGER = LoggerFactory.getLogger(I18nMockUpdater.class);

    private static final int MILLIS_TO_WAIT_BETWEEN_SERVICE_CALLS = 100;
    private static final String LOCALISATIONSVC_STAGING = "http://localisationsvc.staging.hcom/messages/";

    /**
     *
     * @param args The first parameter should be the input json file.
     *
     * @throws IOException File not found, or something IO went wrong.
     * @throws InterruptedException Thread.sleep can throw this, but the service went down when we tried it without wait.
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length < 1) {
            LOGGER.error("Mandatory parameter missing.\n\t\tUsage: java I18nMockUpdater {path to the i18n_messages.json in the Checkout repository}");
        } else {
            new I18nMockUpdater().update(args[0]);
        }

    }

    private void update(String jsonFile) throws IOException, InterruptedException {
        Map<String, String> i18nMessages = loadJsonFileToMap(jsonFile);
        TreeMap<String, String> newI18nMessages = new TreeMap<>();

        int totalMessageCount = i18nMessages.size();
        int processed = 0;
        int nullValueCounter = 0;
        int sameAsBeforeCounter = 0;

        LOGGER.info("Updating mock data from localisation service started.");
        for (Map.Entry messageEntry : i18nMessages.entrySet()) {
            String key = messageEntry.getKey().toString();

            Thread.sleep(MILLIS_TO_WAIT_BETWEEN_SERVICE_CALLS);

            LOGGER.debug("[{} / {}] Getting {} from localisation service...", processed++, totalMessageCount, messageEntry.getKey());
            String newLocalisationValue = getMockDataFromLocalisationService(new URL(LOCALISATIONSVC_STAGING + key));

            LOGGER.debug("[{} / {}] New value for {} is '{}'", processed, totalMessageCount, messageEntry.getKey(), newLocalisationValue);
            newI18nMessages.put(key, newLocalisationValue);

            if (null == newLocalisationValue) {
                LOGGER.warn("Null value found for key: {}", messageEntry.getKey());
                nullValueCounter++;
            } else if (newLocalisationValue.equals(messageEntry.getValue())) {
                LOGGER.debug("Value in localisation service has not changed since last update for key: {}", messageEntry.getKey());
                sameAsBeforeCounter++;
            }
        }

        LOGGER.info("Updating mock data from localisation service finished.");
        LOGGER.info("Total calls: {}, unchanged values: {}, null values in service: {}.", totalMessageCount, sameAsBeforeCounter, nullValueCounter);

        createJsonFileFromMap(newI18nMessages, jsonFile);
    }

    private String getMockDataFromLocalisationService(URL url) throws IOException {
        LOGGER.debug("Opening connection to {}.", url);
        URLConnection urlConnection = url.openConnection();
        InputStream inputStream = urlConnection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        LOGGER.debug("Parsing data from service...");
        String mockData = new Gson().fromJson(reader, I18nMessagesLocalisationServiceModel.class).getEnUS();

        LOGGER.debug("Closing readers...");
        reader.close();
        inputStream.close();

        return mockData;
    }

    private void createJsonFileFromMap(TreeMap<String, String> mapToSaveAsJson, String fileName) {
        Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
        File file = new File(fileName);

        try (Writer writer = new FileWriter(file)) {
            LOGGER.info("Writing file to {}.", file.getAbsolutePath());
            gson.toJson(mapToSaveAsJson, writer);
        } catch (IOException e) {
            LOGGER.error("File writing failed to {}.\nCause: {}", file.getAbsolutePath(), e);
        }
    }

    private Map<String, String> loadJsonFileToMap(String jsonFile) {
        LOGGER.debug("Loading JSON file from {} to map.", jsonFile);
        Type mapType = new TypeToken<Map<String, String>>() {
        }.getType();

        Map<String, String> resultMap = new HashMap<>();
        try {
            resultMap = new Gson().fromJson(new FileReader(jsonFile), mapType);
        } catch (FileNotFoundException e) {
            LOGGER.error("File not found {}.\nCause: {}", jsonFile, e);
        }

        return resultMap;
    }

}
