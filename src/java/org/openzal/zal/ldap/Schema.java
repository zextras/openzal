package org.openzal.zal.ldap;

import org.jetbrains.annotations.NotNull;

public class Schema
{
  @NotNull
  private final com.unboundid.ldap.sdk.schema.Schema mSchema;

  public Schema(@NotNull Object schema)
  {
    mSchema = (com.unboundid.ldap.sdk.schema.Schema)schema;
  }

  protected <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mSchema);
  }

  public Entry getSchemaEntry()
  {
    return new Entry(mSchema.getSchemaEntry());
  }
}
