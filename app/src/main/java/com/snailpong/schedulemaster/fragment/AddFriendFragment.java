package com.snailpong.schedulemaster.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.snailpong.schedulemaster.R;
import com.snailpong.schedulemaster.model.UserModel;

import java.util.HashMap;
import java.util.Map;

public class AddFriendFragment extends Fragment {

    private Button findButton;
    private Button addButton;
    private EditText keyword;
    private ImageView imageView;
    private TextView foundname;
    private FirebaseDatabase firebaseDatabase;
    private LinearLayout found;
    private boolean dataFound = false;
    private String friendUid;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_addfriend, container, false);
        keyword = (EditText) view.findViewById(R.id.addfriend_keyword);
        foundname = (TextView)view.findViewById(R.id.addfriend_foundname);
        findButton = (Button) view.findViewById(R.id.addfriend_find);
        addButton = (Button) view.findViewById(R.id.addfriend_add);
        imageView = (ImageView) view.findViewById(R.id.addfriend_friendimg);
        found = (LinearLayout) view.findViewById(R.id.addfriend_found);
        firebaseDatabase = FirebaseDatabase.getInstance();
        found.setVisibility(View.GONE);

        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String key = keyword.getText().toString();
                found.setVisibility(View.GONE);
                if(key.isEmpty()) {
                    Toast.makeText(getContext(), "이메일 주소를 입력하십시요.", Toast.LENGTH_SHORT).show();
                } else if (FirebaseAuth.getInstance().getCurrentUser().getEmail().equals(key)) {
                    Toast.makeText(getContext(), "자기 자신을 친구로 추가할 수 없습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    ValueEvent valueEvent;
                    firebaseDatabase.getReference().child("users").addListenerForSingleValueEvent((valueEvent = new ValueEvent(key)));
                }
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FriendManagement friendManagement = new FriendManagement();
                friendManagement.addFriend(friendUid);
            }
        });
        return view;
    }
    class ValueEvent implements ValueEventListener {
        UserModel userModel;
        String key;
        Boolean dataFound;
        public ValueEvent(String key) {
            this.key = key;
            this.dataFound = false;
        }
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                userModel = snapshot.getValue(UserModel.class);
                String thisKey = userModel.email;
                if (key.equals(thisKey)) {
                    dataFound = true;
                    break;
                }
            }
            if (dataFound) {
                friendUid = userModel.uid;
                if (!userModel.profileImageUrl.equals("null")) {
                    Glide.with(getContext())
                            .load(userModel.profileImageUrl)
                            .into(imageView);
                } else {
                    imageView.setImageResource(R.drawable.ic_sentiment_satisfied_gray_24dp);
                }
                foundname.setText(userModel.userName);
                found.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(getContext(), "없어.", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    }
    class FriendManagement {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("users/" + uid + "/friends");
        void addFriend(final String thisUid) {

            dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Map<String, Boolean> friends = (HashMap<String, Boolean>) dataSnapshot.getValue();
                    boolean notAdded = false;
                    // Check friendUid is already added.
                    if (friends == null) {
                        friends = new HashMap<String, Boolean>();
                        notAdded = true;
                    } else if (!friends.containsKey(thisUid)) {
                        notAdded = true;
                    }

                    if (notAdded) {
                        friends.put(thisUid, true);
                        dataRef.setValue(friends);
                        Toast.makeText(getContext(), "친구가 추가되었습니다.", Toast.LENGTH_SHORT).show();
                        getActivity().getSupportFragmentManager().beginTransaction().remove(AddFriendFragment.this).commit();
                        getActivity().getSupportFragmentManager().popBackStack();
                    } else {
                        Toast.makeText(getContext(), "이미 친구로 등록되어 있습니다.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
