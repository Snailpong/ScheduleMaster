package com.snailpong.schedulemaster;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class CalendarAddDialog extends DialogFragment {

    private Fragment fragment;
    private int starthour, startmin, endhour, endmin;

    public CalendarAddDialog() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_calendar_add, container, false);
        // 과제 추가 버튼 이벤트 처리

        Bundle args = getArguments();
        String value = args.getString("key");

        Button add = (Button)view.findViewById(R.id.dia_add_add);
        Button cancel = (Button)view.findViewById(R.id.dia_add_cancel);

        LinearLayout startlin = (LinearLayout)view.findViewById(R.id.calender_add_starttime_lin);
        LinearLayout endlin = (LinearLayout)view.findViewById(R.id.calender_add_endtime_lin);

        final TextView starttxt = (TextView)view.findViewById(R.id.calender_add_starttime_txt);
        final TextView endtxt = (TextView)view.findViewById(R.id.calender_add_endtime_txt);

        final TimePickerDialog.OnTimeSetListener startTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                starthour = hourOfDay;
                startmin = minute;
                starttxt.setText(String.format("%02d", starthour) + ":" + String.format("%02d", startmin));
            }
        };

        final TimePickerDialog.OnTimeSetListener endTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                endhour = hourOfDay;
                endmin = minute;
                endtxt.setText(String.format("%02d", endhour) + ":" + String.format("%02d", endmin));
            }
        };

        startlin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(getActivity(), startTimeSetListener, starthour, startmin, false).show();
            }
        });

        endlin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(getActivity(), startTimeSetListener, endhour, endmin, false).show();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });



        return view;
    }
}
