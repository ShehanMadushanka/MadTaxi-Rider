package com.madapps.madtaxi_rider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.madapps.madtaxi_rider.model.Rider;
import com.rengwuxian.materialedittext.MaterialEditText;

import dmax.dialog.SpotsDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSION = 1000;
    Button btnSignIn;
    Button btnRegister;
    RelativeLayout rootLayout;
    SpotsDialog waitingDialog;
    FirebaseAuth mAuth;
    FirebaseDatabase mDataBase;
    DatabaseReference mRiders;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Arkhip_font.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mDataBase = FirebaseDatabase.getInstance();
        mRiders = mDataBase.getReference("Riders");

        btnRegister = findViewById(R.id.btn_register);
        btnSignIn = findViewById(R.id.btn_sign_in);
        rootLayout = findViewById(R.id.rl_root);

        btnSignIn.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                showRegisterDialog();
                break;
            case R.id.btn_sign_in:
                showLoginDialog();
                break;
        }
    }

    private void showWaiting() {
        waitingDialog.show();
    }

    private void hideWaiting() {
        waitingDialog.dismiss();
    }

    private void showLoginDialog() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("LOGIN");
        dialogBuilder.setMessage("Please use email to login");

        LayoutInflater inflater = LayoutInflater.from(this);
        @SuppressLint("InflateParams") View login_layout = inflater.inflate(R.layout.layout_sign_in, null);

        final MaterialEditText etEmail = login_layout.findViewById(R.id.et_email);
        final MaterialEditText etPassword = login_layout.findViewById(R.id.et_password);

        dialogBuilder.setView(login_layout);
        dialogBuilder.setPositiveButton("LOGIN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
                btnSignIn.setEnabled(false);

                showWaiting();

                if (TextUtils.isEmpty(etEmail.getText())) {
                    Snackbar.make(rootLayout, "Email can not be empty!", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etPassword.getText())) {
                    Snackbar.make(rootLayout, "Password can not be empty!", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(etEmail.getText().toString(), etPassword.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
//                                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                hideWaiting();
                                Snackbar.make(rootLayout, "Failed " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                                btnSignIn.setEnabled(true);
                            }
                        });
            }
        });
        dialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        dialogBuilder.show();
    }

    private void showRegisterDialog() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("REGISTER");
        dialogBuilder.setMessage("Please use email to register");

        LayoutInflater inflater = LayoutInflater.from(this);
        @SuppressLint("InflateParams") View register_layout = inflater.inflate(R.layout.layout_register, null);

        final MaterialEditText etEmail = register_layout.findViewById(R.id.et_email);
        final MaterialEditText etName = register_layout.findViewById(R.id.et_name);
        final MaterialEditText etPassword = register_layout.findViewById(R.id.et_password);
        final MaterialEditText etMobile = register_layout.findViewById(R.id.et_phone);

        dialogBuilder.setView(register_layout);
        dialogBuilder.setPositiveButton("REGISTER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
                showWaiting();

                if (TextUtils.isEmpty(etEmail.getText())) {
                    Snackbar.make(rootLayout, "Email can not be empty!", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etMobile.getText())) {
                    Snackbar.make(rootLayout, "Mobile can not be empty!", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etName.getText())) {
                    Snackbar.make(rootLayout, "Name can not be empty!", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etPassword.getText())) {
                    Snackbar.make(rootLayout, "Password can not be empty!", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (etPassword.getText().toString().length() < 6) {
                    Snackbar.make(rootLayout, "Password should have more than 6 characters!", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(etEmail.getText().toString(), etPassword.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Rider newRider = new Rider(etName.getText().toString(), etEmail.getText().toString(), etPassword.getText().toString(), etMobile.getText().toString());
                                mRiders.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(newRider)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Snackbar.make(rootLayout, "Register success!", Snackbar.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                hideWaiting();
                                                Snackbar.make(rootLayout, "Failed " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(rootLayout, "Failed " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        dialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        dialogBuilder.show();
    }

}
