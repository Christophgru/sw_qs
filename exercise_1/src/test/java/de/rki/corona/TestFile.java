package de.rki.corona;

import static org.mockito.Mockito.*;

import java.lang.reflect.Method;

import de.rki.corona.database.CoronaDatabase;
import de.rki.corona.time.TimeService;
import de.rki.corona.bluetooth.BluetoothService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CoronaAppTest {

    @Mock
    BluetoothService bluetooth;
    @Mock
    CoronaDatabase database;
    @Mock
    TimeService time;

    CoronaApp app;

    private Method updateEncounter;

    @BeforeEach
    void setUp() throws Exception {
        app = new CoronaApp(bluetooth, database, time);
        // Reflection Handle on private method (wtf Java???)
        updateEncounter = CoronaApp.class.getDeclaredMethod("updateEncounter", String.class);
        updateEncounter.setAccessible(true);
    }

    /** Call updateEncounter twice for the same hash and two time values */
    private void tickTwice(String hash, long t1, long t2) {
        when(time.currentTimeMillis()).thenReturn(t1, t2);
        try {
            updateEncounter.invoke(app, hash);
            updateEncounter.invoke(app, hash);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Nested
    class NoContact {
        @Test
        void noTick_none() {
            //updateEncounter will not be called if there is no bluetooth device nearby
            //No Tick => NONE
            Assertions.assertNull(app.getSeverity());
        }
    }

    @Nested
    class UninfectedOnly {
        @Test
        void uninfectedShort_none() {
            when(database.isInfected("H_SAFE")).thenReturn(false);
            tickTwice("H_SAFE", 0L, 1000L); //1s uninfected => NONE
            Assertions.assertEquals(CoronaApp.Severity.NONE, app.getSeverity());
        }

        @Test
        void uninfectedLong_none() {
            when(database.isInfected("H_SAFE")).thenReturn(false);
            tickTwice("H_SAFE", 0L, 6000L); // 6s uninfected => NONE
            Assertions.assertEquals(CoronaApp.Severity.NONE, app.getSeverity());
        }
    }

    @Nested
    class InfectedOnly {
        @Test
        void infectedShort_safeContact() {
            when(database.isInfected("H_INFECTED")).thenReturn(true);
            tickTwice("H_INFECTED", 0L, 1000L); // 1s infected => SAFE_CONTACT
            Assertions.assertEquals(CoronaApp.Severity.SAFE_CONTACT, app.getSeverity());
        }

        @Test
        void infectedLong_infectious() {
            when(database.isInfected("H_INFECTED")).thenReturn(true);
            tickTwice("H_INFECTED", 0L, 6000L); // 6s infected => INFECTIOUS_CONTACT
            Assertions.assertEquals(CoronaApp.Severity.INFECTIOUS_CONTACT, app.getSeverity());
        }
    }

    @Nested
    class Mixed {
        @Test
        void uninfectedShort_infectedShort_safeContact() {
            when(database.isInfected("H_SAFE")).thenReturn(false);
            when(database.isInfected("H_INFECTED")).thenReturn(true);

            // beide kurz: jeweils 1s
            tickTwice("H_SAFE", 0L, 1000L);
            tickTwice("H_INFECTED", 2000L, 3000L); // SAFE_CONTACT
            Assertions.assertEquals(CoronaApp.Severity.SAFE_CONTACT, app.getSeverity());
        }

        @Test
        void uninfectedLong_infectedShort_safeContact() {
            when(database.isInfected("H_SAFE")).thenReturn(false);
            when(database.isInfected("H_INFECTED")).thenReturn(true);

            tickTwice("H_SAFE", 0L, 6000L);
            tickTwice("H_INFECTED", 7000L, 8000L); // SAFE_CONTACT
            Assertions.assertEquals(CoronaApp.Severity.SAFE_CONTACT, app.getSeverity());
        }

        @Test
        void uninfectedShort_infectedLong_infectious() {
            when(database.isInfected("H_SAFE")).thenReturn(false);
            when(database.isInfected("H_INFECTED")).thenReturn(true);

            tickTwice("H_SAFE", 0L, 1000L);
            tickTwice("H_INFECTED", 2000L, 8000L); // INFECTIOUS_CONTACT
            Assertions.assertEquals(CoronaApp.Severity.INFECTIOUS_CONTACT, app.getSeverity());
        }

        @Test
        void uninfectedLong_infectedLong_infectious() {
            when(database.isInfected("H_SAFE")).thenReturn(false);
            when(database.isInfected("H_INFECTED")).thenReturn(true);

            tickTwice("H_SAFE", 0L, 6000L);
            tickTwice("H_INFECTED", 7000L, 13000L); // INFECTIOUS_CONTACT
            Assertions.assertEquals(CoronaApp.Severity.INFECTIOUS_CONTACT, app.getSeverity());
        }
    }
}
