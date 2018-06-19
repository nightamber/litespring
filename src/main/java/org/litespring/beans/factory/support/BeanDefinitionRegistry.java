package org.litespring.beans.factory.support;

import org.litespring.beans.BeanDefinition;

public interface BeanDefinitionRegistry {
    //获取beandefinition
    BeanDefinition getBeanDefinition(String beanId);
    void registerBeanDefinition(String beanId, BeanDefinition bd);
}
