package kr.mdns.madness.config;

import jakarta.annotation.Nonnull;
import javax.sql.DataSource;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.ObjectUtils;

@Configuration(proxyBeanMethods = false)
@Profile("h2")
public class H2DependsOnConfig {

    @Bean
    static BeanFactoryPostProcessor dataSourceDependsOnH2Server() {
        return beanFactory -> {
            String[] dataSourceBeans = beanFactory.getBeanNamesForType(DataSource.class, false, false);
            for (String name : dataSourceBeans) {
                BeanDefinition bd = beanFactory.getBeanDefinition(name);
                bd.setDependsOn(append(bd.getDependsOn(), "h2TcpServer"));
            }
        };
    }

    @Nonnull
    private static String[] append(String[] original, String beanName) {
        return (String[]) ObjectUtils.addObjectToArray(original, beanName);
    }
}
