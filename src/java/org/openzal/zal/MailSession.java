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

import javax.annotation.Nonnull;
import org.openzal.zal.exceptions.ExceptionWrapper;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.util.JMSession;
import javax.annotation.Nullable;

import javax.mail.MessagingException;
import javax.mail.Session;
import java.util.List;

public class MailSession
{
  @Nonnull
  public static Session getSmtpSession(@Nullable Account account) throws MessagingException
  {
    if (account != null)
    {
      return JMSession.getSmtpSession(account.toZimbra(com.zimbra.cs.account.Account.class));
    }
    else
    {
      return JMSession.getSmtpSession((com.zimbra.cs.account.Account) null);
    }
  }

  @Nonnull
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
        return JMSession.getSmtpHosts(domain.toZimbra(com.zimbra.cs.account.Domain.class));
      }
      else
      {
        return JMSession.getSmtpHosts(null);
      }
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

}
