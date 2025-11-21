package xyz.rkgn.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.rkgn.common.Result;
import xyz.rkgn.dto.AppealQueryDto;
import xyz.rkgn.dto.AppealSubmitDto;
import xyz.rkgn.dto.AppealUpdateDto;
import xyz.rkgn.entity.Appeal;
import xyz.rkgn.mapper.AppealMapper;
import xyz.rkgn.service.AppealService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

/**
 * @author SuohaChan
 * @data 2025/9/14
 */
@Service
public class AppealServiceImpl extends ServiceImpl<AppealMapper, Appeal> implements AppealService {
    @Override
    @Transactional
    public Long submitAppeal(AppealSubmitDto dto) {
        Appeal appeal = BeanUtil.copyProperties(dto, Appeal.class);
        appeal.setSubmitTime(LocalDateTime.now());
        this.save(appeal);
        return appeal.getId();  // 返回自增ID
    }

    @Override
    public List<Appeal> getUserAppeals(Long userId) {
        QueryWrapper<Appeal> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .orderByDesc("submit_time")
                .select("id",  "appeal_type", "appeal_title", "appeal_description",  "submit_time",  "status");
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<Appeal> getHandlerAppeals(Long handlerId) {
        QueryWrapper<Appeal> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("handler_id", handlerId)
                .orderByDesc("update_time");
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<Appeal> getAppealsByStatus(String status) {
        QueryWrapper<Appeal> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("submit_time");
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional
    public boolean handleAppeal(AppealUpdateDto dto) {
        Appeal appeal = this.getById(dto.getId());
        if (appeal == null) {
            throw new RuntimeException("申诉不存在");
        }

        // 校验状态流转是否合法
        if ("completed".equals(appeal.getStatus()) | "rejected".equals(appeal.getStatus())) {
            throw new RuntimeException("该申诉已完成处理，无法再次操作");
        }

        // 更新申诉信息
        Appeal updateAppeal = BeanUtil.copyProperties(dto, Appeal.class);
        // 根据状态设置对应的时间
        LocalDateTime now = LocalDateTime.now();
        switch (dto.getStatus()) {
            case "accept":
                updateAppeal.setAcceptTime(now);
                break;
            case "processing":
                updateAppeal.setProcessingTime(now);
                break;
            case "complete":
                updateAppeal.setCompleteTime(now);
                break;
            case "rejected":
                updateAppeal.setCompleteTime(now);
                break;
        }
        return this.updateById(updateAppeal);
    }

    @Override
    public Result page(AppealQueryDto queryDTO, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Appeal> queryWrapper = new LambdaQueryWrapper<>();

        try {
            Long id = Long.valueOf(queryDTO.getId());
            queryWrapper.eq(Appeal::getId, id);
        } catch (NumberFormatException e) {
            log.warn("Invalid ID format: " + queryDTO.getId());
        }

        if (queryDTO.getAppealerId() != null) {
            queryWrapper.eq(Appeal::getUserId, queryDTO.getAppealerId());
        }

        if (queryDTO.getStatus() != null ){
            if(queryDTO.getStatus().equals("complete"))
                queryWrapper.eq(Appeal::getStatus, "completed").or(wrapper -> wrapper.eq(Appeal::getStatus, "rejected"));
            else
                queryWrapper.eq(Appeal::getStatus, queryDTO.getStatus());
        }

        List<DateTimeFormatter> formatters = Arrays.asList(
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd")
        );

        LocalDateTime queryStart = null;
        if (queryDTO.getStartTime() != null && !queryDTO.getStartTime().isEmpty()) {
            queryStart = parseDateTime(queryDTO.getStartTime(), formatters);
            if (queryStart != null) {
                queryWrapper.gt(Appeal::getUpdateTime, queryStart);
            }
        }
        Page<Appeal> page = new Page<>(pageNum, pageSize);
        baseMapper.selectPage(page, queryWrapper);

        return Result.ok(page);
    }

    private LocalDateTime parseDateTime(String timeStr, List<DateTimeFormatter> formatters) {
        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDateTime.parse(timeStr, formatter);
            } catch (DateTimeParseException e) {
                // 忽略当前格式解析失败的异常，尝试下一种格式
            }
        }
        return null;
    }
}
