package xyz.rkgn.service;

import xyz.rkgn.common.Result;
import xyz.rkgn.entity.Gift;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author ljx
* @description 针对表【tb_gift(礼品)】的数据库操作Service
* @createDate 2024-02-17 14:26:45
*/
public interface GiftService extends IService<Gift> {

    Result queryGiftById(Long id);

    Result listGift(Gift gift);

    Result deleteGiftById(List<Long> ids);

    Result updateGift(Gift gift);

    Result addGift(Gift gift);
}
