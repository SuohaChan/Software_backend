package xyz.rkgn.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.aliyun.oss.ClientException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import xyz.rkgn.common.Result;
import xyz.rkgn.dto.AddNoticeDto;
import xyz.rkgn.dto.NoticeQueryDto;
import xyz.rkgn.dto.NoticeSimpleShowDto;
import xyz.rkgn.entity.Notice;
import xyz.rkgn.entity.StudentClass;
import xyz.rkgn.mapper.NoticeMapper;
import xyz.rkgn.service.NoticeService;
import xyz.rkgn.service.StudentClassService;
import xyz.rkgn.utils.AliOSSUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {
    @Resource
    private AliOSSUtils aliOSSUtils;

    private final StudentClassService studentClassService;

    public NoticeServiceImpl(StudentClassService studentClassService) {
        this.studentClassService = studentClassService;
    }


    @Override
    @Transactional
    public Result addNotice(AddNoticeDto addNoticeDTO, MultipartFile[] images) {
        try {
            Notice notice = BeanUtil.copyProperties(addNoticeDTO, Notice.class);

            if (images != null && images.length > 0) {
                List<String> imageUrls = new ArrayList<>();
                for (MultipartFile image : images) {
                    if (!image.isEmpty()) {
                        String url = aliOSSUtils.upload(image); // 调用OSS工具类上传
                        imageUrls.add(url);
                        log.info("图片上传成功：{}", url);
                    }
                }
                // 设置图片URL到实体
                notice.setImageUrls(String.join(",", imageUrls));
            }

            boolean saved = save(notice);
            return saved ? Result.ok("通知创建成功") : Result.fail("通知创建失败");

        } catch (IOException e) {
            log.error("图片文件读取失败", e);
            return Result.fail("图片上传失败：文件无法读取");
        } catch (ClientException e) {
            log.error("阿里云OSS上传失败", e);
            return Result.fail("图片上传失败：OSS服务错误");
        } catch (com.aliyuncs.exceptions.ClientException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 删除通知
     */
    @Override
    @Transactional
    public Result deleteNoticeById(Long id) {
        boolean removed = removeById(id);
        return removed ? Result.ok("删除成功") : Result.fail("通知不存在或已删除");
    }
    /**
     * 条件分页查询通知
     */
    @Override
    public Result page(NoticeQueryDto queryDTO, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Notice> queryWrapper = new LambdaQueryWrapper<>();

        if(queryDTO.getId() != null){ queryWrapper.eq(Notice::getId, queryDTO.getId());}
        if(queryDTO.getTitle() != null && !queryDTO.getTitle().trim().isEmpty()){ queryWrapper.like(Notice::getTitle, queryDTO.getTitle().trim());}
        if(queryDTO.getIsPublished() != null){ queryWrapper.eq(Notice::getIsPublished, queryDTO.getIsPublished());}

        //转换格式
        List<DateTimeFormatter> formatters = Arrays.asList(
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
        );

        LocalDateTime queryStart = null;
        if (queryDTO.getCreateTime() != null && !queryDTO.getCreateTime().isEmpty()) {
            queryStart = parseDateTime(queryDTO.getCreateTime(), formatters);
        }

        System.out.println("queryStart: " + queryStart);
        if (queryStart != null) {   queryWrapper.ge(Notice::getCreateTime,    queryStart);}

        Page<Notice> page = new Page<>(pageNum, pageSize);
        baseMapper.selectPage(page, queryWrapper);
        return Result.ok(page);

    }

    @Override
    public Result getNoticeByUserId(Long userId) {
        StudentClass studentClass = studentClassService.searchClassByStudentId(userId);
        log.info("学生班级信息: {}", studentClass);

        LambdaQueryWrapper<Notice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Notice::getIsPublished, 1);

        // 查询符合条件的通知：
        // 1. 校级资讯 (scope = "校级")
        // 2. 院级资讯且学院匹配 (scope = "院级" 且 college 匹配)
        // 3. 班级资讯且学院和班级都匹配 (scope = "班级" 且 college 和 clazz 都匹配)
        queryWrapper.and(wrapper -> wrapper
                .eq(Notice::getScope, "校级")
                .or(w -> w.eq(Notice::getScope, "院级")
                        .eq(Notice::getCollege, studentClass.getCollege()))
                .or(w -> w.eq(Notice::getScope, "班级")
                        .eq(Notice::getCollege, studentClass.getCollege())
                        .eq(Notice::getClazz, studentClass.getClazz()))
        );
        // 只选择需要的字段：标题、是否必看、更新时间、图片URL
        queryWrapper.select(Notice::getId, Notice::getTitle, Notice::getIsRequired,
                Notice::getUpdateTime, Notice::getImageUrls,Notice::getScope,Notice::getCollege,Notice::getClazz);

        List<Notice> noticeList = baseMapper.selectList(queryWrapper);
        List<NoticeSimpleShowDto> dtoList = noticeList.stream()
                .map(notice -> BeanUtil.copyProperties(notice, NoticeSimpleShowDto.class))
                .toList();

        log.info("查询到的通知数量: {}", dtoList.size());
        return Result.ok(dtoList);
    }

    @Override
    public Result getNoticeById(Long id) {
        if (id == null)
            return Result.fail("参数id不能为空");
        try {
            LambdaQueryWrapper<Notice> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Notice::getId, id);
            Notice notice = getOne(queryWrapper);

            // 结果判空处理
            if (notice == null) {
                return Result.fail("未找到对应信息");
            }

            return Result.ok(notice);
        } catch (Exception e) {
            // 异常处理
            return Result.fail("查询信息失败：" + e.getMessage());
        }
    }

    @Override
    public Result getNoticeByCounselorId(Long userId) {
        LambdaQueryWrapper<Notice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Notice::getIsPublished, 1);
        // 只选择需要的字段
        queryWrapper.select(Notice::getId, Notice::getTitle, Notice::getIsRequired,
                Notice::getUpdateTime, Notice::getDesc);

        List<Notice> noticeList = baseMapper.selectList(queryWrapper);
        return Result.ok(noticeList );
    }


    // 辅助方法：解析时间字符串
    private LocalDateTime parseDateTime(String timeStr, List<DateTimeFormatter> formatters) {
        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDateTime.parse(timeStr, formatter);
            } catch (DateTimeParseException ignored) {
            }
        }
        log.warn("无法解析时间格式: {}");
        return null;
    }


}
