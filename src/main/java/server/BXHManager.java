package server;

import real.ItemData;
import real.ClanManager;

import java.util.Timer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

public class BXHManager {
    public static final ArrayList<Entry>[] bangXH;
    public static final Timer t;

    public static void init() {
        for (int i = 0; i < BXHManager.bangXH.length; ++i) {
            BXHManager.bangXH[i] = new ArrayList<Entry>();
        }
        util.Debug("load BXH");
        for (int i = 0; i < BXHManager.bangXH.length; ++i) {
            initBXH(i);
        }
        Calendar cl = Calendar.getInstance();
        Date d = new Date();
        cl.setTime(d);
        cl.set(Calendar.HOUR_OF_DAY, 0);
        cl.set(Calendar.MINUTE, 0);
        cl.set(Calendar.SECOND, 0);
        cl.set(Calendar.MILLISECOND, 0);
        cl.add(Calendar.DATE, 0);
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                for (int i = 0; i < BXHManager.bangXH.length; ++i) {
                    initBXH(i);
                }
            //    System.err.println("Refresh BXH");
            }
        }, cl.getTime(), 1000*15*2);
    }

    public static void initBXH(final int type) {
        BXHManager.bangXH[type].clear();
        final ArrayList<Entry> bxh = BXHManager.bangXH[type];
        switch (type) {
            case 0: {

                SQLManager.executeQuery("SELECT `name`,`yen`,`level` FROM `ninja` WHERE (`yen` > 0) ORDER BY `yen` DESC LIMIT 10;", (red) -> {

                    int i = 1;

                    while (red.next()) {
                        final String name = red.getString("name");
                        final int coin = red.getInt("yen");
                        final int level = red.getInt("level");
                        final Entry bXHE = new Entry();
                        bXHE.nXH = new long[2];
                        bXHE.name = name;
                        bXHE.index = i;
                        bXHE.nXH[0] = coin;
                        bXHE.nXH[1] = level;
                        bxh.add(bXHE);
                        ++i;
                    }
                    red.close();

                });


                break;
            }
            case 1: {

                SQLManager.executeQuery("SELECT `name`,`exp`,`level`,`timeUpLevel` FROM `ninja` WHERE (`exp` > 0  AND `timeUpLevel` != 'NULL') ORDER BY `exp` DESC LIMIT 10;", (red) -> {
                    int i = 1;
                    while (red.next()) {
                        final String name = red.getString("name");
                        final long exp = red.getLong("exp");
                        final int level2 = red.getInt("level");
                        final Date timeUpLevel = util.getDate(red.getString("timeUpLevel"));
                        final Entry bXHE2 = new Entry();
                        bXHE2.nXH = new long[2];
                        bXHE2.name = name;
                        bXHE2.index = i;
                        bXHE2.nXH[0] = exp;
                        bXHE2.nXH[1] = level2;
                        bXHE2.timeUpLevel = timeUpLevel;
                        bxh.add(bXHE2);
                        ++i;
                    }
                    red.close();

                });

                break;
            }
            case 2: {

                SQLManager.executeQuery("SELECT `name`,`level` FROM `clan` WHERE (`level` > 0) ORDER BY `level` DESC LIMIT 10;", (red) -> {
                    int i = 1;
                    while (red.next()) {
                        final String name = red.getString("name");
                        final int level3 = red.getInt("level");
                        final Entry bXHE3 = new Entry();
                        bXHE3.nXH = new long[1];
                        bXHE3.name = name;
                        bXHE3.index = i;
                        bXHE3.nXH[0] = level3;
                        bxh.add(bXHE3);
                        ++i;
                    }
                    red.close();
                });
                break;
            }
            case 3: {
                SQLManager.executeQuery("SELECT `name`,`bagCaveMax`,`itemIDCaveMax` FROM `ninja` WHERE (`bagCaveMax` > 0) ORDER BY `bagCaveMax` DESC LIMIT 10;", (red) -> {
                    int i = 1;
                    while (red.next()) {
                        final String name = red.getString("name");
                        final int cave = red.getInt("bagCaveMax");
                        final short id = red.getShort("itemIDCaveMax");
                        final Entry bXHE = new Entry();
                        bXHE.nXH = new long[2];
                        bXHE.name = name;
                        bXHE.index = i;
                        bXHE.nXH[0] = cave;
                        bXHE.nXH[1] = id;
                        bxh.add(bXHE);
                        ++i;
                    }
                    red.close();
                });
                break;
            }
            case 4: {
                SQLManager.executeQuery("SELECT `name`,`event1_pointCayThongNoel` FROM `ninja` WHERE (`event1_pointCayThongNoel` > 0) ORDER BY `event1_pointCayThongNoel` DESC LIMIT 10;", (red) -> {
                    int i = 1;
                    while (red.next()) {
                        final String name = red.getString("name");
                        final int point = red.getInt("event1_pointCayThongNoel");
                        final Entry bXHE4 = new Entry();
                        bXHE4.nXH = new long[1];
                        bXHE4.name = name;
                        bXHE4.index = i;
                        bXHE4.nXH[0] = point;
                        bxh.add(bXHE4);
                        ++i;
                    }
                    red.close();
                });
                break;
            }
            case 5: {
                SQLManager.executeQuery("SELECT `name`,`event1_pointBossTuanLoc` FROM `ninja` WHERE (`event1_pointBossTuanLoc` > 0) ORDER BY `event1_pointBossTuanLoc` DESC LIMIT 10;", (red) -> {
                    int i = 1;
                    while (red.next()) {
                        final String name = red.getString("name");
                        final int point = red.getInt("event1_pointBossTuanLoc");
                        final Entry bXHE5 = new Entry();
                        bXHE5.nXH = new long[1];
                        bXHE5.name = name;
                        bXHE5.index = i;
                        bXHE5.nXH[0] = point;
                        bxh.add(bXHE5);
                        ++i;
                    }
                    red.close();
                });
                break;
            }
            case 6: {
                SQLManager.executeQuery("SELECT `name`,`topthiep` FROM `ninja` WHERE (`topthiep` > 0) ORDER BY `topthiep` DESC LIMIT 10;", (red) -> {
                    int i = 1;
                    while (red.next()) {
                        final String name = red.getString("name");
                        final int point = red.getInt("topthiep");
                        final Entry bXHE5 = new Entry();
                        bXHE5.nXH = new long[1];
                        bXHE5.name = name;
                        bXHE5.index = i;
                        bXHE5.nXH[0] = point;
                        bxh.add(bXHE5);
                        ++i;
                    }
                    red.close();
                });
                break;
            }
            case 7: {
                SQLManager.executeQuery("SELECT `name`,`nap` FROM `ninja` WHERE (`nap` > 0) ORDER BY `nap` DESC LIMIT 10;", (red) -> {
                    int i = 1;
                    while (red.next()) {
                        final String name = red.getString("name");
                        final int point = red.getInt("nap");
                        final Entry bXHE5 = new Entry();
                        bXHE5.nXH = new long[1];
                        bXHE5.name = name;
                        bXHE5.index = i;
                        bXHE5.nXH[0] = point;
                        bxh.add(bXHE5);
                        ++i;
                    }
                    red.close();
                });
                break;
            }
        }
    }

    public static Entry[] getBangXH(final int type) {
        final ArrayList<Entry> bxh = BXHManager.bangXH[type];
        final Entry[] bxhA = new Entry[bxh.size()];
        for (int i = 0; i < bxhA.length; ++i) {
            bxhA[i] = bxh.get(i);
        }
        return bxhA;
    }

    public static String getStringBXH(final int type) {
        String str = "";
        switch (type) {
            case 0: {
                if (BXHManager.bangXH[type].isEmpty()) {
                    str = "Chưa có thông tin";
                    break;
                }
                for (final Entry bxh : BXHManager.bangXH[type]) {
                    str = str + bxh.index + ". " + bxh.name + ": " + util.getFormatNumber(bxh.nXH[0]) + " yên - cấp: " + bxh.nXH[1] + "\n";
                }
                break;
            }
            case 1: {
                if (BXHManager.bangXH[type].isEmpty()) {
                    str = "Chưa có thông tin";
                    break;
                }
                for (final Entry bxh : BXHManager.bangXH[type]) {
                    str = str + bxh.index + ". " + bxh.name + ": đã đạt cấp: " + bxh.nXH[1] + "\n";
                }
                break;
            }
            case 2: {
                if (BXHManager.bangXH[type].isEmpty()) {
                    str = "Chưa có thông tin";
                    break;
                }
                for (final Entry bxh : BXHManager.bangXH[type]) {
                    final ClanManager clan = ClanManager.getClanByName(bxh.name);
                    if (clan != null) {
                        str = str + bxh.index + ". Gia tộc " + bxh.name + " trình độ cấp " + bxh.nXH[0] + " do " + clan.getmain_name() + " làm tộc trưởng, thành viên " + clan.members.size() + "/" + clan.getMemMax() + "\n";
                    } else {
                        str = str + bxh.index + ". Gia tộc " + bxh.name + " trình độ cấp " + bxh.nXH[0] + " đã bị giải tán\n";
                    }
                }
                break;
            }
            case 3: {
                if (BXHManager.bangXH[type].isEmpty()) {
                    str = "Chưa có thông tin";
                    break;
                }
                for (final Entry bxh : BXHManager.bangXH[type]) {
                    str = str + bxh.index + ". " + bxh.name + " nhận được " + util.getFormatNumber(bxh.nXH[0]) + " " + ItemData.ItemDataId((int) bxh.nXH[1]).name + "\n";
                }
                break;
            }
            case 4: {
                if (BXHManager.bangXH[type].isEmpty()) {
                    str = "Chưa có thông tin";
                    break;
                }
                for (final Entry bxh : BXHManager.bangXH[type]) {
                    str = str + bxh.index + ". " + bxh.name + "\n";
                }
                break;
            }
            case 5: {
                if (BXHManager.bangXH[type].isEmpty()) {
                    str = "Chưa có thông tin";
                    break;
                }
                for (final Entry bxh : BXHManager.bangXH[type]) {
                    str = str + bxh.index + ". " + bxh.name + "\n";
                }
                break;
            }
            case 6: {
                if (BXHManager.bangXH[type].isEmpty()) {
                    str = "Chưa có thông tin";
                    break;
                }
                for (final Entry bxh : BXHManager.bangXH[type]) {
                    str = str + bxh.index + ". " + bxh.name + " sử dụng " + util.getFormatNumber(bxh.nXH[0]) + " VPSK .\n";
                }
                break;
            }
            case 7: {
                if (BXHManager.bangXH[type].isEmpty()) {
                    str = "Chưa có thông tin";
                    break;
                }
                for (final Entry bxh : BXHManager.bangXH[type]) {
                    str = str + bxh.index + ". " + bxh.name + " đã nạp " + util.getFormatNumber(bxh.nXH[0]) + " VND .\n";
                }
                break;
            }
        }
        return str;
    }

    static {
        bangXH = new ArrayList[8];
        t = new Timer(true);
    }

    public static class Entry {
        int index;
        String name;
        long[] nXH;
        Date timeUpLevel;
    }
}
