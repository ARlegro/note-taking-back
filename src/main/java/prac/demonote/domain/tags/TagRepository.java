package prac.demonote.domain.tags;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import prac.demonote.domain.tags.model.Tag;

@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {

}
