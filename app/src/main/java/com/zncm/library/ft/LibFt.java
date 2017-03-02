package com.zncm.library.ft;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.malinskiy.materialicons.Iconify;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.umeng.analytics.MobclickAgent;
import com.zncm.library.R;
import com.zncm.library.adapter.LibAdapter;
import com.zncm.library.data.Constant;
import com.zncm.library.data.EnumData;
import com.zncm.library.data.Fields;
import com.zncm.library.data.Lib;
import com.zncm.library.data.RefreshEvent;
import com.zncm.library.ui.ItemsAc;
import com.zncm.library.ui.LibAc;
import com.zncm.library.ui.LibAddAc;
import com.zncm.library.ui.LocLibAc;
import com.zncm.library.ui.SettingAc;
import com.zncm.library.ui.ShareAc;
import com.zncm.library.utils.Dbutils;
import com.zncm.library.utils.DoubleClickImp;
import com.zncm.library.utils.MySp;
import com.zncm.library.utils.PhoneUtils;
import com.zncm.library.utils.XUtil;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import de.greenrobot.event.EventBus;
import tr.xip.errorview.RetryListener;


public class LibFt extends BaseListFt {
    private LibAdapter mAdapter;
    private Activity ctx;
    private List<Lib> datas = null;
    ColorGenerator mColorGenerator = ColorGenerator.DEFAULT;

