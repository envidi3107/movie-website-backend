package com.example.MovieWebsiteProject.Dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
