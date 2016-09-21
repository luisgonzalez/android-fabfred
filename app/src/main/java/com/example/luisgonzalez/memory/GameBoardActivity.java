package com.example.luisgonzalez.memory;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridLayout.LayoutParams;

public class GameBoardActivity extends AppCompatActivity {
    Button[] _buttons = new Button[0];
    Button[] _sequence = new Button[100];
    int _lastPosition = 0;
    int _currentPosition = 0;
    int _width = 3;
    int _height = 4;
    int _delay = 400;
    int _animationDelay = 100;
    Animation _animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_board);

        createButtons();
        setEnabled(false);
        final Button buttonRestart = (Button) this.findViewById(R.id.buttonRestart);
        buttonRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initializeSequence();
                updateResetButtonText();
                setEnabled(true);
                walkSequence();
                buttonRestart.getBackground().clearColorFilter();
            }
        });

        _animation = new AlphaAnimation(1, 0);
        _animation.setDuration(_animationDelay);
        _animation.setInterpolator(new LinearInterpolator());
        _animation.setRepeatCount(1);
        _animation.setRepeatMode(Animation.REVERSE);
    }

    View.OnClickListener _buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final Button button = (Button) view;

            highlight(button);

            resolve(button);
        }
    };

    protected int getButtonPosition(int row, int column) {
        return row * _width + column;
    }

    protected void walkSequence() {
        setEnabled(false);

        for (int i = 0; i <= _lastPosition; i++) {
            final Handler handler = new Handler();
            final Button button = _sequence[i];
            final int x = i;

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    highlight(button);

                    if (x == _lastPosition) {
                        setEnabled(true);
                    }
                }
            }, _delay * i + 750);
        }
    }

    protected void updateResetButtonText() {
        Button buttonRestart = (Button) this.findViewById(R.id.buttonRestart);
        buttonRestart.setText(_lastPosition + " - Restart");
    }

    protected void gameLost() {
        Button buttonRestart = (Button) this.findViewById(R.id.buttonRestart);
        buttonRestart.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
    }

    protected void setEnabled(boolean enabled) {
        for (Button button : _buttons) {
            button.setEnabled(enabled);
        }
    }

    protected void highlight(Button button){
        button.startAnimation(_animation);
    }

    protected void resolve(Button button) {
        if (_sequence[_currentPosition] == button) {
            if (_currentPosition == _lastPosition) {
                _currentPosition = 0;
                _lastPosition++;
                updateResetButtonText();
                walkSequence();
            } else {
                _currentPosition++;
            }
        } else {
            setEnabled(false);
            gameLost();
        }
    }

    protected void initializeSequence() {
        int max = _width * _height;
        for (int i = 0; i < _sequence.length; i++) {
            _sequence[i] = _buttons[(int) Math.floor(Math.random() * max)];
        }
        _lastPosition = 0;
        _currentPosition = 0;
    }

    protected void createButtons() {
        destroyButtons();
        int totalButtons = _width * _height;

        _buttons = new Button[totalButtons];

        GridLayout gridLayout = (GridLayout) findViewById(R.id.ButtonGridLayout);
        gridLayout.setColumnCount(_width);
        gridLayout.setRowCount(_height);

        for (int column = 0; column < _width; column++) {
            for (int row = 0; row < _height; row++) {
                LayoutParams params = new LayoutParams();

                params.setGravity(Gravity.FILL);
                params.columnSpec = GridLayout.spec(column, 1.0f);
                params.rowSpec = GridLayout.spec(row, 1.0f);

                Button button = new Button(this);
                button.setLayoutParams(params);
                button.setText(column + " " + row);
                button.setTextColor(0x00FFFFFF);
                button.setPadding(0, 0, 0, 0);

                int buttonPosition = getButtonPosition(row, column);
                int color = Color.HSVToColor(new float[]{ buttonPosition * (360 / totalButtons) % 360, 0.5f, 0.5f});
                button.setBackgroundColor(color);

                _buttons[buttonPosition] = button;
                gridLayout.addView(button);
                button.setOnClickListener(_buttonListener);
            }
        }
    }

    protected void destroyButtons() {
        GridLayout gridLayout = (GridLayout) findViewById(R.id.ButtonGridLayout);

        for (Button button : _buttons) {
            gridLayout.removeView(button);
        }
    }
}
