package net.sunken.common.packet.expectation;

import com.google.inject.Inject;
import lombok.NonNull;
import net.sunken.common.event.EventManager;
import net.sunken.common.packet.Packet;

import java.util.function.Predicate;

public class ExpectationFactory {

    @Inject
    private EventManager eventManager;

    public boolean waitFor(@NonNull Predicate<Packet> condition, int iterationsBeforeTimeout, int wait) {
        final Expectation expectation = new Expectation(condition);
        eventManager.register(expectation);

        int iterations = 0;
        while (!expectation.isMet()) {
            if (iterations >= iterationsBeforeTimeout) {
                eventManager.unregister(expectation);
                return false; // Didn't meet expectation, we timed out.
            }

            try {
                Thread.sleep(wait);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            iterations++;
        }

        eventManager.unregister(expectation);
        return true;
    }
}
