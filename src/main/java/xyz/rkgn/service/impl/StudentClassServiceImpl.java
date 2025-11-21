package xyz.rkgn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.rkgn.entity.StudentClass;
import xyz.rkgn.mapper.StudentClassMapper;
import xyz.rkgn.service.StudentClassService;

/**
 * @author SuohaChan
 * @data 2025/9/13
 */

@Service
public class StudentClassServiceImpl extends ServiceImpl<StudentClassMapper, StudentClass> implements StudentClassService {
    @Override
    public StudentClass searchClassByStudentId(Long id) {
        if (id == null) {
            return null;
        }
        LambdaQueryWrapper<StudentClass> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StudentClass::getStudentId, id);
        return baseMapper.selectOne(queryWrapper);
    }
}
