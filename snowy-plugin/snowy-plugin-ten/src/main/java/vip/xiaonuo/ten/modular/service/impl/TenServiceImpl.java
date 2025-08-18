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
package vip.xiaonuo.ten.modular.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.common.cache.CommonCacheOperator;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageRequest;
import vip.xiaonuo.common.prop.CommonProperties;
import vip.xiaonuo.common.util.CommonServletUtil;
import vip.xiaonuo.dbs.api.DbsApi;
import vip.xiaonuo.dev.api.DevApi;
import vip.xiaonuo.sys.api.SysApi;
import vip.xiaonuo.ten.core.prop.TenProperties;
import vip.xiaonuo.ten.modular.entity.TenStorage;
import vip.xiaonuo.ten.modular.enums.TenCategoryEnum;
import vip.xiaonuo.ten.modular.mapper.TenMapper;
import vip.xiaonuo.ten.modular.param.*;
import vip.xiaonuo.ten.modular.result.TenDbsSelectorResult;
import vip.xiaonuo.ten.modular.service.TenService;

import javax.annotation.Resource;
import java.sql.Connection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 多租户Service接口实现类
 *
 * @author xuyuxiang
 * @date 2022/3/11 10:25
 **/
@Service
public class TenServiceImpl extends ServiceImpl<TenMapper, TenStorage> implements TenService {

    private static final String TEN_CACHE_KEY = "ext-tenant:";

    @Resource
    private MybatisPlusProperties mybatisPlusProperties;

    @Resource
    private CommonProperties commonProperties;

    @Resource
    private TenProperties tenProperties;

    @Resource
    private SysApi sysApi;

    @Resource
    private DevApi devApi;

    @Resource
    private DbsApi dbsApi;

    @Resource
    private CommonCacheOperator commonCacheOperator;

    @Override
    public String getCurrentTenDomain() {

        // 先获取主租户域名
        String mainDomain = commonProperties.getFrontUrl();
        if(ObjectUtil.isEmpty(mainDomain)) {
            throw new CommonException("前端地址未正确配置：snowy.config.common.front-url为空");
        }

        // 获取后端域名
        String apiUrl = commonProperties.getBackendUrl();
        if(ObjectUtil.isEmpty(apiUrl)) {
            throw new CommonException("后端域名地址未正确配置：snowy.config.common.backend-url为空");
        }

        // 获取当前租户域名
        String domain = CommonServletUtil.getRequest().getHeader("Domain");
        if (ObjectUtil.isEmpty(domain)) {
            domain = CommonServletUtil.getRequest().getParameter("Domain");
            if (ObjectUtil.isEmpty(domain)) {
                domain = CommonServletUtil.getRequest().getHeader("Origin");
                if (ObjectUtil.isEmpty(domain)) {
                    domain = CommonServletUtil.getRequest().getParameter("Origin");
                }
            }
        }

        // 如果获取不到当前租户域名，则返回主租户域名
        if(ObjectUtil.isEmpty(domain)) {
            return mainDomain;
        } else {
            // 如果是后端域名则返回主租户域名
            if(domain.equals(apiUrl)) {
                return mainDomain;
            } else {
                // 否则返回当前域名
                return domain;
            }
        }
    }

