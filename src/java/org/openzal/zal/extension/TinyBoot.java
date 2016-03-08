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

import org.openzal.zal.tools.VersionChooser;

import java.io.File;

public class TinyBoot
{
  private final static String sZalEntrypointName = "org.openzal.zal.extension.ZalEntrypointImpl";
  private final VersionChooser mVersionChooser;
  private final File           mExtensionPathFile;

  public TinyBoot(File extensionPathFile)
  {
    mExtensionPathFile = extensionPathFile;
    mVersionChooser = new VersionChooser();
  }

  public ZalEntrypoint createZalEntryPoint(File extensionDirectory, ZalExtensionController zalExtensionController) throws Exception
  {
    BootstrapClassLoader bootstrapClassLoader = mVersionChooser.getBootstrapClassLoader(extensionDirectory);

    Class<ZalEntrypoint> zalEntrypoint = (Class<ZalEntrypoint>) bootstrapClassLoader.loadClass(sZalEntrypointName);

    ZalEntrypoint entrypoint = zalEntrypoint.newInstance();
    entrypoint.provideCustomClassLoader(bootstrapClassLoader);
    entrypoint.provideCustomExtensionController(zalExtensionController);
    entrypoint.provideCustomExtensionDirectory(extensionDirectory);

    return entrypoint;
  }

  public ZalEntrypoint createZalEntryPoint(ZalExtensionController controller) throws Exception
  {
    return createZalEntryPoint(mVersionChooser.getBestVersionDirectory(mExtensionPathFile), controller);
  }
}
