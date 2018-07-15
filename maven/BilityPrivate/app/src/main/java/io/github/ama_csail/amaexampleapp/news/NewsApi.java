package io.github.ama_csail.amaexampleapp.news;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.LinkedList;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Hooks for the News API from newsapi.org
 * You should not need to touch this file for the purposes of the project
 * @author Aaron Vontell
 */
public class NewsApi {

    private static final String API_KEY = "d3f0c5c2b1124f8793c61c57639a5a35";

    private static final String HOST = "https://newsapi.org/v1/articles?source=";
    private static final String EXAMPLE = HOST + "ars-technica&sortBy=latest&apiKey=" + API_KEY;

    public static List<Article> getArsNews() {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(EXAMPLE)
                .build();

        try {
            Response response = client.newCall(request).execute();
            String result = response.body().string();
            JSONObject jobj = new JSONObject(result);

            LinkedList<Article> headlines = new LinkedList<>();

            JSONArray articles = jobj.getJSONArray("articles");

            for(int i = 0; i < articles.length(); i++) {

                JSONObject article = articles.getJSONObject(i);
                String source = "Ars Technica";
                String author = article.getString("author");
                String title = article.getString("title");
                String description = article.getString("description");
                String url = article.getString("url");
                String urlToImage = article.getString("urlToImage");
                String publishedAt = article.getString("publishedAt");

                Article headline = new Article(source, title, author, description, url, urlToImage, publishedAt);
                headlines.add(headline);

            }

            return headlines;

        } catch (Exception e) {
            Log.e("NEWS ERROR", e.toString());
            return new LinkedList<>();
        }

    }

}