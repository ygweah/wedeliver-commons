package us.wedeliver.commons.util;

import javax.inject.Singleton;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.google.inject.struts2.Struts2GuicePluginModule;

public class GuiceStrutsInitializer extends GuiceServletContextListener {
  private static final String MODULES_PARAM = "guice-modules";
  private static final String PROPERTIES_PARAM = "guice-properties";
  private static final String OVERRIDE_MODULES_PARAM = "guice-override-modules";
  private static final String OVERRIDE_PROPERTIES_PARAM = "guice-override-properties";

  private static final String INJECTOR_NAME = GuiceStrutsInitializer.class.getName();

  public static final Injector getInjector(ServletContext servletContext) {
    return (Injector) servletContext.getAttribute(INJECTOR_NAME);
  }

  protected Logger logger = LoggerFactory.getLogger(getClass());
  private Injector injector;

  @Override
  public Injector getInjector() {
    return injector;
  }

  protected void createInjector(ServletContext servletContext) {
    injector = Guice.createInjector(new Struts2GuicePluginModule(),
                                    createServletModule(servletContext),
                                    createApplicationModule(servletContext));
    servletContext.setAttribute(INJECTOR_NAME, injector);
  }

  protected Module createServletModule(ServletContext servletContext) {
    return new ServletModule() {
      @Override
      protected void configureServlets() {
        // Struts 2 setup
        bind(StrutsPrepareAndExecuteFilter.class).in(Singleton.class);
        filter("/*").through(StrutsPrepareAndExecuteFilter.class);
      }
    };
  }

  protected Module createApplicationModule(ServletContext servletContext) {
    String moduleNames = servletContext.getInitParameter(MODULES_PARAM);
    logger.info("Using guice modules: {}", moduleNames);

    String propertiesResources = servletContext.getInitParameter(PROPERTIES_PARAM);
    logger.info("Using guice properties: {}", propertiesResources);

    String overrideModuleNames = servletContext.getInitParameter(OVERRIDE_MODULES_PARAM);
    logger.info("Using guice override modules: {}", overrideModuleNames);

    String overridePropertiesResources = servletContext.getInitParameter(OVERRIDE_PROPERTIES_PARAM);
    logger.info("Using guice override properties: {}", overridePropertiesResources);

    return GuiceUtil.createModule(moduleNames, propertiesResources, overrideModuleNames, overridePropertiesResources);
  }

  @Override
  public void contextInitialized(ServletContextEvent servletContextEvent) {
    createInjector(servletContextEvent.getServletContext());
    super.contextInitialized(servletContextEvent);
  }

  @Override
  public void contextDestroyed(ServletContextEvent servletContextEvent) {
    try {
      if (injector != null) {
        ShutdownSupport shutdownSupport = injector.getInstance(ShutdownSupport.class);
        shutdownSupport.shutdown();
        injector = null;
      }
    } finally {
      super.contextDestroyed(servletContextEvent);
    }
  }

}
