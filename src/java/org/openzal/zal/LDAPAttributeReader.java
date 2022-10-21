package org.openzal.zal;

import com.zimbra.cs.account.Entry;

import java.util.function.Function;

public class LDAPAttributeReader<S, A> {

  private final String attributeName;
  private final A defaultValue;
  private final Function<S, A> readFunction;

  private static <A> LDAPAttributeReader entry(String attributeName, A defaultValue, Function<Entry, A> readFunction) {
    return new LDAPAttributeReader(attributeName, defaultValue, readFunction);
  }

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

}
