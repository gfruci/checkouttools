package main.kotlin

import java.io.File
import java.security.cert.X509Certificate
import java.util.regex.Pattern
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

internal object Config {

    private const val CONFIG_SOURCE : String = "configSource"
    private const val PROJECT_ROOT : String = "projectRoot"
    private const val CREDENTIAL : String = "credential"
    internal const val REGEX_TEMPLATE : String = "(\\\"Property\\\"|is\\(\\\"Property\\\"\\)|is\\(Property\\)|\\\$\\{Property(\\:.*?)?\\}|configuration.is.Property|configuration.Property)";
    internal const val DEFAULT_PROPERTY_FILE = "env_rules_default.properties"

    internal var confSrc: String
    internal var projRoot : String
    internal var credential : String

    init {
        confSrc = System.getProperty(CONFIG_SOURCE)
        projRoot = System.getProperty(PROJECT_ROOT)
        credential = System.getProperty(CREDENTIAL)

        if (!File(confSrc).isDirectory || !File(projRoot).isDirectory) {
            throw IllegalStateException("Not a valid directory: $confSrc or $projRoot")
        }

        useInsecureSSL()
    }

    private fun useInsecureSSL() {

        // Create a trust manager that does not validate certificate chains
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate>? = null
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) = Unit
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) = Unit
        })

        val sc = SSLContext.getInstance("SSL")
        sc.init(null, trustAllCerts, java.security.SecureRandom())
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.socketFactory)

        // Create all-trusting host name verifier
        val allHostsValid = HostnameVerifier { _, _ -> true }

        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid)

    }
}