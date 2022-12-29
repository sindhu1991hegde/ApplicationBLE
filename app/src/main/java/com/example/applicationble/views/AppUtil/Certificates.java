package com.example.applicationble.views.AppUtil;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Certificates {

    @SerializedName("ca_crt")
    @Expose
    private String caCrt;
    @SerializedName("client_crt")
    @Expose
    private String clientCrt;
    @SerializedName("client_key")
    @Expose
    private String clientKey;

    public String getCaCrt() {
        return caCrt;
    }

    public void setCaCrt(String caCrt) {
        this.caCrt = caCrt;
    }

    public String getClientCrt() {
        return clientCrt;
    }

    public void setClientCrt(String clientCrt) {
        this.clientCrt = clientCrt;
    }

    public String getClientKey() {
        return clientKey;
    }

    public void setClientKey(String clientKey) {
        this.clientKey = clientKey;
    }

}
