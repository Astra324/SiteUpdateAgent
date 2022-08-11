package com.example.client.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import com.example.client.site_engine.helpers.JsonPair;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Element;


public class TagMap implements SiteData {
	private Integer id = 0;
	private String tagName = "";
	private String html = "";
	private String title = "";
	private String text = "";
	private String key = "";
	private String value = "";
	private String siteName = "";
	private String type = "";
	private String dateString = "";
	private Date postDate = null;
	private String video = "";
	private int catalogIndex;
	private Boolean isNewRegisteredItem = false;
	private ArrayList<String> imageGallery = null;


	public static class Builder {
		private  Integer id = 0;
		private String tagName = "";
		private String html = "";
		private String text = "";
		private  String key;
		private  String value;
		private String siteName;
		private Date postDate;
		private Boolean isNewItem = false;

		
		public Builder setSiteName(String siteName){
			this.siteName = siteName;
			return  this;
		}

		public Builder createTag(Element element) {
			this.tagName = element.tagName();
			this.html = element.outerHtml();
			this.text = element.text();
			return this;
		}
		public Builder createCatalogItem(Integer id, String siteName, String key, String value, String text, Date postDate) {
			this.id = id;
			this.siteName = siteName;
			this.key = key;
			this.value = value;
			this.text = text;
			this.postDate = postDate;
			return this;
		}
		public Builder registerNewCatalogItem(String siteName, String key, String value, String text) {
			this.siteName = siteName;
			this.key = key;
			this.value = value;
			this.text = text;
			isNewItem = true;
			return this;
		}

		public Builder createMap( Element element, String k, String v) {
			key = element.getElementsByAttribute(k).attr(k);
			value = element.getElementsByAttribute(v).attr(v);
			
			return this;
		}
		public Builder createMap( Element element, String k) {
			key = element.getElementsByAttribute(k).attr(k);
			value = element.getElementsByAttribute(k).text();		
			return this;
		}
		public Builder createMap( String key, String value) {
			this.key = key;
			this.value = value;
			return this;
		}
		
		public TagMap build() {
			return new TagMap(this);
		}
		public TagMap buildEmpty() {
			return new TagMap();
		}
	}
	private TagMap() {
        this.siteName = "";
		this.tagName = "";
	}
	private TagMap(Builder builder) {
		this.id = builder.id;
		this.siteName = builder.siteName;
		this.key = builder.key;
		this.value = builder.value;
		this.postDate = builder.postDate;
		this.isNewRegisteredItem = builder.isNewItem;
		this.tagName = builder.tagName ;
		this.html = builder.html;
		this.text = builder.text;
		if(this.postDate !=null) {
			this.dateString = postDate.toString();
		}
		this.siteName = Objects.requireNonNull(builder.siteName);
	}
	public static TagMap createAndRegisterFromCatalogItem(CatalogItem importItem){
		TagMap newItem = new TagMap.Builder()
				.registerNewCatalogItem( importItem.getSiteName(), importItem.getHref(), importItem.getImgSrc(), importItem.getTitle())
				.build();
		return newItem;
	}
	public static TagMap createFromCatalogItem(CatalogItem importItem){
		TagMap newItem = new TagMap.Builder()
				.createCatalogItem(importItem.getId().intValue(), importItem.getSiteName(), importItem.getHref(), importItem.getImgSrc(), importItem.getTitle(), importItem.getPostDate())
				.build();
		return newItem;
	}
	public  static TagMap createAndRegisterFromJsonPair(JsonPair<?, ?> jsonPair){
		//System.out.println(jsonPair);
		Optional<TagMap> resultMap = Optional.ofNullable((JSONObject) jsonPair.getValue()).map((e)->{
			String siteName = (String) e.get("siteName");
			String key = (String) e.get("key");
			String value = (String) e.get("value");
			String text = (String) e.get("text");
			TagMap newItem = new Builder().registerNewCatalogItem(siteName, key, value, text).build();
			return newItem;

		}).or(Optional::empty);
		if ((resultMap.isPresent())) return resultMap.get();
		throw new JSONException("Cant crete TagMap from JSONObject : " + jsonPair.getValue() + " resultMap is empty");
	}
	public static TagMap createFromJsonPair(JsonPair<?, ?> jsonPair){
		//System.out.println(jsonPair);
		Optional<TagMap> resultMap = Optional.ofNullable((JSONObject) jsonPair.getValue()).map((e)->{
			String date = "";
			for(String k : e.keySet()){
				if(k.equals("date")){
					date = (String) e.get(k);
				}
			}
			return new Builder().buildEmpty()
					.setSiteName((String) e.get("siteName"))
					.key((String) e.get("key"))
					.value((String) e.get("value"))
					.setDateString(date)
					.setText((String) e.get("text"));
		}).or(Optional::empty);
        if ((resultMap.isPresent())) return resultMap.get();
		throw new JSONException("Cant crete TagMap from JSONObject : " + jsonPair.getValue() + " resultMap is empty");
	}


