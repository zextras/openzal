package org.openzal.zal.lucene.search;


import javax.annotation.Nonnull;

import java.io.Closeable;
import java.io.IOException;

public class IndexReader
  implements Closeable
{
  private final com.zimbra.cs.index.ZimbraIndexReader mZObject;

  public IndexReader(@Nonnull Object zObject)
  {
    mZObject = (com.zimbra.cs.index.ZimbraIndexReader) zObject;
  }

  public int countDocuments()
  {
    return mZObject.numDocs();
  }

  public int countDeletedDocument()
  {
    return mZObject.numDeletedDocs();
  }

  @Override
  public void close()
    throws IOException
  {
    mZObject.close();
  }

  @Override
  public String toString()
  {
    return mZObject.toString();
  }

  public <T> T toZimbra(@Nonnull Class<T> target)
  {
    return target.cast(mZObject);
  }
}