import java.io.File

class XmlParser(val webSrc: String, val jspFactory: JspFileFactory) {

    private val XML_JSP_REGEX = Regex("\\/WEB-INF.*\\.(jspf?|tag)")

    /**
     * Reads XML files under the directory, collects the references to JSP files,
     * returns the result in a set of [XmlFile]
     */
    fun readXMLFiles(): Set<XmlFile> {
        var xmlFiles = HashSet<XmlFile>()

        File(webSrc).walk()
            .filter { it.isFile && it.extension in setOf("xml", "tld") }
            .forEach {
                val content = File(it.canonicalPath).readText()
                var jspIncludes = XML_JSP_REGEX.findAll(content).map { it.groupValues[0] }.toSet()

                if (jspIncludes.size > 0) {
                    var jspRefs = jspIncludes.map { jspFactory.create(it) }.toSet()
                    xmlFiles.add(XmlFile(it.canonicalPath, jspRefs))
                }
            }
        return xmlFiles
    }

    /**
     * Prints all XML files with references to JSPs that do not exist
     */
    fun printNonExistingJsps(xmlFiles: Iterable<XmlFile>) {
        var nonExistingFiles = xmlFiles
            .flatMap { it.references }
            .filter { !it.fileExists() }
            .toSet()

        xmlFiles
            .filter { it.references.any { it in nonExistingFiles } }
            .forEach {
                println(it.file)
                it.references.filter { it in nonExistingFiles }.forEach {
                    println("   ${it.file.split("webapp")[1]}") }
            }
    }
}