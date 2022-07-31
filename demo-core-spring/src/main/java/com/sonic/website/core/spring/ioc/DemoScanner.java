package com.sonic.website.core.spring.ioc;

import java.util.Set;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import com.sonic.website.core.common.manager.annotation.ManangerInject;
import com.sonic.website.core.common.support.LogCore;

public class DemoScanner extends ClassPathBeanDefinitionScanner {

    public DemoScanner(BeanDefinitionRegistry registry) {
        super(registry);
    }

    /**
     * 使用注解过滤比类型过滤快
     * new AssignableTypeFilter(ManagerBase.class)会比较慢从9到12秒 ManagerBase可以放到外部判断
     */
    @Override
    public void registerDefaultFilters() {
        this.addIncludeFilter(new AnnotationTypeFilter(ManangerInject.class));// 9944 ms
        // this.addIncludeFilter(new AssignableTypeFilter(ManagerBase.class));//10710 ms
    }

    @Override
    public int scan(String... basePackages) {
        return super.scan(basePackages);
    }

    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
        LogCore.BASE.info("all mine is{}", beanDefinitions);
        // for (BeanDefinitionHolder holder : beanDefinitions) {
        // GenericBeanDefinition definition = (GenericBeanDefinition) holder.getBeanDefinition();
        // definition.getPropertyValues().add("innerClassName", definition.getBeanClassName());
        // definition.setBeanClass(FactoryBeanTest.class);
        // }
        return beanDefinitions;
    }

    /**
     * 重要方法 super.isCandidateComponent(beanDefinition)如果是抽象类或者是接口会返回false
     * beanDefinition.getMetadata().hasAnnotation(ManangerInject.class.getName());这个的结果是得写在子类上如果是父类的注解是inherited的话也不行
     */
    @Override
    public boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        LogCore.BASE.debug("look for ManangerInject! isCandidateComponent={} hasAnnotation={} name={}",
                    super.isCandidateComponent(beanDefinition),
                    beanDefinition.getMetadata().hasAnnotation(ManangerInject.class.getName()),
                    beanDefinition.getMetadata().getClassName());
        return super.isCandidateComponent(beanDefinition);
    }
}
