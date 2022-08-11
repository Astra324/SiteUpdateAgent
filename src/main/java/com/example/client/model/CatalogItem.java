package com.example.client.model;
import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "catalog")
public class CatalogItem {
    public CatalogItem(){}
    public CatalogItem(TagMap importedMap){
        if(importedMap != null){
            this.href = importedMap.key();
            this.imgSrc = importedMap.value();
            this.title = importedMap.getTitle();
            if(this.getTitle().isEmpty()) this.title = importedMap.getText();
            this.siteName = importedMap.getSiteName();
            if(importedMap.isNewRegistered()){
                this.postDate = new Date(System.currentTimeMillis());
                this.timestamp = new Date(System.currentTimeMillis());
            }else{
                this.postDate = importedMap.getPostDate();
            }
            this.signature = creteSignature(importedMap);
        } else{
          throw new NullPointerException("Don`t import tagMap : " + this.toString());
        }
    }
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) //AUTO, SEQUENCE, TABLE
    @Column(name = "id")
    private Long id;

    @Column(name = "signature")
    private Integer signature;

    @Column(name = "href",  length = 800)
    private String href;

    @Column(name = "img_src",  length = 800)
    private String imgSrc;

    @Column(name = "site_name",  length = 50)
    private String siteName;

    @Column(name = "title",  length = 1000)
    private String title;

    @Temporal(TemporalType.DATE)
    @Column(name = "post_date")
    private Date postDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "timestamp")
    private Date timestamp;

    public String toJson() {
        String format = "{" +
                "\"id\":" + id +
                ",\"siteName\":\"" + siteName + "\"" +
                ",\"key\":\"" + href + "\"" +
                ",\"value\":\"" + imgSrc + "\"" +
                ",\"date\":\"" + postDate.toString() + "\"" +
                ",\"text\":\"" + title.replaceAll("\"", "")
                .replaceAll(":", "-") + "\"" +
                "}";
        //System.out.println(format);
        return format;
    }
    public Date getTimestamp() {
        return timestamp;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSignature() {
        return signature;
    }

    public void setSignature(Integer signature) {
        this.signature = signature;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    @Transient
    public Integer creteSignature( TagMap map){

        int key = map.key().chars().reduce((n1, n2)->n1 + n2).orElse(0);
        int value = map.value().chars().reduce((n1, n2)->n1 + n2).orElse(0);
        int text = map.getText().chars().reduce((n1, n2)->n1 + n2).orElse(0);
        int site = map.getSiteName().chars().reduce((n1, n2)->n1 + n2).orElse(0);
        int resultInt = (key + value) * site;
        //System.out.println("Signature - " + resultInt);
        return resultInt;
    }

    @Override
    public String toString() {
        return "CatalogItem{" +
                "id=" + id +
                ", signature=" + signature +
                ", href='" + href + '\'' +
                ", imgSrc='" + imgSrc + '\'' +
                ", siteName='" + siteName + '\'' +
                ", title='" + title + '\'' +
                ", postDate=" + postDate +
                ", postDate=" + timestamp +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CatalogItem that = (CatalogItem) o;
        return Objects.equals(id, that.id) && Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, timestamp);
    }
}
