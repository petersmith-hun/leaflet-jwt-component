package hu.psprog.leaflet.security.sessionstore.task;

import hu.psprog.leaflet.security.sessionstore.service.SessionStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Configuration for scheduled session store cleanup.
 *
 * @author Peter Smith
 */
@Component
@ConfigurationProperties(prefix = "session-store.auto-cleanup")
public class SessionStoreCleanUpScheduledTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionStoreCleanUpScheduledTask.class);

    private boolean enabled;
    private int threshold;

    private SessionStoreService sessionStoreService;

    @Autowired
    public SessionStoreCleanUpScheduledTask(SessionStoreService sessionStoreService) {
        this.sessionStoreService = sessionStoreService;
    }

    @Scheduled(cron = "${session-store.auto-cleanup.schedule}")
    public void scheduledCleanup() {
        if (enabled) {
            sessionStoreService.cleanExpiredToken(threshold);
        } else {
            LOGGER.warn("Session Store scheduled cleanup is currently disabled.");
        }
    }

    /**
     * Enables scheduled cleanup.
     *
     * @param enabled task status
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Sets threshold in minutes. If a token is expired earlier than the threshold, then it will be removed.
     *
     * @param threshold threshold in minutes.
     */
    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }
}
