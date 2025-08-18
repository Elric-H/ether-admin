/*
 * Copyright [2022] [https://www.xiaonuo.vip]
 *
 * Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：
 *
 * 1.请不要删除和修改根目录下的LICENSE文件。
 * 2.请不要删除和修改Snowy源码头部的版权声明。
 * 3.本项目代码可免费商业使用，商业使用请保留源码和相关描述文件的项目出处，作者声明等。
 * 4.分发源码时候，请注明软件出处 https://www.xiaonuo.vip
 * 5.不可二次分发开源参与同类竞品，如有想法可联系团队xiaonuobase@qq.com商议合作。
 * 6.若您的项目无法满足以上几点，需要更多功能代码，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package vip.xiaonuo.ten.modular.provider;

import org.springframework.stereotype.Service;
import vip.xiaonuo.ten.api.TenApi;
import vip.xiaonuo.ten.core.context.TenContextHolder;
import vip.xiaonuo.ten.core.prop.TenProperties;
import vip.xiaonuo.ten.modular.service.TenService;

import javax.annotation.Resource;

/**
 * 多租户API接口提供者
 *
 * @author xuyuxiang
 * @date 2022/3/10 18:57
 **/
@Service
public class TenApiProvider implements TenApi {

    @Resource
    private TenProperties tenProperties;

    @Resource
    private TenService tenService;

    @Override
    public boolean getTenEnabled() {
        return tenProperties.getEnabled();
    }

    @Override
    public String getDefaultTenColumnName() {
        return tenProperties.getTenIdColumnName();
    }

    @Override
    public String getDefaultTenId() {
        return tenProperties.getDefaultTenId();
    }

    @Override
    public String getCurrentTenId() {
        return TenContextHolder.get();
    }

    @Override
    public String getCurrentTenDomain() {
        return tenService.getCurrentTenDomain();
    }
}
