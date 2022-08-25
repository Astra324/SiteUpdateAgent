package com.example.client.model;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "catalog")
public class CatalogItem implements SiteData{
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
    @Getter @Setter private Long id;

    @Column(name = "signature")
    @Getter @Setter private Integer signature;

    @Column(name = "href",  length = 800)
    @Getter @Setter private String href;

    @Column(name = "img_src",  length = 800)
    @Getter @Setter private String imgSrc;

    @Column(name = "site_name",  length = 50)
    @Getter @Setter private String siteName;

    @Column(name = "title",  length = 1000)
    @Getter @Setter private String title;

    @Temporal(TemporalType.DATE)
    @Column(name = "post_date")
    @Getter @Setter private Date postDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "timestamp")
    @Getter @Setter private Date timestamp;

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
