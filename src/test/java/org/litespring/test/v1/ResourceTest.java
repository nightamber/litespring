package org.litespring.test.v1;

import java.io.InputStream;
import org.junit.Assert;
import org.junit.Test;
import org.litespring.core.io.ClassPathResource;
import org.litespring.core.io.FileSystemResource;
import org.litespring.core.io.Resource;
public class ResourceTest {
    @Test
    public void testClassPathResource() throws Exception {
        Resource resource = new ClassPathResource("petstore-v1.xml");
        InputStream is = null;
        try {
            is = resource.getInputStream();
            //讲道理这里需要根据读取内容进行验证而不是判断
            Assert.assertNotNull(is);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                is.close();
            }
        }

    }

    @Test
    public void testFileSystemResource() throws Exception {
        Resource resource = new FileSystemResource(
            "E:\\myspring\\litespring\\src\\test\\resource\\petstore-v1.xml");

        InputStream is = null;
        try {
            is = resource.getInputStream();
            Assert.assertNotNull(is);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                is.close();
            }
        }


    }
}
