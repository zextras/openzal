package org.openzal.zal;

import javax.annotation.*;

import java.util.Set;
/* $if ZimbraX == 1 $ */
import org.redisson.api.RBucket;
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
  public Set<String> putInSet(@Nonnull String key, String value)
  {
    /* $if ZimbraX == 1 $ */
    RSet<String> set = mRedissonClient.getSet(key);
    set.add(value);
    return set.readAll();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public Set<String> getSet(@Nonnull String key)
  {
    /* $if ZimbraX == 1 $ */
    RSet<String> set = mRedissonClient.getSet(key);
    return set.readAll();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public void removeFromSet(@Nonnull String key, @Nonnull String value)
  {
    /* $if ZimbraX == 1 $ */
    RSet<String> set = mRedissonClient.getSet(key);
    set.remove(value);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  @Nullable
  public String set(@Nonnull String key, String value)
  {
    /* $if ZimbraX == 1 $ */
    RBucket<String> bucket = mRedissonClient.getBucket(key);
    bucket.set(value);
    return value;
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  @Nullable
  public String get(@Nonnull String key)
  {
    /* $if ZimbraX == 1 $ */
    RBucket<String> bucket = mRedissonClient.getBucket(key);
    return bucket.get();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public boolean contains(@Nonnull String key)
  {
    /* $if ZimbraX == 1 $ */
    RBucket<String> bucket = mRedissonClient.getBucket(key);
    return bucket.isExists();
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
