package ru.otus.kunin.hw4;

import com.sun.management.GarbageCollectionNotificationInfo;

import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;
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
        final boolean leakedUntilOOM = MemoryLeak.leak();
        isActive = false;
        System.out.println("Collectors: " + yName + " and " + oName);
        System.out.println("Total GC runs: " + (yGenCount.get() + oGenCount.get()));
        System.out.println("Total GC duration: " + (yGenDuration.get() + oGenDuration.get()) + "ms");
    }

    private void subscribeToGC() {
        final List<GarbageCollectorMXBean> garbageCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
        for (GarbageCollectorMXBean mxBean : garbageCollectorMXBeans) {
            final NotificationEmitter emitter = (NotificationEmitter) mxBean;
            emitter.addNotificationListener(this, null, null);
        }
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
}
