package com.pifyz.switchcolor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class ChooseLevelActivity extends Activity {
    private AlertDialog dialog;
    private CellLevelAdapter cellAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_choose_level);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
            .setTitle(getString(R.string.txt_level_gen_random))
            .setItems(R.array.levels, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    MenuActivity.curLevel = -1 * which - 1;

                    Intent intent = new Intent(ChooseLevelActivity.this, GameActivity.class);
                    startActivity(intent);
                }
            });

        dialog = builder.create();

        GridView gridView = findViewById(R.id.gridview);
        cellAdapter = new CellLevelAdapter(this);
        gridView.setAdapter(cellAdapter);
        gridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                TextView btn = v.findViewById(R.id.txt_level);
                int level = Integer.parseInt((btn).getText().toString());

                if (level <= MenuActivity.prefs.getInt("cur_level", 0) + 1) {
                    MenuActivity.curLevel = level - 1;

                    Intent intent = new Intent(ChooseLevelActivity.this, GameActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.reach_level_to_unlock), Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        Button btnRandomLevel = findViewById(R.id.btn_random_level);
        btnRandomLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        cellAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_random_level, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        List<?> allowedItems = Arrays.asList(
            R.id.btn_level_beginner,
            R.id.btn_level_easy,
            R.id.btn_level_medium,
            R.id.btn_level_difficult,
            R.id.btn_level_expert
        );

        if (allowedItems.contains(itemId)) {
            return true;
        }

        return super.onContextItemSelected(item);
    }
}
