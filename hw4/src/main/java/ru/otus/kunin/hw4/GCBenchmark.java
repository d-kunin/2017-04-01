package ru.otus.kunin.hw4;

import com.sun.management.GarbageCollectionNotificationInfo;

import javax.management.ListenerNotFoundException;
import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static com.sun.management.GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION;

public class GCBenchmark implements NotificationListener {

    private volatile boolean isActive = false;

    private volatile String yName;
    private final AtomicLong yGenCount = new AtomicLong(0);
    private final AtomicLong yGenDuration = new AtomicLong(0);

    private volatile String oName;
    private final AtomicLong oGenCount = new AtomicLong(0);
    private final AtomicLong oGenDuration = new AtomicLong(0);

    public void start() {
        subscribeToGC();
        isActive = true;
        final long startTime = System.currentTimeMillis();
        final boolean leakedUntilOOM = MemoryLeak.leak();
        if (!leakedUntilOOM) {
            throw new IllegalStateException("OOM didn't happen, try to tune params in MemoryLeak or/and Xmx");
        }
        isActive = false;
        final long timeAlive = System.currentTimeMillis() - startTime;
        unsubscribeFromGC();

        report(timeAlive);
    }

    private void report(long timeAlive) {
        System.out.println("Time alive: " + timeAlive+ "ms (" + TimeUnit.MILLISECONDS.toMinutes(timeAlive) + " minutes)");
        System.out.println("Collectors: " + yName + " and " + oName);
        final long totalGcCount = yGenCount.get() + oGenCount.get();
        System.out.println("Total GC runs: " + totalGcCount);
        final long totalGcDuration = yGenDuration.get() + oGenDuration.get();
        System.out.println("Total GC duration: " + totalGcDuration + "ms");

        System.out.println("Young Gen GC runs: " + yGenCount.get());
        System.out.println("Young Gen GC duration: " + yGenDuration.get() + "ms");

        System.out.println("Old Gen GC runs: " + oGenCount.get());
        System.out.println("Old Gen GC duration: " + oGenDuration.get() + "ms");

        final double gcPerMinute = (double) totalGcCount * TimeUnit.MINUTES.toMillis(1) / timeAlive;
        System.out.println("Collections per minute: " + gcPerMinute);

        final double shareOfTimeCollecting = 100 * (double) totalGcDuration / timeAlive;
        System.out.println("Share of time spend collecting: " + shareOfTimeCollecting + "%");
    }

    @Override
    public void handleNotification(Notification notification, Object handback) {
        if (!isActive) {
            return;
        }
        if (GARBAGE_COLLECTION_NOTIFICATION.equals(notification.getType())) {
            final GarbageCollectionNotificationInfo notificationInfo =
                    GarbageCollectionNotificationInfo.from((CompositeData) notification.getUserData());
            String gctype = notificationInfo.getGcAction();
            if ("end of minor GC".equals(gctype)) {
                yName = notificationInfo.getGcName();
                yGenCount.incrementAndGet();
                yGenDuration.addAndGet(notificationInfo.getGcInfo().getDuration());
            } else if ("end of major GC".equals(gctype)) {
                oName = notificationInfo.getGcName();
                oGenCount.incrementAndGet();
                oGenDuration.addAndGet(notificationInfo.getGcInfo().getDuration());
            }
        }
    }

    private void unsubscribeFromGC() {
        final List<GarbageCollectorMXBean> garbageCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
        for (GarbageCollectorMXBean mxBean : garbageCollectorMXBeans) {
            final NotificationEmitter emitter = (NotificationEmitter) mxBean;
            try {
                emitter.removeNotificationListener(this);
            } catch (ListenerNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void subscribeToGC() {
        final List<GarbageCollectorMXBean> garbageCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
        for (GarbageCollectorMXBean mxBean : garbageCollectorMXBeans) {
            final NotificationEmitter emitter = (NotificationEmitter) mxBean;
            emitter.addNotificationListener(this, null, null);
        }
    }
}
