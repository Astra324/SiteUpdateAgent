package com.example.client.site_engine;


import com.example.client.model.TagMap;

import java.util.ArrayList;

public interface SiteView {
    public <T> T view(ArrayList<TagMap> tagMap);


}
