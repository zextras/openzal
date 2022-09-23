package com.zimbra.cs;

import java.util.Map;
import javax.annotation.Nonnull;
import org.junit.Ignore;
import org.openzal.zal.Account;
import org.openzal.zal.Domain;
import org.openzal.zal.ProvisioningImp;
import org.openzal.zal.SimpleVisitor;
import org.openzal.zal.exceptions.ZimbraException;

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
  @Nonnull
  public GalSearchResult galSearch(@Nonnull Account account, String query, int skip, int limit)
  {
    return super.galSearch(account, query, skip, limit);
  }

  @Override
  @Nonnull
  public GalSearchResult galSearch(@Nonnull Account account, Domain domain, String query, int skip, int limit)
  {
    Map<String, Object> attrs = account.getAttrs(true);
    attrs.put("zimbraFeatureGalEnabled", "TRUE");
    attrs.put("zimbraFeatureGalAutoCompleteEnabled", "TRUE");
    account.setAttrs(attrs);
    return super.galSearch(account, domain, query, skip, limit);
  }

  @Override
  public void visitAllAccounts(@Nonnull SimpleVisitor<Account> visitor)
    throws ZimbraException
  {
    for( Domain domain : getAllDomains() )
    {
      for( Account account : getAllAccounts(domain) )
      {
        visitor.visit(account);
      }
    }
  }
}