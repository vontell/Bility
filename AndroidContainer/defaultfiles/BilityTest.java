package internalbilitytester;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.vontech.bilitytester.BilityTester;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class BilityTest {

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("org.vontech.bilitytestapplication", appContext.getPackageName());

        BilityTester.testTester();

        URL url = new URL("http://10.0.2.2:8080/internal/test");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setDoOutput(false);
        InputStream is = con.getInputStream();
        is.close();

    }

}