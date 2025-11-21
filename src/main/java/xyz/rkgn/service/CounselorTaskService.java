package xyz.rkgn.service;




import xyz.rkgn.common.Result;

/**
 * @author SuohaChan
 * @data 2025/9/10
 */
public interface CounselorTaskService {

    /**
     * 查看辅导员所属学院各班级的任务完成情况
     * @return 包含各班级任务统计信息的结果
     */
    Result getClassTask();

    /**
     * 查看辅导员发布的任务状态统计
     * @return 包含任务状态统计信息的结果 即将超时/已超时/正常进行
     */
    Result getTaskStatus();

    Result getTaskByUserId(Long userId);
}
