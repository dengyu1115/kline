package com.nature.kline.common.mapper;


import com.nature.kline.android.db.BaseDB;
import com.nature.kline.android.db.SqlParam;
import com.nature.kline.common.model.WorkDay;

import java.util.List;

/**
 * 工作日
 * @author nature
 * @version 1.0.0
 * @since 2019/12/15 19:56
 */
public class WorkDayMapper {

    private static final String DATE = "date";

    private static final String SQL_TABLE = "" +
            "CREATE TABLE IF NOT EXISTS work_day (" +
            "date TEXT NOT NULL," +
            "type TEXT NOT NULL," +
            "PRIMARY KEY (date)" +
            ")";

    private final BaseDB baseDB = BaseDB.create();

    public WorkDayMapper() {
        baseDB.executeSql(SQL_TABLE);
    }

    /**
     * 查询最新的工作日
     * @param date date
     * @return string
     */
    public String getLatestWorkDay(String date) {
        String sql = "select date" +
                "       from work_day" +
                "      where date <= ?" +
                "        and type = '0'" +
                "      order by date desc" +
                "      limit 1";
        return baseDB.find(sql, new String[]{date}, c -> BaseDB.getString(c, DATE));
    }

    /**
     * 获取下一个工作日
     * @param date date
     * @return string
     */
    public String getNextWorkDay(String date) {
        String sql = "select date" +
                "       from work_day" +
                "      where date > ?" +
                "        and type = '0'" +
                "      order by date" +
                "      limit 1";
        return baseDB.find(sql, new String[]{date}, c -> BaseDB.getString(c, DATE));
    }

    /**
     * 获取前一工作日
     * @param date date
     * @return string
     */
    public String getPreWorkDay(String date) {
        String sql = "select date" +
                "       from work_day" +
                "      where date < ?" +
                "        and type = '0'" +
                "      order by date desc" +
                "      limit 1";
        return baseDB.find(sql, new String[]{date}, c -> BaseDB.getString(c, DATE));
    }

    /**
     * 保存或者更新工作日数据
     * @param workDays 数据
     * @return int
     */
    public int batchMerge(List<WorkDay> workDays) {
        SqlParam param = SqlParam.build().append("REPLACE INTO work_day (date, type) VALUES")
                .foreach(workDays, null, null, ",",
                        (w, p) -> p.append("(?, ?)", w.getDate(), w.getType()));
        return baseDB.executeUpdate(param);
    }

    /**
     * 查询指定日期之前的工作日
     * @param date date
     * @return list
     */
    public List<String> listWorkDays(String date) {
        SqlParam param = SqlParam.build().append("select date from work_day")
                .append("where type = '0' and date <= ? order by date desc", date);
        return baseDB.list(param, c -> BaseDB.getString(c, DATE));
    }

    /**
     * 查询指定年份工作日数量
     * @param year year
     * @return int
     */
    public int count(String year) {
        SqlParam param = SqlParam.build().append("select count(1) from work_day")
                .append("where date like ?", year.concat("%"));
        return baseDB.find(param, c -> c.getInt(0));
    }

    /**
     * 查询指定日期之前的全部日期
     * @param date date
     * @return list
     */
    public List<String> list(String date) {
        SqlParam param = SqlParam.build().append("select date from work_day")
                .append("where date <= ? order by date desc", date);
        return baseDB.list(param, c -> BaseDB.getString(c, DATE));
    }
}
