package org.openzal.zal.extension;
import com.zimbra.cs.mailbox.ZimbraSimulator;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class ZimbraTest
{
  @Rule
  public ZimbraSimulator mZimbraSimulator = new ZimbraSimulator();

  @Test
  public void reflection_initialization()
  {
    Zimbra zimbra = new Zimbra();
  }

  @Test
  public void remove_extension()
  {
    Zimbra zimbra = new Zimbra();
    assertFalse(zimbra.removeExtension("not_existing_extension"));
  }
}