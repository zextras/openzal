package org.openzal.zal.lucene.search;


import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;

/* $if ZimbraVersion >= 8.5.0 $ */
public class IndexReader
  implements Closeable
{
  private final com.zimbra.cs.index.ZimbraIndexReader mZObject;

  public IndexReader(@NotNull Object zObject) {
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

  public <T> T toZimbra(@NotNull Class<T> target)
  {
    return target.cast(mZObject);
  }
}
/* $else $
public class IndexReader
  implements Closeable
{
  public IndexReader(@NotNull Object zObject)
  {
    throw new UnsupportedOperationException();
  }

  public int countDocuments()
  {
    throw new UnsupportedOperationException();
  }

  public int countDeletedDocument()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void close()
    throws IOException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public String toString()
  {
    throw new UnsupportedOperationException();
  }

  public <T> T toZimbra(@NotNull Class<T> target)
  {
    throw new UnsupportedOperationException();
  }
}
/* $endif $ */