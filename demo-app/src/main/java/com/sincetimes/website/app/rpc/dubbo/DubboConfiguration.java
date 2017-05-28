/*package com.sincetimes.website.app.rpc.dubbo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ConsumerConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.ProviderConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.spring.AnnotationBean;
import com.alibaba.dubbo.rpc.Invoker;
import com.sincetimes.website.core.common.support.LogCore;
@Configuration
@ConditionalOnClass(Invoker.class)
@PropertySource(value = "classpath:/dubbo.properties")
public class DubboConfiguration {

    @Value("${dubbo.application.name}")
    private String applicationName;

    @Value("${dubbo.registr.protocol}")
    private String protocol;

    @Value("${dubbo.registry.address}")
    private String registryAddress;

    @Value("${dubbo.protocol.name}")
    private String protocolName;

    @Value("${dubbo.protocol.port}")
    private int protocolPort;

    @Value("${dubbo.provider.timeout}")
    private int timeout;

    @Value("${dubbo.provider.retries}")
    private int retries;

    @Value("${dubbo.provider.delay}")
    private int delay;
    
    *//**设置dubbo扫描包*//*
    @Bean
    public static AnnotationBean annotationBean(@Value("${dubbo.annotation.package}") String packageName) {
        AnnotationBean annotationBean = new AnnotationBean();
        annotationBean.setPackage(packageName);
        return annotationBean;
    }

    *//**dubbo上下文*//*
    @Bean
    public ApplicationConfig applicationConfig() {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName(this.applicationName);
        return applicationConfig;
    }

    *//** 注册zookeeper*//*
    @Bean
    public RegistryConfig registryConfig() {
        RegistryConfig registry = new RegistryConfig();
        registry.setProtocol(protocol);
        registry.setAddress(registryAddress);
        return registry;
    }

   *//**协议 *//*
    @Bean
    public ProtocolConfig protocolConfig() {
        ProtocolConfig protocolConfig = new ProtocolConfig();
        protocolConfig.setName(protocolName);
        protocolConfig.setPort(protocolPort);
        protocolConfig.setThreads(200);
        LogCore.RPC.info("protocolConfig:{}", protocolConfig.hashCode());
        return protocolConfig;
    }

    *//**服务提供者*//*
    @Bean(name="myProvider")
    public ProviderConfig providerConfig(ApplicationConfig applicationConfig, RegistryConfig registryConfig, ProtocolConfig protocolConfig) {
        ProviderConfig providerConfig = new ProviderConfig();
        providerConfig.setTimeout(timeout);
        providerConfig.setRetries(retries);
        providerConfig.setDelay(delay);
        providerConfig.setApplication(applicationConfig);
        providerConfig.setRegistry(registryConfig);
        providerConfig.setProtocol(protocolConfig);
        return providerConfig;
    }

    *//**dubbo消费*//*
    @Bean(name="defaultConsumer")
    public ConsumerConfig consumerConfig(ApplicationConfig applicationConfig, RegistryConfig registryConfig) {
    	ConsumerConfig consumerConfig = new ConsumerConfig();
        return consumerConfig;
    }
}
*/