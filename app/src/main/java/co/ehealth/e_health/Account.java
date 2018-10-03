package co.ehealth.e_health;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.muddzdev.styleabletoast.StyleableToast;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;


public class Account extends AppCompatActivity {

    private AutoCompleteTextView eUsernameView;

    private EditText ePasswordView;

    private Button eLoginButton, registerButton;

    private FirebaseAuth eAuth;

    private FirebaseAuth.AuthStateListener eAuthListener;

    Dialog registerDialog;

    private ImageView line;

    ProgressDialog progress;

    DatabaseReference eDatabase = FirebaseDatabase.getInstance()
            .getReference().child("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        eDatabase.keepSynced(true);
        eAuth = FirebaseAuth.getInstance();

        // Login Elements
        eUsernameView = (AutoCompleteTextView) findViewById(R.id.username);
        ePasswordView = (EditText) findViewById(R.id.password);
        eLoginButton = (Button) findViewById(R.id.signIn);
        registerButton = (Button) findViewById(R.id.reg);
        registerDialog = new Dialog(this);
        progress = new ProgressDialog(this);
        line = (ImageView) findViewById(R.id.iline);


        eLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                loginUser();

            }
        });



        eAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() != null) {

                    Intent accountIntent = new Intent(Account.this, Home.class);
                    accountIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(accountIntent);

                }

            }
        };

    }


    @Override
    protected void onStart() {
        super.onStart();
        eAuth.addAuthStateListener(eAuthListener);
    }


    public void showRegister(View v) {

        final AutoCompleteTextView Firstname, Lastname, Pass, Uname, RepeatPass;
        final Button cancelRegistration;
        registerDialog.setContentView(R.layout.register);

        Firstname = (AutoCompleteTextView) registerDialog.findViewById(R.id.firstname);
        Lastname = (AutoCompleteTextView) registerDialog.findViewById(R.id.lastname);
        Pass = (AutoCompleteTextView) registerDialog.findViewById(R.id.pass);
        RepeatPass = (AutoCompleteTextView) registerDialog.findViewById(R.id.pass2);
        Uname = (AutoCompleteTextView) registerDialog.findViewById(R.id.uname);
        final CircularProgressButton makeAccount = (CircularProgressButton) registerDialog.findViewById(R.id.sign_now);

        cancelRegistration = (Button) registerDialog.findViewById(R.id.close_dialog);

        cancelRegistration.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                registerDialog.dismiss();
                line.setVisibility(View.VISIBLE);
                registerButton.setVisibility(View.VISIBLE);
            }
        });

        registerDialog.setCanceledOnTouchOutside(false);
        registerDialog.show();
        line.setVisibility(View.GONE);
        registerButton.setVisibility(View.GONE);


        makeAccount.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                // Clear Errors
                Firstname.setError(null);
                Lastname.setError(null);
                Pass.setError(null);
                RepeatPass.setError(null);
                Uname.setError(null);

                boolean cancel = false;
                View focusView = null;

                final String first = Firstname.getText().toString();
                final String second = Lastname.getText().toString();
                String user = Uname.getText().toString();
                String passy = Pass.getText().toString();
                String repeatPass = RepeatPass.getText().toString();
                String newUser = user.concat("@ehealth.com");

                if (!TextUtils.isEmpty(first) && !TextUtils.isEmpty(second) &&
                        !TextUtils.isEmpty(user) && !TextUtils.isEmpty(passy) &&
                        !TextUtils.isEmpty(repeatPass)) {

                    // Check Password Length
                    if (isPasswordValid(passy)) {

                        Pass.setError(getString(R.string.password_length));
                        Pass.requestFocus();
                        cancel = true;

                    } else {


                        if (passy.equals(repeatPass)) {
                            cancelRegistration.setVisibility(View.GONE);
                            makeAccount.startAnimation();

                            eAuth.createUserWithEmailAndPassword(newUser, passy).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {


                                    if (task.isSuccessful()) {

                                        String user_id = eAuth.getCurrentUser().getUid();

                                        DatabaseReference userProfile = eDatabase.child(user_id);

                                        userProfile.child("Firstname").setValue(first);

                                        userProfile.child("Lastname").setValue(second);

                                        userProfile.child("Age").setValue("");

                                        userProfile.child("Gender").setValue("");

                                        userProfile.child("Role").setValue(3);

                                        userProfile.child("Location").setValue("");

                                        userProfile.child("Status").setValue("Welcome to eHealth Kenya");

                                        userProfile.child("Image").setValue("user.jpg");

                                        makeAccount.revertAnimation();

                                        Intent accountIntent = new Intent(Account.this, Home.class);

                                        accountIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                        startActivity(accountIntent);

                                    } else {


                                        makeAccount.revertAnimation();
                                        cancelRegistration.setVisibility(View.VISIBLE);

                                        StyleableToast.makeText(Account.this, "That Account Already Exist", Toast.LENGTH_LONG, R.style.error).show();


                                    }

                                }
                            });

                        } else {

                            StyleableToast.makeText(Account.this, "Passwords DO NOT Match", Toast.LENGTH_LONG, R.style.error).show();

                        }

                    }

                } else {

                    // Validate Firstname
                    if (TextUtils.isEmpty(first)) {

                        Firstname.setError(getString(R.string.required_firstname));
                        focusView = Firstname;
                        cancel = true;

                    }


                    // Validate Lastname
                    if (TextUtils.isEmpty(second)) {

                        Lastname.setError(getString(R.string.required_lastname));
                        focusView = Lastname;
                        cancel = true;

                    }


                    // Validate Username
                    if (TextUtils.isEmpty(user)) {

                        Uname.setError(getString(R.string.required_username));
                        focusView = Uname;
                        cancel = true;

                    }


                    // Validate Password
                    if (TextUtils.isEmpty(passy)) {

                        Pass.setError(getString(R.string.required_password));
                        focusView = Pass;
                        cancel = true;

                    }


                    // Validate Repeat Password
                    if (TextUtils.isEmpty(repeatPass)) {

                        RepeatPass.setError(getString(R.string.required_password));
                        focusView = RepeatPass;
                        cancel = true;

                    }


                }

            }
        });

    }


    private void loginUser() {

        // Clear Errors
        eUsernameView.setError(null);
        ePasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = eUsernameView.getText().toString();
        String password = ePasswordView.getText().toString();
        String newUsername = username.concat("@ehealth.com");

        boolean cancel = false;
        View focusView = null;


        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {

            progress.setMessage("Please Wait");

            progress.setCancelable(false);

            progress.show();

            eAuth.signInWithEmailAndPassword(newUsername, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (!task.isSuccessful()) {

                        progress.dismiss();

                        StyleableToast.makeText(Account.this, "Wrong Login Details...Please Try Again", Toast.LENGTH_LONG, R.style.error).show();

                    } else {

                        checkUserExist();

                        progress.dismiss();


                    }

                }
            });


        } else {

            // Validate Password
            if (TextUtils.isEmpty(password)) {

                ePasswordView.setError(getString(R.string.required_password));
                focusView = ePasswordView;
                cancel = true;

            }


            // Validate Username
            if (TextUtils.isEmpty(username)) {

                eUsernameView.setError(getString(R.string.required_username));
                focusView = eUsernameView;
                cancel = true;

            }


        }

    }


    private void checkUserExist() {

        final String user_id = eAuth.getCurrentUser().getUid();

        eDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(user_id)) {

                    Intent accountIntent = new Intent(Account.this, Home.class);

                    accountIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    startActivity(accountIntent);


                } else {
                    StyleableToast.makeText(Account.this, "You need to setup your account", Toast.LENGTH_LONG, R.style.error).show();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private boolean isPasswordValid(String password) {
        return password.length() < 6;
    }

}

