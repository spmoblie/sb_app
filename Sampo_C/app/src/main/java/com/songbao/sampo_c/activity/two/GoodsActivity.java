package com.songbao.sampo_c.activity.two;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.songbao.sampo_c.AppApplication;
import com.songbao.sampo_c.AppConfig;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.activity.BaseActivity;
import com.songbao.sampo_c.activity.mine.CommentGoodsActivity;
import com.songbao.sampo_c.adapter.AdapterCallback;
import com.songbao.sampo_c.adapter.CommentGLVAdapter;
import com.songbao.sampo_c.adapter.ImageListAdapter;
import com.songbao.sampo_c.entity.BaseEntity;
import com.songbao.sampo_c.entity.CommentEntity;
import com.songbao.sampo_c.entity.GoodsAttrEntity;
import com.songbao.sampo_c.entity.GoodsEntity;
import com.songbao.sampo_c.utils.ClickUtils;
import com.songbao.sampo_c.utils.CommonTools;
import com.songbao.sampo_c.utils.ExceptionUtil;
import com.songbao.sampo_c.utils.JsonUtils;
import com.songbao.sampo_c.utils.LogUtil;
import com.songbao.sampo_c.utils.StringUtil;
import com.songbao.sampo_c.utils.retrofit.HttpRequests;
import com.songbao.sampo_c.widgets.ObservableScrollView;
import com.songbao.sampo_c.widgets.ScrollViewListView;
import com.songbao.sampo_c.widgets.ViewPagerScroller;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;


public class GoodsActivity extends BaseActivity implements OnClickListener {

    String TAG = GoodsActivity.class.getSimpleName();

    @BindView(R.id.top_common_ll_main)
    ConstraintLayout ll_top_main;

    @BindView(R.id.top_common_left)
    ImageButton ib_left;

    @BindView(R.id.top_common_right)
    ImageButton ib_right;

    @BindView(R.id.top_common_rb_1)
    RadioButton rb_1;

    @BindView(R.id.top_common_rb_2)
    RadioButton rb_2;

    @BindView(R.id.top_common_rb_3)
    RadioButton rb_3;

    @BindView(R.id.goods_iv_left)
    ImageView iv_left;

    @BindView(R.id.goods_iv_share)
    ImageView iv_share;

    @BindView(R.id.goods_view_sv)
    ObservableScrollView goods_sv;

    @BindView(R.id.goods_view_vp)
    ViewPager goods_vp;

    @BindView(R.id.goods_vp_indicator)
    LinearLayout vp_indicator;

    @BindView(R.id.goods_tv_goods_name)
    TextView tv_name;

    @BindView(R.id.goods_tv_price)
    TextView tv_price;

    @BindView(R.id.goods_spec_choice_main)
    ConstraintLayout spec_main;

    @BindView(R.id.goods_tv_selected_show)
    TextView tv_spec;

    @BindView(R.id.goods_good_comment_main)
    ConstraintLayout comment_main;

    @BindView(R.id.goods_tv_good_comment_num)
    TextView tv_comment_num;

    @BindView(R.id.goods_tv_good_comment_percentage)
    TextView tv_percentage;

    @BindView(R.id.goods_lv_comment)
    ScrollViewListView lv_comment;

    @BindView(R.id.goods_lv_detail)
    ScrollViewListView lv_detail;

    @BindView(R.id.goods_tv_good_detail)
    TextView title_detail;

    @BindView(R.id.bottom_add_cart_tv_home)
    TextView tv_home;

    @BindView(R.id.bottom_add_cart_tv_cart)
    TextView tv_cart;

    @BindView(R.id.bottom_add_cart_tv_cart_num)
    TextView tv_cart_num;

    @BindView(R.id.bottom_add_cart_tv_cart_add)
    TextView tv_cart_add;

    @BindView(R.id.bottom_add_cart_tv_customize)
    TextView tv_customize;

    private Runnable mPagerAction;
    private LinearLayout.LayoutParams indicatorsLP;
    private CommentGLVAdapter lv_comment_Adapter;
    private ImageListAdapter lv_detail_Adapter;

    public static final int TYPE_1 = 1;  //商品
    public static final int TYPE_2 = 2;  //评价
    public static final int TYPE_3 = 3;  //详情
    private int top_type = TYPE_1; //Top标记

