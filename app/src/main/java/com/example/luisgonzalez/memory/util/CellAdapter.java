package com.example.luisgonzalez.memory.util;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class CellAdapter extends BaseAdapter {
    private int columns;
    private int rows;

    public CellAdapter(Context context, int rows, int columns) {
        super();
        this.columns = columns;
        this.rows = rows;
    }

    @Override
    public int getCount() {
        return this.columns * this.rows;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = new TextView(parent.getContext());

        int desiredWidth = parent.getWidth() / this.columns;
        int desiredHeight = parent.getHeight() / this.rows;
        int totalCells = this.columns * this.rows;

        view.setLayoutParams(new GridView.LayoutParams(desiredWidth, desiredHeight));

        int color = Color.HSVToColor(new float[]{ position * (360 / totalCells) % 360, 0.8f, 0.8f});
        view.setBackgroundColor(color);

        return view;
    }
}
