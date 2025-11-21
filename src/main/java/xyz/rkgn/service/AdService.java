package xyz.rkgn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.rkgn.common.Result;
import xyz.rkgn.entity.Ad;

import java.util.List;

/**
 * @author ljx
 * @description 针对表【tb_ad(广告)】的数据库操作Service
 * @createDate 2024-02-17 14:51:55
 */
public interface AdService extends IService<Ad> {

    Result queryAdById(Long id);

    Result listAd(Ad ad);

    Result deleteAdById(List<Long> ids);

    Result updateAd(Ad ad);

    Result addAd(Ad ad);
}
