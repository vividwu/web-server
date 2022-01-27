package com.vivid.framework.web.ou.config.db;

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

/**
 * Created by wuwei2_m on 2018/10/19.
 */
@Configuration
//@EnableTransactionManagement 全局application注解了
@MapperScan(basePackages = {"com.vivid.framework.web.ou.mapper"}, sqlSessionTemplateRef = "dbSqlSessionTemplate")
public class DatabaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    @Value("${db.datasource.driver-class-name}")
    private String driverClassName;
    @Value("${db.datasource.url}")
    private String url;
    @Value("${db.datasource.username}")
    private String username;
    @Value("${db.datasource.password}")
    private String password;


    @Bean("dbDataSource")
    //@ConfigurationProperties(prefix = "atsync.datasource")
    public DataSource dbDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        logger.info("at db:"+url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setValidationQuery("SELECT 1");
        return dataSource;
        //return DataSourceBuilder.create().build();
    }

    @Bean("dbSqlSessionTemplate")
    public SqlSessionTemplate dbSqlSessionTemplate(@Qualifier("dbSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        SqlSessionTemplate template = new SqlSessionTemplate(sqlSessionFactory);
        return template;
    }

    @Bean("dbSqlSessionFactory")
    public SqlSessionFactory dbSqlSessionFactory(@Qualifier("dbDataSource") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean bean = new MybatisSqlSessionFactoryBean();
        MybatisConfiguration mybatisConfiguration = new MybatisConfiguration();
        //驼峰
        mybatisConfiguration.setMapUnderscoreToCamelCase(true);
        // 配置打印sql语句
        mybatisConfiguration.setLogImpl(StdOutImpl.class);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:com/vivid/framework/web/ou/mapper/*.xml"));

        bean.setDataSource(dataSource);
        return bean.getObject();
    }

//    @Primary
//    @Bean(name = "atTrans")
//    public PlatformTransactionManager annotationDrivenTransactionManager() {
//        return new DataSourceTransactionManager(autoTestDataSource());
//    }
    //@Primary
    @Bean("dbTransactionManager")
    public DataSourceTransactionManager dbTransactionManager(){
        return new DataSourceTransactionManager(dbDataSource());
    }
}
