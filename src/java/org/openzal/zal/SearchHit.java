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

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.index.ZimbraHit;
import com.zimbra.cs.mailbox.MailItem;
import java.util.Optional;
import java.util.function.Function;
import org.openzal.zal.exceptions.ExceptionWrapper;

import javax.annotation.Nonnull;

public class SearchHit
{
  @Nonnull private final ZimbraHit mZimbraHit;

  protected SearchHit(@Nonnull Object zimbraHit)
  {
    mZimbraHit = (ZimbraHit) zimbraHit;
  }

  public Optional<Item> getMailItem()
  {
    try
    {
      return Optional.ofNullable(mZimbraHit.getMailItem()).map(new Function<MailItem, Item>()
      {
        @Override
        public Item apply(MailItem mailItem)
        {
          return new Item(mailItem);
        }
      });
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public int getItemId()
  {
    try
    {
      return mZimbraHit.getItemId();
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public ZimbraItemId getZimbraItemId()
  {
    try
    {
      return new ZimbraItemId(
        mZimbraHit.getParsedItemID().getAccountId(),
        mZimbraHit.getParsedItemID().getId()
      );
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }
}
