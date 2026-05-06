package com.wintersports.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MedalCountryResponse {
    private String country;
    private long gold;
    private long silver;
    private long bronze;
    private long total;
}