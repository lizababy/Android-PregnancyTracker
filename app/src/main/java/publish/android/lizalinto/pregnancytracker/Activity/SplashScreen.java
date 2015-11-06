package publish.android.lizalinto.pregnancytracker.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;

/**
 * This activity is the launcher activity showing the users the logo and app themes**/
/* set to show for 1sec
  * */
public class SplashScreen extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(publish.android.lizalinto.pregnancytracker.R.layout.activity_splash_screen);


        // Splash screen timer
        int SPLASH_TIME_OUT = 1000;

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer.
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over

                Intent i = new Intent(SplashScreen.this, HomeActivity.class);
                startActivity(i);


                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);

    }


}
