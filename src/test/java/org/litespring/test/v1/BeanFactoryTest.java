package org.litespring.test.v1;

import org.junit.Assert;
import org.junit.Test;
import org.litespring.beans.BeanDefinition;
import org.litespring.beans.factory.BeanCreationException;
import org.litespring.beans.factory.BeanDefinitionStoreException;
import org.litespring.beans.factory.BeanFactory;
import org.litespring.beans.factory.support.DefaultBeanFactory;
import org.litespring.service.v1.PetStoreService;

import static org.junit.Assert.*;

public class BeanFactoryTest {

    /**
     * 通过TDD 的方式编程 即 测试驱动编程
     * 置顶而下编写 之后在消除编译错误
     * 简单的说 就是 先完成 想要的测试功能 之后再 为了这个功能补全代码
     * 目标 尽可能 减少耦合
     */
    @Test
    public void testGetBean(){
        //BeanFactory 接口 TDD编程
        BeanFactory factory = new DefaultBeanFactory("petstore-v1.xml");//通过解析xml
        //定义bean 接口
        BeanDefinition bd = factory.getBeanDefinition("petStore");
        //判断与 xml中的是否相等
        assertEquals("org.litespring.service.v1.PetStoreService",bd.getBeanClassName());
        //获得bean
        PetStoreService petStore = (PetStoreService) factory.getBean("petStore");
        //测试 是否获得bean
        assertNotNull(petStore);
    }

    @Test
    public void testInvalidBean(){
        BeanFactory factory = new DefaultBeanFactory("petstore-v1.xml");
        try {
            factory.getBean("invalidBean");
        }catch (BeanCreationException e){
            return;
        }
        Assert.fail("expect BeanCreationException");
    }

    @Test
    public void testInvalidXML(){
        try {
            new DefaultBeanFactory("xxx.xml");
        }catch (BeanDefinitionStoreException e){
            return;
        }

        Assert.fail("expect BeanDefinitionStoreException");
    }



}
