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
package vip.xiaonuo.flw.modular.model.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.DeploymentBuilder;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageRequest;
import vip.xiaonuo.flw.core.listener.FlwGlobalCustomEventListener;
import vip.xiaonuo.flw.core.node.FlwNode;
import vip.xiaonuo.flw.core.parser.ProcessParser;
import vip.xiaonuo.flw.core.util.NodeRuntimeUtil;
import vip.xiaonuo.flw.modular.model.entity.FlwModel;
import vip.xiaonuo.flw.modular.model.enums.FlwModelStatusEnum;
import vip.xiaonuo.flw.modular.model.mapper.FlwModelMapper;
import vip.xiaonuo.flw.modular.model.param.*;
import vip.xiaonuo.flw.modular.model.result.FlwModelOrgResult;
import vip.xiaonuo.flw.modular.model.result.FlwModelPositionResult;
import vip.xiaonuo.flw.modular.model.result.FlwModelRoleResult;
import vip.xiaonuo.flw.modular.model.result.FlwModelUserResult;
import vip.xiaonuo.flw.modular.model.service.FlwModelService;
import vip.xiaonuo.sys.api.SysOrgApi;
import vip.xiaonuo.sys.api.SysPositionApi;
import vip.xiaonuo.sys.api.SysRoleApi;
import vip.xiaonuo.sys.api.SysUserApi;
import vip.xiaonuo.ten.api.TenApi;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 模型Service接口实现类
 *
 * @author xuyuxiang
 * @date 2022/5/11 15:33
 **/
@Service
public class FlwModelServiceImpl extends ServiceImpl<FlwModelMapper, FlwModel> implements FlwModelService {

    @Resource
    private TenApi tenApi;

    @Resource
    private SysOrgApi sysOrgApi;

    @Resource
    private SysPositionApi sysPositionApi;

    @Resource
    private SysRoleApi sysRoleApi;

    @Resource
    private SysUserApi sysUserApi;

    @Resource
    private RepositoryService repositoryService;

