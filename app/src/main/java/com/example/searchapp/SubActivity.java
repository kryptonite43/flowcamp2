package com.example.searchapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.inputmethod.InputMethodManager;

import android.widget.ArrayAdapter;

import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.transform.Result;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SubActivity extends AppCompatActivity { // 검색창 뜨는 액티비티
    private String strNick, strProfileImg, strEmail;
    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;

    private static PopupMenu.OnMenuItemClickListener onMenuItemClickListener;
    private Menu menu;

    private String BASE_URL = "http://192.249.18.161:443";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);
        Intent intent = getIntent();
        strNick = intent.getStringExtra("name");
        strProfileImg = intent.getStringExtra("profileImg");
        strEmail = intent.getStringExtra("email");

        FrameLayout nickframe = findViewById(R.id.nickframe);
//        final GradientDrawable drawable = (GradientDrawable) ContextCompat.getDrawable(this, R.drawable.circle);
//
//        Random rnd = new Random();
//        int color = Color.argb(255,rnd.nextInt(256),rnd.nextInt(256),rnd.nextInt(256));
//        drawable.setColor(color);
        TextView tv_nick = findViewById(R.id.tv_nickname);
        TextView tv_email = findViewById(R.id.tv_email);
        ImageView iv_profile = findViewById(R.id.iv_profile);
        ListView searchlist = findViewById(R.id.listview);
        ListView realtimelist = findViewById(R.id.realtimelist);
//        realtimelist.addItemDecoration(new DividerItemDecoration(getApplicationContext(),DividerItemDecoration.VERTICAL));
        //ListViewAdapter adapter = new ListViewAdapter();
//        searchlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                final String item = (String) adapter.getItem(i); // listview의 item type으로 변경
//
//            }
//        });

        //adapter.addItem(new ListViewItem("hello"));
        //adapter.addItem(new ListViewItem("hello"));
        //adapter.addItem(new ListViewItem("hello"));
        //searchlist.setAdapter(adapter);

        onMenuItemClickListener = new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                switch( menuItem.getItemId() ){//눌러진 MenuItem의 Item Id를 얻어와 식별
                    case R.id.name:
                        //menuItem.setTitle(strNick);
                        break;
                    case R.id.email:
                        break;
                    case R.id.logout:
                        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                            @Override
                            public void onCompleteLogout() {
                                // 로그아웃 성공 시 수행하는 지점
                                Intent intent = new Intent(SubActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                startActivity(intent);
                                finish(); // 현재 액티비티 종료
                            }
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
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
//        realtimelist.setLayoutManager(gridLayoutManager);
        LinearLayout fullscreen = findViewById(R.id.fullscreen); // 다른 곳 터치하면 listview 사라짐
        fullscreen.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideKeyboard();
                searchlist.setVisibility(View.GONE);
//                Display display = getWindowManager().getDefaultDisplay();
//                int width = display.getWidth();
//                ListView.LayoutParams parms = new ListView.LayoutParams(width,100);
//                realtimelist.setLayoutParams(parms);

                return false;
            }
        });

        iv_profile.setOutlineProvider(new ViewOutlineProvider() { // 프로필 이미지사진 둥글게
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0,0,view.getWidth(),view.getHeight(),40);
            }
        });
        iv_profile.setClipToOutline(true);

        // 닉네임, 이메일, 프로필이미지
        tv_nick.setText(strNick);
        tv_email.setText(strEmail);
        Glide.with(this).load(strProfileImg).into(iv_profile);

//        findViewById(R.id.btn_logout).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
////                    @Override
////                    public void onCompleteLogout() {
////                        // 로그아웃 성공 시 수행하는 지점
////                        finish(); // 현재 액티비티 종료
////                    }
////                });
//            }
//        });

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);

        EditText search = findViewById(R.id.what) ;
        Button searchb = findViewById(R.id.searchit);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchlist.setVisibility(View.VISIBLE);

                Call<List<String>> call = retrofitInterface.executeMyRecord(strEmail);
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
                    public void onFailure(Call<List<String>> call, @NonNull Throwable t) {
                        Toast.makeText(SubActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

            }
        });



//        search.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View view, int i, KeyEvent keyEvent) {
//                //searchlist.setVisibility(View.VISIBLE);
//                if (i==KeyEvent.KEYCODE_ENTER && keyEvent.getAction()==KeyEvent.ACTION_DOWN){
//                    if (search.getText().toString().compareTo("")!=0) { // null값 return이 아닐 때만 동작
//                        Intent intent = new Intent(SubActivity.this, ResultActivity.class);
//                        intent.putExtra("search", search.getText().toString());
//                        intent.putExtra("name",strNick);
//                        intent.putExtra("email",strEmail);
//                        intent.putExtra("profileImg",strProfileImg);
//                        startActivity(intent);
//
//                        HashMap<String, String> map = new HashMap<>();
//                        map.put("email", strEmail);
//                        map.put("text", search.getText().toString());
//
//                        Call<Void> call = retrofitInterface.executeSearch(map);
//
//                        call.enqueue(new Callback<Void>() {
//                            @Override
//                            public void onResponse(Call<Void> call, Response<Void> response) {
//                                Toast.makeText(SubActivity.this, "post success", Toast.LENGTH_LONG).show();
//                            }
//
//                            @Override
//                            public void onFailure(Call<Void> call, Throwable t) {
//                                Toast.makeText(SubActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
//                            }
//                        });
//                    }
//                    //Toast.makeText(SubActivity.this, search.getText().toString(), Toast.LENGTH_SHORT).show();
//
//
//                }
//                else {
//                    Toast.makeText(SubActivity.this, "press"+i, Toast.LENGTH_LONG).show();
//                }
//                return true;
//            }
//        });

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
                            Toast.makeText(SubActivity.this, "post success", Toast.LENGTH_LONG).show();
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
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            popup.setForceShowIcon(true);
//        }
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
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}