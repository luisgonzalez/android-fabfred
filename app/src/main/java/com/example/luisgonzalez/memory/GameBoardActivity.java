package com.example.luisgonzalez.memory;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import com.example.luisgonzalez.memory.util.CellAdapter;

import java.util.ArrayList;
import java.util.Arrays;

public class GameBoardActivity extends AppCompatActivity {
    static final String CURRENT_POSITION = "CURRENT_POSITION";
    static final String SEQUENCE = "SEQUENCE";
    static final String[] SPEEDS = new String[]{"Slow", "Normal", "Fast"};
    static final String[] DIFFICULTIES = new String[]{"Very Easy", "Easy", "Normal", "Hard", "Very Hard"};

    ArrayList<Integer> _sequence = new ArrayList<>();
    int _currentPosition = 0;
    int _width = 3;
    int _height = 3;
    int _delay = 400;
    int _animationDelay = 100;
    Animation _animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_board);

        loadConfig();

        setEnabled(false);

        final Button buttonRestart = (Button) this.findViewById(R.id.buttonRestart);
        buttonRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initializeSequence();
                updateResetButtonText();
                walkSequence();
                view.getBackground().clearColorFilter();
                setEnabled(true);
            }
        });

        if (savedInstanceState != null) {
            _currentPosition = savedInstanceState.getInt(CURRENT_POSITION);
            _sequence = savedInstanceState.getIntegerArrayList(SEQUENCE);
            updateResetButtonText();
            setEnabled(true);
        } else {
            initializeSequence();
        }

        _animation = new AlphaAnimation(1, 0);
        _animation.setDuration(_animationDelay);
        _animation.setInterpolator(new LinearInterpolator());
        _animation.setRepeatCount(1);
        _animation.setRepeatMode(Animation.REVERSE);

        createButtons();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedState) {
        savedState.putInt(CURRENT_POSITION, _currentPosition);
        savedState.putIntegerArrayList(SEQUENCE, _sequence);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public void onPostResume() {
        int currentDifficulty = _width * _height;

        loadConfig();

        if (currentDifficulty != _width * _height) {
            createButtons();
            initializeSequence();
        }

        super.onPostResume();
    }

    protected void loadConfig() {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String gameSpeed = defaultSharedPreferences.getString("game_speed", "Normal");
        String difficulty = defaultSharedPreferences.getString("difficulty", "Normal");

        switch (Arrays.asList(DIFFICULTIES).indexOf(difficulty)) {
            case 1:
                _width = 3;
                _height = 4;
                break;
            case 2:
                _width = 4;
                _height = 4;
                break;
            case 3:
                _width = 4;
                _height = 5;
                break;
            case 4:
                _width = 5;
                _height = 5;
                break;
            default:
                _width = 3;
                _height = 3;
        }

        switch (Arrays.asList(SPEEDS).indexOf(gameSpeed)) {
            case 0:
                _delay = 600;
                _animationDelay = 200;
                break;
            case 2:
                _delay = 200;
                _animationDelay = 50;
                break;
            default:
                _delay = 400;
                _animationDelay = 150;
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void walkSequence() {
        setEnabled(false);

        Handler handler = new Handler();
        int index = 0;
        final int lastIndex = _sequence.size() - 1;
        for (final Integer buttonPosition : _sequence) {
            GridView gridView = (GridView) findViewById(R.id.GridViewBoard);
            final View cell = gridView.getChildAt(buttonPosition);
            final int i = index++;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    highlight(cell);

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
        GridView gridView = (GridView) findViewById(R.id.GridViewBoard);
        gridView.setEnabled(enabled);
    }

    protected void highlight(View cell) {
        if (cell != null) {
            cell.startAnimation(_animation);
        }
    }

    protected void resolve(int buttonPosition) {
        if (_sequence.get(_currentPosition) == buttonPosition) {
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

    protected void addElement() {
        _sequence.add((int) Math.floor(Math.random() * _width * _height));
    }

    protected void initializeSequence() {
        _currentPosition = 0;
        _sequence.clear();
        addElement();
    }

    protected void createButtons() {
        GridView gridView = (GridView) findViewById(R.id.GridViewBoard);
        CellAdapter adapter = new CellAdapter(this, _height, _width);
        gridView.setNumColumns(_width);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                highlight(view);
                resolve(i);
            }
        });


    }
}
