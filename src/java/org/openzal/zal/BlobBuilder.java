package org.openzal.zal;

import org.openzal.zal.exceptions.ZimbraException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public interface BlobBuilder
{
  BlobBuilder setSizeHint(long size);
  long getSizeHint();
  long getTotalBytes();
  BlobBuilder disableCompression(boolean disable);
  BlobBuilder disableDigest(boolean disable);
  BlobBuilder init() throws IOException, ZimbraException;
  BlobBuilder append(InputStream in) throws IOException;
  BlobBuilder append(byte[] b, int off, int len) throws IOException;
  BlobBuilder append(byte[] b) throws IOException;
  BlobBuilder append(ByteBuffer bb) throws IOException;
  boolean isFinished();
  void dispose();
  Blob finish() throws IOException, ZimbraException;
  Blob getBlob();
}
