package englishschoolrestful;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView homework_date;
    private EditText editTextName;
    private EditText editTextPass;
    private ProgressDialog progressDialog;
    private String restResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        homework_date = findViewById(R.id.homework_date);
        editTextName = findViewById(R.id.editTextName);
        editTextPass = findViewById(R.id.editTextPass);
        Button continue_button = findViewById(R.id.continue_button);
        Button call_button = findViewById(R.id.call_button);
        continue_button.setOnClickListener(onClickListener);
        call_button.setOnClickListener(onClickListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String date_now = getResources().getString(R.string.date_now) + " " +
                (new SimpleDateFormat(getResources().getString(R.string.date_format), Locale.getDefault())).format(new Date());
        homework_date.setText(date_now);
    }

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.continue_button:
                    if (NetworkManager.isNetworkAvailable(MainActivity.this)) {
                        new checkUserPass().execute();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.connection_error, Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.call_button:
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", getResources().getString(R.string.school_phone), null)));
                    break;
            }
        }
    };

    @SuppressLint("StaticFieldLeak")
    private class checkUserPass extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage(getApplicationContext().getResources().getString(R.string.loading));
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler httpHandler = new HttpHandler();
            String user = editTextName.getText().toString();
            String pass = editTextPass.getText().toString();
            MessageDigest messageDigest;
            byte[] digest = new byte[0];
            try {
                messageDigest = MessageDigest.getInstance("MD5");
                messageDigest.reset();
                messageDigest.update(pass.getBytes());
                digest = messageDigest.digest();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            BigInteger bigInt = new BigInteger(1, digest);
            StringBuilder md5Hex = new StringBuilder(bigInt.toString(16));
            while (md5Hex.length() < 32) {
                md5Hex.insert(0, "0");
            }
            String url = null;
            try {
                url = "http://" + getResources().getString(R.string.host) + "/check?name=" + URLEncoder.encode(user, "UTF-8") + "&pass=" + md5Hex.toString();
            } catch (UnsupportedEncodingException ignored) {
            }
            restResult = httpHandler.makeServiceCall(url);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (progressDialog.isShowing()) {
                try {
                    Thread.sleep(1500);
                    progressDialog.dismiss();
                } catch (InterruptedException ignored) {
                }
            }
            if (restResult != null) {
                if (restResult.trim().equals("success")) {
                    Intent intent = new Intent(MainActivity.this, FuncActivity.class);
                    intent.putExtra("user", editTextName.getText().toString());
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.checkUserPass_error, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), R.string.server_error, Toast.LENGTH_LONG).show();
            }
        }
    }
}