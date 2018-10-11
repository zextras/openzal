package org.openzal.zal;

import org.jetbrains.annotations.NotNull;
import org.openzal.zal.lucene.index.IndexStore;
import org.openzal.zal.lucene.analysis.Analyzer;

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
    return new Analyzer(mZObject.getAnalyzer());
  }

  public <T> T toZimbra(@NotNull Class<T> clazz)
  {
    return clazz.cast(mZObject);
  }
}
