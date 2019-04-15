package org.openzal.zal.ldap;

import javax.annotation.Nonnull;

public class Entry
{
  @Nonnull
  private final com.unboundid.ldap.sdk.Entry mEntry;

  public Entry(@Nonnull Object entry)
  {
    mEntry = (com.unboundid.ldap.sdk.Entry)entry;
  }

  protected <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mEntry);
  }

}
