package xyz.rkgn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;
import xyz.rkgn.common.Result;
import xyz.rkgn.dto.AddNoticeDto;
import xyz.rkgn.dto.NoticeQueryDto;
import xyz.rkgn.entity.Notice;


/**
 * 通知(TbNotice)表服务接口
 *
 * @author tree
 * @since 2025-08-14 12:21:38
 */
public interface NoticeService extends IService<Notice> {

    /**
     * 添加通知
     *
     * @param addNoticeDTO images
     * @return
     */
    Result addNotice(AddNoticeDto addNoticeDTO, MultipartFile[] images);

    /**
     * 删除通知
     *
     * @param id
     * @return
     */
    Result deleteNoticeById(Long id);

    /**
     * 条件分页查询通知
     *
     * @param queryDTO
     * @param pageNum
     * @param pageSize
     * @return
     */
    Result page(NoticeQueryDto queryDTO, Integer pageNum, Integer pageSize);


    Result getNoticeByUserId(Long userId);

    Result getNoticeById(Long id);

    Result getNoticeByCounselorId(Long userId);
}
