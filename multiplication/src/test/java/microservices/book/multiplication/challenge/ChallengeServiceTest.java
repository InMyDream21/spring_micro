package microservices.book.multiplication.challenge;

import microservices.book.multiplication.user.Users;
import microservices.book.multiplication.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
//@DataJpaTest
public class ChallengeServiceTest {
    private ChallengeService challengeService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private ChallengeAttemptRepository attemptRepository;
    @Mock
    private ChallengeEventPub challengeEventPub;

    @BeforeEach
    public void setUp() {
        challengeService = new ChallengeServiceImpl(
                userRepository,
                attemptRepository,
                challengeEventPub
        );
    }

    @Test
    public void checkCorrectAttemptTest() {
        // given
        given(attemptRepository.save(any())).will(returnsFirstArg());
        ChallengeAttemptDTO attemptDTO = new ChallengeAttemptDTO(50, 60, "john_doe", 3000);

        // when
        ChallengeAttempt resultAttempt = challengeService.verifyAttempt(attemptDTO);

        // then
        then(resultAttempt.isCorrect()).isTrue();
        verify(userRepository).save(new Users("john_doe"));
        verify(attemptRepository).save(resultAttempt);
        verify(challengeEventPub).challengeSolved(resultAttempt);
    }

    @Test
    public void checkWrongAttemptTest() {
        // given
        given(attemptRepository.save(any())).will(returnsFirstArg());
        ChallengeAttemptDTO attemptDTO = new ChallengeAttemptDTO(50, 60, "john_doe", 5000);
        // when
        ChallengeAttempt resultAttempt = challengeService.verifyAttempt(attemptDTO);
        // then
        then(resultAttempt.isCorrect()).isFalse();
        verify(userRepository).save(new Users("john_doe"));
        verify(attemptRepository).save(resultAttempt);
        verify(challengeEventPub).challengeSolved(resultAttempt);
    }

    @Test
    public void checkExistingUserTest() {
        // given
        given(attemptRepository.save(any())).will(returnsFirstArg());
        Users existingUsers = new Users(1L, "john_doe");
        given(userRepository.findByAlias("john_doe"))
                .willReturn(Optional.of(existingUsers));
        ChallengeAttemptDTO attemptDTO = new ChallengeAttemptDTO(50, 60, "john_doe", 5000);

        // when
        ChallengeAttempt resultAttempt = challengeService.verifyAttempt(attemptDTO);

        // then
        then(resultAttempt.isCorrect()).isFalse();
        then(resultAttempt.getUsers()).isEqualTo(existingUsers);
        verify(userRepository, never()).save(any());
        verify(attemptRepository).save(resultAttempt);
        verify(challengeEventPub).challengeSolved(resultAttempt);
    }

    @Test
    public void retrieveStatsTest() {
        // given
        Users users = new Users("john_doe");
        ChallengeAttempt attempt1 = new ChallengeAttempt(1L, users, 50, 60, 3010, false);
        ChallengeAttempt attempt2 = new ChallengeAttempt(1L, users, 50, 60, 3051, false);
        List<ChallengeAttempt> lastAttempts = List.of(attempt1, attempt2);
        given(attemptRepository.findTop10ByUsersAliasOrderByIdDesc("john_doe"))
                .willReturn(lastAttempts);

        // when
        List<ChallengeAttempt> latestAttemptsResult = challengeService.getStatsForUser("john_doe");

        // then
        then(latestAttemptsResult).isEqualTo(lastAttempts);
    }
}
