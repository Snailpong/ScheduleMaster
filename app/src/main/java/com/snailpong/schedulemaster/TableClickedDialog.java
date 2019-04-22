package com.snailpong.schedulemaster;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

public class TableClickedDialog extends DialogFragment {

    private Fragment fragment;

    public TableClickedDialog() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_table_clicked, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        // 마감 추가 버튼 이벤트 처리
        ImageView assignmentAddBtn = (ImageView) view.findViewById(R.id.assignmentAddBtn);
        assignmentAddBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddDeadlineActivity.class);
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

        Bundle args = getArguments();
        String value = args.getString("key");
        fragment = getActivity().getSupportFragmentManager().findFragmentByTag("tag");

        return view;
    }
}