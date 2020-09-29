package com.example.system.nablarch.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.example.domain.model.AccountId;
import com.example.domain.model.Payload;

import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;

@SystemRepositoryComponent
public class NotifierManager {

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Map<String, ComplexNotifier> notifiers = new HashMap<>();
    private final NullNotifier nullNotifier = new NullNotifier();

    public void add(AccountId accountId, Notifier notifier) {
        Lock lock = readWriteLock.writeLock();
        lock.lock();
        try {
            ComplexNotifier complexNotifier = notifiers.get(accountId.value());
            if (complexNotifier == null) {
                complexNotifier = new ComplexNotifier();
                notifiers.put(accountId.value(), complexNotifier);
            }
            complexNotifier.add(notifier);
        } finally {
            lock.unlock();
        }
    }

    public void remove(AccountId accountId, Notifier notifier) {
        Lock lock = readWriteLock.writeLock();
        lock.lock();
        try {
            ComplexNotifier complexNotifier = notifiers.get(accountId.value());
            if (complexNotifier != null) {
                complexNotifier.remove(notifier);
            }
        } finally {
            lock.unlock();
        }
    }

    public Notifier find(AccountId accountId) {
        Lock lock = readWriteLock.readLock();
        lock.lock();
        try {
            ComplexNotifier notifier = notifiers.get(accountId.value());
            if (notifier != null) {
                return notifier;
            }
            return nullNotifier;
        } finally {
            lock.unlock();
        }
    }

    private static class NullNotifier implements Notifier {

        @Override
        public void notify(Payload payload) {
        }
    }

    private static class ComplexNotifier implements Notifier {

        private final List<Notifier> notifiers = new ArrayList<>();

        @Override
        public void notify(Payload payload) {
            notifiers.forEach(notifier -> notifier.notify(payload));
        }

        public void add(Notifier notifier) {
            notifiers.add(notifier);
        }

        public void remove(Notifier notifier) {
            notifiers.remove(notifier);
        }
    }
}
