package org.openzal.zal.ldap;

import com.unboundid.ldap.sdk.Attribute;
import javax.annotation.Nonnull;

public class SearchResultEntry
{
  @Nonnull
  private final com.unboundid.ldap.sdk.SearchResultEntry mSearchResultEntry;

  public SearchResultEntry(@Nonnull Object searchResultEntry)
  {
    mSearchResultEntry = (com.unboundid.ldap.sdk.SearchResultEntry)searchResultEntry;
  }

  public SearchResultEntry(String dn, String[] attributes) {
    Attribute[] attrs = new Attribute[attributes.length];
    for (int i = 0 ; i < attributes.length; i++)
    {
      attrs[i] = new Attribute(attributes[i]);
    }
    mSearchResultEntry = new com.unboundid.ldap.sdk.SearchResultEntry(dn,attrs);
  }


  protected <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mSearchResultEntry);
  }

}
