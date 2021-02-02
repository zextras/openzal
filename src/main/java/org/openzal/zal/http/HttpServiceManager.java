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

package org.openzal.zal.http;

import com.zimbra.cs.extension.ExtensionDispatcherServlet;
import com.zimbra.cs.extension.ZimbraExtension;
import javax.annotation.Nonnull;

public class HttpServiceManager
{
  static class FakeZimbraExtension implements ZimbraExtension
  {
    private final String mPath;

    public FakeZimbraExtension(@Nonnull String path)
    {
      mPath = path.replaceFirst("/","");
    }

    @Override
    public String getName()
    {
      return mPath;
    }

    @Override
    public void init()
    { }

    @Override
    public void destroy()
    { }
  }

  public void registerHandler(@Nonnull HttpHandler httpHandler )
  {
    try
    {
      FakeZimbraExtension fakeZimbraExtension = new FakeZimbraExtension(httpHandler.getPath());
      ExtensionDispatcherServlet.register(fakeZimbraExtension, new InternalHttpHandler(httpHandler));
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw new RuntimeException("Http handler "+httpHandler.getPath()+" already registered");
    }
  }

  public void replaceHandler(@Nonnull HttpHandler httpHandler)
  {
    unregisterHandler(httpHandler);
    registerHandler(httpHandler);
  }

  public void unregisterHandler(@Nonnull HttpHandler httpHandler )
  {
    FakeZimbraExtension fakeZimbraExtension = new FakeZimbraExtension(httpHandler.getPath());
    ExtensionDispatcherServlet.unregister(fakeZimbraExtension);
  }
}
