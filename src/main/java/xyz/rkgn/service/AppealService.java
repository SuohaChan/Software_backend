package xyz.rkgn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.rkgn.common.Result;
import xyz.rkgn.dto.AppealQueryDto;
import xyz.rkgn.dto.AppealSubmitDto;
import xyz.rkgn.dto.AppealUpdateDto;
import xyz.rkgn.entity.Appeal;

import java.util.List;

/**
 * @author SuohaChan
 * @data 2025/9/14
 */
public interface AppealService extends IService<Appeal> {
    // 提交申诉
    Long submitAppeal(AppealSubmitDto dto);

    // 查询用户的申诉列表
    List<Appeal> getUserAppeals(Long userId);

    // 查询处理人的申诉列表
    List<Appeal> getHandlerAppeals(Long handlerId);

    // 查询指定状态的申诉列表
    List<Appeal> getAppealsByStatus(String status);

    // 处理申诉（更新状态）
    boolean handleAppeal(AppealUpdateDto dto);

    Result page(AppealQueryDto queryDTO, Integer pageNum, Integer pageSize);
}
