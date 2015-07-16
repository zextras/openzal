/*
 * ZAL - The abstraction layer for Zimbra.
 * Copyright (C) 2015 ZeXtras S.r.l.
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

import org.jetbrains.annotations.NotNull;
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
  @NotNull
  public static Session getSmtpSession(@Nullable Account account) throws MessagingException
  {
    if (account != null)
    {
      return JMSession.getSmtpSession(account.toZimbra(com.zimbra.cs.account.Account.class));
    }
    else
    {
      return JMSession.getSmtpSession(null);
    }
  }

  @NotNull
  public static Session getSession()
  {
    try
    {
      return JMSession.getSession();
    }
    catch (Exception ex)
    {
      throw new RuntimeException(ex);
    }
  }

  public static List<String> getSmtpHosts(@Nullable Domain domain)
  {
    try
    {
      if (domain != null)
      {
        /* $if ZimbraVersion >= 8.0.0 $ */
        return JMSession.getSmtpHosts(domain.toZimbra(com.zimbra.cs.account.Domain.class));
        /* $else $
        return new ArrayList<String>(JMSession.getSmtpHosts(domain.toZimbra(com.zimbra.cs.account.Domain.class)));
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
