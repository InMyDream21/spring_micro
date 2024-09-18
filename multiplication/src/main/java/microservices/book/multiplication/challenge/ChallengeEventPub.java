package microservices.book.multiplication.challenge;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ChallengeEventPub {
    private final AmqpTemplate amqpTemplate;
    private final String challengeTopicExchange;

    public ChallengeEventPub(final AmqpTemplate amqpTemplate, @Value("${amqp.exchange.attempts}") final String challengeTopicExchange) {
        this.amqpTemplate = amqpTemplate;
        this.challengeTopicExchange = challengeTopicExchange;
    }

    public void challengeSolved(final ChallengeAttempt challengeAttempt) {
        ChallengeSolvedEvent event = buildEvent(challengeAttempt);
        // Routing Key is attempt.correct or attempt.wrong
        String routingKey = "attempt." + (event.isCorrect() ? "correct" : "wrong");
        amqpTemplate.convertAndSend(challengeTopicExchange, routingKey, event);
    }

    private ChallengeSolvedEvent buildEvent(final ChallengeAttempt attempt) {
        return new ChallengeSolvedEvent(
                attempt.getId(),
                attempt.isCorrect(),
                attempt.getFactorA(),
                attempt.getFactorB(),
                attempt.getUsers().getId(),
                attempt.getUsers().getAlias()
                );
    }
}
