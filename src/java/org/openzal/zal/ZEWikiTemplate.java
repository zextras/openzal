/*
 * ZAL - An abstraction layer for Zimbra.
 * Copyright (C) 2014 ZeXtras S.r.l.
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

import com.zimbra.cs.mailbox.MailItem;
import org.openzal.zal.exceptions.ExceptionWrapper;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.wiki.WikiTemplate;

import java.io.IOException;

public class ZEWikiTemplate implements Comparable<ZEWikiTemplate>
{
  private final com.zimbra.cs.wiki.WikiTemplate mWikiTemplate;

  public ZEWikiTemplate(String item, String id, String key, String name)
  {
    mWikiTemplate = new com.zimbra.cs.wiki.WikiTemplate(item, id, key, name);
  }

  @Override
  public int compareTo(ZEWikiTemplate o)
  {
    return mWikiTemplate.compareTo(o.toZimbra(WikiTemplate.class));
  }

  public static class ZEContext
  {
    private final com.zimbra.cs.wiki.WikiTemplate.Context mContext;

    public ZEContext(ZEWikiPage.ZEWikiContext wikiPageContext, ZEItem item, ZEWikiTemplate wikiTemplate)
    {
      mContext = new com.zimbra.cs.wiki.WikiTemplate.Context(
        wikiPageContext.toZimbra(),
        item.toZimbra(MailItem.class),
        wikiTemplate.toZimbra(WikiTemplate.class)
      );
    }

    protected <T> T toZimbra(Class<T> cls)
    {
      return cls.cast(mContext);
    }
  }

  protected <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mWikiTemplate);
  }

  public String toString(ZEContext context) throws IOException
  {
    try
    {
      return mWikiTemplate.toString(context.toZimbra(WikiTemplate.Context.class));
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }
}
