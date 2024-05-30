package com.example.test.ui.activities;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.test.R;
import com.example.test.firestoreclass.FirestoreClassKT;
import com.example.test.models.User;
import com.example.test.models.UserRole;
import com.example.test.utils.ClothesEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class RegisterActivity extends BaseActivity {
    private TextView tv_login;
    private Button btn_register;
    private Toolbar toolbar_register_activity;
    private ClothesEditText et_first_name, et_last_name, et_email, et_password, et_confirm_password;
    private CheckBox cb_terms_and_condition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().getInsetsController().hide(WindowInsets.Type.statusBars());
        } else {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
            );
        }
        addControl();
        setupActionBar();
        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void addControl() {
        tv_login = findViewById(R.id.tv_login);
        btn_register = findViewById(R.id.btn_register);
        et_first_name = findViewById(R.id.et_first_name);
        et_last_name = findViewById(R.id.et_last_name);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        et_confirm_password = findViewById(R.id.et_confirm_password);
        cb_terms_and_condition = findViewById(R.id.cb_terms_and_condition);
    }

    private void setupActionBar() {
        toolbar_register_activity = findViewById(R.id.toolbar_register_activity);
        setSupportActionBar(toolbar_register_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_black_color_back_24);
        toolbar_register_activity.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private boolean validateRegisterDetails() {
        if (TextUtils.isEmpty(et_first_name.getText().toString().trim())) {
            showErrorSnackBar(getString(R.string.err_msg_enter_first_name), true);
            return false;
        } else if (TextUtils.isEmpty(et_last_name.getText().toString().trim())) {
            showErrorSnackBar(getString(R.string.err_msg_enter_last_name), true);
            return false;
        } else if (TextUtils.isEmpty(et_email.getText().toString().trim())) {
            showErrorSnackBar(getString(R.string.err_msg_enter_email), true);
            return false;
        } else if (TextUtils.isEmpty(et_password.getText().toString().trim())) {
            showErrorSnackBar(getString(R.string.err_msg_enter_password), true);
            return false;
        } else if (TextUtils.isEmpty(et_confirm_password.getText().toString().trim())) {
            showErrorSnackBar(getString(R.string.err_msg_enter_confirm_password), true);
            return false;
        } else if (!et_password.getText().toString().trim().equals(et_confirm_password.getText().toString().trim())) {
            showErrorSnackBar(getString(R.string.err_msg_password_and_confirm_password_mismatch), true);
            return false;
        } else if (!cb_terms_and_condition.isChecked()) {
            showErrorSnackBar(getString(R.string.err_msg_agree_terms_and_condition), true);
            return false;
        }
        return true;
    }

    private String createIDKey(String password) {
        if (password.chars().allMatch(Character::isDigit)) {
            String firstThreeDigits = password.substring(0, 3);
            String remainingDigits = password.substring(3);
            String newPassword = remainingDigits + firstThreeDigits;
            return newPassword.chars()
                    .mapToObj(c -> Character.isLetter(c) ? c + IntStream.range(0, 3)
                            .mapToObj(i -> String.valueOf((char) ('a' + new Random().nextInt(26))))
                            .collect(Collectors.joining()) : c + IntStream.range(0, 3)
                            .mapToObj(i -> String.valueOf(new Random().nextInt(10)))
                            .collect(Collectors.joining()))
                    .collect(Collectors.joining());
        } else if (password.chars().allMatch(Character::isLetter)) {
            List<Character> alphabet = IntStream.rangeClosed('a', 'z')
                    .mapToObj(c -> (char) c)
                    .collect(Collectors.toList());
            String firstThreeLetters = password.substring(0, Math.min(password.length(), 3));
            String remainingLetters = password.substring(3);
            String shuffledPassword = remainingLetters + firstThreeLetters;
            return shuffledPassword.chars()
                    .mapToObj(c -> IntStream.range(0, 3)
                            .mapToObj(i -> String.valueOf(alphabet.get(new Random().nextInt(alphabet.size()))))
                            .collect(Collectors.joining()) + (char) c)
                    .collect(Collectors.joining());
        } else {
            String firstThree = password.substring(0, Math.min(password.length(), 3));
            List<Character> remainingChars = password.substring(3).chars()
                    .mapToObj(c -> (char) c)
                    .collect(Collectors.toList());
            remainingChars.addAll(firstThree.chars()
                    .mapToObj(c -> (char) c)
                    .collect(Collectors.toList()));

            StringBuilder newPassword = new StringBuilder();
            for (char c : remainingChars) {
                newPassword.append(IntStream.range(0, 3)
                        .mapToObj(i -> new Random().nextBoolean() ? String.valueOf(new Random().nextInt(10)) : String.valueOf((char) ('a' + new Random().nextInt(26))))
                        .collect(Collectors.joining()));
                newPassword.append(c);
            }
            return newPassword.toString();
        }
    }
    private void registerUser() {
        if (validateRegisterDetails()) {
            showProgressDialog(getString(R.string.please_wait));
            String email = et_email.getText().toString().trim();
            String password = et_password.getText().toString().trim();
            String idKey = createIDKey(password);
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = task.getResult().getUser();
                            User user = new User(
                                    firebaseUser.getUid(),
                                    et_first_name.getText().toString().trim(),
                                    et_last_name.getText().toString().trim(),
                                    email,
                                    UserRole.USER.name(), idKey
                            );
                            new FirestoreClassKT().registerUser(RegisterActivity.this, user);
                        } else {
                            hideProgressDialog();
                            showErrorSnackBar(task.getException().getMessage(), true);
                        }
                    });
        }
    }

    public void userRegistrationSuccess() {
        hideProgressDialog();
        Toast.makeText(
                this,
                getString(R.string.register_succes),
                Toast.LENGTH_SHORT
        ).show();
        FirebaseAuth.getInstance().signOut();
        finish();
    }
}
