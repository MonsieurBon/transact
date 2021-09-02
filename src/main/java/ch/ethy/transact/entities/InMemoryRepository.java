package ch.ethy.transact.entities;

import java.util.*;

public class InMemoryRepository<T extends Entity> implements Repository<T> {
  protected final Map<String, T> entities = new HashMap<>();

  @Override
  public void add(T entity) {
    entities.put(entity.getId(), entity.copy());
  }

  @Override
  public T get(String id) {
    T t = entities.get(id);
    return t != null ? t.copy() : null;
  }

  @Override
  public void update(T entity) {
    if (!entities.containsKey(entity.getId())) {
      throw new EntityDoesNotExistException();
    }

    entities.put(entity.getId(), entity.copy());
  }
}
