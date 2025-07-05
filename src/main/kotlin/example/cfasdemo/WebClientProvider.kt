package example.cfasdemo

import io.netty.handler.ssl.SslContext
import io.netty.handler.ssl.SslContextBuilder
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo
import org.bouncycastle.cert.X509CertificateHolder
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
import org.slf4j.LoggerFactory
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.io.File
import java.io.FileReader
import java.security.PrivateKey
import java.security.Security
import java.security.cert.X509Certificate
import java.util.concurrent.atomic.AtomicReference

private val log = LoggerFactory.getLogger(WebClientProvider::class.java)

@Service
class WebClientProvider(
    cfEnv: CfEnv,
    private val builder: WebClient.Builder
) {
    private val webClientRef = AtomicReference<WebClient>()
    private val certificatesFile = File(cfEnv.cfInstanceCert)
    private val privateKeyFile = File(cfEnv.cfInstanceKey)
    private var certLastModified = -1L

    fun getWebClient(): WebClient {
        // certificates are rotated on daily basis, recreate webClient when changed
        if (certificatesFile.lastModified() != certLastModified) {
            log.info("building ssl context")
            certLastModified = certificatesFile.lastModified()
            val sslContext = Utils.buildSslContext(certificatesFile, privateKeyFile)
            val httpClient = HttpClient.create()
                .secure {
                    it.sslContext(sslContext)
                }
            val webClient = builder
                .clientConnector(ReactorClientHttpConnector(httpClient))
                .build()
            webClientRef.set(webClient)
        }
        return webClientRef.get()
    }

    object Utils {
        init {
            if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
                Security.addProvider(BouncyCastleProvider())
            }
        }

        fun buildSslContext(certificatesFile: File, privateKeyFile: File): SslContext {
            val privateKey = loadPrivateKeyFromPem(privateKeyFile)
            val certificates = loadCertificates(certificatesFile)
            return SslContextBuilder.forClient()
                .keyManager(privateKey, certificates)
                .build()
        }

        private fun loadPrivateKeyFromPem(privateKeyFile: File): PrivateKey {
            FileReader(privateKeyFile).use { fileReader ->
                PEMParser(fileReader).use { pemParser ->
                    val obj = pemParser.readObject()
                    val converter = JcaPEMKeyConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME)
                    val privateKey: PrivateKey = when (obj) {
                        is PrivateKeyInfo -> {
                            converter.getPrivateKey(obj)
                        }
                        is org.bouncycastle.openssl.PEMKeyPair -> {
                            converter.getPrivateKey(obj.privateKeyInfo)
                        }

                        else -> {
                            throw IllegalArgumentException("Unsupported private key format: ${obj?.javaClass?.name}")
                        }
                    }
                    return privateKey
                }
            }
        }

        private fun loadCertificates(file: File): List<X509Certificate> {
            val certs = mutableListOf<X509Certificate>()
            FileReader(file).use { reader ->
                PEMParser(reader).use { parser ->
                    var obj: Any?
                    while (parser.readObject().also { obj = it } != null) {
                        when (obj) {
                            is X509CertificateHolder -> {
                                certs.add(
                                    JcaX509CertificateConverter()
                                        .setProvider(BouncyCastleProvider.PROVIDER_NAME)
                                        .getCertificate(obj as X509CertificateHolder)
                                )
                            }
                            else -> {
                                log.info("Skipping unexpected object in certificate file: ${obj?.javaClass?.name}")
                            }
                        }
                    }
                }
            }
            return certs
        }
    }
}
