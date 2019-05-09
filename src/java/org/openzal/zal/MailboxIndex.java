package org.openzal.zal;

import org.openzal.zal.index.x.solr.SolrIndexStore;
import org.openzal.zal.lucene.analysis.Analyzer;
import org.openzal.zal.lucene.index.IndexStore;

import javax.annotation.Nonnull;

public class MailboxIndex
{
  private final Mailbox mMailbox;
  private final com.zimbra.cs.mailbox.MailboxIndex mZObject;

  @Deprecated
  public MailboxIndex(@Nonnull Object zObject)
  {
    throw new UnsupportedOperationException();
  }

  public MailboxIndex(@Nonnull Mailbox mailbox, @Nonnull Object zObject)
  {
    /* $if ZimbraX == 1 $
    throw new UnsupportedOperationException();
    /* $else $ */
    mMailbox = mailbox;
    mZObject = (com.zimbra.cs.mailbox.MailboxIndex) zObject;
    /* $endif $ */
  }

  public IndexStore getIndexStore()
  {
    return new IndexStore(mZObject.getIndexStore());
  }

  public org.openzal.zal.index.x.IndexStore getTmpIndexStore()
  {
    /* $if ZimbraX == 1 $
    {
      return new SolrIndexStore(this, mZObject.getIndexStore());
    }
    /* $else $ */
    {
      throw new UnsupportedOperationException();
    }
    /* $endif $ */
  }

  public Analyzer getAnalyzer()
  {
    /* $if ZimbraX == 1 $
    {
      throw new UnsupportedOperationException();
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
