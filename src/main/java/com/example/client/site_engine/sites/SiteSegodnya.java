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
import java.util.concurrent.atomic.AtomicInteger;


public class SiteSegodnya extends SiteBuilder {
    private int index = 0;

    private SiteParser catalogParser = new SiteParser() {
        private final static String linkPrefix = "http://theins5.press";

        @Override
        public <T> T parseDoc(Document document) {
            return (T) test(document, "");
        }

        @Override
        public ArrayList<TagMap> getArticleMap() {
            return getTagMaps();
        }

        @Override
        public <T> T test(Document document, String pattern) {
            getTagMaps().clear();
            String parsePattern= "img.main-photo__image, a.list-news__title, h3.newsfeed__link, span.list-news__time time";
            var targetMap = new ArrayList<TagMap>();
            MyParser parser = new MyParser();


            Elements articles = document.select("div.list-news__item");

            for(Element article : articles) {

                Elements content = article.select("h3, a.list-news__title, img, span.list-news__time");
                TagMap tm = new TagMap.Builder().buildEmpty().setSiteName(getName());

                for(Element e: content) {
                    if (e.is("h3")) {
                        //System.out.println("title : " + e.text());
                        tm.setTitle(e.text());
                    } else if (e.is("a.list-news__title")) {
                        tm.key(e.attr("href"));
                        //System.out.println("key : " + e.attr("href"));
                    } else if (e.is("img")) {
                        //System.out.println("img : " + e.outerHtml());
                        tm.value(e.attr("data-src"));
                    } else if (e.is("span.list-news__time")) {
                        //System.out.println("date : " + e.text());
                        tm.setDateString(e.text());
                    }
                }

                targetMap.add(tm);
                //System.out.println(" : " + tm);

            }
            setTagMaps(targetMap);
            System.out.println(getName() + " : Catalog size : " + getTagMaps().size());
            return (T) "";
        }
    };
    private SiteParser pageParser = new SiteParser() {
        private final ArrayList<TagMap> articleMap = new ArrayList<>();

        @Override
        public <T> T parseDoc(Document doc) {
            articleMap.clear();
            MyParser parser = new MyParser(Objects.requireNonNull(doc), getName());
            AtomicInteger index = new AtomicInteger(0);
            ArrayList<TagMap> pageContent = new ArrayList<>();

            Element bodyContent = doc.getElementById("page_content");
            Elements content = bodyContent.select("h1, p.article__like-h2, figure.photo_block img, div.article-text p, iframe, div.article__info-item");
            for (Element el : content) {
                TagMap newItem = new TagMap.Builder().buildEmpty().setSiteName(getName()).setId(index.getAndIncrement());
                if (el.is("h1")) {
                    String title = "<h1>" + el.text() + "</h1>";
                    newItem.setTitle(title);
                }
                if (el.is("p.article__like-h2")) {
                    String title1 = "<h3>" + el.text() + "</h3>";
                    newItem.setText(title1);
                }
                if (el.is("div.article-text p")) {
                    String title1 = "<p>" + el.text() + "</p>";
                    newItem.setText(title1);
                }
                if (el.is("figure.photo_block img")) {
                    String src = el.attr("src");
                    String img = "<img src=\"" + src + "\" class=\"img-fluid\" style=\"max-width:50%;height:auto; >";
                    newItem.value(img);
                }
                if (el.is("iframe")) {
                    newItem.setVideo(el.outerHtml());
                }
                if (el.is("div.article__info-item")) {
                    newItem.setDateString(el.text());
                }
                articleMap.add(newItem);
            }
            return (T) getArticleMap();
        }
        @Override
        public ArrayList<TagMap> getArticleMap() {
            return articleMap;
        }
        @Override
        public <T> T test(Document document, String pattern) {
            articleMap.clear();

            return (T) parseDoc(document);
        }
    };
    protected SiteSegodnya(Builder builder) {
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
//            Patcher.PatchDoc patcher = new Patcher().CreateDoc();
//            for(TagMap tm : tagMap){
//                String result = patcher.imgSrcPatch(tm.value().replace(".webp", ""), "small_image");
//                result += patcher.paragraphPath("catalog_title", tm.getText());
//                result += patcher.paragraphPath("", "<b>" + tm.getType() + "</b>");
//                result = patcher.divPatch(tm.key(), tm.getId(), "", tm.getSiteName(), result);
//                tm.setHtml(result);
//            }
            return (T) "";//SiteViews.CLEAR_VIEW_CATALOG.view(tagMap);
        }
    };
    private SiteView pageView = new SiteView() {
        @Override
        public <T> T view(ArrayList<TagMap> tagMap) {
//            Patcher.PatchDoc patcher = new Patcher().CreateDoc();
//            AtomicInteger indexer = new AtomicInteger(0);
//            String resultHtml = tagMap.stream().map((e)->{
//
//                StringBuilder html = new StringBuilder();
//                if(e.getTagName().equals("header")){
//                    String header = "<h2>" + e.getText().toUpperCase(Locale.ROOT) + "</h2><p>";
//                    html.append(header);
//                }else if(e.getTagName().equals("image")){
//                    String img = patcher.imgSrcPatch(e.key().replace(".webp", ""), e.value(),"bbc-image");
//                    String link = patcher.linkPath(e.key() + "?site_name=" + e.getSiteName()
//                            + "&view_type=image&id=" + (indexer.getAndIncrement()) + "&navigate_param=base&", "", img);
//                    html.append(link);
//                }else if(e.getTagName().equals("text")){
//                    String text = patcher.paragraphPath("", e.getText());
//                    html.append(text);
//                }else if(e.getTagName().equals("video")){
//                    html.append(e.getVideo());
//                }
//                return html.toString();
//
//            }).reduce((s1, s2)->s1+s2).orElse("");
//
//            //System.out.println(resultHtml);
//            tagMap.get(0).setHtml(resultHtml);

            return (T) "";//SiteViews.CLEAR_VIEW_PAGE.view(tagMap);
        }
    };


    public static class Builder extends SiteBuilder.Builder<Builder> {

        @Override
        public SiteBuilder build() {
            return new SiteSegodnya(this);
        }

        @Override
        public Builder self() {
            return this;
        }
    }

}
