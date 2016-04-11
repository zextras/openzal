package org.openzal.zal;

public interface StagedBlob
{
  Mailbox getMailbox();
  long getSize();
  String getLocator();
  String getDigest();
  Blob getBlob();
  <T> T toZimbra(Class<T> cls);
}
