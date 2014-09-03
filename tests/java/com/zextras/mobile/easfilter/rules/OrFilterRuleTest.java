package com.zextras.mobile.easfilter.rules;

import org.openzal.zal.ZEAccount;
import org.openzal.zal.ZEProvisioningSimulator;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class OrFilterRuleTest
{
  private FilterRule mAlwaysTrueRule;
  private FilterRule mAlwaysFalseRule;
  private ZEAccount  mFakeAccount;

  @Before
  public void setup()
  {
    ZEProvisioningSimulator provisioning = new ZEProvisioningSimulator();
    mFakeAccount = provisioning.createFakeAccount("foo@example.com");

    mAlwaysTrueRule = mock(FilterRule.class);
    when(mAlwaysTrueRule.match(any(ZEAccount.class), any(String.class))).thenReturn(true);

    mAlwaysFalseRule = mock(FilterRule.class);
    when(mAlwaysFalseRule.match(any(ZEAccount.class), any(String.class))).thenReturn(false);
  }

  @Test
  public void compose_two_syntactically_valid_rules_should_be_syntactically_valid()
  {
    FilterRule firstRule = new FilterRuleAccount("test@example.com");
    FilterRule secondRule = new FilterRuleContains("Outlook");

    OrFilterRule filterRule = new OrFilterRule(firstRule, secondRule);

    assertTrue(firstRule.isRuleSyntacticallyValid().first());
    assertTrue(secondRule.isRuleSyntacticallyValid().first());
    assertTrue(filterRule.isRuleSyntacticallyValid().first());
  }

  @Test
  public void if_one_rule_is_not_syntactically_valid_rule_should_be_invalid()
  {
    FilterRule firstRule = new FilterRuleAccount("foo@example.com");
    FilterRule secondRule = new FilterRuleContains("");
    FilterRule thirdRule = new FilterRuleAccount("bar@example.com");

    OrFilterRule filterRule = new OrFilterRule(firstRule, secondRule, thirdRule);

    assertTrue(firstRule.isRuleSyntacticallyValid().first());
    assertFalse(secondRule.isRuleSyntacticallyValid().first());
    assertTrue(thirdRule.isRuleSyntacticallyValid().first());
    assertFalse(filterRule.isRuleSyntacticallyValid().first());
  }

  @Test
  public void at_least_rule_must_match_to_return_true()
  {
    OrFilterRule filterRule = new OrFilterRule(mAlwaysFalseRule,
                                               mAlwaysFalseRule,
                                               mAlwaysTrueRule);

    assertTrue(filterRule.match(mFakeAccount, "test"));
    verify(mAlwaysFalseRule, times(2)).match(mFakeAccount, "test");
    verify(mAlwaysTrueRule, times(1)).match(mFakeAccount, "test");
  }

  @Test
  public void only_if_all_rule_fail_to_match_return_false()
  {
    OrFilterRule filterRule = new OrFilterRule(mAlwaysFalseRule,
                                               mAlwaysFalseRule,
                                               mAlwaysFalseRule);

    assertFalse(filterRule.match(mFakeAccount, "test"));
    verify(mAlwaysFalseRule, times(3)).match(mFakeAccount, "test");
  }

  @Test
  public void composite_rule_getRule_should_respect_the_other_rules()
  {
    FilterRule firstRule = new FilterRuleAccount("test@example.com");
    FilterRule secondRule = new FilterRuleContains("Outlook");

    OrFilterRule OrFilterRule = new OrFilterRule(firstRule, secondRule);
    assertEquals(
      "[{\"class\":\"com.zextras.mobile.easfilter.rules.FilterRuleAccount\",\"rule\":\"test@example.com\"},{\"class\":\"com.zextras.mobile.easfilter.rules.FilterRuleContains\",\"rule\":\"outlook\"}]",
      OrFilterRule.getRule()
    );
  }

  @Test
  public void composite_rule_toString_method()
  {
    FilterRule firstRule = new FilterRuleAccount("test@example.com");
    FilterRule secondRule = new FilterRuleContains("Outlook");

    OrFilterRule filterRule = new OrFilterRule(firstRule, secondRule);
    assertEquals(
      "[type = or; rules = [[type = account; rule = test@example.com] OR [type = contains; rule = outlook]]",
      filterRule.toString());
  }

  @Test
  public void test_equality()
    throws Exception
  {
    FilterRule firstRule = new FilterRuleAccount("foo@example.com");
    FilterRule secondRule = new FilterRuleContains("bar");

    FilterRule firstOrFilterRule = new OrFilterRule(firstRule, secondRule);
    FilterRule secondOrFilterRule = new OrFilterRule(firstRule, secondRule);

    assertNotEquals(firstOrFilterRule, firstRule);
    assertEquals(firstOrFilterRule, secondOrFilterRule);
  }
}