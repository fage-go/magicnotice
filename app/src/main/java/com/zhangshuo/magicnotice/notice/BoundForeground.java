package com.zhangshuo.magicnotice.notice;

/** Main-thread state for opt-in foreground promotion of an already bound listener. */
final class BoundForeground {
    interface Eligibility {
        boolean allowsPromotion();
    }

    interface Host {
        /** Null means the platform state could not be observed. */
        Boolean isForeground();

        void promote();
        void demote();
        void failed(String action, RuntimeException error);
    }

    private boolean mayBeForeground;
    private boolean promotionCallReturned;

    void reconcile(Eligibility eligibility, boolean retryUnverified, Host host) {
        if (!eligibility.allowsPromotion()) {
            demoteIfNeeded(host);
            return;
        }

        Boolean observed = null;
        if (mayBeForeground) {
            try {
                observed = host.isForeground();
            } catch (RuntimeException error) {
                host.failed("inspect", error);
            }
        }

        // Re-read the current switch/binding after any framework observation and
        // immediately before deciding whether to keep or enter the foreground.
        if (!eligibility.allowsPromotion()) {
            demoteIfNeeded(host);
            return;
        }
        if (Boolean.TRUE.equals(observed)
                || (mayBeForeground && observed == null && promotionCallReturned && !retryUnverified)) return;
        // A framework call can be ignored or fail partway through. Keep cleanup eligible
        // in either case and never treat a returned call as proof of OS state.
        mayBeForeground = true;
        promotionCallReturned = false;
        try {
            host.promote();
            promotionCallReturned = true;
        } catch (RuntimeException error) {
            host.failed("promote", error);
        }
    }

    private void demoteIfNeeded(Host host) {
        if (!mayBeForeground) return;
        try {
            host.demote();
            mayBeForeground = false;
            promotionCallReturned = false;
        } catch (RuntimeException error) {
            host.failed("demote", error);
        }
    }
}
