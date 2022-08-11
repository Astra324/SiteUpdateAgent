package com.example.client.site_engine.helpers;

import org.json.JSONException;

public interface JsonExtractFunction <R>{
    public R extract(String inputJasonFormatString, R targetJson) throws JSONException;
}
