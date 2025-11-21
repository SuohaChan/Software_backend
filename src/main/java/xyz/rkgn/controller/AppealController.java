package xyz.rkgn.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.rkgn.annotation.mySystemLog;
import xyz.rkgn.common.Result;
import xyz.rkgn.dto.AppealQueryDto;
import xyz.rkgn.dto.AppealSubmitDto;
import xyz.rkgn.dto.AppealUpdateDto;
import xyz.rkgn.dto.NoticeQueryDto;
import xyz.rkgn.entity.Appeal;
import xyz.rkgn.service.AppealService;

import java.util.List;

/**
 * @author SuohaChan
 * @data 2025/9/14
 */

@RestController
@Slf4j
@RequestMapping("/appeal")
public class AppealController {

    @Autowired
    private AppealService appealService;

    /**
     * 提交申诉
     */
    @PostMapping
    public Result submitAppeal(@Validated @RequestBody AppealSubmitDto dto) {
        try {
            Long appealId = appealService.submitAppeal(dto);
            return Result.ok(appealId);
        } catch (Exception e) {
            log.error("提交申诉失败", e);
            return Result.fail( "提交申诉失败：" + e.getMessage());
        }
    }

    /**
     * 查询申诉详情
     */
    @GetMapping("/{id}")
    public Result getAppealDetail(@PathVariable Long id) {
        try {
            Appeal appeal = appealService.getById(id);
            if (appeal == null) {
                return Result.fail( "申诉不存在");
            }
            return Result.ok(appeal);
        } catch (Exception e) {
            log.error("查询申诉详情失败", e);
            return Result.fail( "查询失败");
        }
    }

    /**
     * 查询用户的申诉列表
     */
    @GetMapping("/user/{userId}")
    public Result getUserAppeals(@PathVariable Long userId) {
        try {
            List<Appeal> appeals = appealService.getUserAppeals(userId);
            return Result.ok(appeals);
        } catch (Exception e) {
            log.error("查询用户申诉列表失败", e);
            return Result.fail( "查询失败");
        }
    }

    /**
     * 条件分页查询
     */
    @GetMapping("/page")
    @mySystemLog(xxbusinessName = "条件分页查询申诉")
    public Result page(AppealQueryDto queryDTO, Integer pageNum, Integer pageSize) {
        return appealService.page(queryDTO, pageNum, pageSize);
    }

    /**
     * 处理申诉
     */
    @PutMapping("/handle")
    public Result handleAppeal(@Validated @RequestBody AppealUpdateDto dto) {
        try {
            boolean success = appealService.handleAppeal(dto);
            return Result.ok(success);
        } catch (RuntimeException e) {
            log.error("处理申诉失败", e);
            return Result.fail( e.getMessage());
        } catch (Exception e) {
            log.error("处理申诉失败", e);
            return Result.fail( "处理失败");
        }
    }
}
