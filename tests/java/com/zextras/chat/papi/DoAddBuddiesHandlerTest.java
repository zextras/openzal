package com.zextras.chat.papi;

import com.zextras.chat.services.ChatModuleSwitches;
import com.zextras.lib.ZEPAPIContext;
import com.zextras.lib.switches.ServiceSwitch;
import com.zextras.newchat.Relationship;
import com.zextras.newchat.User;
import com.zextras.newchat.address.SpecificAddress;
import com.zextras.newchat.db.providers.UserProvider;
import org.openzal.zal.ZEProvisioningSimulator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


public class DoAddBuddiesHandlerTest
{
  private SpecificAddress         mFirstUserAddress;
  private User                    mFirstUser;
  private SpecificAddress         mSecondUserAddress;
  private User                    mSecondUser;
  private UserProvider            mUserProvider;
  private ZEProvisioningSimulator mProvisioning;
  private ChatModuleSwitches      mChatModuleSwitches;
  private ZEPAPIContext           mPapiContext;

  @Before
  public void setup()
    throws Exception
  {
    mFirstUserAddress = new SpecificAddress("first@example.com");
    mFirstUser = Mockito.mock(User.class);

    mSecondUserAddress = new SpecificAddress("second@example.com");
    mSecondUser = Mockito.mock(User.class);

    mUserProvider = Mockito.mock(UserProvider.class);
    Mockito.when(mUserProvider.getUser(mFirstUserAddress)).thenReturn(mFirstUser);
    Mockito.when(mUserProvider.getUser(mSecondUserAddress)).thenReturn(mSecondUser);

    mProvisioning = new ZEProvisioningSimulator();
    mProvisioning.addUser(mFirstUserAddress.toString());
    mProvisioning.addUser(mSecondUserAddress.toString());

    mChatModuleSwitches = Mockito.mock(ChatModuleSwitches.class);
    ServiceSwitch serviceSwitchOn = Mockito.mock(ServiceSwitch.class);
    Mockito.when(serviceSwitchOn.isOn()).thenReturn(true);
    Mockito.when(mChatModuleSwitches.getModuleSwitch()).thenReturn(serviceSwitchOn);

    mPapiContext = Mockito.mock(ZEPAPIContext.class);
    Mockito.when(mPapiContext.getParameter("user1")).thenReturn(mFirstUserAddress.toString());
    Mockito.when(mPapiContext.getParameter("user2")).thenReturn(mSecondUserAddress.toString());
    Mockito.when(mPapiContext.getParameter("user1nick", null)).thenReturn("First");
    Mockito.when(mPapiContext.getParameter("user2nick", null)).thenReturn("Second");
  }

  @Test
  public void addNewFriendship_noPreviousRelationship_inviteRelationshipCreated()
    throws Exception
  {
    Mockito.when(mPapiContext.getBoolean("user1accepted", true)).thenReturn(false);
    Mockito.when(mPapiContext.getBoolean("user2accepted", true)).thenReturn(false);

    DoAddBuddiesHandler addBuddiesHandler = new DoAddBuddiesHandler(mProvisioning,
                                                                    mUserProvider,
                                                                    mChatModuleSwitches);
    addBuddiesHandler.handle(mPapiContext);

    Mockito.verify(mFirstUser, Mockito.atLeastOnce()).addRelationship(
      mSecondUserAddress,
      Relationship.RelationshipType.INVITED,
      "Second",
      ""
    );

    Mockito.verify(mSecondUser, Mockito.atLeastOnce()).addRelationship(
      mFirstUserAddress,
      Relationship.RelationshipType.NEED_RESPONSE,
      "First",
      ""
    );
  }

  @Test
  public void addAcceptedFriendship_noPreviousRelationship_acceptRelationshipCreated()
    throws Exception
  {
    Mockito.when(mPapiContext.getBoolean("user1accepted", true)).thenReturn(true);
    Mockito.when(mPapiContext.getBoolean("user2accepted", true)).thenReturn(true);

    DoAddBuddiesHandler addBuddiesHandler = new DoAddBuddiesHandler(mProvisioning,
                                                                    mUserProvider,
                                                                    mChatModuleSwitches);
    addBuddiesHandler.handle(mPapiContext);

    Mockito.verify(mFirstUser, Mockito.atLeastOnce()).addRelationship(
      mSecondUserAddress,
      Relationship.RelationshipType.ACCEPTED,
      "Second",
      ""
    );

    Mockito.verify(mSecondUser, Mockito.atLeastOnce()).addRelationship(
      mFirstUserAddress,
      Relationship.RelationshipType.ACCEPTED,
      "First",
      ""
    );
  }

  @Test
  public void addAcceptedFriendship_aPreviousRelationship_relationshipUpdated()
    throws Exception
  {
    Mockito.when(mFirstUser.hasRelationship(mSecondUserAddress)).thenReturn(true);
    Mockito.when(mSecondUser.hasRelationship(mFirstUserAddress)).thenReturn(true);

    Mockito.when(mPapiContext.getBoolean("user1accepted", true)).thenReturn(true);
    Mockito.when(mPapiContext.getBoolean("user2accepted", true)).thenReturn(true);

    DoAddBuddiesHandler addBuddiesHandler = new DoAddBuddiesHandler(mProvisioning,
                                                                    mUserProvider,
                                                                    mChatModuleSwitches);
    addBuddiesHandler.handle(mPapiContext);

    Mockito.verify(mFirstUser, Mockito.atLeastOnce()).updateRelationshipType(
      mSecondUserAddress,
      Relationship.RelationshipType.ACCEPTED
    );

    Mockito.verify(mSecondUser, Mockito.atLeastOnce()).updateRelationshipType(
      mFirstUserAddress,
      Relationship.RelationshipType.ACCEPTED
    );
  }
}