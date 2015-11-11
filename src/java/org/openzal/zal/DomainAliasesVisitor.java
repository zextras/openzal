package org.openzal.zal;

import java.util.HashSet;
import java.util.Set;

class DomainAliasesVisitor implements SimpleVisitor<Domain>
{
  private final Domain      mDomain;
  private final Set<Domain> mAliases;

  public DomainAliasesVisitor(Domain domain)
  {
    mDomain = domain;
    mAliases = new HashSet<Domain>();
  }

  public Set<Domain> getAliases()
  {
    return mAliases;
  }

  @Override
  public void visit(Domain entry)
  {
    if (entry.isAliasDomain() && mDomain.getId().equals(entry.getDomainAliasTargetId()))
    {
      mAliases.add(entry);
    }
  }
}
