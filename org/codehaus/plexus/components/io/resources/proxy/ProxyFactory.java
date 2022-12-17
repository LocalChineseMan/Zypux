package org.codehaus.plexus.components.io.resources.proxy;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.codehaus.plexus.components.io.functions.FileSupplier;
import org.codehaus.plexus.components.io.functions.ResourceAttributeSupplier;
import org.codehaus.plexus.components.io.functions.SymlinkDestinationSupplier;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;

public class ProxyFactory {
  public static PlexusIoResource createProxy(@Nonnull PlexusIoResource target, Object alternateSupplier) {
    List<Class<?>> interfaces = new ArrayList<>();
    interfaces.add(PlexusIoResource.class);
    if (target instanceof SymlinkDestinationSupplier)
      interfaces.add(SymlinkDestinationSupplier.class); 
    if (target instanceof FileSupplier)
      interfaces.add(FileSupplier.class); 
    if (target instanceof ResourceAttributeSupplier)
      interfaces.add(ResourceAttributeSupplier.class); 
    return (PlexusIoResource)Proxy.newProxyInstance(PlexusIoResource.class.getClassLoader(), (Class[])interfaces
        .<Class<?>[]>toArray((Class<?>[][])new Class[0]), new ResourceInvocationHandler(target, alternateSupplier));
  }
}
