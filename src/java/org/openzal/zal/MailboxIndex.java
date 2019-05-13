package org.openzal.zal;

import org.apache.lucene.analysis.standard.ClassicAnalyzer;
import org.openzal.zal.lucene.analysis.Analyzer;
import org.openzal.zal.lucene.index.IndexStore;

import javax.annotation.Nonnull;

public class MailboxIndex
{
  private final Mailbox                            mMailbox;
  private final com.zimbra.cs.mailbox.MailboxIndex mZObject;

  @Deprecated
  public MailboxIndex(@Nonnull Object zObject)
  {
    this(null, zObject);
  }

  public MailboxIndex(@Nonnull Mailbox mailbox, @Nonnull Object zObject)
  {
    mMailbox = mailbox;
    mZObject = (com.zimbra.cs.mailbox.MailboxIndex) zObject;
  }

  public IndexStore getIndexStore()
  {
    return new IndexStore(this, mZObject.getIndexStore());
  }

  public Analyzer getAnalyzer()
  {
    /* $if ZimbraX == 1 $ */
    {
      return new Analyzer(new ClassicAnalyzer());
    }
    /* $else $ */
    {
      return new Analyzer(mZObject.getAnalyzer());
    }
    /* $endif $ */
  }

  public Mailbox getMailbox()
  {
    return mMailbox;
  }

  public <T> T toZimbra(@Nonnull Class<T> clazz)
  {
    return clazz.cast(mZObject);
  }
}
