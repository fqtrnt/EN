package com.example.fqtrnt.en;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by fqtrnt on 16-6-17.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "en.db";
    private static final int VERSION = 1;
    private final Context context;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists dict (id INTEGER PRIMARY KEY AUTOINCREMENT" +
                ", word varchar, ph_en varchar, ph_am varchar, means text, ordered integer," +
                "know_me integer)");
        initilData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    private void initilData(SQLiteDatabase db) {
        List<Dict> dicts = loadDicts();
        Function<Dict, String> keyFunction = new Function<Dict, String>() {
            @Override
            public String apply(Dict input) {
                return input.getWord();
            }
        };
        Map<String, Dict> wordMap = FluentIterable.from((dicts)).uniqueIndex(keyFunction);
        List<String> randoms = random(FluentIterable.from((dicts)).transform(keyFunction).toList());
        for (int i = 0; i < randoms.size(); i++) {
            String word = randoms.get(i);
            wordMap.get(word).setOrdered(i);
        }
        db.beginTransaction();
        try {
            for (Dict dict : dicts) {
                db.execSQL("insert into dict (id, word, ph_en, ph_am, means, ordered, know_me) values (null, ?, ?, ?, ?, ?, ?)", new Object[]{
                        dict.getWord(), dict.getPhEn(), dict.getPhAm(), dict.getMeans(), dict.getOrdered(), 0
                });
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Toast.makeText(context, "Data initialized fail.", Toast.LENGTH_SHORT);
        } finally {
            db.endTransaction();
        }
    }

    public List<Dict> loadAll() {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "select * from dict order by ordered";
        Cursor c = db.rawQuery(sql, null);
        List<Dict> dicts = Lists.newArrayList();
        while (c.moveToNext()) {
            Dict dict = new Dict(
                c.getString(c.getColumnIndex("word")),
                c.getString(c.getColumnIndex("ph_en")),
                c.getString(c.getColumnIndex("ph_am")),
                c.getString(c.getColumnIndex("means"))
            );
            dict.setOrdered(c.getInt(c.getColumnIndex("ordered")));
            dict.setKnowMe(c.getInt(c.getColumnIndex("know_me")));
            dicts.add(dict);
        }
        return dicts;
    }
    @NonNull
    protected List<Dict> loadDicts() {
        InputStream is = null;
        try {
            is = context.getAssets().open("data/pets3_new_2.txt");
            List<String> lists = IOUtils.readLines(is, Charsets.UTF_8);
            return FluentIterable.from(lists).transform(new Function<String, Dict>() {
                @Override
                public Dict apply(String input) {
                    Iterator<String> temp = Splitter.on("\t").split(input).iterator();
                    String word = temp.next();
                    String symbol = temp.next();
                    String ph = symbol.substring(symbol.indexOf('[') + 1, symbol.indexOf(']'));
                    List<String> phs = Lists.newArrayList(Splitter.on(",").trimResults().splitToList(ph));
                    while (phs.size() < 2) phs.add("");
                    String means = symbol.substring(symbol.indexOf(']') + 1);
                    return new Dict(word, phs.get(0), phs.get(1), means);
                }
            }).toList();
        } catch (Exception e) {
            e.printStackTrace();
            return Lists.newArrayList();
        } finally {
            IOUtils.closeQuietly(is);
        }
    }
    protected List<String> random(List<String> lines) {
        List<String> lineSet = Lists.newArrayList(Sets.newHashSet(lines));
        List<String> newLines = Lists.newArrayListWithCapacity(lineSet.size());
        Random random = new Random();
        for (int i = 0; i < lineSet.size(); i++) {
            int seed = random.nextInt(lineSet.size() - i);
            newLines.add(lineSet.get(seed));
            lineSet.set(seed, lineSet.get(lineSet.size() - i - 1));
        }
        return newLines;
    }
}
