package org.litespring.beans.factory.support;

import org.litespring.beans.BeanDefinition;

public interface BeanNameGenerator {
    String generateBeanName(BeanDefinition definition,BeanDefinitionRegistry registry);
}
