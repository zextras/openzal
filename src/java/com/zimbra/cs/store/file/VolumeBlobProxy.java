package com.zimbra.cs.store.file;

import org.openzal.zal.Blob;

import java.io.File;

public class VolumeBlobProxy extends VolumeBlob
{
  private final Blob mBlob;

  public VolumeBlobProxy(Blob blob)
  {
    super(new File("/tmp/fakeblob"), (short) 0);
    mBlob = blob;
  }
}
