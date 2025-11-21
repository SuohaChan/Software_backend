package xyz.rkgn.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import xyz.rkgn.common.Result;
import xyz.rkgn.entity.Ad;
import xyz.rkgn.service.AdService;

import java.util.List;


/**
 * 广告接口
 */
@RestController
@RequestMapping("ad")
public class AdController {
    @Resource
    private AdService adService;

    /**
     * 添加广告
     *
     * @param ad 要添加的广告对象
     * @return Result对象
     */
    @PostMapping
    public Result addAd(@RequestBody Ad ad) {
        return adService.addAd(ad);
    }

    /**
     * 通过id查询广告
     *
     * @param id 要查询的广告的id
     * @return Result对象
     */
    @GetMapping("/{id}")
    public Result queryAdById(@PathVariable Long id) {
        return adService.queryAdById(id);
    }

    /**
     * 条件查询广告列表
     *
     * @param ad 支持通过标题查询
     * @return Result对象
     */
    @GetMapping
    public Result listAd(Ad ad) {
        return adService.listAd(ad);
    }

    /**
     * 修改广告对象
     *
     * @param ad id必需，其他需要修改的字段不为空
     * @return Result对象
     */
    @PutMapping
    public Result updateAd(@RequestBody Ad ad) {
        return adService.updateAd(ad);
    }

    /**
     * 通过id删除广告
     *
     * @param ids 要删除的广告的id，可以有多个，用逗号分隔
     * @return Result对象
     */
    @DeleteMapping
    public Result deleteAdById(@RequestParam("ids") List<Long> ids) {
        return adService.deleteAdById(ids);
    }
}