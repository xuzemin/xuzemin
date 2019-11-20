
package com.ctv.welcome.vo;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.internal.DaoConfig;

public class SignatureDataDao extends AbstractDao<SignatureData, Long> {
    public static final String TABLENAME = "SIGNATURE_DATA";

    public static class Properties {
        public static final Property FilePath = new Property(1, String.class, "filePath", false,
                "FILE_PATH");

        public static final Property Id = new Property(0, Long.class, "id", true, "_id");
    }

    public SignatureDataDao(DaoConfig config) {
        super(config);
    }

    public SignatureDataDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    public static void createTable(Database db, boolean ifNotExists) {
        db.execSQL("CREATE TABLE " + (ifNotExists ? "IF NOT EXISTS " : "") + "\"SIGNATURE_DATA\" ("
                + "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + "\"FILE_PATH\" TEXT);");
    }

    public static void dropTable(Database db, boolean ifExists) {
        db.execSQL("DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"SIGNATURE_DATA\"");
    }

    protected final void bindValues(DatabaseStatement stmt, SignatureData entity) {
        stmt.clearBindings();
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id.longValue());
        }
        String filePath = entity.getFilePath();
        if (filePath != null) {
            stmt.bindString(2, filePath);
        }
    }

    protected final void bindValues(SQLiteStatement stmt, SignatureData entity) {
        stmt.clearBindings();
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id.longValue());
        }
        String filePath = entity.getFilePath();
        if (filePath != null) {
            stmt.bindString(2, filePath);
        }
    }

    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : Long.valueOf(cursor.getLong(offset + 0));
    }

    public SignatureData readEntity(Cursor cursor, int offset) {
        String str = null;
        Long valueOf = cursor.isNull(offset + 0) ? null : Long.valueOf(cursor.getLong(offset + 0));
        if (!cursor.isNull(offset + 1)) {
            str = cursor.getString(offset + 1);
        }
        return new SignatureData(valueOf, str);
    }

    public void readEntity(Cursor cursor, SignatureData entity, int offset) {
        String str = null;
        entity.setId(cursor.isNull(offset + 0) ? null : Long.valueOf(cursor.getLong(offset + 0)));
        if (!cursor.isNull(offset + 1)) {
            str = cursor.getString(offset + 1);
        }
        entity.setFilePath(str);
    }

    protected final Long updateKeyAfterInsert(SignatureData entity, long rowId) {
        entity.setId(Long.valueOf(rowId));
        return Long.valueOf(rowId);
    }

    public Long getKey(SignatureData entity) {
        if (entity != null) {
            return entity.getId();
        }
        return null;
    }

    public boolean hasKey(SignatureData entity) {
        return entity.getId() != null;
    }

    protected final boolean isEntityUpdateable() {
        return true;
    }
}
