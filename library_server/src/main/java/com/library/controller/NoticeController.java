package com.library.controller;

import com.library.common.Result;
import com.library.entity.Notice;
import com.library.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
@Tag(name = "通知管理", description = "通知增删改查接口")
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping
    @Operation(summary = "分页查询通知")
    @PreAuthorize("isAuthenticated()")
    public Result<Page<Notice>> list(@PageableDefault(size = 10) Pageable pageable) {
        return Result.success(noticeService.listNotices(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取通知详情")
    @PreAuthorize("isAuthenticated()")
    public Result<Notice> get(@PathVariable Long id) {
        return Result.success(noticeService.getById(id));
    }

    @PostMapping
    @Operation(summary = "新增通知")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Notice> create(@Validated @RequestBody Notice notice) {
        return Result.success(noticeService.create(notice));
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改通知")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Notice> update(@PathVariable Long id, @Validated @RequestBody Notice notice) {
        return Result.success(noticeService.update(id, notice));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除通知")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        noticeService.delete(id);
        return Result.success(null);
    }

    @PostMapping("/{id}/publish")
    @Operation(summary = "发布通知")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Notice> publish(@PathVariable Long id) {
        return Result.success(noticeService.publish(id));
    }
}
