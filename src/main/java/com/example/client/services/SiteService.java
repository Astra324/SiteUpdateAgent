package com.example.client.services;

import com.example.client.StartSchedules;
import com.example.client.model.CatalogItem;
import com.example.client.site_engine.SiteBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class SiteService {
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    ExecutorService fixedThreadPool;
    @Autowired
    ExecutorService singleThreadExecutor;


    public List<SiteBuilder> getSitesList(){
        return SiteBuilder.Sites.SITEMAP.map().values().stream().filter(SiteBuilder::isAggregated).toList();
    }
    public SiteBuilder getSiteByName(String siteName){
        return SiteBuilder.Sites.SITEMAP.getSiteByName(siteName);
    }

    public SiteBuilder loadSiteCatalogContent(String siteName) {
        SiteBuilder site = SiteBuilder.Sites.SITEMAP.getSiteByName(siteName);
        SiteBuilder resultList = null;
        Future<SiteBuilder> result = null;
        if (site != null) {
            Callable<SiteBuilder> task = () -> {
                Document doc = load(site.getBaseUrl());
                site.getParserByName(SiteBuilder.ParserTypes.AGGREGATE).parseDoc(doc);
                return site;
            };
            result = singleThreadExecutor.submit(task);
        }

        try {
            resultList = result.get();
            singleThreadExecutor.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }finally {
            if (!singleThreadExecutor.isShutdown())
            singleThreadExecutor.shutdown();
        }
        return Optional.ofNullable(resultList).orElseThrow(()->new NullPointerException("Load site content fail content map is null: " + siteName));
    }

    private Document load(String uri) {
        String result = restTemplate.getForObject(uri, String.class);
        Document document = Jsoup.parse(result);
        return document;
    }

    public List<SiteBuilder> getTop10(){
        var sites = SiteBuilder.Sites.SITEMAP.map().values().stream().filter(SiteBuilder::isAggregated).collect(Collectors.toList());
        getAggregateResult(sites);
        return new ArrayList<>(sites);
    }
    public List<CatalogItem> getAggregatedCatalogList(){
        var sites = SiteBuilder.Sites.SITEMAP.map().values().stream().filter(SiteBuilder::isAggregated).collect(Collectors.toList());
        getAggregateResult(sites);
        return sites.stream().flatMap((e)->e.getTagMaps().stream().map((map)->{
            map.setNewRegisteredItem(true);
            return new CatalogItem(map);
        })).collect(Collectors.toList());
    }
    public <T extends List<SiteBuilder>> T getAggregateResult(T sites) {
        AtomicReference<Integer> errorsCounter = new AtomicReference<>(0);
        ArrayList<SiteBuilder> resultSites = null;
        if (sites.size() > 0) {
            ArrayList<Callable<SiteBuilder>> tasks = new ArrayList<>();
            for (Object site : sites) {
                tasks.add(() -> {
                    SiteBuilder s = (SiteBuilder) site;
                    System.out.println("Aggregate loaded : " + s.getBaseUrl());
                    Document doc = load(s.getBaseUrl());
                    s.getParserByName(SiteBuilder.ParserTypes.AGGREGATE).parseDoc(doc);
                    return s;
                });
            }
            try {
                resultSites = (ArrayList<SiteBuilder>) fixedThreadPool.invokeAll(tasks).stream().map((e) -> {
                    try {
                        if (e.isDone()) {
                            return e.get();
                        }
                    } catch (InterruptedException | ExecutionException ex) {
                        ex.printStackTrace();
                        errorsCounter.getAndSet(errorsCounter.get() + 1);
                    }
                    return null;
                }).filter(Objects::nonNull).collect(Collectors.toList());
            } catch (InterruptedException e) {

                e.printStackTrace();
            } finally {
                //executorService.shutdown();
                if(errorsCounter.get() > 0){
                    //schedules.onErrorUpdate();
                }
                System.out.println("End task with : " + errorsCounter.get() + " errors.");
            }
        }
        return (T) Optional.ofNullable(resultSites).orElseThrow(() -> new NullPointerException("Aggregate array not loaded result is null: "));
    }
}