    @Override
    public Page<FlwModel> page(FlwModelPageParam flwModelPageParam) {
        QueryWrapper<FlwModel> queryWrapper = new QueryWrapper<>();
        if(ObjectUtil.isNotEmpty(flwModelPageParam.getCategory())) {
            queryWrapper.lambda().eq(FlwModel::getCategory, flwModelPageParam.getCategory());
        }
        if(ObjectUtil.isNotEmpty(flwModelPageParam.getSearchKey())) {
            queryWrapper.lambda().like(FlwModel::getName, flwModelPageParam.getSearchKey());
        }
        if(ObjectUtil.isNotEmpty(flwModelPageParam.getModelStatus())) {
            queryWrapper.lambda().eq(FlwModel::getModelStatus, flwModelPageParam.getModelStatus());
        }
        if(ObjectUtil.isAllNotEmpty(flwModelPageParam.getSortField(), flwModelPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(flwModelPageParam.getSortOrder());
            queryWrapper.orderBy(true, flwModelPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(flwModelPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(FlwModel::getSortCode);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Override
    public List<FlwModel> allList(FlwModelListParam flwModelListParam) {
        LambdaQueryWrapper<FlwModel> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(ObjectUtil.isNotEmpty(flwModelListParam.getCategory())) {
            lambdaQueryWrapper.eq(FlwModel::getCategory, flwModelListParam.getCategory());
        }
        if(ObjectUtil.isNotEmpty(flwModelListParam.getSearchKey())) {
            lambdaQueryWrapper.like(FlwModel::getName, flwModelListParam.getSearchKey());
        }
        if(ObjectUtil.isNotEmpty(flwModelListParam.getModelStatus())) {
            lambdaQueryWrapper.eq(FlwModel::getModelStatus, flwModelListParam.getModelStatus());
        }
        return this.list(lambdaQueryWrapper);
    }

    @Override
    public List<FlwModel> myList(FlwModelMyParam flwModelMyParam) {
        LambdaQueryWrapper<FlwModel> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(ObjectUtil.isNotEmpty(flwModelMyParam.getCategory())) {
            lambdaQueryWrapper.eq(FlwModel::getCategory, flwModelMyParam.getCategory());
        }
        if(ObjectUtil.isNotEmpty(flwModelMyParam.getSearchKey())) {
            lambdaQueryWrapper.like(FlwModel::getName, flwModelMyParam.getSearchKey());
        }
        // 只查询已启用的
        lambdaQueryWrapper.eq(FlwModel::getModelStatus, FlwModelStatusEnum.ENABLE.getValue());
        List<FlwModel> list = this.list(lambdaQueryWrapper);
        // 过滤已经设计了的
        return CollectionUtil.removeNull(list.stream().map(flwModel -> {
            String processJson = flwModel.getProcessJson();
            if(ObjectUtil.isNotEmpty(processJson)) {
                FlwNode flwNode = JSONUtil.toBean(processJson, FlwNode.class);
                List<FlwNode.FlwNodeParticipateProp> participateInfo = flwNode.getProperties().getParticipateInfo();
                if(ObjectUtil.isEmpty(participateInfo)) {
                    return flwModel;
                } else {
                    String participateInfoJson = JSONUtil.toJsonStr(participateInfo);
                    List<String> processParticipateInfo = NodeRuntimeUtil.handleProcessParticipateInfo(participateInfoJson);
                    if(ObjectUtil.isEmpty(processParticipateInfo)) {
                        return flwModel;
                    } else {
                        if(processParticipateInfo.contains(StpUtil.getLoginIdAsString())) {
                            return flwModel;
                        } else {
                            return null;
                        }
                    }
                }
            } else {
                return null;
            }
        }).collect(Collectors.toList()));
    }

    @Override
    public void add(FlwModelAddParam flwModelAddParam) {
        FlwModel flwModel = BeanUtil.toBean(flwModelAddParam, FlwModel.class);
        flwModel.setCode(RandomUtil.randomString(10));
        flwModel.setModelStatus(FlwModelStatusEnum.DISABLED.getValue());
        this.save(flwModel);
    }

    @Override
    public void edit(FlwModelEditParam flwModelEditParam) {
        FlwModel flwModel = this.queryEntity(flwModelEditParam.getId());
        BeanUtil.copyProperties(flwModelEditParam, flwModel);
        this.updateById(flwModel);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<FlwModelIdParam> flwModelIdParamList) {
        List<String> modelIdList = flwModelIdParamList.stream().map(FlwModelIdParam::getId).collect(Collectors.toList());
        if(ObjectUtil.isNotEmpty(modelIdList)) {
            List<FlwModel> flwModelList = this.listByIds(modelIdList);
            flwModelList.forEach(flwModel -> {
                String processJson = flwModel.getProcessJson();
                if(ObjectUtil.isNotEmpty(processJson)) {
                    repositoryService.createProcessDefinitionQuery().processDefinitionKey(JSONUtil.parseObj(processJson).getStr("id"))
                            .list().forEach(processDefinition -> repositoryService.deleteDeployment(processDefinition.getDeploymentId(), true));
                }
            });
            this.removeBatchByIds(CollStreamUtil.toList(flwModelIdParamList, FlwModelIdParam::getId));
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deploy(FlwModelIdParam flwModelIdParam) {
        try {
            FlwModel flwModel = this.queryEntity(flwModelIdParam.getId());
            String processJson = flwModel.getProcessJson();
            if(ObjectUtil.isEmpty(processJson)) {
                throw new CommonException("请先完成模型设计，名称为：{}", flwModel.getName());
            }
            FlwNode process = JSONUtil.toBean(processJson, FlwNode.class);
            process.setTitle(flwModel.getName());
            processJson = JSONUtil.toJsonStr(process);
            BpmnModelInstance bpmnModelInstance = ProcessParser.buildBpmnModelInstance(processJson);
            String flowXml = Bpmn.convertToString(bpmnModelInstance);
            DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().addString(flwModel.getName() + ".bpmn", flowXml)
                    .name(flwModel.getName());
            if(tenApi.getTenEnabled()) {
                deploymentBuilder.tenantId(tenApi.getCurrentTenId());
            }
            Deployment deploy = deploymentBuilder.deploy();
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deploy.getId()).singleResult();
            flwModel.setDefinitionId(processDefinition.getId());
            flwModel.setDeploymentId(processDefinition.getDeploymentId());
            flwModel.setVersionCode(StrUtil.format("V{}.0", processDefinition.getVersion()));
            flwModel.setProcessXml(flowXml);
            this.updateById(flwModel);
        } catch (ProcessEngineException e) {
            throw new CommonException("模型部署异常，id值为：{}，原因为：{}", flwModelIdParam.getId(), ExceptionUtil.getMessage(e));
        }
    }

    @Override
    public FlwModel detail(FlwModelIdParam flwModelIdParam) {
        return this.queryEntity(flwModelIdParam.getId());
    }

    @Override
    public FlwModel queryEntity(String id) {
        FlwModel flwModel = this.getById(id);
        if(ObjectUtil.isEmpty(flwModel)) {
            throw new CommonException("模型不存在，id值为：{}", id);
        }
        return flwModel;
    }

    @Override
    public void disableModel(FlwModelIdParam flwModelIdParam) {
        this.update(new LambdaUpdateWrapper<FlwModel>().eq(FlwModel::getId,
                flwModelIdParam.getId()).set(FlwModel::getModelStatus, FlwModelStatusEnum.DISABLED.getValue()));
    }

    @Override
    public void enableModel(FlwModelIdParam flwModelIdParam) {
        this.update(new LambdaUpdateWrapper<FlwModel>().eq(FlwModel::getId,
                flwModelIdParam.getId()).set(FlwModel::getModelStatus, FlwModelStatusEnum.ENABLE.getValue()));
    }

    @Override
    public void downVersion(FlwModelIdParam flwModelIdParam) {
        FlwModel flwModel = this.queryEntity(flwModelIdParam.getId());
        String definitionId = flwModel.getDefinitionId();
        String deploymentId = flwModel.getDeploymentId();
        if(ObjectUtil.hasEmpty(definitionId, deploymentId)) {
            throw new CommonException("请先完成模型设计，名称为：{}", flwModel.getName());
        }
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(definitionId).singleResult();
        if(ObjectUtil.isEmpty(processDefinition)) {
            throw new CommonException("模型的流程定义不存在，id值为：{}", flwModelIdParam.getId());
        }
        if(processDefinition.getVersion() == 1) {
            throw new CommonException("当前模型无历史版本，id值为：{}", flwModelIdParam.getId());
        }
        ProcessDefinition targetProcessDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(processDefinition.getKey()).processDefinitionVersion(processDefinition.getVersion() - 1).singleResult();
        if(ObjectUtil.isEmpty(targetProcessDefinition)) {
            throw new CommonException("当前模型上一版本流程定义数据不存在，id值为：{}", flwModelIdParam.getId());
        }
        Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(targetProcessDefinition.getDeploymentId()).singleResult();
        if(ObjectUtil.isEmpty(deployment)) {
            throw new CommonException("当前模型上一版本流程部署数据不存在，id值为：{}", flwModelIdParam.getId());
        }
        flwModel.setDefinitionId(targetProcessDefinition.getId());
        flwModel.setDeploymentId(targetProcessDefinition.getDeploymentId());
        flwModel.setVersionCode(StrUtil.format("V{}.0", targetProcessDefinition.getVersion()));
        flwModel.setProcessXml(Bpmn.convertToString(repositoryService.getBpmnModelInstance(targetProcessDefinition.getId())));
        this.updateById(flwModel);
    }

    @Override
    public List<Tree<String>> orgTreeSelector() {
        return sysOrgApi.orgTreeSelector();
    }

    @SuppressWarnings("ALL")
    @Override
    public Page<FlwModelOrgResult> orgListSelector(FlwModelSelectorOrgListParam flwModelSelectorOrgListParam) {
        return BeanUtil.toBean(sysOrgApi.orgListSelector(flwModelSelectorOrgListParam.getParentId()), Page.class);
    }

    @SuppressWarnings("ALL")
    @Override
    public Page<FlwModelPositionResult> positionSelector(FlwModelSelectorPositionParam flwModelSelectorPositionParam) {
        return BeanUtil.toBean(sysPositionApi.positionSelector(flwModelSelectorPositionParam.getOrgId(),
                flwModelSelectorPositionParam.getSearchKey()), Page.class);
    }

    @SuppressWarnings("ALL")
    @Override
    public Page<FlwModelRoleResult> roleSelector(FlwModelSelectorRoleParam flwModelSelectorRoleParam) {
        return BeanUtil.toBean(sysRoleApi.roleSelector(flwModelSelectorRoleParam.getOrgId(), flwModelSelectorRoleParam.getCategory(),
                flwModelSelectorRoleParam.getSearchKey(), null, false), Page.class);
    }

    @SuppressWarnings("ALL")
    @Override
    public Page<FlwModelUserResult> userSelector(FlwModelSelectorUserParam flwModelSelectorUserParam) {
        return BeanUtil.toBean(sysUserApi.userSelector(flwModelSelectorUserParam.getOrgId(), flwModelSelectorUserParam.getSearchKey()), Page.class);
    }

    @Override
    public List<String> executionListenerSelector() {
        Map<String, ExecutionListener> executionListenerMap = SpringUtil.getBeansOfType(ExecutionListener.class);
        if (ObjectUtil.isNotEmpty(executionListenerMap)) {
            Collection<ExecutionListener> values = executionListenerMap.values();
            return values.stream().map(executionListener -> executionListener.getClass().getName()).collect(Collectors.toList());
        } else {
            return CollectionUtil.newArrayList();
        }
    }

    @Override
    public List<String> executionListenerSelectorForCustomEvent() {
        Map<String, FlwGlobalCustomEventListener> executionListenerMap = SpringUtil.getBeansOfType(FlwGlobalCustomEventListener.class);
        if (ObjectUtil.isNotEmpty(executionListenerMap)) {
            Collection<FlwGlobalCustomEventListener> values = executionListenerMap.values();
            return values.stream().map(executionListener -> executionListener.getClass().getName()).collect(Collectors.toList());
        } else {
            return CollectionUtil.newArrayList();
        }
    }

    @Override
    public List<String> taskListenerSelector() {
        Map<String, TaskListener> taskListenerMap = SpringUtil.getBeansOfType(TaskListener.class);
        if (ObjectUtil.isNotEmpty(taskListenerMap)) {
            Collection<TaskListener> values = taskListenerMap.values();
            return values.stream().map(taskListener -> taskListener.getClass().getName()).collect(Collectors.toList());
        } else {
            return CollectionUtil.newArrayList();
        }
    }

    @Override
    public FlwModel validModel(String modelId) {
        FlwModel flwModel = this.queryEntity(modelId);
        if(ObjectUtil.hasEmpty(flwModel.getProcessJson(), flwModel.getProcessXml(), flwModel.getDefinitionId(), flwModel.getDeploymentId())) {
            throw new CommonException("模型数据错误，名称为：{}", flwModel.getName());
        }
        if (flwModel.getModelStatus().equals(FlwModelStatusEnum.DISABLED.getValue())) {
            throw new CommonException("模型未启用，名称为：{}", flwModel.getName());
        }
        return flwModel;
    }

}
