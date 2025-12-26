package com.example.MovieWebsiteProject.Dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PopularHourResponse {
    private LocalDate day;
    private Integer startHour;
    private Integer endHour;
    private Long userCount;
}

