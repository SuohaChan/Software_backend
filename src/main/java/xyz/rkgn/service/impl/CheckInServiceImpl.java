package xyz.rkgn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.rkgn.common.Result;
import xyz.rkgn.dto.CheckInWeekDto;
import xyz.rkgn.entity.CheckIn;
import xyz.rkgn.entity.LevelRule;
import xyz.rkgn.entity.Student;
import xyz.rkgn.dto.CheckInStatusDto;
import xyz.rkgn.exception.BusinessException;
import xyz.rkgn.mapper.CheckInMapper;
import xyz.rkgn.mapper.LevelRuleMapper;
import xyz.rkgn.mapper.StudentMapper;
import xyz.rkgn.service.CheckInService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CheckInServiceImpl extends ServiceImpl<CheckInMapper, CheckIn> implements CheckInService {

    private final StudentMapper studentMapper;
    private final LevelRuleMapper levelRuleMapper;

    public CheckInServiceImpl(StudentMapper studentMapper, LevelRuleMapper levelRuleMapper) {
        this.studentMapper = studentMapper;
        this.levelRuleMapper = levelRuleMapper;
    }


    @Transactional(rollbackFor = Exception.class)//异常回滚
    @Override
    public Result checkInToday(Long studentId) {
        //验证学生是否存在
        Student student = studentMapper.selectById(studentId);
        if (student == null) {
            throw new BusinessException("用户不存在");
        }

        LocalDate today = LocalDate.now();
        //检验是否重复签到
        LambdaQueryWrapper<CheckIn> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CheckIn::getStudentId, studentId)
                .eq(CheckIn::getCheckInDate, today)
                .eq(CheckIn::getIsValid, 1);

        if (baseMapper.selectCount(queryWrapper) > 0 ){
            return Result.fail("今日已签到");
        }

        //签到
        CheckIn checkIn = new CheckIn();
        checkIn.setStudentId(studentId);
        checkIn.setCheckInDate(today);
        checkIn.setExperience(5); // 固定5点经验
        checkIn.setCreateTime(LocalDateTime.now());
        checkIn.setIsValid(1);
        baseMapper.insert(checkIn);

        // 更新学生总经验和等级
        int currentExp = student.getTotalExperience() == null ? 0 : student.getTotalExperience();
        int newExp = currentExp + 5;
        student.setTotalExperience(newExp);
        int newLevel = calculateLevel(newExp);
        student.setLevel(newLevel);
        student.setUpdateTime(LocalDateTime.now());
        studentMapper.updateById(student);

        CheckInStatusDto checkInStatusVO = new CheckInStatusDto();
        checkInStatusVO.setCheckedIn(true);
        checkInStatusVO.setCheckInDate(today);
        checkInStatusVO.setExperienceAdded(5);
        checkInStatusVO.setCurrentExperience(newExp);
        checkInStatusVO.setCurrentLevel(newLevel);

        return Result.ok(checkInStatusVO);
    }

    @Override
    public Result getWeekCheckStatus(Long studentId) {
        LocalDate today = LocalDate.now();
        // 获取本周周一与周日的日期
        LocalDate monedy = today.with(DayOfWeek.MONDAY) ;
        LocalDate sunday = today.with(DayOfWeek.SUNDAY);

        //查询本周签到记录
        QueryWrapper<CheckIn> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", studentId)
                .ge("check_in_date", monedy)
                .le("check_in_date", sunday)
                .eq("is_valid", 1);

        //获得已经签到的日期
        List<CheckIn> checkIns = list(queryWrapper);
        List<LocalDate> checkedDates =  checkIns.stream()
                .map(CheckIn::getCheckInDate)
                .toList();

        CheckInWeekDto  checkInWeekDto = new CheckInWeekDto();
        //获得今日签到状态
        checkInWeekDto.setTodayChecked(checkedDates.contains(today)) ;
        //获得本周签到状态
        List< Boolean> weekStatus = new ArrayList<>();
        for( LocalDate data = monedy; !data.isAfter(sunday); data = data.plusDays(1)){
            weekStatus.add(checkedDates.contains(data));
        }
        checkInWeekDto.setWeekCheckStatus(weekStatus);

        return Result.ok(checkInWeekDto);
    }


    /**
     * 计算等级
     */
    private Integer calculateLevel(int totalExperience) {
        QueryWrapper<LevelRule> queryWrapper = new QueryWrapper<>();
        queryWrapper.le("need_experience", totalExperience)
                .orderByDesc("level")
                .last("LIMIT 1");

        LevelRule levelRule = levelRuleMapper.selectOne(queryWrapper);
        return levelRule != null ? levelRule.getLevel() : 1;
    }
}
