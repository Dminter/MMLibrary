package com.zncm.library.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.fastjson.JSON;
import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.nostra13.universalimageloader.utils.L;
import com.zncm.library.R;
import com.zncm.library.data.Constant;
import com.zncm.library.data.Fields;
import com.zncm.library.data.Items;
import com.zncm.library.data.Lib;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CSVUtils {
    static MaterialDialog dlg;

    public static boolean exportCsv(File file, List<String> dataList) {
        boolean isSucess = false;

        FileOutputStream out = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;
        try {
            out = new FileOutputStream(file);
            osw = new OutputStreamWriter(out, "UTF-8");
            bw = new BufferedWriter(osw);
            if (dataList != null && !dataList.isEmpty()) {
                for (String data : dataList) {
                    bw.append(data).append("\r");
                }
            }
            isSucess = true;
        } catch (Exception e) {
            isSucess = false;
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                    bw = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (osw != null) {
                try {
                    osw.close();
                    osw = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                    out = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return isSucess;
    }


    public static List<String> importCsv(File file, String charset) {
        List<String> dataList = new ArrayList<String>();
        try {
            // 生成CsvReader对象，以，为分隔符，GBK编码方式
            CsvReader r = new CsvReader(
                    file.getAbsolutePath(), ',',
                    Charset.forName(charset));
            // 逐条读取记录，直至读完
            while (r.readRecord()) {
                XUtil.debug("importCsv:" + r.getRawRecord());
                // 读取一条记录
                dataList.add(r.getRawRecord());
            }
            r.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList;
    }

    public static List<String> importLocCsv(Context ctx, File file) {
        List<String> dataList = new ArrayList<String>();
        try {
            CsvReader r;
            if (file != null) {
                r = new CsvReader(
                        file.getAbsolutePath(), ',',
                        Charset.forName("GBK"));
            } else {
                InputStream inputStream = ctx.getResources().openRawResource(R.raw.loclib_app);
                r = new CsvReader(
                        inputStream, ',',
                        Charset.forName("GBK"));
            }

            while (r.readRecord()) {
                XUtil.debug("getRawRecord:" + r.getRawRecord());
                // 读取一条记录
                dataList.add(r.getRawRecord());
            }
            r.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList;
    }

    public static void outCsv(Lib lib) {
        ArrayList<Items> itemses = new ArrayList<Items>();
        try {
            int lib_id = lib.getLib_id();
            itemses = Dbutils.getItems(lib_id);
            String path = MyPath.getPathData() + "/" + lib.getLib_name() + ".csv";
            CsvWriter csvWriter = new CsvWriter(path, ',', Charset.forName("UTF-8"));
            List<Fields> fieldsList = null;
            if (!XUtil.listNotNull(itemses)) {
                return;
            }
            fieldsList = Dbutils.getFields(lib_id);
            if (!XUtil.listNotNull(fieldsList)) {
                return;
            }
            String[] head = new String[fieldsList.size()];
            for (int i = 0; i < fieldsList.size(); i++) {
                head[i] = fieldsList.get(i).getFields_name();
            }
            csvWriter.writeRecord(head);
            for (Items item : itemses) {
                String[] data = new String[fieldsList.size()];
                if (XUtil.notEmptyOrNull(item.getItem_json())) {
                    Map<String, Object> map = JSON.parseObject(item.getItem_json());
                    for (int i = 0; i < fieldsList.size(); i++) {
                        String obj = (String) map.get(String.valueOf(fieldsList.get(i).getFields_id()));
                        data[i] = obj;
                    }

                }
                csvWriter.writeRecord(data);
            }
            csvWriter.flush();
            csvWriter.close();
//            XUtil.tLong("数据已导出到" + path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void outCsvUtils(Context ctx, Lib lib) {
        dlg = new MaterialDialog.Builder(ctx)
                .title("请稍后...")
                .content("数据导出中...")
                .autoDismiss(false)
                .show();
        GetData getData = new GetData();
        getData.execute(lib);
    }

    static class GetData extends AsyncTask<Lib, Integer, Boolean> {

        protected Boolean doInBackground(Lib... params) {
            outCsv(params[0]);
            return true;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected void onPostExecute(Boolean canLoadMore) {
            super.onPostExecute(canLoadMore);
            XUtil.tLong("CSV已成功导出到 " + MyPath.getPathData() + " 目录下~");
            dlg.dismiss();
        }
    }


}