package com.snailpong.schedulemaster.dialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.snailpong.schedulemaster.AddDeadlineActivity;
import com.snailpong.schedulemaster.CalendarRegularModifyActivity;
import com.snailpong.schedulemaster.DBHelper;
import com.snailpong.schedulemaster.LoadingActivity;
import com.snailpong.schedulemaster.R;

public class TableClickedDialog extends DialogFragment {

    private Fragment fragment;
    private int id;

    public TableClickedDialog() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_table_clicked, container, false);
        id = getArguments().getInt("id");
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
        ImageView cancelAddBtn = (ImageView) view.findViewById(R.id.cancelAddBtn);
        cancelAddBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putString("key", "value");
                TableClickedCancelAddDialog dialog = new TableClickedCancelAddDialog();
                dialog.setArguments(args); // 데이터 전달
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
                                DBHelper helper;
                                SQLiteDatabase db;

                                helper = new DBHelper(getActivity(), "db.db", null, 1);
                                db = helper.getWritableDatabase();
                                helper.onCreate(db);
                                db.delete("weekly", "_id="+String.valueOf(id), null);
                                Intent intent = new Intent(getActivity(), LoadingActivity.class);
                                startActivity(intent);
                                dismiss();
                            }
                        });
                builder.setNegativeButton("아니오", null);
                builder.show();
            }
        });

        return view;
    }
}