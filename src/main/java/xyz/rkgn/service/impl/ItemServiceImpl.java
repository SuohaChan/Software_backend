package xyz.rkgn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.rkgn.common.Result;
import xyz.rkgn.entity.Information;
import xyz.rkgn.entity.Item;
import xyz.rkgn.entity.StudentClass;
import xyz.rkgn.mapper.ItemMapper;
import xyz.rkgn.service.ItemService;
import xyz.rkgn.service.StudentClassService;

import java.util.Date;
import java.util.List;

/**
 * @author ljx
 * @description 针对表【tb_item(开学前要准备的物品)】的数据库操作Service实现
 * @createDate 2024-02-17 14:26:45
 */
@Slf4j
@Service
public class ItemServiceImpl extends ServiceImpl<ItemMapper, Item> implements ItemService {
    private final StudentClassService studentClassService;

    public ItemServiceImpl(StudentClassService studentClassService) {
        this.studentClassService = studentClassService;
    }

    @Override
    public Result queryItemById(Long id) {
        Item item = getById(id);
        return Result.ok(item);
    }

    @Override
    public Result listItem(Item item) {
        String name = item.getName();
        Date date = item.getDate();
        String description = item.getDescription();
        List<Item> items = lambdaQuery().like(StringUtils.isNotEmpty(name), Item::getName, name)
                .like(StringUtils.isNotEmpty(description), Item::getDescription, description)
                .eq(date != null, Item::getDate, date).list();
        return Result.ok(items);
    }

    @Override
    @Transactional
    public Result deleteItemById(List<Long> ids) {
        removeByIds(ids);
        return Result.ok();
    }

    @Override
    public Result updateItem(Item item) {
        boolean updated = updateById(item);
        if (!updated) {
            return Result.fail("物品id不存在");
        }
        return Result.ok();
    }

    @Override
    public Result addItem(Item item) {
        save(item);
        return Result.ok();
    }
}




