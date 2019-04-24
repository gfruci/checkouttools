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



    public List<Output> urlCall() throws MalformedURLException, IOException {

        URL baseLomsURL = new URL("http://localisationmessagesvc.milan.hcom/messages/");

        List<Output> outputList = new ArrayList<Output>();
        List<String> readedKeys = keyReader(null);



        for (String key : readedKeys) {

            URL concatenatedOriginalURL = new URL(baseLomsURL + key);
            HashMap<String,String> hashMapResponse = hashMapRespons(concatenatedOriginalURL);

            Output newOutput = new Output();
            newOutput.setKeyName(key);

            String originalEnGbValue = hashMapResponse.get("en_GB");

            System.out.println("original en_GB line: " + originalEnGbValue);

            if (originalEnGbValue.matches("(.*)hotel(.*)") || originalEnGbValue.matches("(.*)room(.*)")) {
                System.out.println("en_GB line contains 'hotel or room word': ");
                newOutput.setOriginalContentEN_GB(originalEnGbValue);
            } else {
                outputList.add(newOutput);
                break;
            }



            //inputUnhotellingText
            URL unhotellingURL = new URL(concatenatedOriginalURL + "unhotelling");
            HttpURLConnection unhotellingConnection = (HttpURLConnection) concatenatedOriginalURL.openConnection();

        //    if(){

                //newOutput.setMissingUnhotellingKey(true);
         //   }

            outputList.add(newOutput);
        }
        return outputList;
    }


    //File reader will be here
    public List<String> keyReader(String filePath){

        List<String> readedKeys = new ArrayList<String>();
        String examplekey = "confirmation.crossSell.bookAgain.text";
        readedKeys.add(examplekey);

        return readedKeys;
    }


    public HashMap<String, String> hashMapRespons(URL url) throws IOException{

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
        HashMap<String,String> hashMapResponse = new Gson().fromJson(response, HashMap.class);
        return hashMapResponse;
    }

}
