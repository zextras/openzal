package org.openzal.zal.ldap;

import javax.annotation.Nonnull;

public class SearchScope
{
  public static final SearchScope SUB = new SearchScope(com.unboundid.ldap.sdk.SearchScope.SUB);
  public static final SearchScope BASE = new SearchScope(com.unboundid.ldap.sdk.SearchScope.BASE);
  public static final SearchScope ONE = new SearchScope(com.unboundid.ldap.sdk.SearchScope.ONE);

  @Nonnull
  private final com.unboundid.ldap.sdk.SearchScope mSearchScope;

  public SearchScope(@Nonnull Object searchScope)
  {
    mSearchScope = (com.unboundid.ldap.sdk.SearchScope)searchScope;
  }

  protected <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mSearchScope);
  }

}
