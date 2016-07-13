/*
 * ZAL - The abstraction layer for Zimbra.
 * Copyright (C) 2016 ZeXtras S.r.l.
 *
 * This file is part of ZAL.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, version 2 of
 * the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ZAL. If not, see <http://www.gnu.org/licenses/>.
 */

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
    boolean isTheSame = mDomain.getId().equals(entry.getId());
    boolean isAlias = entry.isAliasDomain() && mDomain.getId().equals(entry.getDomainAliasTargetId());
    if (isTheSame || isAlias)
    {
      mAliases.add(entry);
    }
  }
}
