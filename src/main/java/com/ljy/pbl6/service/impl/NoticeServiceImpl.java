package com.ljy.pbl6.service.impl;

import com.ljy.pbl6.entity.Notice;
import com.ljy.pbl6.mapper.NoticeMapper;
import com.ljy.pbl6.service.NoticeService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NoticeServiceImpl implements NoticeService {

    private final NoticeMapper noticeMapper;

    public NoticeServiceImpl(NoticeMapper noticeMapper) {
        this.noticeMapper = noticeMapper;
    }

    @Override
    public Notice findById(Long id) {
        return noticeMapper.findById(id);
    }

    @Override
    public List<Notice> findAll() {
        return noticeMapper.findAll();
    }

    @Override
    public Notice create(Notice notice) {
        notice.setCreateTime(LocalDateTime.now());
        noticeMapper.insert(notice);
        return notice;
    }

    @Override
    public Notice update(Notice notice) {
        noticeMapper.update(notice);
        return notice;
    }

    @Override
    public void delete(Long id) {
        noticeMapper.delete(id);
    }
}
