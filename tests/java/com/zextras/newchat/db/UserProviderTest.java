package com.zextras.newchat.db;

import org.openzal.zal.ZEProvisioningSimulator;
import com.zextras.newchat.address.SpecificAddress;
import com.zextras.newchat.db.mappers.EventMapper;
import com.zextras.newchat.db.mappers.RelationshipMapper;
import com.zextras.newchat.db.mappers.StatusMapper;
import com.zextras.newchat.db.mappers.UserInfoMapper;
import com.zextras.newchat.db.modifiers.UserModifier;
import com.zextras.newchat.db.providers.UserIdentityMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class UserProviderTest {
  private DbHandler               mDbHandler;
  private ZEProvisioningSimulator mProvisioning;

  @Before
  public void setUp() throws Exception
  {
    mProvisioning = new ZEProvisioningSimulator();
    mDbHandler = new DbHandlerImpl("jdbc:hsqldb:mem:zxchat;hsqldb.log_data=false;shutdown=false");
  }

  @Test
  public void test_userTest_userInfo() throws Exception
  {
    UserInfoMapper userInfoMapper = new UserInfoMapper(mDbHandler);
    RelationshipMapper relationshipMapper = new RelationshipMapper(mDbHandler);
    StatusMapper statusMapper = new StatusMapper(mDbHandler);
    EventMapper eventMapper = new EventMapper(mDbHandler);

    UserModifier userModifier = new UserModifier(
      userInfoMapper, relationshipMapper,
      statusMapper, eventMapper);
    UserIdentityMap userIdentityMap = Mockito.mock(UserIdentityMap.class);

    Mockito.when(userIdentityMap.hasUser(Mockito.any(SpecificAddress.class))).thenReturn(false);

    SpecificAddress address = new SpecificAddress("marco@example.com");
//    User user = new UserProvider(userInfoMapper,
//                                 relationshipMapper,
//                                 statusMapper,
//                                 eventMapper,
//                                 userIdentityMap,
//                                 userModifier,
//                                 mProvisioning).getUser(address);
  }
}
