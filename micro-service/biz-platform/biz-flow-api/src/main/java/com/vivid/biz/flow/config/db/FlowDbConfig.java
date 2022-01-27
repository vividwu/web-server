package com.vivid.biz.flow.config.db;


import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
//@EnableTransactionManagement 全局application注解了
@MapperScan(basePackages = {"com.vivid.biz.flow.repository"}, sqlSessionTemplateRef = "flowSqlSessionTemplate")
public class FlowDbConfig {
    private static final Logger logger = LoggerFactory.getLogger(FlowDbConfig.class);

    @Value("${db.datasource.driver-class-name}")
    private String driverClassName;
    @Value("${db.datasource.url}")
    private String url;
    @Value("${db.datasource.username}")
    private String username;
    @Value("${db.datasource.password}")
    private String password;


    @Bean("flowDataSource")
    //@ConfigurationProperties(prefix = "atsync.datasource")
    public DataSource flowDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        logger.info("at db:" + url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setValidationQuery("SELECT 1 FROM DUAL");
        return dataSource;
        //return DataSourceBuilder.create().build();
    }

    @Bean("flowSqlSessionTemplate")
    public SqlSessionTemplate flowSqlSessionTemplate(@Qualifier("flowSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        SqlSessionTemplate template = new SqlSessionTemplate(sqlSessionFactory);
        return template;
    }

    @Bean("flowSqlSessionFactory")
    public SqlSessionFactory flowSqlSessionFactory(@Qualifier("flowDataSource") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean bean = new MybatisSqlSessionFactoryBean();
        MybatisConfiguration mybatisConfiguration = new MybatisConfiguration();
        //驼峰
        mybatisConfiguration.setMapUnderscoreToCamelCase(true);
        // 配置打印sql语句
        mybatisConfiguration.setLogImpl(StdOutImpl.class);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:com/changyou/hr/contract/mapper/*Mapper.xml"));

        bean.setDataSource(dataSource);

        //分页插件
//        Properties properties = new Properties();
//        properties.setProperty("helperDialect", "mysql");
//        properties.setProperty("offsetAsPageNum", "true");
//        properties.setProperty("rowBoundsWithCount", "true");
//        properties.setProperty("reasonable", "true");
//        Interceptor interceptor = new PageInterceptor();
//        interceptor.setProperties(properties);
//        bean.setPlugins(new Interceptor[] {interceptor});

            /*//添加XML目录
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            try {
                bean.setMapperLocations(resolver.getResources("classpath*:com/cyou/localjob/datasources/oa_hr/*.xml"));
                bean.getObject().getConfiguration().setMapUnderscoreToCamelCase(true);
                return bean.getObject();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }*/
        // 指明mapper.xml位置(配置文件中指明的xml位置会失效用此方式代替，具体原因未知)
        return bean.getObject();
    }

    //    @Primary
//    @Bean(name = "atTrans")
//    public PlatformTransactionManager annotationDrivenTransactionManager() {
//        return new DataSourceTransactionManager(autoTestDataSource());
//    }
    //@Primary
    @Bean("flowTransactionManager")
    public DataSourceTransactionManager flowTransactionManager() {
        return new DataSourceTransactionManager(flowDataSource());
    }
}
