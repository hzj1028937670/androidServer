//package com.android.server.db;
//
//import com.etv.entity.PoOnOffLogEntity;
//import com.etv.util.MyLog;
//
//import org.litepal.crud.DataSupport;
//
//import java.util.List;
//
///***
// * 用来保存定时开关机Log的文件
// */
//public class PowerOnOffLogDb {
//
//    /***
//     * 保存控件数据库
//     * @param entity
//     * @return
//     */
//    public static boolean savePowerOnOffToWeb(PoOnOffLogEntity entity) {
//        if (entity == null) {
//            return false;
//        }
//        String onTime = entity.getOnTime();
//        String offTime = entity.getOffTime();
//        try {
//            List<PoOnOffLogEntity> cpList = DataSupport.where("onTime=? and offTime=?", onTime + "", offTime + "").find(PoOnOffLogEntity.class);
//            if (cpList == null || cpList.size() < 1) {
//                MyLog.cdl("===0000====没有数据，添加到数据库");
//                return addPowerInfoToDb(entity);
//            } else {
//                MyLog.cdl("===0000====有数据，直接忽略");
//                return true;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        MyLog.cdl("===0000====不知道为什么");
//        return false;
//    }
//
//    /**
//     * 保存数据到数据库
//     *
//     * @param entity
//     * @return
//     */
//    public static boolean addPowerInfoToDb(PoOnOffLogEntity entity) {
//        boolean isSave = false;
//        if (entity == null) {
//            return isSave;
//        }
//        try {
//            isSave = entity.save();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return isSave;
//    }
//
//
//    public static List<PoOnOffLogEntity> getPowerInfoList() {
//        List<PoOnOffLogEntity> txtList = null;
//        try {
//            txtList = DataSupport.findAll(PoOnOffLogEntity.class);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return txtList;
//    }
//
//    public static void clearAllData() {
//        DataSupport.deleteAll(PoOnOffLogEntity.class);
//    }
//
//}