    @Override
    public TenStorage getCurrentTen() {

        // 获取当前租户域名
        String domain = this.getCurrentTenDomain();

        // 从缓存中取
        Object cacheValue = commonCacheOperator.get(TEN_CACHE_KEY + domain);
        if(ObjectUtil.isNotEmpty(cacheValue)) {
            return BeanUtil.toBean(cacheValue, TenStorage.class);
        }

        TenStorage tenStorage;

        // 如果当前域名是主租户域名，则使用主租户
        if(domain.equals(commonProperties.getFrontUrl())) {
            TenStorage mainTen = new TenStorage();
            mainTen.setId(tenProperties.getDefaultTenId());
            tenStorage = mainTen;
        } else {
            // 否则根据域名获取租户, 先切换主数据源为master，因为租户数据源无租户表
            dbsApi.changeDataSource(dbsApi.getDefaultDataSourceName());
            if(isDmDb()) {
                // 使用此方式来兼容DOMAIN与达梦数据库的关键字冲突的问题，DOMAIN查询条件使用单引号，查询的字段不查询DOMAIN
                tenStorage = this.getOne(new QueryWrapper<TenStorage>().eq("'DOMAIN'", domain).lambda()
                        .select(TenStorage::getId, TenStorage::getTenantId, TenStorage::getDbsId, TenStorage::getDbsName,
                                TenStorage::getName, TenStorage::getCode, TenStorage::getCategory, TenStorage::getSortCode,
                                TenStorage:: getExtJson));
            } else {
                tenStorage = this.getOne(new LambdaQueryWrapper<TenStorage>().eq(TenStorage::getDomain, domain));
            }
            if(ObjectUtil.isEmpty(tenStorage)) {
                throw new CommonException("租户不存在，域名为：{}", domain);
            } else {
                // 设置DOMAIN字段
                tenStorage.setDomain(domain);
            }
        }
        // 更新到缓存
        commonCacheOperator.put(TEN_CACHE_KEY + domain, tenStorage);
        return tenStorage;
    }

