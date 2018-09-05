package org.openzal.zal;

import org.jetbrains.annotations.NotNull;
import org.openzal.zal.lib.ZalWrapper;
import org.openzal.zal.lucene.index.LuceneIndexStore;

public class MailboxIndex
{
  private final com.zimbra.cs.mailbox.MailboxIndex mZObject;

  public MailboxIndex(@NotNull Object zObject) {
    mZObject = (com.zimbra.cs.mailbox.MailboxIndex) zObject;
  }

  public LuceneIndexStore getIndexStore()
  {
    return new LuceneIndexStore(mZObject.getIndexStore());
  }

  public <T> T toZimbra(@NotNull Class<T> clazz)
  {
    return clazz.cast(mZObject);
  }
}
