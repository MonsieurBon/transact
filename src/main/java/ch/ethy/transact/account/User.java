package ch.ethy.transact.account;

import ch.ethy.transact.authentication.*;
import ch.ethy.transact.entities.*;

import java.util.*;

public class User implements Entity, SecurityUser {
  private final String id;
  private String username;
  private String password;

  public User(String id) {
    this.id = id;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  @SuppressWarnings("unchecked")
  public User copy() {
    User user = new User(id);
    user.username = username;
    user.password = password;
    return user;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @Override
  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    User user = (User) o;
    return id.equals(user.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
