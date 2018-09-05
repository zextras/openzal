package org.openzal.zal.lucene.search;


import org.jetbrains.annotations.NotNull;
import org.openzal.zal.lib.ZalWrapper;

/* $if ZimbraVersion >= 8.5.0 $ */
public class LuceneIndexReader
{
  private final com.zimbra.cs.index.ZimbraIndexReader mZObject;

  public LuceneIndexReader(@NotNull Object zObject) {
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
public class LuceneIndexReader
{
  public LuceneIndexReader(@NotNull Object zObject)
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