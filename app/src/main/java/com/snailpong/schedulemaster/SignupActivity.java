package com.snailpong.schedulemaster;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.snailpong.schedulemaster.model.UserModel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                if (email.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "이메일 주소를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    email.setBackgroundColor(Color.rgb(255, 204, 204));
                    name.setBackgroundColor(Color.TRANSPARENT);
                    password.setBackgroundColor(Color.TRANSPARENT);
                    return;
                } else if (!emailValid(email.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "정확한 이메일 주소를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    email.setBackgroundColor(Color.rgb(255, 204, 204));
                    name.setBackgroundColor(Color.TRANSPARENT);
                    password.setBackgroundColor(Color.TRANSPARENT);
                    return;
                } else if (name.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "닉네임을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    email.setBackgroundColor(Color.TRANSPARENT);
                    name.setBackgroundColor(Color.rgb(255, 204, 204));
                    password.setBackgroundColor(Color.TRANSPARENT);
                    return;
                } else if (password.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "패스워드를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    email.setBackgroundColor(Color.TRANSPARENT);
                    name.setBackgroundColor(Color.TRANSPARENT);
                    password.setBackgroundColor(Color.rgb(255, 204, 204));
                    return;
                }

                FirebaseAuth.getInstance()
                        .createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    final String uid = task.getResult().getUser().getUid();
                                    final StorageReference profileImageRef = FirebaseStorage.getInstance().getReference().child("userImages").child(uid);
                                    // 사진 등록 안 한 경우
                                    if (imageUri == null) {
                                        UserModel userModel = new UserModel();
                                        userModel.userName = name.getText().toString();
                                        userModel.profileImageUrl = "null";
                                        userModel.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                        userModel.email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                                        FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                SignupActivity.this.finish();
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
                                                    }
                                                });
                                            }
                                        });
                                    }
                                } else if (!task.isSuccessful() && task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    FirebaseAuthUserCollisionException exception =
                                            (FirebaseAuthUserCollisionException)task.getException();
                                    if (exception.getErrorCode().equals("ERROR_EMAIL_ALREADY_IN_USE")) {
                                        Toast.makeText(getApplicationContext(), "이미 가입되어 있습니다. 가입된 계정으로 로그인해 주세요.", Toast.LENGTH_SHORT).show();
                                        email.setBackgroundColor(Color.rgb(255, 204, 204));
                                        name.setBackgroundColor(Color.TRANSPARENT);
                                        password.setBackgroundColor(Color.TRANSPARENT);
                                    }
                                } else if (!task.isSuccessful() && task.getException() instanceof FirebaseAuthWeakPasswordException) {
                                    Toast.makeText(getApplicationContext(), "비밀번호는 6자 이상이어야 합니다.", Toast.LENGTH_SHORT).show();
                                    email.setBackgroundColor(Color.TRANSPARENT);
                                    name.setBackgroundColor(Color.TRANSPARENT);
                                    password.setBackgroundColor(Color.rgb(255, 204, 204));
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
    private boolean emailValid(String email) {
        String regex = "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        return m.matches();
    }
}