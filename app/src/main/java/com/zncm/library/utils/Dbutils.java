package com.zncm.library.utils;

import com.alibaba.fastjson.JSON;
import com.csvreader.CsvWriter;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.nostra13.universalimageloader.utils.L;
import com.zncm.library.data.BaseData;
import com.zncm.library.data.Constant;
import com.zncm.library.data.EnumData;
import com.zncm.library.data.Fields;
import com.zncm.library.data.Items;
import com.zncm.library.data.Lib;
import com.zncm.library.data.LocLib;
import com.zncm.library.data.MyApplication;
import com.zncm.library.data.Options;
import com.zncm.library.utils.db.DatabaseHelper;

import java.io.FileWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by MX on 3/24 0024.
 */
public class Dbutils {

    static RuntimeExceptionDao<Lib, Integer> libDao;
    static RuntimeExceptionDao<Fields, Integer> fieldsDao;
    static RuntimeExceptionDao<Items, Integer> itemsDao;
    static RuntimeExceptionDao<Options, Integer> optionsDao;
    static RuntimeExceptionDao<LocLib, Integer> locDao;
    static DatabaseHelper databaseHelper = null;

    static DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(MyApplication.getInstance().ctx, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    static void init() {
        if (libDao == null) {
            libDao = getHelper().getLibDao();
        }

        if (itemsDao == null) {
            itemsDao = getHelper().getItemsDao();
        }

        if (fieldsDao == null) {
            fieldsDao = getHelper().getFieldsDao();
        }
        if (optionsDao == null) {
            optionsDao = getHelper().getOptionsDao();
        }
        if (locDao == null) {
            locDao = getHelper().getLocLibDao();
        }

    }

    public static ArrayList<Fields> getFields(int lib_id) {
        init();
        ArrayList<Fields> datas = new ArrayList<>();
        try {
            QueryBuilder<Fields, Integer> builder = fieldsDao.queryBuilder();
            builder.orderBy("fields_exi1", false).orderBy("fields_type", true).orderBy("fields_modify_time", true);
            builder.where().eq("lib_id", lib_id);
            List<Fields> list = fieldsDao.query(builder.prepare());
            if (XUtil.listNotNull(list)) {
                datas.addAll(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return datas;
    }

    public static ArrayList<Items> getItems(int lib_id) {
        init();
        ArrayList<Items> datas = new ArrayList<>();
        try {
            QueryBuilder<Items, Integer> builder = itemsDao.queryBuilder();
            builder.orderBy("item_time", false);
            builder.where().eq("lib_id", lib_id);
            builder.limit(Constant.MAX_DB_QUERY);
            List<Items> list = itemsDao.query(builder.prepare());
            if (XUtil.listNotNull(list)) {
                datas.addAll(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return datas;
    }


    public static List<Items> getItemsPage(int lib_id, int pageIndex) {
        init();
        List<Items> datas = new ArrayList<Items>();
        try {
            QueryBuilder<Items, Integer> builder = itemsDao.queryBuilder();
            builder.where().eq("lib_id", lib_id);
            builder.orderBy("item_time", false).limit(Constant.MAX_DB_QUERY).offset((long) pageIndex);
            datas = itemsDao.query(builder.prepare());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return datas;
    }


    public static List<Items> getItemsPage(String query, int lib_id, int pageIndex) {
        init();
        List<Items> datas = new ArrayList<Items>();
        try {
            QueryBuilder<Items, Integer> builder = itemsDao.queryBuilder();

            if (XUtil.notEmptyOrNull(query)) {
                builder.where().eq("lib_id", lib_id).and().like("item_json", "%" + query + "%");
            } else {
                builder.where().eq("lib_id", lib_id);
            }
            builder.orderBy("item_time", false).limit(Constant.MAX_DB_QUERY).offset((long) pageIndex);
            datas = itemsDao.query(builder.prepare());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return datas;
    }

    public static List<Items> getItemsPageMaxRows(int lib_id, int maxRows) {
        init();
        List<Items> datas = new ArrayList<Items>();
        try {
            QueryBuilder<Items, Integer> builder = itemsDao.queryBuilder();
            builder.where().eq("lib_id", lib_id);
            builder.orderBy("item_time", false).limit(maxRows);
            datas = itemsDao.query(builder.prepare());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return datas;
    }

    public static long getItemsSize(int lib_id) {
        long totalSize = 0;
        init();
        try {
            totalSize = itemsDao.queryBuilder().where().eq("lib_id", lib_id).countOf();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalSize;
    }


    public static ArrayList<Lib> getLib() {
        init();
        ArrayList<Lib> datas = new ArrayList<>();
        try {
            QueryBuilder<Lib, Integer> builder = libDao.queryBuilder();
            builder.orderBy("lib_time", false);
            List<Lib> list = libDao.query(builder.prepare());
            if (XUtil.listNotNull(list)) {
                datas.addAll(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return datas;
    }

    public static ArrayList<LocLib> getLocLib(String query, int pageIndex) {
        init();
        ArrayList<LocLib> datas = new ArrayList<>();
        try {
            QueryBuilder<LocLib, Integer> builder = locDao.queryBuilder();
            if (XUtil.notEmptyOrNull(query)) {
                builder.where().like("loc_name", "%" + query + "%").or().like("loc_tag", "%" + query + "%");
            }
            builder.orderBy("loc_sort", false).orderBy("loc_date", false).orderBy("loc_time", false).limit(Constant.MAX_DB_QUERY).offset((long) pageIndex);

            List<LocLib> list = locDao.query(builder.prepare());
            if (XUtil.listNotNull(list)) {
                datas.addAll(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return datas;
    }


    public static List<Lib> getLibPage(String query, int pageIndex, int libType) {
        init();
        ArrayList<Lib> datas = new ArrayList<>();
        try {
            QueryBuilder<Lib, Integer> builder = libDao.queryBuilder();
            if (XUtil.notEmptyOrNull(query)) {
                builder.where().like("lib_name", "%" + query + "%");
//                builder.where().like("lib_name", "%" + query + "%").and().eq("lib_exi1", libType);
            } else {
                builder.where().eq("lib_exi1", libType);
            }
            builder.orderBy("lib_color", false).orderBy("lib_time", false).limit(Constant.MAX_DB_QUERY).offset((long) pageIndex);
            List<Lib> list = libDao.query(builder.prepare());
            if (XUtil.listNotNull(list)) {
                datas.addAll(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return datas;
    }

    public static Lib getLibSys(String lib_neme) {
        init();
        Lib ret = null;
        try {
            QueryBuilder<Lib, Integer> builder = libDao.queryBuilder();
            builder.where().eq("lib_name", lib_neme);
            List<Lib> list = libDao.query(builder.prepare());
            if (XUtil.listNotNull(list)) {
                ret = list.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static Lib getLibById(int lib_id) {
        init();
        Lib ret = null;
        try {
            QueryBuilder<Lib, Integer> builder = libDao.queryBuilder();
            builder.where().eq("lib_id", lib_id);
            List<Lib> list = libDao.query(builder.prepare());
            if (XUtil.listNotNull(list)) {
                ret = list.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }


    public static ArrayList<Options> getOptions(int fields_id) {
        init();
        ArrayList<Options> datas = new ArrayList<>();
        try {
            QueryBuilder<Options, Integer> builder = optionsDao.queryBuilder();
            builder.where().eq("fields_id", fields_id);
            List<Options> list = optionsDao.query(builder.prepare());
            if (XUtil.listNotNull(list)) {
                datas.addAll(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return datas;
    }


    public static void addFields(Fields fields) {
        init();
        try {
            if (!XUtil.notEmptyOrNull(fields.getFields_name())) {
                XUtil.tShort("字段名必填");
                return;
            }
            if (fields.getLib_id() == 0) {
                XUtil.tShort("库不能为空");
                return;
            }
            QueryBuilder<Fields, Integer> builder = fieldsDao.queryBuilder();
            builder.where().eq("lib_id", fields.getLib_id()).and().eq("fields_name", fields.getFields_name());
            List<Fields> tmp = fieldsDao.query(builder.prepare());
            if (!XUtil.listNotNull(tmp)) {
                fieldsDao.create(fields);
//                XUtil.tShort("添加成功");
            } else {
                XUtil.tShort("字段名必须唯一");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int addFieldsID(Fields fields) {
        int _id = 0;
        init();
        try {
            addFields(fields);
            _id = getFieldSaveId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return _id;

    }

    public static int addItemsID(Items items) {
        int _id = 0;
        init();
        try {
            addItems(items);
            _id = getItemsSaveId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return _id;

    }


    public static void addOptions(Options options) {
        init();
        try {
            if (!XUtil.notEmptyOrNull(options.getOptions_name())) {
                XUtil.tShort("选项名必填");
                return;
            }
            QueryBuilder<Options, Integer> builder = optionsDao.queryBuilder();
            builder.where().eq("fields_id", options.getFields_id()).and().eq("options_name", options.getOptions_name());
            List<Options> tmp = optionsDao.query(builder.prepare());
            if (!XUtil.listNotNull(tmp)) {
                optionsDao.create(options);
//                XUtil.tShort("添加成功");
            } else {
                XUtil.tShort("选项名必须唯一");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void deleteLocLibAll() {
        init();
        try {
            DeleteBuilder db1 = locDao.deleteBuilder();
            db1.where().notIn("loc_id", -1);
            db1.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteData(BaseData data) {
        init();
        try {
            if (data instanceof Fields) {
                fieldsDao.deleteById(((Fields) data).getFields_id());
            } else if (data instanceof Items) {
                itemsDao.deleteById(((Items) data).getItem_id());
            } else if (data instanceof Lib) {
                LocLib tmp = findLocLib(((Lib) data).getLib_name());
                if (tmp != null) {
                    Dbutils.uploadLocLib(tmp.getLoc_id(), 0);
                }
                libDao.deleteById(((Lib) data).getLib_id());
                deleteWhereData(((Lib) data).getLib_id());
            } else if (data instanceof Options) {
                optionsDao.deleteById(((Options) data).getOptions_id());
            }
            XUtil.tShort("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteWhereData(int lib_id) {
        init();
        try {
            DeleteBuilder db1 = fieldsDao.deleteBuilder();
            db1.where().eq("lib_id", lib_id);
            db1.delete();
            DeleteBuilder db2 = itemsDao.deleteBuilder();
            db2.where().eq("lib_id", lib_id);
            db2.delete();
            DeleteBuilder db3 = optionsDao.deleteBuilder();
            db3.where().eq("lib_id", lib_id);
            db3.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static void deleteLibItems(int lib_id) {
        init();
        try {
            DeleteBuilder db2 = itemsDao.deleteBuilder();
            db2.where().eq("lib_id", lib_id);
            db2.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static void updateFields(Fields fields) {
        init();
        try {
            Fields old = fieldsDao.queryForId(fields.getFields_id());
            old.setFields_notnull(fields.isFields_notnull());
            old.setFields_name(fields.getFields_name());
            old.setFields_exi1(fields.getFields_exi1());//排序
            old.setFields_modify_time(System.currentTimeMillis());
            fieldsDao.update(old);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int addLib(Lib lib) {
        int _id = 0;
        init();
        try {
            if (!XUtil.notEmptyOrNull(lib.getLib_name())) {
                XUtil.tShort("库名必填");
                return _id;
            }
            libDao.create(lib);
            _id = getLibSaveId();
//            XUtil.tShort("添加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return _id;

    }

    public static void addLocLib(LocLib lib) {
        init();
        try {
            if (!XUtil.notEmptyOrNull(lib.getLoc_name())) {
                return;
            }
            List<LocLib> tmp = locDao.queryForEq("loc_name", lib.getLoc_name());
            if (!XUtil.listNotNull(tmp)) {
                locDao.create(lib);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Lib findSysLib(String sysName, int libType) {
        init();
        try {
            if (!XUtil.notEmptyOrNull(sysName)) {
                return null;
            }
            QueryBuilder<Lib, Integer> builder = libDao.queryBuilder();
            builder.where().eq("lib_name", sysName).and().eq("lib_exi1", libType);
            List<Lib> list = libDao.query(builder.prepare());
            if (XUtil.listNotNull(list)) {
                return list.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Items findItems(int _id) {
        init();
        try {
            return itemsDao.queryForId(_id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Lib findLib(int _id) {
        init();
        try {
            return libDao.queryForId(_id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static LocLib findLocLib(String lib_name) {
        init();
        try {
            if (!XUtil.notEmptyOrNull(lib_name)) {
                return null;
            }
            List<LocLib> tmp = locDao.queryForEq("loc_name", lib_name);
            if (XUtil.listNotNull(tmp)) {
                return tmp.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void uploadLocLib(int locLibId, int status) {
        init();
        try {
            LocLib old = locDao.queryForId(locLibId);
            old.setLoc_status(status);
            locDao.update(old);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateLib(Lib lib) {
        init();
        try {
            if (!XUtil.notEmptyOrNull(lib.getLib_name())) {
                XUtil.tShort("库名不能为空");
                return;
            }
            Lib old = libDao.queryForId(lib.getLib_id());
            old.setLib_name(lib.getLib_name());
            old.setLib_color(lib.getLib_color());
            old.setLib_exs1(lib.getLib_exs1());
            old.setLib_exi1(lib.getLib_exi1());
            old.setLib_exi2(lib.getLib_exi2());
            old.setLib_exb2(lib.isLib_exb2());
            old.setLib_modify_time(System.currentTimeMillis());
            libDao.update(old);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateOptions(Options options) {
        init();
        try {
            if (!XUtil.notEmptyOrNull(options.getOptions_name())) {
                XUtil.tShort("选项名不能为空");
                return;
            }
            Options old = optionsDao.queryForId(options.getOptions_id());
            old.setOptions_name(options.getOptions_name());
            old.setItem_modify_time(System.currentTimeMillis());
            optionsDao.update(old);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addItems(Items items) {
        init();
        try {
            itemsDao.create(items);
//            XUtil.tShort("添加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void updateItems(Items items) {
        init();
        try {
            Items old = itemsDao.queryForId(items.getItem_id());
            old.setItem_json(items.getItem_json());
            old.setItem_modify_time(System.currentTimeMillis());
            itemsDao.update(old);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void readItems(int _id, boolean isRead) {
        init();
        try {
            Items old = itemsDao.queryForId(_id);
            old.setItem_exb1(isRead);
            itemsDao.update(old);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getFieldSaveId() {
        int _id = 0;
        try {
            ArrayList<Fields> fieldses = new ArrayList<Fields>();
            QueryBuilder<Fields, Integer> builder = fieldsDao.queryBuilder();
            builder.orderBy("fields_id", false).limit(1l);
            fieldses = (ArrayList<Fields>) fieldsDao.query(builder.prepare());
            Fields tmp = fieldses.get(0);
            if (tmp != null) {
                _id = tmp.getFields_id();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return _id;

    }

    public static int getItemsSaveId() {
        int _id = 0;
        try {
            ArrayList<Items> itemses = new ArrayList<Items>();
            QueryBuilder<Items, Integer> builder = itemsDao.queryBuilder();
            builder.orderBy("item_id", false).limit(1l);
            itemses = (ArrayList<Items>) itemsDao.query(builder.prepare());
            Items tmp = itemses.get(0);
            if (tmp != null) {
                _id = tmp.getItem_id();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return _id;

    }

    public static int getLibSaveId() {
        int _id = 0;
        try {
            ArrayList<Lib> libs = new ArrayList<Lib>();
            QueryBuilder<Lib, Integer> builder = libDao.queryBuilder();
            builder.orderBy("lib_id", false).limit(1l);
            libs = (ArrayList<Lib>) libDao.query(builder.prepare());
            Lib lib = libs.get(0);
            if (lib != null) {
                _id = lib.getLib_id();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return _id;

    }

    public static int getFieldsSaveId() {
        int _id = 0;
        try {
            ArrayList<Fields> libs = new ArrayList<Fields>();
            QueryBuilder<Fields, Integer> builder = fieldsDao.queryBuilder();
            builder.orderBy("fields_id", false).limit(1l);
            libs = (ArrayList<Fields>) fieldsDao.query(builder.prepare());
            Fields fields = libs.get(0);
            if (fields != null) {
                _id = fields.getFields_id();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return _id;

    }
}
