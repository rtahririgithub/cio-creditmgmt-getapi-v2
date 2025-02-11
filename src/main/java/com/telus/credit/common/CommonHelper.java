package com.telus.credit.common;

import java.util.Collection;
import java.util.Collections;

public class CommonHelper {
   private CommonHelper() {
      // utils
   }
   
   public static <T> Collection<T> nullSafe(Collection<T> collection) {
      return collection == null ? Collections.emptyList() : collection;
   }
}
