package com.example.fqtrnt.en;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class EnActivity extends AppCompatActivity {

    private int page = 0;
    private int pageSize = 9;
    private int showType = 0;
    private List<List<Dict>> partition = Lists.newArrayList();
    private ListItemAdapter listViewAdapter;
    private AdapterView.OnItemLongClickListener onItemLongClickListener;
    private TextView pageInfo;
    private DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_en);
        pageInfo = (TextView) findViewById(R.id.pageInfo);
        final ListView listView = (ListView) findViewById(R.id.listView);
        if (null == listView) return;
        db = new DBHelper(this);
        loadDicts();

        listViewAdapter = new ListItemAdapter(this);
        listView.setAdapter(listViewAdapter);
        onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                List<Dict> dicts = partition.isEmpty() ? Lists.<Dict>newArrayList() : partition.get(page);
                Dict dict = dicts.get(i);
                String temp = dict.getDisplay();
                if (temp.equals(dict.getWord())) {
                    dict.setDisplay(dict.getMeans());
                } else {
                    dict.setDisplay(dict.getWord());
                }
                TextView word = (TextView) view.findViewById(R.id.word);
                word.setText(dict.getDisplay());
                return false;
            }
        };
        listView.setOnItemLongClickListener(onItemLongClickListener);
        Button nextButton = (Button) findViewById(R.id.next);
        assert nextButton != null;
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (page + 1 >= partition.size()) return;
                page++;
                listViewAdapter.refresh(partition.get(page));
                pageInfo.setText(String.format("%s/%s", page + 1, partition.size()));
            }
        });
        Button prevButton = (Button) findViewById(R.id.prev);
        assert prevButton != null;
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (page - 1 < 0) return;
                page--;
                listViewAdapter.refresh(partition.get(page));
                pageInfo.setText(String.format("%s/%s", page + 1, partition.size()));
            }
        });
        final Button keepScreen = (Button) findViewById(R.id.keepScreenon);
        assert keepScreen != null;
        keepScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!listView.getKeepScreenOn()) {
                    listView.setKeepScreenOn(true);
                    keepScreen.setText(R.string.on);
                } else {
                    listView.setKeepScreenOn(false);
                    keepScreen.setText(R.string.off);
                }
            }
        });
    }

    private void loadDicts() {
        partition.clear();
        List<Dict> dictList = db.loadAll();
        FluentIterable.from(Iterables.partition(dictList, pageSize)).copyInto(partition);
        pageInfo.setText(String.format("%s/%s", page + 1, partition.size()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    protected class ListItemAdapter extends BaseAdapter {

        private List<Dict> items;
        private final Context context;

        public ListItemAdapter(Context context) {
            this.items = partition.isEmpty() ? Lists.<Dict>newArrayList() : partition.get(page);
            this.context = context;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View tempView = view;
            if (null == tempView) {
                tempView = LayoutInflater.from(this.context).inflate(R.layout.list_item_view, null);
            }
            TextView word = (TextView) tempView.findViewById(R.id.word);
            word.setText(((Dict) getItem(i)).getDisplay());
            return tempView;
        }

        public void refresh(List<Dict> dicts) {
            this.items = dicts;
            notifyDataSetChanged();
        }
    }
}
