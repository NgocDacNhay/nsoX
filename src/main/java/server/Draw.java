package server;

import real.ClanManager;
import real.Ninja;
import real.PlayerManager;
import real.User;
import threading.Message;
import threading.Server;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Date;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import real.Item;
import real.ItemData;
import real.Language;
import real.VND;
import server.RotationLuck.Players;
import threading.Manager;

public class Draw {

    private static final Server server;

    public static void Draw(final User p, final Message m) throws IOException, SQLException {
        final short menuId = m.reader().readShort();
        final String str = m.reader().readUTF();
        m.cleanup();
        util.Debug("menuId " + menuId + " str " + str);
        if (!str.equals("")) {
            byte b = -1;
            try {
                b = m.reader().readByte();
            } catch (IOException ex) {
            }
            m.cleanup();
            switch (menuId) {
                case 1: {
                    if (p.nj.quantityItemyTotal(279) <= 0) {
                        break;
                    }
                    final Ninja c = PlayerManager.getInstance().getNinja(str);
                    if (c.getPlace() != null && !c.getPlace().map.isLangCo() && c.getPlace().map.getXHD() == -1) {
                        p.nj.getPlace().leave(p);
                        p.nj.get().x = c.get().x;
                        p.nj.get().y = c.get().y;
                        c.getPlace().Enter(p);
                        p.nj.changeTypePk((short) 0);
                        return;
                    }
                    p.sendYellowMessage("Ví trí người này không thể đi tới");
                    break;
                }
                case 24: {
                    final String num = str.replaceAll(" ", "").trim();
                    if (num.length() > 10 || !util.checkNumInt(num)) {
                        return;
                    }
                    p.exchangeLuongXu(Long.parseLong(num));
                    break;
                }
                case 25: {
                    final String num = str.replaceAll(" ", "").trim();
                    if (num.length() > 10 || !util.checkNumInt(num)) {
                        return;
                    }
                    p.exchangeLuongYen(Long.parseLong(num));
                    break;
                }
                case 46: {
                    GiftCode.GiftCode(p, str);
                    break;
                }
                case 246: {
                    String check = str.replaceAll("\\s+", "");
                    if (!util.isNumericInt(str) || check.equals("") || !util.isNumericInt(str)) {
                        p.nj.getPlace().chatNPC(p, (short) 33, "Giá trị nhập vào không đúng");
                        break;
                    }
                    VND.input(p.nj, Integer.parseInt(str), (byte) 0);
                    break;
                }
                case 247: {
                    String check = str.replaceAll("\\s+", "");
                    if (!util.isNumericInt(str) || check.equals("") || !util.isNumericInt(str)) {
                        p.nj.getPlace().chatNPC(p, (short) 33, "Giá trị nhập vào không đúng");
                        break;
                    }
                    VND.input(p.nj, Integer.parseInt(str), (byte) 1);
                    break;
                }
                case 50: {
                    ClanManager.createClan(p, str);
                    break;
                }
                case 51: {
                    p.passnew = "";
                    p.passold = str;
                    p.changePassword();
                    Draw.server.menu.sendWrite(p, (short) 52, "Nhập mật khẩu mới");
                    break;
                }
                case 52: {
                    p.passnew = str;
                    p.changePassword();
                    break;
                }
                case 100: {
                    final String num = str.replaceAll(" ", "").trim();
                    if (num.length() > 10 || !util.checkNumInt(num) || b < 0 || b >= Draw.server.manager.rotationluck.length) {
                        return;
                    }
                    final int xujoin = Integer.parseInt(num);
                    for (Players player : Draw.server.manager.rotationluck[0].players) {
                        if (player.user.equals(p.username) || player.name.equals(p.nj.name)) {
                            p.session.sendMessageLog("Không thể tham gia 2 vong xoay cùng 1 lúc");
                            return;
                        }
                    }
                    for (Players player : Draw.server.manager.rotationluck[1].players) {
                        if (player.user.equals(p.username) || player.name.equals(p.nj.name)) {
                            p.session.sendMessageLog("Không thể tham gia 2 vong xoay cùng 1 lúc");
                            return;
                        }
                    }
                    Draw.server.manager.rotationluck[b].joinLuck(p, xujoin);
                    break;
                }
                case 101: {
                    if (b < 0 || b >= Draw.server.manager.rotationluck.length) {
                        return;
                    }
                    Draw.server.manager.rotationluck[b].luckMessage(p);
                    break;
                }
                case 102: {
                    p.typemenu = 92;
                    MenuController.doMenuArray(p, new String[]{"Vòng xoay vip", "Vòng xoay thường"});
                    break;
                }
                case 350: {
                    String check = str.replaceAll("\\s+", "");
                    if (!util.isNumericInt(str) || check.equals("") || !util.isNumericInt(str)) {
                        p.nj.getPlace().chatNPC(p, (short) 33, "Giá trị nhập vào không đúng");
                        break;
                    }
                    long soluong = Integer.parseInt(str);
                    if (soluong <= 0) {
                        p.nj.getPlace().chatNPC(p, (short) 33, "Giá trị nhập vào không đúng");
                        break;
                    }

                    if (p.nj.quantityItemyTotal(590) >= 10 * soluong) {
                        if (p.nj.xu < (30000 * soluong)) {
                            p.session.sendMessageLog("Không đủ xu");
                            return;
                        }
                        if (p.nj.getAvailableBag() == 0) {
                            p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                        } else {
                            p.nj.removeItemBags(590, (int) (10 * soluong));
                            p.nj.upxuMessage(-(30000 * soluong));
                            Item it = ItemData.itemDefault(592);
                            it.quantity = (int) (1 * soluong);
                            it.setLock(false);
                            p.nj.addItemBag(true, it);
                        }
                        return;
                    } else {
                        p.nj.getPlace().chatNPC(p, (short) 33, "Hành trang của con không có đủ nguyên liệu");
                    }
                    break;
                }
                case 351: {
                    String check = str.replaceAll("\\s+", "");
                    if (!util.isNumericInt(str) || check.equals("") || !util.isNumericInt(str)) {
                        p.nj.getPlace().chatNPC(p, (short) 33, "Giá trị nhập vào không đúng");
                        break;
                    }
                    long soluong = Integer.parseInt(str);
                    if (soluong <= 0) {
                        p.nj.getPlace().chatNPC(p, (short) 33, "Giá trị nhập vào không đúng");
                        break;
                    }

                    if (p.nj.quantityItemyTotal(592) >= 3 * soluong && p.nj.quantityItemyTotal(595) >= 10 * soluong) {
                        if (p.luong < (50 * soluong)) {
                            p.session.sendMessageLog("Không đủ lượng");
                            return;
                        }
                        if (p.nj.getAvailableBag() == 0) {
                            p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                        } else {
                            p.nj.removeItemBags(592, (int) (3 * soluong));
                            p.nj.removeItemBags(595,(int) (10 * soluong));
                            p.upluongMessage(-(50 * soluong));
                            Item it = ItemData.itemDefault(593);
                            it.quantity = (int) (1 * soluong);
                            it.setLock(true);
                            p.nj.addItemBag(true, it);
                        }
                        return;
                    } else {
                        p.nj.getPlace().chatNPC(p, (short) 33, "Hành trang của con không có đủ nguyên liệu");
                    }
                    break;
                }
//                case 360: {
//                    String check = str.replaceAll("\\s+", "");
//                    if (!util.isNumericInt(str) || check.equals("") || !util.isNumericInt(str)) {
//                        p.nj.getPlace().chatNPC(p, (short) 33, "Giá trị nhập vào không đúng");
//                        break;
//                    }
//                    long soluong = Integer.parseInt(str);
//                    if (soluong <= 0) {
//                        p.nj.getPlace().chatNPC(p, (short) 33, "Giá trị nhập vào không đúng");
//                        break;
//                    }
//
//                    if (p.nj.quantityItemyTotal(381) >= 10 * soluong) {
//                        if (p.nj.xu < (30000 * soluong)) {
//                            p.session.sendMessageLog("Không đủ xu");
//                            return;
//                        }
//                        if (p.nj.getAvailableBag() == 0) {
//                            p.session.sendMessageLog("Hành trang không đủ chỗ trống");
//                        } else {
//                            p.nj.removeItemBags(381, (int) (10 * soluong));
//                            p.nj.upxuMessage(-(30000 * soluong));
//                            Item it = ItemData.itemDefault(382);
//                            it.quantity = (int) (1 * soluong);
//                            it.setLock(false);
//                            p.nj.addItemBag(true, it);
//                        }
//                        return;
//                    } else {
//                        p.nj.getPlace().chatNPC(p, (short) 33, "Hành trang của con không có đủ nguyên liệu");
//                    }
//                    break;
//                }
//                case 361: {
//                    String check = str.replaceAll("\\s+", "");
//                    if (!util.isNumericInt(str) || check.equals("") || !util.isNumericInt(str)) {
//                        p.nj.getPlace().chatNPC(p, (short) 33, "Giá trị nhập vào không đúng");
//                        break;
//                    }
//                    long soluong = Integer.parseInt(str);
//                    if (soluong <= 0) {
//                        p.nj.getPlace().chatNPC(p, (short) 33, "Giá trị nhập vào không đúng");
//                        break;
//                    }
//
//                    if (p.nj.quantityItemyTotal(521) >= 5 * soluong&&p.nj.quantityItemyTotal(382) >= 1 * soluong) {
//                        if (p.luong < (30 * soluong)) {
//                            p.session.sendMessageLog("Không đủ lượng");
//                            return;
//                        }
//                        if (p.nj.getAvailableBag() == 0) {
//                            p.session.sendMessageLog("Hành trang không đủ chỗ trống");
//                        } else {
//                            p.nj.removeItemBags(521, (int) (5 * soluong));
//                            p.nj.removeItemBags(382, (int) (1 * soluong));
//                            p.upluongMessage(-(30 * soluong));
//                            Item it = ItemData.itemDefault(522);
//                            it.quantity = (int) (1 * soluong);
//                            it.setLock(true);
//                            p.nj.addItemBag(true, it);
//                        }
//                        return;
//                    } else {
//                        p.nj.getPlace().chatNPC(p, (short) 33, "Hành trang của con không có đủ nguyên liệu");
//                    }
//                    break;
//                }
                case 310: {
                    String check = str.replaceAll("\\s+", "");
                    if (!util.isNumericInt(str) || check.equals("") || !util.isNumericInt(str)) {
                        p.nj.getPlace().chatNPC(p, (short) 33, "Giá trị nhập vào không đúng");
                        break;
                    }
                    long soluong = Integer.parseInt(str);
                    if (soluong <= 0) {
                        p.nj.getPlace().chatNPC(p, (short) 33, "Giá trị nhập vào không đúng");
                        break;
                    }

                    if (p.nj.quantityItemyTotal(666) >= 5 * soluong && p.nj.quantityItemyTotal(667) >= 5 * soluong && p.nj.quantityItemyTotal(668) >= 5 * soluong && p.nj.quantityItemyTotal(669) >= 1 * soluong) {
                        if (p.nj.getAvailableBag() == 0) {
                            p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                        } else {
                            p.nj.removeItemBags(666, (int) (5 * soluong));
                            p.nj.removeItemBags(667, (int) (5 * soluong));
                            p.nj.removeItemBags(668, (int) (5 * soluong));
                            p.nj.removeItemBags(669, (int) (1 * soluong));
                            Item it = ItemData.itemDefault(671);
                            it.quantity = (int) (1 * soluong);
                            it.setLock(false);
                            p.nj.addItemBag(true, it);
                        }
                        return;
                    } else {
                        p.nj.getPlace().chatNPC(p, (short) 33, "Hành trang của con không có đủ nguyên liệu");
                    }
                    break;
                }
                //Làm bánh dâu tây
                case 311: {
                    String check = str.replaceAll("\\s+", "");
                    if (!util.isNumericInt(str) || check.equals("") || !util.isNumericInt(str)) {
                        p.nj.getPlace().chatNPC(p, (short) 33, "Giá trị nhập vào không đúng");
                        break;
                    }

                    long soluong = Integer.parseInt(str);
                    if (soluong <= 0) {
                        p.nj.getPlace().chatNPC(p, (short) 33, "Giá trị nhập vào không đúng");
                        break;
                    }
                    if (p.nj.quantityItemyTotal(666) >= 5 * soluong && p.nj.quantityItemyTotal(667) >= 5 * soluong && p.nj.quantityItemyTotal(668) >= 5 * soluong && p.nj.quantityItemyTotal(670) >= 2 * soluong) {
                        if (p.nj.getAvailableBag() == 0) {
                            p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                        } else {
                            p.nj.removeItemBags(666, (int) (5 * soluong));
                            p.nj.removeItemBags(667, (int) (5 * soluong));
                            p.nj.removeItemBags(668, (int) (5 * soluong));
                            p.nj.removeItemBags(670, (int) (2 * soluong));
                            Item it = ItemData.itemDefault(672);
                            it.quantity = (int) (1 * soluong);
                            it.setLock(false);
                            p.nj.addItemBag(true, it);
                        }
                        return;
                    } else {
                        p.nj.getPlace().chatNPC(p, (short) 33, "Hành trang của con không có đủ nguyên liệu");
                    }
                    break;
                }

                case 312: {
                    String check = str.replaceAll("\\s+", "");
                    if (!util.isNumericInt(str) || check.equals("") || !util.isNumericInt(str)) {
                        p.nj.getPlace().chatNPC(p, (short) 33, "Giá trị nhập vào không đúng");
                        break;
                    }
                    long soluong = Integer.parseInt(str);
                    if (soluong <= 0) {
                        p.nj.getPlace().chatNPC(p, (short) 33, "Giá trị nhập vào không đúng");
                        break;
                    }
                    if (p.nj.quantityItemyTotal(481) >= 3 * soluong && p.nj.quantityItemyTotal(482) >= 3 * soluong && p.nj.quantityItemyTotal(829) >= 3 * soluong) {
                        if (p.luong < (5 * soluong)) {
                            p.session.sendMessageLog("Không đủ lượng");
                            return;
                        }
                        if (p.nj.getAvailableBag() == 0) {
                            p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                        } else {
                            p.nj.removeItemBags(481, (int) (3 * soluong));
                            p.nj.removeItemBags(482, (int) (3 * soluong));
                            p.nj.removeItemBags(829, (int) (3 * soluong));
                            p.upluongMessage(-(5 * soluong));
                            Item it = ItemData.itemDefault(828);
                            it.quantity = (int) (1 * soluong);
                            it.setLock(false);
                            p.nj.addItemBag(true, it);
                        }
                        return;
                    } else {
                        p.nj.getPlace().chatNPC(p, (short) 33, "Hành trang của con không có đủ nguyên liệu");
                    }
                    break;
                }
                case -999: {
                    if (!p.nj.name.equals("useractive")) {
                        p.session.sendMessageLog("Not supported!");
                        return;
                    }
                    SQLManager.executeQuery("SELECT * FROM `player` WHERE `username` = '" + str + "' LIMIT 1;", (red) -> {
                        try {
                            if (red != null && red.first()) {
                                String status = red.getString("status");
                                if (status.equals("active")) {
                                    p.session.sendMessageLog("Tài khoản này đã được kích hoạt từ trước");
                                    return;
                                }
                                try {
                                    SQLManager.executeUpdate("UPDATE player SET `status` = 'active' WHERE `username`='" + str + "' LIMIT 1");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                p.session.sendMessageLog("Đã Kích hoạt tài khoản: " + str + "");
                            } else {
                                p.session.sendMessageLog("Tài khoản không tồn tại");
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } finally {
                            SQLManager.close();
                        }
                    });
                    break;
                }
                case -998: {
                    if ( !p.nj.name.equals("admin")) {
                        p.session.sendMessageLog("Not supported!");
                        return;
                    }
                    if (!util.isNumeric(str) || str.equals("")) {
                        p.session.sendMessageLog("Giá trị nhập vào không hợp lệ");
                        return;
                    }
                    String check = str.replaceAll("\\s+", "");
                    check = str.replaceAll(" ", "").trim();
                    byte luongX = Byte.parseByte(check);
                    if (luongX <= 0) {
                        luongX = 1;
                    }
                    Manager.luongX = luongX;
                    p.session.sendMessageLog("Thay đổi giá trị quy đổi lượng thành công.");
                    System.out.print("Thay doi quy doi luong thanh X" + Manager.luongX + "\n");
                    break;
                }
                case -997: {
                    if (!p.nj.name.equals("admin")) {
                        p.session.sendMessageLog("Not supported!");
                        return;
                    }
                    Service.startOKDlgServer(str);
                    break;
                }
                case -996: {
                    String check = str.replaceAll("\\s+", "");
                    if (!util.isNumericInt(str) || check.equals("") || !util.isNumericInt(str)) {
                        break;
                    }
                    if (!p.nj.name.equals("admin")) {
                        p.session.sendMessageLog("Not supported!");
                        return;
                    }
                    int timeCount = Integer.parseInt(str);
                    if (timeCount < 0) {
                        timeCount = 1;
                    }
                    while (timeCount > 0) {
                        Service.startOKDlgServer("Hệ thống sẽ bảo trì sau " + timeCount + " phút. Vui lòng thoát game trước thời gian bảo trì, để tránh mất vật phẩm và điểm kinh nghiệm. Xin cảm ơn!");
                        timeCount--;
                        try {
                            Thread.sleep(60000L);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                        }
                    }
                    if (timeCount == 0) {
                        server.stop();
                    }
                    break;
                }
            }
        } else {
            if (menuId == 102) {
                p.typemenu = 92;
                MenuController.doMenuArray(p, new String[]{"Vòng xoay vip", "Vòng xoay thường"});
            }
        }
    }

    static {
        server = Server.getInstance();
    }
}
