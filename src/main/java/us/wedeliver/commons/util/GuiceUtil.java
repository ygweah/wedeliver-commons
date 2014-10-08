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

  public static Module createModule(String moduleNames, String overrideModuleNames) {
    Module module = createGuiceModule(overrideModuleNames);
    Module overrideModule = createGuiceModule(overrideModuleNames);
    return Modules.override(module).with(overrideModule);
  }

  public static Injector createInjector(String moduleNames, String overrideModuleNames) {
    return Guice.createInjector(createModule(moduleNames, overrideModuleNames));
  }

  private static Module createGuiceModule(String moduleNames) {
    logger.info("Creating guice modules: {}", moduleNames);
    Collection<Module> modules = new LinkedList<>();
    if (moduleNames != null && !moduleNames.isEmpty()) {
      for (String moduleName : moduleNames.split(",")) {
        modules.add(createGuiceModuleFrom(moduleName));
      }
    }
    return Modules.combine(modules);
  }

  private static Module createGuiceModuleFrom(final String moduleName) {
    logger.info("Creating guice module: {}", moduleName);
    return ExceptionUtil.unchecked(new Callable<Module>() {

      @Override
      public Module call() throws Exception {
        @SuppressWarnings("unchecked")
        Class<Module> moduleClass = (Class<Module>) Class.forName(moduleName.trim());
        return moduleClass.newInstance();
      }
    });
  }

}
