package com.hotels.checkitotools.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Marton_Kadar on 2017-06-26.
 *
 * Gson model for localisationsvc/messages response
 */
public class I18nMessagesLocalisationServiceModel {
    @SerializedName("en_US")
    private String enUS;

    public String getEnUS() {
        return enUS;
    }

    public void setEnUS(final String enUS) {
        this.enUS = enUS;
    }
}
