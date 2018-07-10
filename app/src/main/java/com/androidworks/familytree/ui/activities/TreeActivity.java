package com.androidworks.familytree.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.androidworks.familytree.R;
import com.androidworks.familytree.data.DataStore;
import com.androidworks.familytree.data.model.Member;
import com.androidworks.familytree.ui.customviews.TreeLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TreeActivity extends AppCompatActivity {

    StorageReference storageRef;
    DatabaseReference myRef;
    FirebaseStorage storage;
    FirebaseDatabase database;
    @BindView(R.id.tree_layout)
    TreeLayout treeLayout;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree);
        ButterKnife.bind(this);
        init();
    }


    private void init() {
        FirebaseApp.initializeApp(this);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://nivezzle.appspot.com/");
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("FAMILY");
//        progressBar.setMax(100);
//        progressBar.setProgress(0);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                Log.d("nikhil", "Value is: " + value);
                DataStore.getInstance(TreeActivity.this).setMembersJSON(value);
                initViews();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e("nikhil", "Failed to read value.", error.toException());
            }
        });

    }

    private void initViews() {
        treeLayout.removeAllViews();
        LayoutInflater layoutInflater = getLayoutInflater();
        ArrayList<Member> members = convertToList(DataStore.getInstance(this).getMembersJSON());
        for (int i = 0; i < members.size(); i++) {

            Member member = members.get(i);
            View tagView = layoutInflater.inflate(R.layout.member_layout, null, false);

            TextView memberName = (TextView) tagView.findViewById(R.id.tv_member_name);
            TextView memberNickName = (TextView) tagView.findViewById(R.id.tv_member_nick_name);
            TextView memberBirthYear = (TextView) tagView.findViewById(R.id.tv_member_birth_year);

            memberName.setText(member.getName());
            memberNickName.setText(member.getNickName());
            memberBirthYear.setText("" + member.getBirthYear());
            treeLayout.addView(tagView);
        }
    }

    public ArrayList<Member> convertToList(String JSON) {
        return gson.fromJson(JSON, new TypeToken<ArrayList<Member>>() {
        }.getType());
    }
}
