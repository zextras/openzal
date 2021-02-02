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

package org.openzal.zal.lib;

import org.openzal.zal.Connection;
import org.openzal.zal.exceptions.*;
import org.openzal.zal.exceptions.ZimbraException;
import com.zimbra.cs.db.DbPool;
import javax.annotation.Nonnull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ZimbraConnectionWrapper implements Connection
{
  private DbPool.DbConnection mConnection;

  public ZimbraConnectionWrapper(@Nonnull Object connection)
  {
    if ( connection == null )
    {
      throw new NullPointerException();
    }
    mConnection = (DbPool.DbConnection)connection;
  }

  @Override
  public PreparedStatement prepareStatement(String sql)
    throws SQLException
  {
    return mConnection.prepareStatement(sql);
  }

  @Override
  public void close()
    throws ZimbraException
  {
    try
    {
      mConnection.close();
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public <T> T toZimbra(@Nonnull Class<T> cls)
  {
    return cls.cast(mConnection);
  }

  @Override
  public void commit()
    throws ZimbraException
  {
    try
    {
      mConnection.commit();
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public void rollback() throws ZimbraException
  {
    try
    {
      mConnection.rollback();
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public void setTransactionIsolation(int transactionRepeatableRead) throws org.openzal.zal.exceptions.ZimbraException
  {
    try
    {
      mConnection.setTransactionIsolation(transactionRepeatableRead);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public void closeResults(ResultSet resultSet)
  {
    try
    {
      DbPool.closeResults(resultSet);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      //
    }
  }

  @Override
  public void closeStatement(Statement statement)
  {
    try
    {
      DbPool.closeStatement(statement);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      //
    }
  }

  @Override
  public java.sql.Connection getConnection()
  {
    return mConnection.getConnection();
  }

  @Override
  public void disableForeignKeyConstraints()
  {
    try
    {
      mConnection.disableForeignKeyConstraints();
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      //
    }
  }

  @Override
  public void enableForeignKeyConstraints()
  {
    try
    {
      mConnection.enableForeignKeyConstraints();
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      //
    }
  }

}
