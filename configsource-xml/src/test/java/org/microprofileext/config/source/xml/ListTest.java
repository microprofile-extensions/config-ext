package org.microprofileext.config.source.xml;

import java.util.List;
import java.util.Set;
import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;
import org.apache.geronimo.config.ConfigImpl;
import org.apache.geronimo.config.cdi.ConfigExtension;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:phillip.kruger@phillip-kruger.com">Phillip Kruger</a>
 */
@RunWith(Arquillian.class)
public class ListTest {

    @Inject
    @ConfigProperty(name = "listTest")
    String stringList; 
    
    @Inject
    @ConfigProperty(name = "listTest")
    List<String> listList;
    
    @Inject
    @ConfigProperty(name = "listTest")
    Set<String> setList;
    
    @Inject
    @ConfigProperty(name = "listTest")
    String[] arrayList; 
    
    @Inject
    @ConfigProperty(name = "deepList.level1")
    List<String> deepList;
    
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackages(true, ConfigImpl.class.getPackage())
                .addPackages(true, Config.class.getPackage())
                .addAsServiceProviderAndClasses(Extension.class, ConfigExtension.class)
                .addAsServiceProviderAndClasses(ConfigSource.class, XmlConfigSource.class)
                .addAsManifestResource("META-INF/beans.xml");
    }

    @Test
    public void testStringList() {
        Assert.assertEquals("item1,item2,item3\\,stillItem3", stringList);
    }

    @Test
    public void testListList() {
        Assert.assertNotNull(listList);
        Assert.assertEquals(3, listList.size());
        
    }
    
    @Test
    public void testSetList() {
        Assert.assertNotNull(setList);
        Assert.assertEquals(3, setList.size());
    }
    
    @Test
    public void testArrayList() {
        Assert.assertNotNull(arrayList);
        Assert.assertEquals(3, arrayList.length);
    }
    
    @Test
    public void testDeepList(){
        Assert.assertNotNull(deepList);
        Assert.assertEquals(2, deepList.size());        
    }
}
