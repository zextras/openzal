/*
 * ZAL - The abstraction layer for Zimbra.
 * Copyright (C) 2016 ZeXtras S.r.l.
 *
 * This file is part of ZAL.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, version 2 of
 * the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ZAL. If not, see <http://www.gnu.org/licenses/>.
 */

package org.openzal.zal;

public class MimeConstants
{
  public static final String CT_TYPE                      = "Content-Type";
  public static final String CT_TEXT_PLAIN                = com.zimbra.common.mime.MimeConstants.CT_TEXT_PLAIN;
  public static final String CT_TEXT_HTML                 = com.zimbra.common.mime.MimeConstants.CT_TEXT_HTML;
  public static final String CT_TEXT_CALENDAR             = com.zimbra.common.mime.MimeConstants.CT_TEXT_CALENDAR;
  public static final String CT_MESSAGE_RFC822            = com.zimbra.common.mime.MimeConstants.CT_MESSAGE_RFC822;
  public static final String CT_APPLICATION_OCTET_STREAM  = com.zimbra.common.mime.MimeConstants.CT_APPLICATION_OCTET_STREAM;
  public static final String CT_MULTIPART_PREFIX          = com.zimbra.common.mime.MimeConstants.CT_MULTIPART_PREFIX;
  public static final String P_CHARSET                    = com.zimbra.common.mime.MimeConstants.P_CHARSET;
  public static final String P_CHARSET_UTF8               = com.zimbra.common.mime.MimeConstants.P_CHARSET_UTF8;
  public static final String CT_MULTIPART_SIGNED          = com.zimbra.common.mime.MimeConstants.CT_MULTIPART_SIGNED;
  public static final String CT_SMIME_TYPE_ENVELOPED_DATA = "enveloped-data";
  public static final String CT_APPLICATION_SMIME         = com.zimbra.common.mime.MimeConstants.CT_APPLICATION_SMIME;
  public static final String CT_APPLICATION_SMIME_OLD     = "application/x-pkcs7-mime";
  public static final String CT_SMIME_TYPE_SIGNED_DATA    = "signed-data";
}
