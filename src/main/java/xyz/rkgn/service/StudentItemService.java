package xyz.rkgn.service;

import xyz.rkgn.common.Result;
import xyz.rkgn.entity.StudentItem;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author ljx
* @description 针对表【tb_student_item(学生和开学前要准备的物品的关系)】的数据库操作Service
* @createDate 2024-02-28 17:59:53
*/
public interface StudentItemService extends IService<StudentItem> {

    Result addStudentItem(StudentItem studentItem);

    Result queryStudentItemById(Long id);

    Result listStudentItem(StudentItem studentItem);

    Result updateStudentItem(StudentItem studentItem);

    Result deleteStudentItemById(List<Long> ids);
}
