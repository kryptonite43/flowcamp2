package com.example.searchapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ResultActivity extends AppCompatActivity implements View.OnClickListener{
    private String search, email, nick, profimg;
    private WebView webView;
    private ArrayList<String> urls;
    private static OnMenuItemClickListener onMenuItemClickListener;
    private RetrofitInterface retrofitInterface;
    private RecentSearchListAdapter adapter;

    Button home;
    LinearLayout fullscreen;
    RelativeLayout relativeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Intent intent = getIntent();
        urls = new ArrayList<String>();
        urls.add("https://www.google.com/search?q="); // google urls
        urls.add("https://search.naver.com/search.naver?where=nexearch&sm=top_hty&fbm=1&ie=utf8&query="); // naver url
        urls.add("https://www.youtube.com/results?search_query="); // youtube url
        urls.add("https://en.m.wikipedia.org/w/index.php?search="); // wiki url

        search = intent.getStringExtra("search");
        nick = intent.getStringExtra("name");
        email = intent.getStringExtra("email");
        profimg = intent.getStringExtra("profileImg");
        Log.e("email", String.valueOf(email));
        Log.e("result", search);

        EditText tv = findViewById(R.id.restv);
        //Button menu = findViewById(R.id.menubutton);
        Button google = findViewById(R.id.google);
        Button naver = findViewById(R.id.naver);
        Button youtube = findViewById(R.id.youtube);
        Button wiki = findViewById(R.id.wiki);
        ListView lv = findViewById(R.id.listview);
        fullscreen = findViewById(R.id.fullscreen);
        
        onMenuItemClickListener = new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                search = search.replace(" ","+");
                switch( menuItem.getItemId() ){//눌러진 MenuItem의 Item Id를 얻어와 식별
                    case R.id.google:
                        webView.loadUrl(urls.get(0) +search);
                        Toast.makeText(ResultActivity.this, "google", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.naver:
                        webView.loadUrl(urls.get(1) +search);
                        Toast.makeText(ResultActivity.this, "naver", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.youtube:
                        webView.loadUrl(urls.get(2) +search);
                        Toast.makeText(ResultActivity.this, "youtube", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.wiki:
                        webView.loadUrl(urls.get(3) +search);
                        Toast.makeText(ResultActivity.this, "wiki", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        };
//        menu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                popUp(view);
//            }
//        });
//        WebView webview = findViewById(R.id.webview);
        relativeLayout = findViewById(R.id.relfullscreen);
        relativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideKeyboard();
                lv.setVisibility(View.GONE);
                return false;
            }
        });
        lv.bringToFront();
        home = findViewById(R.id.home);
        home.setOnClickListener(this::onClick);

        String BASE_URL = "http://192.249.18.161:443";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);

        tv.setText(search);
        tv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Call<List<String>> call = retrofitInterface.executeMyRecord(email);
                call.enqueue(new Callback<List<String>>() {
                    @Override
                    public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                        if (response.code() == 200) {
                            List<String> data = response.body();
                            adapter = new RecentSearchListAdapter(getApplicationContext(), retrofitInterface, nick, profimg, email, data);
                            lv.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        } else if (response.code() == 400) {
                            Toast.makeText(ResultActivity.this, "Failed to get recent search results", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<String>> call, @NonNull Throwable t) {
                        Toast.makeText(ResultActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
                lv.setVisibility(View.VISIBLE);
                return false;
            }
        });

        tv.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                switch (i) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        if (search.compareTo("") != 0) { // null값 return이 아닐 때만 동작
                            search = tv.getText().toString();
                            Intent intent = new Intent(ResultActivity.this, ResultActivity.class);
                            intent.putExtra("search", search);
                            intent.putExtra("name",nick);
                            intent.putExtra("email",email);
                            intent.putExtra("profileImg",profimg);
                            startActivity(intent);
                        }
                        break;
                }
                return true;
            }
        });

        tv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String subtext = tv.getText().toString();
                Call<List<String>> call = retrofitInterface.executeMyRecordStartsWith(email, subtext);
                call.enqueue(new Callback<List<String>>() {
                    @Override
                    public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                        if (response.code() == 200) {
                            List<String> data = response.body();
                            RecentSearchListAdapter adapter = new RecentSearchListAdapter(getApplicationContext(), retrofitInterface, nick, profimg, email, data);
                            lv.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<String>> call, Throwable t) {
                        Toast.makeText(ResultActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        webView = findViewById(R.id.webview);
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.e("webview touch","on");
                lv.setVisibility(View.GONE);
                return false;
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        search = search.replace(" ","+");
        webView.loadUrl(urls.get(0) +search);

        Button searchagain = findViewById(R.id.searchagain);
        searchagain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tv.getText().toString().compareTo("")!=0) {

                    Intent intent = new Intent(ResultActivity.this, ResultActivity.class);
                    intent.putExtra("search", tv.getText().toString());
                    intent.putExtra("name",nick);
                    intent.putExtra("email",email);
                    intent.putExtra("profileImg",profimg);
                    startActivity(intent);

                    HashMap<String, String> map = new HashMap<>();
                    map.put("email", email);
                    map.put("text", tv.getText().toString());

                    Call<Void> call = retrofitInterface.executeSearch(map);

                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.code() == 200) {
                                Toast.makeText(ResultActivity.this, "Posted search record", Toast.LENGTH_LONG).show();
                            } else if (response.code() == 400) {
                                Toast.makeText(ResultActivity.this, "Failed to post search record", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(ResultActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }
        });

        google.setOnClickListener(this);
        naver.setOnClickListener(this);
        youtube.setOnClickListener(this);
        wiki.setOnClickListener(this);


    }
    public final void popUp(View view) {
        PopupMenu popup = new PopupMenu(this, view); // 인자 (context, 팝업메뉴 연결 anchor 뷰)
        getMenuInflater().inflate(R.menu.menu_main, popup.getMenu()); // 메뉴아이템 건져서 메뉴 inflate
        popup.setOnMenuItemClickListener(onMenuItemClickListener); // onCreate에서 생성한 리스너를 팝업메뉴에 셋팅
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popup.setForceShowIcon(true);
        }
        //MenuPopupHelper menuPopupHelper = new MenuPopupHelper(this,(MenuBuilder)popup.getMenu(), view);
        // popup.show(view); //Popup Menu 보이기
//        MenuPopupHelper menuHelper = new MenuPopupHelper(this, (MenuBuilder) popup.getMenu(), view);
//        menuHelper.setForceShowIcon(true);
//        menuHelper.show();

        popup.show(); //Popup Menu 보이기
    }
    void hideKeyboard()
    {
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(fullscreen.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
    @Override
    public void onClick(View view) {
        search = search.replace(" ","+");
        switch (view.getId()) {
            case R.id.google:
                webView.loadUrl(urls.get(0) +search);
                break;
            case R.id.naver:
                webView.loadUrl(urls.get(1)+search);
                break;
            case R.id.youtube:
                webView.loadUrl(urls.get(2)+search);
                break;
            case R.id.wiki:
                webView.loadUrl(urls.get(3)+search);
                break;
            case R.id.home:
//                home.setVisibility(View.VISIBLE);
                Intent intent = new Intent(ResultActivity.this, SubActivity.class);
                intent.putExtra("search", search);
                intent.putExtra("name",nick);
                intent.putExtra("email",email);
                intent.putExtra("profileImg",profimg);
                startActivity(intent);
        }
    }
}