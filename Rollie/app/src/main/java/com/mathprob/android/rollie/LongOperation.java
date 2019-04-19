package com.mathprob.android.rollie;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;


public class LongOperation extends AsyncTask<String, Void,  String> {

    private RequestQueue req;
    private EditText inviteCode;

    @Override
    protected String doInBackground(String... params) {
        String URL = "https://api.myjson.com/bins/tq6qk";
        JsonObjectRequest plz = new JsonObjectRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject propertiesGeneral = response.getJSONObject("addition");
                            String readable = propertiesGeneral.getString("a");
                            readable = readable.replaceAll("https://api.myjson.com/bins/", "");
                            inviteCode.setText("The invite code is: " + readable.toUpperCase());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.e("Ha", String.valueOf(error));
                    }
                });
        req.add(plz);
        return"penis";
    }

    @Override
    protected void onPostExecute(String result) {

        // txt.setText(result);
        // might want to change "executed" for the returned string passed
        // into onPostExecute() but that is upto you
    }

    @Override
    protected void onPreExecute() {}

    @Override
    protected void onProgressUpdate(Void... values) {}
}

