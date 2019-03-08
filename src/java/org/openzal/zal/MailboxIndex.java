package org.openzal.zal;

import org.jetbrains.annotations.NotNull;
import org.openzal.zal.lucene.analysis.Analyzer;
import org.openzal.zal.lucene.index.IndexStore;

public class MailboxIndex
{
  private final com.zimbra.cs.mailbox.MailboxIndex mZObject;

  public MailboxIndex(@NotNull Object zObject) {
    mZObject = (com.zimbra.cs.mailbox.MailboxIndex) zObject;
  }

  public IndexStore getIndexStore()
  {
    return new IndexStore(mZObject.getIndexStore());
  }

  public Analyzer getAnalyzer()
  {
    /* $if ZimbraX == 1 $
    throw new UnsupportedOperationException();
    /* $else $ */
    return new Analyzer(mZObject.getAnalyzer());
    /* $endif $ */
  }

  public <T> T toZimbra(@NotNull Class<T> clazz)
  {
    return clazz.cast(mZObject);
  }
}
