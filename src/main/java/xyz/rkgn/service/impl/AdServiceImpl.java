package xyz.rkgn.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.rkgn.common.Result;
import xyz.rkgn.entity.Ad;
import xyz.rkgn.mapper.AdMapper;
import xyz.rkgn.service.AdService;

import java.util.Arrays;
import java.util.List;

/**
 * @author ljx
 * @description 针对表【tb_ad(广告)】的数据库操作Service实现
 * @createDate 2024-02-17 14:51:55
 */
@Service
public class AdServiceImpl extends ServiceImpl<AdMapper, Ad>
        implements AdService {

    @Override
    public Result queryAdById(Long id) {
        Ad ad = getById(id);
        return Result.ok(ad);
    }

    @Override
    public Result listAd(Ad ad) {
        String title = ad.getTitle();
        String content = ad.getContent();
        String[] keywords = ad.getKeywords().split("，");
        LambdaQueryChainWrapper<Ad> lqcw = lambdaQuery().like(StringUtils.isNotEmpty(title), Ad::getTitle, title)
                .like(StringUtils.isNotEmpty(content), Ad::getContent, content);
        Arrays.stream(keywords).forEach(keyword -> lqcw.like(StringUtils.isNotEmpty(keyword), Ad::getKeywords, keyword));
        List<Ad> adList = lqcw.list();
        return Result.ok(adList);
    }

    @Override
    @Transactional
    public Result deleteAdById(List<Long> ids) {
        removeByIds(ids);
        return Result.ok();
    }

    @Override
    public Result updateAd(Ad ad) {
        boolean updated = updateById(ad);
        if (!updated) {
            return Result.fail("广告id不存在");
        }
        return Result.ok();
    }

    @Override
    public Result addAd(Ad ad) {
        save(ad);
        return Result.ok();
    }
}




