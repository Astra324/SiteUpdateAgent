package com.example.client.site_engine.sites;

import com.example.client.site_engine.SiteBuilder;

import com.example.client.site_engine.SiteParser;
import com.example.client.site_engine.SiteView;
import com.example.client.model.TagMap;
import com.example.client.site_engine.helpers.JsonHelper;
import com.example.client.site_engine.helpers.MyParser;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class SiteBBC extends SiteBuilder {
    private final String sitePattern = "https://www.bbc.com";


    private SiteParser catalogParser = new SiteParser() {

        private int index = 0;

        @Override
        public <T> T parseDoc(Document doc) throws JSONException {
            System.out.println("Start parse : " + getName());
            Elements elements = doc.select("script");
            String resultExtractedJasonStr = "";

            for (Element el : elements) {
                String html = el.outerHtml();
                if (html.contains("SIMORGH_DATA")) {
                    resultExtractedJasonStr = JsonHelper.findMatchJsonString(html);
                }
            }

            JsonHelper helper = new JsonHelper();
            ArrayList<TagMap> loadedMaps = new ArrayList<>();

            if (!resultExtractedJasonStr.isEmpty()) {

                JSONArray contentArray = helper.newObject(resultExtractedJasonStr)
                        .addWork("pageData", JsonHelper.Workers.fromKey)
                        .addWork("content", JsonHelper.Workers.fromKey)
                        .addWork("groups", JsonHelper.Workers.toArray)
                        .extract();
                index = 0;
                contentArray.forEach((line) -> {
                   // System.out.println(line.toString());
                    JSONArray items = helper.newObject(line.toString())
                            .addWork("items", JsonHelper.Workers.toArray).extract();
                    items.forEach((item) -> {
                        JSONObject nextItem = new JSONObject(item.toString());
                        if (nextItem.getString("type").equals("cps")) {
                            if (nextItem.getString("cpsType").equals("STY")
                                    && nextItem.toString().contains("indexImage")) {
                                JSONObject location = JsonHelper.get(nextItem, "locators");
                                JSONObject image = JsonHelper.get(nextItem, "indexImage");
                                JSONObject headlines = JsonHelper.get(nextItem, "headlines");

                                String url = sitePattern + location.getString("assetUri");
                                String src = image.getString("href");
                                String headline = headlines.getString("headline");
                                String summary = nextItem.getString("summary");
                                Long timestamp = nextItem.getLong("timestamp");
                                Date date = new Date(timestamp);
                                TagMap tm = new TagMap.Builder().buildEmpty()
                                        .key(url).value(src).setTitle(headline).setSiteName(getName())
                                        .setType(summary).setDateString(date.toString());
                                loadedMaps.add(tm);
                                index++;
                            }
                        }
                    });
                });
            }
            reindexMap(loadedMaps);
            setTagMaps(loadedMaps);
            System.out.println(getName() + " : Catalog size : " + getTagMaps().size());
            return (T) getTagMaps();
        }

        @Override
        public ArrayList<TagMap> getArticleMap() {
            return getTagMaps();
        }

        @Override
        public <T> T test(Document document, String pattern) {
            return parseDoc(document);
        }
    };
    private SiteParser articleParser = new SiteParser() {
        final Pattern extractIframe = Pattern.compile(".+(iframe.+iframe>?).+");
        private  ArrayList<TagMap> articleMap = new ArrayList<>();
        @Override
        public <T> T parseDoc(Document document) throws JSONException {
            articleMap.clear();
            String extractedJasonStr = document.select("script").stream()
                    .filter((e) -> e.outerHtml().contains("SIMORGH_DATA")).map(Node::outerHtml).reduce((s1, s2) -> s1 + s2).orElse("");
            String videoData = "";
            if (extractedJasonStr.contains("iframe>")) {
                videoData = parseIframe(extractedJasonStr);
            }
            String pagePattern = "h1, h2, h3, img, b, p, time";
            Elements main = document.select("main");
            Elements content = main.select(pagePattern);

            for(Element el : content) {
                TagMap newItem = new TagMap.Builder().buildEmpty().setSiteName(getName());
                if(el.is("h1")) {
                    String title = "<h1>" + el.text() + "</h1>";
                    newItem.setTitle(title);
                }else if(el.is("h2")){
                    String text = "<h2>" + el.text() + "</h2>";
                    newItem.setText(text);
                }else if(el.is("h3")){
                    String text = "<h3>" + el.text() + "</h3>";
                    newItem.setText(text);
                }
                else if(el.is("b")){
                    String text = "<b>" + el.text() + "</b>";
                    newItem.setText(text);
                }
                else if(el.is("p")){
                    String text = "<p>" + el.text() + "</p>";
                    newItem.setText(text);
                }
                else if(el.is("img")){
                    String img = "<img src=\"" + el.attr("src") + "\" class=\"img-fluid\" style=\"max-width:50%;height:auto; >";
                    newItem.value(img);
                }else if(el.is("time")){
                    System.out.println(el.outerHtml());
                    String time = "<p><b>" + el.html() + "</b></p>";
                    newItem.setDateString(time);
                }
                articleMap.add(newItem);
            }
            TagMap videoItem = new TagMap.Builder().buildEmpty().setSiteName(getName())
                    .setVideo(videoData);
            articleMap = (ArrayList<TagMap>) insertArray(articleMap, videoItem, 2).clone();
            return (T) (videoData + content.outerHtml());
        }
        private static <A> ArrayList<A> insertArray(ArrayList<A> inputList, A insertItem, Integer index) {
            final ArrayList<A> resultList = new ArrayList<>();
            for (int i = 0; i < inputList.size(); i++) {
                if (i == index)
                    resultList.add(insertItem);
                resultList.add(inputList.get(i));
            }
            return (ArrayList<A>) resultList.clone();
        }

        private String parseIframe(String extractedJasonStr) {
            MyParser parser = new MyParser();
            Matcher matcher = extractIframe.matcher(extractedJasonStr);
            String videoLink = "";
            if (matcher.find()) {
                videoLink = ("<" + matcher.group(1)).replace("\\\"", "")
                        .replace("\\u003c/", "</");
                videoLink = parser.writeAttribute(videoLink, "width", "640");
                videoLink = parser.writeAttribute(videoLink, "height", "420");
            }
            return videoLink;
        }

        @Override
        public <T> T test(Document document, String pattern) {
            return (T) parseDoc(document);
        }

        @Override
        public ArrayList<TagMap> getArticleMap() {
            return articleMap;
        }
    };

    private SiteParser pageParserOld = new SiteParser() {
        private  ArrayList<TagMap> currentMap = new ArrayList<>();
        private final String imageSrcPrefix = "https://ichef.bbci.co.uk/news/800/";
        final Pattern extractIframe = Pattern.compile(".+(iframe.+iframe>?).+");

        @Override
        public <T> T parseDoc(Document doc) {
            currentMap.clear();
            MyParser parser = new MyParser(Objects.requireNonNull(doc), getName());
            JsonHelper jsonHelper = new JsonHelper();

            String extractedJasonStr = parser.getTagContent("script", (els) -> {
                String resultExtractedJasonStr = "";
                for (Element el : els) {
                    if (el.outerHtml().contains("SIMORGH_DATA")) {
                        resultExtractedJasonStr = JsonHelper.findMatchJsonString(el.outerHtml());
                    }
                }
                return resultExtractedJasonStr;
            });
            if (!extractedJasonStr.isEmpty()) {
                //jsonHelper.helper().parse(extractedJasonStr,System.out::println);

                JSONArray blocksArray = jsonHelper.newObject(extractedJasonStr)
                        .addWork("pageData", JsonHelper.Workers.fromKey)
                        .addWork("content", JsonHelper.Workers.fromKey)
                        .addWork("model", JsonHelper.Workers.fromKey)
                        .addWork("blocks", JsonHelper.Workers.toArray)
                        .extract();

                blocksArray.forEach((line) -> {
                    //System.out.println(line.toString());
                    jsonHelper.viewJsonFromBBCPageString(line.toString(), (map) -> {

                        TagMap consumedTagMap = new TagMap.Builder().buildEmpty().setSiteName(getName());
                        if (map.containsKey("locator")) {
                            String img = "<img src=\"" + imageSrcPrefix + map.get("originCode") + "/" + map.get("locator") + "\" >";
                            consumedTagMap.key(img).setTagName("1");
                        } else if (map.containsKey("text")) {
                            String text = "<p>" + map.get("text") + "</p>";
                            consumedTagMap.setText(text).setTagName("1");
                        }
                        currentMap.add(consumedTagMap);
                        //System.out.println(currentMap.toString());
                    });
                });
                if (extractedJasonStr.contains("iframe>")) {
                    TagMap videoMap = new TagMap.Builder().buildEmpty().setSiteName(getName());
                    Matcher matcher = extractIframe.matcher(extractedJasonStr);
                    if (matcher.find()) {
                        //System.out.println("Matcher find : " + matcher.group(1));
                        String videoLink = ("<" + matcher.group(1)).replace("\\\"", "")
                                .replace("\\u003c/", "</");
                        videoLink = parser.writeAttribute(videoLink, "width", "640");
                        videoLink = parser.writeAttribute(videoLink, "height", "420");
                        videoMap.setVideo(videoLink).setTagName("1");
                        //System.out.println("Formated iframe : " + videoMap.getVideo());
                    }
                    currentMap = insertArray(currentMap, videoMap, 2);
                }

                ArrayList<TagMap> cleanedMap = (ArrayList<TagMap>) currentMap.stream().filter((e) -> !e.getTagName().isEmpty()).distinct().collect(Collectors.toList());
                currentMap = (ArrayList<TagMap>) cleanedMap.clone();
                TagMap title = currentMap.get(0);
                title.setTitle("<h1>" + title.getText() + "</h1>").setText("");
                currentMap.set(0, title);

            }
            //System.out.println(currentMap.toString());
            return (T) getArticleMap();
        }

        @Override
        public ArrayList<TagMap> getArticleMap() {
            return currentMap;
        }

        @Override
        public <T> T test(Document document, String pattern) {
            return parseDoc(document);
        }

        private static <A> ArrayList<A> insertArray(ArrayList<A> inputList, A insertItem, Integer index) {
            final ArrayList<A> resultList = new ArrayList<>();
            for (int i = 0; i < inputList.size(); i++) {
                if (i == index)
                    resultList.add(insertItem);
                resultList.add(inputList.get(i));
            }
            return (ArrayList<A>) resultList.clone();
        }
    };

    protected SiteBBC(Builder builder) {
        super(builder);
        putSiteParser(ParserTypes.CATALOG, catalogParser);
        putSiteParser(ParserTypes.ARTICLE, articleParser);
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
//            Patcher.PatchDoc patcher = new Patcher().CreateDoc();
//            String siteName = tagMap.get(0).getSiteName();
//            Optional<TagMap> firstImage = tagMap.stream().filter((t) -> t.getTagName().equals("image")).findFirst();
//
//            String result = patcher.paragraphPath("", "") + "<h2>" + tagMap.get(0).getText().toUpperCase(Locale.ROOT) + "</h2>";
//            result += patcher.paragraphPath("", "");
//
//            if (!firstImage.isEmpty()) {
//                String mainImage = patcher.imgSrcPatch(firstImage.get().key().replace(".webp", "") + "?size=630x300", "page_head_image");
//                result += patcher.linkPath(Patcher.Patches.SCRIPT_BRIDGE, "navigateImage", firstImage.get().key(), "", mainImage);
//                for (int i = 0; i < tagMap.size(); i++) {
//                    if (tagMap.get(i).getTagName().equals("image")) {
//                        tagMap.remove(i);
//                        break;
//                    }
//                }
//            }
//
//            result = patcher.divPatch("", 0, "page_header", getName(), result);
//
//
//            Integer index = 0;
//            for (TagMap tm : tagMap.stream().skip(1).collect(Collectors.toList())) {
//
//                if (tm.getTagName().equals("text")) {
//                    result += patcher.paragraphPath("", tm.getText());
//
//                } else if (tm.getTagName().equals("image")) {
//                    String img = patcher.imgSrcPatch(tm.key().replace(".webp", "") + "?size=630x300", "bbc-image");
//                    String link = patcher.linkPath(tm.key() + "?site_name=" + siteName
//                            + "&view_type=image&id=" + (index++) + "&navigate_param=base&", "", img);
//                    result += link;
//
//                } else if (tm.getTagName().equals("video")) {
//                    result += tm.getVideo();
//                }
//            }
//            tagMap.get(0).setHtml(result);
//            //return SiteViews.CLEAR_VIEW_PAGE.view(tagMap);
            return null;
        }
    };

    public static class Builder extends SiteBuilder.Builder<Builder> {

        @Override
        public SiteBuilder build() {
            return new SiteBBC(this);
        }

        @Override
        public Builder self() {
            return this;
        }
    }

}
