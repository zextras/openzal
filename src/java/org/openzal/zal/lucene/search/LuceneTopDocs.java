package org.openzal.zal.lucene.search;

import com.zimbra.cs.index.ZimbraTopDocs;
import org.jetbrains.annotations.NotNull;
import org.openzal.zal.lib.ZalWrapper;

public class LuceneTopDocs
  implements ZalWrapper<ZimbraTopDocs>
{
  private ZimbraTopDocs mZObject;

  public LuceneTopDocs(@NotNull Object zObject)
  {
    mZObject = (ZimbraTopDocs) zObject;
  }

  @Override
  public String toString()
  {
    return mZObject.toString();
  }

  @Override
  public ZimbraTopDocs toZimbra()
  {
    return mZObject;
  }

  @Override
  public <T> T toZimbra(@NotNull Class<T> target)
  {
    return target.cast(mZObject);
  }
}
