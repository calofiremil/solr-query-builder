package com.tzapps.solrqc;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Aco extends Aggregate {
    private String name;
    private String description;
    private String publicityType;
    private Integer price;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer minPriceDaysBefore;

}
