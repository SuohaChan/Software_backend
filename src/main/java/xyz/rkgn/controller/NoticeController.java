package xyz.rkgn.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.rkgn.annotation.mySystemLog;
import xyz.rkgn.common.Result;
import xyz.rkgn.dto.AddNoticeDto;
import xyz.rkgn.dto.NoticeQueryDto;
import xyz.rkgn.service.NoticeService;

@RestController
@RequestMapping("notice")
public class NoticeController {
    @Resource
    private NoticeService noticeService;

    /*
     * 添加通知（文本参数用DTO接收，图片单独接收）
     *
     */
    @PostMapping
    @mySystemLog(xxbusinessName = "添加通知")
    public Result addNotice(
            // 文本参数用NoticeDTO接收（自动映射FormData中的文本字段）
            @ModelAttribute AddNoticeDto addNoticeDTO,
            // 图片文件单独接收（允许为空）
            @RequestParam(value = "images", required = false) MultipartFile[] images
    ) {
        // 调用Service，传入DTO和图片
        return noticeService.addNotice(addNoticeDTO, images);
    }

    /**
     * 删除通知
     *
     * @param id 要删除的通知的id
     * @return Result对象
     */
    @DeleteMapping
    public Result deleteNoticeById(@RequestParam("ids") Long id) {
        return noticeService.deleteNoticeById(id);
    }
    /**
     * 条件分页查询通知
     *
     * @param queryDTO 查询条件
     * @param pageNum  页码
     * @param pageSize 页大小
     * @return Result对象
     */
    @GetMapping("/page")
    @mySystemLog(xxbusinessName = "条件分页查询通知")
    public Result page(NoticeQueryDto queryDTO, Integer pageNum, Integer pageSize) {
        return noticeService.page(queryDTO, pageNum, pageSize);
    }

    /**
     *根据用户id显示简短资讯
     *
     */
    @GetMapping("/userId/{userId}")
    public Result getNoticeByUserId(@PathVariable Long userId) {
        return noticeService.getNoticeByUserId(userId);
    }

    @mySystemLog(xxbusinessName = "查询单个通知")
    @GetMapping("NotId/{Id}")
    public Result getNoticeById(@PathVariable Long Id) {
        return noticeService.getNoticeById(Id);
    }

    /**
     *根据用户id显示简短资讯
     *
     */
    @GetMapping("/counselorId/{userId}")
    public Result getNoticeByCounselorId(@PathVariable Long userId) {
        return noticeService.getNoticeByCounselorId(userId);
    }

}
