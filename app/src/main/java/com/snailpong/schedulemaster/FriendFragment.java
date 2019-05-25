package com.snailpong.schedulemaster;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.snailpong.schedulemaster.model.UserModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendFragment extends Fragment {
    private FloatingActionButton addFriendBtn;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.friendfragment_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        recyclerView.setAdapter(new FriendFragmentRecyclerViewAdapter());
        addFriendBtn = (FloatingActionButton) view.findViewById(R.id.friendfragment_addfriend);
        addFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TabbedActivity activity = (TabbedActivity) getActivity();
                activity.onFragmentChange(1);
            }
        });
        return view;
    }
    class FriendFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<UserModel> users = new ArrayList<>();;
        List<UserModel> friends = new ArrayList<>();;
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        public FriendFragmentRecyclerViewAdapter() {

            FirebaseDatabase.getInstance().getReference().child("users/" + uid + "/friends").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    users.clear();
                    friends.clear();
                    final Map<String, Boolean> userFriends = (HashMap<String, Boolean>) dataSnapshot.getValue();

                    if (userFriends != null) {
                        FirebaseDatabase.getInstance().getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                                    users.add(snapshot.getValue(UserModel.class));

                                for (UserModel userModel : users)
                                    if (userFriends.containsKey(userModel.uid))
                                        friends.add(userModel);
                                notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            // 모든 유저 출력 코드
//            FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
//
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    // 서버에서 넘어온 데이터 처리
//                    users.clear();int i = 0;
//                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                        System.out.println("스냅샷 " + i++);
//                        users.add(snapshot.getValue(UserModel.class));
//                    }
//
//                    notifyDataSetChanged();
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_friend, viewGroup, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            UserModel thisModel = friends.get(i);
            if (!thisModel.profileImageUrl.equals("null")) {
                Glide.with(viewHolder.itemView.getContext())
                        .load(thisModel.profileImageUrl)
                        .into(((CustomViewHolder) viewHolder).imageView);
            }
            ((CustomViewHolder) viewHolder).textView.setText(thisModel.userName);
        }

        @Override
        public int getItemCount() {
            return friends.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView textView;

            public CustomViewHolder(View view) {
                super(view);
                imageView = (ImageView) view.findViewById(R.id.frienditem_imageview);
                textView = (TextView) view.findViewById(R.id.frienditem_textview);
            }
        }
    }
}
