package org.openzal.zal.ldap;

import com.unboundid.ldap.sdk.Attribute;

public class ZalAttribute
{
  private final Attribute mAttribute;

  public ZalAttribute(
    String name,
    String[] values
  )
  {
    mAttribute = new Attribute(name, values);
  }

  public String[] getValues()
  {
    return mAttribute.getValues();
  }

  public String getBaseName()
  {
    return mAttribute.getBaseName();
  }
}
