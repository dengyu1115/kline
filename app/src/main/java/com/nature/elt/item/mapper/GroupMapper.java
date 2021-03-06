package com.nature.elt.item.mapper;

import android.database.Cursor;
import com.nature.elt.common.db.BaseDB;
import com.nature.elt.common.db.SqlParam;
import com.nature.elt.common.enums.DefaultGroup;
import com.nature.elt.item.model.Group;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.Function;

/**
 * 分组
 * @author nature
 * @version 1.0.0
 * @since 2020/4/4 19:15
 */
public class GroupMapper {

    private static final String TABLE = "" +
            "CREATE TABLE IF NOT EXISTS `group` ( " +
            " code TEXT NOT NULL, " +
            " name TEXT NOT NULL, " +
            " type TEXT NOT NULL, " +
            " remark TEXT NOT NULL, " +
            " PRIMARY KEY (code,type))";
    private static final Function<Cursor, Group> mapper = c -> {
        Group t = new Group();
        t.setCode(BaseDB.getString(c, "code"));
        t.setName(BaseDB.getString(c, "name"));
        t.setType(BaseDB.getString(c, "type"));
        t.setRemark(BaseDB.getString(c, "remark"));
        return t;
    };
    private final BaseDB baseDB = BaseDB.create();

    public GroupMapper() {
        baseDB.executeSql(TABLE);
        this.merge(this.defaultGroup(DefaultGroup.INDEX));
        this.merge(this.defaultGroup(DefaultGroup.FUND));
    }

    public int merge(Group group) {
        SqlParam param = SqlParam.build().append("REPLACE INTO `group` (code, type, name, remark) VALUES ")
                .append("(?, ?, ?, ?)", group.getCode(), group.getType(), group.getName(), group.getRemark());
        return baseDB.executeUpdate(param);
    }

    public List<Group> list(String type) {
        SqlParam param = SqlParam.build().append("select code, type, name, remark from `group`");
        if (StringUtils.isNoneBlank(type)) param.append("where type = ?", type);
        return baseDB.list(param, mapper);
    }

    public int delete(String code, String type) {
        SqlParam param = SqlParam.build().append("delete from `group`  where code = ? and type = ?", code, type);
        return baseDB.executeUpdate(param);
    }

    public Group findByCode(String code, String type) {
        SqlParam param = SqlParam.build().append("select code, type, name, remark from `group` where code = ? and type = ?", code, type);
        return baseDB.find(param, mapper);
    }

    private Group defaultGroup(DefaultGroup dg) {
        Group group = new Group();
        group.setCode(dg.getCode());
        group.setName(dg.getName());
        group.setType(dg.getCode());
        group.setRemark(StringUtils.EMPTY);
        return group;
    }
}
