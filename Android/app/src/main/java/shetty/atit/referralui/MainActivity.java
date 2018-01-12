package shetty.atit.referralui;

import android.app.PendingIntent;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private  Context currentContext;

    private static final String URL = "http://10.0.2.2:9000/default/query/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        currentContext = this.getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSeekBarProps();

        showHideError(false,"");

        Button queryButton = (Button) findViewById(R.id.query_button);

        queryButton.setOnClickListener(new buttonListener());

    }

    private void setSeekBarProps(){
        final SeekBar needs1 = (SeekBar) findViewById(R.id.needs1);
        needs1.setOnSeekBarChangeListener(new seekListener(R.id.needs1_val));
        final SeekBar needs2 = (SeekBar) findViewById(R.id.needs2);
        needs2.setOnSeekBarChangeListener(new seekListener(R.id.needs2_val));
        final SeekBar needs3 = (SeekBar) findViewById(R.id.needs3);
        needs3.setOnSeekBarChangeListener(new seekListener(R.id.needs3_val));
        final SeekBar needs4 = (SeekBar) findViewById(R.id.needs4);
        needs4.setOnSeekBarChangeListener(new seekListener(R.id.needs4_val));
        final SeekBar result1 = (SeekBar) findViewById(R.id.result1);
        result1.setOnSeekBarChangeListener(new seekListener(R.id.result1_val));
        result1.setEnabled(false);
        final SeekBar result2 = (SeekBar) findViewById(R.id.result2);
        result2.setOnSeekBarChangeListener(new seekListener(R.id.result2_val));
        result2.setEnabled(false);
        final SeekBar result3 = (SeekBar) findViewById(R.id.result3);
        result3.setOnSeekBarChangeListener(new seekListener(R.id.result3_val));
        result3.setEnabled(false);
        final SeekBar result4 = (SeekBar) findViewById(R.id.result4);
        result4.setOnSeekBarChangeListener(new seekListener(R.id.result4_val));
        result4.setEnabled(false);
    }

    private void setSeekBarResult(int res1, int res2, int res3, int res4){
        final SeekBar result1 = (SeekBar) findViewById(R.id.result1);
        final SeekBar result2 = (SeekBar) findViewById(R.id.result2);
        final SeekBar result3 = (SeekBar) findViewById(R.id.result3);
        final SeekBar result4 = (SeekBar) findViewById(R.id.result4);

        result1.setProgress(res1);
        result2.setProgress(res2);
        result3.setProgress(res3);
        result4.setProgress(res4);

    }

    private String getSeekBarQuery(){
        final SeekBar needs1 = (SeekBar) findViewById(R.id.needs1);
        final SeekBar needs2 = (SeekBar) findViewById(R.id.needs2);
        final SeekBar needs3 = (SeekBar) findViewById(R.id.needs3);
        final SeekBar needs4 = (SeekBar) findViewById(R.id.needs4);

        String result = (((double)needs1.getProgress())/100)+",";
        result += (((double)needs2.getProgress())/100)+",";
        result += (((double)needs3.getProgress())/100)+",";
        result += (((double)needs4.getProgress())/100);

        return result;
    }

    private void showHideError(boolean showError, String errorMessage){
        final TextView errorHeader = (TextView) findViewById(R.id.error_header);
        final TextView errorBody = (TextView) findViewById(R.id.error_body);

        if(showError){
            errorBody.setText(errorMessage);
            errorHeader.setVisibility(View.VISIBLE);
            errorBody.setVisibility(View.VISIBLE);
        }else{
            errorHeader.setVisibility(View.INVISIBLE);
            errorBody.setVisibility(View.INVISIBLE);
        }
    }


    private class seekListener implements SeekBar.OnSeekBarChangeListener{

        private int textViewId;

        public seekListener(int textViewId){
            this.textViewId = textViewId;
        }
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {

            double result = (double) progress/100;
            final TextView t1 = (TextView) findViewById(this.textViewId);

            t1.setText(result+"");
        }

        public void onStartTrackingTouch(SeekBar seekBar) {}

        public void onStopTrackingTouch(SeekBar seekBar) {}
    }

    private class buttonListener implements Button.OnClickListener{
        @Override
        public void onClick(View view) {

            final View finalView = view;
            showHideError(false,null);

            view.setEnabled(false);

            String url = URL+getSeekBarQuery();

            //showHideError(true, "url is"+url);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.getString("status").equals("success")) {


                            double [] result = new ObjectMapper().readValue(response.getJSONArray("answer").toString(),double[].class);

                            setSeekBarResult((int)(result[0]*100) ,(int)(result[1]*100),(int)(result[2]*100),(int)(result[3]*100));
                            //showHideError(true, "atit"+response.getJSONArray("answer").toString());
                        } else {
                            showHideError(true, response.getString("message"));
                        }
                    }catch(Exception e){
                        showHideError(true, e.getMessage());
                    }

                    finalView.setEnabled(true);
                }
            },new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error) {
                    showHideError(true,error.getMessage());
                    finalView.setEnabled(true);

                }
            });

            RequestResponseSingleton.getInstance(currentContext).addToRequestQueue(request);
        }
    }
}