    public String query;
    boolean canLoadMore = false;
    LibAc mCtx;
    int libType;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ctx = (Activity) getActivity();
        if (ctx instanceof LibAc) {
            mCtx = (LibAc) getActivity();
            if (XUtil.notEmptyOrNull(mCtx.key)) {
                query = mCtx.key;
            }
        }
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            libType = bundle.getInt("libType", Lib.libType.user.value());
        }

        if (XUtil.notEmptyOrNull(query)) {
            addButton.setVisibility(View.GONE);
        } else {
            addButton.setVisibility(View.VISIBLE);
        }

        listView.setCanLoadMore(false);
        datas = new ArrayList<Lib>();
        mAdapter = new LibAdapter(ctx) {
            @Override
            public void setData(final int position, MyViewHolder holder) {
                final Lib data = datas.get(position);
                if (data == null) {
                    return;
                }
                if (XUtil.notEmptyOrNull(data.getLib_name())) {
                    holder.tvTitle.setVisibility(View.VISIBLE);
                    holder.tvTitle.setText(data.getLib_name());
                    holder.ivIcon.setVisibility(View.VISIBLE);
                    TextDrawable drawable = null;
                    if (data.getLib_exi1() == Lib.libType.net.value()) {
                        drawable = TextDrawable.builder()
                                .buildRect("NET", mColorGenerator.getColor(data.getLib_name()));
                    } else if (data.getLib_exi1() == Lib.libType.sys.value()) {
                        drawable = TextDrawable.builder()
                                .buildRect("SYS", mColorGenerator.getColor(data.getLib_name()));
                    } else if (data.getLib_exi1() == Lib.libType.rss.value()) {
                        drawable = TextDrawable.builder()
                                .buildRect("RSS", mColorGenerator.getColor(data.getLib_name()));
                    } else if (data.getLib_exi1() == Lib.libType.api.value()) {
                        drawable = TextDrawable.builder()
                                .buildRect("API", mColorGenerator.getColor(data.getLib_name()));
                    } else {
                        drawable = TextDrawable.builder()
                                .buildRect(data.getLib_name().substring(0, 1), mColorGenerator.getColor(data.getLib_name()));
                    }
                    holder.ivIcon.setImageDrawable(drawable);
                } else {
                    holder.tvTitle.setVisibility(View.GONE);
                    holder.ivIcon.setVisibility(View.GONE);
                }
                holder.tvContent.setVisibility(View.GONE);
                if (data.getLib_color() > 0) {
                    holder.rlBg.setBackgroundResource(R.drawable.card_dark);
                } else {
                    holder.rlBg.setBackgroundResource(R.drawable.card);
                }
            }
        };
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                            @SuppressWarnings({"rawtypes", "unchecked"})
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                int curPosition = position - listView.getHeaderViewsCount();
                                                Lib data = datas.get(curPosition);
                                                if (data == null) {
                                                    return;
                                                }
                                                Intent intent = new Intent(ctx, ItemsAc.class);
                                                intent.putExtra(Constant.KEY_PARAM_DATA, data);
                                                startActivity(intent);
                                            }
                                        }

        );
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                int curPosition = position - listView.getHeaderViewsCount();
                final Lib data = datas.get(curPosition);
                if (data == null) {
                    return true;
                }
                final boolean star = data.getLib_color() == 1 ? true : false;
                new MaterialDialog.Builder(ctx)
                        .items(new String[]{"编辑", "删除", star ? "取消置顶" : "置顶"})
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                switch (which) {
                                    case 0:
                                        if (data.getLib_exi1() == Lib.libType.user.value()
                                                || data.getLib_exi1() == Lib.libType.rss.value() || data.getLib_exi1() == Lib.libType.net.value()) {
                                            Intent intent = new Intent(ctx, LibAddAc.class);
                                            intent.putExtra(Constant.KEY_PARAM_BOOLEAN, true);
                                            intent.putExtra(Constant.KEY_PARAM_DATA, data);
                                            startActivity(intent);
                                        } else {
                                            XUtil.tShort("系统库不允许编辑~");
                                        }
                                        break;
                                    case 1:
                                        new MaterialDialog.Builder(ctx)
                                                .title("删除")
                                                .content("确认删除" + data.getLib_name())
                                                .positiveText("删除")
                                                .negativeText("取消")
                                                .callback(new MaterialDialog.ButtonCallback() {
                                                    @Override
                                                    public void onPositive(MaterialDialog dialog) {
                                                        super.onPositive(dialog);
                                                        Dbutils.deleteData(data);
                                                        datas.remove(data);
                                                        mAdapter.setItems(datas);
                                                    }
                                                }).show();
                                        break;
                                    case 2:
                                        if (star) {
                                            data.setLib_color(0);

                                        } else {
                                            data.setLib_color(1);
                                        }
                                        Dbutils.updateLib(data);
                                        datas.set(position, data);
                                        mAdapter.setItems(datas);
                                        EventBus.getDefault().post(new RefreshEvent(EnumData.RefreshEnum.LIB.getValue()));
                                        break;
                                }
                            }
                        })
                        .show();
                return true;
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLib();
            }
        });
        addButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                initProgressDlg();
                return false;
            }
        });
        return view;
    }


    void initProgressDlg() {
        LinearLayout view = new LinearLayout(ctx);
        view.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        final EditText editText = new EditText(ctx);
        XUtil.autoKeyBoardShow(editText);
        editText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        editText.setHint("日记本|||天气:单选[晴天|阴天|下雨天|下雪天]||日记内容:文本||日期:日期");
        editText.setLines(3);
        editText.setTextColor(getResources().getColor(R.color.black));
        editText.setBackgroundDrawable(new BitmapDrawable());
        view.addView(editText);
        MaterialDialog md = new MaterialDialog.Builder(ctx)
                .customView(view, true)
                .negativeText("添加")
                .neutralText("取消")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog materialDialog) {
                    }

                    @Override
                    public void onNeutral(MaterialDialog materialDialog) {
                        XUtil.dismissShowDialog(materialDialog, true);
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        try {
                            String content = editText.getText().toString().trim();
                            if (!XUtil.notEmptyOrNull(content)) {
                                XUtil.tShort("输入内容~");
                                XUtil.dismissShowDialog(dialog, false);
                                return;
                            }
                            content = content.replaceAll("，", ",").replaceAll("：", ":");
                            String arr[] = content.split("\\|\\|\\|");
                            if (arr.length > 1) {
                                LocLibFt.mkLib(arr[0], arr[1]);
                                getData();
//                                int lib_id = Dbutils.addLib(new Lib(arr[0]));
//                                for (int i = 1; i < arr.length; i++) {
//                                    String value = arr[i];
//                                    if (XUtil.notEmptyOrNull(value)) {
//                                        String fields_content = value;
//                                        int fields_type = EnumData.FieldsTypeEnum.FIELDS_TEXT.getValue();
//                                        if (value.contains(",")) {
//                                            String fields_type_str = null;
//                                            String fields[] = value.split(",");
//                                            if (fields.length > 1) {
//                                                fields_content = fields[0];
//                                                fields_type_str = fields[1];
//                                                if (XUtil.notEmptyOrNull(fields_type_str)) {
//                                                    fields_type = EnumData.FieldsTypeEnum.nameOf(fields_type_str).getValue();
//                                                }
//                                            }
//                                        } else {
//                                            fields_content = value;
//                                        }
//                                        Fields tmp = new Fields(fields_content, false, lib_id, fields_type);
//                                        Dbutils.addFields(tmp);
//
//                                        getData();
//                                    }
//                                }
                            }
                            XUtil.dismissShowDialog(dialog, true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                })
                .autoDismiss(false)
                .build();
        md.setCancelable(false);
        md.show();

    }

    private void addLib() {
        MobclickAgent.onEvent(ctx, "add_lib");
        if (libType == Lib.libType.rss.value()) {
            initRssEtDlg();
        } else if (libType == Lib.libType.net.value()) {
            initNetEtDlg();
        } else if (libType == Lib.libType.api.value()) {
            initApiEtDlg();
        } else {
            Intent intent = new Intent(ctx, LibAddAc.class);
            startActivity(intent);
        }

    }


    void initRssEtDlg() {
        LinearLayout view = new LinearLayout(ctx);
        view.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        final MaterialEditText editText = new MaterialEditText(ctx);
        editText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        editText.setFloatingLabelText("请输入Rss源");
        editText.setFloatingLabel(MaterialEditText.FLOATING_LABEL_HIGHLIGHT);
        editText.setFloatingLabelAlwaysShown(true);
        XUtil.autoKeyBoardShow(editText);
        view.addView(editText);
        MaterialDialog md = new MaterialDialog.Builder(ctx)
                .customView(view, true)
                .positiveText("保存")
                .negativeText("取消")
                .neutralText("搜索")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        try {

                            String content = editText.getText().toString().trim();
                            if (!XUtil.notEmptyOrNull(content)) {
                                XUtil.tShort("输入内容~");
                                XUtil.dismissShowDialog(dialog, false);
                                return;
                            }
                            ShareAc.initLibRss(content, content);
                            EventBus.getDefault().post(new RefreshEvent(EnumData.RefreshEnum.LIB.getValue()));
                        } catch (Exception e) {
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onNeutral(MaterialDialog dialog) {
                        try {

                            String content = editText.getText().toString().trim();
                            if (!XUtil.notEmptyOrNull(content)) {
                                XUtil.tShort("输入内容~");
                                XUtil.dismissShowDialog(dialog, false);
                                return;
                            }

                            ApiFt.getFeedsByQuery(ctx, content);


                            XUtil.dismissShowDialog(dialog, false);

                        } catch (Exception e) {
                        }


                    }

                    @Override
                    public void onNegative(MaterialDialog materialDialog) {
                        XUtil.dismissShowDialog(materialDialog, true);
                    }
                })
                .autoDismiss(false)
                .build();
        md.setCancelable(false);
        md.show();
    }

    void initNetEtDlg() {
        LinearLayout view = new LinearLayout(ctx);
        view.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        final MaterialEditText editText = new MaterialEditText(ctx);
        editText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        editText.setFloatingLabelText("url");
        editText.setSingleLine();
        editText.setFloatingLabel(MaterialEditText.FLOATING_LABEL_HIGHLIGHT);
        editText.setFloatingLabelAlwaysShown(true);


        final MaterialEditText editText1 = new MaterialEditText(ctx);
        editText1.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        editText1.setFloatingLabelText("div/class");
        editText1.setSingleLine();
        editText1.setFloatingLabel(MaterialEditText.FLOATING_LABEL_HIGHLIGHT);
        editText1.setFloatingLabelAlwaysShown(true);


        XUtil.autoKeyBoardShow(editText);

        LinearLayout linearLayout = new LinearLayout(ctx);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(editText);
        linearLayout.addView(editText1);
        view.addView(linearLayout);
        MaterialDialog md = new MaterialDialog.Builder(ctx)
                .customView(view, true)
                .positiveText("保存")
                .negativeText("取消")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        try {

                            String content = editText.getText().toString().trim();
                            String content1 = editText1.getText().toString().trim();
                            if (!XUtil.notEmptyOrNull(content)) {
                                XUtil.tShort("输入内容~");
                                XUtil.dismissShowDialog(dialog, false);
                                return;
                            }
                            ShareAc.initLibNet(content, content1);
                            EventBus.getDefault().post(new RefreshEvent(EnumData.RefreshEnum.LIB.getValue()));
                        } catch (Exception e) {
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onNeutral(MaterialDialog materialDialog) {

                    }

                    @Override
                    public void onNegative(MaterialDialog materialDialog) {
                        XUtil.dismissShowDialog(materialDialog, true);
                    }
                })
                .autoDismiss(false)
                .build();
        md.setCancelable(false);
        md.show();
    }

    void initApiEtDlg() {
        LinearLayout view = new LinearLayout(ctx);
        view.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        final MaterialEditText editText = new MaterialEditText(ctx);
        editText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        editText.setFloatingLabelText("url");
        editText.setSingleLine();
        editText.setFloatingLabel(MaterialEditText.FLOATING_LABEL_HIGHLIGHT);
        editText.setFloatingLabelAlwaysShown(true);


        final MaterialEditText editText1 = new MaterialEditText(ctx);
        editText1.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        editText1.setFloatingLabelText("results");
        editText1.setSingleLine();
        editText1.setFloatingLabel(MaterialEditText.FLOATING_LABEL_HIGHLIGHT);
        editText1.setFloatingLabelAlwaysShown(true);


        XUtil.autoKeyBoardShow(editText);

        LinearLayout linearLayout = new LinearLayout(ctx);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(editText);
        linearLayout.addView(editText1);
        view.addView(linearLayout);
        MaterialDialog md = new MaterialDialog.Builder(ctx)
                .customView(view, true)
                .positiveText("保存")
                .negativeText("取消")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        try {

                            String content = editText.getText().toString().trim();
                            String content1 = editText1.getText().toString().trim();

                            if (!XUtil.notEmptyOrNull(content)) {
                                XUtil.tShort("输入内容~");
                                XUtil.dismissShowDialog(dialog, false);
                                return;
                            }
                            ShareAc.initLibApi(content, content1);
                            EventBus.getDefault().post(new RefreshEvent(EnumData.RefreshEnum.LIB.getValue()));
                        } catch (Exception e) {
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onNeutral(MaterialDialog materialDialog) {

                    }

                    @Override
                    public void onNegative(MaterialDialog materialDialog) {
                        XUtil.dismissShowDialog(materialDialog, true);
                    }
                })
                .autoDismiss(false)
                .build();
        md.setCancelable(false);
        md.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        try {
            List<Lib> list = Dbutils.getLibPage(query, datas.size(), libType);
            if (XUtil.listNotNull(list)) {
                canLoadMore = (list.size() == Constant.MAX_DB_QUERY);
                datas.addAll(list);
            } else {
                canLoadMore = false;
                if (XUtil.notEmptyOrNull(query)) {
                    XUtil.tShort("无结果~~");
                }
            }
            mAdapter.setItems(datas);
            listView.setCanLoadMore(canLoadMore);
            onLoadMoreComplete();

            if (!XUtil.listNotNull(datas)) {
                errorView.setVisibility(View.VISIBLE);
                errorView.setErrorImageDrawable(null);
                errorView.setErrorTitle("");
                errorView.setErrorSubtitle("请添加库");
                errorView.setRetryButtonText("添加");
                errorView.setOnRetryListener(new RetryListener() {
                    @Override
                    public void onRetry() {
                        addLib();
                    }
                });

            } else {

                errorView.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        setHasOptionsMenu(true);
//
//    }
//
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        if (!XUtil.notEmptyOrNull(query)) {
//            menu.add("md_search").setIcon(XUtil.initIconWhite(Iconify.IconValue.md_search)).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//        }
//    }
//
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        super.onOptionsItemSelected(item);
//
//        if (item == null || item.getTitle() == null) {
//            return false;
//        }
//
//        return false;
//    }

    @Override
    public void onRefresh() {
        query = "";
        if (onLoading) {
            return;
        }
        refresh();
    }

    private void refresh() {
        datas = new ArrayList<>();
        onLoading = true;
        swipeLayout.setRefreshing(true);
        getData();
    }


    @Override
    public void onLoadMore() {
        getData();
    }


}
