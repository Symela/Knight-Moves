package com.example.chess_knight_path;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private String[] sizes = {"6x6", "7x7", "8x8", "9x9", "10x10", "11x11", "12x12", "13x13", "14x14", "15x15", "16x16"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridView chessBoardSizes = findViewById(R.id.size_gridview);

        ArrayAdapter<String> sizesAdapter = new ArrayAdapter<>(this, R.layout.main_size_gridview, R.id.size_textview, sizes);

        chessBoardSizes.setAdapter(sizesAdapter);

        chessBoardSizes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ChessBoardActivity.class);
                intent.putExtra("size", (position + 7));
                startActivity(intent);
            }
        });
    }
}
