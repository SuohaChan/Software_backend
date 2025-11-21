package xyz.rkgn.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import xyz.rkgn.entity.StudentGift;
import xyz.rkgn.service.StudentGiftService;
import xyz.rkgn.mapper.StudentGiftMapper;
import org.springframework.stereotype.Service;

/**
* @author ljx
* @description 针对表【tb_student_gift(学生和礼品关系)】的数据库操作Service实现
* @createDate 2024-02-17 15:41:26
*/
@Service
public class StudentGiftServiceImpl extends ServiceImpl<StudentGiftMapper, StudentGift>
    implements StudentGiftService{

}




