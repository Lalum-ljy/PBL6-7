package com.ljy.pbl6.controller;

import com.ljy.pbl6.common.Response;
import com.ljy.pbl6.entity.Notice;
import com.ljy.pbl6.service.NoticeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notice")
public class NoticeController {

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @GetMapping("/{id}")
    public Response<Notice> findById(@PathVariable Long id) {
        Notice notice = noticeService.findById(id);
        return Response.success(notice);
    }

    @GetMapping
    public Response<List<Notice>> findAll() {
        List<Notice> notices = noticeService.findAll();
        return Response.success(notices);
    }

    @PostMapping
    public Response<Notice> create(@RequestBody Notice notice) {
        Notice createdNotice = noticeService.create(notice);
        return Response.success(createdNotice);
    }

    @PutMapping
    public Response<Notice> update(@RequestBody Notice notice) {
        Notice updatedNotice = noticeService.update(notice);
        return Response.success(updatedNotice);
    }

    @DeleteMapping("/{id}")
    public Response<Void> delete(@PathVariable Long id) {
        noticeService.delete(id);
        return Response.success(null);
    }
}
