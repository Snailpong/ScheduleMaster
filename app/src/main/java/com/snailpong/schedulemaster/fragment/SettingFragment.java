package com.snailpong.schedulemaster.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.snailpong.schedulemaster.DBHelper;
import com.snailpong.schedulemaster.LoginActivity;
import com.snailpong.schedulemaster.MapActivity;
import com.snailpong.schedulemaster.R;
import com.snailpong.schedulemaster.SyncActivity;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class SettingFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        final TextView goo = view.findViewById(R.id.setting_goo);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            goo.setText("로그아웃");
        }
        // 테스트 버튼
        LinearLayout testTableClickedBtn = (LinearLayout) view.findViewById(R.id.setting_alarmsound);
        testTableClickedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Bundle args = new Bundle();
//                args.putString("key", "value");
//                TableClickedDialog dialog = new TableClickedDialog();
//                dialog.setArguments(args); // 데이터 전달
//                dialog.show(getActivity().getSupportFragmentManager(),"tag");
                FirebaseAuth.getInstance().signOut();
            }
        });
        // 구글 계정 연동 로그인 버튼
        LinearLayout loginBtn = (LinearLayout) view.findViewById(R.id.setting_google);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    FirebaseAuth.getInstance().signOut();
                    goo.setText("계정 연동");
                } else {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }

            }
        });
        // Inflate the layout for this fragment

        LinearLayout sync = (LinearLayout) view.findViewById(R.id.setting_sync);
        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SyncActivity.class);
                startActivity(intent);
            }
        });

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    goo.setText("로그아웃");
                }
            }
        };

        LinearLayout save = (LinearLayout) view.findViewById(R.id.setting_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Workbook workbook = new HSSFWorkbook();
                Sheet sheet = workbook.createSheet();

                String[] days = new String[]{"월","화","수","목","금","토","일"};

                DBHelper helper;
                SQLiteDatabase db;

                helper = new DBHelper(getActivity(), "db.db", null, 1);
                db = helper.getWritableDatabase();
                helper.onCreate(db);

                Row row = sheet.createRow(0);
                row.createCell(0).setCellValue("아이디");
                row.createCell(1).setCellValue("이름");
                row.createCell(2).setCellValue("요일");
                row.createCell(3).setCellValue("시작 시간");
                row.createCell(4).setCellValue("종료 시간");

                int rows = 1;

                Cursor c = db.query("weekly", null, null, null, null, null, null);
                while(c.moveToNext()) {
                    row = sheet.createRow(rows);
                    row.createCell(0).setCellValue(c.getInt(c.getColumnIndex("_id")));
                    row.createCell(1).setCellValue(c.getString(c.getColumnIndex("name")));
                    int day = c.getInt(c.getColumnIndex("day"));
                    row.createCell(3).setCellValue(c.getString(c.getColumnIndex("starttime")));
                    row.createCell(4).setCellValue(c.getString(c.getColumnIndex("endtime")));

                    String weeks = "";

                    int week = day;
                    for(int i=0; i!=7; ++i) {
                        if(week % 2 == 1) weeks = weeks + days[i];
                        week /= 2;
                    }

                    row.createCell(2).setCellValue(weeks);

                    rows++;
                }

                File excelFile = new File(getContext().getFilesDir(),"user.xls");
                try{
                    FileOutputStream os = new FileOutputStream(excelFile);
                    workbook.write(os);
                }catch (IOException e){
                    e.printStackTrace();
                }

                Uri path = FileProvider.getUriForFile(getContext(), "com.test.fileprovider", excelFile);
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("application/excel");
                shareIntent.putExtra(Intent.EXTRA_STREAM,path);
                startActivity(Intent.createChooser(shareIntent,"엑셀 내보내기"));


            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}