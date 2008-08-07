/**
@file
    ConsoleActivity.java
@brief
    Coming soon.
@author
    William Chang
@version
    0.1
@date
    - Created: 2007-11-16
    - Modified: 2007-11-23
    .
@note
    References:
    - General:
        - http://code.google.com/android/samples/ApiDemos/src/com/google/android/samples/app/ForwardTarget.html
        .
    - XML:
        - http://code.google.com/android/reference/android/view/ViewGroup.LayoutParams.html
        .
    - Annotations:
        - http://java.sun.com/docs/books/tutorial/java/javaOO/annotations.html
        .
    .
*/

package diehard.sandbox;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

/**
 * Class ConsoleActivity.
 * @author William Chang
 */
public class ConsoleActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        // Mandatory call to the super class.
        super.onCreate(icicle);

        // Load XML resource layout into window's default ViewGroup.
        setContentView(R.layout.console);
        // Create widgets.
        _rlConsole = (RelativeLayout)findViewById(R.id.container_console);
        _etConsole = (EditText)findViewById(R.id.console);
        _btnExecute = (Button)findViewById(R.id.execute);
        // Set listeners.
        _etConsole.setOnKeyListener(_key);
        _btnExecute.setOnClickListener(_click);
        // Instantiate this.
        if(!instantiateThis()) return;
    }
    /** Instantiate this. */
    public boolean instantiateThis() {
        _state = STATE_VISIBLE_TRUE;
        _counter = 0;
        _pressedEnter = false;
        _strCommandLabel = "cmd>";
        _etConsole.append(_strCommandLabel);
        // XXX Double key event on enter: _etConsole.requestFocus();

        return true;
    }
    /** Run user input */
    public void runUserInput(String s) {
        String userInput = s.toLowerCase();
        String msgError = " is not recognized as a command.\n";
        String msgExecuted = " executed.\n";

        if(userInput.equals("hello world")) {
            updateOutput("\n\n\'" + userInput + "\'" + msgExecuted);
            updateOutput("\nWorld replied hello user.\n");
        } else if(userInput.equals("pong")) {
            runIntentActivity(PongActivity.class);
            updateOutput("\n\n\'" + userInput + "\'" + msgExecuted);
        } else if(userInput.equals("mod")) {
            updateOutput("\n\n\'" + userInput + "\'" + msgExecuted);
            updateOutput("\n12 % 10 = " + (12 % 10) + "\n");
            updateOutput("8 % 10 = " + (8 % 10) + "\n");
            updateOutput("Bound 2 = " + (2 & 0x00FF) + "\n");
            updateOutput("Bound 3 = " + (3 & 0x00FF) + "\n");
            updateOutput("Bound 4 = " + (4 & 0x00FF) + "\n");
            updateOutput("Bound 5 = " + (5 & 0x00FF) + "\n");
            updateOutput("Bound 8 = " + (8 & 0x00FF) + "\n");
            updateOutput("Bound 15 = " + (15 & 0x00FF) + "\n");
            updateOutput("Bound 7937 = " + (7937 & 0x00FF) + "\n");
        } else {
            updateOutput("\n\n\'" + userInput + "\'" + msgError);
            return;
        }
    }
    /** Run intent to goto the next activity. */
    private void runIntentActivity(Class<?> c) {
        // Create intent.
        Intent itt = new Intent();
        // Set this activity to open the next activity.
        itt.setClass(ConsoleActivity.this, c);
        // Start the next activity.
        startActivity(itt);
    }
    /** Get user input. */
    public String getUserInput() {
        String s;
        int max = _etConsole.getText().length() - 1;
        int i = 0;
        char c;

        for(i=max;i>0;i--) {
            c = _etConsole.getText().charAt(i);
            if(c == '\n') {
                i++;
                break;
            }
        }
        s = _etConsole.getText().subSequence(i, max + 1).toString();
        if(s.equals(_strCommandLabel)) {
            return "";
        } else {
            return _etConsole.getText().subSequence(i + _strCommandLabel.length(), max + 1).toString();
        }
    }
    /** Update output */
    public void updateOutput(String s) {
        _etConsole.append(s);
    }
    /** Create context menu. */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Mandatory call to the super class.
        super.onCreateOptionsMenu(menu);
        // Add toggle console item to menu.
        menu.add(0, CONSOLE_ID, R.string.contextmenu_console).setShortcut('0', 'c');

        return true;
    }
    /** Event item selected in context menu. */
    @Override
    public boolean onOptionsItemSelected(Menu.Item item) {
        // Handle all of the possible menu actions.
        switch(item.getId()) {
            case CONSOLE_ID: {
                if(_state == STATE_VISIBLE_TRUE) {
                    Animation anim = AnimationUtils.loadAnimation(this, android.R.anim.push_up_out);
                    _rlConsole.startAnimation(anim);
                    _rlConsole.setVisibility(View.INVISIBLE);
                    _state = STATE_VISIBLE_FALSE;
                } else if(_state == STATE_VISIBLE_FALSE) {
                    Animation anim = AnimationUtils.loadAnimation(this, android.R.anim.push_down_in);
                    _rlConsole.startAnimation(anim);
                    _rlConsole.setVisibility(View.VISIBLE);
                    _state = STATE_VISIBLE_TRUE;
                }
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /// Event on key as an anonymous class.
    private OnKeyListener _key = new OnKeyListener() {
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if(keyCode == KeyEvent.KEYCODE_NEWLINE && _pressedEnter == false) {
                //updateOutput("\n\nInput: \"" + getUserInput() + "\"\n\n" + _strCommandLabel);
                runUserInput(getUserInput());
                updateOutput("\n" + _strCommandLabel);
                _pressedEnter = true;
                return true;
            } else {
                _pressedEnter = false;
            }

            return false;
        }
    };
    /// Event on click as an anonymous class.
    private OnClickListener _click = new OnClickListener() {
        public void onClick(View v) {
            for(int i=0;i<64;i++) {
                _etConsole.append("Line " + _counter + "\n");
                _counter++;
            }
        }
    };
    /// Console view.
    private RelativeLayout _rlConsole;
    /// Input widget.
    private EditText _etConsole;
    /// Execute button.
    private Button _btnExecute;

    // State.
    private static final int STATE_VISIBLE_FALSE = 0;
    private static final int STATE_VISIBLE_TRUE = 1;
    // Identifiers for menu items.
    private static final int CONSOLE_ID = Menu.FIRST;

    // Primitive data types.
    private String _strCommandLabel;
    private int _counter;
    private int _state;
    private boolean _pressedEnter;
}
