package com.example.searchapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.text.Editable;
import android.text.TextWatcher;

import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.sdk.user.UserApiClient;

import java.util.HashMap;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SubActivity extends AppCompatActivity { // 검색창 뜨는 액티비티
    private String strNick, strProfileImg, strEmail;
    private RetrofitInterface retrofitInterface;
    private static PopupMenu.OnMenuItemClickListener onMenuItemClickListener;
    private ListView searchlist;
    private RecentSearchListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);
        Intent intent = getIntent();
        strNick = intent.getStringExtra("name");
        strProfileImg = intent.getStringExtra("profileImg");
        strEmail = intent.getStringExtra("email");

        FrameLayout nickframe = findViewById(R.id.nickframe);
        TextView tv_nick = findViewById(R.id.tv_nickname);

        ListView searchlist = findViewById(R.id.listview);
        ListView realtimelist = findViewById(R.id.realtimelist);

        onMenuItemClickListener = new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                switch( menuItem.getItemId() ){//눌러진 MenuItem의 Item Id를 얻어와 식별
                    case R.id.name:
                        break;
                    case R.id.email:
                        break;
                    case R.id.logout:
                        UserApiClient.getInstance().logout(error -> {
                            if (error != null) {
                                Log.e(TAG, "로그아웃 실패, SDK에서 토큰 삭제됨", error);
                            }
                            else{
                               //  로그아웃 성공 시 수행하는 지점
                                Intent intent = new Intent(SubActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                startActivity(intent);
                                finish(); // 현재 액티비티 종료

                                Log.e(TAG, "로그아웃 성공, SDK에서 토큰 삭제됨");
                            }
                            return null;
                        });
                        break;
                }
                return false;
            }
        };

        nickframe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUp(view);
            }
        });
        searchlist.bringToFront();
        searchlist.setVisibility(View.GONE);

        LinearLayout fullscreen = findViewById(R.id.fullscreen); // 다른 곳 터치하면 listview 사라짐
        fullscreen.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideKeyboard();
                searchlist.setVisibility(View.GONE);
                return false;
            }
        });


        tv_nick.setText(strNick);



        String BASE_URL = "http://192.249.18.161:443";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);

        EditText search = (EditText) findViewById(R.id.what) ;
        Button searchb = findViewById(R.id.searchit);
        search.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Call<List<String>> call = retrofitInterface.executeMyRecord(strEmail);
                call.enqueue(new Callback<List<String>>() {
                    @Override
                    public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                        if (response.code() == 200) {
                            List<String> data = response.body();
                            adapter = new RecentSearchListAdapter(getApplicationContext(), retrofitInterface, strNick, strProfileImg, strEmail, data);
                            searchlist.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        } else if (response.code() == 400) {
                            Toast.makeText(SubActivity.this, "Failed to get recent search results", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<String>> call, @NonNull Throwable t) {
                        Toast.makeText(SubActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
                searchlist.setVisibility(View.VISIBLE);
                return false;
            }
        });


        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                switch (i) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        if (search.getText().toString().compareTo("") != 0) { // null값 return이 아닐 때만 동작
                            Intent intent = new Intent(SubActivity.this, ResultActivity.class);
                            intent.putExtra("search", search.getText().toString());
                            intent.putExtra("name", strNick);
                            intent.putExtra("email", strEmail);
                            intent.putExtra("profileImg", strProfileImg);
                            startActivity(intent);

                            HashMap<String, String> map = new HashMap<>();
                            map.put("email", strEmail);
                            map.put("text", search.getText().toString());
                            Call<Void> call = retrofitInterface.executeSearch(map);

                            call.enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if (response.code() == 200) {
                                        Toast.makeText(SubActivity.this, "Posted search record", Toast.LENGTH_LONG).show();
                                    } else if (response.code() == 400) {
                                        Toast.makeText(SubActivity.this, "Failed to post search record", Toast.LENGTH_LONG).show();
                                    }
                                }
                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    Toast.makeText(SubActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                        break;
                }
                return true;
            }
        });
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String subtext = search.getText().toString();
                Call<List<String>> call = retrofitInterface.executeMyRecordStartsWith(strEmail, subtext);
                call.enqueue(new Callback<List<String>>() {
                    @Override
                    public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                        if (response.code() == 200) {
                            List<String> data = response.body();
                            RecentSearchListAdapter adapter = new RecentSearchListAdapter(getApplicationContext(), retrofitInterface, strNick, strProfileImg, strEmail, data);
                            searchlist.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<String>> call, Throwable t) {
                        Toast.makeText(SubActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        searchb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (search.getText().toString().compareTo("")!=0) {
                    Intent intent = new Intent(SubActivity.this, ResultActivity.class);
                    intent.putExtra("search", search.getText().toString());
                    intent.putExtra("name",strNick);
                    intent.putExtra("email",strEmail);
                    intent.putExtra("profileImg",strProfileImg);
                    startActivity(intent);

                    HashMap<String, String> map = new HashMap<>();
                    map.put("email", strEmail);
                    map.put("text", search.getText().toString());

                    Call<Void> call = retrofitInterface.executeSearch(map);

                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.code() == 200) {
                                Toast.makeText(SubActivity.this, "Posted search record", Toast.LENGTH_LONG).show();
                            } else if (response.code() == 400) {
                                Toast.makeText(SubActivity.this, "Failed to post search record", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(SubActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                }

            }
        });

        Call<List<String>> call = retrofitInterface.executeRealTime(System.currentTimeMillis());
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.code() == 200) {
                    List<String> data = response.body();
                    RealtimeListAdapter adapter = new RealtimeListAdapter(getApplicationContext(), data);
                    realtimelist.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {

            }
        });

    }

    public final void popUp(View view) {
        PopupMenu popup = new PopupMenu(this, view); // 인자 (context, 팝업메뉴 연결 anchor 뷰)
        getMenuInflater().inflate(R.menu.menu_account, popup.getMenu()); // 메뉴아이템 건져서 메뉴 inflate
        popup.setOnMenuItemClickListener(onMenuItemClickListener); // onCreate에서 생성한 리스너를 팝업메뉴에 셋팅
        popup.getMenu().findItem(R.id.name).setTitle(strNick);
        popup.getMenu().findItem(R.id.email).setTitle(strEmail);

        popup.show(); //Popup Menu 보이기
    }


    void hideKeyboard()
    {
//        searchlist.setVisibility(View.GONE);
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (this.getCurrentFocus() != null) {
            inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        //searchlist.setVisibility(View.GONE);
    }

//
}