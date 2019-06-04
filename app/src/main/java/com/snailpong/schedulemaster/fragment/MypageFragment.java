package com.snailpong.schedulemaster.fragment;

import android.content.Intent;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.snailpong.schedulemaster.MypageDeadlineActivity;
import com.snailpong.schedulemaster.MypageNoclassActivity;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.snailpong.schedulemaster.R;
import com.snailpong.schedulemaster.SignupActivity;
import com.snailpong.schedulemaster.TabbedActivity;
import com.snailpong.schedulemaster.dialog.EditNickNameDialog;
import com.snailpong.schedulemaster.dialog.EditPasswordDialog;
import com.snailpong.schedulemaster.model.UserModel;

public class MypageFragment extends Fragment {
    private FragmentManager fragmentManager = getFragmentManager();
    private TextView nick;
    private TextView mail;
    private ImageView profileImg;
    private LinearLayout profilelayout;
    private LinearLayout nicknamelayout;
    private LinearLayout editNickname;
    private LinearLayout editPassword;
    private LinearLayout friendlayout;
    private LinearLayout myfriend;
    private LinearLayout deadlist;
    private LinearLayout noclasslist;
    private Uri imageUri;
    private FirebaseAuth.AuthStateListener authStateListener;
    private static final int PICK_FROM_ALBUM = 10;
    final private CharSequence[] profileImgClickedItems = {"앨범에서 사진 선택", "기본 이미지로 변경"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mypage, container, false);
        nick = (TextView) view.findViewById(R.id.mypage_nick);
        mail = (TextView) view.findViewById(R.id.mypage_mail);
        profileImg = (ImageView) view.findViewById(R.id.mypage_profileimg);
        profilelayout = (LinearLayout) view.findViewById(R.id.mypage_profilelayout);
        nicknamelayout = (LinearLayout) view.findViewById(R.id.mypage_editinfolayout);
        editNickname = (LinearLayout) view.findViewById(R.id.mypage_editnickname);
        editPassword = (LinearLayout) view.findViewById(R.id.mypage_editpassword);
        friendlayout = (LinearLayout) view.findViewById(R.id.mypage_friendlayout);
        myfriend = (LinearLayout) view.findViewById(R.id.mypage_friend);
        deadlist = (LinearLayout) view.findViewById(R.id.mypage_deadline);
        noclasslist = (LinearLayout) view.findViewById(R.id.mypage_noclass);

        deadlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MypageDeadlineActivity.class);
                startActivity(intent);
            }
        });

        noclasslist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MypageNoclassActivity.class);
                startActivity(intent);
            }
        });

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    String uid = user.getUid();
                    String email = user.getEmail();
                    DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("users/" + uid);
                    mail.setText(email);

                    dataRef.child("userName").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String name = dataSnapshot.getValue(String.class);
                            nick.setText(name);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    dataRef.child("profileImageUrl").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String url = dataSnapshot.getValue(String.class);
                            // 사용자가 이미지를 업로드한 경우
                            if (!url.equals("null")) {
                                while (getActivity() == null);
                                Glide.with(getContext()).load(url).into(profileImg);
                            } else {
                                profileImg.setImageResource(R.drawable.ic_sentiment_satisfied_gray_24dp);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    profileImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog.Builder profileImgOptionDialog = new AlertDialog.Builder(getContext(),
                                    android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);

                            profileImgOptionDialog.setTitle("프로필 이미지 변경")
                                    .setItems(profileImgClickedItems, new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            switch (which) {
                                                case 0: {
                                                    Intent intent = new Intent(Intent.ACTION_PICK);
                                                    intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                                                    startActivityForResult(intent, PICK_FROM_ALBUM);
                                                    break;
                                                }
                                                case 1: {
                                                    deleteProfileImage(false);
                                                    break;
                                                }
                                            }
                                        }
                                    })
                                    .setCancelable(true)
                                    .show();
                        }
                    });
                    myfriend.setOnClickListener(new View.OnClickListener(){

                        @Override
                        public void onClick(View view) {
                            TabbedActivity activity = (TabbedActivity) getActivity();
                            activity.onFragmentChange(0);
                        }
                    });
                    editNickname.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            Bundle args = new Bundle();
                            args.putString("userName", nick.getText().toString());
                            EditNickNameDialog dialog = new EditNickNameDialog();
                            dialog.setArguments(args);
                            dialog.show(getActivity().getSupportFragmentManager(),"tag");
                        }
                    });
                    editPassword.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            EditPasswordDialog dialog = new EditPasswordDialog();
                            dialog.show(getActivity().getSupportFragmentManager(), "tag");
                        }
                    });
                    profilelayout.setVisibility(View.VISIBLE);
                    nicknamelayout.setVisibility(View.VISIBLE);
                    editNickname.setVisibility(View.VISIBLE);
                    editPassword.setVisibility(View.VISIBLE);
                    friendlayout.setVisibility(View.VISIBLE);
                    myfriend.setVisibility(View.VISIBLE);
                } else {
                    profilelayout.setVisibility(View.GONE);
                    nicknamelayout.setVisibility(View.GONE);
                    editNickname.setVisibility(View.GONE);
                    editPassword.setVisibility(View.GONE);
                    friendlayout.setVisibility(View.GONE);
                    myfriend.setVisibility(View.GONE);
                }
            }
        };

        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_FROM_ALBUM && resultCode == Activity.RESULT_OK) {
            //profileImage.setImageURI(data.getData());       // 이미지뷰 업데이트
            imageUri = data.getData();                      // 이미지 경로
            System.out.println("사진 선택! 프로필 이미지 변경 함수 호출");
            changeProfileImage();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
    }

    public void changeProfileImage() {
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final StorageReference profileImageRef = FirebaseStorage.getInstance().getReference().child("userImages").child(uid);
        System.out.println("프로필 이미지 변경 메소드 호출");
        // 사진 등록 안 한 경우
        profileImageRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                System.out.println("파일 업로드 성공");
                Task<Uri> taskUri = profileImageRef.getDownloadUrl();
                while (!taskUri.isSuccessful());
                String imageUrl = String.valueOf(taskUri.getResult());
                System.out.println(imageUrl);
                FirebaseDatabase.getInstance().getReference("users/" + uid + "/profileImageUrl").setValue(imageUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "프로필 이미지 변경이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                        System.out.println("프로필 이미지 변경 완료");
                    }
                });
            }
        });
    }

    public void deleteProfileImage(final boolean change) {
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseStorage.getInstance().getReference().child("userImages").child(uid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                FirebaseDatabase.getInstance().getReference("users/" + uid + "/profileImageUrl").setValue("null");
                if (!change) {
                    Toast.makeText(getContext(), "이미지 제거가 완료되었습니다.", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}