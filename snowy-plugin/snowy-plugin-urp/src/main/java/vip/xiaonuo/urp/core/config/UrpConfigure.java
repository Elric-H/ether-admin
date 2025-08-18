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
package vip.xiaonuo.urp.core.config;

import com.bstek.ureport.console.UReportServlet;
import com.bstek.ureport.definition.datasource.BuildinDatasource;
import com.bstek.ureport.provider.report.ReportFile;
import com.bstek.ureport.provider.report.ReportProvider;
import com.github.xiaoymin.knife4j.spring.extension.OpenApiExtensionResolver;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.dbs.api.DbsApi;
import vip.xiaonuo.urp.modular.service.UrpService;

import javax.annotation.Resource;
import javax.servlet.Servlet;
import java.io.InputStream;
import java.sql.Connection;
import java.util.List;

/**
 * 报表设计相关配置
 *
 * @author xuyuxiang
 * @date 2022/3/8 15:27
 **/
@Slf4j
@Configuration
public class UrpConfigure {

    @Resource
    private DbsApi dbsApi;

    @Resource
    private OpenApiExtensionResolver openApiExtensionResolver;

    /**
     * uReport2报表Servlet配置
     *
     * @author xuyuxiang
     * @date 2022/3/8 15:28
     **/
    @Bean
    public ServletRegistrationBean<Servlet> uReport2Servlet() {
        return new ServletRegistrationBean<>(new UReportServlet(), "/ureport/*");
    }

    /**
     * 配置内置数据源的来源
     *
     * @author xuyuxiang
     * @date 2022/3/8 16:15
     **/
    @Component
    public class UrpDataSource implements BuildinDatasource {

        /**
         * 获取数据源名称，使用dbs-api获取当前数据源名称
         */
        @Override
        public String name() {
            return dbsApi.getCurrentDataSourceName();
        }

        /**
         * 获取数据源名称，使用dbs-api获取当前数据源
         */
        @SneakyThrows
        @Override
        public Connection getConnection() {
            return dbsApi.getCurrentDataSource().getConnection();
        }
    }

    /**
     * 自定义报表存储器
     *
     * @author xuyuxiang
     * @date 2022/7/6 22:00
     */
    @Component
    public static class UrpReportProvider implements ReportProvider {

        @Resource
        private UrpService urpService;

        /**
         * 根据报表名加载报表文件
         * @param file 报表名称
         * @return 返回的InputStream
         */
        @Override
        public InputStream loadReport(String file) {
            return urpService.loadReport(file);
        }

        /**
         * 根据报表名，删除指定的报表文件
         * @param file 报表名称
         */
        @Override
        public void deleteReport(String file) {
            urpService.deleteReport(file);
        }

        /**
         * 获取所有的报表文件
         * @return 返回报表文件列表
         */
        @Override
        public List<ReportFile> getReportFiles() {
            return urpService.getReportFiles();
        }

        /**
         * 保存报表文件
         * @param file 报表名称
         * @param content 报表的XML内容
         */
        @Override
        public void saveReport(String file, String content) {
            urpService.saveReport(file, content);
        }

        /**
         * @return 返回存储器名称
         */
        @Override
        public String getName() {
            return "URP报表存储系统";
        }

        /**
         * @return 返回是否禁用
         */
        @Override
        public boolean disabled() {
            return false;
        }

        /**
         * @return 返回报表文件名前缀
         */
        @Override
        public String getPrefix() {
            return "URP_";
        }
    }

    /**
     * API文档分组配置
     *
     * @author xuyuxiang
     * @date 2022/7/7 16:18
     **/
    @Bean(value = "urpDocApi")
    public Docket urpDocApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .title("报表设计URP")
                        .description("报表设计URP")
                        .termsOfServiceUrl("https://www.xiaonuo.vip")
                        .contact(new Contact("SNOWY_TEAM","https://www.xiaonuo.vip", "xuyuxiang29@foxmail.com"))
                        .version("2.0.0")
                        .build())
                .globalResponseMessage(RequestMethod.GET, CommonResult.responseList())
                .globalResponseMessage(RequestMethod.POST, CommonResult.responseList())
                .groupName("报表设计URP")
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .apis(RequestHandlerSelectors.basePackage("vip.xiaonuo.urp"))
                .paths(PathSelectors.any())
                .build().extensions(openApiExtensionResolver.buildExtensions("报表设计URP"));
    }
}
