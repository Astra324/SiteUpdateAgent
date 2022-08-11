package com.example.client.config;

import com.example.client.site_engine.SiteBuilder;
import com.example.client.site_engine.sites.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ApplicationConfig {
    @Bean()
    public ExecutorService fixedThreadPool(){
        return Executors.newFixedThreadPool(10);
    }

    @Bean()
    @Scope("prototype")
    public ExecutorService singleThreadExecutor(){
        return Executors.newSingleThreadExecutor();
    }


    @Bean
    public SiteBuilder siteUnian() {
        SiteUnian siteUnian = (SiteUnian) new SiteUnian.Builder().setName("SiteUnian")
                .setTitle("Unian Інформаційне агенство : www.unian.ua/")
                .setBaseUrl("https://www.unian.ua")
                .setAggregate(true)
                .build();
        SiteBuilder.Sites.SITEMAP.map().put(siteUnian.getName(), siteUnian);
        return siteUnian;
    }

    @Bean
    public SiteBuilder siteBBC(){
       SiteBBC siteBBC = (SiteBBC) new SiteBBC.Builder().setName("SiteBBC")
                .setTitle("BBC NEWS Україна : www.bbc.com/ukrainian")
                .setBaseUrl("https://www.bbc.com/ukraine")
                .setAggregate(true)
                .build();
        SiteBuilder.Sites.SITEMAP.map().put(siteBBC.getName(), siteBBC);
        return siteBBC;
    }
    @Bean
    public SiteBuilder siteDW(){
        SiteDW siteDW = (SiteDW) new SiteDW.Builder().setName("SiteDW")
                .setTitle("Deutsche Welle online : www.dw.com")
                .setBaseUrl("https://www.dw.com/ru/")
                .setAggregate(true)
                .build();
        SiteBuilder.Sites.SITEMAP.map().put(siteDW.getName(), siteDW);
        return siteDW;
    }
    @Bean
    public SiteBuilder siteObozrevatel(){
        SiteObozrevatel siteObozrevatel = (SiteObozrevatel) new SiteObozrevatel.Builder().setName("SiteObozrevatel")
                .setTitle("OBOZREVATEL Каждый найдет своё! : www.obozrevatel.com/")
                .setBaseUrl("https://news.obozrevatel.com/ukr/")
                .setAggregate(true)
                .build();
        SiteBuilder.Sites.SITEMAP.map().put(siteObozrevatel.getName(), siteObozrevatel);
        return siteObozrevatel;
    }
    //SiteInterfax
    @Bean
    public SiteBuilder siteInterfax(){
        SiteInterfax siteInterfax = (SiteInterfax) new SiteInterfax.Builder().setName("SiteInterfax")
                .setTitle("Interfax - Україна інформаційне агенство  : www.interfax.com.ua")
                .setBaseUrl("https://interfax.com.ua/news/latest.html")
                .setAggregate(true)
                .build();
        SiteBuilder.Sites.SITEMAP.map().put(siteInterfax.getName(), siteInterfax);
        return siteInterfax;
    }
//    @Bean
//    public SiteBuilder siteSegodnya() {
//        SiteSegodnya siteSegodnya = (SiteSegodnya) new SiteSegodnya.Builder().setName("SiteSegodnya")
//                .setBaseUrl("https://www.segodnya.ua")
//                .setTitle("Сьогодні останні новини : www.segodnya.ua/ua")
//                .setAggregate(false)
//                .build();
//        SiteBuilder.Sites.SITEMAP.map().put(siteSegodnya.getName(), siteSegodnya);
//        return siteSegodnya;
//    }
}
