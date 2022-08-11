package com.example.client.site_engine.helpers;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.example.client.model.TagMap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MyParser {
	private String parseTag = "";
	private String parseAttr = "";
	private String parseId = "";
	private String parseClass = "";
	private final  String siteName;
	
	private boolean isParseTag = false;	
    private boolean isParseClass = false;
    private boolean isParseId = false;
	private boolean isParseAttr = false;
	private boolean isParseText = false;


	private Document parseDoc = null;
	private Elements parseElements = new Elements();
	private List<String> resultList = new ArrayList<String>();

	public MyParser() {
		this.siteName = "BaseSite";
	}
	public MyParser(Document parseDoc) {
		this.siteName = "BaseSite";
		this.parseDoc = Objects.requireNonNull(parseDoc);
	}

	public MyParser(Document parseDoc, String siteName) {
		this.parseDoc = Objects.requireNonNull(parseDoc);
		this.siteName = siteName;
	}




public void loadLocal(String url){
	File file = new File(url);
	try {
		this.parseDoc = Jsoup.parse(file, "UTF-8"); //loadDoc("http://www.google.com");
	} catch (IOException e) {
		e.printStackTrace();
	}
}

	public MyParser doParse() {
		
		resultList.clear();
		parseElements.clear();

	if(this.parseDoc != null) {

		if (isParseTag) {
			parseElements = parseDoc.getElementsByTag(parseTag);
		}
		if (isParseClass) {
			parseElements = parseDoc.getElementsByClass(parseClass);
		}
		if (isParseId) {
			Element el = parseDoc.getElementById(parseId);
			parseElements.add(el);
		}
		if (isParseText) {
			parseElements = parseDoc.getElementsByTag("html");

		}
	}
		return this;
	}
	private void init() {
		isParseTag = false;
		isParseAttr = false;
		isParseText = false;
		isParseClass = false;
		isParseId = false;	
	}

	public MyParser parseTag(String parseTag) {
		this.parseTag = parseTag;
		isParseTag = true;
		return this;
	}
	public MyParser parseClass(String parseClass) {
		this.parseClass = parseClass;
		isParseClass = true;
		return this;
	}
	public MyParser parseId(String parseId) {
		this.parseId = parseId;
		isParseId = true;
		return this;
	}
	public MyParser parseAttr(String parseAtt ) {
		this.parseAttr = parseAtt;
		isParseAttr = true;
		return this;
	}
	public MyParser parseText() {
		isParseText = true;
		return this;
	}
	public <T, C> Stream<T> streamParse(Document document, String tagName){
		Stream<T> tagContent = (Stream<T>) document.getElementsByTag(tagName).stream();
		return Optional.ofNullable(tagContent).orElseThrow(()-> new NullPointerException(""));
	}
	public String getClassContent(String className, Function<Elements, String> function){
		Elements tagContent = parseDoc.getElementsByClass(className);
		String result = function.apply(tagContent);;
		return result;
	}
	public <T> Optional<T> getClassContentOptional(String html, String className, Function<Elements, T> function){
		Optional<T> result = null;
		Document doc = Jsoup.parse(Objects.requireNonNull(html));
		Elements tagContent = doc.getElementsByClass(className);
		result = Optional.ofNullable(function.apply(tagContent));
		return (Optional<T>) result;
	}
	public <T> Optional<T> getTagContentOptional(String html, String outerTag, Function<Elements, T> function){
		Document doc = Jsoup.parse(Objects.requireNonNull(html));
		Elements tagContent = doc.getElementsByTag(outerTag);
		Optional<T> result =  Optional.ofNullable(function.apply(tagContent));
		return (Optional<T>) result;
	}
	public String getClassContent(String html, String className, Function<Elements, String> function){
		Document doc = Jsoup.parse(Objects.requireNonNull(html));
		Elements tagContent = doc.getElementsByClass(className);
		String result = function.apply(tagContent).replaceAll("\\\\", "/")
				.replaceAll("&quot;", "\"")
				.replaceAll("&amp;", "&")
				.replaceAll(".webp", "");;
		return result;
	}
	public String getTagContent(String outerTag, Function<Elements, String> function){
		Elements tagContent = parseDoc.getElementsByTag(outerTag);
		String result = function.apply(tagContent);
		return result;
	}
	public String getTagContent(String html, String outerTag, Function<Elements, String> function){
		Document doc = Jsoup.parse(Objects.requireNonNull(html));
		Elements tagContent = doc.getElementsByTag(outerTag);
		String result = function.apply(tagContent)
				.replaceAll("\\\\", "/")
				.replaceAll("&quot;", "\"")
				.replaceAll("&amp;", "&")
				.replaceAll(".webp", "");
		return result;
	}

	public String getTagContent(String html, String tag){
		Document doc = Jsoup.parse(Objects.requireNonNull(html));
		Elements tagContent = doc.getElementsByTag(tag);
		return tagContent.outerHtml().replaceAll("\\\\", "/")
				.replaceAll("&quot;", "\"")
				.replaceAll("&amp;", "&")
				.replaceAll(".webp", "");
	}
	public String getTextFromClassTag(String html, String className , String tag){
		Document doc = Jsoup.parse(Objects.requireNonNull(html));
		Elements classContent = doc.getElementsByClass(className);

		String result ="";
		for(Element el : classContent){
			Elements links = el.getElementsByTag(tag);
			for(Element e: links){
				result = e.text();
			}
		}
		return result.replaceAll("\\\\", "/")
				.replaceAll("&quot;", "\"")
				.replaceAll("&amp;", "&")
				.replaceAll(".webp", "");
	}
	public String getTextFromClassTag(String className , String tag){
		Elements classContent = Objects.requireNonNull(this.parseDoc.getElementsByClass(className));
		String result ="";
		for(Element el : classContent){
			Elements links = el.getElementsByTag(tag);
			for(Element e: links){
				result = e.text();
			}
		}
		return result.replaceAll("\\\\", "/")
				.replaceAll("&quot;", "\"")
				.replaceAll("&amp;", "&")
				.replaceAll(".webp", "");
	}

	public String getAttributeFromClassTag(String html, String className , String tag, String key){
		Document doc = Jsoup.parse(Objects.requireNonNull(html));
		Elements classContent = doc.getElementsByClass(className);

		String result ="";
		for(Element el : classContent){
			Elements links = el.getElementsByTag(tag);
			for(Element e: links){
				result = e.attr(key);
			}
		}
		return result.replaceAll("\\\\", "/")
				.replaceAll("&quot;", "\"")
				.replaceAll("&amp;", "&")
				.replaceAll(".webp", "");
	}
	public String getAttributeFromTag( String html,  String tag, String key){
		Document doc = Jsoup.parse(Objects.requireNonNull(html));
		String result = doc.getElementsByTag(tag).stream().limit(1).map((e)->e.attr(key)).reduce((s1,s2)->s1+s2).orElse("");
		return result.replaceAll("\\\\", "/")
				.replaceAll("&quot;", "\"")
				.replaceAll("&amp;", "&")
				.replaceAll(".webp", "");
	}
	public String getAttributeFromClassTag( String className , String tag, String key){
		Elements classContent = Objects.requireNonNull(this.parseDoc.getElementsByClass(className));
		String result ="";
		for(Element el : classContent){
			Elements links = el.getElementsByTag(tag);
			for(Element e: links){
				result = e.attr(key);
			}
		}
		return result.replaceAll("\\\\", "/")
				.replaceAll("&quot;", "\"")
				.replaceAll("&amp;", "&")
				.replaceAll(".webp", "");
	}
	public String writeAttribute(String html, String attrName, String attrValue) {
		Document doc = Jsoup.parse(Objects.requireNonNull(html));
		Elements parseElements = doc.getElementsByAttribute(attrName);
		String result = "";
		for(Element el : parseElements) {
			el.attr(attrName, attrValue);
			result += el.outerHtml();
		}
		return result.replaceAll("\\\\", "/")
				.replaceAll("&quot;", "\"")
				.replaceAll("&amp;", "&")
				.replaceAll(".webp", "");
	}

	public String writeAttributePrefix(String html, String attrName, String prefixValue ) {
		Document doc = Jsoup.parse(Objects.requireNonNull(html));
		Elements parseElements = doc.getElementsByAttribute(attrName);
		String result = "";
		for(Element el : parseElements) {
			el.attr(attrName, prefixValue + el.attr(attrName));
			result += el.outerHtml();
		}
		return result.replaceAll("\\\\", "/")
				.replaceAll("&quot;", "\"")
				.replaceAll("&amp;", "&")
				.replaceAll(".webp", "");
	}
	public MyParser writeAttributePrefix(String attrName, String prefixValue ) {
		resultList.clear();
		for(Element el : parseElements) {		
			el.attr(attrName, prefixValue + el.attr(attrName));
			resultList.add(el.attr(attrName));
		}
		return this;
	}
	public MyParser writeAttributePostfix(String attrName, String postfixValue ) {
		resultList.clear();
		for(Element el : parseElements) {			
			el.attr(attrName, el.attr(attrName) + postfixValue);
			resultList.add(el.attr(attrName));
		}
		return this;
	}

	private void parseTextContent() {
		for(Element el : parseElements) {
			resultList.add(el.text());
		}
	}
	private void parseAttribute() {
		for(Element el : parseElements) {
			resultList.add(el.attr(parseAttr));
		}
	}

	public ArrayList<TagMap> toTagMap(){
	String key = "href";
	String value = "src";
	if(parseElements.isEmpty()){
		parseElements = this.parseDoc.getElementsByAttribute(key);
	}
		ArrayList<TagMap> list = new ArrayList<TagMap>();
		Integer id = 0;
		for(Element el : parseElements) {
			TagMap tm = new TagMap.Builder().createTag(el).createMap(el, key, value).setSiteName(this.siteName).build();
			list.add(tm);
			id++;
		}
		init();
		return list;
	}
	public ArrayList<TagMap> toTagMap(String k){
		if(parseElements.isEmpty()){
			parseElements = this.parseDoc.getElementsByAttribute(k);
		}
		ArrayList<TagMap> list = new ArrayList<TagMap>();
		Integer id = 0;
		for(Element el : parseElements) {
			TagMap tm = new TagMap.Builder().createTag(el).createMap(el, k).setSiteName(this.siteName).build();
			list.add(tm);
			id++;
		}
		init();
		return list;
	}
	public ArrayList<TagMap> toTagMap(String k, String v){
		if(parseElements.isEmpty()){
			parseElements = this.parseDoc.getElementsByAttribute(k);
		}
		ArrayList<TagMap> list = new ArrayList<TagMap>();
		Integer id = 0;
		for(Element el : parseElements) {
			TagMap tm = new TagMap.Builder().createTag(el).createMap(el, k, v).setSiteName(this.siteName).build();
			list.add(tm);
			id++;
		}
		init();
		return list;
	}
	public Document setPareDocStripped(){
		if(parseElements != null){
			Optional<String> stripped = parseElements.stream().map((e)->{return e.outerHtml();}).reduce((s1, s2)->{return s1+s2;});
			if(!stripped.isEmpty()){
				this.parseDoc = Jsoup.parse(stripped.get());
				System.out.println("stripped");
				init();
				return Objects.requireNonNull(this.parseDoc);
			}
		}
		return null;
	}
	public ArrayList<String> toList(){
		if(isParseAttr) {
			parseAttribute();
		}

		init();
		ArrayList<String> result = new ArrayList<>(resultList);
		return result;
	}
	public Document getParsedDoc() {
		init();
		return Objects.requireNonNull(parseDoc) ;
	}
	public void setParseDoc(Document doc){
		this.parseDoc = Objects.requireNonNull(doc);
	}
	public Document setParseDocFromHtml(String html){
		Document tempDoc = this.parseDoc.clone();
		Document newDoc = Objects.requireNonNull(Jsoup.parse(html));
		this.parseDoc = newDoc.clone();
		return tempDoc;
	}
    public MyParser setParseElements(Elements elements){
		init();
		this.parseDoc =  Jsoup.parse(Objects.requireNonNull(elements.outerHtml()));
		return this;
	}
	public Elements getParseElements() {
		return Objects.requireNonNull(parseElements.clone());
	}

	public enum ParamExtractor {
		PARAM;
		private static  String url;
		//?local_image_link&
		private static final Pattern clearUrl = Pattern.compile("(.*)\\?.*");
		//private static final Pattern extractVLInk = Pattern.compile("(.*.[.html]|[.htm])\\?.*", Pattern.CASE_INSENSITIVE);
		private static final Pattern extractVLInk = Pattern.compile("(.*)\\?.*", Pattern.CASE_INSENSITIVE);
		private static final Pattern extractImageLink = Pattern.compile("(.*.[.jpg]|[.png]|[.tif]|[.gif]|[.jfif])\\?.*", Pattern.CASE_INSENSITIVE);
		public static final Pattern urlPattern = Pattern.compile("(?:|[\\w*?])((ht|f)tp(s?):\\/\\/|www\\.)"
						+ "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*" + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*/)", Pattern.CASE_INSENSITIVE);

		public ParamExtractor setUrl(String url){
			this.url = url;
			return this;
		}
		public String getUrl(){
			String res = "";
			Matcher matcher = clearUrl.matcher(url);
			if (matcher.find()) res = matcher.group(1);
			return Objects.requireNonNull(res);
		}
		public String key(String key){
			String res ="";
			Pattern pattern = Pattern.compile(key+ "=(.+?)&", Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(url);
			if(matcher.find()) {
				res = matcher.group(1);
			}
			return res;
		}
		public  String extractVirtualLink(){
			Matcher matcher = extractVLInk.matcher(url);
			String virtualLink = "";
			if(matcher.find()) {
				 virtualLink = matcher.group(1);
			}
			return virtualLink;
		}
		public  String extractVirtualImageLink(){
			Matcher matcher = extractImageLink.matcher(url);
			String virtualLink = "";
			if(matcher.find()) {
				virtualLink = matcher.group(1);
			}
			return virtualLink;
		}
		public boolean isImageLink(){
			Pattern pattern = Pattern.compile( "^local_image_link", Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(url);
			return  matcher.find();
		}
		//single_page
		public boolean isSinglePage(){
			Pattern pattern = Pattern.compile( "^single_page", Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(url);
			return  matcher.find();
		}
		public boolean isCatalog(){
			Pattern pattern = Pattern.compile( "^catalog\\?", Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(url);
			return  matcher.find();
		}
		//aggregate
		public boolean isAggregate(){
			Pattern pattern = Pattern.compile( "^aggregate\\?", Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(url);
			return  matcher.find();
		}
	}
}
