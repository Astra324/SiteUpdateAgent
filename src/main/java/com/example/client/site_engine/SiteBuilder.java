package com.example.client.site_engine;

import com.example.client.model.TagMap;

import java.util.*;
import java.util.stream.Collectors;

public abstract class SiteBuilder {
    private HashMap<ParserTypes, SiteParser> siteParserMap = new HashMap<>();
    private HashMap<ViewTypes, SiteView> siteViewMap = new HashMap<>();
    private final String name;
    private final String baseUrl;
    private final String title;
    private final boolean isAggregated;
    private ArrayList<TagMap> tagMaps = new ArrayList<>();

    public enum ParserTypes {CATALOG(), ARTICLE(), CONTENT(), AGGREGATE();}
    public enum ViewTypes {CATALOG_VIEW, PAGE_VIEW;}

    public enum Sites {
        SITEMAP();
        private static final LinkedHashMap<String, SiteBuilder> map = new LinkedHashMap<>();

        public LinkedHashMap<String, SiteBuilder> map() {
            return map;
        }

        public void put(SiteBuilder site) {
            map.put(site.getName(), site);
        }

        public SiteBuilder getSiteByName(String siteName) {
            SiteBuilder site = map.get(siteName);
            return Objects.requireNonNull(site);
        }
    }

    public static abstract class Builder<T extends Builder<T>> {
        private String baseUrl;
        private String name;
        private  String title;
        private  boolean isAggregated;
        private  boolean isDisable;

        public abstract SiteBuilder build();

        public abstract T self();


        public T setAggregate(boolean param) {
            this.isAggregated = param;
            return self();
        }
        public T setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return self();
        }
        public T setTitle(String title) {
            this.title = title;
            return self();
        }

        public T setName(String name) {
            this.name = name;
            return self();
        }
    }

    protected SiteBuilder(Builder<?> builder) {
        this.name = builder.name;
        this.baseUrl = builder.baseUrl;
        this.title = builder.title;
        this.isAggregated = builder.isAggregated;
    }

    public String getName() {
        return this.name;
    }

    public String getBaseUrl() {
        return this.baseUrl;
    }

    public boolean isAggregated() {return isAggregated;}

    public String getTitle() {return title;}

    public SiteView getSiteViewByName(ViewTypes key) {
        return Objects.requireNonNull(siteViewMap.get(key));
    }

    public void putSiteView(ViewTypes key, SiteView siteView) {
        this.siteViewMap.put(key, siteView);
    }

    public SiteParser getParserByName(ParserTypes key) {
        return Objects.requireNonNull(siteParserMap.get(key));
    }

    public void putSiteParser(ParserTypes key, SiteParser siteParser) {
        siteParserMap.put(key, siteParser);
    }


    public String toString() {
        return this.name;
    }

    public ArrayList<TagMap> getTagMaps() {
        return (ArrayList<TagMap>) tagMaps.clone();
    }

    public List<TagMap> getTagMaps(int startIndex, int limit) {
        return  tagMaps.stream().skip(startIndex).limit(limit).collect(Collectors.toList());
    }

    public void setTagMaps(ArrayList<TagMap> tagMaps) {
        this.tagMaps = (ArrayList<TagMap>) Objects.requireNonNull(tagMaps).clone();
    }



}
