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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openzal.zal.calendar.ICalendarTimezone;
import org.openzal.zal.calendar.Invite;
import org.openzal.zal.calendar.WinSystemTime;
import org.openzal.zal.exceptions.ExceptionWrapper;
import com.zimbra.common.calendar.ICalTimeZone;
import com.zimbra.common.calendar.WellKnownTimeZones;
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
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
      sb.append(" (");
      sb.append(elements[n].getFileName());
      sb.append(":");
      sb.append(elements[n].getLineNumber());
      sb.append(")");

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

  public static String getPublicURLForDomain(
    @NotNull Server server,
    @Nullable Domain domain,
    @NotNull String path
  )
  {
    try
    {
      return URLUtil.getPublicURLForDomain(
        server.toZimbra(com.zimbra.cs.account.Server.class),
        domain != null ? domain.toZimbra() : null,
        path,
        true
      );
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public static String encodeFSSafeBase64(byte[] data)
  {
    return ByteUtil.encodeFSSafeBase64(data);
  }

  public static byte[] decodeFSSafeBase64(String  data)
  {
    return ByteUtil.decodeFSSafeBase64(data);
  }

  public static void addToMultiMap(Map<String, Object> result, String name, String value)
  {
    StringUtil.addToMultiMap(result, name, value);
  }

  public static void deployZimlet(Provisioning provisioning, ZimletFile zimlet) throws IOException
  {
    try
    {
      /* $if ZimbraVersion <= 8.0.9$
      ZimletUtil.deployZimletBySoap(zimlet.getZimletPath(), null, null, true);
      /* $endif$ */

      /* $if ZimbraVersion >= 8.5.0 $ */
      provisioning.flushCache(CacheEntryType.zimlet, null);
      ZimletUtil.deployZimletLocally(zimlet.toZimbra(com.zimbra.cs.zimlet.ZimletFile.class));

      FileInputStream in = new FileInputStream(new File(zimlet.getZimletPath()));
      ZimletFile rawZimlet = new ZimletFile(zimlet.getName(), in);

      ZimletUtil.ZimletSoapUtil zimletSoapUtil = new ZimletUtil.ZimletSoapUtil();
      for (Server server : provisioning.getAllServers())
      {
        if (!server.toZimbra(com.zimbra.cs.account.Server.class).isLocalServer())
        {
          if( !server.hasMailboxService() )
          {
            continue;
          }

          zimletSoapUtil.deployZimletRemotely(
              server.toZimbra(com.zimbra.cs.account.Server.class),
              rawZimlet.getName(),
              rawZimlet.getZimletContent(),
              null,
              true
            );
          }
      }
      // In Zimbra 8.5 the Zimlet cache may not be consistent. Better flush it again after the deploy.
      provisioning.flushCache(CacheEntryType.zimlet, null);
      /* $endif$ */
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    /* $if ZimbraVersion >= 8.5.0 $ */
    catch (ZimletException e)
    {
      provisioning.flushCache(CacheEntryType.zimlet, null);
      throw ExceptionWrapper.wrap(e);
    }
    /* $endif$ */
  }

  public static boolean isGzipped(File file) throws IOException
  {
    try
    {
      return FileUtil.isGzipped(file);
    }
    catch (EOFException eof)
    {
      return false;
    }
  }

  public static boolean isGzipped(InputStream inputStream) throws IOException
  {
    try
    {
      return ByteUtil.isGzipped(inputStream);
    }
    catch (EOFException eof)
    {
      return false;
    }
  }

  public static boolean isGzipped(byte[] data) throws IOException
  {
    return ByteUtil.isGzipped(data);
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

  public static String getEmailNamePart(String address)
  {
    if (address.contains("@"))
    {
      String[] parts = address.split("@");

      if (parts.length == 2)
      {
        return address.split("@")[0];
      }
    }

    throw new RuntimeException("Invalid mail address " + address);
  }

  public static String computeDigest(InputStream inputStream) throws IOException
  {
    try
    {
      MessageDigest digest= MessageDigest.getInstance("SHA-256");
      byte[] buffer = new byte[1024];
      int read;
      while ( (read = inputStream.read(buffer)) >= 0)
      {
        digest.update(buffer, 0, read);
      }
      return encodeFSSafeBase64(digest.digest());
    }
    catch (NoSuchAlgorithmException e)
    {
      throw new IOException(e);
    }
  }

  public static boolean isValidInternetAddress(String name)
  {
    try
    {
      new InternetAddress(name).validate();
      return true;
    }
    catch (AddressException e)
    {
      return false;
    }
  }

  public static String currentStackTrace()
  {
    StringBuilder sb = new StringBuilder(128);
    StackTraceElement elements[] = Thread.currentThread().getStackTrace();

    sb.append( "Thread Stack:\n");

    for( int n=2; n < elements.length; ++n ){
      sb.append( "        at ");
      sb.append( elements[n].getClassName() );
      sb.append( "." );
      sb.append( elements[n].getMethodName() );
      sb.append( " ( ");
      sb.append( elements[n].getFileName()  );
      sb.append( ":");
      sb.append( elements[n].getLineNumber()  );
      sb.append( " )");

      if( elements[n].isNativeMethod() ) {
        sb.append(" [native]");
      }

      sb.append("\n");
    }

    return sb.toString();
  }
}
