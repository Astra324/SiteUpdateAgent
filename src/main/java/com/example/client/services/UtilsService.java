package com.example.client.services;

import com.example.client.site_engine.SiteBuilder;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UtilsService {
    @Autowired
    RestClientService clientService;

    @Autowired
    SiteService siteService;

    public <T> T testParsePage(String siteName, String url, SiteBuilder.ParserTypes parserType){
        SiteBuilder site = SiteBuilder.Sites.SITEMAP.getSiteByName(siteName);
        Document document = clientService.loadPage(url);
        Object result = site.getParserByName(parserType).test(document, "");
        return (T) result;
    }
}
