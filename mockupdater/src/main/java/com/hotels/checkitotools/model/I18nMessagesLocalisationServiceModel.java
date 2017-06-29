package com.hotels.checkitotools.model;

import com.google.gson.annotations.SerializedName;

/**
 * Gson model for localisationsvc/messages response
 *
 * @author Marton_Kadar
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
