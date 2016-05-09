package org.openzal.zal.extension;

import org.openzal.zal.Blob;

public interface BlobWrapper<B extends Blob>
{
  B wrap(Blob blob);
}
