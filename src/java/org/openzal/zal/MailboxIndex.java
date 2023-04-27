package org.openzal.zal;

import org.openzal.zal.lucene.analysis.Analyzer;
import org.openzal.zal.lucene.index.IndexStore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MailboxIndex
{
  private final Mailbox                            mMailbox;
  private final com.zimbra.cs.mailbox.MailboxIndex mMailboxIndex;

  @Deprecated
  public MailboxIndex(@Nonnull Object zObject)
  {
    this(null, zObject);
  }

  public MailboxIndex(Mailbox mailbox, @Nonnull Object zObject)
  {
    mMailbox = mailbox;
    mMailboxIndex = (com.zimbra.cs.mailbox.MailboxIndex) zObject;
  }

  public IndexStore getIndexStore()
  {
    return new IndexStore(this, mMailboxIndex.getIndexStore());
  }

  public Analyzer getAnalyzer()
  {
    return new Analyzer(mMailboxIndex.getAnalyzer());
  }

  @Nullable
  public Mailbox getMailbox()
  {
    return mMailbox;
  }

  public <T> T toZimbra(@Nonnull Class<T> clazz)
  {
    return clazz.cast(mMailboxIndex);
  }
}
