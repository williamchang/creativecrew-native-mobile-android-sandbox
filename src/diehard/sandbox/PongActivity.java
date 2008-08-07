/**
@file
    PongActivity.java
@brief
    Coming soon.
@author
    William Chang
@version
    0.1
@date
    - Created: 2007-11-21
    - Modified: 2007-11-21
    .
@note
    References:
    - General:
        - http://code.google.com/android/samples/ApiDemos/src/com/google/android/samples/app/ForwardTarget.html
        .
    .
*/

package diehard.sandbox;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * Class PongActivity.
 * @author William Chang
 */
public class PongActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        // Mandatory call to the super class.
        super.onCreate(icicle);
        // Remove title bar.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Load XML resource layout into window's default ViewGroup.
        // Must set layout to activity before proceeding to widgets.
        setContentView(R.layout.pong);
        // Instantiate this.
        if(!instantiateThis()) return;
        // Process state.
        if (icicle == null) {
            // New state.
            _pongView.setState(PongView.READY);
        } else {
            // TODO Restore state.
        }
    }
    /** Instantiate this. */
    public boolean instantiateThis() {
        _pongView = (PongView)findViewById(R.id.pong);
        _pongView.setTextView((TextView)findViewById(R.id.status));

        return true;
    }
    /** Set window translucent. */
    public void setWindowTranslucent() {
        // Set system blur any windows behind this one.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        // Apply a tint to any windows behind this one.  Doing a tint this
        // way is more efficient than using a translucent background.  Note
        // that the tint color really should come from a resource.
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.tintBehind = 0x60000820;
        getWindow().setAttributes(lp);
    }

    private PongView _pongView;
}
