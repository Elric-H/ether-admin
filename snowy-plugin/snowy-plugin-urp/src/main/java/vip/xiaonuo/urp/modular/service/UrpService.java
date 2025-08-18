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
package vip.xiaonuo.urp.modular.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bstek.ureport.provider.report.ReportFile;
import vip.xiaonuo.urp.modular.entity.UrpStorage;
import vip.xiaonuo.urp.modular.param.UrpStorageAddParam;
import vip.xiaonuo.urp.modular.param.UrpStorageEditParam;
import vip.xiaonuo.urp.modular.param.UrpStorageIdParam;
import vip.xiaonuo.urp.modular.param.UrpStoragePageParam;

import java.io.InputStream;
import java.util.List;

/**
 * 报表Service接口
 *
 * @author xuyuxiang
 * @date 2022/7/6 14:12
 **/
public interface UrpService extends IService<UrpStorage> {

    /**
     * 获取报表分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    Page<UrpStorage> page(UrpStoragePageParam urpStoragePageParam);

    /**
     * 添加报表
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:48
     */
    void add(UrpStorageAddParam urpStorageAddParam);

    /**
     * 编辑报表
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:13
     */
    void edit(UrpStorageEditParam urpStorageEditParam);

    /**
     * 删除报表
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    void delete(List<UrpStorageIdParam> urpStorageIdParamList);

    /**
     * 获取报表详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    UrpStorage detail(UrpStorageIdParam UrpStorageIdParam);

    /**
     * 获取报表详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    UrpStorage queryEntity(String id);

    /* ====报表存储器所需要的方法==== */

    /**
     * 根据报表名加载报表文件
     * @param file 报表名称
     * @return 返回的InputStream
     */
    InputStream loadReport(String file);

    /**
     * 根据报表名，删除指定的报表文件
     * @param file 报表名称
     */
    void deleteReport(String file);

    /**
     * 获取所有的报表文件
     * @return 返回报表文件列表
     */
    List<ReportFile> getReportFiles();

    /**
     * 保存报表文件
     * @param file 报表名称
     * @param content 报表的XML内容
     */
    void saveReport(String file, String content);
}
