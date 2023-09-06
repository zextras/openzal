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

import com.zimbra.cs.account.auth.AuthContext;
import javax.annotation.Nonnull;

public class Protocol
{
  public static          Protocol clientCertificate  = new Protocol(AuthContext.Protocol.client_certificate);
  public static          Protocol httpBasic  = new Protocol(AuthContext.Protocol.http_basic);
  public static          Protocol im = new Protocol(AuthContext.Protocol.im);
  public static          Protocol imap = new Protocol(AuthContext.Protocol.imap);
  public static          Protocol pop3 = new Protocol(AuthContext.Protocol.pop3);
  public static          Protocol soap = new Protocol(AuthContext.Protocol.soap);
  public static          Protocol spnego = new Protocol(AuthContext.Protocol.spnego);
  @Nonnull public static Protocol zsync = new Protocol(AuthContext.Protocol.zsync);
  public static          Protocol test = new Protocol(AuthContext.Protocol.test);

  private final com.zimbra.cs.account.auth.AuthContext.Protocol mProtocol;

  Protocol(
    @Nonnull com.zimbra.cs.account.auth.AuthContext.Protocol protocol
  )
  {
    if (protocol == null)
    {
      throw new NullPointerException();
    }

    mProtocol = protocol;
  }

  com.zimbra.cs.account.auth.AuthContext.Protocol toZimbra()
  {
    return mProtocol;
  }

  public String toString()
  {
    return mProtocol.toString();
  }
}
