package com.sopotek.aipower.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ThreadMonitorService {
private  static final Logger LOG = Logger.getLogger(ThreadMonitorService.class.getName());
    /**
     * Scheduled task to monitor thread activities.
     * Runs every 5 minutes.
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void monitorThreads() {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
LOG.info("Monitoring");
LOG.config(
        "Thread Monitor Service started. Monitoring every 5 minutes." +
        " JVM Name: " + ManagementFactory.getRuntimeMXBean().getVmName() +
        ", JVM Vendor: " + ManagementFactory.getRuntimeMXBean().getVmVendor() +
        ", JVM Version: " + ManagementFactory.getRuntimeMXBean().getVmVersion() +
        ", Java Version: " + ManagementFactory.getRuntimeMXBean().getVmVersion() +
        ", Java Home: " + ManagementFactory.getRuntimeMXBean().getBootClassPath() +
        ", Thread Count: " + threadMXBean.getThreadCount() +


                ", Threads count : " + threadMXBean.getThreadCount() +
                ", Threads in Deadlocked State: " + threadMXBean.isThreadContentionMonitoringEnabled()

);
LOG.log(Level.FINE, "Monitoring...");
LOG.setResourceBundle(ResourceBundle.getBundle(ThreadMonitorService.class.getName()));
        // Get all thread information
        ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(true, true);

        for (ThreadInfo threadInfo : threadInfos) {
            if (threadInfo == null) {
                continue;
            }

            // Log threads in BLOCKED or TIMED_WAITING states
            if (threadInfo.getThreadState() == Thread.State.BLOCKED ||
                    threadInfo.getThreadState() == Thread.State.TIMED_WAITING) {
                LOG.log(Level.ALL,"Detected thread issue:");
                LOG.log(Level.ALL,"Thread Id: " + threadInfo.getThreadId());
                LOG.log(Level.ALL,"Thread Name: " + threadInfo.getThreadName());
                LOG.log(Level.ALL,"Thread State: " + threadInfo.getThreadState());
                LOG.log(Level.ALL,"Blocked Count: " + threadInfo.getBlockedCount());
                LOG.log(Level.ALL,"Lock Info: " + threadInfo.getLockName());
                LOG.log(Level.ALL,"Stack Trace:");
                for (StackTraceElement stackTraceElement : threadInfo.getStackTrace()) {
                    LOG.log(Level.ALL,"\t" + stackTraceElement);
                }
            }
        }
    }
}
