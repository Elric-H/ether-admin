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
package vip.xiaonuo.flw.core.config;

import com.github.xiaoymin.knife4j.spring.extension.OpenApiExtensionResolver;
import io.swagger.annotations.ApiOperation;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.camunda.bpm.engine.impl.cfg.multitenancy.TenantIdProvider;
import org.camunda.bpm.engine.impl.cfg.multitenancy.TenantIdProviderCaseInstanceContext;
import org.camunda.bpm.engine.impl.cfg.multitenancy.TenantIdProviderHistoricDecisionInstanceContext;
import org.camunda.bpm.engine.impl.cfg.multitenancy.TenantIdProviderProcessInstanceContext;
import org.camunda.bpm.spring.boot.starter.configuration.Ordering;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.ten.api.TenApi;

import javax.annotation.Resource;

/**
 * 工作流相关配置
 *
 * @author xuyuxiang
 * @date 2022/2/14 14:31
 **/
@Component
@Order(Ordering.DEFAULT_ORDER - 1)
public class FlwConfigure implements ProcessEnginePlugin {

    @Resource
    private TenApi tenApi;

    @Resource
    private OpenApiExtensionResolver openApiExtensionResolver;

    @Override
    public void preInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
        // 关闭CMMN
        processEngineConfiguration.setCmmnEnabled(false);
        // 关闭DMN
        processEngineConfiguration.setDmnEnabled(false);
        // 不使用身份数据库
        processEngineConfiguration.setDbIdentityUsed(false);
        // 获取是否开启多租户
        boolean tenEnabled = tenApi.getTenEnabled();
        if(tenEnabled) {
            // 租户ID提供者
            processEngineConfiguration.setTenantIdProvider(new TenantIdProvider() {

                @Override
                public String provideTenantIdForProcessInstance(TenantIdProviderProcessInstanceContext ctx) {
                    return tenApi.getCurrentTenId();
                }

                @Override
                public String provideTenantIdForCaseInstance(TenantIdProviderCaseInstanceContext ctx) {
                    return tenApi.getCurrentTenId();
                }

                @Override
                public String provideTenantIdForHistoricDecisionInstance(TenantIdProviderHistoricDecisionInstanceContext ctx) {
                    return tenApi.getCurrentTenId();
                }
            });
        }
    }

    @Override
    public void postInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
    }

    @Override
    public void postProcessEngineBuild(ProcessEngine processEngine) {
    }

    /**
     * API文档分组配置
     *
     * @author xuyuxiang
     * @date 2022/7/7 16:18
     **/
    @Bean(value = "flwDocApi")
    public Docket flwDocApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .title("工作流程FLW")
                        .description("工作流程FLW")
                        .termsOfServiceUrl("https://www.xiaonuo.vip")
                        .contact(new Contact("SNOWY_TEAM","https://www.xiaonuo.vip", "xuyuxiang29@foxmail.com"))
                        .version("2.0.0")
                        .build())
                .globalResponseMessage(RequestMethod.GET, CommonResult.responseList())
                .globalResponseMessage(RequestMethod.POST, CommonResult.responseList())
                .groupName("工作流程FLW")
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .apis(RequestHandlerSelectors.basePackage("vip.xiaonuo.flw"))
                .paths(PathSelectors.any())
                .build().extensions(openApiExtensionResolver.buildExtensions("工作流程FLW"));
    }
}
