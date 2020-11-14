package com.nature.kline.android.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;

public class SqlParam {

    private List<String> params;

    private StringBuilder sqlBuilder;

    private SqlParam() {
        params = new ArrayList<>();
        sqlBuilder = new StringBuilder();
    }

    public static SqlParam build() {
        return new SqlParam();
    }

    public String[] toArgs() {
        return params.toArray(new String[0]);
    }

    public String toSQL() {
        return this.delBlanks(sqlBuilder.toString().trim());
    }

    public SqlParam append(String sql, Object... objects) {
        sqlBuilder.append(" ").append(sql);
        if (objects == null) {
            params.add(null);
        } else {
            for (Object object : objects) {
                if (object == null) {
                    params.add(null);
                } else if (object instanceof String) {
                    params.add((String) object);
                } else if (object instanceof Double || object instanceof Integer) {
                    params.add(object.toString());
                } else {
                    throw new RuntimeException(String.format("%s 数据类型暂不支持", object.getClass()));
                }
            }
        }
        return this;
    }

    public <T> SqlParam foreach(Collection<T> col, String s, String e, String sep, BiConsumer<T, SqlParam> c) {
        if (col != null && !col.isEmpty()) {
            if (s != null && !s.isEmpty()) this.append(s);
            int index = 0;
            for (T t : col) {
                if (++index > 1) sqlBuilder.append(sep);
                c.accept(t, this);
            }
            if (e != null && !e.isEmpty()) this.append(e);
        }
        return this;
    }

    private String delBlanks(CharSequence s) {
        int c = 0;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch == ' ' || ch == '\t' || ch == '\n') c++;
            else c = 0;
            if (c <= 1) builder.append(ch);
        }
        return builder.toString();
    }

}
