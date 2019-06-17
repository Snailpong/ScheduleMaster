package com.snailpong.schedulemaster;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.snailpong.schedulemaster.fragment.FriendFragment;
import com.snailpong.schedulemaster.model.DeadModel;
import com.snailpong.schedulemaster.model.UserModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MypageDeadlineActivity extends AppCompatActivity {

    private DBHelper helper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage_deadline);
        setTitle("마감 목록");
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview_dead);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new DeadRecyclerViewAdapter());
    }

    class DeadRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<DeadModel> dead = new ArrayList<>();

        public DeadRecyclerViewAdapter() {
            helper = new DBHelper(MypageDeadlineActivity.this, "db.db", null, 1);
            db = helper.getWritableDatabase();
            helper.onCreate(db);
            Cursor c = db.query("deadline", null, null, null, null, null, null, null);
            while(c.moveToNext()) {
                int id = c.getInt(c.getColumnIndex("_id"));
                String name = c.getString(c.getColumnIndex("name"));
                int whatid = c.getInt(c.getColumnIndex("whatid"));
                int year = c.getInt(c.getColumnIndex("year"));
                int month = c.getInt(c.getColumnIndex("month"));
                int day = c.getInt(c.getColumnIndex("day"));
                int hour = c.getInt(c.getColumnIndex("hour"));
                int min = c.getInt(c.getColumnIndex("min"));
                int prev = c.getInt(c.getColumnIndex("prev"));
                dead.add(new DeadModel(id, name, whatid, year, month, day, hour, min, prev));
                notifyDataSetChanged();
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_dead, viewGroup, false);
            return new DeadRecyclerViewAdapter.CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            final DeadModel thisModel = dead.get(i);

            Cursor c = db.query("weekly", null, "_id="+String.valueOf(thisModel.getWhatid()), null, null, null, null, null);
            c.moveToNext();
            final String subname = c.getString(c.getColumnIndex("name"));
            Log.d("count: ", thisModel.getName());
            ((DeadRecyclerViewAdapter.CustomViewHolder) viewHolder).title.setText(thisModel.getName());
            ((DeadRecyclerViewAdapter.CustomViewHolder) viewHolder).sub.setText(subname);
            ((DeadRecyclerViewAdapter.CustomViewHolder) viewHolder).time.setText(String.valueOf(thisModel.getYear())+"년 "
                    +String.valueOf(thisModel.getMonth()+1) +"월 "+String.valueOf(thisModel.getDay())+"일 "+String.format("%02d",thisModel.getHour())+
                    ":"+String.format("%02d",thisModel.getMin()));

            ((DeadRecyclerViewAdapter.CustomViewHolder) viewHolder).lin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MypageDeadlineActivity.this, EditDeadlineActivity.class);
                    intent.putExtra("id", thisModel.getId());
                    intent.putExtra("name", subname);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return dead.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public TextView title;
            public TextView sub;
            public TextView time;
            public LinearLayout lin;

            public CustomViewHolder(View view) {
                super(view);
                lin = (LinearLayout) view.findViewById(R.id.dead_layout);
                title = (TextView) view.findViewById(R.id.dead_title);
                time = (TextView) view.findViewById(R.id.dead_time);
                sub = (TextView) view.findViewById(R.id.dead_sub);
            }
        }
    }
}
