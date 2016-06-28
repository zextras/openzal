package com.zimbra.cs.store.file;

import java.io.File;

public class VolumeBlobProxy extends VolumeBlob
{
  public VolumeBlobProxy()
  {
    super(new File("/tmp/fakeblob"), (short) 0);
  }

  public VolumeBlobProxy(Object blob)
  {
    super(((VolumeBlob)blob).getFile(), ((VolumeBlob)blob).getVolumeId());
  }

  @Override
  public short getVolumeId()
  {
    return super.getVolumeId();
  }

  public static boolean isVolumeBlob(Object blob)
  {
    return blob != null && VolumeBlob.class.isAssignableFrom(blob.getClass());
  }
}
