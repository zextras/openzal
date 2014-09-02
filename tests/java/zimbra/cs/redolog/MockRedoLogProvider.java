package org.openzal.zal.redolog;

/**
 * Mock {@link RedoLogProvider} for unit test.
 *
 * @author ysasaki
 *
 * Zimbra Collaboration Suite Server
 */

import java.io.File;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.redolog.RedoLogManager;
import com.zimbra.cs.redolog.RedoLogProvider;

public class MockRedoLogProvider extends RedoLogProvider
{

  private final ZERedoLogManager mRedoLogManager;

  public MockRedoLogProvider()
  {
    mRedoLogManager = new ZERedoLogManager(new File("build/test/redo/redo.log"), new File("build/test/redo"), false);
  }

  public boolean isMaster()
  {
    return true;
  }

  public boolean isSlave()
  {
    return false;
  }

  public void startup() throws ServiceException {
  }

  public void shutdown() throws ServiceException {
  }

  public void initRedoLogManager() {
  }

  @Override
  public RedoLogManager getRedoLogManager()
  {
    return mRedoLogManager.toZimbra(RedoLogManager.class);
  }

}
