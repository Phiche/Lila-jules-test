package com.example.common;

// Assuming TryValue is available in com.example.common
// Kamon related parts are placeholders/omitted for now

public class LapTry<A> {
    private final TryValue<A> result;
    private final long nanos;

    public LapTry(TryValue<A> result, long nanos) {
        this.result = result;
        this.nanos = nanos;
    }

    public TryValue<A> getResult() { return result; }
    public long getNanos() { return nanos; }

    public int getMillis() { return (int) (nanos / 1_000_000L); }

    // mon method would require KamonTimerPlaceholder and proper TryValue handling
    // public LapTry<A> mon(Function<TryValue<A>, KamonTimerPlaceholder> pathFunction) {
    //     pathFunction.apply(result).record(nanos);
    //     return this;
    // }
}
