package com.zimbra.cs.mailbox;

import org.openzal.zal.extension.Zimbra;

public interface ZimbraSimulatorExtension {

  void setup(Zimbra zimbra);
  void cleanup();

}
