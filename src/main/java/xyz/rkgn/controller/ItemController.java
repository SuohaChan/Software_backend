package xyz.rkgn.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import xyz.rkgn.common.Result;
import xyz.rkgn.entity.Item;
import xyz.rkgn.service.ItemService;

import java.util.List;

/**
 * 要准备的物品接口
 */
@RestController
@RequestMapping("item")
public class ItemController {
    @Resource
    private ItemService itemService;

    /**
     * 添加物品
     *
     * @param item 要添加的物品
     * @return Result对象
     */
    @PostMapping
    public Result addItem(@RequestBody Item item) {
        return itemService.addItem(item);
    }

    /**
     * 通过id查询物品
     *
     * @param id 物品id
     * @return Result对象
     */
    @GetMapping("/{id}")
    public Result queryItemById(@PathVariable Long id) {
        return itemService.queryItemById(id);
    }

    /**
     * 条件查询物品
     *
     * @param item 支持通过时间和名字和描述查询
     * @return Result对象
     */
    @GetMapping
    public Result listItem(Item item) {
        return itemService.listItem(item);
    }

    /**
     * 修改物品
     *
     * @param item id必需，其他需要修改的字段不为空
     * @return Result对象
     */
    @PutMapping
    public Result updateItem(@RequestBody Item item) {
        return itemService.updateItem(item);
    }

    /**
     * 通过id删除物品
     *
     * @param ids 要删除的物品的id，可以有多个，用逗号分隔
     * @return Result对象
     */
    @DeleteMapping
    public Result deleteItemById(@RequestParam("ids") List<Long> ids) {
        return itemService.deleteItemById(ids);
    }
}