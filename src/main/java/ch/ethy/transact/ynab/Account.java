package ch.ethy.transact.ynab;

public class Account {
  private String id;
  private String name;
  private String type;
  private boolean on_budget;
  private boolean closed;
  private String note;
  private int balance;
  private int cleared_balance;
  private int uncleared_balance;
  private String transfer_payee_id;
  private boolean deleted;

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  public boolean isOn_budget() {
    return on_budget;
  }

  public boolean isClosed() {
    return closed;
  }

  public String getNote() {
    return note;
  }

  public int getBalance() {
    return balance;
  }

  public int getCleared_balance() {
    return cleared_balance;
  }

  public int getUncleared_balance() {
    return uncleared_balance;
  }

  public String getTransfer_payee_id() {
    return transfer_payee_id;
  }

  public boolean isDeleted() {
    return deleted;
  }
}
