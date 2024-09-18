package microservices.book.multiplication.user;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<Users, Long> {
    Optional<Users> findByAlias(final String alias);
    List<Users> findAllByIdIn(final List<Long> ids);
}
