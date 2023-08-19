package server;

import java.time.Instant;
import java.util.Date;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import real.Item;
import real.ItemData;
import real.Language;
import real.User;
import threading.Server;

public class GiftCode {

    public static Server server;

    public static void GiftCode(User p, String str) {
        String check = str.replaceAll("\\s+", "");
        if (check.equals("")) {
            p.session.sendMessageLog("Mã Gift code nhập vào không hợp lệ.");
            return;
        }
        check = check.toUpperCase();
        try {
            synchronized (Server.LOCK_MYSQL) {
                SQLManager.executeQuery("SELECT * FROM `gift_code` WHERE `code` LIKE '" + check + "';", (red) -> {
                    if (red != null && red.first()) {
                        int id = red.getInt("id");
                        String code = red.getString("code");
                        SQLManager.executeQuery("SELECT * FROM `history_gift` WHERE `player_id` = " + p.id + " AND `code` = '" + code + "';", (blue) -> {
                            JSONArray jar = (JSONArray) JSONValue.parse(red.getString("item_id"));
                            if (p.nj.getAvailableBag() < jar.size()) {
                                p.session.sendMessageLog(Language.NOT_ENOUGH_BAG);
                                return;
                            }
                            int j;
                            int[] itemId = new int[jar.size()];
                            for (j = 0; j < jar.size(); j++) {
                                itemId[j] = Integer.parseInt(jar.get(j).toString());
                            }
                            jar = (JSONArray) JSONValue.parse(red.getString("item_quantity"));
                            long[] itemQuantity = new long[jar.size()];
                            for (j = 0; j < jar.size(); j++) {
                                itemQuantity[j] = Long.parseLong(jar.get(j).toString());
                            }
                            jar = (JSONArray) JSONValue.parse(red.getString("item_isLock"));
                            byte[] itemIsLock = new byte[jar.size()];
                            for (j = 0; j < jar.size(); j++) {
                                itemIsLock[j] = Byte.parseByte(jar.get(j).toString());
                            }
                            jar = (JSONArray) JSONValue.parse(red.getString("item_expires"));
                            long[] itemExpires = new long[jar.size()];
                            for (j = 0; j < jar.size(); j++) {
                                itemExpires[j] = Long.parseLong(jar.get(j).toString());
                            }

                            int isPlayer = red.getInt("isPlayer");
                            int isTime = red.getInt("isTime");
                            int limitLevel = red.getInt("limitLevel");
                            int count = red.getInt("count");
                            if (isPlayer == 1) {
                                jar = (JSONArray) JSONValue.parse(red.getString("player"));
                                boolean checkUser = false;
                                for (j = 0; j < jar.size(); j++) {
                                    if (jar.get(j).toString().equals(p.username)) {
                                        checkUser = true;
                                        break;
                                    }
                                }
                                if (!checkUser) {
                                    p.session.sendMessageLog("Bạn không thể sử dụng mã Gift Code này.");
                                    red.close();
                                    return;
                                }
                            }

                            if (count < 1) {
                                p.session.sendMessageLog("Số lượng Giftcode này đã hết. Vui lòng nhập Giftcode khác!");
                                return;
                            }
                            if (p.nj.getLevel() > limitLevel) {
                                p.session.sendMessageLog("Giftcode này chỉ dành cho cấp độ từ " + limitLevel + " trở xuống.");
                                return;
                            }
                            if (isTime == 1) {
                                if (Date.from(Instant.now()).compareTo(util.getDate(red.getString("time"))) > 0) {
                                    p.session.sendMessageLog("Mã Gift code này đã hết hạn sử dụng.");
                                    red.close();
                                    return;
                                }
                            }
                            red.close();
                            if (blue != null && blue.first()) {
                                p.session.sendMessageLog("Bạn đã sử dụng mã Gift code này rồi.");
                            } else {
                                count--;
                                SQLManager.executeUpdate("UPDATE `gift_code` SET `count`=" + count + " WHERE `id`=" + id + " LIMIT 1;");
                                if (itemId.length == itemQuantity.length) {
                                    ItemData data2;
                                    int i;
                                    for (i = 0; i < itemId.length; i++) {
                                        if (itemId[i] == -3) {
                                            p.nj.upyenMessage(itemQuantity[i]);
                                        } else if (itemId[i] == -2) {
                                            p.nj.upxuMessage(itemQuantity[i]);
                                        } else if (itemId[i] == -1) {
                                            p.upluongMessage(itemQuantity[i]);
                                        } else {
                                            data2 = ItemData.ItemDataId(itemId[i]);
                                            if (data2 != null) {
                                                Item itemup;
                                                if (data2.type < 10) {
                                                    if (data2.type == 1) {
                                                        itemup = ItemData.itemDefault(itemId[i]);
                                                        itemup.sys = GameScr.SysClass(data2.nclass);
                                                    } else {
                                                        byte sys = (byte) util.nextInt(1, 3);
                                                        itemup = ItemData.itemDefault(itemId[i], sys);
                                                    }
                                                } else {
                                                    itemup = ItemData.itemDefault(itemId[i]);
                                                }
                                                itemup.quantity = (int) itemQuantity[i];
                                                if (itemIsLock[i] == 0) {
                                                    itemup.isLock = false;
                                                } else {
                                                    itemup.isLock = true;
                                                }
                                                if (itemExpires[i] != -1) {
                                                    itemup.isExpires = true;
                                                    itemup.expires = System.currentTimeMillis() + itemExpires[i];
                                                } else {
                                                    itemup.isExpires = false;
                                                }
                                                p.nj.addItemBag(true, itemup);
                                            }
                                        }
                                    }
                                    String sqlSET = "(" + p.id + ", '" + code + "', '" + util.toDateString(Date.from(Instant.now())) + "', '" + util.toDateString(Date.from(Instant.now())) + "', '" + util.toDateString(Date.from(Instant.now())) + "');";
                                    SQLManager.executeUpdate("INSERT INTO `history_gift` (`player_id`,`code`,`time`, `created_at`, `updated_at`) VALUES " + sqlSET);
                                    SQLManager.executeQuery("SELECT * FROM `gift_code` WHERE `code` LIKE '" + str + "';", (res) -> {
                                        String textTB;
                                        if (res != null && res.first()) {
                                            textTB = res.getString("thongBao");
                                            Service.sendTB(p, "Admin", textTB);
                                        }
                                    });
                                } else {
                                    p.session.sendMessageLog("Lỗi xác nhận mã Gift code. Hãy liên hệ Admin để biết thêm chi tiết.");
                                }
                            }
                            jar.clear();
                            blue.close();
                            return;
                        });
                    } else {
                        p.session.sendMessageLog("Mã Gift code này đã được sử dụng hoặc không tồn tại.");
                        red.close();
                        return;
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
