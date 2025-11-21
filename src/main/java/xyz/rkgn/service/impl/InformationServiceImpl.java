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
import xyz.rkgn.dto.AddInformationDto;
import xyz.rkgn.dto.InformationQueryDto;
import xyz.rkgn.dto.InformationSimpleShowDto;
import xyz.rkgn.entity.Information;
import xyz.rkgn.entity.StudentClass;
import xyz.rkgn.mapper.InformationMapper;
import xyz.rkgn.service.InformationService;
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
public class InformationServiceImpl extends ServiceImpl<InformationMapper, Information> implements InformationService {
    // 每页默认10条数据（可通过参数覆盖）
    private static final Integer DEFAULT_PAGE_SIZE = 10;

    @Resource
    private AliOSSUtils aliOSSUtils;

    private final StudentClassService studentClassService;

    public InformationServiceImpl(StudentClassService studentClassService) {
        this.studentClassService = studentClassService;
    }

    /**
     * 添加资讯
     *
     * @param addInformationDto images 资讯实体
     * @return 操作结果
     */
    @Override
    @Transactional
    public Result addInformation(AddInformationDto addInformationDto, MultipartFile[] images) {
        try {
            Information information = BeanUtil.copyProperties(addInformationDto, Information.class);
            // 2. 处理图片上传
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
                information.setImageUrls(String.join(",", imageUrls));
            }

            boolean saved = save(information);
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
     * 根据ID删除资讯
     *
     * @param id 资讯ID
     * @return 操作结果
     */
    @Override
    @Transactional
    public Result deleteInformationById(Long id) {
        if (id == null) {
            return Result.fail("ID不能为空");
        }
        boolean removed = removeById(id);
        return removed ? Result.ok("资讯删除成功") : Result.fail("资讯不存在或已删除");
    }

    /**
     * 条件分页查询资讯
     *
     * @param queryDTO 查询条件
     * @param pageNum  页码
     * @param pageSize 每页条数
     * @return 分页查询结果
     */
    @Override
    public Result page(InformationQueryDto queryDTO, Integer pageNum, Integer pageSize) {
        // 处理默认分页参数
        pageNum = (pageNum == null || pageNum <= 0) ? 1 : pageNum;
        pageSize = (pageSize == null || pageSize <= 0) ? DEFAULT_PAGE_SIZE : pageSize;

        // 构建查询条件
        LambdaQueryWrapper<Information> queryWrapper = new LambdaQueryWrapper<>();

        // ID精确查询
        if (queryDTO.getId() != null) {
            queryWrapper.eq(Information::getId, queryDTO.getId());
        }

        // 标题模糊查询
        if (queryDTO.getTitle() != null && !queryDTO.getTitle().trim().isEmpty()) {
            queryWrapper.like(Information::getTitle, queryDTO.getTitle().trim());
        }

        // 发布状态查询
        if (queryDTO.getIsPublished() != null) {
            queryWrapper.eq(Information::getIsPublished, queryDTO.getIsPublished());
        }

        // 创建时间范围查询（大于等于传入时间）
        List<DateTimeFormatter> formatters = Arrays.asList(
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd")
        );

        LocalDateTime queryStartTime = null;
        if (queryDTO.getCreateTime() != null && !queryDTO.getCreateTime().trim().isEmpty()) {
            queryStartTime = parseDateTime(queryDTO.getCreateTime().trim(), formatters);
        }

        if (queryStartTime != null) {
            queryWrapper.ge(Information::getCreateTime, queryStartTime);
        }

        // 执行分页查询
        Page<Information> page = new Page<>(pageNum, pageSize);
        baseMapper.selectPage(page, queryWrapper);

        return Result.ok(page);
    }

    /**
     * 辅助方法：解析时间字符串为LocalDateTime
     *
     * @param timeStr    时间字符串
     * @param formatters 可能的时间格式器列表
     * @return 解析后的LocalDateTime，失败则返回null
     */
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


    /**
     * 更新资讯
     *
     * @param information 资讯实体（需包含ID）
     * @return 操作结果
     */
    @Override
    @Transactional
    public Result updateInformation(Information information) {
        if (information.getId() == null) {
            return Result.fail("ID不能为空");
        }
        // 验证资讯是否存在
        Information existing = getById(information.getId());
        if (existing == null) {
            return Result.fail("资讯不存在");
        }
        // 执行更新（mybatis-plus会自动忽略null值字段）
        boolean updated = updateById(information);
        return updated ? Result.ok("资讯更新成功") : Result.fail("资讯更新失败");
    }

    @Override
    public Result getInformationByUserId(Long id) {

        StudentClass studentClass = studentClassService.searchClassByStudentId(id);
        log.info("学生班级信息: {}", studentClass);

        LambdaQueryWrapper<Information> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Information::getIsPublished,1);

        // 查询符合条件的资讯：
        // 1. 校级资讯 (scope = "校级")
        // 2. 院级资讯且学院匹配 (scope = "院级" 且 college 匹配)
        // 3. 班级资讯且学院和班级都匹配 (scope = "班级" 且 college 和 clazz 都匹配)
        queryWrapper.and(wrapper -> wrapper
                .eq(Information::getScope, "校级")
                .or(w -> w.eq(Information::getScope, "院级")
                        .eq(Information::getCollege, studentClass.getCollege()))
                .or(w -> w.eq(Information::getScope, "班级")
                        .eq(Information::getCollege, studentClass.getCollege())
                        .eq(Information::getClazz, studentClass.getClazz()))
        );
        // 只选择需要的字段：标题、是否必看、更新时间、图片URL
        queryWrapper.select(Information::getId, Information::getTitle, Information::getIsRequired,
                Information::getUpdateTime, Information::getImageUrls,Information::getScope,Information::getCollege,Information::getClazz);

        // 执行查询
        List<Information> informationList = baseMapper.selectList(queryWrapper);
        List<InformationSimpleShowDto> dtoList = informationList.stream()
                .map(information -> BeanUtil.copyProperties(information, InformationSimpleShowDto.class))
                .toList();

        log.info("查询到的资讯数量: {}", dtoList.size());
        return Result.ok(dtoList);
    }

    @Override
    public Result getInformationById(Long id) {
        if (id == null)
            return Result.fail("参数id不能为空");
        try {
            LambdaQueryWrapper<Information> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Information::getId, id);
            Information information = getOne(queryWrapper);

            // 结果判空处理
            if (information == null) {
                return Result.fail("未找到对应信息");
            }

            return Result.ok(information);
        } catch (Exception e) {
            // 异常处理
            return Result.fail("查询信息失败：" + e.getMessage());
        }
    }
}
