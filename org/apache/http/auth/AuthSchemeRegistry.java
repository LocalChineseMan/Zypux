package org.apache.http.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.config.Lookup;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;

@Deprecated
@ThreadSafe
public final class AuthSchemeRegistry implements Lookup<AuthSchemeProvider> {
  private final ConcurrentHashMap<String, AuthSchemeFactory> registeredSchemes = new ConcurrentHashMap<String, AuthSchemeFactory>();
  
  public void register(String name, AuthSchemeFactory factory) {
    Args.notNull(name, "Name");
    Args.notNull(factory, "Authentication scheme factory");
    this.registeredSchemes.put(name.toLowerCase(Locale.ENGLISH), factory);
  }
  
  public void unregister(String name) {
    Args.notNull(name, "Name");
    this.registeredSchemes.remove(name.toLowerCase(Locale.ENGLISH));
  }
  
  public AuthScheme getAuthScheme(String name, HttpParams params) throws IllegalStateException {
    Args.notNull(name, "Name");
    AuthSchemeFactory factory = this.registeredSchemes.get(name.toLowerCase(Locale.ENGLISH));
    if (factory != null)
      return factory.newInstance(params); 
    throw new IllegalStateException("Unsupported authentication scheme: " + name);
  }
  
  public List<String> getSchemeNames() {
    return new ArrayList<String>(this.registeredSchemes.keySet());
  }
  
  public void setItems(Map<String, AuthSchemeFactory> map) {
    if (map == null)
      return; 
    this.registeredSchemes.clear();
    this.registeredSchemes.putAll(map);
  }
  
  public AuthSchemeProvider lookup(String name) {
    return (AuthSchemeProvider)new Object(this, name);
  }
}
