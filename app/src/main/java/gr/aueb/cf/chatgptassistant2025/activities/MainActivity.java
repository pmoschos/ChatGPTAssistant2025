package gr.aueb.cf.chatgptassistant2025.activities;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import gr.aueb.cf.chatgptassistant2025.R;
import gr.aueb.cf.chatgptassistant2025.adapters.PostAdapter;
import gr.aueb.cf.chatgptassistant2025.helpers.SQLiteDBHelper;
import gr.aueb.cf.chatgptassistant2025.interfaces.ApiResponseCallback;
import gr.aueb.cf.chatgptassistant2025.models.Post;
import gr.aueb.cf.chatgptassistant2025.utils.ChatGPTSecret;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText questionET;
    private Button sendBtn;
    private ProgressBar progressBar;
    private ArrayList<Post> postArrayList;
    private PostAdapter postAdapter;
    private SQLiteDBHelper sqLiteDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Ρυθμίζω το toolbar μου
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Objects.requireNonNull(getSupportActionBar()).setTitle("ChatGPT Assistant");
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        // Δημιουργώ έναν δικό μου custom title
        TextView titleTextView = new TextView(this);
        titleTextView.setText(R.string.chat_gpt_assistant);
        titleTextView.setTextColor(Color.WHITE);
        titleTextView.setTextSize(20f);
        titleTextView.setTypeface(null, Typeface.BOLD);
        // center align the title
        Toolbar.LayoutParams layoutParams = new Toolbar.LayoutParams(
                Toolbar.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
        );
        titleTextView.setLayoutParams(layoutParams);
        toolbar.addView(titleTextView);

        initViews();
        initRecyclerView();

        sqLiteDBHelper = new SQLiteDBHelper(this);
        loadPostsFromDatabase();
        handleSendButton();

    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view);
        questionET = findViewById(R.id.question_et);
        sendBtn = findViewById(R.id.send_btn);
        progressBar = findViewById(R.id.progressBar);
    }

    private void initRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);

        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        postArrayList = new ArrayList<>();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadPostsFromDatabase() {
        ArrayList<Post> loadedPosts = sqLiteDBHelper.getAllPosts();
        if (loadedPosts != null) {
            postArrayList.clear();
            postArrayList.addAll(loadedPosts);
        }

        if (postAdapter == null) {
            postAdapter = new PostAdapter(postArrayList, this);
            recyclerView.setAdapter(postAdapter);
        } else {
            postAdapter.notifyDataSetChanged();
        }
    }

    private void addPostToDatabase(String question, String response) {
        SQLiteDatabase dbd = sqLiteDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SQLiteDBHelper.POST_QUESTION, question);
        values.put(SQLiteDBHelper.POST_RESPONSE, response);

        long newRowId = dbd.insert(SQLiteDBHelper.TABLE_NAME, null, values);
        if (newRowId != -1) {
            Post newPost = new Post((int)newRowId, question, response);
            postArrayList.add(newPost);
            postAdapter.notifyItemInserted(postArrayList.size() - 1);
            recyclerView.scrollToPosition(postArrayList.size() - 1);
            questionET.setText("");
            Toast.makeText(this, "Post created", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to create post", Toast.LENGTH_SHORT).show();
        }
    }

    private void callApi(String question, final ApiResponseCallback callback) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://api.openai.com/v1/chat/completions";

        JSONObject requestBodyJson = new JSONObject();
        try {
            requestBodyJson.put("model", "gpt-3.5-turbo");

            JSONArray messagesArray = new JSONArray();

            JSONObject systemMessage = new JSONObject();
            systemMessage.put("role", "system");
            systemMessage.put("content", "You are a helpful assistant");
            messagesArray.put(systemMessage);

            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", question);
            messagesArray.put(userMessage);
            requestBodyJson.put("messages", messagesArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                requestBodyJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Volley", "Response: " + response);
                        try {
                            JSONArray choicesArray = response.getJSONArray("choices");
                            if (choicesArray.length() > 0) {
                                JSONObject choice = choicesArray.getJSONObject(0);
                                JSONObject messageObject = choice.getJSONObject("message");
                                String textResponse = messageObject.getString("content");
                                callback.onResponseReceived(textResponse);
                            } else {
                                callback.onResponseReceived(null);
                            }

                        } catch (JSONException e) {
                            Log.e("Volley", "JSON parsing error: ", e);
                            callback.onResponseReceived(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "JSON parsing error: " + error.getMessage());
                        callback.onResponseReceived(null);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + ChatGPTSecret.MY_KEY);
                return headers;
            }
        };

        queue.add(jsonObjectRequest);
    }

    // Αναζήτηση στη Β.Δ.
    @SuppressLint("NotifyDataSetChanged")
    private void performSearch(String query) {
        if (query.isEmpty()) {
            loadPostsFromDatabase();
            return;
        }
        ArrayList<Post> results = sqLiteDBHelper.searchPosts(query);
        postArrayList.clear();
        postArrayList.addAll(results);
        postAdapter.notifyDataSetChanged();
    }

    private void handleSendButton() {
        sendBtn.setOnClickListener(view -> {
            String question = questionET.getText().toString().trim();

            if (question.isEmpty()) {
                questionET.setError("Please insert some text...");
                questionET.requestFocus();
                return;
            }

            sendBtn.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);

            callApi(question, new ApiResponseCallback() {
                @Override
                public void onResponseReceived(String response) {
                    sendBtn.setEnabled(true);
                    progressBar.setVisibility(View.GONE);

                    if (response != null && !response.isEmpty()) {
                        addPostToDatabase(question, response);
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to get the response", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        if (searchItem != null) {
            SearchView searchView = (SearchView) searchItem.getActionView();

            if (searchView != null) {
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        performSearch(newText);
                        return true;
                    }
                });
            }
        }

        return super.onCreateOptionsMenu(menu);
    }
}