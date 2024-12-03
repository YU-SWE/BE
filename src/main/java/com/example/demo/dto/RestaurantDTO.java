package com.example.demo.dto;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestaurantDTO {
    private String rname;
    private Double rstar;
    private String image1;
    private String addr;

    private Integer rid;

    // 생성자
    public RestaurantDTO(int rid,String rname, Double rstar, String image1, String addr) {
        this.rname = rname;
        this.rstar = rstar;
        this.image1 = image1;
        this.addr = addr;
        this.rid = rid;
    }

    // Getter 및 Setter
    public String getRname() {
        return rname;
    }

    public void setRname(String rname) {
        this.rname = rname;
    }

    public Double getRstar() {
        return rstar;
    }

    public void setRstar(Double rstar) {
        this.rstar = rstar;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public Integer getRid() {
        return rid;
    }

    public void setRid(Integer rid) {
        this.rid = rid;
    }
}
