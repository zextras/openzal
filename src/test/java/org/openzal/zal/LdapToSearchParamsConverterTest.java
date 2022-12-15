package org.openzal.zal;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openzal.zal.LdapToSearchParamsConverter.LdapQueryVisitorConverter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class LdapToSearchParamsConverterTest {

    private static Map<String, Collection<String>> conversionMap;

    @BeforeClass
    public static void setup() {
        conversionMap = new HashMap<String, Collection<String>>() {{
            put("displayName", new TreeSet<>(Arrays.asList("displayName1", "displayName2", "displayName3")));
            put("display-name", new TreeSet<>(Arrays.asList("display-name1", "display-name2", "display-name3")));
            put("givenName", new TreeSet<>(Arrays.asList("givenName1", "givenName2", "givenName3")));
            put("title", new TreeSet<>(Arrays.asList("title1", "title2", "title3")));
        }};
    }

    @Test
    public void parse_simple_not() {
        String convert = LdapToSearchParamsConverter.convertToQueryString(
                "(!(displayName=*a*))"
        );
        Assert.assertEquals("(NOT FIELD[displayName]:*a*)", convert);
    }

    @Test
    public void parse_simple_or() {
        String convert = LdapToSearchParamsConverter.convertToQueryString(
                "(|(displayName=*a*)(display-name=*a*))"
        );
        Assert.assertEquals("(FIELD[displayName]:*a* OR FIELD[display-name]:*a*)", convert);
    }

    @Test
    public void parse_simple_and() {
        String convert = LdapToSearchParamsConverter.convertToQueryString(
                "(&(displayName=*a*)(display-name=*a*))"
        );
        Assert.assertEquals("(FIELD[displayName]:*a* FIELD[display-name]:*a*)", convert);
    }

    @Test
    public void parse_or_and_or_1() {
        String convert = LdapToSearchParamsConverter.convertToQueryString(
                "(|(&(|(displayName=*a*)(display-name=*a*))(givenName=b))(title>=1))"
        );
        Assert.assertEquals("(((FIELD[displayName]:*a* OR FIELD[display-name]:*a*) FIELD[givenName]:b) OR FIELD[title]>=1)", convert);
    }

    @Test
    public void parse_or_and_or_2() {
        String convert = LdapToSearchParamsConverter.convertToQueryString(
                "(&(|(displayName=*a*)(display-name=*a*))(|(givenName=b)(title>=1)))"
        );
        Assert.assertEquals("((FIELD[displayName]:*a* OR FIELD[display-name]:*a*) (FIELD[givenName]:b OR FIELD[title]>=1))", convert);
    }

    @Test
    public void parse_and_or_and_1() {
        String convert = LdapToSearchParamsConverter.convertToQueryString(
                "(&(|(&(displayName=*a*)(display-name=*a*))(givenName=b))(title>=1))"
        );
        Assert.assertEquals("(((FIELD[displayName]:*a* FIELD[display-name]:*a*) OR FIELD[givenName]:b) FIELD[title]>=1)", convert);
    }

    @Test
    public void parse_and_or_and_2() {
        String convert = LdapToSearchParamsConverter.convertToQueryString(
                "(|(&(displayName=*a*)(display-name=*a*))(&(givenName=b)(title>=1)))"
        );
        Assert.assertEquals("((FIELD[displayName]:*a* FIELD[display-name]:*a*) OR (FIELD[givenName]:b FIELD[title]>=1))", convert);
    }

    @Test
    public void parse_multiple_and_or_interleaved() {
        String convert = LdapToSearchParamsConverter.convertToQueryString(
                "(&(&(&(&(&(&(&(&(|(displayName=*a*)(display-name=*a*))(givenName=*b*))(sn=*c*))(title=*d*))(|(uid=*e*)(mailNickname=*e*)))(company=*f*))(mail=*g*))(|(physicalDeliveryOfficeName=*h*)(roomNumber=*h*)))(department=*i*))"
        );
        Assert.assertEquals("(((((((((FIELD[displayName]:*a* OR FIELD[display-name]:*a*) FIELD[givenName]:*b*) FIELD[sn]:*c*) FIELD[title]:*d*) "
                + "(FIELD[uid]:*e* OR FIELD[mailNickname]:*e*)) FIELD[company]:*f*) FIELD[mail]:*g*) (FIELD[physicalDeliveryOfficeName]:*h* "
                + "OR FIELD[roomNumber]:*h*)) FIELD[department]:*i*)", convert);
    }

    @Test(expected = IllegalArgumentException.class)
    public void find_terms() {
        LdapQueryVisitorConverter ldapQueryVisitorConverter = new LdapQueryVisitorConverter(Collections.emptyMap());
        List<Pair<Integer, Integer>> subterms = ldapQueryVisitorConverter.findOrSubTerms("()()()");
        Assert.assertEquals(3, subterms.size());
        Assert.assertEquals(0, subterms.get(0).getFirst().intValue());
        Assert.assertEquals(1, subterms.get(0).getSecond().intValue());
        Assert.assertEquals(2, subterms.get(1).getFirst().intValue());
        Assert.assertEquals(3, subterms.get(1).getSecond().intValue());
        Assert.assertEquals(4, subterms.get(2).getFirst().intValue());
        Assert.assertEquals(5, subterms.get(2).getSecond().intValue());
        subterms = ldapQueryVisitorConverter.findOrSubTerms("(()(())())");
        Assert.assertEquals(1, subterms.size());
        Assert.assertEquals(0, subterms.get(0).getFirst().intValue());
        Assert.assertEquals(9, subterms.get(0).getSecond().intValue());
        subterms = ldapQueryVisitorConverter.findOrSubTerms("()(())((()))");
        Assert.assertEquals(3, subterms.size());
        Assert.assertEquals(0, subterms.get(0).getFirst().intValue());
        Assert.assertEquals(1, subterms.get(0).getSecond().intValue());
        Assert.assertEquals(2, subterms.get(1).getFirst().intValue());
        Assert.assertEquals(5, subterms.get(1).getSecond().intValue());
        Assert.assertEquals(6, subterms.get(2).getFirst().intValue());
        Assert.assertEquals(11, subterms.get(2).getSecond().intValue());
        ldapQueryVisitorConverter.findOrSubTerms("(()()");
    }

    @Test
    public void parse_simple_not_with_map() {
        String convert = LdapToSearchParamsConverter.convertToQueryString(conversionMap,"(!(displayName=*a*))"
        );
        Assert.assertEquals("(NOT (FIELD[displayName1]:*a* OR FIELD[displayName2]:*a* OR FIELD[displayName3]:*a*))", convert);
    }

    @Test
    public void parse_simple_or_with_map() {
        String convert = LdapToSearchParamsConverter.convertToQueryString(conversionMap, "(|(displayName=*a*)(display-name=*a*))"
        );
        Assert.assertEquals("((FIELD[displayName1]:*a* OR FIELD[displayName2]:*a* OR FIELD[displayName3]:*a*) OR "
                + "(FIELD[display-name1]:*a* OR FIELD[display-name2]:*a* OR FIELD[display-name3]:*a*))", convert);
    }

    @Test
    public void parse_simple_and_with_map() {
        String convert = LdapToSearchParamsConverter.convertToQueryString(conversionMap,"(&(displayName=*a*)(display-name=*a*))"
        );
        Assert.assertEquals("((FIELD[displayName1]:*a* OR FIELD[displayName2]:*a* OR FIELD[displayName3]:*a*) "
                + "(FIELD[display-name1]:*a* OR FIELD[display-name2]:*a* OR FIELD[display-name3]:*a*))", convert);
    }

    @Test
    public void parse_or_and_or_1_with_map() {
        String convert = LdapToSearchParamsConverter.convertToQueryString(conversionMap, "(|(&(|(displayName=*a*)(display-name=*a*))(givenName=b))(title>=1))"
        );
        Assert.assertEquals("((((FIELD[displayName1]:*a* OR FIELD[displayName2]:*a* OR FIELD[displayName3]:*a*) OR "
                + "(FIELD[display-name1]:*a* OR FIELD[display-name2]:*a* OR FIELD[display-name3]:*a*)) "
                + "(FIELD[givenName1]:b OR FIELD[givenName2]:b OR FIELD[givenName3]:b)) OR "
                + "(FIELD[title1]>=1 OR FIELD[title2]>=1 OR FIELD[title3]>=1))", convert);
    }

    @Test
    public void parse_or_and_or_2_with_map() {
        String convert = LdapToSearchParamsConverter.convertToQueryString(conversionMap, "(&(|(displayName=*a*)(display-name=*a*))(|(givenName=b)(title>=1)))"
        );
        Assert.assertEquals("(((FIELD[displayName1]:*a* OR FIELD[displayName2]:*a* OR FIELD[displayName3]:*a*) OR "
                + "(FIELD[display-name1]:*a* OR FIELD[display-name2]:*a* OR FIELD[display-name3]:*a*)) "
                + "((FIELD[givenName1]:b OR FIELD[givenName2]:b OR FIELD[givenName3]:b) OR "
                + "(FIELD[title1]>=1 OR FIELD[title2]>=1 OR FIELD[title3]>=1)))", convert);
    }

    @Test
    public void parse_and_or_and_1_with_map() {
        String convert = LdapToSearchParamsConverter.convertToQueryString(conversionMap, "(&(|(&(displayName=*a*)(display-name=*a*))(givenName=b))(title>=1))"
        );
        Assert.assertEquals("((((FIELD[displayName1]:*a* OR FIELD[displayName2]:*a* OR FIELD[displayName3]:*a*) "
                + "(FIELD[display-name1]:*a* OR FIELD[display-name2]:*a* OR FIELD[display-name3]:*a*)) OR "
                + "(FIELD[givenName1]:b OR FIELD[givenName2]:b OR FIELD[givenName3]:b)) "
                + "(FIELD[title1]>=1 OR FIELD[title2]>=1 OR FIELD[title3]>=1))", convert);
    }

    @Test
    public void parse_and_or_and_2_with_map() {
        String convert = LdapToSearchParamsConverter.convertToQueryString(conversionMap, "(|(&(displayName=*a*)(display-name=*a*))(&(givenName=b)(title>=1)))"
        );
        Assert.assertEquals("(((FIELD[displayName1]:*a* OR FIELD[displayName2]:*a* OR FIELD[displayName3]:*a*) "
                + "(FIELD[display-name1]:*a* OR FIELD[display-name2]:*a* OR FIELD[display-name3]:*a*)) OR "
                + "((FIELD[givenName1]:b OR FIELD[givenName2]:b OR FIELD[givenName3]:b) "
                + "(FIELD[title1]>=1 OR FIELD[title2]>=1 OR FIELD[title3]>=1)))", convert);
    }

    @Test
    public void convert_object_class() {
        String convert = LdapToSearchParamsConverter.convertToQueryString(
                "(objectClass=*)"
        );
        Assert.assertEquals("(FIELD[zimbraCalResType]:location OR FIELD[zimbraCalResType]:equipment OR "
                        + "FIELD[type]:group OR FIELD[firstName]:* OR FIELD[lastName]:* OR FIELD[fullName]:* OR FIELD[zimbraId]:*)",
                convert);

        convert = LdapToSearchParamsConverter.convertToQueryString(
                "(objectClass=a)"
        );
        Assert.assertEquals("(FIELD[zimbraCalResType]:location OR FIELD[zimbraCalResType]:equipment OR "
                        + "FIELD[type]:group OR FIELD[firstName]:* OR FIELD[lastName]:* OR FIELD[fullName]:* OR FIELD[zimbraId]:*)",
                convert);
    }
}