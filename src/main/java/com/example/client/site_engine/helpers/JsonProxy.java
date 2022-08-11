package com.example.client.site_engine.helpers;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class JsonProxy<T> {
    private final LinkedHashMap<String, T> proxyMap = new LinkedHashMap<>();
    public JsonProxy(ArrayList<String> keySet){
        for(String key : keySet){
            Object object = new Object();
            proxyMap.put(key, (T) object);
        }
    }
    public boolean acceptKey(String key, T value){
        boolean isExist = proxyMap.containsKey(key);
        if(isExist) proxyMap.replace(key, value);
        return isExist;
    }
    public boolean accept(JsonPair pair){
        boolean isExist = proxyMap.containsKey(pair.getKey());
        if(isExist) proxyMap.replace((String) pair.getKey(), (T) pair.getValue());
        return isExist;
    }
    public boolean exists(String key){
        return proxyMap.containsKey(key);
    }

    public T getByKey(String key){
        if(proxyMap.containsKey(key))
        return proxyMap.get(key);
        return null;
    }
    public LinkedHashMap<String, T> getProxyMap(){
        return (LinkedHashMap<String, T>) proxyMap.clone();
    }

    @Override
    public String toString() {
        return "JsonProxy{" +
                "proxyMap=" + proxyMap +
                '}';
    }
}
