package com.snailpong.schedulemaster.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import com.snailpong.schedulemaster.R;

public class TableClickedCancelAddDialog extends DialogFragment {

    private Fragment fragment;

    public TableClickedCancelAddDialog() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_table_cancel_add, container, false);
        fragment = this;
        Bundle args = getArguments();
        String value = args.getString("key");
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        // 참조 주소 : http://blog.naver.com/qbxlvnf11/221436373954
        // DatePicker 처리 부분
        int year = 2019; int month = 4; int day = 21;
        DatePicker datePicker = (DatePicker) view.findViewById(R.id.dataPicker);
        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String date = year + "/" + monthOfYear + "/" + dayOfMonth;
                //Toast.makeText(getActivity().getApplicationContext(), date, Toast.LENGTH_SHORT).show();
            }
        });
        Button cancelBtn = (Button) view.findViewById(R.id.cancelCancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (fragment != null) {
                    DialogFragment dialogFragment = (DialogFragment) fragment;
                    dialogFragment.dismiss();
                }
            }
        });
        /*
         * DialogFragment를 종료시키려면? 물론 다이얼로그 바깥쪽을 터치하면 되지만
         * 종료하기 버튼으로도 종료시킬 수 있어야겠죠?
         */
        // 먼저 부모 프래그먼트를 받아옵니다.
        //findFragmentByTag안의 문자열 값은 Fragment1.java에서 있던 문자열과 같아야합니다.
        //dialog.show(getActivity().getSupportFragmentManager(),"tag");

        // 아래 코드는 버튼 이벤트 안에 넣어야겠죠?

        //fragment = getActivity().getSupportFragmentManager().findFragmentByTag("tag");
        return view;
    }
}
