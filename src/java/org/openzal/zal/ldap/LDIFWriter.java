package org.openzal.zal.ldap;

import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;

public class LDIFWriter implements Closeable
{
  @NotNull
  private final com.unboundid.ldif.LDIFWriter mLDIFWriter;

  public LDIFWriter(@NotNull Object ldifWriter)
  {
    mLDIFWriter = (com.unboundid.ldif.LDIFWriter)ldifWriter;
  }

  protected <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mLDIFWriter);
  }

  public void writeEntry(Entry entry, String comment) throws IOException
  {
    mLDIFWriter.writeEntry(entry.toZimbra(com.unboundid.ldap.sdk.Entry.class),comment);
  }

  public void writeEntry(Entry entry) throws IOException
  {
    mLDIFWriter.writeEntry(entry.toZimbra(com.unboundid.ldap.sdk.Entry.class));
  }

  @Override
  public void close() throws IOException
  {
    mLDIFWriter.close();
  }

}
