package com.sopotek.aipower.routes;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ResourcesController {
    @GetMapping("/metrics/threads")
    public Map<String, Object> getThreadMetrics() {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        long[] deadlockedThreads = threadMXBean.findDeadlockedThreads();
        if (deadlockedThreads != null) {
            System.out.println("Deadlocked threads detected:");
            for (long threadId : deadlockedThreads) {
                ThreadInfo info = threadMXBean.getThreadInfo(threadId);
                System.out.println("Thread Name: " + info.getThreadName());
                System.out.println("Thread State: " + info.getThreadState());
            }
        }


        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalThreads", threadMXBean.getThreadCount());
        metrics.put("daemonThreads", threadMXBean.getDaemonThreadCount());
        metrics.put("peakThreads", threadMXBean.getPeakThreadCount());
        metrics.put("totalStartedThreads", threadMXBean.getTotalStartedThreadCount());
        return metrics;
    }

}
