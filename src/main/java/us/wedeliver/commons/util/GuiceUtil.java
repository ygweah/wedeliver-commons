package us.wedeliver.commons.util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;

public class GuiceUtil {
  private static final Logger logger = LoggerFactory.getLogger(GuiceUtil.class);

  public static Module createModule(String moduleNames,
                                    String propertiesResources,
                                    String overrideModuleNames,
                                    String overridePropertiesResources) {
    Module module = createGuiceModule(moduleNames);
    Module propertiesModule = createPropertiesModule(propertiesResources);
    Module overrideModule = createGuiceModule(overrideModuleNames);
    Module overridePropertiesModule = createPropertiesModule(overridePropertiesResources);
    return Modules.override(module, propertiesModule).with(overrideModule, overridePropertiesModule);
  }

  public static Injector createInjector(String moduleNames,
                                        String propertiesResources,
                                        String overrideModuleNames,
                                        String overridePropertiesResources) {
    return Guice.createInjector(createModule(moduleNames,
                                             propertiesResources,
                                             overrideModuleNames,
                                             overridePropertiesResources));
  }

  private static Module createGuiceModule(String moduleNames) {
    logger.info("Creating guice modules: {}", moduleNames);
    Collection<Module> modules = new LinkedList<>();
    if (moduleNames != null && !moduleNames.isEmpty()) {
      for (String moduleName : moduleNames.split(",")) {
        moduleName = moduleName.trim();
        if (!moduleName.isEmpty())
          modules.add(createGuiceModuleInstance(moduleName));
      }
    }
    return Modules.combine(modules);
  }

  private static Module createPropertiesModule(String propertiesResources) {
    logger.info("Creating properties modules: {}", propertiesResources);
    Collection<Module> modules = new LinkedList<>();
    if (propertiesResources != null && !propertiesResources.isEmpty()) {
      for (String propertiesResource : propertiesResources.split(",")) {
        propertiesResource = propertiesResource.trim();
        if (!propertiesResource.isEmpty())
          modules.add(createPropertiesModuleInstance(propertiesResource));
      }
    }
    return Modules.combine(modules);
  }

  private static Module createGuiceModuleInstance(final String moduleName) {
    logger.info("Creating guice module instance: {}", moduleName);
    return ExceptionUtil.unchecked(new Callable<Module>() {

      @Override
      public Module call() throws Exception {
        @SuppressWarnings("unchecked")
        Class<Module> moduleClass = (Class<Module>) Class.forName(moduleName.trim());
        return moduleClass.newInstance();
      }
    });
  }

  private static Module createPropertiesModuleInstance(String propertiesResource) {
    logger.info("Creating properties module instance: {}", propertiesResource);
    return new PropertiesModule(propertiesResource);
  }

}
