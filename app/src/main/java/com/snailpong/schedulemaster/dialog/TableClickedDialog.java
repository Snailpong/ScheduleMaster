package com.snailpong.schedulemaster.dialog;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snailpong.schedulemaster.AddDeadlineActivity;
import com.snailpong.schedulemaster.CalendarRegularModifyActivity;
import com.snailpong.schedulemaster.DBHelper;
import com.snailpong.schedulemaster.EditDeadlineActivity;
import com.snailpong.schedulemaster.EditNoclassActivity;
import com.snailpong.schedulemaster.LoadingActivity;
import com.snailpong.schedulemaster.MemoActivity;
import com.snailpong.schedulemaster.MypageDeadlineActivity;
import com.snailpong.schedulemaster.MypageNoclassActivity;
import com.snailpong.schedulemaster.NotificationReceiver;
import com.snailpong.schedulemaster.R;
import com.snailpong.schedulemaster.RingTonePlayingReceiver;
import com.snailpong.schedulemaster.model.DeadModel;
import com.snailpong.schedulemaster.model.NoclassModel;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.ALARM_SERVICE;

public class TableClickedDialog extends DialogFragment {

    private Fragment fragment;
    private int id;
    private DBHelper helper;
    private SQLiteDatabase db;
    private PendingIntent pendingintent;
    private AlarmManager alarm_manager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_table_clicked, container, false);
        id = getArguments().getInt("id");
        helper = new DBHelper(getActivity(), "db.db", null, 1);
        db = helper.getWritableDatabase();
        helper.onCreate(db);

        RecyclerView recyclerViewd = (RecyclerView) view.findViewById(R.id.recyclerview_dead2);
        recyclerViewd.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewd.setAdapter(new DeadRecyclerViewAdapter());

        RecyclerView recyclerViewn = (RecyclerView) view.findViewById(R.id.recyclerview_noclass2);
        recyclerViewn.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewn.setAdapter(new NoclassRecyclerViewAdapter());

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        // 마감 추가 버튼 이벤트 처리
        ImageView assignmentAddBtn = (ImageView) view.findViewById(R.id.assignmentAddBtn);
        assignmentAddBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddDeadlineActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
        // 휴일 추가 버튼 이벤트 처리
        ImageView cancelAddBtn = (ImageView) view.findViewById(R.id.canceladd_addBtn);
        cancelAddBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putInt("id", id);
                CancelAddDialog dialog = new CancelAddDialog();
                dialog.setArguments(args);
                dialog.show(getActivity().getSupportFragmentManager(),"tag");
            }
        });
        Button yes = (Button)view.findViewById(R.id.dia_clicked_yes);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Button edit = (Button)view.findViewById(R.id.dia_clicked_modify);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CalendarRegularModifyActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
                dismiss();
            }
        });

        Button delete = (Button)view.findViewById(R.id.dia_clicked_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("정기일정을 삭제하시겠습니까?");
                builder.setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Cursor cursor;
                                /*
                                DBHelper helper;
                                SQLiteDatabase db;
                                helper = new DBHelper(getActivity(), "db.db", null, 1);
                                db = helper.getWritableDatabase();
                                helper.onCreate(db);
                                */
                                // 정기 일정의 db 제거
                                db.delete("weekly", "_id="+String.valueOf(id), null);

                                // deadline의 db제거 및 알람 제거
                                cursor = db.rawQuery("SELECT * FROM deadline WHERE whatid='" + id + "';", null);
                                cursor.moveToFirst();
                                while(cursor.moveToNext()) {
                                    final Intent my_intent = new Intent(getActivity(), NotificationReceiver.class);
                                    alarm_manager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                                    pendingintent = PendingIntent.getBroadcast(getActivity()
                                            , 1000 + cursor.getColumnIndex("_id"), my_intent, 0);
                                    alarm_manager.cancel(pendingintent);
                                }
                                db.delete("deadline", "whatid="+String.valueOf(id), null);

                                // noclass의 db제거 및 알람 제거
                                cursor = db.rawQuery("SELECT * FROM noclass WHERE whatid='" + id + "';", null);
                                cursor.moveToFirst();
                                while(cursor.moveToNext()) {
                                    final Intent my_intent = new Intent(getActivity(), NotificationReceiver.class);
                                    alarm_manager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                                    pendingintent = PendingIntent.getBroadcast(getActivity()
                                            , 2000 + cursor.getColumnIndex("_id"), my_intent, 0);
                                    alarm_manager.cancel(pendingintent);
                                }
                                db.delete("noclass", "whatid="+String.valueOf(id), null);

                                // alarmset의 정기일정 진동모드 변경 알람 제거
                                cursor = db.rawQuery("SELECT * FROM alarmset WHERE whatid='" + id + "';", null);
                                cursor.moveToFirst();
                                while(cursor.moveToNext()) {
                                    if (cursor.getString(cursor.getColumnIndex("state")) == "vib on"
                                    || cursor.getString(cursor.getColumnIndex("state")) == "vib off") {
                                        final Intent my_intent = new Intent(getActivity(), RingTonePlayingReceiver.class);
                                        alarm_manager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                                        pendingintent = PendingIntent.getBroadcast(getActivity()
                                                , cursor.getColumnIndex("_id"), my_intent, 0);
                                        alarm_manager.cancel(pendingintent);
                                    }
                                }
                                db.delete("alarmset", "whatid="+String.valueOf(id), null);
                                db.close();
                                Intent intent = new Intent(getActivity(), LoadingActivity.class);
                                startActivity(intent);
                                dismiss();
                            }
                        });
                builder.setNegativeButton("아니오", null);
                builder.show();
            }
        });

        LinearLayout memo = (LinearLayout)view.findViewById(R.id.dia_clicked_memo);
        memo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MemoActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        return view;
    }

    class DeadRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<DeadModel> dead = new ArrayList<>();

        public DeadRecyclerViewAdapter() {
            Cursor c = db.query("deadline", null, "whatid="+String.valueOf(id), null, null, null, null, null);
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
            ((DeadRecyclerViewAdapter.CustomViewHolder) viewHolder).sub.setText("");
            ((DeadRecyclerViewAdapter.CustomViewHolder) viewHolder).time.setText(String.valueOf(thisModel.getYear())+"년 "
                    +String.valueOf(thisModel.getMonth()+1) +"월 "+String.valueOf(thisModel.getDay())+"일 "+String.format("%02d",thisModel.getHour())+
                    ":"+String.format("%02d",thisModel.getMin()));
            ((DeadRecyclerViewAdapter.CustomViewHolder) viewHolder).lin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), EditDeadlineActivity.class);
                    intent.putExtra("id", thisModel.getId());
                    intent.putExtra("name", subname);
                    startActivity(intent);
                    dismiss();
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

    class NoclassRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<NoclassModel> Noclass = new ArrayList<>();

        public NoclassRecyclerViewAdapter() {
            Cursor c = db.query("noclass", null, "whatid="+String.valueOf(id), null, null, null, null, null);
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
            return new NoclassRecyclerViewAdapter.CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            final NoclassModel thisModel = Noclass.get(i);

            Cursor c = db.query("weekly", null, "_id="+String.valueOf(thisModel.getWhatid()), null, null, null, null, null);
            c.moveToNext();
            final String subname = c.getString(c.getColumnIndex("name"));
            ((NoclassRecyclerViewAdapter.CustomViewHolder) viewHolder).sub.setText("");
            ((NoclassRecyclerViewAdapter.CustomViewHolder) viewHolder).sub.setVisibility(View.GONE);
            ((NoclassRecyclerViewAdapter.CustomViewHolder) viewHolder).time.setText(String.valueOf(thisModel.getYear())+"년 "
                    +String.valueOf(thisModel.getMonth()+1) +"월 "+String.valueOf(thisModel.getDay())+"일");

            ((NoclassRecyclerViewAdapter.CustomViewHolder) viewHolder).lin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), EditNoclassActivity.class);
                    intent.putExtra("id", thisModel.getId());
                    intent.putExtra("name", subname);
                    startActivity(intent);
                    dismiss();
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