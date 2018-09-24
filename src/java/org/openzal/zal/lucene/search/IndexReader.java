package org.openzal.zal.lucene.search;


import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;

public class IndexReader
  implements Closeable
{
  /* $if ZimbraVersion >= 8.5.0 $ */
  private final com.zimbra.cs.index.ZimbraIndexReader mZObject;
  /* $endif $ */

  public IndexReader(@NotNull Object zObject)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    mZObject = (com.zimbra.cs.index.ZimbraIndexReader) zObject;
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public int countDocuments()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return mZObject.numDocs();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public int countDeletedDocument()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return mZObject.numDeletedDocs();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  @Override
  public void close()
    throws IOException
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    mZObject.close();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  @Override
  public String toString()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return mZObject.toString();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public <T> T toZimbra(@NotNull Class<T> target)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return target.cast(mZObject);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }
}