package org.openzal.zal.extension;
import com.zimbra.cs.mailbox.ZimbraSimulator;
import org.junit.Rule;
import org.junit.Test;

public class ZimbraTest
{
  @Rule
  public ZimbraSimulator mZimbraSimulator = new ZimbraSimulator();

  @Test
  public void reflection_initialization()
  {
    Zimbra zimbra = new Zimbra();
  }
}