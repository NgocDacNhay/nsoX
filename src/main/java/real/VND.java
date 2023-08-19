package real;

import java.sql.SQLException;
import java.time.Instant;
import java.util.Date;
import server.GameCanvas;
import server.SQLManager;
import server.util;
import threading.Manager;
import threading.Server;

public class VND {

    public static void input(final Ninja _char, final int num, final byte type) {
        switch (type) {
            case 0: {
                SQLManager.executeQuery("SELECT `VND` FROM `player` WHERE `id` = " + _char.p.id + " LIMIT 1;", (red) -> {
                    if (num > 0) {
                        try {
                            try {
                                if (red.first()) {
                                    long ngoc = red.getLong("VND");
                                    if (num > ngoc) {
                                        GameCanvas.startOKDlg(_char.p.session, "Không đủ");
                                    } else {
                                        SQLManager.executeUpdate("UPDATE `player` SET `VND` = `VND` - " + num + " WHERE `id` = " + _char.p.id + ";");
                                        final int pre_gold = _char.p.luong;
                                        final int pre_xu = _char.xu;
                                        final int pre_yen = _char.yen;
                                        final long pre_ngoc = ngoc;
                                        final long nums = (long) (num * Manager.luongX);
                                        ngoc -= num;
                                        _char.p.Ngoc = (int) ngoc;
                                        GameCanvas.startOKDlg(_char.p.session, "Quy đổi thành công " + num + " ngọc thành " + nums + " lượng.");
                                        _char.p.upluongMessage(nums);
                                        _char.p.flush();
                                        SQLManager.executeUpdate("INSERT INTO transfer(`userId`,`VNDtruoc`,`VNDsau`,`luongtruoc`,`luongsau`,`xutruoc`,`xusau`,`yentruoc`,`yensau`,`time`,`created_at`) VALUES (" + _char.p.id + "," + pre_ngoc + "," + ngoc + "," + pre_gold + "," + _char.p.luong + "," + pre_xu + "," + _char.xu + "," + pre_yen + "," + _char.yen + "," + System.currentTimeMillis() / 1000L + ",'" + util.toDateString(Date.from(Instant.now())) + "');");
                                    }
                                }
                            } finally {
                                SQLManager.close();
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                });
                break;
            }
            case 1: {
                SQLManager.executeQuery("SELECT `VND` FROM `player` WHERE `id` = " + _char.p.id + " LIMIT 1;", (red) -> {
                    if (num > 0) {
                        try {
                            try {
                                if (red.first()) {
                                    long ngoc = red.getLong("VND");
                                    if (num > ngoc) {
                                        GameCanvas.startOKDlg(_char.p.session, "Không đủ");
                                    } else {
                                        SQLManager.executeUpdate("UPDATE `player` SET `VND` = `VND` - " + num + " WHERE `id` = " + _char.p.id + ";");
                                        final int pre_gold = _char.p.luong;
                                        final int pre_xu = _char.xu;
                                        final int pre_yen = _char.yen;
                                        final long pre_ngoc = ngoc;
                                        final long nums = (long) (num * 1500);
                                        ngoc -= num;
                                        _char.p.Ngoc = (int) ngoc;
                                        GameCanvas.startOKDlg(_char.p.session, "Quy đổi thành công " + num + " ngọc thành " + nums + " xu.");
                                        _char.upXuMessage(nums);
                                        _char.p.flush();
                                        SQLManager.executeUpdate("INSERT INTO transfer(`userId`,`VNDtruoc`,`VNDsau`,`luongtruoc`,`luongsau`,`xutruoc`,`xusau`,`yentruoc`,`yensau`,`time`,`created_at`) VALUES (" + _char.p.id + "," + pre_ngoc + "," + ngoc + "," + pre_gold + "," + _char.p.luong + "," + pre_xu + "," + _char.xu + "," + pre_yen + "," + _char.yen + "," + System.currentTimeMillis() / 1000L + ",'" + util.toDateString(Date.from(Instant.now())) + "');");
                                    }
                                }
                            } finally {
                                SQLManager.close();
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                });
                break;
            }
        }
    }

    public static void view(final Ninja _char) {
        SQLManager.executeQuery("SELECT `VND` FROM `player` WHERE `id` = " + _char.p.id + " LIMIT 1;", (red) -> {
            try {
                try {
                    if (red.first()) {
                        final long ngoc = red.getLong("VND");
                        GameCanvas.startOKDlg(_char.p.session, "Đang có " + ngoc + " ngọc trong tài khoản");
                    }
                } finally {
                    SQLManager.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
