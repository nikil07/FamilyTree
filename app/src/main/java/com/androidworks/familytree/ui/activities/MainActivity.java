package com.androidworks.familytree.ui.activities;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.androidworks.familytree.R;
import com.androidworks.familytree.data.FamilyContract;
import com.androidworks.familytree.data.FamilyDbHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    @BindView(R.id.et_name)
    EditText name;
    @BindView(R.id.et_age)
    EditText age;
    @BindView(R.id.tv_table)
    TextView table;
    @BindView(R.id.bt_show)
    Button show;
    @BindView(R.id.bt_add)
    Button add;
    SQLiteDatabase writableDB;
    SQLiteDatabase readableDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        FamilyDbHelper dbHelper = new FamilyDbHelper(this);

        // Gets the data repository in write mode
        writableDB = dbHelper.getWritableDatabase();
        readableDB = dbHelper.getReadableDatabase();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
// Create a new map of values, where column names are the keys
                ContentValues values = new ContentValues();
                values.put(FamilyContract.FamilyTable.COLUMN_NAME, name.getText().toString());
                values.put(FamilyContract.FamilyTable.COLUMN_AGE, age.getText().toString());

// Insert the new row, returning the primary key value of the new row
                long newRowId = writableDB.insert(FamilyContract.FamilyTable.TABLE_NAME, null, values);
                Log.d(TAG, "DB row ID " + newRowId);
            }
        });

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List itemIds = new ArrayList<>();

                // Define a projection that specifies which columns from the database
// you will actually use after this query.
                String[] projection = {
                        BaseColumns._ID,
                        FamilyContract.FamilyTable.COLUMN_NAME,
                        FamilyContract.FamilyTable.COLUMN_AGE
                };

// Filter results WHERE "title" = 'My Title'
                String selection = FamilyContract.FamilyTable.COLUMN_NAME + " = ?";
                String[] selectionArgs = {"My Title"};

// How you want the results sorted in the resulting Cursor
                String sortOrder =
                        FamilyContract.FamilyTable.COLUMN_AGE + " DESC";

                final Cursor cursor = readableDB.query(
                        FamilyContract.FamilyTable.TABLE_NAME,   // The table to query
                        projection,             // The array of columns to return (pass null to get all)
                        null,              // The columns for the WHERE clause
                        null,          // The values for the WHERE clause
                        null,                   // don't group the rows
                        null,                   // don't filter by row groups
                        sortOrder               // The sort order
                );

                while (cursor.moveToNext()) {
                    long itemId = cursor.getLong(
                            cursor.getColumnIndexOrThrow(FamilyContract.FamilyTable._ID));
                    itemIds.add(itemId);
                    Log.d(TAG, "Name " + cursor.getString(1));
                    Log.d(TAG, "Age " + cursor.getString(2));
                }

                table.setText(itemIds.toString());
                cursor.close();
            }
        });
    }
}
