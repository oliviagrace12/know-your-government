package com.oliviarojas.knowyourgovernment;

public class Office {

    private String officeTitle;
    private String officialName;

    public Office(String officeTitle, String officialName) {
        this.officeTitle = officeTitle;
        this.officialName = officialName;
    }

    public Office() {
    }

    public String getOfficeTitle() {
        return officeTitle;
    }

    public void setOfficeTitle(String officeTitle) {
        this.officeTitle = officeTitle;
    }

    public String getOfficialName() {
        return officialName;
    }

    public void setOfficialName(String officialName) {
        this.officialName = officialName;
    }
}
