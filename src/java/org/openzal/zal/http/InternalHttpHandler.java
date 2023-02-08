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

import com.zimbra.cs.extension.ExtensionHttpHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

class InternalHttpHandler extends ExtensionHttpHandler
{
  private final HttpHandler mHttpHandler;

  public InternalHttpHandler(
    HttpHandler httpHandler
  )
  {
    mHttpHandler = httpHandler;
  }


  @Override
  public void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException
  {
    mHttpHandler.doOptions(req, resp);
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException
  {
    mHttpHandler.doGet(req, resp);
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException
  {
    mHttpHandler.doPost(req, resp);
  }

  @Override
  public void doPut(HttpServletRequest req, HttpServletResponse resp)
    throws IOException, ServletException {
    mHttpHandler.doPut(req, resp);
  }

  @Override
  public void doDelete(HttpServletRequest req, HttpServletResponse resp)
    throws IOException, ServletException {
    mHttpHandler.doDelete(req, resp);
  }

  @Override
  public void doPatch(HttpServletRequest req, HttpServletResponse resp)
    throws IOException, ServletException {
    mHttpHandler.doPatch(req, resp);
  }
}
