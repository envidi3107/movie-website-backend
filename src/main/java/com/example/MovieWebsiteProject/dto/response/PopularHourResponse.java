package com.example.MovieWebsiteProject.Dto.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PopularHourResponse {
    private LocalDate day;
    private Integer startHour;
    private Integer endHour;
    private Long userCount;
}
