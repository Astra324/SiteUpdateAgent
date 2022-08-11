package com.example.client.controllers;

import com.example.client.services.RestClientService;
import com.example.client.services.SiteService;
import com.example.client.services.UtilsService;
import com.example.client.site_engine.SiteBuilder;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@Controller
public class Utils {
    @Autowired
    RestClientService clientService;
    @Autowired
    SiteService siteService;

    @Autowired
    UtilsService utilsService;

    @GetMapping("/utils")
    public String utilsIndex(Model model) {
        model.addAttribute("site_list",siteService.getSitesList());
        return "/utils/utils_index";
    }
    public void ch(){

    }
    @PostMapping ("/utils/catalog_test")
    public String catalogTest(@RequestParam Object sites, Model model) {
        System.out.println(sites.toString());
        String siteName = sites.toString();
        SiteBuilder choseSite = siteService.getSiteByName(siteName);
        model.addAttribute("chose_site", choseSite);
        return "/utils/catalog_test";
    }

    @PostMapping ("/utils/article_test")
    public String articleTest(@RequestParam Object sites, Model model) {
        System.out.println(sites.toString());
        String siteName = sites.toString();
        SiteBuilder choseSite = siteService.loadSiteCatalogContent(siteName);
        model.addAttribute("site", choseSite);
        return "/utils/article_test";
    }


    @PostMapping("/test_article_view")
    //@ResponseBody
    public String testArticle(@RequestParam String siteName, @RequestParam String url, @RequestParam String pattern, Model model){
        System.out.println(url + " : " + siteName + " : " + pattern);
        Document doc = clientService.loadPage(url);
        SiteBuilder site = SiteBuilder.Sites.SITEMAP.getSiteByName(siteName);
        site.getParserByName(SiteBuilder.ParserTypes.ARTICLE).test(doc, pattern);
        var res =site.getParserByName(SiteBuilder.ParserTypes.ARTICLE).getArticleMap();
        model.addAttribute("site_data", res);
        return "/article_view";
    }
    @PostMapping("/test_catalog")
    //@ResponseBody
    public String testCatalog(@RequestParam String siteName, @RequestParam String url, @RequestParam String pattern, Model model){
        System.out.println(url + " : " + siteName + " : " + pattern);
        Document doc = clientService.loadPage(url);
        SiteBuilder site = SiteBuilder.Sites.SITEMAP.getSiteByName(siteName);
        site.getParserByName(SiteBuilder.ParserTypes.CATALOG).test(doc, pattern);
        var sites = siteService.getSitesList().stream().filter((e)->e.getName().equals(siteName)).collect(Collectors.toList());
        model.addAttribute("site_list", sites);
        model.addAttribute("start", 0);
        model.addAttribute("limit", 30);
        return "index";
    }

}
