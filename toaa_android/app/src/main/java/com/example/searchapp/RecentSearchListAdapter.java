package com.example.searchapp;

import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.transform.sax.SAXResult;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecentSearchListAdapter extends BaseAdapter {

    Context context;
    RetrofitInterface retrofitInterface;
    String strEmail, strNick, strProfileImg;
    List<String> data;
    LayoutInflater inflater;

    public RecentSearchListAdapter(Context context, RetrofitInterface retrofitInterface, String strNick, String strProfileImg, String strEmail, List<String> data) {
        this.context = context;
        this.retrofitInterface = retrofitInterface;
        this.strNick = strNick;
        this.strProfileImg = strProfileImg;
        this.strEmail = strEmail;
        this.data = data;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = inflater.inflate(R.layout.recent_search, null);
        TextView recentSearchText = (TextView) view.findViewById(R.id.recent_search_text);
        ImageView recentSearchClear = (ImageView) view.findViewById(R.id.recent_search_clear);
        recentSearchText.setText(data.get(i));
        recentSearchClear.setImageResource(R.drawable.ic_baseline_clear_24);
        recentSearchClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> map = new HashMap<>();
                map.put("email", strEmail);
                map.put("text", data.get(i));

                Call<Void> call = retrofitInterface.executeDeleteMyRecord(map);

                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.code() == 200) {
                            Toast.makeText(view.getContext(), "Record deleted", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(view.getContext(), "failed", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(view.getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

                data.remove(i);
                notifyDataSetChanged();
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ResultActivity.class);
                intent.putExtra("search", data.get(i));
                intent.putExtra("name",strNick);
                intent.putExtra("email",strEmail);
                intent.putExtra("profileImg",strProfileImg);
                context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

                HashMap<String, String> map = new HashMap<>();
                map.put("email", strEmail);
                map.put("text", data.get(i));

                Call<Void> call = retrofitInterface.executeSearch(map);

                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Toast.makeText(context, "post success", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        return view;
    }
}