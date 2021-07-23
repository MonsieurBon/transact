package ch.ethy.transact.authentication;

public interface SecurityUser {
  Object getId();
  String getPassword();
  String getSalt();
}
