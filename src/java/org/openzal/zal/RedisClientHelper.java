package org.openzal.zal;

import javax.annotation.*;

import java.util.Set;
/* $if ZimbraX == 1 $ */
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
/* $endif $ */

public class RedisClientHelper
{
  /* $if ZimbraX == 1 $ */
  private final com.zimbra.cs.mailbox.RedissonClientHolder mZObject;
  private final RedissonClient                             mRedissonClient;
  /* $endif $ */

  public RedisClientHelper()
  {
    /* $if ZimbraX == 1 $ */
    mZObject = com.zimbra.cs.mailbox.RedissonClientHolder.getInstance();
    mRedissonClient = mZObject.getRedissonClient();
    /* $endif $ */
  }

  @Nullable
  public String put(@Nonnull String key, String value)
  {
    /* $if ZimbraX == 1 $ */
    RSet<String> set = mRedissonClient.getSet(key);
    set.add(value);
    return value;
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public Set<String> get(@Nonnull String key)
  {
    /* $if ZimbraX == 1 $ */
    RSet<String> set = mRedissonClient.getSet(key);
    return set.readAll();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public void remove(@Nonnull String key, @Nonnull String value)
  {
    /* $if ZimbraX == 1 $ */
    RSet<String> set = mRedissonClient.getSet(key);
    set.remove(value);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public void remove(@Nonnull String key)
  {
    /* $if ZimbraX == 1 $ */
    RSet<String> set = mRedissonClient.getSet(key);
    set.delete();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }
}
