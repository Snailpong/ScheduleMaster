package com.snailpong.schedulemaster;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SyncActivity extends AppCompatActivity {

    LinearLayout importl, exportl;
    FirebaseUser currentFirebaseUser;
    DatabaseReference mDatabase;
    DBHelper helper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);

        helper = new DBHelper(SyncActivity.this, "db.db", null, 1);
        db = helper.getWritableDatabase();
        helper.onCreate(db);

        setTitle("동기화");
        importl = (LinearLayout)findViewById(R.id.sync_import);
        exportl = (LinearLayout)findViewById(R.id.sync_export);
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentFirebaseUser == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SyncActivity.this);
            builder.setMessage("계정 연동 후 사용할 수 있는 기능입니다.");
            builder.setPositiveButton("확인",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            builder.show();
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();

        importl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SyncActivity.this);
                builder.setMessage("서버에 있는 데이터로 덮어씌여집니다. 진행하겠습니까?");
                builder.setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ///////////
                                finish();
                            }
                        });
                builder.setNegativeButton("아니오", null);
                builder.show();
            }
        });
        exportl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SyncActivity.this);
                builder.setMessage("원래 서버에 있는 데이터는 삭제됩니다. 진행하겠습니까?");
                builder.setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                mDatabase.child("users").child(currentFirebaseUser.getUid()).child("weekly").setValue(null);
                                mDatabase.child("users").child(currentFirebaseUser.getUid()).child("daily").setValue(null);

                                Cursor c = db.query("weekly", null, null, null, null, null, null);
                                while(c.moveToNext()) {
                                    int id = c.getInt(c.getColumnIndex("_id"));

                                    mDatabase.child("users").child(currentFirebaseUser.getUid()).child("weekly").child(String.valueOf(id)).child("id").setValue(c.getInt(c.getColumnIndex("_id")));
                                    mDatabase.child("users").child(currentFirebaseUser.getUid()).child("weekly").child(String.valueOf(id)).child("name").setValue(c.getString(c.getColumnIndex("name")));
                                    mDatabase.child("users").child(currentFirebaseUser.getUid()).child("weekly").child(String.valueOf(id)).child("day").setValue(c.getInt(c.getColumnIndex("day")));
                                    mDatabase.child("users").child(currentFirebaseUser.getUid()).child("weekly").child(String.valueOf(id)).child("starttime").setValue(c.getString(c.getColumnIndex("starttime")));
                                    mDatabase.child("users").child(currentFirebaseUser.getUid()).child("weekly").child(String.valueOf(id)).child("endtime").setValue(c.getString(c.getColumnIndex("endtime")));
                                    mDatabase.child("users").child(currentFirebaseUser.getUid()).child("weekly").child(String.valueOf(id)).child("vib").setValue(c.getInt(c.getColumnIndex("vib")));
                                    mDatabase.child("users").child(currentFirebaseUser.getUid()).child("weekly").child(String.valueOf(id)).child("gps").setValue(c.getInt(c.getColumnIndex("gps")));
                                    mDatabase.child("users").child(currentFirebaseUser.getUid()).child("weekly").child(String.valueOf(id)).child("y").setValue(c.getDouble(c.getColumnIndex("y")));
                                    mDatabase.child("users").child(currentFirebaseUser.getUid()).child("weekly").child(String.valueOf(id)).child("x").setValue(c.getDouble(c.getColumnIndex("x")));
                                }

                                c = db.query("daily", null, null, null, null, null, null);
                                while(c.moveToNext()) {
                                    int id = c.getInt(c.getColumnIndex("_id"));

                                    mDatabase.child("users").child(currentFirebaseUser.getUid()).child("daily").child(String.valueOf(id)).child("id").setValue(c.getInt(c.getColumnIndex("_id")));
                                    mDatabase.child("users").child(currentFirebaseUser.getUid()).child("daily").child(String.valueOf(id)).child("name").setValue(c.getString(c.getColumnIndex("name")));
                                    mDatabase.child("users").child(currentFirebaseUser.getUid()).child("daily").child(String.valueOf(id)).child("day").setValue(c.getString(c.getColumnIndex("day")));
                                    mDatabase.child("users").child(currentFirebaseUser.getUid()).child("daily").child(String.valueOf(id)).child("starttime").setValue(c.getString(c.getColumnIndex("starttime")));
                                    mDatabase.child("users").child(currentFirebaseUser.getUid()).child("daily").child(String.valueOf(id)).child("endtime").setValue(c.getString(c.getColumnIndex("endtime")));
                                    mDatabase.child("users").child(currentFirebaseUser.getUid()).child("daily").child(String.valueOf(id)).child("vib").setValue(c.getInt(c.getColumnIndex("vib")));
                                    mDatabase.child("users").child(currentFirebaseUser.getUid()).child("daily").child(String.valueOf(id)).child("gps").setValue(c.getInt(c.getColumnIndex("gps")));
                                    mDatabase.child("users").child(currentFirebaseUser.getUid()).child("daily").child(String.valueOf(id)).child("y").setValue(c.getDouble(c.getColumnIndex("y")));
                                    mDatabase.child("users").child(currentFirebaseUser.getUid()).child("daily").child(String.valueOf(id)).child("x").setValue(c.getDouble(c.getColumnIndex("x")));
                                }
                                finish();
                            }
                        });
                builder.setNegativeButton("아니오", null);
                builder.show();
            }
        });
    }
}
