package org.openzal.zal;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class LDAPAttributeReader<S, A> {

  public static LDAPAttributeReader<org.openzal.zal.Entry,Boolean> bool(String name, boolean defaultValue) {
    return new LDAPAttributeReader<>(name, defaultValue, (entry) -> entry.getBooleanAttr(name, defaultValue));
  }

  public static LDAPAttributeReader<org.openzal.zal.Entry,String> string(String name, String defaultValue) {
    return new LDAPAttributeReader<>(name, defaultValue, (entry) -> entry.getAttr(name, defaultValue));
  }

  public static LDAPAttributeReader<org.openzal.zal.Entry, Set<String>> stringSet(String name) {
    return new LDAPAttributeReader<>(name, new HashSet<>(), (entry) -> entry.getMultiAttrSet(name));
  }

  private final String attributeName;
  private final A defaultValue;
  private final Function<S, A> readFunction;

  public LDAPAttributeReader(String attributeName, A defaultValue, Function<S, A> readFunction) {
    this.attributeName = attributeName;
    this.defaultValue = defaultValue;
    this.readFunction = readFunction;
  }

  public String getAttributeName() {
    return attributeName;
  }

  public A getDefaultValue() {
    return defaultValue;
  }

  public A read(S source) {
    A result = this.readFunction.apply(source);
    return result == null ? defaultValue : result;
  }

  public <S1> LDAPAttributeReader<S1, A> compose(Function<S1, S> readFunction) {
    return new LDAPAttributeReader<>(attributeName, defaultValue, readFunction.andThen(this.readFunction));
  }

  public <B> LDAPAttributeReader<S, B> map(Function<A, B> func) {
    return new LDAPAttributeReader<>(attributeName, func.apply(defaultValue), readFunction.andThen(func));
  }

}
