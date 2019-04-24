import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;

/**
 * @author Nandor_Sebestyen
 */
public class Translator {

    static String baseLomsURLString = "http://localisationmessagesvc.milan.hcom/messages/";
    static String ENG_LANGUAGE_KEY = "en_GB";
    static String FR_LANGUAGE_KEY = "fr_FR";

    public List<Output> urlCall() throws MalformedURLException, IOException {

        URL baseLomsURL = new URL(baseLomsURLString);

        List<Output> outputList = new ArrayList<Output>();
        List<String> readedKeys = keyReader(null);

        for (String key : readedKeys) {

            URL concatenatedOriginalURL = new URL(baseLomsURL + key);
            HashMap<String, String> originalResponseMap = hashMapRespons(concatenatedOriginalURL);

            Output newOutput = new Output();
            newOutput.setKeyName(key);
            String originalEnGbValue = originalResponseMap.get(ENG_LANGUAGE_KEY);

            //setOriginalValues
            if (originalEnGbValue != null) {
                if (originalEnGbValue.matches("(.*)hotel(.*)") || originalEnGbValue.matches("(.*)room(.*)")) {
                    newOutput.setOriginalContentEN_GB(originalEnGbValue);
                } else {
                    break;
                }
            }
            //setUnhotellingValues inputUnhotellingText
            URL unhotellingURL = new URL(concatenatedOriginalURL + ".unhotelling");

            HashMap<String, String> unhotellingResponseMap = hashMapRespons(unhotellingURL);
            String unhotellingEnGbValue = unhotellingResponseMap.get(ENG_LANGUAGE_KEY);
            String unhotellingFRValue = unhotellingResponseMap.get(FR_LANGUAGE_KEY);

            System.out.println(unhotellingResponseMap);

            if (unhotellingEnGbValue == null) {
                newOutput.setMissingUnhotellingKey(true);
            } else {
                if (!unhotellingEnGbValue.equals(originalEnGbValue)) {
                    newOutput.setMissingUnhotellingKey(true);
                }

                newOutput.setUnhotellingContentEN_GB(unhotellingEnGbValue);

                if (unhotellingFRValue != null) {
                    if (unhotellingEnGbValue.equals(unhotellingFRValue)) {
                        newOutput.setUnhotellingNotTranslated(true);
                    }
                }
            }

            //setUnhotellingPropertyKey
            URL unhotellingPropertyKeyURL = new URL(unhotellingURL + ".property");
            HashMap<String, String> unhotellingPropertyResponseMap = hashMapRespons(unhotellingPropertyKeyURL);
            String unhotellingPropertyEnGbValue = unhotellingPropertyResponseMap.get(ENG_LANGUAGE_KEY);

            if (unhotellingPropertyEnGbValue == null) {
                newOutput.setMissingUnhotellingKey(true);
            } else {
                newOutput.setUnhotellingPropertyContetnt(unhotellingPropertyEnGbValue);
            }

            setColorCode(newOutput);
            outputList.add(newOutput);
        }

        System.out.println(outputList.size());

        return outputList;
    }

    //File reader will be here
    public List<String> keyReader(String filePath) {

        List<String> readedKeys = new ArrayList<String>();
        String examplekey1 = "confirmation.crossSell.bookAgain.text";
        String examplekey2 = "confirmation.mail.greeting.prepay.with_installment";
        String examplekey3 = "booking.bookingForm.mandatoryFee.hotelCollected.hover.singleFee.crossCurrency.mandatoryFee";
        String examplekey4 = "fastbooking.submit.label.book.alipay.text";
        String examplekey5 = "confirmation.mail.wr_module.reduced_nightsfree_star";

        readedKeys.add(examplekey1);
        readedKeys.add(examplekey2);
        readedKeys.add(examplekey3);
        readedKeys.add(examplekey4);
        readedKeys.add(examplekey5);

        return readedKeys;
    }

    public HashMap<String, String> hashMapRespons(URL url) throws IOException {

        HttpURLConnection originalConnection = (HttpURLConnection) url.openConnection();
        originalConnection.setRequestMethod("GET");

        // int status = con.getResponseCode();

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

    private void setColorCode(Output output) {
        //GREEN
        if (output.isNoPropertyKey() == null && output.isUnhotellingNotTranslated() == null && output.isMissingUnhotellingKey() == null) {
            output.setColorCode(ColorCodes.GREEN);
        return;
        }

        //BROWN
        if ((output.isNoPropertyKey() != null || output.isUnhotellingNotTranslated() != null) && output.isMissingUnhotellingKey() == null) {
            output.setColorCode(ColorCodes.BROWN);
            return;
        }

        //RED
        if(output.isMissingUnhotellingKey() == true){
            output.setColorCode(ColorCodes.RED);
            return;
        }

    }
}
