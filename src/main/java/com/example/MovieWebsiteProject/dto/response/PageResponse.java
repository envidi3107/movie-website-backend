package com.example.MovieWebsiteProject.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private long totalPages;
    private boolean last;
    private List<T> results;
}
