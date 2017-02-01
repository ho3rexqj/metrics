package com.codahale.metrics;

/**
 * Factory for creating long adders depending on the runtime. By default it tries to
 * the JDK's implementation and fallbacks to the internal one if the JDK doesn't provide
 * any. The JDK's LongAdder and the internal one don't have a common interface, therefore
 * we adapten them to {@link LongAdderAdapter}, which serves as a common interface for
 * long adders.
 */
public class LongAdderFactory {

    /**
     * A static internal class to avoid NoClassDefFoundError during loading {@link LongAdderFactory}
     */
    private static class JdkDelegate {
        /**
         * @return {@link LongAdderAdapter} backed by the JDK's LongAdder
         */
        private static LongAdderAdapter get() {
            return new LongAdderAdapter() {
                private final java.util.concurrent.atomic.LongAdder longAdder =
                        new java.util.concurrent.atomic.LongAdder();

                @Override
                public void add(long x) {
                    longAdder.add(x);
                }

                @Override
                public long sum() {
                    return longAdder.sum();
                }

                @Override
                public void increment() {
                    longAdder.increment();
                }

                @Override
                public void decrement() {
                    longAdder.decrement();
                }

                @Override
                public long sumThenReset() {
                    return longAdder.sumThenReset();
                }
            };
        }
    }

    /**
     * @return {@link LongAdderAdapter} backed by the internal LongAdder
     */
    private static LongAdderAdapter internalLongAdder() {
        return new LongAdderAdapter() {
            private final LongAdder longAdder = new LongAdder();

            @Override
            public void add(long x) {
                longAdder.add(x);
            }

            @Override
            public long sum() {
                return longAdder.sum();
            }

            @Override
            public void increment() {
                longAdder.increment();
            }

            @Override
            public void decrement() {
                longAdder.decrement();
            }

            @Override
            public long sumThenReset() {
                return longAdder.sumThenReset();
            }
        };
    }

    private static final boolean IS_JDK_LONG_ADDER_AVAILABLE = isIsJdkLongAdderAvailable();
    private static boolean isIsJdkLongAdderAvailable() {
        try {
            JdkDelegate.get();
            return true;
        } catch (NoClassDefFoundError e) {
            return false;
        }
    }

    public static LongAdderAdapter create() {
        return IS_JDK_LONG_ADDER_AVAILABLE ? JdkDelegate.get() : internalLongAdder();
    }
}
