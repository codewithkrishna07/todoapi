package todoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import todoapi.model.Todo;

@Repository
public interface TodoRepository extends JpaRepository<Todo,Long> {
}
