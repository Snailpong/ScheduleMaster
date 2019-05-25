package com.snailpong.schedulemaster;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.snailpong.schedulemaster.model.UserModel;

public class SignupActivity extends AppCompatActivity {
    private EditText email;
    private EditText name;
    private EditText password;
    private Button signup;
    private ImageView profileImage;
    private Uri imageUri;
    private static final int PICK_FROM_ALBUM = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        profileImage = (ImageView) findViewById(R.id.signupActivity_imageview_profile);
        profileImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, PICK_FROM_ALBUM);
            }
        });

        email = (EditText) findViewById(R.id.signupActivity_editText_email);
        name = (EditText) findViewById(R.id.signupActivity_editText_name);
        password = (EditText) findViewById(R.id.signupActivity_editText_password);
        signup = (Button) findViewById(R.id.signupActivity_button_signup);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 입력 폼 유효성 체크
                if (email.getText().toString().isEmpty() || name.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
                    return;
                }
                FirebaseAuth.getInstance()
                        .createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                final String uid = task.getResult().getUser().getUid();
                                final StorageReference profileImageRef = FirebaseStorage.getInstance().getReference().child("userImages").child(uid);
                                // 사진 등록 안 한 경우
                                if (imageUri == null) {
                                    UserModel userModel = new UserModel();
                                    userModel.userName = name.getText().toString();
                                    userModel.profileImageUrl = "null";
                                    userModel.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    userModel.email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                                    userModel.friends.put(uid, true);
                                    FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            SignupActivity.this.finish();
                                            // 회원가입 완료 후 이동할 프래그머트 지정할 것.
                                        }
                                    });
                                } else {
                                    profileImageRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            Task<Uri> taskUri = profileImageRef.getDownloadUrl();
                                            while (!taskUri.isSuccessful()) ;
                                            String imageUrl = String.valueOf(taskUri.getResult());

                                            UserModel userModel = new UserModel();
                                            userModel.userName = name.getText().toString();
                                            userModel.profileImageUrl = imageUrl;
                                            userModel.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                            userModel.email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                                            FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    SignupActivity.this.finish();
                                                    // 회원가입 완료 후 이동할 프래그머트 지정할 것.
                                                }
                                            });
                                        }
                                    });
                                }
                            }
                        });
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_FROM_ALBUM && resultCode == RESULT_OK) {
            profileImage.setImageURI(data.getData());       // 이미지뷰 업데이트
            imageUri = data.getData();                      // 이미지 경로
        }
    }
}