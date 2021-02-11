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


import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.EntrySearchFilter;
import com.zimbra.cs.account.EntrySearchFilter.Multi;
import com.zimbra.cs.account.EntrySearchFilter.Operator;
import com.zimbra.cs.account.EntrySearchFilter.Single;
import com.zimbra.cs.account.EntrySearchFilter.Term;
import com.zimbra.cs.account.ldap.LdapEntrySearchFilter;
import com.zimbra.cs.account.ldap.LdapFilterParser;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openzal.zal.exceptions.ExceptionWrapper;
import org.openzal.zal.log.ZimbraLog;

public class LdapToSearchParamsConverter
{
  public static class LdapQueryVisitorConverter extends LdapEntrySearchFilter.LdapQueryVisitor {

    private static final String OPEN_PARENTHESIS = "(";
    private static final String CLOSE_PARENTHESIS = ") ";
    private static final String OPEN_NOT = OPEN_PARENTHESIS + "NOT ";
    private static final String CLOSE_NOT = CLOSE_PARENTHESIS;
    public static final String OR = " OR ";
    public static final String FILTER_FORMAT = "FIELD[%s]%s%s";
    public static final String EQUALS_OPERATOR = ":";
    public static final String STAR = "*";
    public static final String LESS_OR_EQ_OPERATOR = "<=";
    public static final String GREATER_OR_EQ_OPERATOR = ">=";

    private final StringBuilder filter;
    private final Deque<Integer> orStack;
    private final Map<String, Collection<String>> conversionKeyMap;

    public LdapQueryVisitorConverter(Map<String, Collection<String>> conversionKeyMap) {
      this.conversionKeyMap = conversionKeyMap;
      filter = new StringBuilder();
      orStack = new ArrayDeque<>();
    }

    @Override
    public String getFilter() {
      return filter.toString();
    }

    private static String removeRedundantStars(String filter) {
      return filter.replaceAll("[\\*]+", "*");
    }

    private String formatFilter(String key, String operator, String value) {
      if( "objectClass".equalsIgnoreCase(key) ) {
        StringBuilder builder = new StringBuilder(OPEN_PARENTHESIS);

        // Currently "objectClass" filtering is skipped
        switch (value) {
          case STAR:
            break;
          default:
            ZimbraLog.mailbox.warn("ignoring objectClass filter value " + value);
            break;
        }

        builder.append(String.format(FILTER_FORMAT, "zimbraCalResType", EQUALS_OPERATOR, "location")).append(OR);
        builder.append(String.format(FILTER_FORMAT, "zimbraCalResType", EQUALS_OPERATOR, "equipment")).append(OR);
        builder.append(String.format(FILTER_FORMAT, "type", EQUALS_OPERATOR, "group")).append(OR);
        builder.append(String.format(FILTER_FORMAT, "firstName", EQUALS_OPERATOR, STAR)).append(OR);
        builder.append(String.format(FILTER_FORMAT, "lastName", EQUALS_OPERATOR, STAR)).append(OR);
        builder.append(String.format(FILTER_FORMAT, "fullName", EQUALS_OPERATOR, STAR)).append(OR);
        builder.append(String.format(FILTER_FORMAT, "zimbraId", EQUALS_OPERATOR, STAR)).append(")");
        return builder.toString();
      } else {
        Collection<String> conversionSet =
            conversionKeyMap.getOrDefault(key, Collections.singleton(key));
        if (conversionSet.size() < 1) {
          throw new UnsupportedOperationException();
        } else if (conversionSet.size() == 1) {
          return removeRedundantStars(String.format(FILTER_FORMAT, conversionSet.iterator().next(), operator, value));
        } else {
          StringBuilder builder = new StringBuilder(OPEN_PARENTHESIS);
          for (String convertedKey : conversionSet) {
            builder.append(String.format(FILTER_FORMAT, convertedKey, operator, value)).append(OR);
          }
          builder.setLength(builder.length() - OR.length());
          builder.append(")");
          return removeRedundantStars(builder.toString());
        }
      }
    }

    private String formatEquals(String key, String value) {
      return formatFilter(key, EQUALS_OPERATOR, value);
    }

    private String formatHas(String key, String value) {
      return formatEquals(key, STAR + value + STAR);
    }

    private String formatEndsWith(String key, String value) {
      return formatEquals(key, STAR + value);
    }

    private String formatStartsWith(String key, String value) {
      return formatEquals(key, value + STAR);
    }

    private String formatLessOrEqual(String key, String value) {
      return formatFilter(key, LESS_OR_EQ_OPERATOR, value);
    }

    private String formatGreaterOrEquals(String key, String value) {
      return formatFilter(key, GREATER_OR_EQ_OPERATOR, value);
    }

