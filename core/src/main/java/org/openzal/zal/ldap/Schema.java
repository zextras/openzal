package org.openzal.zal.ldap;

import javax.annotation.Nonnull;

public class Schema
{
  @Nonnull
  private final com.unboundid.ldap.sdk.schema.Schema mSchema;

  public Schema(@Nonnull Object schema)
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
