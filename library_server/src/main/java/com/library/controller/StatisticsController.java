package com.library.controller;

import com.library.common.Result;
import com.library.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@Tag(name = "统计管理", description = "数据统计相关接口")
public class StatisticsController {
    
    private final StatisticsService statisticsService;
    
    @GetMapping
    @Operation(summary = "获取统计数据")
    public Result<Map<String, Object>> getStatistics() {
        return Result.success(statisticsService.getStatistics());
    }
}
