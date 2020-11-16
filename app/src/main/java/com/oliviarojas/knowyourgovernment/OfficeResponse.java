package com.oliviarojas.knowyourgovernment;

import java.util.List;

public class OfficeResponse {

    private String name;
    private List<Integer> officialIndices;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getOfficialIndices() {
        return officialIndices;
    }

    public void setOfficialIndices(List<Integer> officialIndices) {
        this.officialIndices = officialIndices;
    }
}
