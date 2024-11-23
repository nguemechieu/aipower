
package  com.sopotek.aipower.task;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CacheScheduler {
    private static final Log LOG = LogFactory.getLog(CacheScheduler.class);

    @CacheEvict(value = "users", allEntries = true)
    @Scheduled(fixedRate = 60000) // Every 60 seconds
    public void clearAllUserCache() {
        LOG.info("Cache cleared on schedule.");
    }
}
