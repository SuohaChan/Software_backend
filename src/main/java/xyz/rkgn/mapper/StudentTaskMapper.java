package xyz.rkgn.mapper;

import org.apache.ibatis.annotations.Select;
import xyz.rkgn.entity.StudentTask;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import xyz.rkgn.entity.Task;

import java.util.List;

import java.util.List;

/**
* @author ljx
* @description 针对表【tb_student_task(学生和任务关系)】的数据库操作Mapper
* @createDate 2024-02-28 17:59:39
* @Entity xyz.rkgn.entity.StudentTask
*/
public interface StudentTaskMapper extends BaseMapper<StudentTask> {
    //批量插入
    void batchInsert(@Param("list") List<StudentTask> list);

    //查询未完成任务
    List<Task> selectUnfinishedTasks(
            @Param("studentId") Long studentId,
            @Param("college") String studentCollege,
            @Param("clazz") String studentClazz
    );

    //查询学生对某个任务的完成状态（返回status值）
    @Select("SELECT status FROM tb_student_task " +
            "WHERE student_id = #{studentId} AND task_id = #{taskId}")
    Integer getTaskStatus(@Param("studentId") Long studentId, @Param("taskId") Long taskId);
}




