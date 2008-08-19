/**
@file
    ConsoleActivity.java
@brief
    Copyright 2008 Creative Crew. All rights reserved.
@author
    William Chang
@version
    0.1
@date
    - Created: 2007-11-16
    - Modified: 2008-08-18
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
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Class ConsoleActivity.
 * @author William Chang
 */
public class ConsoleActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Mandatory call to the super class.
        super.onCreate(savedInstanceState);

        // Load XML resource layout into window's default ViewGroup.
        setContentView(R.layout.console);
        // Create widgets.
        _rlContainer = (RelativeLayout)findViewById(R.id.console_container);
        _etInput = (EditText)findViewById(R.id.console_input);
        _tvOutput = (TextView)findViewById(R.id.console_output);
        _btnExecute = (Button)findViewById(R.id.console_execute);
        // Set listeners.
        _etInput.setOnKeyListener(_key);
        _btnExecute.setOnClickListener(_click);
        // Instantiate this.
        if(!instantiateThis()) return;
    }
    /** Instantiate this. */
    public boolean instantiateThis() {
        _state = STATE_VISIBLE_TRUE;
        _counter = 0;
        _pressedEnter = false;

        return true;
    }
    /** Run user input */
    public void runUserInput(String s) {
        String userInput = s.toLowerCase();
        String msgError = " is not recognized as a command.\n";
        String msgExecuted = " executed.\n";

        this.updateOutput("$ " + s + "\n");

        if(userInput.equals("hello world")) {
            updateOutput("\'" + userInput + "\'" + msgExecuted);
            updateOutput("System replied hello user.\n");
        } else if(userInput.equals("pong")) {
            runIntentActivity(PongActivity.class);
            updateOutput("\'" + userInput + "\'" + msgExecuted);
        } else if(userInput.equals("mod")) {
            updateOutput("\'" + userInput + "\'" + msgExecuted);
            updateOutput("12 % 10 = " + (12 % 10) + "\n");
            updateOutput("8 % 10 = " + (8 % 10) + "\n");
            updateOutput("Bound 2 = " + (2 & 0x00FF) + "\n");
            updateOutput("Bound 3 = " + (3 & 0x00FF) + "\n");
            updateOutput("Bound 4 = " + (4 & 0x00FF) + "\n");
            updateOutput("Bound 5 = " + (5 & 0x00FF) + "\n");
            updateOutput("Bound 8 = " + (8 & 0x00FF) + "\n");
            updateOutput("Bound 15 = " + (15 & 0x00FF) + "\n");
            updateOutput("Bound 7937 = " + (7937 & 0x00FF) + "\n");
        } else {
            updateOutput("\'" + userInput + "\'" + msgError);
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
        return _etInput.getText().toString();
    }
    /** Update output */
    public void updateOutput(String s) {
        _tvOutput.setText(s + _tvOutput.getText().toString());
        _counter++;
    }
    /** Create context menu. */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Mandatory call to the super class.
        super.onCreateOptionsMenu(menu);
        // Add toggle console item to menu.
        menu.add(0, CONSOLE_ID, 0, R.string.contextmenu_console).setShortcut('0', 'c');

        return true;
    }
    /** Event item selected in context menu. */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle all of the possible menu actions.
        switch(item.getItemId()) {
            case CONSOLE_ID: {
                if(_state == STATE_VISIBLE_TRUE) {
                    Animation anim = AnimationUtils.loadAnimation(this, R.anim.push_up_out);
                    _rlContainer.startAnimation(anim);
                    _rlContainer.setVisibility(View.INVISIBLE);
                    _state = STATE_VISIBLE_FALSE;
                } else if(_state == STATE_VISIBLE_FALSE) {
                    Animation anim = AnimationUtils.loadAnimation(this, R.anim.push_up_in);
                    _rlContainer.startAnimation(anim);
                    _rlContainer.setVisibility(View.VISIBLE);
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
            if(keyCode == KeyEvent.KEYCODE_ENTER && _pressedEnter == false) {
                runUserInput(getUserInput());
                _etInput.selectAll();
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
            runUserInput(getUserInput());
            _etInput.selectAll();
        }
    };
    /// Widgets.
    private RelativeLayout _rlContainer;
    private EditText _etInput;
    private TextView _tvOutput;
    private Button _btnExecute;

    // State.
    private static final int STATE_VISIBLE_FALSE = 0;
    private static final int STATE_VISIBLE_TRUE = 1;
    // Identifiers for menu items.
    private static final int CONSOLE_ID = Menu.FIRST;

    // Primitive data types.
    private int _counter;
    private int _state;
    private boolean _pressedEnter;
}
