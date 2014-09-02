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

import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Domain;
import org.openzal.zal.exceptions.ExceptionWrapper;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.util.JMSession;
import org.jetbrains.annotations.Nullable;

import javax.mail.MessagingException;
import javax.mail.Session;
import java.util.List;
/* $if ZimbraVersion < 8.0.0 $
import java.util.ArrayList;
import java.util.Set;
/* $endif $ */

public class MailSession
{
  public static Session getSmtpSession(@Nullable ZEAccount account) throws MessagingException
  {
    if (account != null)
    {
      return JMSession.getSmtpSession(account.toZimbra(Account.class));
    }
    else
    {
      return JMSession.getSmtpSession(null);
    }
  }

  public static Session getSession()
    /* $if ZimbraVersion < 8.0.0 $
    throws MessagingException
    /* $endif $ */
  {
    return JMSession.getSession();
  }

  public static List<String> getSmtpHosts(@Nullable ZEDomain domain)
  {
    try
    {
      if (domain != null)
      {
        /* $if ZimbraVersion >= 8.0.0 $ */
        return JMSession.getSmtpHosts(domain.toZimbra(Domain.class));
        /* $else $
        return new ArrayList<String>(JMSession.getSmtpHosts(domain.toZimbra(Domain.class)));
        /* $endif $ */
      }
      else
      {
        /* $if ZimbraVersion >= 8.0.0 $ */
        return JMSession.getSmtpHosts(null);
        /* $else $
        return new ArrayList<String>(JMSession.getSmtpHosts(null));
        /* $endif $ */
      }
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

}
