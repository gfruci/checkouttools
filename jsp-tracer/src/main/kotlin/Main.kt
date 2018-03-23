import java.io.File

fun main(args: Array<String>) {
    val jspFileFactory = JspFileFactory(Config.jspPathPrefix)
    val xmlParser = XmlParser(Config.webSrc, jspFileFactory)
    val jspParser = JspParser(jspFileFactory)

    var allJspFiles = jspParser.findAllJspFiles()
    var xmlFiles = xmlParser.readXMLFiles()
    var rootJspFiles = xmlFiles.flatMap { it.references }.filter { it.fileExists() }

    var reachableJsps = jspParser.findJspReferencesRecursive(rootJspFiles)
    var orphanedJsps = allJspFiles.subtract(reachableJsps)


    println("The following XML files contain references to non-existing JSP files:")
    xmlParser.printNonExistingJsps(xmlFiles)

    println("\n#################################\n")

    println("The following JSP files are unreachable: (${orphanedJsps.size})")
    orphanedJsps.forEach { println(it.file) }

    if (args.contains("-D") || args.contains("--delete")) {
        orphanedJsps.forEach {
            var file = File(it.file)
            file.delete()
        }
        println("\nDeleted ${orphanedJsps.size} unreachable JSP files")
    }
}

object Config {
    var jspPathPrefix: String
    var webSrc: String

    init {
        jspPathPrefix = System.getProperty("jspPathPrefix")
        webSrc = System.getProperty("webSrc")

        for (file in setOf(jspPathPrefix, webSrc)) {
            if (!File(file).isDirectory) {
                throw IllegalStateException("Not a valid directory: ${file}")
            }
        }
    }
}