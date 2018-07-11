package org.litespring.beans.factory.support;



import java.beans.BeanInfo;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.beanutils.BeanUtils;
import org.litespring.beans.BeanDefinition;
import org.litespring.beans.PropertyValue;
import org.litespring.beans.SimpleTypeConverter;
import org.litespring.beans.factory.BeanCreationException;


import org.litespring.beans.factory.config.ConfigurableBeanFactory;
import org.litespring.util.ClassUtils;

public class DefaultBeanFactory extends DefaultSingletonBeanRegistry
    implements ConfigurableBeanFactory,BeanDefinitionRegistry{

    private final Map<String,BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>(64);
    private ClassLoader beanClassLoader;
    public DefaultBeanFactory() {
    }

    public void registerBeanDefinition(String beanId, BeanDefinition bd) {
        this.beanDefinitionMap.put(beanId,bd);
    }

    //不保证线程安全 把解析xml的职责交给beanDifinitionRegistry
    public BeanDefinition getBeanDefinition(String beanId) {
        return this.beanDefinitionMap.get(beanId);
    }

    public Object getBean(String beanId) {
        BeanDefinition bd = this.getBeanDefinition(beanId);
        if(bd == null){
            throw  null;
        }
        if(bd.isSingleton()){
            Object bean = this.getSingleton(beanId);
            if(bean == null){
               bean = createBean(bd);
               this.registerSingleton(beanId,bean);
            }
            return bean;
        }
        return createBean(bd);

    }

    private Object createBean(BeanDefinition bd){
        //创建实例
        Object bean = instantiateBean(bd);
        //设置属性
        populateBean(bd,bean);
        return bean;
    }

    private void populateBean(BeanDefinition bd, Object bean) {
        List<PropertyValue> pvs = bd.getPropertyValues();
        if(pvs == null || pvs.isEmpty()){
            return;
        }
        SimpleTypeConverter converter = new SimpleTypeConverter();
        BeanDefinitionValueResolver valueResolver = new BeanDefinitionValueResolver(
            this);
        try {
            for (PropertyValue pv : pvs) {
                String propertyName = pv.getName();
                Object originalValue = pv.getValue();
                Object resolveValue = valueResolver.resolveValueIfNecessary(originalValue);
                //假设现在originalValue 表示的是 ref = accountDao 已经通过resolve得到了accountDao对象
                //调用Petstore 的 setAccount方法
                BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
                PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
                for (PropertyDescriptor pd : pds) {
                    if(pd.getName().equals(propertyName)){
                        Object convertedValue = converter.convertIfNecessary(resolveValue, pd.getPropertyType());
                        pd.getWriteMethod().invoke(bean,convertedValue);
                        break;
                    }
                }

            }
        } catch (Exception ex) {
            throw new BeanCreationException("Failed to obtain BeanInfo for class [ "+bd.getBeanClassName()+" ]");
        }
    }


    private Object instantiateBean(BeanDefinition bd) {
        if(bd.hasConstructorArgumentValues()){
            ConstructorResolver resolver = new ConstructorResolver(this);
            return resolver.autowireConstructor(bd);
        }else {
            ClassLoader cl = this.getBeanClassLoader();
            String beanClassName = bd.getBeanClassName();
            try {
                //通过反射获得对象 -- 加载的类 需要有无參构造方法
                Class<?> clz = cl.loadClass(beanClassName);
                return clz.newInstance();
            }catch (Exception e){
                //异常处理
                throw  new BeanCreationException("create bean for" + beanClassName + "failed",e);
            }
        }



    }

    /**
     * 使用apache 提供的工具类 实现反射 真的是非常简单了
     * @param bd
     * @param bean
     */
    private void populateBeanUseCommonBeanUtils(BeanDefinition bd,Object bean){
        List<PropertyValue> pvs = bd.getPropertyValues();
        if(pvs == null || pvs.isEmpty()){
            return;
        }

        BeanDefinitionValueResolver valueResolver = new BeanDefinitionValueResolver(
            this);
        try {
            for (PropertyValue pv : pvs) {
                String propertyName = pv.getName();
                Object originalValue = pv.getValue();
                Object resolvedValue = valueResolver.resolveValueIfNecessary(originalValue);
                BeanUtils.setProperty(bean,propertyName,resolvedValue);
            }
        } catch (Exception e) {
            throw new BeanCreationException("Populater bean property failed for [" + bd.getBeanClassName()+"]");
        }
    }

    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        this.beanClassLoader =beanClassLoader;
    }

    public ClassLoader getBeanClassLoader() {
        return (this.beanClassLoader != null ? this.beanClassLoader : ClassUtils.getDefaultClassLoader());
    }


}
