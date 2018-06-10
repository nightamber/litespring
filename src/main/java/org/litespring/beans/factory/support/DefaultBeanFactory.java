package org.litespring.beans.factory.support;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.litespring.beans.BeanDefinition;
import org.litespring.beans.factory.BeanCreationException;
import org.litespring.beans.factory.BeanDefinitionStoreException;
import org.litespring.beans.factory.BeanFactory;
import org.litespring.util.ClassUtils;

public class DefaultBeanFactory implements BeanFactory {
    public static final String ID_ATTRIBUTE = "id";
    public static final String CLASS_ATTRIBUTE = "class";
    private final Map<String,BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>();

    public DefaultBeanFactory(String configFile) {
        loadBeanDefinition(configFile);
    }

    private void loadBeanDefinition(String configFile) {
        InputStream is = null;
        try {
            ClassLoader cl = ClassUtils.getDefaultClassLoader();
            is = cl.getResourceAsStream(configFile);

            SAXReader reader = new SAXReader();
            Document doc = reader.read(is);
            //dom4j解析
            Element root = doc.getRootElement();//获得根目录 <beans>
            Iterator<Element> iter = root.elementIterator();
            while (iter.hasNext()){
                Element ele = (Element) iter.next();
                String id = ele.attributeValue(ID_ATTRIBUTE);
                String beanClassName = ele.attributeValue(CLASS_ATTRIBUTE);
                BeanDefinition bd = new GenericBeanDefinition(id, beanClassName);
                this.beanDefinitionMap.put(id,bd);//把解析的bean 保留在map中 可以在getBeanDefinition中可以返回
            }

        }catch (DocumentException e){
            //异常处理
            throw new BeanDefinitionStoreException("IOException parsing XML document",e);
        }finally {
            if(is != null){
                try{
                    is.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public BeanDefinition getBeanDefinition(String beanId) {
        return this.beanDefinitionMap.get(beanId);
    }

    public Object getBean(String beanId) {
        BeanDefinition bd = this.getBeanDefinition(beanId);
        if(bd == null){
            throw  new BeanCreationException("Bean Definition does not exist");
        }
        ClassLoader cl = ClassUtils.getDefaultClassLoader();
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