    private GoodsEntity goodsEn;
    private String skuCode = "";
    private boolean isLoop = false;
    private boolean vprStop = false;
    private boolean isOpenAttr = false;
    private int commentNum, goodStar;
    private int idsSize, idsPosition, vprPosition;
    private ImageView[] indicators = null;
    private ArrayList<ImageView> viewLists = new ArrayList<>();
    private ArrayList<String> al_image = new ArrayList<>();
    private ArrayList<String> al_detail = new ArrayList<>();
    private ArrayList<CommentEntity> al_comment = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods);

        skuCode = getIntent().getStringExtra("skuCode");
        isOpenAttr = getIntent().getBooleanExtra("isOpenAttr", false);

        initView();
    }

    private void initView() {
        setHeadVisibility(View.GONE);

        ib_left.setOnClickListener(this);
        iv_left.setOnClickListener(this);
        ib_right.setOnClickListener(this);
        iv_share.setOnClickListener(this);
        spec_main.setOnClickListener(this);
        comment_main.setOnClickListener(this);
        tv_home.setOnClickListener(this);
        tv_cart.setOnClickListener(this);
        tv_cart_add.setOnClickListener(this);
        tv_customize.setOnClickListener(this);

        // 动态调整宽高
        int ind_margin = CommonTools.dpToPx(mContext, 5);
        indicatorsLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        indicatorsLP.setMargins(ind_margin, 0, 0, 0);

        initRadioGroup();
        initScrollView();
        loadGoodsData();
        loadCommentData();
    }

    private void initShowView() {
        if (goodsEn != null) {
            //打开属性面板
            if (isOpenAttr) {
                openAttrView();
                isOpenAttr = false;
            }

            tv_name.setText(goodsEn.getName());
            tv_price.setText(df.format(goodsEn.getPrice()));

            //已选属性
            updateSelectAttrStr(goodsEn.getAttrEn());

            //商品图片
            if (goodsEn.getImageList() != null) {
                al_image.clear();
                al_image.addAll(goodsEn.getImageList());
            }
            initViewPager();

            //精彩评论
            if (al_comment.size() > 0) {
                commentNum = al_comment.get(0).getNumber();
                goodStar = al_comment.get(0).getGoodStar();
            } else {
                commentNum = 0;
                goodStar = 0;
            }
            tv_comment_num.setText("（" + commentNum + "）");
            tv_percentage.setText("好评率\n" + goodStar + "%");

            //详情图片
            if (goodsEn.getDetailList() != null) {
                al_detail.clear();
                al_detail.addAll(goodsEn.getDetailList());
            }
            initListView();
        }
    }

    private void initRadioGroup() {
        rb_1.setText("商品");
        rb_2.setText("评价");
        rb_3.setText("详情");
        rb_1.setOnClickListener(this);
        rb_2.setOnClickListener(this);
        rb_3.setOnClickListener(this);
        setDefaultRadioButton();
    }

    private void initScrollView() {
        goods_sv.setScrollViewListener(new ObservableScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(ObservableScrollView scrollView, int new_x, int new_y, int old_x, int old_y) {
                double alpha;
                if (new_y <= 0) {
                    //在顶部时完全透明
                    alpha = 0;
                } else if (new_y > 0 && new_y <= 600) {
                    //在滑动高度中时，设置透明度百分比（当前高度/总高度）
                    double d = (double) new_y / 600;
                    alpha = d * 255;
                } else {
                    //滑出总高度 完全不透明
                    alpha = 255;
                }
                changeTopViewBackground(alpha);
            }
        });
    }

    private void changeTopViewBackground(double alpha) {
        if (alpha <= 0) {
            ll_top_main.setVisibility(View.GONE);
        } else {
            ll_top_main.setVisibility(View.VISIBLE);
        }
        ll_top_main.setBackgroundColor(Color.argb((int) alpha, 255, 255, 255));
        ib_left.setBackgroundColor(Color.argb(0, 255, 255, 255));
        ib_right.setBackgroundColor(Color.argb(0, 255, 255, 255));
        rb_1.setBackgroundColor(Color.argb(0, 255, 255, 255));
        rb_1.setBackgroundColor(Color.argb(0, 255, 255, 255));
        rb_1.setBackgroundColor(Color.argb(0, 255, 255, 255));
    }

    private void initViewPager() {
        if (al_image.size() > 0) {
            isLoop = false;
            clearViewPagerData();
            ArrayList<String> al_show = new ArrayList<>();
            al_show.addAll(al_image);
            idsSize = al_show.size();
            indicators = new ImageView[idsSize]; // 定义指示器数组大小
            if (idsSize == 2 || idsSize == 3) {
                al_show.addAll(al_image);
            }
            for (int i = 0; i < al_show.size(); i++) {
                final int position = i;
                final String imgUrl = al_show.get(i);

                ImageView iv_show = new ImageView(mContext);
                iv_show.setScaleType(ImageView.ScaleType.CENTER_CROP);

                Glide.with(AppApplication.getAppContext())
                        .load(imgUrl)
                        .apply(AppApplication.getShowOptions())
                        .into(iv_show);

                iv_show.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (ClickUtils.isDoubleClick()) return;
                        openViewPagerActivity(al_image, position);
                    }
                });
                viewLists.add(iv_show);

                if (i < idsSize) {
                    // 循环加入指示器
                    indicators[i] = new ImageView(mContext);
                    indicators[i].setLayoutParams(indicatorsLP);
                    indicators[i].setImageResource(R.mipmap.indicators_default);
                    if (i == 0) {
                        indicators[i].setImageResource(R.mipmap.indicators_now);
                    }
                    vp_indicator.addView(indicators[i]);
                }
            }
            if (viewLists.size() > 3) {
                isLoop = true;
            }
            goods_vp.setAdapter(new PagerAdapter() {

                // 创建
                @NonNull
                @Override
                public Object instantiateItem(@NonNull ViewGroup container, int position) {
                    View layout;
                    if (isLoop) {
                        layout = viewLists.get(position % viewLists.size());
                    } else {
                        layout = viewLists.get(position);
                    }
                    container.addView(layout);
                    return layout;
                }

                // 销毁
                @Override
                public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                    View layout;
                    if (isLoop) {
                        layout = viewLists.get(position % viewLists.size());
                    } else {
                        layout = viewLists.get(position);
                    }
                    container.removeView(layout);
                }

                @Override
                public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                    return view == object;
                }

                @Override
                public int getCount() {
                    if (isLoop) {
                        return Integer.MAX_VALUE;
                    } else {
                        return viewLists.size();
                    }
                }
            });
            goods_vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageSelected(final int arg0) {
                    if (goods_vp == null || viewLists.size() <= 1) return;
                    if (isLoop) {
                        vprPosition = arg0;
                        idsPosition = arg0 % viewLists.size();
                        if (idsPosition == viewLists.size()) {
                            idsPosition = 0;
                            goods_vp.setCurrentItem(0);
                        }
                    } else {
                        idsPosition = arg0;
                    }
                    // 更改指示器图片
                    if ((idsSize == 2 || idsSize == 3) && idsPosition >= idsSize) {
                        idsPosition = idsPosition - idsSize;
                    }
                    for (int i = 0; i < idsSize; i++) {
                        ImageView imageView = indicators[i];
                        if (i == idsPosition)
                            imageView.setImageResource(R.mipmap.indicators_now);
                        else
                            imageView.setImageResource(R.mipmap.indicators_default);
                    }
                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {

                }

                @Override
                public void onPageScrollStateChanged(int arg0) {
                    if (arg0 == 1) {
                        vprStop = true;
                    }
                }
            });
            if (isLoop) {
                goods_vp.setCurrentItem(viewLists.size() * 10);
                if (mPagerAction == null) {
                    mPagerAction = new Runnable() {

                        @Override
                        public void run() {
                            if (goods_vp == null || viewLists.size() <= 1) return;
                            if (!vprStop) {
                                vprPosition++;
                                if (goods_vp != null) {
                                    goods_vp.setCurrentItem(vprPosition);
                                }
                            }
                            vprStop = false;
                            goods_vp.postDelayed(mPagerAction, 5000);
                        }
                    };
                }
                goods_vp.post(mPagerAction);

                ViewPagerScroller scroller = new ViewPagerScroller(mContext);
                scroller.initViewPagerScroll(goods_vp);
            }
        }
    }

    private void clearViewPagerData() {
        if (viewLists.size() > 0) {
            viewLists.clear();
        }
        if (goods_vp != null) {
            goods_vp.removeAllViews();
            goods_vp.removeCallbacks(mPagerAction);
        }
        if (vp_indicator != null) {
            vp_indicator.removeAllViews();
        }
    }

    private void initListView() {
        //精彩评价
        if (lv_comment_Adapter == null) {
            lv_comment_Adapter = new CommentGLVAdapter(mContext);
            lv_comment_Adapter.addCallback(new AdapterCallback() {
                @Override
                public void setOnClick(Object data, int position, int type) {
                    openCommentActivity(skuCode);
                }
            });
        }
        lv_comment_Adapter.updateData(al_comment);
        lv_comment.setAdapter(lv_comment_Adapter);
        lv_comment.setOverScrollMode(ListView.OVER_SCROLL_NEVER);

        //商品详情
        if (lv_detail_Adapter == null) {
            lv_detail_Adapter = new ImageListAdapter(mContext);
            lv_detail_Adapter.addCallback(new AdapterCallback() {
                @Override
                public void setOnClick(Object data, int position, int type) {

                }
            });
        }
        lv_detail_Adapter.updateData(al_detail);
        lv_detail.setAdapter(lv_detail_Adapter);
        lv_detail.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
    }

    @Override
    protected void updateSelectAttrStr(GoodsAttrEntity attrEn) {
        if (attrEn != null) {
            String attrsNameStr = attrEn.getAttrNameStr();
            if (StringUtil.isNull(attrsNameStr)) {
                attrsNameStr = getString(R.string.goods_attr_num_1, attrEn.getBuyNum());
            } else {
                attrsNameStr = getString(R.string.goods_attr_num_2, attrsNameStr, attrEn.getBuyNum());
            }
            tv_spec.setText(attrsNameStr);
            if (attrEn.isAdd()) {
                postCartData(attrEn);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.top_common_left:
            case R.id.goods_iv_left:
                finish();
                break;
            case R.id.top_common_right:
            case R.id.goods_iv_share:
                break;
            case R.id.top_common_rb_1:
                if (top_type == TYPE_1) return;
                top_type = TYPE_1;
                changeItemStatus();
                scrollTo(0);
                break;
            case R.id.top_common_rb_2:
                if (top_type == TYPE_2) return;
                top_type = TYPE_2;
                changeItemStatus();
                scrollTo(comment_main.getTop() - 100);
                break;
            case R.id.top_common_rb_3:
                if (top_type == TYPE_3) return;
                top_type = TYPE_3;
                changeItemStatus();
                scrollTo(title_detail.getTop() - 150);
                break;
            case R.id.goods_good_comment_main:
                openCommentActivity(skuCode);
                break;
            case R.id.bottom_add_cart_tv_home:
                returnHomeActivity();
                break;
            case R.id.bottom_add_cart_tv_cart:
                openActivity(CartActivity.class);
                break;
            case R.id.goods_spec_choice_main:
            case R.id.bottom_add_cart_tv_cart_add:
                openAttrView();
                break;
            case R.id.bottom_add_cart_tv_customize:
                openDesignerActivity(skuCode);
                break;
        }
    }

    /**
     * 设置默认项
     */
    private void setDefaultRadioButton() {
        RadioButton defaultBtn;
        switch (top_type) {
            default:
            case TYPE_1:
                defaultBtn = rb_1;
                break;
            case TYPE_2:
                defaultBtn = rb_2;
                break;
            case TYPE_3:
                defaultBtn = rb_3;
                break;
        }
        changeItemStatus();
        defaultBtn.setChecked(true);
    }

    /**
     * 自定义Top Item状态切换
     */
    private void changeItemStatus() {
        rb_1.setTextSize(15);
        rb_2.setTextSize(15);
        rb_3.setTextSize(15);
        rb_1.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
        rb_2.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
        rb_3.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
        switch (top_type) {
            default:
            case TYPE_1:
                rb_1.setTextSize(18);
                rb_1.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
                break;
            case TYPE_2:
                rb_2.setTextSize(18);
                rb_2.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
                break;
            case TYPE_3:
                rb_3.setTextSize(18);
                rb_3.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
                break;
        }
    }

    /**
     * 打开属性面板
     */
    private void openAttrView() {
        if (goodsEn != null) {
            loadGoodsAttrData(goodsEn.getGoodsCode(), goodsEn.getAttrEn());
        }
    }

    /**
     * 打开评价列表页
     */
    private void openCommentActivity(String goodsCode) {
        Intent intent = new Intent(mContext, CommentGoodsActivity.class);
        intent.putExtra("goodsCode", goodsCode);
        startActivity(intent);
    }

    /**
     * 滚动到指定位置
     */
    private void scrollTo(int y) {
        goods_sv.smoothScrollTo(0, y);
    }

    @Override
    protected void onResume() {
        LogUtil.i(LogUtil.LOG_TAG, TAG + ": onResume");
        // 页面开始
        AppApplication.onPageStart(this, TAG);

        super.onResume();
    }

    @Override
    protected void onPause() {
        LogUtil.i(LogUtil.LOG_TAG, TAG + ": onPause");
        // 页面结束
        AppApplication.onPageEnd(this, TAG);

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // 清除轮播数据
        clearViewPagerData();

        super.onDestroy();
    }

    /**
     * 加载商品详情数据
     */
    private void loadGoodsData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("sourceType", AppConfig.LOAD_TYPE);
        map.put("skuCode", skuCode);
        loadSVData(AppConfig.URL_GOODS_DETAIL, map, HttpRequests.HTTP_GET, AppConfig.REQUEST_SV_GOODS_DETAIL);
    }

    /**
     * 加载精彩评价数据
     */
    private void loadCommentData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("page", "1");
        map.put("size", AppConfig.LOAD_SIZE);
        map.put("skuCode", skuCode);
        loadSVData(AppConfig.URL_GOODS_COMMENT, map, HttpRequests.HTTP_GET, AppConfig.REQUEST_SV_GOODS_COMMENT);
    }

    /**
     * 添加购物车
     */
    private void postCartData(GoodsAttrEntity attrEn) {
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("goodsCode", goodsEn.getGoodsCode());
            jsonObj.put("skuCode", attrEn.getSkuCode());
            jsonObj.put("buyNum", attrEn.getBuyNum());
            postJsonData(AppConfig.BASE_URL_3, AppConfig.URL_CART_ADD, jsonObj, AppConfig.REQUEST_SV_CART_ADD);
        } catch (JSONException e) {
            ExceptionUtil.handle(e);
        }
    }

    @Override
    protected void callbackData(JSONObject jsonObject, int dataType) {
        super.callbackData(jsonObject, dataType);
        try {
            switch (dataType) {
                case AppConfig.REQUEST_SV_GOODS_DETAIL:
                    BaseEntity<GoodsEntity> GoodsEn = JsonUtils.getGoodsDetailData(jsonObject);
                    if (GoodsEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
                        goodsEn = GoodsEn.getData();
                        initShowView();
                    } else {
                        handleErrorCode(GoodsEn);
                    }
                    break;
                case AppConfig.REQUEST_SV_GOODS_COMMENT:
                    BaseEntity<CommentEntity> commentEn = JsonUtils.getCommentGoodsListData(jsonObject);
                    if (commentEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
                        ArrayList<CommentEntity> newList = new ArrayList<>();
                        newList.addAll(commentEn.getLists());
                        al_comment.clear();
                        if (newList.size() > 2) {
                            al_comment.add(newList.get(0));
                            al_comment.add(newList.get(1));
                        } else {
                            al_comment.addAll(newList);
                        }
                        initShowView();
                    } else {
                        handleErrorCode(commentEn);
                    }
                    break;
                case AppConfig.REQUEST_SV_CART_ADD:
                    BaseEntity baseEn = JsonUtils.getBaseErrorData(jsonObject);
                    if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
                        CommonTools.showToast("加入购物车成功");
                    } else {
                        handleErrorCode(baseEn);
                    }
                    break;
            }
        } catch (Exception e) {
            loadFailHandle();
            ExceptionUtil.handle(e);
        }
    }

    @Override
    protected void loadFailHandle() {
        super.loadFailHandle();
        handleErrorCode(null);
    }

}
