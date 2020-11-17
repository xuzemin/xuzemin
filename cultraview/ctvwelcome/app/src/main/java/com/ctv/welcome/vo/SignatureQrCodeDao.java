
package com.ctv.welcome.vo;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.internal.DaoConfig;

public class SignatureQrCodeDao extends AbstractDao<SignatureQrCode, Long> {
    public static final String TABLENAME = "SIGNATURE_QR_CODE";

    public static class Properties {
        public static final Property FileName = new Property(1, String.class, "fileName", false,
                "FILE_NAME");

        public static final Property FilePath = new Property(2, String.class, "filePath", false,
                "FILE_PATH");

        public static final Property Id = new Property(0, Long.class, "id", true, "_id");
    }

    public SignatureQrCodeDao(DaoConfig config) {
        super(config);
    }

    public SignatureQrCodeDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    public static void createTable(Database db, boolean ifNotExists) {
        db.execSQL("CREATE TABLE " + (ifNotExists ? "IF NOT EXISTS " : "")
                + "\"SIGNATURE_QR_CODE\" (" + "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + "\"FILE_NAME\" TEXT," + "\"FILE_PATH\" TEXT);");
    }

    public static void dropTable(Database db, boolean ifExists) {
        db.execSQL("DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"SIGNATURE_QR_CODE\"");
    }

    protected final void bindValues(DatabaseStatement stmt, SignatureQrCode entity) {
        stmt.clearBindings();
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id.longValue());
        }
        String fileName = entity.getFileName();
        if (fileName != null) {
            stmt.bindString(2, fileName);
        }
        String filePath = entity.getFilePath();
        if (filePath != null) {
            stmt.bindString(3, filePath);
        }
    }

    protected final void bindValues(SQLiteStatement stmt, SignatureQrCode entity) {
        stmt.clearBindings();
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id.longValue());
        }
        String fileName = entity.getFileName();
        if (fileName != null) {
            stmt.bindString(2, fileName);
        }
        String filePath = entity.getFilePath();
        if (filePath != null) {
            stmt.bindString(3, filePath);
        }
    }

    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : Long.valueOf(cursor.getLong(offset + 0));
    }

    public SignatureQrCode readEntity(Cursor cursor, int offset) {
        String str = null;
        Long valueOf = cursor.isNull(offset + 0) ? null : Long.valueOf(cursor.getLong(offset + 0));
        String string = cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1);
        if (!cursor.isNull(offset + 2)) {
            str = cursor.getString(offset + 2);
        }
        return new SignatureQrCode(valueOf, string, str);
    }

    public void readEntity(Cursor cursor, SignatureQrCode entity, int offset) {
        String str = null;
        entity.setId(cursor.isNull(offset + 0) ? null : Long.valueOf(cursor.getLong(offset + 0)));
        entity.setFileName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        if (!cursor.isNull(offset + 2)) {
            str = cursor.getString(offset + 2);
        }
        entity.setFilePath(str);
    }

    protected final Long updateKeyAfterInsert(SignatureQrCode entity, long rowId) {
        entity.setId(Long.valueOf(rowId));
        return Long.valueOf(rowId);
    }

    public Long getKey(SignatureQrCode entity) {
        if (entity != null) {
            return entity.getId();
        }
        return null;
    }

    public boolean hasKey(SignatureQrCode entity) {
        return entity.getId() != null;
    }

    protected final boolean isEntityUpdateable() {
        return true;
    }
}
