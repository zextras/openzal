package org.openzal.zal;

public class InternalOverrideBlobProxy
{
  private final InternalOverrideBlob mBlob;

  public InternalOverrideBlobProxy(Object blob)
  {
    mBlob = (InternalOverrideBlob) blob;
  }

  public Blob getWrappedObject()
  {
    return mBlob.getWrappedObject();
  }
}
