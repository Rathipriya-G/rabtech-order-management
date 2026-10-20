
package com.rabtech.order.application;

import java.time.Instant;

public interface TimeProvider {

    Instant now();
}
