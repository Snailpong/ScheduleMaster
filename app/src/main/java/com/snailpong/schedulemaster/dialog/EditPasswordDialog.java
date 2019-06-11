package com.snailpong.schedulemaster.dialog;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.snailpong.schedulemaster.R;

public class EditPasswordDialog extends DialogFragment {

    private Fragment fragment;
    private EditText password1;
    private EditText password2;
    private Button updateBtn;
    private Button cancelBtn;
    private String uid;
    private DatabaseReference dataRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_editpassword, container, false);
        password1 = (EditText) view.findViewById(R.id.editpassword_password1);
        password2 = (EditText) view.findViewById(R.id.editpassword_password2);
        updateBtn = (Button) view.findViewById(R.id.editpassword_update);
        cancelBtn = (Button) view.findViewById(R.id.editpassword_cancel);
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        fragment = this;

        updateBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (password1.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "변경할 패스워드를 입력하십시요.", Toast.LENGTH_SHORT).show();
                } else if (password2.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "변경할 패스워드를 다시 입력하십시요.", Toast.LENGTH_SHORT).show();
                } else if (!password1.getText().toString().equals(password2.getText().toString())) {
                    Toast.makeText(getContext(), "패스워드가 일치하지 않습니다. 다시 입력하십시요.", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseAuth.getInstance().getCurrentUser().updatePassword(password1.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "패스워드 변경이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                DialogFragment dialogFragment = (DialogFragment) fragment;
                                dialogFragment.dismiss();
                            } else if (!task.isSuccessful() && task.getException() instanceof FirebaseAuthWeakPasswordException) {
                                Toast.makeText(getContext(), "비밀번호는 6자 이상이어야 합니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                DialogFragment dialogFragment = (DialogFragment) fragment;
                dialogFragment.dismiss();
            }
        });

        return view;
    }
}
