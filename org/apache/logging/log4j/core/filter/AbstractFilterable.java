package org.apache.logging.log4j.core.filter;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.PluginElement;

public abstract class Builder<B extends AbstractFilterable.Builder<B>> {
  @PluginElement("Filter")
  private Filter filter;
  
  @PluginElement("Properties")
  private Property[] propertyArray;
  
  public B asBuilder() {
    return (B)this;
  }
  
  public Filter getFilter() {
    return this.filter;
  }
  
  public Property[] getPropertyArray() {
    return this.propertyArray;
  }
  
  public B setFilter(Filter filter) {
    this.filter = filter;
    return asBuilder();
  }
  
  public B setPropertyArray(Property[] properties) {
    this.propertyArray = properties;
    return asBuilder();
  }
  
  @Deprecated
  public B withFilter(Filter filter) {
    return setFilter(filter);
  }
  
  public static abstract class AbstractFilterable {}
}
