package com.zimbra.cs.store.file;

import java.io.File;

public class VolumeBlobProxy extends VolumeBlob
{
  public VolumeBlobProxy()
  {
    super(new File("/tmp/fakeblob"), (short) 0);
  }

  @Override
  public short getVolumeId()
  {
    throw new UnsupportedOperationException();
  }
}
