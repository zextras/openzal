package org.openzal.zal.ldap;

import com.unboundid.ldap.sdk.Attribute;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ZalSearchResultEntry
{
  @Nonnull
  private final com.unboundid.ldap.sdk.SearchResultEntry mSearchResultEntry;

  ZalSearchResultEntry(@Nonnull Object searchResultEntry)
  {
    mSearchResultEntry = (com.unboundid.ldap.sdk.SearchResultEntry)searchResultEntry;
  }

  public ZalSearchResultEntry(String dn, String[] attributes) {
    Attribute[] attrs = new Attribute[attributes.length];
    for (int i = 0 ; i < attributes.length; i++)
    {
      attrs[i] = new Attribute(attributes[i]);
    }
    mSearchResultEntry = new com.unboundid.ldap.sdk.SearchResultEntry(dn,attrs);
  }

  public String getDn()
  {
    return mSearchResultEntry.getDN();
  }

  public List<ZalAttribute> getAttributes()
  {
    List<ZalAttribute> list = new ArrayList<>();
    for( Attribute it : mSearchResultEntry.getAttributes() )
    {
      ZalAttribute zalAttribute = new ZalAttribute(
        it.getName(),
        it.getValues()
      );
      list.add(zalAttribute);
    }
    return list;
  }

  protected <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mSearchResultEntry);
  }
}
