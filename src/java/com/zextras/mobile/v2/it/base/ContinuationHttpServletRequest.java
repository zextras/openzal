package com.zextras.mobile.v2.it.base;

import org.openzal.zal.Continuation;

import javax.servlet.http.HttpServletRequest;


public interface ContinuationHttpServletRequest extends HttpServletRequest
{
  void setContinuation(Continuation continuation);
  Continuation getContinuation();
}
