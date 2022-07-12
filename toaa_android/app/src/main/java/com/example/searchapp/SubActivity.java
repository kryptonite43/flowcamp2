package com.example.searchapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.RoundedCorner;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
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
        ListView searchlist = findViewById(R.id.searchlist);
        ListView realtimelist = findViewById(R.id.realtimelist);
        RelativeLayout cardviewlayout = findViewById(R.id.cardviewlayout);
        CardView infocard = findViewById(R.id.infocard);
        Button logoutbutton = findViewById(R.id.logout_button);
        TextView profilename = findViewById(R.id.profilename);
        TextView profileemail = findViewById(R.id.profileemail);
        ImageView profileimage = findViewById(R.id.profileimage);

        logoutbutton.setOnClickListener(new View.OnClickListener() { // 로그아웃 버튼
            @Override
            public void onClick(View view) {
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
            }
        });

        nickframe.setOnClickListener(new View.OnClickListener() { // 프로필 누를 시 infocard 보여주기
            @Override
            public void onClick(View view) {
                if (infocard.getVisibility()==View.VISIBLE){
                    infocard.setVisibility(View.INVISIBLE);
                }
                else if (infocard.getVisibility() == View.INVISIBLE) {
                    profilename.setText(strNick);
                    profileemail.setText(strEmail);
                    Glide.with(SubActivity.this).load(strProfileImg).apply(new RequestOptions().transforms(new CenterCrop(),new RoundedCorners(30))).into(profileimage);
                    infocard.setVisibility(View.VISIBLE);
                }
            }
        });
        searchlist.bringToFront();
        searchlist.setVisibility(View.INVISIBLE);

        LinearLayout fullscreen = findViewById(R.id.fullscreen);
        fullscreen.bringToFront();
        fullscreen.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideKeyboard();
                searchlist.setVisibility(View.INVISIBLE);
                if (infocard.getVisibility()==View.VISIBLE) {
                    infocard.setVisibility(View.INVISIBLE);
                }
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

        EditText search = (EditText) findViewById(R.id.search_keyword) ;
        Button searchb = findViewById(R.id.search_button);

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

        search.setOnEditorActionListener(new TextView.OnEditorActionListener() { // 키보드에서 엔터 시 검색됨
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

        searchb.setOnClickListener(new View.OnClickListener() { // 검색 버튼 클릭
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

        ImageView refresh_button = (ImageView) findViewById(R.id.refresh);
        refresh_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        });
        cardviewlayout.bringToFront();
    }

    void hideKeyboard()
    {
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (this.getCurrentFocus() != null) {
            inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}