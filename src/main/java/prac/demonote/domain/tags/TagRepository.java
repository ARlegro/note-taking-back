package prac.demonote.domain.tags;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import prac.demonote.domain.tags.model.Tag;

@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {

  List<Tag> findByOwnerId(UUID ownerId);

  Optional<Tag> findByNameAndOwnerId(String name, UUID ownerId);

  boolean existsByNameAndOwnerId(String name, UUID ownerId);

  boolean existsByIdAndOwnerId(UUID tagId, UUID ownerId);
}
