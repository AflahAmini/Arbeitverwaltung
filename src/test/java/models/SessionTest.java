package models;

import arbyte.models.Session;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SessionTest {
    @Test
    void shouldReturnElapsedTimeFromStart() {
        Session session = new Session();
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Assertions.assertEquals(session.getDuration().getSeconds(), 2);

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Assertions.assertEquals(session.getDuration().getSeconds(), 3);
    }

    @Test
    void shouldTogglePause() {
        Session session = new Session();
        session.toggleSessionPause();

        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Assertions.assertTrue(session.isPaused());
        Assertions.assertEquals(session.getDuration().getSeconds(), 0);

        session.toggleSessionPause();

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Assertions.assertFalse(session.isPaused());
        Assertions.assertEquals(session.getDuration().getSeconds(), 1);
    }
}
