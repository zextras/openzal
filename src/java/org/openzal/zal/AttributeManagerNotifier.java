package org.openzal.zal;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openzal.zal.log.ZimbraLog;

public class AttributeManagerNotifier
{
  private class AttributeManager extends com.zimbra.cs.account.AttributeManager
  {
    @Override
    public boolean isMultiValued(String attrName)
    {
      return sAttributeManagerOriginal.isMultiValued(attrName);
    }

    @Override
    public boolean isEphemeral(String attrName)
    {
      return sAttributeManagerOriginal.isEphemeral(attrName);
    }

    public boolean isDynamic(String attrName)
    {
      return sAttributeManagerOriginal.isDynamic(attrName);
    }

    @Override
    public Map<String, com.zimbra.cs.account.AttributeInfo> getNonDynamicEphemeralAttrs(com.zimbra.cs.account.Entry.EntryType entryType)
    {
      return sAttributeManagerOriginal.getNonDynamicEphemeralAttrs(entryType);
    }

    @Override
    public void addAttribute(com.zimbra.cs.account.AttributeInfo info)
    {
      sAttributeManagerOriginal.addAttribute(info);
    }

    @Override
    protected com.zimbra.cs.account.AttributeInfo createAttributeInfo(
      String name,
      int id,
      String parentOid,
      int groupId,
      com.zimbra.cs.account.AttributeCallback callback,
      com.zimbra.cs.account.AttributeType type,
      com.zimbra.cs.account.AttributeOrder order,
      String value,
      boolean immutable,
      String min,
      String max,
      com.zimbra.cs.account.AttributeCardinality cardinality,
      Set<com.zimbra.cs.account.AttributeClass> requiredIn,
      Set<com.zimbra.cs.account.AttributeClass> optionalIn,
      Set<com.zimbra.cs.account.AttributeFlag> flags,
      List<String> globalConfigValues,
      List<String> defaultCOSValues,
      List<String> defaultExternalCOSValues,
      List<String> globalConfigValuesUpgrade,
      List<String> defaultCOSValuesUpgrade,
      String description,
      List<com.zimbra.cs.account.AttributeServerType> requiresRestart,
      List<com.zimbra.common.util.Version> sinceVer,
      com.zimbra.common.util.Version deprecatedSinceVer
    )
    {
      return super.createAttributeInfo(
        name,
        id,
        parentOid,
        groupId,
        callback,
        type,
        order,
        value,
        immutable,
        min,
        max,
        cardinality,
        requiredIn,
        optionalIn,
        flags,
        globalConfigValues,
        defaultCOSValues,
        defaultExternalCOSValues,
        globalConfigValuesUpgrade,
        defaultCOSValuesUpgrade,
        description,
        requiresRestart,
        sinceVer,
        deprecatedSinceVer
      );
    }

    @Override
    public boolean isAccountInherited(String attr)
    {
      return sAttributeManagerOriginal.isAccountInherited(attr);
    }

    @Override
    public boolean isAccountCosDomainInherited(String attr)
    {
      return sAttributeManagerOriginal.isAccountCosDomainInherited(attr);
    }

    @Override
    public boolean isDomainInherited(String attr)
    {
      return sAttributeManagerOriginal.isDomainInherited(attr);
    }

    @Override
    public boolean isServerInherited(String attr)
    {
      return sAttributeManagerOriginal.isServerInherited(attr);
    }

    @Override
    public boolean isDomainAdminModifiable(String attr, com.zimbra.cs.account.AttributeClass klass)
      throws com.zimbra.common.service.ServiceException
    {
      return sAttributeManagerOriginal.isDomainAdminModifiable(attr, klass);
    }

    @Override
    public void makeDomainAdminModifiable(String attr)
    {
      sAttributeManagerOriginal.makeDomainAdminModifiable(attr);
    }

    @Override
    public boolean inVersion(String attr, String version)
      throws com.zimbra.common.service.ServiceException
    {
      return sAttributeManagerOriginal.inVersion(attr, version);
    }

    @Override
    public boolean beforeVersion(String attr, String version)
      throws com.zimbra.common.service.ServiceException
    {
      return sAttributeManagerOriginal.beforeVersion(attr, version);
    }

    @Override
    public boolean isFuture(String attr)
    {
      return sAttributeManagerOriginal.isFuture(attr);
    }

    @Override
    public boolean addedIn(String attr, String version)
      throws com.zimbra.common.service.ServiceException
    {
      return sAttributeManagerOriginal.addedIn(attr, version);
    }

    @Override
    public com.zimbra.cs.account.AttributeType getAttributeType(String attr)
      throws com.zimbra.common.service.ServiceException
    {
      return sAttributeManagerOriginal.getAttributeType(attr);
    }

    @Override
    public boolean containsBinaryData(String attr)
    {
      return sAttributeManagerOriginal.containsBinaryData(attr);
    }

    @Override
    public boolean isBinaryTransfer(String attr)
    {
      return sAttributeManagerOriginal.isBinaryTransfer(attr);
    }

    @Override
    public Set<String> getBinaryAttrs()
    {
      return sAttributeManagerOriginal.getBinaryAttrs();
    }

    @Override
    public Set<String> getBinaryTransferAttrs()
    {
      return sAttributeManagerOriginal.getBinaryTransferAttrs();
    }

