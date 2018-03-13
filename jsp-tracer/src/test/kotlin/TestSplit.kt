import org.junit.Test
import kotlin.test.assertEquals

class TestJspParser {

    val TEST_JSP_DIR = this::class.java.getResource("testjsp").file
    val INDEX_JSP = this::class.java.getResource("testjsp/WEB-INF/index.jsp").file

    val JSP_PARSER = JspParser(JspFileFactory(TEST_JSP_DIR))

    @Test fun `should read all test jsp files`() {
        var readFiles = JSP_PARSER.findAllJspFiles().asFileNames()
        assertEquals(setOf("index.jsp", "includeA1.jsp", "includeA2.jsp", "orphan.jsp", "includeB.jsp", "withConfiguration.jsp"), readFiles)
    }

    @Test fun `should find two references in index file`() {
        var jsps = JSP_PARSER.findIncludesInFile(JspFile(INDEX_JSP)).asFileNames()
        assertEquals(setOf("includeA1.jsp", "includeB.jsp"), jsps)
    }

    @Test fun `should return all reachable jsp files`() {
        val index = JspFile(INDEX_JSP)
        val walkedFiles = JSP_PARSER.findJspReferencesRecursive(setOf(index)).asFileNames()
        assertEquals(setOf("index.jsp", "includeA1.jsp", "includeA2.jsp", "includeB.jsp", "non-existent.jsp"), walkedFiles)
    }

    @Test fun `should expand mobile configuration as two jsps`() {
        val withConfiguration = JspFile(TEST_JSP_DIR + "/WEB-INF/withConfiguration.jsp")
        val references = JSP_PARSER.findIncludesInFile(withConfiguration)

        val expected = setOf(
                JspFile(TEST_JSP_DIR + "/WEB-INF/tablet/includeA2.jsp"),
                JspFile(TEST_JSP_DIR + "/WEB-INF/mobile/includeA2.jsp")
        )

        assertEquals(expected, references)
    }

    private fun Iterable<JspFile>.asFileNames() = this.map { it.file }
            .map {
                val parts = it.split(Regex("\\\\|/"))
                parts[parts.size - 1]
            }.toSet()
}