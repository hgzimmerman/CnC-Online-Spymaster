package com.mooo.ziggypop.candconline;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Created by ziggypop on 1/6/16.
 */

public class RealUsernameHandler {

    public static final String TAG = "JSwA";

    LinearLayout progressBarLayout;
    TextView textView;
    String url;
    String id;

    public RealUsernameHandler(String url, String pid, TextView textView, LinearLayout progressBarLayout){
        this.url = url;
        this.id = pid;
        this.textView = textView;
        this.progressBarLayout = progressBarLayout;
    }


    public void getUsername() {
        (new RealUsernameGetter() ).execute(url, id);
    }


    private class RealUsernameGetter extends AsyncTask<String, Void, String> {




        @Override
        protected String doInBackground(String... strings) {
            StringBuffer buffer = new StringBuffer();
            try {
                Log.d("JSwa", "Connecting to [" + strings[0] + "]");
                Document doc = Jsoup.connect(strings[0]).get();
                Log.d("JSwa", "Connected to [" + strings[0] + "]");
                // Get document (HTML page) title
                String title = doc.title();
                Log.d("JSwA", "Title [" + title + "]");

                String queryString = "[href*=/users/"+strings[1]+"/]";
                Log.v(TAG, "queryString: " +queryString);
                Log.v(TAG, doc.select(queryString).toString());
                buffer.append(doc.select(queryString).text());

            } catch (Throwable t) {
                t.printStackTrace();
            }

            return buffer.toString();
        }

        @Override
        protected void onPreExecute() {
            progressBarLayout.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            textView.setText(s);
            progressBarLayout.setVisibility(View.GONE);
        }
    }
}
