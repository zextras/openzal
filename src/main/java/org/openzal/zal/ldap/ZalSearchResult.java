package org.openzal.zal.ldap;

import javax.annotation.Nonnull;

import java.util.ArrayList;
import java.util.List;

public class ZalSearchResult
{
  @Nonnull
  private final com.unboundid.ldap.sdk.SearchResult mSearchResult;

  ZalSearchResult(@Nonnull Object searchResult)
  {
    mSearchResult = (com.unboundid.ldap.sdk.SearchResult)searchResult;
  }

  protected <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mSearchResult);
  }

  public List<ZalSearchResultEntry> getSearchEntries()
  {
    List<com.unboundid.ldap.sdk.SearchResultEntry> entries = mSearchResult.getSearchEntries();
    List<ZalSearchResultEntry> result = new ArrayList<ZalSearchResultEntry>();

    for (com.unboundid.ldap.sdk.SearchResultEntry entry : entries)
    {
      result.add(new ZalSearchResultEntry(entry));
    }

    return result;
  }
}
