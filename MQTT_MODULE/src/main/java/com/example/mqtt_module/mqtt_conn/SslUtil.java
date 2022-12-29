package com.example.mqtt_module.mqtt_conn;

import android.content.Context;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

public class SslUtil {
    private SslUtil() {
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static SSLSocketFactory getSSLSocketFactory( final String password,Context mContext)
            throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException,
            KeyManagementException, UnrecoverableKeyException, InvalidKeySpecException {
        /**
         * Add BouncyCastle as a Security Provider
         */


        try {
            Security.addProvider(new BouncyCastleProvider());

            JcaX509CertificateConverter certificateConverter = new JcaX509CertificateConverter().setProvider("BC");

            /**
             * Load Certificate Authority (CA) certificate
             */
            SessionManager sessionManager = new SessionManager(mContext);
            X509Certificate caCert = null;
            InputStream fis = new ByteArrayInputStream(sessionManager.getCertificate().get(SessionManager.CA_CRT).getBytes());
            // InputStream fis = mcontext.getResources().openRawResource(R.raw.ca);
            BufferedInputStream clientBis = new BufferedInputStream(fis);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            while (clientBis.available() > 0) {
                caCert = (X509Certificate) cf.generateCertificate(clientBis);
            }

            //X509Certificate cert = certificateConverter.getCertificate(certHolder);
            InputStream ClientInputstream = new ByteArrayInputStream(sessionManager.getCertificate().get(SessionManager.CLIENT_CRT).getBytes());
            clientBis = new BufferedInputStream(ClientInputstream);
            X509Certificate clientCert = null;
            while (clientBis.available() > 0) {
                clientCert = (X509Certificate) cf.generateCertificate(clientBis);
            }
            /**
             * Load client private key
             */

            /**
             * CA certificate is used to authenticate server
             */
            KeyStore caKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            caKeyStore.load(null, null);
            caKeyStore.setCertificateEntry("ca-certificate", caCert);

            /**
             * Client key and certificates are sent to server so it can authenticate the
             * client. (server send CertificateRequest message in TLS handshake step).
             */
            InputStream fiskeyFile = new ByteArrayInputStream(sessionManager.getCertificate().get(SessionManager.CLIENT_KEY).getBytes());
           // InputStream fiskeyFile = m.getResources().openRawResource(R.raw.client_keys);
            //  bis = new BufferedInputStream(fiskeyFile);
            Log.e( "getSSLSocketFactory: ", fiskeyFile.toString());
            InputStreamReader isReader = new InputStreamReader(fiskeyFile);
            BufferedReader reader = new BufferedReader(isReader);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                // skip "-----BEGIN/END RSA PRIVATE KEY-----"
                if (!line.startsWith("--") || !line.endsWith("--")) {
                    sb.append(line);
                }
            }
            KeyStore clientKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            clientKeyStore.load(null, null);
            clientKeyStore.setCertificateEntry("certificate", clientCert);
            clientKeyStore.setKeyEntry("private-key", readPrivateKeyFromString(sb.toString()), password.toCharArray(), new Certificate[] { clientCert });

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(clientKeyStore, password.toCharArray());

            /**
             * Create SSL socket factory
             */
            SSLContext context = SSLContext.getInstance("TLSv1.2");
           /* context.init(keyManagerFactory.getKeyManagers(),
                    getUnsafeTrustManagers(caKeyStore) , null);*/

            context.init(keyManagerFactory.getKeyManagers(),
                    new TrustManager[]{new EasyX509TrustManager(
                            caKeyStore)}, new SecureRandom());

            /**
             * Return the newly created socket factory object
             */
            return context.getSocketFactory();
        } catch(CertificateException originalException) {
            Log.e( "checkServhori", originalException.getMessage());
            throw originalException;
        }
    }

    public static PrivateKey readPrivateKeyFromString(String privateKeyData) throws InvalidKeySpecException,
            NoSuchAlgorithmException, IOException {
        Log.e( "reaeKeyFromString: ", privateKeyData);
        byte[] keyBytes = Base64.decode(privateKeyData, Base64.DEFAULT);

        // Get private Key
        KeyFactory fact = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);

        PrivateKey privateKey = fact.generatePrivate(pkcs8EncodedKeySpec);

        return privateKey;
    }

}