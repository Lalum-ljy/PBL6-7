package com.ljy.pbl6.dto;

import lombok.Data;

import java.util.List;

@Data
public class PageResponse<T> {
    private List<T> data;
    private int totalPages;
    private long totalItems;
    private int currentPage;
    private int pageSize;
}
