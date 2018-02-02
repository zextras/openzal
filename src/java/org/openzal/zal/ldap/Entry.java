package org.openzal.zal.ldap;

import org.jetbrains.annotations.NotNull;

public class Entry
{
  @NotNull
  private final com.unboundid.ldap.sdk.Entry mEntry;

  public Entry(@NotNull Object entry)
  {
    mEntry = (com.unboundid.ldap.sdk.Entry)entry;
  }

  protected <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mEntry);
  }

}
