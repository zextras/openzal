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

package org.openzal.zal.log;

import com.zimbra.common.util.Log;
import javax.annotation.Nonnull;

import java.util.Map;

public class ZimbraLog
{
  public static final ZimbraLogSection extensions = new ZimbraLogSection(com.zimbra.common.util.ZimbraLog.extensions);
  public static final ZimbraLogSection sync = new ZimbraLogSection(com.zimbra.common.util.ZimbraLog.sync);
  public static final ZimbraLogSection mailbox = new ZimbraLogSection(com.zimbra.common.util.ZimbraLog.mailbox);
  public static final ZimbraLogSection security = new ZimbraLogSection(com.zimbra.common.util.ZimbraLog.security);
  public static final ZimbraLogSection misc = new ZimbraLogSection(com.zimbra.common.util.ZimbraLog.misc);
  public static final ZimbraLogSection backup = new ZimbraLogSection(com.zimbra.common.util.ZimbraLog.backup);
  public static final ZimbraLogSection mobile = new ZimbraLogSection(com.zimbra.common.util.ZimbraLog.extensions);

  public static class ZimbraLogSection
  {
    private Log mLog;

    public ZimbraLogSection(Log log)
    {
      mLog = log;
    }

    public void trace(String msg)
    {
      mLog.info(msg);
    }

    public void trace(String msg, Object ... content)
    {
      mLog.info(msg, content);
    }

    public void debug(String msg)
    {
      mLog.info(msg);
    }

    public void debug(String msg, Object ... content)
    {
      mLog.info(msg, content);
    }

    public void info(String msg)
    {
      mLog.info(msg);
    }

    public void info(String msg, Object ... content)
    {
      mLog.info(msg, content);
    }

    public void warn( Throwable e )
    {
      mLog.warn(e);
    }

    public void warn( String msg )
    {
      mLog.warn(msg);
    }

    public void warn( String msg , Object ... content )
    {
      mLog.warn(msg, content);
    }

    public void warn( String msg , Throwable e )
    {
      mLog.warn(msg, e);
    }

    public void error( String msg )
    {
      mLog.error(msg);
    }

    public void error( String msg, Throwable e )
    {
      mLog.error(msg, e);
    }

    public void fatal( String msg )
    {
      mLog.fatal(msg);
    }

    public boolean isDebugEnabled()
    {
      return mLog.isDebugEnabled();
    }
  }

  public static void clearContext()
  {
    com.zimbra.common.util.ZimbraLog.clearContext();
  }

  public static void addToContext( String key, String value )
  {
    com.zimbra.common.util.ZimbraLog.addToContext(key, value);
  }

  public static void addAccountNameToContext(String username)
  {
    com.zimbra.common.util.ZimbraLog.addAccountNameToContext(username);
  }

  public static void addIpToContext(String remoteIp)
  {
    com.zimbra.common.util.ZimbraLog.addIpToContext(remoteIp);
  }

  public static void addUserAgentToContext(String userAgent)
  {
    com.zimbra.common.util.ZimbraLog.addUserAgentToContext(userAgent);
  }

  public static String encodeAttrs(@Nonnull Map<String, String> attrs)
  {
    String[] attrsList = new String[attrs.size()*2];
    int i = 0;
    for (String key : attrs.keySet())
    {
      attrsList[i] = key;
      i++;
      attrsList[i] = attrs.get(key);
      i++;
    }
    return com.zimbra.common.util.ZimbraLog.encodeAttrs(attrsList);
  }
}
