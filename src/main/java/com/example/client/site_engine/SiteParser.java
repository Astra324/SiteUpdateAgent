package com.example.client.site_engine;

import com.example.client.model.TagMap;
import org.json.JSONException;
import org.jsoup.nodes.Document;

import java.util.ArrayList;

public interface SiteParser {
    public  <T> T parseDoc(Document doc) throws JSONException;
    public ArrayList<TagMap> getArticleMap();
    public <T> T test(Document document, String pattern);
    public default void reindexMap(ArrayList<TagMap> targetMap){
        int index = 0;
        for(TagMap tm : targetMap){
            tm.setCatalogIndex(index);
            index++;
        }
    }
}
