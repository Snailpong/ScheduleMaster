package com.snailpong.schedulemaster;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class CalendarAddDialog extends DialogFragment {

    private Fragment fragment;

    public CalendarAddDialog() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 코드 참조 https://altongmon.tistory.com/254
        View view = inflater.inflate(R.layout.dialog_calendar_add, container, false);
        // 과제 추가 버튼 이벤트 처리

        Bundle args = getArguments();
        String value = args.getString("key");

        Button add = (Button)view.findViewById(R.id.dia_add_add);
        Button cancel = (Button)view.findViewById(R.id.dia_add_cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }
}
