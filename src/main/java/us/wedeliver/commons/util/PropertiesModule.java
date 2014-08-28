package us.wedeliver.commons.util;

import java.util.Properties;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class PropertiesModule extends AbstractModule {
  private Properties properties;

  public PropertiesModule(Properties properties) {
    this.properties = properties;
  }

  public PropertiesModule(String propertiesResource) {
    this.properties = PropertiesUtil.load(propertiesResource);
  }

  @Override
  protected void configure() {
    Names.bindProperties(binder(), properties);
  }

}
