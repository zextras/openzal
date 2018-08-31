package org.openzal.zal;

import org.jetbrains.annotations.NotNull;
import org.openzal.zal.lib.ZalWrapper;
import org.openzal.zal.lucene.index.LuceneIndexStore;

public class MailboxIndex
  implements ZalWrapper<com.zimbra.cs.mailbox.MailboxIndex>
{
  private final com.zimbra.cs.mailbox.MailboxIndex mZObject;

  public MailboxIndex(@NotNull Object zObject) {
    mZObject = (com.zimbra.cs.mailbox.MailboxIndex) zObject;
  }

  public LuceneIndexStore getIndexStore()
  {
    return new LuceneIndexStore(mZObject.getIndexStore());
  }

  @Override
  public com.zimbra.cs.mailbox.MailboxIndex toZimbra()
  {
    return mZObject;
  }

  @Override
  public <T> T toZimbra(@NotNull Class<T> clazz)
  {
    return clazz.cast(mZObject);
  }
}
