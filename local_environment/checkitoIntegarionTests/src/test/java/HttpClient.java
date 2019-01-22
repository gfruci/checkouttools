import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.gson.Gson;

/**
 * @author Zsolt_Saskovy
 */
public class HttpClient {
    protected String loadPageFromUrl(String url, RequestMethod requestMethod, Map<String, String> parameters) {
        acceptSelfSignedHTTPSCertificate();

        try {
            String urlWithRequestParameters = url;
            if (requestMethod == RequestMethod.GET) {
                urlWithRequestParameters += "?" + getParamsString(parameters);
            }

            HttpURLConnection connection = (HttpURLConnection) new URL(urlWithRequestParameters).openConnection();
            connection.setRequestMethod(requestMethod.toString());
            connection.setInstanceFollowRedirects(true);

            if (requestMethod == RequestMethod.POST) {
                connection.setDoOutput(true);
                DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                dataOutputStream.writeBytes(getParamsString(parameters));
                dataOutputStream.flush();
                dataOutputStream.close();
            }

            int responseCode = connection.getResponseCode();
            StringBuilder content = new StringBuilder();
            String inputLine;

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();
            connection.disconnect();
            return content.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void acceptSelfSignedHTTPSCertificate() {
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
                }
                public void checkServerTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
        };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        } catch (Exception e) {
        }
    }

    private String getParamsString(Map<String, String> params) {
        StringBuilder result = new StringBuilder();

        params.forEach((key, value) -> {
            if (result.length() > 0) {
                result.append("&");
            }

            try {
                result.append(URLEncoder.encode(key, "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(value, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        });

        return result.toString();
    }
}
