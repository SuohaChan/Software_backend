package xyz.rkgn.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.rkgn.common.Result;
import xyz.rkgn.entity.Gift;
import xyz.rkgn.mapper.GiftMapper;
import xyz.rkgn.service.GiftService;

import java.util.List;

/**
 * @author ljx
 * @description 针对表【tb_gift(礼品)】的数据库操作Service实现
 * @createDate 2024-02-17 14:26:45
 */
@Service
public class GiftServiceImpl extends ServiceImpl<GiftMapper, Gift>
        implements GiftService {

    @Override
    public Result queryGiftById(Long id) {
        Gift gift = getById(id);
        return Result.ok(gift);
    }

    @Override
    public Result listGift(Gift gift) {
        String name = gift.getName();
        String description = gift.getDescription();
        List<Gift> giftList = lambdaQuery().like(StringUtils.isNotEmpty(name), Gift::getName, name)
                .like(StringUtils.isNotEmpty(description), Gift::getDescription, description).list();
        return Result.ok(giftList);
    }

    @Override
    @Transactional
    public Result deleteGiftById(List<Long> ids) {
        removeByIds(ids);
        return Result.ok();
    }

    @Override
    public Result updateGift(Gift gift) {
        boolean updated = updateById(gift);
        if (!updated) {
            return Result.fail("礼品id不存在");
        }
        return Result.ok();
    }

    @Override
    public Result addGift(Gift gift) {
        save(gift);
        return Result.ok();
    }
}




