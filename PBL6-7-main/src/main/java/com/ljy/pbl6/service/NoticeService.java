package com.ljy.pbl6.service;

import com.ljy.pbl6.entity.Notice;

import java.util.List;

public interface NoticeService {
    Notice findById(Long id);
    List<Notice> findAll();
    Notice create(Notice notice);
    Notice update(Notice notice);
    void delete(Long id);
}
