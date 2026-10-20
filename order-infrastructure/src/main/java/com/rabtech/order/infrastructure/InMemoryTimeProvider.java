
package com.rabtech.order.infrastructure;

import com.rabtech.order.application.TimeProvider;
import java.time.Instant;

public class InMemoryTimeProvider implements TimeProvider {

    private final Instant fixedTime;

    public InMemoryTimeProvider(Instant fixedTime) {
        this.fixedTime = fixedTime;
    }

    @Override
    public Instant now() {
        return fixedTime;
    }
}
