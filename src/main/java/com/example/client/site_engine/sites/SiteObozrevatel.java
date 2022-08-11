package com.example.client.site_engine.sites;

import com.example.client.model.TagMap;
import com.example.client.site_engine.SiteBuilder;
import com.example.client.site_engine.SiteParser;
import com.example.client.site_engine.SiteView;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.regex.Pattern;

public class SiteObozrevatel extends SiteBuilder {

    private Pattern clearImgSize = Pattern.compile("(.*)\\?.*", Pattern.CASE_INSENSITIVE);
    private SiteParser catalogParser = new SiteParser() {
        @Override
        public <T> T parseDoc(Document document) {return test(document, "");}
        @Override
        public ArrayList<TagMap> getArticleMap() {return getTagMaps();}
        @Override
        public <T> T test(Document document, String pattern) {
            System.out.println("Start parse : " + getName());
            ArrayList<TagMap> loadedMaps = new ArrayList<>();
            String parsePattern = "a.newsImgRowTime_imgLink, img.newsImgRowTime_img, h3.newsImgRowTime_title, p.newsImgRowTime_descr, div.newsImgRowTime_footer";
            Elements allNews = document.select("div.newsImgRowTime_inner");

            for (Element target : allNews) {
                Elements content = target.select(parsePattern);
                TagMap newItem = new TagMap.Builder().buildEmpty().setSiteName(getName());
                for (Element el : content) {
                    if (el.is("a.newsImgRowTime_imgLink")) {
                        newItem.key(el.attr("href"));
                    } else if (el.is("img.newsImgRowTime_img")) {
                        newItem.value(el.attr("src"));
                    } else if (el.is("h3.newsImgRowTime_title")) {
                        //System.out.println(el.text());
                        newItem.setTitle(el.text());
                    } else if (el.is("p.newsImgRowTime_descr")) {
                        newItem.setText(el.text());
                    } else if (el.is("div.newsImgRowTime_footer")) {
                        String type = el.text().substring(0, el.text().indexOf(" "));
                        String dt = el.text().substring(el.text().indexOf(" "), el.text().length() - 1);
                        newItem.setType(type).setDateString(dt);
                    }
                }
                loadedMaps.add(newItem);
            }
            setTagMaps(loadedMaps);
            System.out.println(getName() + " : Catalog size : " + getTagMaps().size());
            return (T) loadedMaps;
        }
    };
    private SiteParser pageParser = new SiteParser() {
        private final ArrayList<TagMap> articleMap = new ArrayList<>();

        @Override public <T> T parseDoc(Document doc) {return test(doc, "");}
        @Override
        public ArrayList<TagMap> getArticleMap() {
            return articleMap;
        }
        @Override
        public <T> T test(Document document, String pattern) {
            articleMap.clear();
            String parsePattern = "header h1, iframe, div.newsFull_text p, time, img.newsFull_image";
            Elements main = document.select("main");
            StringBuilder html = new StringBuilder();
            Elements content = main.select(parsePattern);
            for (Element el : content) {
                TagMap newItem = new TagMap.Builder().buildEmpty().setSiteName(getName());
                if (el.is("header h1")) {
                    String header = "<h1>" + el.text() + "</h1>";
                    newItem.setTitle(header);
                } else if (el.is("iframe")) {
                    String src = el.attr("src");
                    System.out.println(src);
                    String videoTag = extractEmbedCode(src);
                    newItem.setVideo(videoTag);
                } else if (el.is("div.newsFull_text p")) {
                    String text = "<p>" + el.text() + "</p>";
                    newItem.setText(text);
                } else if (el.is("time")) {
                    String date = "<p><b>" + el.text() + "</b></p>";
                    newItem.setDateString(date);
                } else if (el.is("img.newsFull_image")) {
                    System.out.println(el.attr("src"));
                    String src = el.attr("src") + "?size=640x480";
                    String img = "<img src=\"" + src + " class=\"img-fluid\" style=\"max-width:50%;height:auto;>";
                    newItem.value(img);
                }
                articleMap.add(newItem);
            }
            return (T) html;
        }

        private String extractEmbedCode(String url) {
            return "<iframe src=\"" + url + "\" width=\"630\" height=\"420\" autoplay; encrypted-media; gyroscope; picture-in-picture; fullscreen\" frameborder=\"0\">" +
                    "</iframe>";
        }
    };

    protected SiteObozrevatel(Builder builder) {
        super(builder);
        putSiteParser(ParserTypes.CATALOG, catalogParser);
        putSiteParser(ParserTypes.ARTICLE, pageParser);
        putSiteParser(ParserTypes.AGGREGATE, catalogParser);

        putSiteView(ViewTypes.CATALOG_VIEW, catalogView);
        putSiteView(ViewTypes.PAGE_VIEW, pageView);
    }

    private SiteView catalogView = new SiteView() {
        @Override
        public <T> T view(ArrayList<TagMap> tagMap) {
            return null;
        }
    };
    private SiteView pageView = new SiteView() {
        @Override
        public <T> T view(ArrayList<TagMap> tagMap) {
            return null;
        }
    };

    public static class Builder extends SiteBuilder.Builder<Builder> {

        @Override
        public SiteBuilder build() {
            return new SiteObozrevatel(this);
        }

        @Override
        public Builder self() {
            return this;
        }
    }

}
