/*
 * ZAL - The abstraction layer for Zimbra.
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

import org.jetbrains.annotations.Nullable;
import org.openzal.zal.calendar.ICalendarTimezone;
import org.openzal.zal.calendar.Invite;
import org.openzal.zal.calendar.WinSystemTime;
import org.openzal.zal.exceptions.ExceptionWrapper;
/* $if ZimbraVersion >= 8.0.0 $ */
import com.zimbra.common.calendar.ICalTimeZone;
import com.zimbra.common.calendar.WellKnownTimeZones;
/* $else $
import com.zimbra.cs.mailbox.calendar.WellKnownTimeZones;
import com.zimbra.cs.mailbox.calendar.ZCalendar;
import com.zimbra.cs.mailbox.calendar.ICalTimeZone;
/* $endif $ */
/* $if ZimbraVersion <= 7.0.0 $
import com.zimbra.cs.mailbox.Tag;
/* $endif $ */
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.BEncoding;
import com.zimbra.common.util.ByteUtil;
import com.zimbra.common.util.FileUtil;
import com.zimbra.common.util.StringUtil;
import com.zimbra.cs.httpclient.URLUtil;
import com.zimbra.cs.mailbox.MessageCache;
import com.zimbra.cs.mailbox.calendar.IcalXmlStrMap;
import com.zimbra.cs.mailbox.calendar.WindowsSystemTime;
import com.zimbra.cs.util.JMSession;
import com.zimbra.cs.zimlet.ZimletException;
import com.zimbra.cs.zimlet.ZimletUtil;

