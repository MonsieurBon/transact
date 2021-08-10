package ch.ethy.transact.entities;

public interface Entity {
  String getId();

  <U extends Entity> U copy();
}
