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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snailpong.schedulemaster.model.NoclassModel;

import java.util.ArrayList;
import java.util.List;

public class MypageNoclassActivity extends AppCompatActivity {

    private DBHelper helper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage_noclass);
        setTitle("휴일 목록");
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview_noclass);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MypageNoclassActivity.NoclassRecyclerViewAdapter());
    }

    class NoclassRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<NoclassModel> Noclass = new ArrayList<>();

        public NoclassRecyclerViewAdapter() {
            helper = new DBHelper(MypageNoclassActivity.this, "db.db", null, 1);
            db = helper.getWritableDatabase();
            helper.onCreate(db);
            Cursor c = db.query("Noclass", null, null, null, null, null, null, null);
            while(c.moveToNext()) {
                int id = c.getInt(c.getColumnIndex("_id"));
                int whatid = c.getInt(c.getColumnIndex("whatid"));
                int year = c.getInt(c.getColumnIndex("year"));
                int month = c.getInt(c.getColumnIndex("month"));
                int day = c.getInt(c.getColumnIndex("day"));
                Noclass.add(new NoclassModel(id, whatid, year, month, day));
                notifyDataSetChanged();
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_noclass, viewGroup, false);
            return new MypageNoclassActivity.NoclassRecyclerViewAdapter.CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            final NoclassModel thisModel = Noclass.get(i);

            Cursor c = db.query("weekly", null, "_id="+String.valueOf(thisModel.getWhatid()), null, null, null, null, null);
            c.moveToNext();
            final String subname = c.getString(c.getColumnIndex("name"));
            ((MypageNoclassActivity.NoclassRecyclerViewAdapter.CustomViewHolder) viewHolder).sub.setText(subname);
            ((MypageNoclassActivity.NoclassRecyclerViewAdapter.CustomViewHolder) viewHolder).time.setText(String.valueOf(thisModel.getYear())+"년 "
                    +String.valueOf(thisModel.getMonth()) +"월 "+String.valueOf(thisModel.getDay())+"일 ");

            ((MypageNoclassActivity.NoclassRecyclerViewAdapter.CustomViewHolder) viewHolder).lin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MypageNoclassActivity.this, EditNoclassActivity.class);
                    intent.putExtra("whatid", thisModel.getWhatid());
                    intent.putExtra("name", subname);
                    startActivity(intent);
                    finish();
                }
            });
        }

        @Override
        public int getItemCount() {
            return Noclass.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public TextView sub;
            public TextView time;
            public LinearLayout lin;

            public CustomViewHolder(View view) {
                super(view);
                lin = (LinearLayout) view.findViewById(R.id.noclass_layout);
                time = (TextView) view.findViewById(R.id.noclass_time);
                sub = (TextView) view.findViewById(R.id.noclass_sub);
            }
        }
    }
}
