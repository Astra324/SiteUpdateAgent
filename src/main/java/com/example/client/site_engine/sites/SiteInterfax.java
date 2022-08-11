package com.example.client.site_engine.sites;

import com.example.client.model.TagMap;
import com.example.client.site_engine.SiteBuilder;
import com.example.client.site_engine.SiteParser;
import com.example.client.site_engine.SiteView;
import com.example.client.site_engine.helpers.MyParser;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SiteInterfax extends SiteBuilder {

    private SiteParser catalogParser = new SiteParser() {
        private final Pattern date = Pattern.compile("(\\d{2,}:\\d{2,}\\s\\d{2,}\\.\\d{2,}\\.\\d{4,})(.+)");

        @Override
        public <T> T parseDoc(Document doc) {
            MyParser parser = new MyParser(Objects.requireNonNull(doc), getName());
            parser.parseTag("a").doParse().writeAttributePrefix("href", "https://interfax.com.ua");
            ArrayList<TagMap> loadedMaps = (ArrayList<TagMap>) parser.parseClass("article").doParse().toTagMap("href", "src")
                    .stream().peek((e)->{
                        Matcher m = date.matcher(e.getText());
                        if(m.find()) e.setText(m.group(2));
                        if(e.value().isEmpty()) e.value("https://cdn.pixabay.com/photo/2017/05/15/23/48/survey-2316468_960_720.png");
                    }).collect(Collectors.toList());
            reindexMap(loadedMaps);
            setTagMaps(loadedMaps);
            System.out.println(getName() + " : Catalog size : " + getTagMaps().size());
            return (T) getTagMaps();
        }
        @Override
        public ArrayList<TagMap> getArticleMap() {return getTagMaps();}
        @Override public <T> T test(Document document, String pattern) {return parseDoc(document);}
    };

    private SiteParser pageParser = new SiteParser() {
        private final ArrayList<TagMap> articleMap = new ArrayList<>();
        @Override
        public <T> T parseDoc(Document doc) {return (T) test(doc, "");}
        @Override
        public ArrayList<TagMap> getArticleMap() {return articleMap;}
        @Override
        public <T> T test(Document document, String pattern) {
            System.out.println("Start parse : " + getName());
            articleMap.clear();
            String parsePattern = "h1.article-content-title, img.article-content-image, p, div.article-time";
            Elements article = document.select("article");
            Elements content = article.select(parsePattern);
            for(Element el : content) {
                TagMap newItem = new TagMap.Builder().buildEmpty().setSiteName(getName());
                if(el.is("h1.article-content-title")){
                    String title = "<h1>" + el.text() + "</h1>";
                    newItem.setTitle(title);
                }else if(el.is("img.article-content-image")){
                    newItem.value(el.outerHtml());
                }else if(el.is("p")){
                    String text = "<p>" + el.text() + "</p>";
                    newItem.setText(text);
                }else if(el.is("div.article-time")){
                    System.out.println(el.outerHtml());
                    String time = "<p><b>" + el.html() + "</b></p>";
                    newItem.setDateString(time);
                }
                articleMap.add(newItem);
            }
            return (T) content.outerHtml();
        }
    };
    protected SiteInterfax(Builder builder) {
        super(builder);
        putSiteParser(ParserTypes.CATALOG, catalogParser);
        putSiteParser(ParserTypes.AGGREGATE, catalogParser);
        putSiteParser(ParserTypes.ARTICLE, pageParser);

        putSiteView(ViewTypes.CATALOG_VIEW, catalogView);
        putSiteView(ViewTypes.PAGE_VIEW, pageView);

    }
    private SiteView catalogView = new SiteView() {
        @Override
        public <T> T  view(ArrayList<TagMap> tagMap) {
            return null;
        }
    };
    private SiteView pageView = new SiteView() {
        @Override
        public <T> T view(ArrayList<TagMap> tagMap) {
            return null;
        }
    };

    public static class Builder extends SiteBuilder.Builder<Builder>{

        @Override
        public SiteBuilder build() {
            return new SiteInterfax(this);
        }

        @Override
        public Builder self() {
            return this;
        }
    }

}
