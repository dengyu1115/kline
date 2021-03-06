package com.nature.elt.item.activity;

import android.content.Intent;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.alibaba.fastjson.JSON;
import com.nature.elt.common.activity.BaseListActivity;
import com.nature.elt.common.enums.DefaultGroup;
import com.nature.elt.common.util.*;
import com.nature.elt.common.view.ExcelView;
import com.nature.elt.common.view.SearchBar;
import com.nature.elt.common.view.Selector;
import com.nature.elt.item.manager.GroupManager;
import com.nature.elt.item.model.Group;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 分组管理
 * @author nature
 * @version 1.0.0
 * @since 2020/11/21 15:58
 */
public class GroupListActivity extends BaseListActivity<Group> {

    private final GroupManager groupManager = InstanceHolder.get(GroupManager.class);
    private EditText keyword;
    private LinearLayout page;
    private EditText code, name, remark;
    private Button add;
    private Selector<String> type, typeSel;
    private final List<ExcelView.D<Group>> ds = Arrays.asList(
            new ExcelView.D<>("名称", d -> TextUtil.text(d.getName()), C, S, CommonUtil.nullsLast(Group::getName)),
            new ExcelView.D<>("code", d -> TextUtil.text(d.getCode()), C, C, CommonUtil.nullsLast(Group::getCode)),
            new ExcelView.D<>("类型", d -> TextUtil.text(DefaultGroup.getName(d.getType())), C, C),
            new ExcelView.D<>("项目明细", d -> TextUtil.text("跳转"), C, C, this.toItemGroup()),
            new ExcelView.D<>("编辑", d -> "+", C, C, this.edit()),
            new ExcelView.D<>("删除", d -> "-", C, C, this.delete())
    );
    private Map<String, String> typeMap;

    @Override
    protected List<ExcelView.D<Group>> define() {
        return ds;
    }

    @Override
    protected List<Group> listData() {
        List<Group> list = groupManager.list(this.typeSel.getValue());
        String keyword = this.keyword.getText().toString();
        if (StringUtils.isNotBlank(keyword)) {
            list = list.stream().filter(i -> i.getName().contains(keyword)).collect(Collectors.toList());
        }
        list = list.stream().filter(i -> !DefaultGroup.codes().contains(i.getCode())).collect(Collectors.toList());
        return list;
    }


    @Override
    protected void initHeaderViews(SearchBar searchBar) {
        searchBar.addConditionView(add = template.button("+", 30, 30));
        searchBar.addConditionView(typeSel = template.selector(100, 30));
        searchBar.addConditionView(keyword = template.editText(100, 30));
    }

    @Override
    protected void initHeaderBehaviours() {
        add.setOnClickListener(v -> this.add());
        List<String> types = new ArrayList<>();
        types.add(null);
        types.addAll(DefaultGroup.codes());
        typeMap = new HashMap<>();
        typeMap.put(null, "-请选择-");
        typeMap.put(DefaultGroup.INDEX.getCode(), DefaultGroup.INDEX.getName());
        typeMap.put(DefaultGroup.FUND.getCode(), DefaultGroup.FUND.getName());
        typeSel.mapper(this::getTypeName).init().refreshData(types);
    }

    private String getTypeName(String code) {
        return typeMap.get(code);
    }

    private void add() {
        this.makeWindowStructure();
        this.initWindowBehaviours();
        PopUtil.confirm(context, "新增分组", page, this::doAdd);
    }

    private Consumer<Group> edit() {
        return d -> {
            this.makeWindowStructure();
            this.initWindowBehaviours();
            this.type.setValue(d.getType());
            this.code.setText(d.getCode());
            this.name.setText(d.getName());
            this.remark.setText(d.getRemark());
            PopUtil.confirm(context, "编辑分组-" + d.getName(), page, this::doEdit);
        };
    }

    private Consumer<Group> toItemGroup() {
        return d -> {
            Intent intent = new Intent(context, ItemGroupActivity.class);
            intent.putExtra("group", JSON.toJSONString(d));
            this.startActivity(intent);
        };
    }

    private void doAdd() {
        this.doEdit((c, t) -> groupManager.findByCode(c, t) != null);
    }

    private void doEdit() {
        this.doEdit((c, t) -> false);
    }

    private void doEdit(BiFunction<String, String, Boolean> fun) {
        String type = this.type.getValue();
        if (StringUtils.isBlank(type)) {
            throw new RuntimeException("请选择类型");
        }
        String code = this.code.getText().toString();
        if (code.isEmpty()) {
            throw new RuntimeException("请填写编号");
        }
        if (fun.apply(code, type)) {
            throw new RuntimeException("分组已存在");
        }
        String name = this.name.getText().toString();
        if (name.isEmpty()) {
            throw new RuntimeException("请填写名称");
        }
        String remark = this.remark.getText().toString();
        Group group = new Group();
        group.setType(type);
        group.setCode(code);
        group.setName(name);
        group.setRemark(remark);
        groupManager.merge(group);
        this.refreshData();
        PopUtil.alert(context, "编辑成功！");
    }

    private Consumer<Group> delete() {
        return d -> PopUtil.confirm(context, "删除分组-" + d.getName(), "确认删除吗？", () -> {
            groupManager.delete(d.getCode(), d.getType());
            this.refreshData();
            PopUtil.alert(context, "删除成功！");
        });
    }

    private void makeWindowStructure() {
        ViewTemplate template = ViewTemplate.build(context);
        page = template.linearPage();
        page.setGravity(Gravity.CENTER);
        LinearLayout tl = template.line(300, 30);
        LinearLayout cl = template.line(300, 30);
        LinearLayout nl = template.line(300, 30);
        LinearLayout rl = template.line(300, 90);
        LinearLayout el = template.line(300, 30);
        tl.addView(template.textView("类型：", 100, 30));
        tl.addView(type = template.selector(200, 30));
        cl.addView(template.textView("分组编号：", 100, 30));
        cl.addView(code = template.editText(200, 30));
        nl.addView(template.textView("分组名称：", 100, 30));
        nl.addView(name = template.editText(200, 30));
        rl.addView(template.textView("备注：", 100, 30));
        rl.addView(remark = template.areaText(200, 90));
        page.addView(tl);
        page.addView(cl);
        page.addView(nl);
        page.addView(rl);
        page.addView(el);
    }

    private void initWindowBehaviours() {
        type.mapper(DefaultGroup::getName).init().refreshData(DefaultGroup.codes());
    }

}
