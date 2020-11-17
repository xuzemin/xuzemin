package com.ctv.settings.about.Bean;

public class AboutTvInfo {

    private String aboutString;

    private String aboutValue;

    private String visibility = "visible";// gone or visible

    public AboutTvInfo() {
        super();
        // TODO Auto-generated constructor stub
    }

    public AboutTvInfo(String aboutString, String aboutValue, String visibility) {
        super();
        this.aboutString = aboutString;
        this.aboutValue = aboutValue;
        this.visibility = visibility;
    }

    @Override
    public String toString() {
        return "AboutTvInfo [aboutString=" + aboutString + ", aboutValue=" + aboutValue + "]";
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        if (visibility == null || visibility == "") {
            return;
        }
        this.visibility = visibility;
    }

    public String getAboutString() {
        return aboutString;
    }

    public void setAboutString(String aboutString) {
        this.aboutString = aboutString;
    }

    public String getAboutValue() {
        return aboutValue;
    }

    public void setAboutValue(String aboutValue) {
        this.aboutValue = aboutValue;
    }


}
