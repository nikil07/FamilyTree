package com.androidworks.familytree.ui.activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.androidworks.familytree.R;
import com.androidworks.familytree.data.DataStore;
import com.androidworks.familytree.data.model.Member;
import com.androidworks.familytree.ui.adapters.TreeAdapter;
import com.androidworks.familytree.ui.customviews.TreeLayout;
import com.androidworks.familytree.utils.Constants;
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
import de.blox.treeview.AlgorithmFactory;
import de.blox.treeview.BaseTreeAdapter;
import de.blox.treeview.TreeNode;
import de.blox.treeview.TreeView;

public class TreeActivity extends AppCompatActivity {

    StorageReference storageRef;
    DatabaseReference myRef;
    FirebaseStorage storage;
    FirebaseDatabase database;
    @BindView(R.id.tree_layout)
    TreeLayout treeLayout;
    @BindView(R.id.treeView)
    TreeView treeView;
    private Gson gson = new Gson();
    private int nodeCount = 0;
    BaseTreeAdapter adapter;
    final private String TAG = "NIK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree);
        ButterKnife.bind(this);
        init();

        adapter = new TreeAdapter(this, R.layout.node);
        treeView.setAdapter(adapter);
    }

    private String getNodeText() {
        return "Member " + nodeCount++;
    }

    private void init() {
        FirebaseApp.initializeApp(this);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://nivezzle.appspot.com/");
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("FAMILY");
//        progressBar.setMax(100);
//        progressBar.setProgress(0);


        // example tree
        TreeNode rootNode = new TreeNode(getNodeText());
        rootNode.addChild(new TreeNode(getNodeText()));
        final TreeNode child3 = new TreeNode(getNodeText());
        child3.addChild(new TreeNode(getNodeText()));
        final TreeNode child6 = new TreeNode(getNodeText());
        child6.addChild(new TreeNode(getNodeText()));
        child6.addChild(new TreeNode(getNodeText()));
        child3.addChild(child6);
        rootNode.addChild(child3);
        final TreeNode child4 = new TreeNode(getNodeText());
        child4.addChild(new TreeNode(getNodeText()));
        child4.addChild(new TreeNode(getNodeText()));
        rootNode.addChild(child4);

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
        ArrayList<Member> members = convertToList(DataStore.getInstance(this).getMembersJSON());
        TreeNode rootNode = new TreeNode(Constants.ROOT_NODE_MEMBER);

        for (int i = 0; i < members.size(); i++) {
            Member member = members.get(i);
            switch (member.getGeneration()) {
                case 1:
                    rootNode.addChild(new TreeNode(member));
                    break;
                case 2:
                    final TreeNode child3 = new TreeNode(member);
                    child3.addChild(new TreeNode(member));
                    final TreeNode child6 = new TreeNode(member);
                    child6.addChild(child3);
                    rootNode.addChild(child6);
                    break;
                case 3:
                    break;
                case 4:
                    Log.d(TAG, "Gen 1");
                    break;
                case 5:
                    Log.d(TAG, "Gen 1");
                    break;
                case 6:
                    Log.d(TAG, "Gen 1");
                    break;
                case 7:
                    Log.d(TAG, "Gen 1");
                    break;
            }
            rootNode.addChild(new TreeNode(member));
        }
        adapter.setRootNode(rootNode);
    }

    public ArrayList<Member> convertToList(String JSON) {
        return gson.fromJson(JSON, new TypeToken<ArrayList<Member>>() {
        }.getType());
    }
}
