package com.zhangshuo.magicnotice.db;


import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.zhangshuo.common.AppUtil;
import com.zhangshuo.magicnotice.db.dao.ConfigRuleDataDao;
import com.zhangshuo.magicnotice.db.dao.GlobalConfigDataDao;
import com.zhangshuo.magicnotice.db.dao.NotificationDataDao;
import com.zhangshuo.magicnotice.db.dao.RemindDataDao;
import com.zhangshuo.magicnotice.db.dao.TabDataDao;
import com.zhangshuo.magicnotice.db.dao.TxtDataDao;
import com.zhangshuo.magicnotice.db.model.ConfigRuleData;
import com.zhangshuo.magicnotice.db.model.GlobalConfigData;
import com.zhangshuo.magicnotice.db.model.NotificationData;
import com.zhangshuo.magicnotice.db.model.RemindData;
import com.zhangshuo.magicnotice.db.model.SchedulerData;
import com.zhangshuo.magicnotice.db.model.SchedulerRecordData;
import com.zhangshuo.magicnotice.db.model.TabData;
import com.zhangshuo.magicnotice.db.model.TxtClassifyData;
import com.zhangshuo.magicnotice.db.model.TxtData;

@Database(entities = {NotificationData.class, ConfigRuleData.class, GlobalConfigData.class, TabData.class,
        SchedulerData.class, SchedulerRecordData.class, TxtData.class, TxtClassifyData.class, RemindData.class},
        version = 20, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract NotificationDataDao notificationDataDao();

    public abstract ConfigRuleDataDao configRuleDataDao();

    public abstract GlobalConfigDataDao globalConfigDataDao();

    public abstract TabDataDao tabDataDao();

    public abstract RemindDataDao remindDataDao();

    public abstract TxtDataDao txtDataDao();


    // 数据库升级
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE notificationdata ADD COLUMN `lock` INTEGER NOT NULL DEFAULT 0");
        }
    };
    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `GlobalConfigData` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `code` TEXT, `value` TEXT)");
        }
    };
    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE configruledata ADD COLUMN `tagColor` TEXT");
        }
    };
    static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE configruledata ADD COLUMN `cleanTime` TEXT");
        }
    };
    static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `TabData` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT, `relationIds` TEXT)");
        }
    };
    static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE TabData ADD COLUMN `openFlag` INTEGER NOT NULL default 1");
        }
    };
    static final Migration MIGRATION_7_8 = new Migration(7, 8) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE TabData ADD COLUMN `lastNoticeId` INTEGER NOT NULL default 0");
        }
    };
    static final Migration MIGRATION_8_9 = new Migration(8, 9) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE notificationdata ADD COLUMN `readType` INTEGER NOT NULL default 1");
        }
    };
    static final Migration MIGRATION_9_10 = new Migration(9, 10) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE TabData ADD COLUMN `relationDateOffset` INTEGER NOT NULL default -1");
            database.execSQL("ALTER TABLE TabData ADD COLUMN `relationPkgs` TEXT");
        }
    };
    static final Migration MIGRATION_10_11 = new Migration(10, 11) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE TabData ADD COLUMN `receiveFlag` INTEGER NOT NULL default 0");
        }
    };
    static final Migration MIGRATION_11_12 = new Migration(11, 12) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE TabData ADD COLUMN `showKeyWord` TEXT");
        }
    };

    static final Migration MIGRATION_12_13 = new Migration(12, 13) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `SchedulerData` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT, `content` TEXT" +
                    ", `dateStr` TEXT, `timeStr` TEXT, `retain` INTEGER NOT NULL default 0,`behaviorList` TEXT)");
        }
    };
    static final Migration MIGRATION_13_14 = new Migration(13, 14) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `SchedulerRecordData` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`schedulerId` INTEGER NOT NULL default 0, `postTime` INTEGER NOT NULL default 0)");
        }
    };
    static final Migration MIGRATION_14_15 = new Migration(14, 15) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `TxtData` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,`classifyId` INTEGER NOT NULL DEFAULT 0, `title` TEXT,  `content` TEXT)");
            database.execSQL("CREATE TABLE IF NOT EXISTS `TxtClassifyData` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,`title` TEXT)");
            database.execSQL("ALTER TABLE SchedulerData ADD COLUMN `classifyId` INTEGER NOT NULL DEFAULT 0");
        }
    };
    static final Migration MIGRATION_15_16 = new Migration(15, 16) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE SchedulerData ADD COLUMN `notifyNumber` INTEGER NOT NULL DEFAULT 0");
        }
    };
    static final Migration MIGRATION_16_17 = new Migration(16, 17) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("DELETE FROM SchedulerData");
            database.execSQL("DELETE FROM SchedulerRecordData");
            database.execSQL("CREATE TABLE IF NOT EXISTS `RemindData` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "`remindType` INTEGER NOT NULL DEFAULT 0, " +
                    "`title` TEXT,  `content` TEXT, " +
                    "`classifyId` INTEGER NOT NULL DEFAULT 0, " +
                    "`timeJson` TEXT," +
                    "`notifyNumber` INTEGER NOT NULL DEFAULT 0," +
                    "`notifyCount` INTEGER NOT NULL DEFAULT 0)");
        }
    };

    static final Migration MIGRATION_17_18 = new Migration(17, 18) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE ConfigRuleData ADD COLUMN `policy` INTEGER NOT NULL DEFAULT 0");
        }
    };
    static final Migration MIGRATION_18_19 = new Migration(18, 19) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE RemindData ADD COLUMN `mockPkg` TEXT");
        }
    };

    static final Migration MIGRATION_19_20 = new Migration(19, 20) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE ConfigRuleData ADD COLUMN `collect` INTEGER NOT NULL DEFAULT 0");
        }
    };

    //
    /// 单例模式获取
    private static final String DB_NAME = "magic_data.db";

    public static AppDatabase getInstance() {
        return Holder.instance;
    }

    public static final class Holder {
        private static final AppDatabase instance = Room.databaseBuilder(
                        AppUtil.get().getApplication(), AppDatabase.class, DB_NAME)
                .addMigrations(MIGRATION_1_2)
                .addMigrations(MIGRATION_2_3)
                .addMigrations(MIGRATION_3_4)
                .addMigrations(MIGRATION_4_5)
                .addMigrations(MIGRATION_5_6)
                .addMigrations(MIGRATION_6_7)
                .addMigrations(MIGRATION_7_8)
                .addMigrations(MIGRATION_8_9)
                .addMigrations(MIGRATION_9_10)
                .addMigrations(MIGRATION_10_11)
                .addMigrations(MIGRATION_11_12)
                .addMigrations(MIGRATION_12_13)
                .addMigrations(MIGRATION_13_14)
                .addMigrations(MIGRATION_14_15)
                .addMigrations(MIGRATION_15_16)
                .addMigrations(MIGRATION_16_17)
                .addMigrations(MIGRATION_17_18)
                .addMigrations(MIGRATION_18_19)
                .addMigrations(MIGRATION_19_20)
                .build();
    }

}
