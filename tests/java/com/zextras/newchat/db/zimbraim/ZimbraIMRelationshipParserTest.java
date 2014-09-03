package com.zextras.newchat.db.zimbraim;

import com.zextras.utils.ResultSetSimulator;
import org.openzal.zal.ZEProvisioningSimulator;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;


public class ZimbraIMRelationshipParserTest
{
  private static final Map<String, Integer> mColumns = new HashMap<String, Integer>(7);

  static
  {
    mColumns.put("rosterID", 1);
    mColumns.put("username", 2);
    mColumns.put("jid", 3);
    mColumns.put("sub", 4);
    mColumns.put("ask", 5);
    mColumns.put("recv", 6);
    mColumns.put("nick", 7);
    mColumns.put("GroupName", 8);
  }

  private ResultSetSimulator mResultSet;
  private ZEProvisioningSimulator mProvisioning;

  @Before
  public void setup()
    throws Exception
  {
    mProvisioning = new ZEProvisioningSimulator();
    mProvisioning.addUser("foo@example.com");
    mProvisioning.addUser("Bazzz!", "baz@example.com");

    mResultSet = new ResultSetSimulator(
      Arrays.asList(
        Arrays.<Object>asList(
          1, "foo@example.com", "bar@example.com", 3, -1, -1, "Bar", "Baz"
        ),
        Arrays.<Object>asList(
          2, "foo@example.com", "baz@example.com", 3, -1, -1, null, "Baz"
        ),
        Arrays.<Object>asList(
          3, "baz@example.com", "foo@example.com", 3, -1, -1, null, null
        )
      ), mColumns
    );
  }

  @Test
  public void test_JiveRelationshipParser() throws Exception
  {
    ZimbraIMRelationshipParser parser = new ZimbraIMRelationshipParser(mProvisioning);

    mResultSet.next();
    ZimbraIMRelationshipInfo info = parser.readFromResultSet(mResultSet);
    assertEquals("foo@example.com", info.getUserAddress());
    assertEquals("bar@example.com", info.getBuddyAddress());
    assertEquals("Bar", info.getBuddyNickname());

    mResultSet.next();
    info = parser.readFromResultSet(mResultSet);
    assertEquals("foo@example.com", info.getUserAddress());
    assertEquals("baz@example.com", info.getBuddyAddress());
    assertEquals("Bazzz!", info.getBuddyNickname());

    mResultSet.next();
    info = parser.readFromResultSet(mResultSet);
    assertEquals("baz@example.com", info.getUserAddress());
    assertEquals("foo@example.com", info.getBuddyAddress());
    assertEquals("foo@example.com", info.getBuddyNickname());
  }
}