	public TagMap setTagName(String tagName){this.tagName = tagName;return this;}
	public TagMap setSiteName(String siteName){this.siteName = siteName;return this;}
	public TagMap setHtml(String html){this.html = html;return this;}
	public TagMap setText(String text){this.text = text;return this;}
	public TagMap key(String key){this.key = key;return this;}
	public TagMap value(String value){this.value = value;return this;}
	public TagMap setId(Integer id){ this.id = id;return this;}
	public TagMap setType(String type) {this.type = type;return this;}
	public TagMap setDateString(String dateString) {this.dateString = dateString; return this;}
	public TagMap setVideo(String video) {this.video = video; return this;}
	public TagMap setNewRegisteredItem(Boolean state){this.isNewRegisteredItem = state;return this;}
	public TagMap setTitle(String title) {this.title = title; return this;}
	public TagMap addImageGallery(String src){
		if(imageGallery == null) imageGallery = new ArrayList<>();
		imageGallery.add(src);
		return  this;
	}





	public String getTitle() {return title;}
	public ArrayList<String> getImageGallery() {return imageGallery;}
	public boolean isNewRegistered(){
		return this.isNewRegisteredItem;
	}
	public TagMap setCatalogIndex(int catalogIndex) {this.catalogIndex = catalogIndex;return this;}
	public int getCatalogIndex() {return catalogIndex;}
    public Date getPostDate(){return this.postDate;}
	public String getVideo() {return video;}
	public String getDateString() {return dateString;}
	public String key() {return this.key;}
	public String value() {return this.value;}
	public String getTagName() {return tagName;}
	public String getHtml() {return this.html;}
	public String getText() {return this.text;}
	public String getSiteName() {return siteName;}
	public Integer getId() {
		return id;
	}
	public String getType() {return type;}


	@Override
	public String toString() {
		String format = "{" +
				"\"id\":" + id +
				",\"siteName\":\"" + siteName + "\"" +
				",\"tagName\":\"" + tagName + "\"" +
				",\"key\":\"" + key + "\"" +
				",\"value\":\"" + value + "\"" +
				",\"type\":\"" + type + "\"" +
				",\"dateString\":\"" + dateString + "\"" +
				",\"video\":\"" + video + "\"" +
				",\"title\":\"" + title.replaceAll("\"", "") + "\"" +
				",\"text\":\"" + text.replaceAll("\"", "") + "\"" +
				"}";
		//System.out.println(format);
		return format;
	}
	public String toHtml() {

		if(!key.isEmpty()){return key;}
		if(!value.isEmpty()){return value;}
		if(!title.isEmpty()){return title;}
		if(!text.isEmpty()){return text;}
		if(!video.isEmpty()){return video;}
		if(!dateString.isEmpty()){return dateString;}
		return "";
	}

	public String toJson() {
		String format = "{" +
				"\"id\":" + id +
				",\"siteName\":\"" + siteName + "\"" +
				",\"key\":\"" + key + "\"" +
				",\"value\":\"" + value + "\"" +
				",\"date\":\"" + dateString + "\"" +
				",\"text\":\"" + text.replaceAll("\"", "")
				.replaceAll(":", "-") + "\"" +
				"}";
		//System.out.println(format);
		return format;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TagMap tagMap = (TagMap) o;
		return Objects.equals(key, tagMap.key)
				&& Objects.equals(value, tagMap.value)
				&& Objects.equals(siteName, tagMap.siteName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(text, key, value, siteName);
	}
}