    @Override
    public Map<String, com.zimbra.cs.account.AttributeInfo> getEphemeralAttrs()
    {
      return sAttributeManagerOriginal.getEphemeralAttrs();
    }

    @Override
    public Set<String> getEphemeralAttributeNames()
    {
      return sAttributeManagerOriginal.getEphemeralAttributeNames();
    }

    @Override
    public Set<String> getAttrsWithFlag(com.zimbra.cs.account.AttributeFlag flag)
    {
      return sAttributeManagerOriginal.getAttrsWithFlag(flag);
    }

    @Override
    public Set<String> getAttrsInClass(com.zimbra.cs.account.AttributeClass klass)
    {
      return sAttributeManagerOriginal.getAttrsInClass(klass);
    }

    @Override
    public Set<String> getAllAttrsInClass(com.zimbra.cs.account.AttributeClass klass)
    {
      return sAttributeManagerOriginal.getAllAttrsInClass(klass);
    }

    @Override
    public Set<String> getLowerCaseAttrsInClass(com.zimbra.cs.account.AttributeClass klass)
    {
      return sAttributeManagerOriginal.getLowerCaseAttrsInClass(klass);
    }

    @Override
    public Set<String> getImmutableAttrs()
    {
      return sAttributeManagerOriginal.getImmutableAttrs();
    }

    @Override
    public Set<String> getImmutableAttrsInClass(com.zimbra.cs.account.AttributeClass klass)
    {
      return sAttributeManagerOriginal.getImmutableAttrsInClass(klass);
    }

    @Override
    public void preModify(Map<String, ?> attrs, com.zimbra.cs.account.Entry entry, com.zimbra.cs.account.callback.CallbackContext context, boolean checkImmutable)
      throws com.zimbra.common.service.ServiceException
    {
      sAttributeManagerOriginal.preModify(attrs, entry, context, checkImmutable);
    }

    @Override
    public void preModify(
      Map<String, ?> attrs, com.zimbra.cs.account.Entry entry, com.zimbra.cs.account.callback.CallbackContext context, boolean checkImmutable, boolean allowCallback
    )
      throws com.zimbra.common.service.ServiceException
    {
      sAttributeManagerOriginal.preModify(attrs, entry, context, checkImmutable, allowCallback);
    }

    @Override
    public com.zimbra.cs.account.AttributeInfo getAttributeInfo(String name)
    {
      return sAttributeManagerOriginal.getAttributeInfo(name);
    }

    public void postModify(
      Map<String, ? extends Object> attrs,
      com.zimbra.cs.account.Entry entry,
      com.zimbra.cs.account.callback.CallbackContext context
    )
    {
      sAttributeManagerOriginal.postModify(attrs, entry, context);
      notify(entry);
    }

    public void postModify(
      Map<String, ? extends Object> attrs,
      com.zimbra.cs.account.Entry entry,
      com.zimbra.cs.account.callback.CallbackContext context,
      boolean allowCallback
    )
    {
      sAttributeManagerOriginal.postModify(attrs, entry, context, allowCallback);
      notify(entry);
    }

    private void notify(com.zimbra.cs.account.Entry entry)
    {
      switch( entry.getEntryType() )
      {
        case ACCOUNT:
          if( entry instanceof com.zimbra.cs.account.Account )
          {
            Account account = new Account(entry);
            notifyListeners(Operation.ACCOUNT, account);
            break;
          }
      }
    }
  }

  public interface AttributeChangesListener
  {
    void attributeModified(Operation operation, Entry entry);
  }

  private static com.zimbra.cs.account.AttributeManager sAttributeManagerOriginal;

  private final Set<AttributeChangesListener> mListeners;

  public AttributeManagerNotifier()
  {
    mListeners = new HashSet<>();
  }

  public void injectField()
  {
    if( sAttributeManagerOriginal == null)
    {
      try
      {
        Field mInstance = com.zimbra.cs.account.AttributeManager.class.getDeclaredField("mInstance");
        mInstance.setAccessible(true);
        sAttributeManagerOriginal = (com.zimbra.cs.account.AttributeManager) mInstance.get(null);
        AttributeManager sAttributeManagerModified = new AttributeManager();
        mInstance.set(null, sAttributeManagerModified);
      }
      catch( Throwable ex )
      {
        ZimbraLog.extensions.fatal("ZAL Reflection Initialization Exception: " + Utils.exceptionToString(ex));
        throw new RuntimeException(ex);
      }
    }
  }

  public void restoreField()
  {
    try
    {
      Field mInstance = com.zimbra.cs.account.AttributeManager.class.getDeclaredField("mInstance");
      mInstance.setAccessible(true);
      mInstance.set(null, sAttributeManagerOriginal);
    }
    catch (Throwable ex)
    {
      ZimbraLog.extensions.fatal("ZAL Reflection Initialization Exception: " + Utils.exceptionToString(ex));
      throw new RuntimeException(ex);
    }
  }

  public void notifyListeners(Operation operation, Entry entry)
  {
    for(AttributeChangesListener listener : mListeners)
    {
      listener.attributeModified(operation, entry);
    }
  }

  public void registerListener(AttributeChangesListener listener)
  {
    mListeners.add(listener);
  }

  public void unregisterListener(AttributeChangesListener listener)
  {
    mListeners.remove(listener);
  }
}