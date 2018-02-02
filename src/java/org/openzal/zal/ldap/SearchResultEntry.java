package org.openzal.zal.ldap;

import org.jetbrains.annotations.NotNull;

public class SearchResultEntry extends Entry
{
  @NotNull
  private final com.unboundid.ldap.sdk.SearchResultEntry mSearchResultEntry;

  public SearchResultEntry(@NotNull Object searchResultEntry)
  {
    super(searchResultEntry);
    mSearchResultEntry = (com.unboundid.ldap.sdk.SearchResultEntry)searchResultEntry;
  }

  protected <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mSearchResultEntry);
  }

}
