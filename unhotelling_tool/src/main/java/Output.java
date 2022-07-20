/**
 *
 */
public class Output {

    private String keyName;
    private Boolean missingUnhotellingKey;
    private Boolean differentUnhotellingText;
    private Boolean noPropertyKey;
    private Boolean unhotellingPropertyNotTranslated;
    private String originalContentEN_GB;
    private String unhotellingContentEN_GB;
    private String unhotellingPropertyContent;
    private ColorCodes colorCode;

    public ColorCodes getColorCode() {
        return colorCode;
    }

    public void setColorCode(ColorCodes colorCode) {
        this.colorCode = colorCode;
    }

    public Boolean isMissingUnhotellingKey() {
        return missingUnhotellingKey;
    }

    public void setMissingUnhotellingKey(Boolean missingUnhotellingKey) {
        this.missingUnhotellingKey = missingUnhotellingKey;
    }

    public Boolean isDifferentUnhotellingText() {
        return differentUnhotellingText;
    }

    public void setDifferentUnhotellingText(Boolean differentUnhotellingText) {
        this.differentUnhotellingText = differentUnhotellingText;
    }

    public Boolean isNoPropertyKey() {
        return noPropertyKey;
    }

    public void setNoPropertyKey(Boolean noPropertyKey) {
        this.noPropertyKey = noPropertyKey;
    }

    public Boolean isUnhotellingPropertyNotTranslated() {
        return unhotellingPropertyNotTranslated;
    }

    public void setUnhotellingPropertyNotTranslated(Boolean unhotellingPropertyNotTranslated) {
        this.unhotellingPropertyNotTranslated = unhotellingPropertyNotTranslated;
    }

    public String getOriginalContentEN_GB() {
        return originalContentEN_GB;
    }

    public void setOriginalContentEN_GB(String originalContentEN_GB) {
        this.originalContentEN_GB = originalContentEN_GB;
    }

    public String getUnhotellingContentEN_GB() {
        return unhotellingContentEN_GB;
    }

    public void setUnhotellingContentEN_GB(String unhotellingContentEN_GB) {
        this.unhotellingContentEN_GB = unhotellingContentEN_GB;
    }

    public String getUnhotellingPropertyContent() {
        return unhotellingPropertyContent;
    }

    public void setUnhotellingPropertyContetnt(String unhotellingPropertyContetnt) {
        this.unhotellingPropertyContent = unhotellingPropertyContetnt;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }
}
