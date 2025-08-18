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
package vip.xiaonuo.urp.modular.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bstek.ureport.provider.report.ReportFile;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageRequest;
import vip.xiaonuo.urp.modular.entity.UrpStorage;
import vip.xiaonuo.urp.modular.enums.UrpCategoryEnum;
import vip.xiaonuo.urp.modular.mapper.UrpMapper;
import vip.xiaonuo.urp.modular.param.UrpStorageAddParam;
import vip.xiaonuo.urp.modular.param.UrpStorageEditParam;
import vip.xiaonuo.urp.modular.param.UrpStorageIdParam;
import vip.xiaonuo.urp.modular.param.UrpStoragePageParam;
import vip.xiaonuo.urp.modular.service.UrpService;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 报表Service接口实现类
 *
 * @author xuyuxiang
 * @date 2022/7/6 14:13
 **/
@Service
public class UrpServiceImpl extends ServiceImpl<UrpMapper, UrpStorage> implements UrpService {

    private static final String REPORT_PREFIX = "URP_";

    @Override
    public Page<UrpStorage> page(UrpStoragePageParam urpStoragePageParam) {
        QueryWrapper<UrpStorage> queryWrapper = new QueryWrapper<>();
        // 查询部分字段
        queryWrapper.lambda().select(UrpStorage::getId, UrpStorage::getName, UrpStorage::getReportXml,
                UrpStorage::getCategory, UrpStorage::getSortCode);
        if(ObjectUtil.isNotEmpty(urpStoragePageParam.getCategory())) {
            queryWrapper.lambda().eq(UrpStorage::getCategory, urpStoragePageParam.getCategory());
        }
        if(ObjectUtil.isNotEmpty(urpStoragePageParam.getSearchKey())) {
            queryWrapper.lambda().like(UrpStorage::getName, urpStoragePageParam.getSearchKey());
        }
        if(ObjectUtil.isAllNotEmpty(urpStoragePageParam.getSortField(), urpStoragePageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(urpStoragePageParam.getSortOrder());
            queryWrapper.orderBy(true, urpStoragePageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(urpStoragePageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(UrpStorage::getSortCode);
        }
        Page<UrpStorage> page = this.page(CommonPageRequest.defaultPage(), queryWrapper);
        page.getRecords().forEach(urpStorage -> urpStorage.setName(StrUtil.removePrefix(urpStorage.getName(), REPORT_PREFIX)));
        return page;
    }

    @Override
    public void add(UrpStorageAddParam urpStorageAddParam) {
        UrpStorage urpStorage = BeanUtil.toBean(urpStorageAddParam, UrpStorage.class);
        urpStorage.setCode(RandomUtil.randomString(10));
        urpStorage.setName(REPORT_PREFIX + urpStorageAddParam.getName());

        // 读取xml并且set进字段内
        String pathStr = ResourceUtil.readUtf8Str("template" + File.separator + "template.ureport.xml");
        Document docResult = XmlUtil.readXML(pathStr);
        urpStorage.setReportXml(XmlUtil.toStr(docResult));
        this.save(urpStorage);
    }

    @Override
    public void edit(UrpStorageEditParam urpStorageEditParam) {
        UrpStorage urpStorage = this.queryEntity(urpStorageEditParam.getId());
        BeanUtil.copyProperties(urpStorageEditParam, urpStorage);
        urpStorage.setName(REPORT_PREFIX + urpStorageEditParam.getName());
        this.updateById(urpStorage);
    }

    @Override
    public void delete(List<UrpStorageIdParam> urpStorageIdParamList) {
        // 删除
        this.removeBatchByIds(CollStreamUtil.toList(urpStorageIdParamList, UrpStorageIdParam::getId));
        // TODO
    }

    @Override
    public UrpStorage detail(UrpStorageIdParam urpStorageIdParam) {
        return this.queryEntity(urpStorageIdParam.getId());
    }

    @Override
    public UrpStorage queryEntity(String id) {
        UrpStorage urpStorage = this.getById(id);
        if(ObjectUtil.isEmpty(urpStorage)) {
            throw new CommonException("报表不存在，id值为：{}", id);
        }
        return urpStorage;
    }

    /* ====报表存储器所需要的方法==== */

    @Override
    public InputStream loadReport(String file) {
        UrpStorage urpStorage = this.getOne(new LambdaQueryWrapper<UrpStorage>().eq(UrpStorage::getName, file));
        if(ObjectUtil.isEmpty(urpStorage)) {
            throw new CommonException("报表不存在，名称为：{}", file);
        }
        return IoUtil.toStream(urpStorage.getReportXml(), Charset.defaultCharset());
    }

    @Override
    public void deleteReport(String file) {
        this.remove(new LambdaQueryWrapper<UrpStorage>().eq(UrpStorage::getName, file));
    }

    @Override
    public List<ReportFile> getReportFiles() {
        return this.list().stream().map(urpStorage -> new ReportFile(StrUtil.removePrefix(urpStorage.getName(), REPORT_PREFIX),
                urpStorage.getUpdateTime())).collect(Collectors.toList());
    }

    @Override
    public void saveReport(String file, String content) {
        List<UrpStorage> urpStorageList = this.list(new LambdaQueryWrapper<UrpStorage>().eq(UrpStorage::getName, file));
        if(urpStorageList.size() > 1) {
            throw new CommonException("存在重复的报表，名称为：{}", file);
        }
        if(ObjectUtil.isNotEmpty(urpStorageList)) {
            UrpStorage urpStorage = urpStorageList.get(0);
            urpStorage.setName(file);
            urpStorage.setReportXml(content);
            this.updateById(urpStorage);
        } else {
            UrpStorage urpStorage = new UrpStorage();
            urpStorage.setName(file);
            urpStorage.setCode(RandomUtil.randomString(10));
            urpStorage.setSortCode(10);
            urpStorage.setCategory(UrpCategoryEnum.OTHER.getValue());
            urpStorage.setReportXml(content);
            urpStorage.setUpdateTime(DateTime.now());
            this.save(urpStorage);
        }
    }
}
