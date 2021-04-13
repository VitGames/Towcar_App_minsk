package com.vitgames.towcar_minsk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class ClientRegLoginActivity extends AppCompatActivity {

    TextView status_client, accountCreate_client;
    Button signIn_client, signUp_client;
    EditText clientEmail_ET, clientPassword_ET;
    FirebaseAuth mAuth;
    ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_reg_login);
        status_client = (TextView) findViewById(R.id.status_client);
        accountCreate_client = (TextView) findViewById(R.id.accountCreate_client);
        signIn_client = (Button) findViewById(R.id.signIn_client);
        signUp_client = (Button) findViewById(R.id.signUp_client);
        clientEmail_ET = (EditText) findViewById(R.id.clientEmail);
        clientPassword_ET = (EditText) findViewById(R.id.clientPassword);

        signUp_client.setVisibility(View.INVISIBLE);
        signUp_client.setEnabled(false);
        mAuth = FirebaseAuth.getInstance();
        loadingbar = new ProgressDialog(this);
        accountCreate_client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn_client.setVisibility(View.INVISIBLE);
                signUp_client.setVisibility(View.VISIBLE);
                signUp_client.setEnabled(true);
                status_client.setText("Регистрация для пользователей");
                accountCreate_client.setVisibility(View.INVISIBLE);
            }
        });
        signUp_client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = clientEmail_ET.getText().toString();
                String password = clientPassword_ET.getText().toString();
                RegisterClient(email, password);

            }
        });
        signIn_client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = clientEmail_ET.getText().toString();
                String password = clientPassword_ET.getText().toString();
                SignInClient(email, password);
            }
        });
    }

    private void SignInClient(String email, String password) {
        loadingbar.setTitle("Вход в систему пользователя");
        loadingbar.setMessage("Пожалуйста, дождитесь загрузки");
        loadingbar.show();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ClientRegLoginActivity.this, "Вы успешно вошли", Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                    Intent clientmap = new Intent (ClientRegLoginActivity.this, ClientMapActivity.class);
                    startActivity(clientmap);
                } else {
                    Toast.makeText(ClientRegLoginActivity.this, "Неправильный логин или пароль", Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                }
            }
        });
    }

    private void RegisterClient(String email, String password) {
        loadingbar.setTitle("Регистрация нового пользователя...");
        loadingbar.setMessage("Пожалуйста, дождитесь загрузки");
        loadingbar.show();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ClientRegLoginActivity.this, "Регистрация прошла успешно", Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                    Intent clientmap = new Intent (ClientRegLoginActivity.this, ClientMapActivity.class);
                    startActivity(clientmap);
                } else {
                    Toast.makeText(ClientRegLoginActivity.this, "Ошибка регистрации", Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                }
            }
        });
    }
}