    @Override
    public void visitSingle(Single term) {
      Operator operator = term.getOperator();
      boolean negation = term.isNegation();

      switch(operator){
        case gt: // gt = !le
          operator = Operator.le;
          negation = !negation;
          break;
        case lt: // lt = !ge
          operator = Operator.ge;
          negation = !negation;
          break;
      }

      if( negation ) {
        filter.append(OPEN_NOT);
      }

      String currentFilter;

      String attr = term.getLhs();
      String val = getVal(term);
      switch( operator ){
        case has:
          currentFilter = formatHas(attr, val);
          break;
        case ge:
          currentFilter = formatGreaterOrEquals(attr, val);
          break;
        case le:
          currentFilter = formatLessOrEqual(attr, val);
          break;
        case startswith:
          currentFilter = formatStartsWith(attr, val);
          break;
        case endswith:
          currentFilter = formatEndsWith(attr, val);
          break;
        case eq:
        default:
          currentFilter = formatEquals(attr, val);
          break;
      }

      if( negation ) {
        filter.append(CLOSE_NOT);
      } else {
        filter.append(currentFilter).append(" ");
      }
    }

    @Override
    public void enterMulti(Multi term) {
      if( term.isNegation() ) {
        filter.append(OPEN_NOT);
      }
      if( term.getTerms().size() > 1 ) {
        filter.append(OPEN_PARENTHESIS);
        if( !term.isAnd() ) {
          orStack.push(filter.length());
        }
      }
    }

    @Override
    public void leaveMulti(Multi term) {
      if( term.getTerms().size() > 1 ) {
        filter.setLength(filter.length() - 1); // remove trailing space
        if( !term.isAnd() ) {
          formatOrTerm();
        }
        filter.append(CLOSE_PARENTHESIS);
      }
      if (term.isNegation()) {
        filter.setLength(filter.length() - 1); // remove trailing space
        filter.append(CLOSE_NOT);
      }
    }

    private void formatOrTerm() {
      Integer orIndex = orStack.pop();
      String substring = filter.substring(orIndex);
      filter.setLength(orIndex);

      List<Pair<Integer, Integer>> subtermsBounds = findOrSubTerms(substring);
      int startIndex = 0;
      int endIndex;
      subtermsBounds.sort(new Comparator<Pair<Integer, Integer>>() {
        @Override
        public int compare(Pair<Integer, Integer> p1, Pair<Integer, Integer> p2) {
          return p1.getFirst().compareTo(p2.getFirst());
        }
      });
      for( Pair<Integer, Integer> bound : subtermsBounds) {
        endIndex = bound.getFirst();
        filter.append(substring.substring(startIndex, endIndex).replaceAll(" ", OR))
            .append(substring, endIndex, bound.getSecond());
        startIndex = bound.getSecond();
      }
      if( startIndex < substring.length() ) {
        filter.append(substring.substring(startIndex).replaceAll(" ", OR));
      }
    }

    public List<Pair<Integer, Integer>> findOrSubTerms(String filter) {
      List<Pair<Integer, Integer>> result = new ArrayList<>();

      int i = 0;
      while( i < filter.length() ) {
        char c = filter.charAt(i);
        if( c == '(' ) {
          int end = i + findMatchingClosedParenthesis(filter.substring(i + 1)) + 1;
          result.add(new Pair<>(i, end));
          i = end;
        } else {
          i++;
        }
      }

      return result;
    }

    private Integer findMatchingClosedParenthesis(String filterSubstring) {
      int counter = 0;
      char c;
      for( int i = 0; i < filterSubstring.length(); i++ ) {
        c = filterSubstring.charAt(i);
        if( c == '(' ) {
          counter ++;
        } else if( c == ')' ) {
          if( counter == 0 ) {
            return i;
          } else {
            counter--;
          }
        }
      }
      throw new IllegalArgumentException("Invalid expression: wrong parenthesis count");
    }
  }

  public static String convertToQueryString(Map<String, Collection<String>> conversionKeyMap, String ldapQuery) {
    try {
      Term term = LdapFilterParser.parse(ldapQuery.replaceAll("\\\\2a", ""));
      EntrySearchFilter filter = new EntrySearchFilter(term);
      LdapQueryVisitorConverter visitor = new LdapQueryVisitorConverter(conversionKeyMap);
      filter.traverse(visitor);
      return visitor.getFilter().trim();
    } catch ( ServiceException e ) {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public static String convertToQueryString(String ldapQuery) {
    return convertToQueryString(new HashMap<String, Collection<String>>(), ldapQuery);
  }

  public static SearchParams convertToSearchParams(Map<String, Collection<String>> conversionKeyMap, String ldapQuery) {
    com.zimbra.cs.index.SearchParams searchParams = new com.zimbra.cs.index.SearchParams();
    searchParams.setQueryString(convertToQueryString(conversionKeyMap, ldapQuery));
    return new SearchParams(searchParams);
  }
}
