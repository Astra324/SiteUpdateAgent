package com.example.client.config;

import com.example.client.model.CatalogItem;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class CatalogConfig {
    @Bean
    public ConcurrentHashMap<String, CatalogItem> catalogMap(){
        return new ConcurrentHashMap<String, CatalogItem>();
    }
}
