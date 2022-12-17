package org.apache.logging.log4j.core.appender;

import java.io.Serializable;
import java.nio.charset.Charset;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.filter.AbstractFilterable;
import org.apache.logging.log4j.core.layout.PatternLayout;

public abstract class Builder<B extends AbstractAppender.Builder<B>> extends AbstractFilterable.Builder<B> {
  @PluginBuilderAttribute
  private boolean ignoreExceptions = true;
  
  @PluginElement("Layout")
  private Layout<? extends Serializable> layout;
  
  @PluginBuilderAttribute
  @Required(message = "No appender name provided")
  private String name;
  
  @PluginConfiguration
  private Configuration configuration;
  
  public Configuration getConfiguration() {
    return this.configuration;
  }
  
  public Layout<? extends Serializable> getLayout() {
    return this.layout;
  }
  
  public String getName() {
    return this.name;
  }
  
  public Layout<? extends Serializable> getOrCreateLayout() {
    if (this.layout == null)
      return (Layout<? extends Serializable>)PatternLayout.createDefaultLayout(this.configuration); 
    return this.layout;
  }
  
  public Layout<? extends Serializable> getOrCreateLayout(Charset charset) {
    if (this.layout == null)
      return (Layout<? extends Serializable>)PatternLayout.newBuilder().withCharset(charset).withConfiguration(this.configuration).build(); 
    return this.layout;
  }
  
  public boolean isIgnoreExceptions() {
    return this.ignoreExceptions;
  }
  
  public B setConfiguration(Configuration configuration) {
    this.configuration = configuration;
    return (B)asBuilder();
  }
  
  public B setIgnoreExceptions(boolean ignoreExceptions) {
    this.ignoreExceptions = ignoreExceptions;
    return (B)asBuilder();
  }
  
  public B setLayout(Layout<? extends Serializable> layout) {
    this.layout = layout;
    return (B)asBuilder();
  }
  
  public B setName(String name) {
    this.name = name;
    return (B)asBuilder();
  }
  
  @Deprecated
  public B withConfiguration(Configuration configuration) {
    this.configuration = configuration;
    return (B)asBuilder();
  }
  
  @Deprecated
  public B withIgnoreExceptions(boolean ignoreExceptions) {
    return setIgnoreExceptions(ignoreExceptions);
  }
  
  @Deprecated
  public B withLayout(Layout<? extends Serializable> layout) {
    return setLayout(layout);
  }
  
  @Deprecated
  public B withName(String name) {
    return setName(name);
  }
  
  public String getErrorPrefix() {
    Class<?> appenderClass = getClass().getEnclosingClass();
    String name = getName();
    StringBuilder sb = new StringBuilder((appenderClass != null) ? appenderClass.getSimpleName() : "Appender");
    if (name != null)
      sb.append(" '").append(name).append("'"); 
    return sb.toString();
  }
  
  public static abstract class AbstractAppender {}
}
