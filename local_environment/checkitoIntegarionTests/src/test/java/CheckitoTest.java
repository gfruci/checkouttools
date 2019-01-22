import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

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
    private HttpClient httpClient = new HttpClient();
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

        return httpClient.loadPageFromUrl(pageOpen.getUrl(), pageOpen.getRequestMethod(), pageOpen.getParameters());
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
