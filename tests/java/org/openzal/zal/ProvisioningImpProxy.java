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
    return super.galSearch(account, query, skip, limit);
  }
}