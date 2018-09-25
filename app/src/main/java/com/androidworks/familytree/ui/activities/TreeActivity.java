package com.androidworks.familytree.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

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
import de.blox.treeview.BaseTreeAdapter;
import de.blox.treeview.TreeNode;
import de.blox.treeview.TreeView;

public class TreeActivity extends AppCompatActivity {

    StorageReference storageRef;
    DatabaseReference myRef;
    FirebaseStorage storage;
    FirebaseDatabase database;
    @BindView(R.id.treeView)
    TreeView treeView;
    private Gson gson = new Gson();
    private int nodeCount = 0;
    BaseTreeAdapter adapter;
    private ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor = 1.0f;
    final private String TAG = "NIK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree);
        ButterKnife.bind(this);
        init();
        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
        adapter = new TreeAdapter(this, R.layout.node);
        treeView.setAdapter(adapter);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        mScaleGestureDetector.onTouchEvent(motionEvent);
        return true;
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
//        TreeNode rootNode = new TreeNode(getNodeText());
//        rootNode.addChild(new TreeNode(getNodeText()));
//        final TreeNode child3 = new TreeNode(getNodeText());
//        child3.addChild(new TreeNode(getNodeText()));
//        final TreeNode child6 = new TreeNode(getNodeText());
//        child6.addChild(new TreeNode(getNodeText()));
//        child6.addChild(new TreeNode(getNodeText()));
//        child3.addChild(child6);
//        rootNode.addChild(child3);
//        final TreeNode child4 = new TreeNode(getNodeText());
//        child4.addChild(new TreeNode(getNodeText()));
//        child4.addChild(new TreeNode(getNodeText()));
//        rootNode.addChild(child4);

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
        TreeNode firstGenNode = null;

        for (int i = 0; i < members.size(); i++) {
            Member member = members.get(i);
            switch (member.getGeneration()) {
                case 1:
                    firstGenNode = new TreeNode(member);
                    rootNode.addChild(firstGenNode);
                    break;
                case 2:
                    final TreeNode child3 = new TreeNode(member);
                    if (firstGenNode != null) {
                        firstGenNode.addChild(child3);
                    }
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
        }
        adapter.setRootNode(rootNode);
    }

    public ArrayList<Member> convertToList(String JSON) {
        return gson.fromJson(JSON, new TypeToken<ArrayList<Member>>() {}.getType());
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector){
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(0.1f,Math.min(mScaleFactor, 10.0f));
            treeView.setScaleX(mScaleFactor);
            treeView.setScaleY(mScaleFactor);
            return true;
        }
    }
}
