package ch.ethy.transact.ynab;

import java.util.List;

public class Budget {
  private String id;
  private String name;
  private String last_modified_on;
  private String first_month;
  private String last_month;
  private DateFormat date_format;
  private CurrencyFormat currency_format;
  private List<Account> accounts;

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getLast_modified_on() {
    return last_modified_on;
  }

  public String getFirst_month() {
    return first_month;
  }

  public String getLast_month() {
    return last_month;
  }

  public DateFormat getDate_format() {
    return date_format;
  }

  public CurrencyFormat getCurrency_format() {
    return currency_format;
  }

  public List<Account> getAccounts() {
    return accounts;
  }
}
