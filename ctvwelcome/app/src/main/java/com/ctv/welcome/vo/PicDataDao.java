
package com.ctv.welcome.vo;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.internal.DaoConfig;

public class PicDataDao extends AbstractDao<PicData, Long> {
    public static final String TABLENAME = "PIC_DATA";

    public static class Properties {
        public static final Property CodePath = new Property(7, String.class, "codePath", false,
                "CODE_PATH");

        public static final Property FileName = new Property(1, String.class, "fileName", false,
                "FILE_NAME");

        public static final Property FilePath = new Property(3, String.class, "filePath", false,
                "FILE_PATH");

        public static final Property FileType = new Property(2, Boolean.TYPE, "fileType", false,
                "FILE_TYPE");

        public static final Property HtmlText = new Property(4, String.class, "htmlText", false,
                "HTML_TEXT");

        public static final Property Id = new Property(0, Long.class, "id", true, "_id");

        public static final Property X = new Property(5, Float.TYPE, "x", false, "X");

        public static final Property Y = new Property(6, Float.TYPE, "y", false, "Y");
    }

    public PicDataDao(DaoConfig config) {
        super(config);
    }

    public PicDataDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    public static void createTable(Database db, boolean ifNotExists) {
        db.execSQL("CREATE TABLE " + (ifNotExists ? "IF NOT EXISTS " : "") + "\"PIC_DATA\" ("
                + "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + "\"FILE_NAME\" TEXT,"
                + "\"FILE_TYPE\" INTEGER NOT NULL ," + "\"FILE_PATH\" TEXT,"
                + "\"HTML_TEXT\" TEXT," + "\"X\" REAL NOT NULL ," + "\"Y\" REAL NOT NULL ,"
                + "\"CODE_PATH\" TEXT);");
    }

    public static void dropTable(Database db, boolean ifExists) {
        db.execSQL("DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"PIC_DATA\"");
    }

    protected final void bindValues(DatabaseStatement stmt, PicData entity) {
        stmt.clearBindings();
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id.longValue());
        }
        String fileName = entity.getFileName();
        if (fileName != null) {
            stmt.bindString(2, fileName);
        }
        stmt.bindLong(3, entity.getFileType() ? 1 : 0);
        String filePath = entity.getFilePath();
        if (filePath != null) {
            stmt.bindString(4, filePath);
        }
        String htmlText = entity.getHtmlText();
        if (htmlText != null) {
            stmt.bindString(5, htmlText);
        }
        stmt.bindDouble(6, (double) entity.getX());
        stmt.bindDouble(7, (double) entity.getY());
        String codePath = entity.getCodePath();
        if (codePath != null) {
            stmt.bindString(8, codePath);
        }
    }

    protected final void bindValues(SQLiteStatement stmt, PicData entity) {
        stmt.clearBindings();
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id.longValue());
        }
        String fileName = entity.getFileName();
        if (fileName != null) {
            stmt.bindString(2, fileName);
        }
        stmt.bindLong(3, entity.getFileType() ? 1 : 0);
        String filePath = entity.getFilePath();
        if (filePath != null) {
            stmt.bindString(4, filePath);
        }
        String htmlText = entity.getHtmlText();
        if (htmlText != null) {
            stmt.bindString(5, htmlText);
        }
        stmt.bindDouble(6, (double) entity.getX());
        stmt.bindDouble(7, (double) entity.getY());
        String codePath = entity.getCodePath();
        if (codePath != null) {
            stmt.bindString(8, codePath);
        }
    }

    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : Long.valueOf(cursor.getLong(offset + 0));
    }

    public PicData readEntity(Cursor cursor, int offset) {
        String str = null;
        Long valueOf = cursor.isNull(offset + 0) ? null : Long.valueOf(cursor.getLong(offset + 0));
        String string = cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1);
        boolean z = cursor.getShort(offset + 2) != (short) 0;
        String string2 = cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3);
        String string3 = cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4);
        float f = cursor.getFloat(offset + 5);
        float f2 = cursor.getFloat(offset + 6);
        if (!cursor.isNull(offset + 7)) {
            str = cursor.getString(offset + 7);
        }
        return new PicData(valueOf, string, z, string2, string3, f, f2, str);
    }

    public void readEntity(Cursor cursor, PicData entity, int offset) {
        String str = null;
        entity.setId(cursor.isNull(offset + 0) ? null : Long.valueOf(cursor.getLong(offset + 0)));
        entity.setFileName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setFileType(cursor.getShort(offset + 2) != (short) 0);
        entity.setFilePath(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setHtmlText(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setX(cursor.getFloat(offset + 5));
        entity.setY(cursor.getFloat(offset + 6));
        if (!cursor.isNull(offset + 7)) {
            str = cursor.getString(offset + 7);
        }
        entity.setCodePath(str);
    }

    protected final Long updateKeyAfterInsert(PicData entity, long rowId) {
        entity.setId(Long.valueOf(rowId));
        return Long.valueOf(rowId);
    }

    public Long getKey(PicData entity) {
        if (entity != null) {
            return entity.getId();
        }
        return null;
    }

    public boolean hasKey(PicData entity) {
        return entity.getId() != null;
    }

    protected final boolean isEntityUpdateable() {
        return true;
    }
}
