package com.ssos.learn.concurrent.disruptor;

import com.lmax.disruptor.EventFactory;

public class LongEventFactory implements EventFactory<LongEventFactory.LongEvent> {
    @Override
    public LongEventFactory.LongEvent newInstance() {
        return new LongEvent();
    }

    static class LongEvent {
        long value;

        public void set(long value) {
            this.value = value;
        }

    }
}
