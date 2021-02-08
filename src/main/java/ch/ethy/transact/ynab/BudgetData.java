package ch.ethy.transact.ynab;

import java.util.List;

public class BudgetData {
  private List<Budget> budgets;
  private Budget default_budget;

  public List<Budget> getBudgets() {
    return budgets;
  }

  public Budget getDefault_budget() {
    return default_budget;
  }
}
