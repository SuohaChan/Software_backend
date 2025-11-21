package xyz.rkgn.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import xyz.rkgn.common.Result;
import xyz.rkgn.entity.Gift;
import xyz.rkgn.service.GiftService;

import java.util.List;

/**
 * 礼品接口
 */
@RestController
@RequestMapping("gift")
public class GiftController {
    @Resource
    private GiftService giftService;

    /**
     * 添加礼品
     *
     * @param gift 要添加的礼品
     * @return Result对象
     */
    @PostMapping
    public Result addGift(@RequestBody Gift gift) {
        return giftService.addGift(gift);
    }

    /**
     * 通过id获取礼品
     *
     * @param id 礼品id
     * @return Result对象
     */
    @GetMapping("/{id}")
    public Result queryGiftById(@PathVariable Long id) {
        return giftService.queryGiftById(id);
    }

    /**
     * 条件查询礼品列表
     *
     * @param gift 支持通过名字和描述查询
     * @return Result对象
     */
    @GetMapping
    public Result listGift(Gift gift) {
        return giftService.listGift(gift);
    }

    /**
     * 修改礼品
     *
     * @param gift id必需，其他需要修改的字段不为空
     * @return Result对象
     */
    @PutMapping
    public Result updateGift(@RequestBody Gift gift) {
        return giftService.updateGift(gift);
    }

    /**
     * 通过id删除礼品
     *
     * @param ids 要删除的礼品的id，可以有多个，用逗号分隔
     * @return Result
     */
    @DeleteMapping
    public Result deleteGiftById(@RequestParam("ids") List<Long> ids) {
        return giftService.deleteGiftById(ids);
    }
}
