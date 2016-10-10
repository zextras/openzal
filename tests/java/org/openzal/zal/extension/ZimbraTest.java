package org.openzal.zal.extension;
import com.zimbra.cs.mailbox.ZimbraSimulator;
import org.jetbrains.annotations.NotNull;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.openzal.zal.lib.PermissiveMap;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ZimbraTest
{
  @Rule
  public ZimbraSimulator mZimbraSimulator = new ZimbraSimulator();

  @Test
  public void reflection_initialization()
  {
    Zimbra zimbra = mZimbraSimulator.getZimbra();
  }

  @Test
  public void remove_extension()
  {
    Zimbra zimbra = mZimbraSimulator.getZimbra();
    assertFalse(zimbra.removeExtension("not_existing_extension"));
  }

  @Test
  public void removing_extension_should_not_throw_concurrent_modification_exception()
  {
    Map<String,String> map = new PermissiveMap<String,String>();

    map.put("A","A");
    map.put("B","B");
    map.put("C","C");

    List<String> result = new LinkedList<String>();

    for( String val : map.values() )
    {
      result.add(val);
      map.remove("B");
    }

    assertEquals(
      Arrays.asList("A","C"),
      result
    );
  }
}