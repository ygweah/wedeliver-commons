package us.wedeliver.commons.util;

import javax.inject.Singleton;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.google.inject.struts2.Struts2GuicePluginModule;

public class GuiceInitializer extends GuiceServletContextListener {
  private static final String MODULES_PARAM = "guice-modules";
  private static final String PROPERTIES_PARAM = "guice-properties";
  private static final String OVERRIDE_MODULES_PARAM = "guice-override-modules";
  private static final String OVERRIDE_PROPERTIES_PARAM = "guice-override-properties";

  private Injector injector;

  @Override
  public Injector getInjector() {
    return injector;
  }

  protected void createInjector(ServletContext servletContext) {
    injector = Guice.createInjector(new Struts2GuicePluginModule(),
                                    createServletModule(servletContext),
                                    createApplicationModule(servletContext));
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
    String propertiesResources = servletContext.getInitParameter(PROPERTIES_PARAM);
    String overrideModuleNames = servletContext.getInitParameter(OVERRIDE_MODULES_PARAM);
    String overridePropertiesResources = servletContext.getInitParameter(OVERRIDE_PROPERTIES_PARAM);
    return GuiceUtil.createModule(moduleNames, propertiesResources, overrideModuleNames, overridePropertiesResources);
  }

  @Override
  public void contextInitialized(ServletContextEvent servletContextEvent) {
    createInjector(servletContextEvent.getServletContext());
    super.contextInitialized(servletContextEvent);
  }

  @Override
  public void contextDestroyed(ServletContextEvent servletContextEvent) {
    ShutdownSupport shutdownSupport = injector.getInstance(ShutdownSupport.class);
    shutdownSupport.shutdown();

    super.contextDestroyed(servletContextEvent);
  }

}
