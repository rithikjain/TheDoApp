package com.rithikjain.thedoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    List<String> toDoList;
    RecyclerView taskList;
    TaskAdapter adapter;

    FloatingActionButton addTaskBtn;
    EditText taskText;

    TextView dayText;
    TextView dateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dayText = findViewById(R.id.day_text);
        dateText = findViewById(R.id.date_text);

        final Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        String date = df.format(calendar.getTime());
        dateText.setText(date);

        switch (day) {
            case Calendar.SUNDAY:
                dayText.setText("Sunday,");
                break;
            case Calendar.MONDAY:
                dayText.setText("Monday,");
                break;
            case Calendar.TUESDAY:
                dayText.setText("Tuesday,");
                break;
            case Calendar.WEDNESDAY:
                dayText.setText("Wednesday,");
                break;
            case Calendar.THURSDAY:
                dayText.setText("Thurday,");
                break;
            case Calendar.FRIDAY:
                dayText.setText("Friday,");
                break;

            case Calendar.SATURDAY:
                dayText.setText("Saturday");
                break;
        }

        readData();

        adapter = new TaskAdapter(toDoList);
        adapter.notifyDataSetChanged();

        taskList = findViewById(R.id.task_list);
        taskList.setLayoutManager(new LinearLayoutManager(this));
        taskList.setAdapter(adapter);
        new ItemTouchHelper(itemTouchHelper).attachToRecyclerView(taskList);

        addTaskBtn = findViewById(R.id.add_task_btn);
        addTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTaskAddBox("Enter a Task!");
            }
        });
    }

    protected void onStop() {
        super.onStop();
        saveData();
    }

    public void showTaskAddBox(String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);

        View viewInflated = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_dialog,
                (ViewGroup) findViewById(android.R.id.content),false);

        taskText = viewInflated.findViewById(R.id.toDoText);

        builder.setView(viewInflated);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                toDoList.add(taskText.getText().toString());
                adapter.notifyDataSetChanged();
                taskText.setText("");
                saveData();
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    ItemTouchHelper.SimpleCallback itemTouchHelper = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int position_dragged = viewHolder.getAdapterPosition();
            int position_target = target.getAdapterPosition();

            Collections.swap(toDoList, position_dragged, position_target);
            adapter.notifyItemMoved(position_dragged, position_target);

            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            toDoList.remove(viewHolder.getAdapterPosition());
            adapter.notifyDataSetChanged();
            saveData();
        }
    };

    private void saveData() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(toDoList);
        editor.putString("list", json);
        editor.apply();
    }

    private void readData() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = prefs.getString("list", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        toDoList = gson.fromJson(json, type);

        if (toDoList == null) {
            toDoList = new ArrayList<>();
        }
    }
}
