package org.openzal.zal;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.ldap.ZLdapFilterFactory;
import org.openzal.zal.exceptions.ExceptionWrapper;
import org.openzal.zal.exceptions.ZimbraException;

import java.util.Collection;

public class SearchDirectoryOptions
{
  private final ZLdapFilterFactory mFilterFactory;
  private final com.zimbra.cs.account.SearchDirectoryOptions mOptions;

  public SearchDirectoryOptions()
  {
    mFilterFactory = ZLdapFilterFactory.getInstance();
    mOptions = new com.zimbra.cs.account.SearchDirectoryOptions();
  }

  public <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mOptions);
  }

  public SearchDirectoryOptions withType(ObjectType...types) throws ZimbraException
  {
    com.zimbra.cs.account.SearchDirectoryOptions.ObjectType[] zimbraTypes = new com.zimbra.cs.account.SearchDirectoryOptions.ObjectType[types.length];
    for (int i = 0; i < types.length; i++)
    {
      zimbraTypes[i] = types[i].toZimbra(com.zimbra.cs.account.SearchDirectoryOptions.ObjectType.class);
    }
    try
    {
      mOptions.setTypes(zimbraTypes);
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    return this;
  }

  public SearchDirectoryOptions withAttrs(Collection<String> attrs)
  {
    mOptions.setReturnAttrs(
      attrs.toArray(new String[attrs.size()])
    );
    return this;
  }

  public SearchDirectoryOptions includeAccounts()
  {
    mOptions.setFilter(
      mFilterFactory.allAccountsOnly()
    );
    return this;
  }

  public SearchDirectoryOptions includeCoses()
  {
    mOptions.setFilter(
      mFilterFactory.allCoses()
    );
    return this;
  }

  public SearchDirectoryOptions noDefaults()
  {
    mOptions.setMakeObjectOpt(
      com.zimbra.cs.account.SearchDirectoryOptions.MakeObjectOpt.NO_DEFAULTS
    );
    return this;
  }

  public SearchDirectoryOptions includeAccountsByCosId(String cosId)
  {
    mOptions.setFilter(
      mFilterFactory.allAccountsOnlyByCos(cosId)
    );
    return this;
  }

  public SearchDirectoryOptions includeAccountById(String accountId)
  {
    mOptions.setFilter(
      mFilterFactory.accountById(accountId)
    );
    return this;
  }

  public SearchDirectoryOptions includeAccountsByServerId(String serverId)
  {
    mOptions.setFilter(
      mFilterFactory.accountsHomedOnServerAccountsOnly(serverId)
    );
    return this;
  }

  public enum ObjectType
  {
    accounts(com.zimbra.cs.account.SearchDirectoryOptions.ObjectType.accounts),
    aliases(com.zimbra.cs.account.SearchDirectoryOptions.ObjectType.aliases),
    distributionlists(com.zimbra.cs.account.SearchDirectoryOptions.ObjectType.distributionlists),
    dynamicgroups(com.zimbra.cs.account.SearchDirectoryOptions.ObjectType.dynamicgroups),
    resources(com.zimbra.cs.account.SearchDirectoryOptions.ObjectType.resources),
    domains(com.zimbra.cs.account.SearchDirectoryOptions.ObjectType.domains),
    coses(com.zimbra.cs.account.SearchDirectoryOptions.ObjectType.coses),
    servers(com.zimbra.cs.account.SearchDirectoryOptions.ObjectType.servers),
    ucservices(com.zimbra.cs.account.SearchDirectoryOptions.ObjectType.ucservices),
    ;

    private final com.zimbra.cs.account.SearchDirectoryOptions.ObjectType mType;

    public <T> T toZimbra(Class<T> cls)
    {
      return cls.cast(mType);
    }

    ObjectType(com.zimbra.cs.account.SearchDirectoryOptions.ObjectType type)
    {
      mType = type;
    }
  }
}
