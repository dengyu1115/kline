package com.nature.kline.android.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.nature.kline.android.util.ViewTemplate;
import com.nature.kline.android.util.ViewUtil;
import com.nature.kline.android.view.ExcelView;
import com.nature.kline.android.view.SearchBar;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public abstract class ListPageActivity<T> extends AppCompatActivity {

    protected static final int C = 0, S = 1, E = 2;

    private LinearLayout page;

    private ExcelView<T> excel;

    private Button button;

    private TextView total;

    private int height;

    protected Context context;

    protected ViewTemplate template;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = ListPageActivity.this;
        this.makeStructure();
        this.initBehaviours();
        this.setContentView(page);
        ViewUtil.initActivity(this);
        this.refreshData();
    }

    private void makeStructure() {
        template = ViewTemplate.build(context);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        page = template.linearPage();
        page.setOrientation(LinearLayout.VERTICAL);
        height = metrics.heightPixels;
        this.header();
        this.body();
        this.footer();
    }

    private void initBehaviours() {
        this.button.setOnClickListener(v -> this.refreshData());
        this.excel.define(this.define());
        this.initHeaderBehaviours();
    }

    private void header() {
        LinearLayout header = new LinearLayout(context);
        page.addView(header);
        header.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, (int) (height * 0.05)));
        SearchBar searchBar = new SearchBar(context);
        header.addView(searchBar);
        button = template.button("查询", 50, 30);
        searchBar.addHandleView(button);
        this.initHeaderViews(searchBar);
    }

    private void body() {
        LinearLayout body = new LinearLayout(context);
        page.addView(body);
        body.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, (int) (height * 0.9)));
        this.excel = new ExcelView<>(context, 5);
        body.addView(this.excel);
    }

    private void footer() {
        LinearLayout footer = new LinearLayout(context);
        page.addView(footer);
        footer.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, (int) (height * 0.05)));
        footer.setGravity(Gravity.CENTER);
        total = new TextView(context);
        footer.addView(total);
        total.setGravity(Gravity.CENTER);
    }

    protected void refreshData() {
        new Thread(() -> {
            this.excel.data(this.listData());
            this.refreshTotal();
        }).start();
    }

    private final Handler handler = new Handler(msg -> {
        this.total.setText(String.valueOf(this.excel.getListSize()));
        return false;
    });

    private void refreshTotal() {
        handler.sendMessage(new Message());
    }

    protected abstract List<ExcelView.D<T>> define();

    protected abstract List<T> listData();

    protected abstract void initHeaderViews(SearchBar searchBar);

    protected abstract void initHeaderBehaviours();

}
