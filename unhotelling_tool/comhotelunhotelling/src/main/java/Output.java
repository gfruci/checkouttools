/**
 * @author Nandor_Sebestyen
 */
public class Output {
    String keyName;
    Boolean missingUnhotellingKey;
    Boolean differentUnhotellingText;
    Boolean noPropertyKey;
    Boolean unhotellingNotTranslated;
    String originalContentEN_GB;
    String unhotellingContentEN_GB;
    String unhotellingPropertyContent;

    ColorCodes colorCode;

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

    public Boolean isUnhotellingNotTranslated() {
        return unhotellingNotTranslated;
    }

    public void setUnhotellingNotTranslated(Boolean unhotellingNotTranslated) {
        this.unhotellingNotTranslated = unhotellingNotTranslated;
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

    public String getUnhotellingPropertyContetnt() {
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

    @Override
    public String toString() {
        return "Output{" + "keyName='" + keyName + '\'' + ", missingUnhotellingKey=" + missingUnhotellingKey + ", differentUnhotellingText='"
            + differentUnhotellingText + '\'' + ", noPropertyKey=" + noPropertyKey + ", unhotellingNotTranslated=" + unhotellingNotTranslated
            + ", originalContentEN_GB='" + originalContentEN_GB + '\'' + ", unhotellingContentEN_GB='" + unhotellingContentEN_GB + '\''
            + ", unhotellingPropertyContent='" + unhotellingPropertyContent + '\'' + ", colorCode=" + colorCode + '}';
    }
}
