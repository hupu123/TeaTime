package com.hugh.teatime.models.gasoline;

public class IllustrationBean {
    private int colorID;
    private String illustration;

    public IllustrationBean(int colorID, String illustration) {
        this.colorID = colorID;
        this.illustration = illustration;
    }

    public int getColorID() {
        return colorID;
    }

    public void setColorID(int colorID) {
        this.colorID = colorID;
    }

    public String getIllustration() {
        return illustration;
    }

    public void setIllustration(String illustration) {
        this.illustration = illustration;
    }
}
