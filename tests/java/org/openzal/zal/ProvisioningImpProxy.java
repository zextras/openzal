package org.openzal.zal;

import org.jetbrains.annotations.NotNull;
import org.junit.Ignore;
import org.openzal.zal.Account;
import org.openzal.zal.ProvisioningImp;
import com.zimbra.common.service.ServiceException;
import org.openzal.zal.exceptions.ExceptionWrapper;

@Ignore
public class ProvisioningImpProxy extends ProvisioningImp
{
  public ProvisioningImpProxy()
  {
    this(com.zimbra.cs.account.Provisioning.getInstance());
  }

  public ProvisioningImpProxy(Object provisioning)
  {
    super(provisioning);
  }

  @Override
  @NotNull
  public GalSearchResult galSearch(@NotNull Account account, String query, int skip, int limit)
  {
/* $if ZimbraVersion >= 8.0.0 $ */
    return super.galSearch(account, query, skip, limit);
/* $else$
    GalSearchResult result = new GalSearchResult();

    try
    {
      com.zimbra.cs.account.Provisioning.SearchGalResult galResult = mProvisioning.searchGal(
              this.getDomain(account).toZimbra(com.zimbra.cs.account.Domain.class),
              query + "*",
 $if ZimbraVersion >= 7.0.0 $
              com.zimbra.cs.account.Provisioning.GalSearchType.all,
              null);
 $else$
              com.zimbra.cs.account.Provisioning.GAL_SEARCH_TYPE.ALL,
              "");
 $endif$
      for (com.zimbra.cs.account.GalContact galContact : galResult.getMatches())
      {
        result.addContact(new GalSearchResult.GalContact(galContact));
      }
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return result;
  $endif$ */
  }
}