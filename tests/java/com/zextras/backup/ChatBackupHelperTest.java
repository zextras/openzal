package com.zextras.backup;


import com.zextras.lib.json.JSONArray;
import com.zextras.lib.json.JSONObject;
import com.zextras.newchat.Relationship;
import com.zextras.newchat.User;
import org.openzal.zal.ZEAccount;
import org.openzal.zal.ZEProvisioningSimulator;
import com.zextras.newchat.address.SpecificAddress;
import com.zextras.newchat.db.providers.UserProvider;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class ChatBackupHelperTest
{
  private static final int FIRST = 0;
  private UserProvider mUserprovider;
  private String NO_RELATIONSHIPS_ACCOUNT_MAIL = "norelationships@example.com";
  private String ONE_RELATIONSHIPS_ACCOUNT_MAIL = "onerelationships@example.com";
  private String TWO_RELATIONSHIPS_ACCOUNT_MAIL = "tworelationships@example.com";
  private ZEProvisioningSimulator mProvisioning;
  private Relationship mFirstRelationship;
  private Relationship mSecondRelationship;
  private User mUserWithOneRelationship;

  @Before
  public void setup()
      throws Exception
  {
    mProvisioning = new ZEProvisioningSimulator();

    SpecificAddress noRelationshipsUserAddress = new SpecificAddress(NO_RELATIONSHIPS_ACCOUNT_MAIL);
    User userWithNoRelationships = Mockito.mock(User.class);
    Mockito.when(userWithNoRelationships.getAddress()).thenReturn(noRelationshipsUserAddress);
    Mockito.when(userWithNoRelationships.getRelationships()).thenReturn(new ArrayList< Relationship>(0));

    SpecificAddress twoRelationshipsUserAddress = new SpecificAddress(TWO_RELATIONSHIPS_ACCOUNT_MAIL);
    User userWithTwoRelationships = Mockito.mock(User.class);
    mFirstRelationship = new Relationship(0,
                                          new SpecificAddress("firstfriend@example.com"),
                                          Relationship.RelationshipType.ACCEPTED,
                                          "First Friend", "zimbra"
    );
    mSecondRelationship = new Relationship(0,
                                           new SpecificAddress("secondfriend@example.com"),
                                           Relationship.RelationshipType.BLOCKED,
                                           "Second Friend", "zimbra"
    );

    ArrayList<Relationship> relationshipArrayList = new ArrayList<Relationship>(2);
    relationshipArrayList.add(mFirstRelationship);
    relationshipArrayList.add(mSecondRelationship);
    Mockito.when(userWithTwoRelationships.getRelationships()).thenReturn(relationshipArrayList);

    SpecificAddress oneRelationshipUserAddress = new SpecificAddress(ONE_RELATIONSHIPS_ACCOUNT_MAIL);
    mUserWithOneRelationship = Mockito.mock(User.class);

    mUserprovider = Mockito.mock(UserProvider.class);
    Mockito.when(mUserprovider.getUser(noRelationshipsUserAddress)).thenReturn(userWithNoRelationships);
    Mockito.when(mUserprovider.getUser(twoRelationshipsUserAddress)).thenReturn(userWithTwoRelationships);
    Mockito.when(mUserprovider.getUser(oneRelationshipUserAddress)).thenReturn(mUserWithOneRelationship);

  }

  @Test
  public void test_dumpToJson_noRelationshipsUser_emptyJsonArray() throws Exception {
    ZEAccount noRelationshipsAccount = mProvisioning.createFakeAccount(NO_RELATIONSHIPS_ACCOUNT_MAIL);

    ChatBackupHelper chatBackupHelper = new ChatBackupHelperImpl(mUserprovider);
    JSONObject result = chatBackupHelper.dumpToJson(noRelationshipsAccount);

    assertTrue("Resulting JSON should have a relationships key.",
               result.has(ChatBackupHelperImpl.RELATIONSHIP_KEY));

    JSONArray relationships = result.getJSONArray(ChatBackupHelperImpl.RELATIONSHIP_KEY);
    assertTrue("An account with no relationships should produce an empty JSONArray of relationships.",
               relationships.isEmpty());
  }

  @Test
  public void test_dumpToJson_twoRelationshipsUser_jsonArrayShouldBePopulated() throws Exception {
    ZEAccount twoRelationshipsAccount = mProvisioning.createFakeAccount(TWO_RELATIONSHIPS_ACCOUNT_MAIL);

    ChatBackupHelper chatBackupHelper = new ChatBackupHelperImpl(mUserprovider);
    JSONObject result = chatBackupHelper.dumpToJson(twoRelationshipsAccount);

    assertTrue("Resulting JSON should have a relationships key.",
               result.has(ChatBackupHelperImpl.RELATIONSHIP_KEY));

    JSONArray relationships = result.getJSONArray(ChatBackupHelperImpl.RELATIONSHIP_KEY);
    assertEquals("An account with two relationships should produce a JSONArray with two relationships.",
                 2, relationships.size());

    JSONObject firstRelationship = relationships.getJSONObject(FIRST);

    assertEquals("The relationship has a wrong RelationshipType.",
                 mFirstRelationship.getType().toString(),
                 firstRelationship.getString(ChatBackupHelperImpl.FIELD_TYPE));

    assertEquals("The relationship has a wrong BuddyAddress.",
                 mFirstRelationship.getBuddyAddress().toString(),
                 firstRelationship.getString(ChatBackupHelperImpl.FIELD_BUDDY_ADDRESS));

    assertEquals("The relationship has a wrong BuddyNickname.",
                 mFirstRelationship.getBuddyNickname(),
                 firstRelationship.getString(ChatBackupHelperImpl.FIELD_BUDDY_NICKNAME));
  }

  @Test
  public void test_restoreFromJSON_oneRelationshipsUser_chatUserShouldHaveRelationship()
    throws Exception
  {
    ZEAccount oneRelationshipsAccount = mProvisioning.createFakeAccount(ONE_RELATIONSHIPS_ACCOUNT_MAIL);
    SpecificAddress firstAddress = new SpecificAddress("first@example.com");
    String firstNickname = "First";

    JSONObject firstRelationship = new JSONObject()
        .put(ChatBackupHelperImpl.FIELD_TYPE, Relationship.RelationshipType.ACCEPTED.toString())
        .put(ChatBackupHelperImpl.FIELD_BUDDY_ADDRESS, firstAddress)
        .put(ChatBackupHelperImpl.FIELD_BUDDY_NICKNAME, firstNickname);

    JSONArray relationshipArray = new JSONArray();
    relationshipArray.add(firstRelationship);
    JSONObject result = new JSONObject().put(ChatBackupHelperImpl.RELATIONSHIP_KEY, relationshipArray);

    ChatBackupHelper chatBackupHelper = new ChatBackupHelperImpl(mUserprovider);
    chatBackupHelper.restoreFromJson(oneRelationshipsAccount, result);

    Mockito.verify(mUserWithOneRelationship).addRelationship(firstAddress,
                                                             Relationship.RelationshipType.ACCEPTED,
                                                             firstNickname, "zimbra"
    );
  }

}
