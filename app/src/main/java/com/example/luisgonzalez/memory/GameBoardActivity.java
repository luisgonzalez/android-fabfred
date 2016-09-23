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
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridLayout.LayoutParams;

import java.util.ArrayList;

public class GameBoardActivity extends AppCompatActivity {
    static final String CURRENT_POSITION = "CurrentPosition";
    static final String SEQUENCE = "SEQUENCE";

    ArrayList<Button> _buttons = new ArrayList<>();
    ArrayList<Button> _sequence = new ArrayList<>();
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
        buttonRestart.setOnClickListener(_buttonStartListener);

        if (savedInstanceState != null) {
            _currentPosition = savedInstanceState.getInt(CURRENT_POSITION);
            _sequence = transformIntegerSequenceToButtonSequence(savedInstanceState.getIntegerArrayList(SEQUENCE));
            updateResetButtonText();
            setEnabled(true);
        }

        _animation = new AlphaAnimation(1, 0);
        _animation.setDuration(_animationDelay);
        _animation.setInterpolator(new LinearInterpolator());
        _animation.setRepeatCount(1);
        _animation.setRepeatMode(Animation.REVERSE);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedState){
        savedState.putInt(CURRENT_POSITION, _currentPosition);
        savedState.putIntegerArrayList(SEQUENCE, transformButtonSequenceToIntegerSequence(_sequence));
    }

    View.OnClickListener _buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final Button button = (Button) view;

            highlight(button);

            resolve(button);
        }
    };

    View.OnClickListener _buttonStartListener = new View.OnClickListener() {
        @Override
        public void onClick(View view){
            initializeSequence();
            updateResetButtonText();
            walkSequence();
            view.getBackground().clearColorFilter();
            setEnabled(true);
        }
    };

    protected ArrayList<Button> transformIntegerSequenceToButtonSequence(ArrayList<Integer> list){
        ArrayList<Button> result = new ArrayList<>();
        for (Integer index : list) {
            result.add(_buttons.get(index));
        }
        return result;
    }

    protected ArrayList<Integer> transformButtonSequenceToIntegerSequence(ArrayList<Button> list){
        ArrayList<Integer> result = new ArrayList<>();
        for (Button button : list) {
            result.add(_buttons.indexOf(button));
        }
        return result;
    }

    protected int getButtonPosition(int row, int column) {
        return row * _width + column;
    }

    protected void walkSequence() {
        setEnabled(false);

        Handler handler = new Handler();
        int index = 0;
        final int lastIndex = _sequence.size() - 1;
        for (final Button button : _sequence) {
            final int i = index++;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    highlight(button);

                    if (i == lastIndex) {
                        setEnabled(true);
                    }
                }
            }, _delay * i + 750);
        }
    }

    protected void updateResetButtonText() {
        Button buttonRestart = (Button) this.findViewById(R.id.buttonRestart);
        buttonRestart.setText((_sequence.size() - 1) + " - Restart");
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
        if (_sequence.get(_currentPosition) == button) {
            _currentPosition++;
            if (_currentPosition == _sequence.size()) {
                _currentPosition = 0;
                addElement();
                updateResetButtonText();
                walkSequence();
            }
        } else {
            setEnabled(false);
            gameLost();
        }
    }

    protected void addElement(){
        _sequence.add(_buttons.get((int)Math.floor(Math.random() * _width * _height)));
    }

    protected void initializeSequence() {
        _currentPosition = 0;
        _sequence.clear();
        addElement();
    }

    protected void createButtons() {
        destroyButtons();
        int totalButtons = _width * _height;

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

                _buttons.add(button);
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
