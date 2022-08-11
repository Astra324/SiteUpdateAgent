package com.example.client.services;

import com.example.client.site_engine.SiteBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Service
public class RestClientService {
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    ExecutorService singleThreadExecutor;

    public Document loadPage(String url) {
        Callable<Document> task = () -> {
            Document doc = load(url);
            return doc;
        };
        Future<Document> result = null;
        result = singleThreadExecutor.submit(task);
        Document resultDoc = null;
        try {
            resultDoc = result.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(resultDoc).orElseThrow(() -> new NullPointerException("Load document fail from link : " + url));
    }

    private Document load(String uri) {
        String result = restTemplate.getForObject(uri, String.class);
        Document document = Jsoup.parse(result);
        return document;
    }
}
