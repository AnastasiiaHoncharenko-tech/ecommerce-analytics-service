package com.ecommerce.analytics.util;

import static com.ecommerce.analytics.constants.AnalyticsConstants.*;

public final class ValidationUtils {

    private ValidationUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static int validateAndNormalizeLimit(Integer limit) {
        if (limit == null || limit < MIN_LIMIT) {
            return DEFAULT_LIMIT;
        }
        if (limit > MAX_LIMIT) {
            return MAX_LIMIT;
        }
        return limit;
    }
}
