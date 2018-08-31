package org.openzal.zal.lucene.search;


import org.jetbrains.annotations.NotNull;
import org.openzal.zal.lib.ZalWrapper;

public class LuceneIndexReader
  implements ZalWrapper<com.zimbra.cs.index.ZimbraIndexReader>
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

  @Override
  public com.zimbra.cs.index.ZimbraIndexReader toZimbra()
  {
    return mZObject;
  }

  @Override
  public <T> T toZimbra(@NotNull Class<T> target)
  {
    return target.cast(mZObject);
  }
}
