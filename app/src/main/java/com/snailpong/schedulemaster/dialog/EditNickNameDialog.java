package com.snailpong.schedulemaster.dialog;

import android.app.Dialog;
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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.snailpong.schedulemaster.R;

public class EditNickNameDialog extends DialogFragment {

    private Fragment fragment;
    private EditText input;
    private Button updateBtn;
    private Button cancelBtn;
    private String uid;
    private DatabaseReference dataRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_editnickname, container, false);
        Bundle args = getArguments();
        final String beforeName = args.getString("userName");
        input = (EditText) view.findViewById(R.id.editnickname_input);
        updateBtn = (Button) view.findViewById(R.id.editnickname_update);
        cancelBtn = (Button) view.findViewById(R.id.editnickname_cancel);
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        fragment = this;
        dataRef = FirebaseDatabase.getInstance().getReference("users/" + uid + "/userName");
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newName = input.getText().toString();
                if (newName.isEmpty()) {
                    Toast.makeText(getContext(), "변경할 닉네임을 입력하십시요.", Toast.LENGTH_SHORT).show();
                } else if (newName.equals(beforeName)) {
                    Toast.makeText(getContext(), "현재 닉네임과 동일합니다.", Toast.LENGTH_SHORT).show();
                } else {
                    dataRef.setValue(newName).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getContext(), "닉네임 변경이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                            DialogFragment dialogFragment = (DialogFragment) fragment;
                            dialogFragment.dismiss();
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
