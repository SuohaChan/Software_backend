package xyz.rkgn.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.rkgn.annotation.mySystemLog;
import xyz.rkgn.common.Result;
import xyz.rkgn.dto.InformationQueryDto;
import xyz.rkgn.dto.AddInformationDto;
import xyz.rkgn.entity.Information;
import xyz.rkgn.service.InformationService;

@RestController
@RequestMapping("information")
public class InformationController {
    @Resource
    private InformationService informationService;

    /**
     * 添加资讯
     *
     * @return Result对象
     */
    @PostMapping
    @mySystemLog(xxbusinessName = "添加资讯")
    public Result addInformation(  // 文本参数用NoticeDTO接收（自动映射FormData中的文本字段）
                                   @ModelAttribute AddInformationDto addInformationDto,
                                   // 图片文件单独接收（允许为空）
                                   @RequestParam(value = "images", required = false) MultipartFile[] images) {
        return informationService.addInformation(addInformationDto, images);
    }

    /**
     * 删除资讯
     *
     * @param id 要删除的资讯的id
     * @return Result对象
     */
    @DeleteMapping
    public Result deleteInformationById(@RequestParam("ids") Long id) {
        return informationService.deleteInformationById(id);
    }

    /**
     * 条件分页查询资讯
     *
     * @param queryDTO 查询条件
     * @param pageNum  页码
     * @param pageSize 页大小
     * @return Result对象
     */
    @GetMapping("/page")
    @mySystemLog(xxbusinessName = "条件分页查询资讯")
    public Result page(InformationQueryDto queryDTO, Integer pageNum, Integer pageSize) {
        return informationService.page(queryDTO, pageNum, pageSize);
    }

    /**
     * 更新资讯
     *
     * @param information 要更新的资讯
     * @return Result对象
     */
    @PutMapping
    public Result updateInformation(@RequestBody Information information) {
        return informationService.updateInformation(information);
    }

    /**
     *根据用户id显示简短资讯
     *
     */
    @GetMapping("/userId/{userId}")
    public Result getInformationByUserId(@PathVariable Long userId) {
        return informationService.getInformationByUserId(userId);
    }

    @mySystemLog(xxbusinessName = "查询单个资讯")
    @GetMapping("InfId/{Id}")
    public Result getInformationById(@PathVariable Long Id) {
        return informationService.getInformationById(Id);
    }
}