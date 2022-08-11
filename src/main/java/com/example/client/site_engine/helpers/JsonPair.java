package com.example.client.site_engine.helpers;

import java.util.Objects;

public class JsonPair <K, V>{
    private K key = null;
    private V value = null;
    private Class type;
    public JsonPair(){

    }
    public JsonPair(K key, V value){
        this.key = key;
        this.value = value;

    }
    public K getKey() {return (K) key;}

    public  JsonPair setKey(K key) {
        this.key = key;
        return this;
    }
    public  V getValue() {
        return (V)value;
    }

    public  JsonPair setValue(V value) {
        this.value = value;
        return this;
    }
    @Override
    public String toString() {
        return "{\"" + key + "\":" + value + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonPair<?, ?> jsonPair = (JsonPair<?, ?>) o;
        return Objects.equals(key, jsonPair.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    public Class getType() {
        return type;
    }

    public JsonPair setType(Class type) {
        this.type = type;
        return this;
    }
}
