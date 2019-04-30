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

import org.openzal.zal.exceptions.ExceptionWrapper;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.gal.GalImport;
import javax.annotation.Nonnull;

public class DataImport
{
  @Nonnull private final DataSource.DataImport mDataImport;

  protected DataImport(@Nonnull Object dataImport)
  {
    if (dataImport == null)
    {
      throw new NullPointerException();
    }
    mDataImport = (DataSource.DataImport) dataImport;
  }

  public boolean isGalImport()
  {
    return mDataImport instanceof GalImport;
  }

  public void importGal(int fid, boolean fullSync, boolean force)
  {
    if (!isGalImport())
    {
      throw new RuntimeException();
    }
    try
    {
      ((GalImport) mDataImport).importGal(fid, fullSync, force);
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }
}
