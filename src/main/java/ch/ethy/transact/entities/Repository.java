package ch.ethy.transact.entities;

public interface Repository<T extends Entity> {
  void add(T entity);

  T get(String id);

  void update(T entity);
}
