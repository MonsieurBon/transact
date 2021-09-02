package ch.ethy.transact.account;

import ch.ethy.transact.authentication.*;
import ch.ethy.transact.entities.*;

public class UserRepository extends InMemoryRepository<User> implements UserProvider {
  public boolean usernameExists(String username) {
    return this.entities.values().stream()
        .anyMatch(user -> user.getUsername().equals(username));
  }

  @Override
  public SecurityUser getByUsername(String username) {
    return this.entities.values().stream()
        .filter(user -> user.getUsername().equals(username))
        .findFirst()
        .orElse(null);
  }
}
