package com.example.mqtt_module.mqtt_conn;

import android.util.Log;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class EasyX509TrustManager
        implements X509TrustManager {

    private X509TrustManager standardTrustManager = null;

    public EasyX509TrustManager(KeyStore keystore)
            throws NoSuchAlgorithmException, KeyStoreException {
        super();
        TrustManagerFactory factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        factory.init(keystore);
        TrustManager[] trustmanagers = factory.getTrustManagers();
        if (trustmanagers.length == 0) {
            throw new NoSuchAlgorithmException("no trust manager found");
        }
        this.standardTrustManager = (X509TrustManager) trustmanagers[0];
    }

    public void checkServerTrusted(X509Certificate[] certificates, String authType)
            throws CertificateException {
       // Log.e( "checkServerTrusted: ", certificates+"-----"+authType);
        if ((certificates != null) && (certificates.length == 1)) {
            Log.e( "checkServerTrusted: ", certificates+"-----"+authType);
            certificates[0].checkValidity();
        }
        Log.e( "checkServerTrusted1: ", certificates+"-----"+authType);
        try {
            standardTrustManager.checkServerTrusted(certificates, authType);
        } catch(CertificateException originalException) {
            Log.e( "checkServerTrusted2: ", certificates+"-----"+originalException);
           /* try {
                X509Certificate[] reorderedChain = reorderCertificateChain(chain);
                CertPathValidator validator = CertPathValidator.getInstance("PKIX");
                CertificateFactory factory = CertificateFactory.getInstance("X509");
                CertPath certPath = factory.generateCertPath(Arrays.asList(reorderedChain));
                PKIXParameters params = new PKIXParameters(trustStore);
                params.setRevocationEnabled(false);
                validator.validate(certPath, params);
            } catch(Exception ex) {
                throw originalException;
            }*/
        }
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        standardTrustManager.checkClientTrusted(chain, authType);
    }

    public X509Certificate[] getAcceptedIssuers() {
        return this.standardTrustManager.getAcceptedIssuers();
    }

}