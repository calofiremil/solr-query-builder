package com.tzapps.solrqc;


import java.time.LocalDateTime;

public class Aco extends Aggregate {
    private String name;
    private String desc;
    private String publicityType;
    private Integer price;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer minPriceDaysBefore;

    public Integer getMinPriceDaysBefore() {
        return minPriceDaysBefore;
    }

    public void setMinPriceDaysBefore(Integer minPriceDaysBefore) {
        this.minPriceDaysBefore = minPriceDaysBefore;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public void setAggregateId(String aggregateId) {
        this.aggregateId = aggregateId;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPublicityType() {
        return publicityType;
    }

    public void setPublicityType(String publicityType) {
        this.publicityType = publicityType;
    }
}
