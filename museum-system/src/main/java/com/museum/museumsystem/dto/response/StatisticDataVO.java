package com.museum.museumsystem.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class StatisticDataVO {
    private String name;
    private Object value;
    private BigDecimal percentage;
}
