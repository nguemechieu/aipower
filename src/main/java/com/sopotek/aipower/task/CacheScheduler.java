
package  com.sopotek.aipower.task;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CacheScheduler {

    @CacheEvict(value = "users", allEntries = true)
    @Scheduled(fixedRate = 60000) // Every 60 seconds
    public void clearAllUserCache() {
        System.out.println("Cache cleared on schedule.");
    }
}
