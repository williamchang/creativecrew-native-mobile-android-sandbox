/**
@file
    MainActivity.java
@brief
    Coming soon.
@author
    William Chang
@version
    0.1
@date
    - Created: 2007-11-16
    - Modified: 2007-11-20
    .
@note
    References:
    - General:
        - http://code.google.com/android/samples/ApiDemos/src/com/google/android/samples/app/Forwarding.html
        .
    - XML:
        - http://code.google.com/android/reference/android/view/ViewGroup.LayoutParams.html
        .
    - Java:
        - http://java.sun.com/docs/books/tutorial/java/javaOO/annotations.html
        - http://www.roseindia.net/javatutorials/anonymous_innerclassestutorial.shtml
        - http://www.geom.uiuc.edu/~daeron/docs/javaguide/java/threads/simple.html
        .
    .
*/

package diehard.sandbox;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

/**
 * Class MainActivity.
 * @author William Chang
 */
public class MainActivity extends Activity implements View.OnClickListener {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        // Mandatory call to the super class.
        super.onCreate(icicle);

        // Remove title bar.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Load XML resource layout into window's default ViewGroup.
        // Must set layout to activity before proceeding to widgets.
        setContentView(R.layout.main);
        // Create button.
        _btnIntroduction = (Button)findViewById(R.id.introduction);
        // Set listener to button.
        _btnIntroduction.setOnClickListener(this);
    }
    /** Event on click. */
    public void onClick(View v) {
        // Create intent.
        Intent itt = new Intent();
        // Set this activity to open the next activity.
        itt.setClass(MainActivity.this, ConsoleActivity.class);
        // Start the next activity.
        startActivity(itt);
        // Create animation.
        Animation anim = AnimationUtils.loadAnimation(this, android.R.anim.app_exit);
        _timer = anim.getDuration();
        // Start animation.
        v.startAnimation(anim);
        // Create thread as an anonymous class.
        new Thread() {
            public void run() {
                try {
                    // Sleep thread in milliseconds.
                    sleep(_timer + 5000);
                    // Remove this activity from histroy stack.
                    finish();
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /// Execute button.
    private Button _btnIntroduction;

    // Primitive data types.
    private long _timer;
}