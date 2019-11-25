
package com.ctv.welcome.vo;

import java.util.Map;
import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

public class DaoSession extends AbstractDaoSession {
    private  DaoConfig picDataDaoConfig;

    private  DaoConfig signatureQrCodeDaoConfig;
    private  DaoConfig signatureDataDaoConfig;
    private  PicDataDao picDataDao ;
    private  SignatureQrCodeDao signatureQrCodeDao;
    private  SignatureDataDao signatureDataDao;

    public DaoSession(Database db, IdentityScopeType type,
            Map<Class<? extends AbstractDao<?, ?>>, DaoConfig> daoConfigMap) {
        super(db);
        picDataDaoConfig = ((DaoConfig) daoConfigMap.get(PicDataDao.class)).clone();
        picDataDaoConfig.initIdentityScope(type);
        picDataDao = new PicDataDao(picDataDaoConfig, this);
        signatureDataDaoConfig = ((DaoConfig) daoConfigMap.get(SignatureDataDao.class))
                .clone();
        signatureDataDaoConfig.initIdentityScope(type);
        signatureDataDao = new SignatureDataDao(
                signatureDataDaoConfig, this);

        signatureQrCodeDaoConfig = ((DaoConfig) daoConfigMap.get(SignatureQrCodeDao.class))
                .clone();
        signatureQrCodeDaoConfig.initIdentityScope(type);
        signatureQrCodeDao = new SignatureQrCodeDao(
                signatureQrCodeDaoConfig, this);

        registerDao(PicData.class, picDataDao);
        registerDao(SignatureData.class, signatureDataDao);
        registerDao(SignatureQrCode.class, signatureQrCodeDao);
    }


    public void clear() {
        picDataDaoConfig.clearIdentityScope();
        signatureDataDaoConfig.clearIdentityScope();
        signatureQrCodeDaoConfig.clearIdentityScope();
    }

    public PicDataDao getPicDataDao() {
        return picDataDao;
    }

    public SignatureDataDao getSignatureDataDao() {
        return signatureDataDao;
    }

    public SignatureQrCodeDao getSignatureQrCodeDao() {
        return signatureQrCodeDao;
    }
}
