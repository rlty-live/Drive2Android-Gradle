package com.geronimostudios.drive2android.model;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class Header {

    private HashMap<String, Integer> mNamesIndex;
    private ArrayList<String> mNamesValues;

    public Header(String[] names) {
        mNamesIndex = new HashMap<>();
        mNamesValues = new ArrayList<>(names.length);
        for (int i = 0; i < names.length; i++) {
            mNamesIndex.put(names[i], i);
            mNamesValues.add(names[i]);
        }
    }

    public String getValue(int i) {
        return mNamesValues.get(i);
    }

    public int getIndex(String value) {
        return mNamesIndex.get(value);
    }

    public int size() {
        return mNamesValues.size();
    }

    @SuppressWarnings("deprecation")
    public String format() {
        String res = "key,";

        for (int i = 0; i < mNamesValues.size() - 1; i++) {
            res += StringEscapeUtils.escapeCsv(mNamesValues.get(i)) + ",";
        }
        res += StringEscapeUtils.escapeCsv(mNamesValues.get(mNamesValues.size() - 1))+ "\n";
        return res;
    }

    public Header add(String[] strings) {
        for (String s : strings) {
            if (mNamesIndex.get(s) == null) {
                mNamesIndex.put(s, mNamesValues.size());
                mNamesValues.add(s);
            }
        }
        return this;
    }
}
