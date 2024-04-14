package com.planx.xchat.retrofit.request;

public class SearchRequest {
    private String strSearch;

    public SearchRequest(String strSearch) {
        this.strSearch = strSearch;
    }

    public String getStrSearch() {
        return strSearch;
    }

    public void setStrSearch(String strSearch) {
        this.strSearch = strSearch;
    }
}
