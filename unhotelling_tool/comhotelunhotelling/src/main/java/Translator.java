import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.google.gson.Gson;

/**
 *
 */
public class Translator {

    static String baseLomsURLString = "http://localisationmessagesvc.milan.hcom/messages/";
    static String ENG_LANGUAGE_KEY = "en_GB";
    static String FR_LANGUAGE_KEY = "fr_FR";
    final static Logger logger = Logger.getLogger(Translator.class);

    public List<Output> urlCall() throws MalformedURLException, IOException {

        URL baseLomsURL = new URL(baseLomsURLString);

        List<Output> outputList = new ArrayList<Output>();
        List<String> readedKeys = keyReader();
        int i = 1;
        for (String key : readedKeys) {
            logger.info("processing: " + i++ + " out of " + readedKeys.size());
            logger.info("key : " + key);
            URL concatenatedOriginalURL = new URL(baseLomsURL + key);

            Output newOutput = new Output();
            newOutput.setKeyName(key);

            HashMap<String, String> originalResponseMap = hashMapResponse(concatenatedOriginalURL);
            String originalEnGbValue = originalResponseMap.get(ENG_LANGUAGE_KEY);

            if (originalEnGbValue != null) {
                if (hotelRoomMatches(originalEnGbValue)) {
                    newOutput.setOriginalContentEN_GB(originalEnGbValue);
                } else {
                    continue;
                }
            } else {
                continue;
            }

            setUnhotellingKey(newOutput, concatenatedOriginalURL, originalEnGbValue);

            setUnhotellingPropertyKey(newOutput, concatenatedOriginalURL);

            setColorCode(newOutput);
            outputList.add(newOutput);
        }

        return outputList;
    }

    public boolean hotelRoomMatches(String originalEnGbValue) {
        return originalEnGbValue.matches("(.*)(?i:hotel)(.*)") || originalEnGbValue.matches("(.*)(?i:room)(.*)");
    }

    //File reader
    public List<String> keyReader() throws IOException {

        List<String> readedKeys = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new FileReader("sample.txt"));
        try {
            String strLine;
            while ((strLine = br.readLine()) != null) {
                logger.info(strLine);

                String[] valuesInQuotes = StringUtils.substringsBetween(strLine, "\"", "\"");

                if (valuesInQuotes != null) readedKeys.add(valuesInQuotes[0]);
            }
            br.close();
        } catch (Exception e) {

        } finally {
            br.close();
        }
        return readedKeys;
    }

    public HashMap<String, String> hashMapResponse(URL url) throws IOException {

        HttpURLConnection originalConnection = (HttpURLConnection) url.openConnection();
        originalConnection.setRequestMethod("GET");

        BufferedReader inputOriginal = new BufferedReader(new InputStreamReader(originalConnection.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();

        while ((inputLine = inputOriginal.readLine()) != null) {
            content.append(inputLine);
        }

        inputOriginal.close();

        String response = content.toString();
        HashMap<String, String> hashMapResponse = new Gson().fromJson(response, HashMap.class);
        return hashMapResponse;
    }

    private void setUnhotellingKey(Output newOutput, URL concatenatedOriginalURL, String originalEnGbValue) throws IOException {

        URL unhotellingURL = new URL(concatenatedOriginalURL + ".unhotelling");
        HashMap<String, String> unhotellingResponseMap = hashMapResponse(unhotellingURL);
        String unhotellingEnGbValue = unhotellingResponseMap.get(ENG_LANGUAGE_KEY);

        if (unhotellingEnGbValue == null) {
            newOutput.setMissingUnhotellingKey(true);
        } else {
            if (!unhotellingEnGbValue.equals(originalEnGbValue)) {
                newOutput.setDifferentUnhotellingText(true);
            }

            newOutput.setUnhotellingContentEN_GB(unhotellingEnGbValue);
        }
    }

    private void setUnhotellingPropertyKey(Output newOutput, URL concatenatedURL) throws IOException {

        URL unhotellingPropertyKeyURL = new URL(concatenatedURL + ".unhotelling" + ".property");
        HashMap<String, String> unhotellingPropertyResponseMap = hashMapResponse(unhotellingPropertyKeyURL);
        String unhotellingPropertyEnGbValue = unhotellingPropertyResponseMap.get(ENG_LANGUAGE_KEY);
        String unhotellingPropertyFRValue = unhotellingPropertyResponseMap.get(FR_LANGUAGE_KEY);

        if (unhotellingPropertyEnGbValue == null) {
            newOutput.setNoPropertyKey(true);
        } else {
            newOutput.setUnhotellingPropertyContetnt(unhotellingPropertyEnGbValue);

            if (unhotellingPropertyFRValue != null) {
                if (unhotellingPropertyEnGbValue.equals(unhotellingPropertyFRValue)) {
                    newOutput.setUnhotellingPropertyNotTranslated(true);
                }
            }
        }
    }

    private void setColorCode(Output output) {
        //RED
        if (output.isMissingUnhotellingKey() != null && output.isMissingUnhotellingKey() == true) {
            output.setColorCode(ColorCodes.RED);
            return;
        }

        //BROWN
        if ((output.isNoPropertyKey() != null || output.isUnhotellingPropertyNotTranslated() != null) && output.isMissingUnhotellingKey() == null) {
            output.setColorCode(ColorCodes.BROWN);
            return;
        }

        //GREEN
        if (output.isNoPropertyKey() == null && output.isUnhotellingPropertyNotTranslated() == null && output.isMissingUnhotellingKey() == null) {
            output.setColorCode(ColorCodes.GREEN);
            return;
        }
    }
}
