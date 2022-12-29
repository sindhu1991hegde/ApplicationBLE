
package com.example.applicationble.views.AppUtil;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {



    @SerializedName("certificates")
    @Expose
    private Certificates certificates;

    public Certificates getCertificates() {
        return certificates;
    }

    public void setCertificates(Certificates certificates) {
        this.certificates = certificates;
    }
}
