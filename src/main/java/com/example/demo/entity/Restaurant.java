package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "restaurant_info")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int rid;

    private String rname;
    private double rstar; // decimal(2, 1)
    private int rcount;
    private String addr;
    private String phone;
    private String rtag;
    private String rloc;

    // 운영 시간 관련 필드들
    private String mon;
    private String tue;
    private String wed;
    private String thr;
    private String fri;
    private String sat;
    private String sun;

    private String image1;
    private String image2;

    private Integer keyword1;
    private Integer keyword2;
    private Integer keyword3;
    private Integer keyword4;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<Menu> menus;

    // Getters and Setters
    public int getRid() {
        return rid;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    public String getRname() {
        return rname;
    }

    public void setRname(String rname) {
        this.rname = rname;
    }

    public double getRating() {
        return rstar;
    }

    public void setRating(double rating) {
        this.rstar = rating;
    }

    public int getRcount() {
        return rcount;
    }

    public void setRcount(int rcount) {
        this.rcount = rcount;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRtag() {
        return rtag;
    }

    public void setRtag(String rtag) {
        this.rtag = rtag;
    }

    public String getRloc() {
        return rloc;
    }

    public void setRloc(String rloc) {
        this.rloc = rloc;
    }

    public String getMon() {
        return mon;
    }

    public void setMon(String mon) {
        this.mon = mon;
    }

    public String getTue() {
        return tue;
    }

    public void setTue(String tue) {
        this.tue = tue;
    }

    public String getWed() {
        return wed;
    }

    public void setWed(String wed) {
        this.wed = wed;
    }

    public String getThr() {
        return thr;
    }

    public void setThr(String thr) {
        this.thr = thr;
    }

    public String getFri() {
        return fri;
    }

    public void setFri(String fri) {
        this.fri = fri;
    }

    public String getSat() {
        return sat;
    }

    public void setSat(String sat) {
        this.sat = sat;
    }

    public String getSun() {
        return sun;
    }

    public void setSun(String sun) {
        this.sun = sun;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public Integer getKeyword1() {
        return keyword1;
    }

    public void setKeyword1(Integer keyword1) {
        this.keyword1 = keyword1;
    }

    public Integer getKeyword2() {
        return keyword2;
    }

    public void setKeyword2(Integer keyword2) {
        this.keyword2 = keyword2;
    }

    public Integer getKeyword3() {
        return keyword3;
    }

    public void setKeyword3(Integer keyword3) {
        this.keyword3 = keyword3;
    }

    public Integer getKeyword4() {
        return keyword4;
    }

    public void setKeyword4(Integer keyword4) {
        this.keyword4 = keyword4;
    }

    public List<Menu> getMenus() {
        return menus;
    }

    public void setMenus(List<Menu> menus) {
        this.menus = menus;
        if (menus != null) {
            for (Menu menu : menus) {
                menu.setRestaurant(this);
            }
        }
    }
}
