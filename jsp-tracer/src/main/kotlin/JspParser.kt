import java.io.File

class JspParser(val jspFactory: JspFileFactory) {

    private val JSP_INCLUDE_REGEX = Regex("\\/WEB-INF.*\\.(jspf?|tag)")
    private val MOBILE_CONFIG_REGEX = Regex("\\\$\\{configuration\\.MOBILE_CHANNEL_TYPE}")

    fun findAllJspFiles(): Set<JspFile> =
        File(jspFactory.jspPathPrefix).walk()
            .filter { it.isFile && it.extension in setOf("jsp", "jspf", "tag") }
            .map { jspFactory.create(it) }
            .toSet()

    /**
     * Returns all JSP files reachable from the parameter set of JSPs (including the params)
     */
    fun findJspReferencesRecursive(jspFiles: Iterable<JspFile>): Set<JspFile> {
        return jspFiles
            .filter { it.fileExists() }
            .map { findIncludesInFile(it) }
            .map { findJspReferencesRecursive(it) }
            .flatten()
            .union(jspFiles)
            .toSet()
    }

    /**
     * Finds other JSP files included in the parameter JSP.
     * Sets [JspFile.references] field on the parameter.
     */
    fun findIncludesInFile(jsp: JspFile): Set<JspFile> {
        val content = File(jsp.file).readText()
        jsp.references = JSP_INCLUDE_REGEX.findAll(content)
            .map { jspFactory.create(it.groupValues[0]) }
            .toSet()
            .expandConfigurationPaths()

        return jsp.references
    }

    /**
     * Expands the ${configuration.MOBILE_CHANNEL_TYPE} string in JSP filenames
     * to paths containing 'mobile' and 'tablet'. Returns a new set.
     */
    fun Set<JspFile>.expandConfigurationPaths(): Set<JspFile> {
        var newSet = HashSet<JspFile>()
        this.forEach {
            if (MOBILE_CONFIG_REGEX.containsMatchIn(it.file)) {
                newSet.add(JspFile(MOBILE_CONFIG_REGEX.replace(it.file, "tablet")))
                newSet.add(JspFile(MOBILE_CONFIG_REGEX.replace(it.file, "mobile")))
            } else {
                newSet.add(it)
            }
        }
        return newSet
    }
}