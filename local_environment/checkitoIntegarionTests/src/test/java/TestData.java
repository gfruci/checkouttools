import java.util.List;
import java.util.Map;

/**
 * @author Zsolt_Saskovy
 */
public class TestData {
    public List<Test> getTests() {
        return tests;
    }

    public void setTests(List<Test> tests) {
        this.tests = tests;
    }

    private List<Test> tests;
}


class Test {
    private String testName;
    private List<Command> preconditions;
    private List<Command> steps;
    private List<Assert> asserts;

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public List<Command> getPreconditions() {
        return preconditions;
    }

    public void setPreconditions(List<Command> preconditions) {
        this.preconditions = preconditions;
    }

    public List<Command> getSteps() {
        return steps;
    }

    public void setSteps(List<Command> steps) {
        this.steps = steps;
    }

    public List<Assert> getAsserts() {
        return asserts;
    }

    public void setAsserts(List<Assert> asserts) {
        this.asserts = asserts;
    }
}

enum RequestMethod {
    GET, POST
}
class Command {
    private Operation operation;
    private String url;
    private RequestMethod requestMethod;
    private Map<String, String> parameters;

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(RequestMethod requestMethod) {
        this.requestMethod = requestMethod;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }
}

enum Operation {
    OPEN_URL
}

class Assert {
    private String jsonPath;
    private String expectedValue;
    private String message;

    public String getJsonPath() {
        return jsonPath;
    }

    public void setJsonPath(String jsonPath) {
        this.jsonPath = jsonPath;
    }

    public String getExpectedValue() {
        return expectedValue;
    }

    public void setExpectedValue(String expectedValue) {
        this.expectedValue = expectedValue;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
