import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * This goal will run the main
 *
 * @goal translate
 */
@Mojo(name = "translate")
public class Main extends AbstractMojo {

    public static void main(String[] args) {

        Translator t = new Translator();

        try {
            List<Output> outputs = t.urlCall();
            ExcelCreator.saveToExcel(outputs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        this.main(null);
    }
}
