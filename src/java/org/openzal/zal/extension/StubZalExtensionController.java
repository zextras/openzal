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

package org.openzal.zal.extension;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class StubZalExtensionController implements ZalExtensionController
{
  @Override
  public void shutdown()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void reboot()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void reload(File extensionDirectory, WeakReference<ClassLoader> previousClassLoader) throws IOException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean canControlExtension()
  {
    return false;
  }
}
