package microservices.book.multiplication.challenge;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChallengeAttemptRepository extends CrudRepository<ChallengeAttempt, Long> {
    /**
     * @return the last 10 attempts for a given user, identified by their alias.
     */
    List<ChallengeAttempt> findTop10ByUsersAliasOrderByIdDesc(String userAlias);

    /**
     * @return the last attempts for a given user, identified by their alias.
     */
    @Query("SELECT a FROM ChallengeAttempt a WHERE a.users.alias = :userAlias ORDER BY a.id DESC")
    List<ChallengeAttempt> lastAttempts(@Param("userAlias") String userAlias);
}
