package ch.ethy.transact.authentication;

public interface UserProvider {
  SecurityUser getByUsername(String username);
}
