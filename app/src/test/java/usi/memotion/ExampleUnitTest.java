package usi.memotion;

import org.junit.Test;


import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 31);
        c.add(Calendar.DAY_OF_MONTH, 1);

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyy  HH:mm:ss");
        System.out.println("Scheduled scheduler at " + format.format(c.getTime()));
    }
}