import javax.mail.MessagingException;
import javax.mail.Session;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Utils
{
  public static String FBTYPE_BUSY = IcalXmlStrMap.FBTYPE_BUSY;

  public static String exceptionToString(Throwable e)
  {
    StringBuilder sb = new StringBuilder(128);
    StackTraceElement elements[] = e.getStackTrace();

    sb.append(e.toString());
    sb.append("\n");

    for (int n = 0; n < elements.length; ++n)
    {
      sb.append("        at ");
      sb.append(elements[n].getClassName());
      sb.append(".");
      sb.append(elements[n].getMethodName());
      sb.append(" ( ");
      sb.append(elements[n].getFileName());
      sb.append(":");
      sb.append(elements[n].getLineNumber());
      sb.append(" )");

      if (elements[n].isNativeMethod())
      {
        sb.append(" [native]");
      }

      sb.append("\n");
    }

    Throwable cause = e.getCause();
    if (cause != null)
    {
      sb.append("Caused by: ");
      sb.append(exceptionToString(cause));
    }

    return sb.toString();
  }

  public static Map<String, Object> decode(String attrs)
  {
    try
    {
      return (Map<String, Object>) BEncoding.decode(attrs);
    }
    catch (BEncoding.BEncodingException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public static String encode(Map<String, Object> attrs)
  {
    return BEncoding.encode(attrs);
  }

  @Nullable
  public static List<Zimlet> orderZimletsByPriority(List<Zimlet> zimlets)
  {
    List<com.zimbra.cs.account.Zimlet> zimbraZimletList =
      new ArrayList<com.zimbra.cs.account.Zimlet>(zimlets.size());

    for (Zimlet zimlet : zimlets)
    {
      zimbraZimletList.add(zimlet.toZimbra());
    }

    List<Zimlet> orderedZimletList = new ArrayList<Zimlet>(zimlets.size());
    for (com.zimbra.cs.account.Zimlet zimlet : ZimletUtil.orderZimletsByPriority(zimbraZimletList))
    {
      orderedZimletList.add(new Zimlet(zimlet));
    }

    return orderedZimletList;
  }

  public static String getPublicURLForDomain(Server server, Domain domain, String path)
  {
    try
    {
      return URLUtil.getPublicURLForDomain(
        server.toZimbra(com.zimbra.cs.account.Server.class),
        domain.toZimbra(com.zimbra.cs.account.Domain.class),
        path,
        true
      );
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public static String bitmaskToTags(long tagBitmask)
  {
    /* $if ZimbraVersion <= 7.0.0 $
    return Tag.bitmaskToTags(tagBitmask);
    /* $else $ */
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public static String encodeFSSafeBase64(byte[] data)
  {
    return ByteUtil.encodeFSSafeBase64(data);
  }

  public static void addToMultiMap(Map<String, Object> result, String name, String value)
  {
    StringUtil.addToMultiMap(result, name, value);
  }

  public static void deployZimlet(Provisioning provisioning, ZimletFile zimlet) throws IOException
  {
    try
    {
/* $if ZimbraVersion < 6.0.8 $
      ZimletUtil.deployZimletBySoap(zimlet.getZimletPath(), null, null, null, null);
   $endif$ */

/* $if ZimbraVersion >= 6.0.8 && ZimbraVersion <= 8.0.7$
      ZimletUtil.deployZimletBySoap(zimlet.getZimletPath(), null, null, true);
   $endif$ */

/* $if ZimbraVersion >= 8.5.0 $ */
      provisioning.flushCache(CacheEntryType.zimlet, null);
      ZimletUtil.deployZimletLocally(zimlet.toZimbra(com.zimbra.cs.zimlet.ZimletFile.class));

      ZimletUtil.ZimletSoapUtil zimletSoapUtil = new ZimletUtil.ZimletSoapUtil();
      for (Server server : provisioning.getAllServers())
      {
          if (!server.toZimbra(com.zimbra.cs.account.Server.class).isLocalServer()) {
            zimletSoapUtil.deployZimletRemotely(
              server.toZimbra(com.zimbra.cs.account.Server.class),
              zimlet.getName(),
              zimlet.getZimletContent(),
              null,
              true
            );
          }
      }
/* $endif$ */
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
/* $if ZimbraVersion >= 8.5.0 $ */
    catch (ZimletException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
/* $endif$ */
  }

  public static boolean isGzipped(File file) throws IOException
  {
    return FileUtil.isGzipped(file);
  }

  public static byte[] getContent(InputStream stream, int sizeHint) throws IOException
  {
    return ByteUtil.getContent(stream, sizeHint);
  }

  public static long copy(InputStream in, boolean closeIn, OutputStream out, boolean closeOut) throws IOException
  {
    return ByteUtil.copy(in, closeIn, out, closeOut);
  }

  public static void purgeMessageCache(String digest)
  {
    MessageCache.purge(digest);
  }

  public static Session getSmtpSession()
  {
    try
    {
      return JMSession.getSmtpSession(null);
    }
    catch (MessagingException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public static void setDefaultAlarm(Invite invite, Account account)
  {
    try
    {
      com.zimbra.cs.mailbox.calendar.Invite.setDefaultAlarm(invite.toZimbra(com.zimbra.cs.mailbox.calendar.Invite.class),
                                                            account.toZimbra(com.zimbra.cs.account.Account.class));
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public static WinSystemTime windowsSystemTimeFromSimpleOnset(ICalendarTimezone.SimpleOnset simpleOnset)
  {
    ICalTimeZone.SimpleOnset zimbraSimpleOnSet;
    if (simpleOnset == null)
    {
      zimbraSimpleOnSet = null;
    }
    else
    {
      zimbraSimpleOnSet = simpleOnset.toZimbra(ICalTimeZone.SimpleOnset.class);
    }
    WindowsSystemTime windowsSystemTime  =
      WindowsSystemTime.fromSimpleOnset(zimbraSimpleOnSet);
    if ( windowsSystemTime == null )
    {
      return null;
    }

    return new WinSystemTime(WindowsSystemTime.fromSimpleOnset(zimbraSimpleOnSet));
  }

  public static void loadTimeZonesFromFile(File tzFile) throws IOException
  {
    try
    {
      WellKnownTimeZones.loadFromFile(tzFile);
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }
}
