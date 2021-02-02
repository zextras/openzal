package org.openzal.zal.ldap;

import javax.annotation.Nonnull;

import java.util.ArrayList;
import java.util.List;

public class SearchResult
{
  @Nonnull
  private final com.unboundid.ldap.sdk.SearchResult mSearchResult;

  public SearchResult(@Nonnull Object searchResult)
  {
    mSearchResult = (com.unboundid.ldap.sdk.SearchResult)searchResult;
  }

  protected <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mSearchResult);
  }

  public List<SearchResultEntry> getSearchEntries()
  {
    List<com.unboundid.ldap.sdk.SearchResultEntry> entries = mSearchResult.getSearchEntries();
    List<SearchResultEntry> result = new ArrayList<SearchResultEntry>();

    for (com.unboundid.ldap.sdk.SearchResultEntry entry : entries)
    {
      result.add(new SearchResultEntry(entry));
    }

    return result;
  }
}