    @Override
    public Page<TenStorage> page(TenStoragePageParam tenStoragePageParam) {
        QueryWrapper<TenStorage> queryWrapper = new QueryWrapper<>();
        if(isDmDb()) {
            // 查询部分字段，使用此方式来兼容DOMAIN与达梦数据库的关键字冲突的问题，DOMAIN查询字段使用双引号
            queryWrapper.select("ID", "DBS_ID", "DBS_NAME", "NAME", "\"DOMAIN\"", "CATEGORY", "SORT_CODE");
        } else {
            // 查询部分字段
            queryWrapper.lambda().select(TenStorage::getId, TenStorage::getDbsId, TenStorage::getDbsName,
                    TenStorage::getName, TenStorage::getDomain, TenStorage::getCategory, TenStorage::getSortCode);
        }
        if(ObjectUtil.isNotEmpty(tenStoragePageParam.getCategory())) {
            queryWrapper.lambda().eq(TenStorage::getCategory, tenStoragePageParam.getCategory());
        }
        if(ObjectUtil.isNotEmpty(tenStoragePageParam.getSearchKey())) {
            queryWrapper.lambda().like(TenStorage::getName, tenStoragePageParam.getSearchKey());
        }
        if(ObjectUtil.isAllNotEmpty(tenStoragePageParam.getSortField(), tenStoragePageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(tenStoragePageParam.getSortOrder());
            queryWrapper.orderBy(true, tenStoragePageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(tenStoragePageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(TenStorage::getSortCode);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Override
    public List<TenStorage> tenSelector(TenStorageSelectorParam tenStorageSelectorParam) {
        QueryWrapper<TenStorage> queryWrapper = new QueryWrapper<>();
        if(isDmDb()) {
            // 查询部分字段，使用此方式来兼容DOMAIN与达梦数据库的关键字冲突的问题，DOMAIN查询字段使用双引号
            queryWrapper.select("ID", "DBS_ID", "DBS_NAME", "NAME", "\"DOMAIN\"", "CATEGORY", "SORT_CODE");
        } else {
            // 查询部分字段
            queryWrapper.lambda().select(TenStorage::getId, TenStorage::getDbsId, TenStorage::getDbsName,
                    TenStorage::getName, TenStorage::getDomain, TenStorage::getCategory, TenStorage::getSortCode);
        }
        if(ObjectUtil.isNotEmpty(tenStorageSelectorParam.getCategory())) {
            queryWrapper.lambda().eq(TenStorage::getCategory, tenStorageSelectorParam.getCategory());
        }
        if(ObjectUtil.isNotEmpty(tenStorageSelectorParam.getSearchKey())) {
            queryWrapper.lambda().like(TenStorage::getName, tenStorageSelectorParam.getSearchKey());
        }
        if(ObjectUtil.isAllNotEmpty(tenStorageSelectorParam.getSortField(), tenStorageSelectorParam.getSortOrder())) {
            CommonSortOrderEnum.validate(tenStorageSelectorParam.getSortOrder());
            queryWrapper.orderBy(true, tenStorageSelectorParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(tenStorageSelectorParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(TenStorage::getSortCode);
        }
        return this.list(queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(TenStorageAddParam tenStorageAddParam) {
        checkParam(tenStorageAddParam);
        TenStorage tenStorage = BeanUtil.toBean(tenStorageAddParam, TenStorage.class);
        tenStorage.setCode(RandomUtil.randomString(10));
        if(isDmDb()) {
            String sql = new SQL().INSERT_INTO("EXT_TENANT")
                    .INTO_COLUMNS("ID", "DBS_ID", "DBS_NAME", "NAME", "CODE", "\"DOMAIN\"",
                            "CATEGORY", "SORT_CODE", "EXT_JSON", "DELETE_FLAG", "CREATE_TIME", "CREATE_USER")
                    .INTO_VALUES("{0}, {1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}, {9}, {10}, {11}").toString();
            tenStorage.setId(IdWorker.getIdStr());
            SqlRunner.db().insert(sql, tenStorage.getId(), tenStorage.getDbsId(),
                    tenStorage.getDbsName(), tenStorage.getName(), tenStorage.getCode(), tenStorage.getDomain(),
                    tenStorage.getCategory(), tenStorage.getSortCode(), tenStorageAddParam.getExtJson(),
                    mybatisPlusProperties.getGlobalConfig().getDbConfig().getLogicNotDeleteValue(),DateUtil.now(), getUserId());
        } else {
            this.save(tenStorage);
        }
        // ID隔离的租户，需要在初始化模块
        if(tenStorage.getCategory().equals(TenCategoryEnum.ID.getValue())) {
            // 初始化ID类型的租户系统模块数据
            sysApi.initTenDataForCategoryId(tenStorage.getId(), tenStorage.getName());

            // 初始化ID类型的租户开发工具模块数据
            devApi.initTenDataForCategoryId(tenStorage.getId(), tenStorage.getName());
        } else {
            // DB隔离的租户，给其库中插入一条数据源（即其自己的数据源），以便使用动态字段
            dbsApi.changeDataSourceAndInsertDbs(tenStorage.getDbsId());
        }
    }

    private void checkParam(TenStorageAddParam tenStorageAddParam) {
        if(!tenProperties.getEnabled()) {
            throw new CommonException("请先开启租户功能");
        }
        String tenCategory = tenStorageAddParam.getCategory();
        TenCategoryEnum.validate(tenCategory);
        if(TenCategoryEnum.DB.getValue().equals(tenCategory)) {
            String dbsId = tenStorageAddParam.getDbsId();
            String dbsName = tenStorageAddParam.getDbsName();
            if(ObjectUtil.isEmpty(dbsId)) {
                throw new CommonException("dbsId不能为空");
            }
            if(ObjectUtil.isEmpty(dbsName)) {
                throw new CommonException("dbsName不能为空");
            }
        } else {
            tenStorageAddParam.setDbsId(null);
            tenStorageAddParam.setDbsName(null);
        }
        boolean hasSameTen = this.count(new LambdaQueryWrapper<TenStorage>()
                .eq(TenStorage::getName, tenStorageAddParam.getName())) > 0;
        if(hasSameTen) {
            throw new CommonException("存在重复的租户，名称为：{}", tenStorageAddParam.getName());
        }
        boolean hasSameDomain;
        if(isDmDb()) {
            // 使用此方式来兼容DOMAIN与达梦数据库的关键字冲突的问题，DOMAIN查询条件使用单引号
            hasSameDomain = this.count(new QueryWrapper<TenStorage>().eq("'DOMAIN'",
                    tenStorageAddParam.getDomain())) > 0;
        } else {
            hasSameDomain = this.count(new LambdaQueryWrapper<TenStorage>().eq(TenStorage::getDomain,
                    tenStorageAddParam.getDomain())) > 0;
        }
        if(hasSameDomain) {
            throw new CommonException("存在重复的绑定域名，域名为：{}", tenStorageAddParam.getDomain());
        }
    }

    @Override
    public void edit(TenStorageEditParam tenStorageEditParam) {
        if(!tenProperties.getEnabled()) {
            throw new CommonException("请先开启租户功能");
        }
        TenStorage tenStorage = this.queryEntity(tenStorageEditParam.getId());
        // 移除对应的缓存
        commonCacheOperator.remove(TEN_CACHE_KEY + tenStorage.getDomain());
        checkParam(tenStorageEditParam);
        BeanUtil.copyProperties(tenStorageEditParam, tenStorage);
        if(isDmDb()) {
            // 使用此方式来兼容DOMAIN与达梦数据库的关键字冲突的问题，DOMAIN查询条件使用单引号
            UpdateWrapper<TenStorage> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().eq(TenStorage::getId, tenStorage.getId());
            updateWrapper.set("\"DOMAIN\"", tenStorage.getDomain())
                    .lambda()
                    .set(TenStorage::getName, tenStorage.getName())
                    .set(TenStorage::getSortCode, tenStorage.getSortCode())
                    .set(TenStorage::getExtJson, tenStorage.getExtJson())
                    .set(TenStorage::getUpdateTime, DateTime.now())
                    .set(TenStorage::getUpdateUser, getUserId());
            this.update(updateWrapper);
        } else {
            this.updateById(tenStorage);
        }
    }

    private void checkParam(TenStorageEditParam tenStorageEditParam) {
        boolean hasSameTen = this.count(new LambdaQueryWrapper<TenStorage>()
                .eq(TenStorage::getName, tenStorageEditParam.getName())
                .ne(TenStorage::getId, tenStorageEditParam.getId())) > 0;
        if(hasSameTen) {
            throw new CommonException("存在重复的租户，名称为：{}", tenStorageEditParam.getName());
        }
        boolean hasSameDomain;
        if(isDmDb()) {
            // 使用此方式来兼容DOMAIN与达梦数据库的关键字冲突的问题，DOMAIN查询条件使用单引号
            hasSameDomain = this.count(new QueryWrapper<TenStorage>()
                    .eq("'DOMAIN'", tenStorageEditParam.getDomain()).lambda()
                    .ne(TenStorage::getId, tenStorageEditParam.getId())) > 0;
        } else {
            hasSameDomain = this.count(new LambdaQueryWrapper<TenStorage>().eq(TenStorage::getDomain,
                    tenStorageEditParam.getDomain()).ne(TenStorage::getId, tenStorageEditParam.getId())) > 0;
        }
        if(hasSameDomain) {
            throw new CommonException("存在重复的绑定域名，域名为：{}", tenStorageEditParam.getDomain());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<TenStorageIdParam> tenStorageIdParamList) {
        if(!tenProperties.getEnabled()) {
            throw new CommonException("请先开启租户功能");
        }
        List<String> tenStorageIdList = CollStreamUtil.toList(tenStorageIdParamList, TenStorageIdParam::getId);
        QueryWrapper<TenStorage> queryWrapper = new QueryWrapper<>();
        if(isDmDb()) {
            // 查询部分字段，使用此方式来兼容DOMAIN与达梦数据库的关键字冲突的问题，DOMAIN查询字段使用双引号
            queryWrapper.select("ID", "DBS_ID", "DBS_NAME", "NAME", "\"DOMAIN\"", "CATEGORY", "SORT_CODE");
        } else {
            // 查询部分字段
            queryWrapper.lambda().select(TenStorage::getId, TenStorage::getDbsId, TenStorage::getDbsName,
                    TenStorage::getName, TenStorage::getDomain, TenStorage::getCategory, TenStorage::getSortCode);
        }
        if(ObjectUtil.isNotEmpty(tenStorageIdList)) {
            this.list(queryWrapper.lambda().in(TenStorage::getId, tenStorageIdList)).forEach(tenStorage -> {
                // 移除对应的缓存
                commonCacheOperator.remove(TEN_CACHE_KEY + tenStorage.getDomain());

                // 删除ID类型的租户系统模块数据（如果不是ID的类型，移除无妨）
                sysApi.removeTenDataForCategoryId(tenStorage.getId());

                // 删除ID类型的租户开发工具模块数据（如果不是ID的类型，移除无妨）
                devApi.removeTenDataForCategoryId(tenStorage.getId(), tenStorage.getDomain());
            });
            // 执行删除并忽略插件（逻辑删除、租户拼接），即真删除
            this.baseMapper.deleteIgnoreInterceptor(new LambdaQueryWrapper<TenStorage>().in(TenStorage::getId, tenStorageIdList));
        }
    }

    @Override
    public TenStorage detail(TenStorageIdParam tenStorageIdParam) {
        return this.queryEntity(tenStorageIdParam.getId());
    }

    @Override
    public TenStorage queryEntity(String id) {
        QueryWrapper<TenStorage> queryWrapper = new QueryWrapper<>();
        if(isDmDb()) {
            // 查询部分字段，使用此方式来兼容DOMAIN与达梦数据库的关键字冲突的问题，DOMAIN查询字段使用双引号
            queryWrapper.select("ID", "DBS_ID", "DBS_NAME", "NAME", "\"DOMAIN\"", "CATEGORY", "SORT_CODE");
        } else {
            // 查询部分字段
            queryWrapper.lambda().select(TenStorage::getId, TenStorage::getDbsId, TenStorage::getDbsName,
                    TenStorage::getName, TenStorage::getDomain, TenStorage::getCategory, TenStorage::getSortCode);
        }
        TenStorage tenStorage = this.getOne(queryWrapper.lambda().eq(TenStorage::getId, id));
        if(ObjectUtil.isEmpty(tenStorage)) {
            throw new CommonException("租户不存在，id值为：{}", id);
        }
        return tenStorage;
    }

    @Override
    public List<TenDbsSelectorResult> dbsList() {
        // 获取已被使用的数据源id集合
        List<String> usedDbsIdList = this.list(new LambdaQueryWrapper<TenStorage>().select(TenStorage::getDbsId).isNotNull(TenStorage::getDbsId))
                .stream().map(TenStorage::getDbsId).collect(Collectors.toList());
        // 获取所有租户数据源集合
        List<TenDbsSelectorResult> selectorResultList = dbsApi.tenDbsSelector().stream()
                .map(jsonObject -> JSONUtil.toBean(jsonObject, TenDbsSelectorResult.class)).collect(Collectors.toList());
        if(ObjectUtil.isNotEmpty(usedDbsIdList)) {
            // 过滤掉已被使用的数据源id集合
            return selectorResultList.stream().filter(tenDbsSelectorResult -> !usedDbsIdList
                    .contains(tenDbsSelectorResult.getId())).collect(Collectors.toList());
        } else {
            // 返回全部的
            return selectorResultList;
        }
    }

    /**
     * 获取用户id
     */
    private String getUserId() {
        try {
            String loginId = StpUtil.getLoginIdAsString();
            if (ObjectUtil.isNotEmpty(loginId)) {
                return loginId;
            } else {
                return "-1";
            }
        } catch (Exception e) {
            return "-1";
        }
    }

    /**
     * 是否达梦数据库
     */
    private boolean isDmDb() {
        Connection conn = null;
        try {
            conn = dbsApi.getCurrentDataSource().getConnection();
            return conn.getMetaData().getURL().toLowerCase().contains("jdbc:dm");
        } catch (Exception e) {
            throw new CommonException("判断数据库类型异常");
        } finally {
            JdbcUtils.closeConnection(conn);
        }
    }
}
