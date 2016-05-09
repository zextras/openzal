package org.openzal.zal.extension;

import org.openzal.zal.StagedBlob;

public interface StagedBlobWrapper<S extends StagedBlob>
{
  S wrap(StagedBlob stagedBlob);
}
