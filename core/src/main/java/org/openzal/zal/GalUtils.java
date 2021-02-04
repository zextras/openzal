package org.openzal.zal;

import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.Domain;
import com.zimbra.cs.datasource.DataSourceManager;
import com.zimbra.cs.gal.GalGroup;
import com.zimbra.cs.gal.GalImport;
import com.zimbra.soap.admin.type.DataSourceType;
import org.openzal.zal.exceptions.ExceptionWrapper;

public class GalUtils
{
  private final Provisioning mProvisioning;
  private final MailboxManager mMailboxManager;

  public GalUtils(
    Provisioning provisioning,
    MailboxManager mailboxManager
  )
  {
    mProvisioning = provisioning;
    mMailboxManager = mailboxManager;
  }

  /**
   * Test utility
   * Flush internalGAL of the provided domain
   */
  public void forceRefresh(org.openzal.zal.Domain domain)
  {
    String[] accountIds = domain.toZimbra(Domain.class).getGalAccountId();

    if (accountIds != null)
    {
      for (String accountId : accountIds)
      {
        try
        {
          Account account = mProvisioning.getAccountById(accountId);
          if (account == null)
          {
            continue;
          }

          for (org.openzal.zal.DataSource zalDs : account.getAllDataSources())
          {
            DataSource ds = zalDs.toZimbra();

            if (ds.getType().equals(DataSourceType.gal))
            {
              int fid = ds.getFolderId();
              DataSource.DataImport dataImport = DataSourceManager.getInstance().getDataImport(ds);
              if (dataImport instanceof GalImport)
              {
                ((GalImport) dataImport).importGal(fid, true, true);
              }
            }
          }

          Mailbox mailboxByAccount = mMailboxManager.getMailboxByAccount(account);
          mailboxByAccount.startReIndex();
          while( mailboxByAccount.isReIndexInProgress() )
          {
            // We need to wait for indexing to finish otherwise the Mailbox will deadlock
            Thread.yield();
          }
        } catch (Exception ex) {
          throw ExceptionWrapper.wrap(ex);
        }
      }
    }

    GalGroup.flushCache(domain.toZimbra(Domain.class));
  }
}
