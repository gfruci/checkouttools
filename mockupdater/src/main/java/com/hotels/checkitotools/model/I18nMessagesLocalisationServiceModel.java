package com.hotels.checkitotools.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Marton_Kadar on 2017-06-26.
 *
 * Gson model for localisationsvc/messages response
 */
public class I18nMessagesLocalisationServiceModel {
    public String getEnUS() {
        return enUS;
    }

    public void setEnUS(String enUS) {
        this.enUS = enUS;
    }

    @SerializedName("en_US")
    private String enUS;
}
