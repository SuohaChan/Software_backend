package xyz.rkgn.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.rkgn.common.Result;
import xyz.rkgn.entity.StudentItem;
import xyz.rkgn.mapper.StudentItemMapper;
import xyz.rkgn.service.StudentItemService;

import java.util.List;

/**
 * @author ljx
 * @description 针对表【tb_student_item(学生和开学前要准备的物品的关系)】的数据库操作Service实现
 * @createDate 2024-02-28 17:59:53
 */
@Service
public class StudentItemServiceImpl extends ServiceImpl<StudentItemMapper, StudentItem>
        implements StudentItemService {

    @Override
    public Result addStudentItem(StudentItem studentItem) {
        save(studentItem);
        return Result.ok();
    }

    @Override
    public Result queryStudentItemById(Long id) {
        StudentItem studentItem = getById(id);
        return Result.ok(studentItem);
    }

    @Override
    public Result listStudentItem(StudentItem studentItem) {
        return null;
    }

    @Override
    public Result updateStudentItem(StudentItem studentItem) {
        return null;
    }

    @Override
    public Result deleteStudentItemById(List<Long> ids) {
        return null;
    }
}




