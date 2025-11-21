package xyz.rkgn.service;

import xyz.rkgn.common.Result;
import xyz.rkgn.entity.Item;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author ljx
* @description 针对表【tb_item(开学前要准备的物品)】的数据库操作Service
* @createDate 2024-02-17 14:26:45
*/
public interface ItemService extends IService<Item> {

    Result queryItemById(Long id);

    Result listItem(Item item);

    Result deleteItemById(List<Long> ids);

    Result updateItem(Item item);

    Result addItem(Item item);
}
