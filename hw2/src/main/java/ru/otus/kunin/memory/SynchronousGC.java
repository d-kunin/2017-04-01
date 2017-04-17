package ru.otus.kunin.memory;

import com.sun.management.GarbageCollectionNotificationInfo;

import javax.management.ListenerNotFoundException;
import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static com.sun.management.GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION;

public class SynchronousGC {

    public static void collect() {
        final CountDownLatch latch = seUpListener();
        System.gc();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static CountDownLatch seUpListener() {
        final List<GarbageCollectorMXBean> garbageCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
        final CountDownLatch latch = new CountDownLatch(garbageCollectorMXBeans.size());
        for (GarbageCollectorMXBean mxBean : garbageCollectorMXBeans) {
            final NotificationEmitter notificationEmitter = (NotificationEmitter) mxBean;
            final NotificationListener notificationListener =
                    (notification, handbackListener) -> handleNotification(
                            latch,
                            notificationEmitter,
                            notification,
                            (NotificationListener) handbackListener);
            notificationEmitter.addNotificationListener(
                    notificationListener,
                    null,
                    notificationListener);
        }
        return latch;
    }

    private static void handleNotification(
            CountDownLatch latch,
            NotificationEmitter notificationEmitter,
            Notification notification,
            NotificationListener handbackListener) {
        if (GARBAGE_COLLECTION_NOTIFICATION.equals(notification.getType())) {
            final GarbageCollectionNotificationInfo notificationInfo =
                    GarbageCollectionNotificationInfo.from((CompositeData) notification.getUserData());
            final String gcCause = notificationInfo.getGcCause();
            System.out.println(notificationInfo.getGcName() + " " + gcCause);
            if ("System.gc()".equals(gcCause)) {
                latch.countDown();
                try {
                    notificationEmitter.removeNotificationListener(handbackListener);
                } catch (ListenerNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
