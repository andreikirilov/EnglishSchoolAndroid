package englishschoolrestful;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FuncActivity extends AppCompatActivity {

    private String user;
    private TextView hello;
    private TextView homework_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_func);

        user = getIntent().getStringExtra("user");
        hello = findViewById(R.id.hello);
        homework_date = findViewById(R.id.homework_date);
        Button next_button = findViewById(R.id.next_button);
        Button absence_button = findViewById(R.id.absence_button);
        Button select_button = findViewById(R.id.select_button);
        Button retake_button = findViewById(R.id.retake_button);
        Button call_button = findViewById(R.id.call_button);
        next_button.setOnClickListener(onClickListener);
        absence_button.setOnClickListener(onClickListener);
        select_button.setOnClickListener(onClickListener);
        retake_button.setOnClickListener(onClickListener);
        call_button.setOnClickListener(onClickListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String helloText = "Welcome, " + user + "!";
        hello.setText(helloText);
        String date_now = getResources().getString(R.string.date_now) + " " +
                (new SimpleDateFormat(getResources().getString(R.string.date_format), Locale.getDefault())).format(new Date());
        homework_date.setText(date_now);
    }

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.next_button:
                    if (NetworkManager.isNetworkAvailable(FuncActivity.this)) {
                        Intent intent = new Intent(FuncActivity.this, InfoActivity.class);
                        intent.putExtra("user", user);
                        intent.putExtra("select", "next");
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.connection_error, Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.absence_button:
                    if (NetworkManager.isNetworkAvailable(FuncActivity.this)) {
                        Intent intent = new Intent(FuncActivity.this, InfoActivity.class);
                        intent.putExtra("user", user);
                        intent.putExtra("select", "absence");
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.connection_error, Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.select_button:
                    if (NetworkManager.isNetworkAvailable(FuncActivity.this)) {
                        Intent intent = new Intent(FuncActivity.this, InfoActivity.class);
                        intent.putExtra("user", user);
                        intent.putExtra("select", "all");
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.connection_error, Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.retake_button:
                    if (NetworkManager.isNetworkAvailable(FuncActivity.this)) {
                        Intent intent = new Intent(FuncActivity.this, InfoActivity.class);
                        intent.putExtra("user", user);
                        intent.putExtra("select", "retake");
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.connection_error, Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.call_button:
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", getResources().getString(R.string.teacher_phone), null)));
                    break;
            }
        }
    };
}