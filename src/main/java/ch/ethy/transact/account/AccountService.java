package ch.ethy.transact.account;

import java.util.*;
import java.util.function.*;

public class AccountService {
  private final UserRepository userRepository;
  private final Supplier<UUID> uuidSupplier;
  private final Function<String, String> passwordHasher;

  public AccountService(UserRepository userRepository, Supplier<UUID> uuidSupplier, Function<String, String> passwordHasher) {
    this.userRepository = userRepository;
    this.uuidSupplier = uuidSupplier;
    this.passwordHasher = passwordHasher;
  }

  public User register(String username, String password) throws UserAlreadyExistsException {
    if (userRepository.usernameExists(username)) {
      throw new UserAlreadyExistsException();
    }

    String id = uuidSupplier.get().toString();

    User user = new User(id);
    user.setUsername(username);
    user.setPassword(passwordHasher.apply(password));

    userRepository.add(user);

    return user;
  }
}
