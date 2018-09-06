package org.openzal.zal.lucene.search;

import org.jetbrains.annotations.NotNull;
import org.openzal.zal.lib.ZalWrapper;

/* $if ZimbraVersion >= 8.5.0 $ */
public class TopDocs
{
  private com.zimbra.cs.index.ZimbraTopDocs mZObject;

  public TopDocs(@NotNull Object zObject)
  {
    mZObject = (com.zimbra.cs.index.ZimbraTopDocs) zObject;
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
public class TopDocs
{
  public TopDocs(@NotNull Object zObject)
  {
    throw new UnsupportedOperationException();
  }

  public <T> T toZimbra(@NotNull Class<T> target)
  {
    throw new UnsupportedOperationException();
  }
}
/* $endif $ */