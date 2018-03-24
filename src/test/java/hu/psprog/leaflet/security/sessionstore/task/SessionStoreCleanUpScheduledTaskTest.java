package hu.psprog.leaflet.security.sessionstore.task;

import hu.psprog.leaflet.security.sessionstore.service.SessionStoreService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * Unit tests for {@link SessionStoreCleanUpScheduledTask}.
 *
 * @author Peter Smith
 */
@RunWith(MockitoJUnitRunner.class)
public class SessionStoreCleanUpScheduledTaskTest {

    private static final int THRESHOLD = 1;

    @Mock
    private SessionStoreService sessionStoreService;

    @InjectMocks
    private SessionStoreCleanUpScheduledTask sessionStoreCleanUpScheduledTask;

    @Test
    public void shouldRunScheduledCleanupWhenEnabled() {

        // given
        sessionStoreCleanUpScheduledTask.setThreshold(THRESHOLD);
        sessionStoreCleanUpScheduledTask.setEnabled(true);

        // when
        sessionStoreCleanUpScheduledTask.scheduledCleanup();

        // then
        verify(sessionStoreService).cleanExpiredToken(THRESHOLD);
    }

    @Test
    public void shouldNotRunScheduledCleanupWhenDisabled() {

        // given
        sessionStoreCleanUpScheduledTask.setEnabled(false);

        // when
        sessionStoreCleanUpScheduledTask.scheduledCleanup();

        // then
        verifyZeroInteractions(sessionStoreService);
    }
}