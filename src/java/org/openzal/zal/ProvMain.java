package org.openzal.zal;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.ProvUtil;
import java.io.IOException;

public class ProvMain {
  public static void main(String[] args) throws ServiceException, IOException {
    ProvUtil.main(args);
  }
}
