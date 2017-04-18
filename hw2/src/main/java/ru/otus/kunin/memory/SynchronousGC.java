package ru.otus.kunin.memory;

import static com.sun.management.GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION;

import com.sun.management.GarbageCollectionNotificationInfo;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import javax.management.ListenerNotFoundException;
import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;

public class SynchronousGC {

  final static boolean logsEnabled = false;
  static List leakingListeners = new ArrayList<>(1024);

  public static void collect() {
    final List<GarbageCollectorMXBean> garbageCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
    final int latchCount = garbageCollectorMXBeans.size();
    final CountDownLatch latch = new CountDownLatch(latchCount);
    for (GarbageCollectorMXBean mxBean : garbageCollectorMXBeans) {
      final NotificationEmitter emitter = (NotificationEmitter) mxBean;
      final NotificationListener listener = (notification, handbackListener)
          -> handleNotification(latch, emitter, notification, handbackListener);
      leakingListeners.add(listener);
      emitter.addNotificationListener(listener, null, listener);
    }
    System.gc();
    try {
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private static void handleNotification(
      CountDownLatch latch,
      NotificationEmitter notificationEmitter,
      Notification notification,
      Object handbackListener) {
    if (GARBAGE_COLLECTION_NOTIFICATION.equals(notification.getType())) {
      final GarbageCollectionNotificationInfo notificationInfo =
          GarbageCollectionNotificationInfo.from((CompositeData) notification.getUserData());
      final String gcCause = notificationInfo.getGcCause();
      if ("System.gc()".equals(gcCause)) {
        if (logsEnabled) {
          System.out.printf("%s - %s\n", notificationInfo.getGcName(), notificationInfo.getGcAction());
        }
        latch.countDown();
        try {
          notificationEmitter.removeNotificationListener((NotificationListener) handbackListener);
        } catch (ListenerNotFoundException e) {
          e.printStackTrace();
        }
      }
    }
  }

}
