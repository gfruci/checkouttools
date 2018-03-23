import java.io.File

class JspFileFactory(val jspPathPrefix: String) {

    fun create(fileName: String): JspFile = JspFile(normalizePath(jspPathPrefix + fileName))
    fun create(file: File): JspFile = JspFile(normalizePath(file.canonicalPath))

    private fun normalizePath(path: String): String = path.replace("\\", "/")
}

data class JspFile(val file: String) {
    var references: Set<JspFile> = HashSet()

    fun fileExists(): Boolean = File(this.file).isFile

    fun printTreeRecursive(currDepth: Int = 0) {
        println("${"\t".repeat(currDepth)} ${if (fileExists()) "" else "INVALID" } ${file}")
        references.forEach { it.printTreeRecursive(currDepth + 1) }
    }

    override fun equals(other: Any?): Boolean {
        return (other is JspFile && this.file.equals(other.file, ignoreCase = true))
    }

    override fun hashCode(): Int {
        return file.toUpperCase().hashCode()
    }
}

data class XmlFile(val file: String, val references: Set<JspFile>)