package org.openzal.zal.lib;

import org.jetbrains.annotations.NotNull;

public interface ZalWrapper<R>
{
  R toZimbra();

  <T> T toZimbra(@NotNull Class<T> target);
}
