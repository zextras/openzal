package org.openzal.zal;

import java.util.Collection;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;
/* $if ZimbraX == 1 $ */
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
/* $endif $ */

public class RedisClientHelper
{
  /* $if ZimbraX == 1 $ */
  private final com.zimbra.cs.mailbox.RedissonClientHolder mZObject;
  private final RedissonClient mRedissonClient;
  /* $endif $ */

  public RedisClientHelper()
  {
    /* $if ZimbraX == 1 $ */
    mZObject = com.zimbra.cs.mailbox.RedissonClientHolder.getInstance();
    mRedissonClient = mZObject.getRedissonClient();
    /* $endif $ */
  }

  @Nullable
  public String put(@NotNull String scope, @NotNull String key, String value)
  {
    /* $if ZimbraX == 1 $ */
    RMap<String, String> bucket = mRedissonClient.getMap(scope);
    bucket.put(key, value);
    return value;
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  @Nullable
  public String get(@NotNull String scope, @NotNull String key)
  {
    /* $if ZimbraX == 1 $ */
    return get(scope, key, null);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public boolean contains(@NotNull String scope, @NotNull String key)
  {
    /* $if ZimbraX == 1 $ */
    return get(scope, key, null) != null;
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  @Nullable
  public String get(@NotNull String scope, @NotNull String key, String def)
  {
    /* $if ZimbraX == 1 $ */
    RMap<String, String> bucket = mRedissonClient.getMap(scope);
    String result = bucket.get(key);
    return result == null ? def : result;
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public Collection<String> getValuesByScope(@NotNull String scope)
  {
    /* $if ZimbraX == 1 $ */
    RMap<String, String> bucket = mRedissonClient.getMap(scope);
    return bucket.values();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public Collection<String> getValuesByScopeAndKeyPattern(@NotNull String scope, @NotNull String keyBegin, @NotNull String keyEnd)
  {
    /* $if ZimbraX == 1 $ */
    RMap<String, String> bucket = mRedissonClient.getMap(scope);
    return bucket.values(String.format("%s*%s", keyBegin, keyEnd));
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public void remove(@NotNull String scope, @NotNull String key)
  {
    /* $if ZimbraX == 1 $ */
    RMap<String, String> bucket = mRedissonClient.getMap(scope);
    bucket.remove(key);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }
}
