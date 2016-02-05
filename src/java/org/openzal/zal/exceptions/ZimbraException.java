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

package org.openzal.zal.exceptions;

import com.zimbra.common.service.ServiceException;

public class ZimbraException extends RuntimeException
{
  protected ZimbraException(Exception exception)
  {
    super(exception);
  }

  protected ZimbraException(Throwable exception)
  {
    super(exception);
  }

  public ZimbraException(String msg)
  {
    this(ServiceException.FAILURE(msg, null));
  }

  public ZimbraException(String msg, Throwable e)
  {
    this(ServiceException.FAILURE(msg, e));
  }
}
