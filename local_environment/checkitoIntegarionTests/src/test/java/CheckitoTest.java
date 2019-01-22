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

import org.htmlcleaner.CleanerProperties;
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
public class CheckitoTest {
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final String JSON_TEST_DATA_FILE_NAME = "tests.json";
    private TestData testData;

    @BeforeClass
    private void setUp() {
        testData = loadTestData();
    }

    private TestData loadTestData() {
        String jsonTestData = loadResourceContent(JSON_TEST_DATA_FILE_NAME);

        return new Gson().fromJson(jsonTestData, TestData.class);
    }

    private String loadResourceContent(final String resourceName) {
        try {
            InputStream in = getClass().getClassLoader().getResourceAsStream(resourceName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append(LINE_SEPARATOR);
            }

            return stringBuilder.toString();
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load file:" + resourceName, ex);
        }
    }

    @Test(dataProvider = "dataProvider")
    public void CheckitoDataDrivenIntegrationTests(List<Command> preconditions, List<Command> steps, List<Assert> asserts) {
        handlePreconditions(preconditions);
        String pageContent = handleStepsAndLoadPage(steps);

        HtmlCleaner cleaner = new HtmlCleaner();
        TagNode htmlRootNode = cleaner.clean(pageContent);
        checkPage(htmlRootNode, asserts);
    }

    @DataProvider
    private Object[][] dataProvider() {
        return testData.getTests().stream()
            .map(x -> new Object[]{x.getPreconditions(), x.getSteps(), x.getAsserts()})
            .toArray(Object[][]::new);
    }

    private String handleStepsAndLoadPage(List<Command> steps) {
        Command pageOpen = steps.stream()
            .filter(command -> Operation.OPEN_URL.equals(command.getOperation()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("At least one OPEN_URL command should be within the steps"));

        return loadPageFromUrl(pageOpen.getUrl(), pageOpen.getRequestMethod(), pageOpen.getParameters());
    }

    private String loadPageFromUrl(String url, RequestMethod requestMethod, Map<String, String> parameters) {
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

    private void handlePreconditions(List<Command> preconditions) {

    }

    private void checkPage(TagNode page, List<Assert> asserts) {
        asserts.forEach(a -> {
            Object[] xPathResult = null;
            String actualValue = "";
            try {
                xPathResult = page.evaluateXPath(a.getxPath());
                if (xPathResult.length > 1) {
                    throw new RuntimeException(String.format("Xpath '%s' returns multiple nodes. This is not yet supported.", a.getxPath()));
                }

                actualValue = ((TagNode)xPathResult[0]).getText().toString();

            } catch (XPatherException e) {
                throw new RuntimeException(e);
            }
            org.testng.Assert.assertEquals(actualValue, a.getExpectedValue(), a.getMessage());
        });
    }
}
