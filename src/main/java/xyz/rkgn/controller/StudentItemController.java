package xyz.rkgn.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import xyz.rkgn.common.Result;
import xyz.rkgn.entity.StudentItem;
import xyz.rkgn.service.StudentItemService;

import java.util.List;

@RestController
@RequestMapping("student/item")
public class StudentItemController {
    @Resource
    private StudentItemService studentItemService;

    /**
     * 添加学生和要准备的物品的关系
     *
     * @param studentItem 要添加的学生和要准备的物品的关系对象
     * @return Result对象
     */
    @PostMapping
    public Result addStudentItem(@RequestBody StudentItem studentItem) {
        return studentItemService.addStudentItem(studentItem);
    }

    /**
     * 通过id查询学生和要准备的物品的关系
     *
     * @param id 要查询的学生和要准备的物品的关系的id
     * @return Result对象
     */
    @GetMapping("/{id}")
    public Result queryStudentItemById(@PathVariable Long id) {
        return studentItemService.queryStudentItemById(id);
    }

    /**
     * 条件查询学生和要准备的物品的关系列表
     *
     * @param studentItem 支持通过标题查询
     * @return Result对象
     */
    @GetMapping
    public Result listStudentItem(StudentItem studentItem) {
        return studentItemService.listStudentItem(studentItem);
    }

    /**
     * 修改学生和要准备的物品的关系
     *
     * @param studentItem id必需，其他需要修改的字段不为空
     * @return Result对象
     */
    @PutMapping
    public Result updateStudentItem(@RequestBody StudentItem studentItem) {
        return studentItemService.updateStudentItem(studentItem);
    }

    /**
     * 通过id删除
     *
     * @param ids 要删除的学生和要准备的物品的关系的id，可以有多个，用逗号分隔
     * @return Result对象
     */
    @DeleteMapping
    public Result deleteStudentItemById(@RequestParam("ids") List<Long> ids) {
        return studentItemService.deleteStudentItemById(ids);
    }
}
