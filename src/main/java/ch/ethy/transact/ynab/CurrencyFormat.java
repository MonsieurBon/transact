package ch.ethy.transact.ynab;

public class CurrencyFormat {
  private String iso_code;
  private String example_format;
  private int decimal_digits;
  private String decimal_separator;
  private boolean symbol_first;
  private String group_separator;
  private String currency_symbol;
  private boolean display_symbol;

  public String getIso_code() {
    return iso_code;
  }

  public String getExample_format() {
    return example_format;
  }

  public int getDecimal_digits() {
    return decimal_digits;
  }

  public String getDecimal_separator() {
    return decimal_separator;
  }

  public boolean isSymbol_first() {
    return symbol_first;
  }

  public String getGroup_separator() {
    return group_separator;
  }

  public String getCurrency_symbol() {
    return currency_symbol;
  }

  public boolean isDisplay_symbol() {
    return display_symbol;
  }
}
