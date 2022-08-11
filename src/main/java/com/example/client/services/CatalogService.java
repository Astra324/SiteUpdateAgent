package com.example.client.services;

import com.example.client.model.CatalogItem;
import com.example.client.repo.CatalogEntityManager;
import com.example.client.repo.CatalogRepository;
import com.example.client.site_engine.SiteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Service
public class CatalogService {
    @Autowired
    CatalogEntityManager catalogQuery;
    @Autowired
    CatalogRepository repository;
    @Autowired
    ConcurrentHashMap catalogMap;
    @Autowired
    SiteService siteService;
    @Autowired
    ExecutorService fixedThreadPool;
    public HashMap<SiteBuilder, List<CatalogItem>> getMapBySites(long startIndex, int limit){
        var resultMap = new HashMap<SiteBuilder, List<CatalogItem>>();
        var sites = siteService.getSitesList();
        List<CatalogItem> data = (ArrayList<CatalogItem>) catalogMap.values().stream().collect(Collectors.toList());
        for(SiteBuilder site : sites){
            resultMap.put(site, data.stream()
                    .filter((e)->e.getSiteName().equals(site.getName()))
                    .sorted((e1,e2)->e2.getTimestamp().compareTo(e1.getTimestamp()))
                    .skip(startIndex).limit(limit)
                    .collect(Collectors.toList()));
        }
        return resultMap;
    }
    public List<CatalogItem> getList(int startIndex, int limit){
        if(catalogMap.size() == 0) loadCatalogMap();
        List<CatalogItem> resultList = (ArrayList<CatalogItem>) catalogMap.values().stream().collect(Collectors.toList());
        return resultList.stream().sorted((e1,e2)->e2.getTimestamp().compareTo(e1.getTimestamp())).skip(startIndex).limit(limit).collect(Collectors.toList());
    }
    public void updateCatalog(){
        System.out.println("Update start : " + new Date(System.currentTimeMillis()));
        loadCatalogMap();
        var siteDataList = siteService.getAggregatedCatalogList()
                .stream().filter((e)->!catalogMap.containsKey(e.getHref())).collect(Collectors.toList());

            System.out.println("Pre size : " + repository.count());
            for(CatalogItem i : siteDataList){
                if(catalogQuery.ifExists(i.getHref()) == 0){
                    repository.save(i);
                    System.out.println("Added new item : " + i);
                }
            }
            System.out.println("Post size : " + repository.count());
            loadCatalogMap();
    }

    public void  loadCatalogMap(){
        catalogMap.clear();
        Iterable<CatalogItem> loadData= catalogQuery.selectOrdered();
        for(CatalogItem item : loadData){
                catalogMap.putIfAbsent(item.getHref(), item);
        }
    }

}
