package com.ibm.icu.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocalePriorityList implements Iterable<ULocale> {
  private static final double D0 = 0.0D;
  
  private static final Double D1 = Double.valueOf(1.0D);
  
  private static final Pattern languageSplitter = Pattern.compile("\\s*,\\s*");
  
  private static final Pattern weightSplitter = Pattern.compile("\\s*(\\S*)\\s*;\\s*q\\s*=\\s*(\\S*)");
  
  private final Map<ULocale, Double> languagesAndWeights;
  
  public static Builder add(ULocale languageCode) {
    return (new Builder()).add(languageCode);
  }
  
  public static Builder add(ULocale languageCode, double weight) {
    return (new Builder()).add(languageCode, weight);
  }
  
  public static Builder add(LocalePriorityList languagePriorityList) {
    return (new Builder()).add(languagePriorityList);
  }
  
  public static Builder add(String acceptLanguageString) {
    return (new Builder()).add(acceptLanguageString);
  }
  
  public Double getWeight(ULocale language) {
    return this.languagesAndWeights.get(language);
  }
  
  public String toString() {
    StringBuilder result = new StringBuilder();
    for (ULocale language : this.languagesAndWeights.keySet()) {
      if (result.length() != 0)
        result.append(", "); 
      result.append(language);
      double weight = ((Double)this.languagesAndWeights.get(language)).doubleValue();
      if (weight != D1.doubleValue())
        result.append(";q=").append(weight); 
    } 
    return result.toString();
  }
  
  public Iterator<ULocale> iterator() {
    return this.languagesAndWeights.keySet().iterator();
  }
  
  public boolean equals(Object o) {
    if (o == null)
      return false; 
    if (this == o)
      return true; 
    try {
      LocalePriorityList that = (LocalePriorityList)o;
      return this.languagesAndWeights.equals(that.languagesAndWeights);
    } catch (RuntimeException e) {
      return false;
    } 
  }
  
  public int hashCode() {
    return this.languagesAndWeights.hashCode();
  }
  
  private LocalePriorityList(Map<ULocale, Double> languageToWeight) {
    this.languagesAndWeights = languageToWeight;
  }
  
  public static class Builder {
    private final Map<ULocale, Double> languageToWeight = new LinkedHashMap<ULocale, Double>();
    
    public LocalePriorityList build() {
      return build(false);
    }
    
    public LocalePriorityList build(boolean preserveWeights) {
      Map<Double, Set<ULocale>> doubleCheck = new TreeMap<Double, Set<ULocale>>(LocalePriorityList.myDescendingDouble);
      for (ULocale lang : this.languageToWeight.keySet()) {
        Double weight = this.languageToWeight.get(lang);
        Set<ULocale> s = doubleCheck.get(weight);
        if (s == null)
          doubleCheck.put(weight, s = new LinkedHashSet<ULocale>()); 
        s.add(lang);
      } 
      Map<ULocale, Double> temp = new LinkedHashMap<ULocale, Double>();
      for (Map.Entry<Double, Set<ULocale>> langEntry : doubleCheck.entrySet()) {
        Double weight = langEntry.getKey();
        for (ULocale lang : langEntry.getValue())
          temp.put(lang, preserveWeights ? weight : LocalePriorityList.D1); 
      } 
      return new LocalePriorityList(Collections.unmodifiableMap(temp));
    }
    
    public Builder add(LocalePriorityList languagePriorityList) {
      for (ULocale language : languagePriorityList.languagesAndWeights.keySet())
        add(language, ((Double)languagePriorityList.languagesAndWeights.get(language)).doubleValue()); 
      return this;
    }
    
    public Builder add(ULocale languageCode) {
      return add(languageCode, LocalePriorityList.D1.doubleValue());
    }
    
    public Builder add(ULocale... languageCodes) {
      for (ULocale languageCode : languageCodes)
        add(languageCode, LocalePriorityList.D1.doubleValue()); 
      return this;
    }
    
    public Builder add(ULocale languageCode, double weight) {
      if (this.languageToWeight.containsKey(languageCode))
        this.languageToWeight.remove(languageCode); 
      if (weight <= 0.0D)
        return this; 
      if (weight > LocalePriorityList.D1.doubleValue())
        weight = LocalePriorityList.D1.doubleValue(); 
      this.languageToWeight.put(languageCode, Double.valueOf(weight));
      return this;
    }
    
    public Builder add(String acceptLanguageList) {
      String[] items = LocalePriorityList.languageSplitter.split(acceptLanguageList.trim());
      Matcher itemMatcher = LocalePriorityList.weightSplitter.matcher("");
      for (String item : items) {
        if (itemMatcher.reset(item).matches()) {
          ULocale language = new ULocale(itemMatcher.group(1));
          double weight = Double.parseDouble(itemMatcher.group(2));
          if (weight < 0.0D || weight > LocalePriorityList.D1.doubleValue())
            throw new IllegalArgumentException("Illegal weight, must be 0..1: " + weight); 
          add(language, weight);
        } else if (item.length() != 0) {
          add(new ULocale(item));
        } 
      } 
      return this;
    }
    
    private Builder() {}
  }
  
  private static Comparator<Double> myDescendingDouble = new Comparator<Double>() {
      public int compare(Double o1, Double o2) {
        return -o1.compareTo(o2);
      }
    };
}
