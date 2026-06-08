package com.library.task;

import com.library.entity.BorrowRecord;
import com.library.repository.BorrowRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * 借阅逾期检测定时任务
 * 每天凌晨2点扫描到期未还的借阅记录，更新状态为"已逾期"
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BorrowOverdueTask {

    private final BorrowRecordRepository borrowRecordRepository;

    /**
     * 每天凌晨2点执行逾期检测
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void checkOverdueRecords() {
        log.info("开始执行借阅逾期检测...");
        try {
            List<BorrowRecord> overdueRecords = borrowRecordRepository.findOverdueRecords(LocalDate.now());
            if (overdueRecords.isEmpty()) {
                log.info("未发现逾期记录");
                return;
            }
            for (BorrowRecord record : overdueRecords) {
                record.setStatus(2); // 0=借阅中, 2=已逾期
            }
            borrowRecordRepository.saveAll(overdueRecords);
            log.info("逾期检测完成，更新逾期记录 {} 条", overdueRecords.size());
        } catch (Exception e) {
            log.error("逾期检测执行失败", e);
        }
    }
}
