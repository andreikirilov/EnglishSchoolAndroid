package englishschoolrestful;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class InfoActivity extends AppCompatActivity {

    private String user;
    private String select;
    private TextView hello;
    private TextView homework_date;
    private ListView list_info;
    private ProgressDialog progressDialog;
    private String date, topic, homework, mark;
    private ArrayList<HashMap<String, String>> classesList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        user = getIntent().getStringExtra("user");
        select = getIntent().getStringExtra("select");
        hello = findViewById(R.id.hello);
        homework_date = findViewById(R.id.homework_date);
        list_info = findViewById(R.id.list_info);
        if (select.equals("absence") || select.equals("retake")) {
            list_info.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> map = (Map<String, Object>) list_info.getItemAtPosition(position);
                    date = ((String) map.get("date")).substring(getResources().getString(R.string.date).length());
                    topic = ((String) map.get("topic")).substring(getResources().getString(R.string.topic).length());
                    homework = ((String) map.get("homework")).substring(getResources().getString(R.string.homework).length());
                    if (select.equals("absence")) {
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int button) {
                                switch (button) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        //TODO: INSERT INTO...
                                        Toast.makeText(getApplicationContext(), "Преподаватель предупрежден\nо вашем отсутствии " + date, Toast.LENGTH_LONG).show();
                                        break;
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        //TODO: INSERT INTO...
                                        Toast.makeText(getApplicationContext(), "Преподаватель ждет вас!", Toast.LENGTH_LONG).show();
                                        break;
                                }
                            }
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(InfoActivity.this);
                        builder.setMessage("Предупредить об отсутствии " + date + " Вы действительно хотите пропустить следующую тему: " + topic).setPositiveButton("Да", dialogClickListener).setNegativeButton("Нет", dialogClickListener).show();
                    } else if (select.equals("retake")) {
                        mark = ((String) map.get("mark")).substring(getResources().getString(R.string.mark).length());
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int button) {
                                switch (button) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        //TODO: INSERT INTO...
                                        Toast.makeText(getApplicationContext(), "Вы записались на пересдачу!", Toast.LENGTH_LONG).show();
                                        break;
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        //TODO: INSERT INTO...
                                        Toast.makeText(getApplicationContext(), "Вы решили подготовиться получше!", Toast.LENGTH_LONG).show();
                                        break;
                                }
                            }
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(InfoActivity.this);
                        builder.setMessage("Вы действительно хотите записаться на пересдачу темы: " + topic + " Ваш результат от " + date + " составил: " + mark).setPositiveButton("Да", dialogClickListener).setNegativeButton("Нет", dialogClickListener).show();
                    }
                }
            });
        }
        classesList = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String helloText = "Welcome, " + user + "!";
        hello.setText(helloText);
        String date_now = getResources().getString(R.string.date_now) + " " +
                (new SimpleDateFormat(getResources().getString(R.string.date_format), Locale.getDefault())).format(new Date());
        homework_date.setText(date_now);
        new selectAllInfo().execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class selectAllInfo extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(InfoActivity.this);
            progressDialog.setMessage(getApplicationContext().getResources().getString(R.string.loading));
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler httpHandler = new HttpHandler();
            String url = "http://" + getResources().getString(R.string.host) + "/info?name=" + user;
            String restResult = httpHandler.makeServiceCall(url);
            if (restResult != null) {
                JSONArray jsonArray;
                try {
                    jsonArray = new JSONArray(restResult);
                    if (select.equals("next") || select.equals("absence")) {
                        JSONObject jsonObject = jsonArray.getJSONObject(jsonArray.length() - 1);
                        HashMap<String, String> classes = new HashMap<>();
                        classes.put("date", getResources().getString(R.string.date) + jsonObject.getString("date"));
                        classes.put("topic", getResources().getString(R.string.topic) + jsonObject.getString("topic"));
                        classes.put("homework", getResources().getString(R.string.homework) + jsonObject.getString("homework"));
                        classesList.add(classes);
                    } else if (select.equals("all") || select.equals("retake")) {
                        for (int i = jsonArray.length() - 1; i >= 0; i--) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            HashMap<String, String> classes = new HashMap<>();
                            classes.put("date", getResources().getString(R.string.date) + jsonObject.getString("date"));
                            classes.put("topic", getResources().getString(R.string.topic) + jsonObject.getString("topic"));
                            classes.put("homework", getResources().getString(R.string.homework) + jsonObject.getString("homework"));
                            classes.put("mark", getResources().getString(R.string.mark) + jsonObject.getString("mark"));
                            classesList.add(classes);
                        }
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), R.string.json_parsing_error, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), R.string.json_getting_error, Toast.LENGTH_LONG).show();
            }
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
            ListAdapter listAdapter = new SimpleAdapter(InfoActivity.this, classesList, R.layout.list_item, new String[]{"date", "topic", "homework", "mark"}, new int[]{R.id.date, R.id.topic, R.id.homework, R.id.mark});
            list_info.setAdapter(listAdapter);
        }
    }
}