package META-INF.versions.9.org.apache.logging.log4j.util;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashSet;
import java.util.Objects;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Stream;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.LoaderUtil;

public final class ServiceLoaderUtil {
  public static <T> Stream<T> loadServices(Class<T> serviceType, MethodHandles.Lookup lookup) {
    return loadServices(serviceType, lookup, false);
  }
  
  public static <T> Stream<T> loadServices(Class<T> serviceType, MethodHandles.Lookup lookup, boolean useTccl) {
    return loadServices(serviceType, lookup, useTccl, true);
  }
  
  static <T> Stream<T> loadServices(Class<T> serviceType, MethodHandles.Lookup lookup, boolean useTccl, boolean verbose) {
    ClassLoader classLoader = lookup.lookupClass().getClassLoader();
    Stream<T> services = loadClassloaderServices(serviceType, lookup, classLoader, verbose);
    if (useTccl) {
      ClassLoader contextClassLoader = LoaderUtil.getThreadContextClassLoader();
      if (contextClassLoader != classLoader)
        services = Stream.concat(services, 
            loadClassloaderServices(serviceType, lookup, contextClassLoader, verbose)); 
    } 
    Set<Class<?>> classes = new HashSet<>();
    return services.filter(service -> classes.add(service.getClass()));
  }
  
  static <T> Stream<T> loadClassloaderServices(Class<T> serviceType, MethodHandles.Lookup lookup, ClassLoader classLoader, boolean verbose) {
    try {
      ServiceLoader<T> serviceLoader;
      MethodHandle loadHandle = lookup.findStatic(ServiceLoader.class, "load", 
          MethodType.methodType(ServiceLoader.class, Class.class, new Class[] { ClassLoader.class }));
      CallSite callSite = LambdaMetafactory.metafactory(lookup, "run", 
          
          MethodType.methodType(PrivilegedAction.class, Class.class, new Class[] { ClassLoader.class }), MethodType.methodType(Object.class), loadHandle, 
          
          MethodType.methodType(ServiceLoader.class));
      PrivilegedAction<ServiceLoader<T>> action = callSite.getTarget().bindTo(serviceType).bindTo(classLoader).invoke();
      if (System.getSecurityManager() == null) {
        serviceLoader = action.run();
      } else {
        MethodHandle privilegedHandle = lookup.findStatic(AccessController.class, "doPrivileged", 
            MethodType.methodType(Object.class, PrivilegedAction.class));
        serviceLoader = privilegedHandle.invoke(action);
      } 
      return serviceLoader.stream().map(provider -> {
            try {
              return provider.get();
            } catch (ServiceConfigurationError e) {
              if (verbose)
                StatusLogger.getLogger().warn("Unable to load service class for service {}", serviceType.getClass(), e); 
              return null;
            } 
          }).filter(Objects::nonNull);
    } catch (Throwable e) {
      if (verbose)
        StatusLogger.getLogger().error("Unable to load services for service {}", serviceType, e); 
      return Stream.empty();
    } 
  }
}
