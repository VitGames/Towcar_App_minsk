package com.vitgames.towcar_minsk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class DriverRegLoginActivity extends AppCompatActivity {
    TextView status_driver, accountCreate;
    Button signIn_driver, signUp_driver;
    EditText driverEmail_ET, driverPassword_ET;
    FirebaseAuth mAuth;
    ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_reg_login);

        status_driver = (TextView)findViewById(R.id.status_driver); accountCreate = (TextView)findViewById(R.id.accountCreate);
        signIn_driver = (Button) findViewById(R.id.signIn_driver); signUp_driver = (Button) findViewById(R.id.signUp_driver);
        driverEmail_ET = (EditText)findViewById(R.id.driverEmail); driverPassword_ET = (EditText)findViewById(R.id.driverPassword);

        signUp_driver.setVisibility(View.INVISIBLE);
        signUp_driver.setEnabled(false);

        mAuth = FirebaseAuth.getInstance();
        loadingbar = new ProgressDialog(this);
        accountCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn_driver.setVisibility(View.INVISIBLE); signUp_driver.setVisibility(View.VISIBLE); signUp_driver.setEnabled(true);
                status_driver.setText("Регистрация для водителей");
                accountCreate.setVisibility(View.INVISIBLE);
            }
        });

        signUp_driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = driverEmail_ET.getText().toString();
                String password = driverPassword_ET.getText().toString();
                RegisterDriver(email, password);

            }
        });
         signIn_driver.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 String email = driverEmail_ET.getText().toString();
                 String password = driverPassword_ET.getText().toString();
                 SignInDriver(email, password);
             }
         });
    }

    private void SignInDriver(String email, String password) {
        loadingbar.setTitle("Вход в систему");
        loadingbar.setMessage("Пожалуйста, дождитесь загрузки");
        loadingbar.show();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(DriverRegLoginActivity.this, "Вы успешно вошли", Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                    Intent driverIntent = new Intent(DriverRegLoginActivity.this, DriversMapActivity.class);
                    startActivity(driverIntent);
                } else {
                    Toast.makeText(DriverRegLoginActivity.this, "Неправильный логин или пароль", Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                }
            }
        });

    }

    private void RegisterDriver(String email, String password) {
        loadingbar.setTitle("Регистрация нового аккаунта водителя...");
        loadingbar.setMessage("Пожалуйста, дождитесь загрузки");
        loadingbar.show();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete(@NonNull Task<AuthResult> task) {
            if(task.isSuccessful()){
                Toast.makeText(DriverRegLoginActivity.this, "Регистрация прошла успешно", Toast.LENGTH_SHORT).show();
                loadingbar.dismiss();
                Intent driverIntent = new Intent(DriverRegLoginActivity.this, DriversMapActivity.class);
                startActivity(driverIntent);
            } else {
                Toast.makeText(DriverRegLoginActivity.this, "Ошибка регистрации", Toast.LENGTH_SHORT).show();
                loadingbar.dismiss();
            }
          }
      });
    }
}