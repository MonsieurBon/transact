package ch.ethy.transact.entities;

import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryRepositoryTest {

  private InMemoryRepository<MockEntity> repository;

  @BeforeEach
  void setUp() {
    repository = new InMemoryRepository<>();
  }

  @Test
  public void can_add_entities() {
    repository.add(new MockEntity("a"));
  }

  @Test
  public void can_retrieve_entities() {
    MockEntity a = new MockEntity("a");
    repository.add(a);
    MockEntity copyOfA = repository.get("a");
    assertEquals(a, copyOfA);
    assertNotSame(a, copyOfA);
  }

  @Test
  public void changing_entity_does_not_update_store() {
    MockEntity b = new MockEntity("b");
    repository.add(b);
    b.setProperty("myProperty");
    assertNull(repository.get("b").getProperty());
  }

  @Test
  public void can_be_updated() {
    MockEntity c = new MockEntity("c");
    repository.add(c);
    c.setProperty("myProperty");
    repository.update(c);
    assertEquals("myProperty", repository.get("c").getProperty());
  }

  @Test
  public void cannot_update_before_add() {
    MockEntity d = new MockEntity("d");
    assertThrows(EntityDoesNotExistException.class, () -> repository.update(d));
  }

  @Test
  public void invalid_id_returns_null() {
    MockEntity e = repository.get("e");
    assertNull(e);
  }

  private static class MockEntity implements Entity {
    private final String id;
    private String property;

    private MockEntity(String id) {
      this.id = id;
    }

    @Override
    public String getId() {
      return id;
    }

    public String getProperty() {
      return property;
    }

    public void setProperty(String property) {
      this.property = property;
    }

    @Override
    public MockEntity copy() {
      MockEntity copy = new MockEntity(id);
      copy.property = property;
      return copy;
    }


    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      MockEntity that = (MockEntity) o;
      return id.equals(that.id) && Objects.equals(property, that.property);
    }

    @Override
    public int hashCode() {
      return Objects.hash(id, property);
    }
  }
}
