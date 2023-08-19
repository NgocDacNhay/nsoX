package real;

import boardGame.Place;
import lombok.val;
import patch.EventItem;
import patch.clan.ClanThanThu;
import server.*;
import tasks.TaskHandle;
import tasks.Text;
import threading.Map;
import threading.Message;
import threading.Server;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import threading.Manager;

import static threading.Manager.*;

public class useItem {
    public static final int _1_DAY = 86400;
    public static final int _1HOUR = 3600000;
    static Server server;
    static final int[] arrOp;
    static final int[] arrParam;
    private static final byte[] arrOpenBag;
    public static final int _10_MINS = 10 * 60 * 1000;
    
    static int[] OpIdMatNaNew = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 87, 57, 58};
    static int[] ParramOpMatNaNew = new int[]{util.nextInt(250, 399), util.nextInt(250, 399), util.nextInt(100, 140), util.nextInt(100, 140), util.nextInt(100, 140)
            , util.nextInt(30, 50), util.nextInt(500, 1200), util.nextInt(500, 1200), util.nextInt(80, 199), util.nextInt(80, 199), util.nextInt(2800, 3500), util.nextInt(60, 120), util.nextInt(20, 35)};

    //Event
    private static short[] idItemBanhChocolate = new short[]{275,276,277,278,804,805,799,800,568,569,570,571,574,574,573,573,436,436,436,436,436,436,437,437,437,438,737,737,738,738,739,739,741,741,764,764,765,765,766,766,768,768,523,9,10,409,410,419,775,775,775,775,775,695,696,697,698,699,701,702,703,704};
    private static short[] idItemBanhDauTay = new short[]{4, 4, 4, 5, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 7, 7, 7, 7, 275, 276, 277, 278, 289, 340, 340, 409, 410, 419, 436, 436, 436, 5, 5, 5, 5, 6, 6, 6, 6, 7, 7, 7, 567, 567, 775, 569, 575, 5, 5, 5, 6, 6, 6, 6, 828, 775, 695, 696, 697, 698, 699, 701, 702, 703, 704};
    
    static {
        useItem.server = Server.getInstance();
        arrOp = new int[]{6, 7, 10, 67, 68, 69, 70, 71, 72, 73, 74};
        arrParam = new int[]{50, 50, 10, 5, 10, 10, 5, 5, 5, 100, 50};
        arrOpenBag = new byte[]{0, 6, 6, 12, 6};
    }

    public static void uesItem(final User p, final Item item, final byte index) throws IOException, InterruptedException {
        if (ItemData.ItemDataId(item.id).level > p.nj.get().getLevel()) {
            return;
        }
        if (item == null) {
            GameCanvas.startOKDlg(p.session, "Bug con cặc");
            return;
        }
        final ItemData data = ItemData.ItemDataId(item.id);
        if (data.gender != 2 && data.gender != p.nj.gender) {
            return;
        }
        if (data.type == 26) {
            p.sendYellowMessage("Vật phẩm liên quan đến nâng cấp, hãy gặp Kenshinto trong làng để sử dụng.");
            return;
        }

        if (item.id != 194) {
            if ((p.nj.get().nclass == 0 && item.id == 547) || item.id != 400 && (data.nclass > 0 && data.nclass != p.nj.get().nclass)) {
                p.sendYellowMessage("Môn phái không phù hợp");
                return;
            }
        }

        // TODO
        if (p.nj.isNhanban && item.id == 547) {
            p.sendYellowMessage("Chức năng này không thể sử dụng cho phân thân");
            return;
        }
        if (p.nj.isNhanban) {
            if (p.nj.get().nclass != 1 && p.nj.get().nclass != 2 && item.id == 420) {
                p.sendYellowMessage("Chỉ hoả hệ mới có thể dùng Faiyaa Yoroi");
                return;
            }
            if (p.nj.get().nclass != 3 && p.nj.get().nclass != 4 && item.id == 421) {
                p.sendYellowMessage("Chỉ băng hệ mới có thể dùng Mizu Yoroi");
                return;
            }
            if (p.nj.get().nclass != 5 && p.nj.get().nclass != 6 && item.id == 422) {
                p.sendYellowMessage("Chỉ phong hệ mới có thể dùng Windo Yoroi");
                return;
            }
        }
        if (ItemData.isTypeBody(item.id)) {
            item.setLock(true);
            Item itemb = null;
            if (item.id == 795 || item.id == 796 || item.id == 799 || item.id == 800 || item.id == 804 || item.id == 805 || item.id == 813 || item.id == 814 || item.id == 815 || item.id == 816 || item.id == 817 || item.id == 825 || item.id == 826 || item.id == 830|| item.id == 891|| item.id == 895 || item.id == 896|| item.id == 903 || item.id == 904 || item.id == 905 || item.id == 909) {
                itemb = p.nj.get().ItemBody[data.type+16];
                p.nj.ItemBag[index] = itemb;
                if (itemb != null) {
                    if (p.nj.isNhanban) {
                        ThoiTrang.removeThoiTrangPT(p.nj.clone, itemb.id);
                    } else if (p.nj.isHuman) {
                        ThoiTrang.removeThoiTrang(p.nj, itemb.id);
                    }
                }
                p.nj.get().ItemBody[data.type+16] = item;
                if ((item.id == 813 || item.id == 814 || item.id == 815 || item.id == 816 || item.id == 817) && !item.isLock) {
                    byte i;
                    int op;
                    Option option2;
                    for (i = 0; i < util.nextInt(1, 7); ++i) {
                        op = -1;
                        do {
                            op = util.nextInt(useItem.OpIdMatNaNew.length);
                            for (Option option : item.option) {
                                if (useItem.OpIdMatNaNew[op] == option.id) {
                                    op = -1;
                                    break;
                                }
                            }
                        } while (op == -1);
                        if (op == -1) {
                            return;
                        }
                        int par = useItem.ParramOpMatNaNew[op];
                        option2 = new Option(useItem.OpIdMatNaNew[op], par);
                        item.option.add(option2);
                    }
                }
                if(p.nj.isNhanban) {
                    ThoiTrang.setThoiTrangPT(p.nj.clone, item.id);
                } else if (p.nj.isHuman) {
                    ThoiTrang.setThoiTrang(p.nj, item.id);
                }
            } else {
                itemb = p.nj.get().ItemBody[data.type];
                p.nj.ItemBag[index] = itemb;
                p.nj.get().ItemBody[data.type] = item;
            }

            if (data.type == 10) {
                p.mobMeMessage(0, (byte) 0);
            }
            if (itemb != null && itemb.id == 568) {
                p.removeEffect(38);
            }
            if (itemb != null && itemb.id == 569) {
                p.removeEffect(36);
            }
            if (itemb != null && itemb.id == 570) {
                p.removeEffect(37);
            }
            if (itemb != null && itemb.id == 571) {
                p.removeEffect(39);
            }
            if (itemb != null && itemb.id == 772) {
                p.removeEffect(42);
            }

            switch (item.id) {
                case 246: {
                    p.mobMeMessage(70, (byte) 0);
                    break;
                }
                case 419: {
                    p.mobMeMessage(122, (byte) 0);
                    break;
                }
                case 568: {
                    p.setEffect(38, 0, (int) (item.expires - System.currentTimeMillis()), p.nj.get().getPramItem(100));
                    p.mobMeMessage(205, (byte) 0);
                    break;
                }
                case 569: {
                    p.setEffect(36, 0, (int) (item.expires - System.currentTimeMillis()), p.nj.get().getPramItem(99));
                    p.mobMeMessage(206, (byte) 0);
                    break;
                }
                case 570: {
                    p.setEffect(37, 0, (int) (item.expires - System.currentTimeMillis()), p.nj.get().getPramItem(98));
                    p.mobMeMessage(207, (byte) 0);
                    break;
                }
                case 571: {
                    p.setEffect(39, 0, (int) (item.expires - System.currentTimeMillis()), p.nj.get().getPramItem(101));
                    p.mobMeMessage(208, (byte) 0);
                    break;
                }
                case 583: {
                    p.mobMeMessage(211, (byte) 1);
                    break;
                }
                case 584: {
                    p.mobMeMessage(212, (byte) 1);
                    break;
                }
                case 585: {
                    p.mobMeMessage(213, (byte) 1);
                    break;
                }
                case 586: {
                    p.mobMeMessage(214, (byte) 1);
                    break;
                }
                case 587: {
                    p.mobMeMessage(215, (byte) 1);
                    break;
                }
                case 588: {
                    p.mobMeMessage(216, (byte) 1);
                    break;
                }
                case 589: {
                    p.mobMeMessage(217, (byte) 1);
                    break;
                }
                case 742: {
                    p.mobMeMessage(229, (byte) 1);
                    break;
                }
                case 772: {
                    p.setEffect(42, 0, (int) (item.expires - System.currentTimeMillis()), 400);
                    p.mobMeMessage(234, (byte) 1);
                    break;
                }
                case 781: {
                    p.mobMeMessage(235, (byte) 1);
                    break;
                }
                case 832: {
                    p.mobMeMessage(238, (byte) 1);
                    break;
                }
            }
        } else if (ItemData.isTypeMounts(item.id)) {
            final byte idM = (byte) (data.type - 29);
            final Item itemM = p.nj.get().ItemMounts[idM];
            if (idM == 4) {
                if (p.nj.get().ItemMounts[0] != null || p.nj.get().ItemMounts[1] != null || p.nj.get().ItemMounts[2] != null || p.nj.get().ItemMounts[3] != null) {
                    p.session.sendMessageLog("Bạn cần phải tháo trang bị thú cưới đang sử dụng");
                    return;
                }
                if (!item.isLock()) {

                    for (byte i = 0; i < 4; ++i) {
                        int attemp = 400;
                        int optionId = -1;
                        do {
                            optionId = util.nextInt(useItem.arrOp.length);
                            for (final Option option : item.option) {
                                if (useItem.arrOp[optionId] == option.id) {
                                    optionId = -1;
                                    break;
                                }
                            }
                            attemp--;
                            if (attemp <= 0) {
                                if (optionId == -1) {
                                    optionId = Arrays.stream(useItem.arrOp)
                                            .filter(id -> item.option.stream().noneMatch(o -> o.id == id))
                                            .findFirst().orElse(-1);
                                }
                                break;
                            }
                        } while (optionId == -1);
                        if (optionId == -1) {
                            return;
                        }
                        final int idOp = useItem.arrOp[optionId];
                        int par = useItem.arrParam[optionId];
                        // Soi den
                        if (item.isExpires || item.id == 523) {
                            par *= 10;
                        }
                        final Option option2 = new Option(idOp, par);
                        item.option.add(option2);
                    }
                 /*   if (item.id == 801) {//Xích tử mã
                        Option option3 = new Option(130, 20);//Kháng st hệ băng
                        item.option.add(option3);
                    } else if (item.id == 802) {//Tà linh mã
                        Option option3 = new Option(131, 20);//Kháng st hệ phong
                        item.option.add(option3);
                    } else if (item.id == 803) {//Phong Thương Mã
                        Option option3 = new Option(127, 20);//Kháng st hệ hoả
                        item.option.add(option3);
                    } else if (item.id == 827) {//Phượng Hoàng Băng
                        Option option3 = new Option(134, 3);
                        item.option.add(option3);
                        option3 = new Option(135, 3);
                        item.option.add(option3);
                    } else if (item.id == 831) {//Bạch hổ
                        Option option3 = new Option(58, 20);
                        item.option.add(option3);
                        option3 = new Option(94, 15);
                        item.option.add(option3);
                    } */
                }
            } else if (p.nj.get().ItemMounts[4] == null) {
                p.session.sendMessageLog("Bạn cần có thú cưới để sử dụng");
                return;
            }
            item.setLock(true);
            p.nj.ItemBag[index] = itemM;
            if (itemM != null) {
                if (p.nj.isNhanban) {
                    ThoiTrang.removeThoiTrangPT(p.nj.clone, itemM.id);
                } else if (p.nj.isHuman) {
                    ThoiTrang.removeThoiTrang(p.nj, itemM.id);
                }
                p.loadMounts();
            }
            p.nj.get().ItemMounts[idM] = item;
            if (p.nj.isNhanban) {
                ThoiTrang.setThoiTrangPT(p.nj.clone, item.id);
            } else if (p.nj.isHuman) {
                ThoiTrang.setThoiTrang(p.nj, item.id);
            }
        }
        if (data.skill > 0) {
            byte skill = data.skill;
            if (item.id == 547) {
                skill += p.nj.get().nclass;
            }
            p.openBookSkill(index, skill);
            return;
        }
        final byte numbagnull = p.nj.getAvailableBag();
        switch (item.id) {
            case 13: {
                if (p.buffHP(25)) {
                    p.nj.removeItemBag(index, 1);
                }
                return;
            }
            case 14: {
                if (p.buffHP(90)) {
                    p.nj.removeItemBag(index, 1);
                }
                return;
            }
            case 15: {
                if (p.buffHP(230)) {
                    p.nj.removeItemBag(index, 1);
                }
                return;
            }
            case 16: {
                if (p.buffHP(400)) {
                    p.nj.removeItemBag(index, 1);
                }
                return;
            }
            case 17: {
                if (p.buffHP(650)) {
                    p.nj.removeItemBag(index, 1);
                }
                return;
            }
            case 565: {
                if (p.buffHP(1500)) {
                    p.nj.removeItemBag(index, 1);
                }
                return;
            }
            case 18: {
                if (p.buffMP(150)) {
                    p.nj.removeItemBag(index, 1);
                }
                return;
            }
            case 19: {
                if (p.buffMP(500)) {
                    p.nj.removeItemBag(index, 1);
                }
                return;
            }
            case 20: {
                if (p.buffMP(1000)) {
                    p.nj.removeItemBag(index, 1);
                }
                return;
            }
            case 21: {
                if (p.buffMP(2000)) {
                    p.nj.removeItemBag(index, 1);
                }
                return;
            }
            case 22: {
                if (p.buffMP(3500)) {
                    p.nj.removeItemBag(index, 1);
                }
                return;
            }
            case 566: {
                if (p.buffMP(5000)) {
                    p.nj.removeItemBag(index, 1);
                }
                return;
            }
            case 23: {
                if (p.dungThucan((byte) 0, 3, 1800)) {
                    p.nj.removeItemBag(index, 1);
                    break;
                }
                break;
            }
            case 24: {
                if (p.dungThucan((byte) 1, 20, 1800)) {
                    p.nj.removeItemBag(index, 1);
                    break;
                }
                break;
            }
            case 25: {
                if (p.dungThucan((byte) 2, 30, 1800)) {
                    p.nj.removeItemBag(index, 1);
                    break;
                }
                break;
            }
            case 26: {
                if (p.dungThucan((byte) 3, 40, 1800)) {
                    p.nj.removeItemBag(index, 1);
                    break;
                }
                break;
            }
            case 249: {//sashimi 3 ngày
                if (p.dungThucan((byte)3, 40, 259200)) {
                    p.nj.removeItemBag(index, 1);
                    break;
                }
                break;
            }
            case 27: {
                if (p.dungThucan((byte) 4, 50, 1800)) {
                    p.nj.removeItemBag(index, 1);
                    break;
                }
                break;
            }
           
            case 250: {//gà quay 3 ngày
                if (p.dungThucan((byte)4, 50, 259200)) {
                    p.nj.removeItemBag(index, 1);
                    break;
                }
                break;
            }
            case 29: {
                if (p.dungThucan((byte) 28, 60, 1800)) {
                    p.nj.removeItemBag(index, 1);
                    break;
                }
                break;
            }
            case 30: {
                if (p.dungThucan((byte) 28, 60, 259200)) {
                    p.nj.removeItemBag(index, 1);
                    break;
                }
                break;
            }
            case 409: {//gà tây ta 6x
                if (p.dungThucan((byte)30, 75, 86400)) {
                    p.nj.removeItemBag(index, 1);
                    break;
                }
                break;
            }
            case 410: {//tôm hùm ta 7x
                if (p.dungThucan((byte)31, 90, 86400)) {
                    p.nj.removeItemBag(index, 1);
                    break;
                }
                break;
            }
            case 567: {//haggis ta 9x
                if (p.dungThucan((byte)35, 120, 86400)) {
                    p.nj.removeItemBag(index, 1);
                    break;
                }
                break;
            }
            case 34:
            case 36: {
                final Map map = getMapid(p.nj.mapLTD);
                if (map != null) {
                    for (byte i = 0; i < map.area.length; ++i) {
                        if (map.area[i].getNumplayers() < map.template.maxplayers) {
                            p.nj.getPlace().leave(p);
                            map.area[i].EnterMap0(p.nj);
                            if (item.id == 34) {
                                p.nj.removeItemBag(index, 1);
                            }
                            return;
                        }
                    }
                    break;
                }
                break;
            }

            case 240: {//giấy tẩy tn
                p.nj.tayTN++;
                p.sendYellowMessage("Số lần tẩy tiềm năng của bạn tăng lên " + p.nj.tayTN + " lần");
                p.nj.removeItemBag(index, 1);
                break;
            }
            case 241: {//giấy tẩy kn
                p.nj.tayKN++;
                p.sendYellowMessage("Số lần tẩy kỹ năng của bạn tăng lên " + p.nj.tayKN + " lần");
                p.nj.removeItemBag(index, 1);
                break;
            }

            case 257: {
                if (p.nj.get().pk > 0) {
                    final Body value = p.nj.get();
                    value.pk -= 5;
                    if (p.nj.get().pk < 0) {
                        p.nj.get().pk = 0;
                    }
                    p.sendYellowMessage("Điểm hiếu chiến của bạn còn lại là " + p.nj.get().pk);
                    p.nj.removeItemBag(index, 1);
                    break;
                }
                p.sendYellowMessage("Bạn không có điểm hiếu chiến");
                break;
            }
            case 279: {
                server.menu.sendWrite(p, (short) 1, "Nhập tên nhân vật");
                break;
            }

            case 252: {
                if (p.nj.get().getKyNangSo() >= 3) {
                    p.nj.get().setKyNangSo(3);
                    p.session.sendMessageLog("Chỉ được học tối đa 3 quyển");
                } else if (p.nj.isHuman) {
                    p.nj.get().setKyNangSo(p.nj.getKyNangSo() + 1);
                    p.nj.removeItemBag(index, 1);
                    p.nj.get().setSpoint(p.nj.getSpoint() + 1);
                    p.sendYellowMessage("Bạn nhận được 1 điểm kỹ năng");
                    p.loadSkill();
                } else if (p.nj.isNhanban && p.nj.clone != null) {
                    p.nj.get().setKyNangSo(p.nj.clone.getKyNangSo() + 1);
                    p.nj.removeItemBag(index, 1);
                    p.nj.get().setSpoint(p.nj.getSpoint() + 1);
                    p.sendYellowMessage("Bạn nhận được 1 điểm kỹ năng");
                    p.loadSkill();
                }


                break;
            }

            case 253: {
                // Hoc sach tiem nang TODO
                if (p.nj.get().getTiemNangSo() >= 8) {
                    p.nj.get().setTiemNangSo(8);
                    p.session.sendMessageLog("Chỉ được học tối đa 8 quyển");
                    break;
                } else if (p.nj.isHuman) {
                    p.nj.get().setTiemNangSo(p.nj.get().getTiemNangSo() + 1);
                    p.nj.get().updatePpoint(p.nj.get().getPpoint() + 10);
                    p.nj.removeItemBag(index, 1);
                    p.updatePotential();
                    p.sendYellowMessage("Bạn nhận được 10 điểm tiềm năng");
                } else if (p.nj.isNhanban && p.nj.clone != null) {
                    p.nj.clone.setTiemNangSo(p.nj.clone.getTiemNangSo() + 1);
                    p.nj.get().updatePpoint(p.nj.get().getPpoint() + 10);
                    p.nj.removeItemBag(index, 1);
                    p.updatePotential();
                }
                break;
            }

            case 215:
            case 229:
            case 283:
            case 833: {
                final byte level = (byte) ((item.id != 215) ? ((item.id != 229) ? ((item.id != 283) ? 4 : 3) : 2) : 1);
                if (level > p.nj.levelBag + 1) {
                    p.sendYellowMessage("Cần mở Túi vải cấp " + (p.nj.levelBag + 1) + " mới có thể mở được túi vải này");
                    return;
                }
                if (p.nj.levelBag >= level) {
                    p.sendYellowMessage("Bạn đã mở túi vải này rồi");
                    return;
                }
                
                final Ninja c = p.nj;
                c.maxluggage += useItem.arrOpenBag[level];
                if (c.levelBag < 3) {
                    p.nj.levelBag = level;
                } else {
                    if (c.levelBag == 3 && c.maxluggage >= 84) { 
                        p.nj.levelBag = level;
                    }
                }
                final Item[] bag = new Item[p.nj.maxluggage];
                for (int j = 0; j < p.nj.ItemBag.length; ++j) {
                    bag[j] = p.nj.ItemBag[j];
                }
                (p.nj.ItemBag = bag)[index] = null;
                p.openBagLevel(index);
                break;
            }
 
            case 272: {
                // Rương may mắn
                if (numbagnull == 0) {
                    p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                    return;
                }
                if (util.percent(100, 20)) {
                    final int num = util.nextInt(MIN_MAX_YEN_RUONG_MAY_MAN[0], MIN_MAX_YEN_RUONG_MAY_MAN[1]);
                    p.nj.upyenMessage(num);
                    p.sendYellowMessage("Bạn nhận được " + num + " yên");
                } else {
                    final short[] arId = {2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10, 11, 242, 275, 276, 277, 278, 280, 284};
                    final short idI = arId[util.nextInt(arId.length)];
                    final ItemData data2 = ItemData.ItemDataId(idI);
                    Item itemup = ItemData.itemDefault(idI);
                    itemup.setLock(false);
                    p.nj.addItemBag(true, itemup);
                }
                if (p.nj.getTaskId() == 40 && p.nj.getTaskIndex() == 1) {
                    p.nj.upMainTask();
                }
                p.nj.removeItemBag(index, 1);
                break;
            }
            case 248: {
                final Effect eff = p.nj.get().getEffId(22);
                if (eff != null) {
                    final long time = eff.timeRemove + 18000000L;
                    p.setEffect(22, 0, (int) (time - System.currentTimeMillis()), 2);
                } else {
                    p.setEffect(22, 0, 18000000, 2);
                }
                p.nj.removeItemBag(index, 1);
                break;
            }
            case 276: {
                // Long luc dan
                p.setEffect(25, 0, 600000, 500);
                p.nj.removeItemBag(index, 1);
                break;
            }
            case 275: {
                // Minh man dan
                p.setEffect(24, 0, _10_MINS, 500);
                p.nj.removeItemBag(index, 1);
                break;
            }
            case 277: {
                // Khang the dan
                p.setEffect(26, 0, _10_MINS, 100);
                p.nj.removeItemBag(index, 1);
                break;

            }
            case 278: {
                // SInh menh dan
                p.setEffect(29, 0, _10_MINS, 1000);
                p.nj.removeItemBag(index, 1);
                break;
            }
            case 280: {
                // TODO HD COUNT
                if (p.nj.useCave == 0) {
                    p.session.sendMessageLog("Số lần dùng Lệnh bài hạng động trong ngày hôm nay đã hết");
                    return;
                }
                final Ninja c2 = p.nj;
                ++c2.nCave;
                final Ninja c3 = p.nj;
                --c3.useCave;
                p.sendYellowMessage("Số lần đi hang động của bạn trong ngày hôm nay tăng lên là " + p.nj.nCave + " lần");
                p.nj.removeItemBag(index, 1);
                break;
            }
            case 282: {
                if (numbagnull == 0) {
                    p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                    return;
                }
                if (util.percent(4000, 1)) {
                    Item im = ItemData.itemDefault(833);
                    im.isExpires = false;
                    im.isLock = false;
                    im.quantity = 1;
                    Manager.chatKTG("Chúc mừng " + p.nj.name + " đã nhận được " + ItemData.ItemDataId(im.id).name + " khi sử dụng " + ItemData.ItemDataId(item.id).name);
                    p.nj.addItemBag(false, im);
                } else if (util.percent(1000, 1)) {
                    Item im = ItemData.itemDefault(283);
                    im.isExpires = false;
                    im.isLock = false;
                    im.quantity = 1;
                    Manager.chatKTG("Chúc mừng " + p.nj.name + " đã nhận được " + ItemData.ItemDataId(im.id).name + " khi sử dụng " + ItemData.ItemDataId(item.id).name);
                    p.nj.addItemBag(false, im);
                }
                if (util.percent(100, 15)) {
                    final int num = util.nextInt(MIN_MAX_YEN_RUONG_TINH_SAO[0], MIN_MAX_YEN_RUONG_TINH_SAO[1]);
                    p.nj.upyenMessage(num);
                    p.sendYellowMessage("Bạn nhận được " + num + " yên");
                } else {
                    final short[] arId = {2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 6, 6, 6, 7, 7, 8, 8, 8, 9, 9, 9, 10, 10, 11, 242, 275, 276, 277, 278, 280, 280, 280, 284, 436, 437};
                    final short idI = arId[util.nextInt(arId.length)];
                    Item itemup = ItemData.itemDefault(idI);
                    itemup.setLock(false);
                    if (itemup.id == 280 || itemup.id == 284) {
                        Manager.chatKTG("Chúc mừng " + p.nj.name + " đã nhận được " + ItemData.ItemDataId(itemup.id).name + " khi sử dụng " + ItemData.ItemDataId(item.id).name);
                    }
                    p.nj.addItemBag(true, itemup);
                }
                p.nj.removeItemBag(index, 1);
                if (p.nj.getTaskId() == 40 && p.nj.getTaskIndex() == 1) {
                    p.nj.upMainTask();
                }
                break;
            }
            
            case 288: {
                Item im = ItemData.itemDefault(util.nextInt(289, 291));
                im.quantity = 1;
                im.isExpires = false;
                im.expires = -1;
                im.setLock(false);
                p.nj.addItemBag(true, im);
                if (p.nj.getTaskId() == 40 && p.nj.getTaskIndex() == 2) {
                    p.nj.upMainTask();
                }
                break;
            }

            case 289: {//thẻ bài sơ
                p.nj.diemTinhTu++;
                p.sendYellowMessage("Bạn nhận được 1 điểm tinh tú");
                p.nj.removeItemBag(index, 1);
                break;
            }
            case 290: {//thẻ bài trung
                p.nj.diemTinhTu += 3;
                p.sendYellowMessage("Bạn nhận được 3 điểm tinh tú");
                p.nj.removeItemBag(index, 1);
                break;
            }
            case 291: {//thẻ bài cao
                p.nj.diemTinhTu += 9;
                p.sendYellowMessage("Bạn nhận được 9 điểm tinh tú");
                p.nj.removeItemBag(index, 1);
                break;
            }

            case 308: {
                // Phong loi
                if (p.nj.get().getPhongLoi() >= 10) {
                    p.nj.get().setPhongLoi(10);
                    p.session.sendMessageLog("Chi được dùng tối đa 10 cái");
                } else if (p.nj.isHuman) {
                    p.nj.get().setPhongLoi(p.nj.get().getPhongLoi() + 1);
                    p.nj.removeItemBag(index, 1);
                    p.nj.get().setSpoint(p.nj.get().getSpoint() + 1);
                    p.sendYellowMessage("Bạn nhận được 1 điểm kỹ năng");
                    p.loadSkill();
                } else if (p.nj.isNhanban) {
                    if (p.nj.clone != null) {
                        p.nj.clone.setPhongLoi(p.nj.clone.getPhongLoi() + 1);
                        p.nj.get().setSpoint(p.nj.get().getSpoint() + 1);
                        p.nj.removeItemBag(index, 1);
                        p.sendYellowMessage("Bạn nhận được 1 điểm kỹ năng");
                        p.loadSkill();
                    }
                }
                break;
            }
            case 309: {
                if (p.nj.get().getBanghoa() >= 10) {
                    p.nj.get().setBanghoa(10);
                    p.session.sendMessageLog("Chi được dùng tối đa 10 cái");
                } else if (p.nj.isHuman) {
                    p.nj.get().setBanghoa(p.nj.get().getBanghoa() + 1);
                    p.nj.get().updatePpoint(p.nj.getPpoint() + 10);
                    p.nj.removeItemBag(index, 1);
                    p.updatePotential();
                    p.sendYellowMessage("Bạn nhận được 10 điểm tiềm năng");
                } else if (p.nj.isNhanban) {
                    if (p.nj.clone != null) {
                        p.nj.clone.setBanghoa(p.nj.clone.getBanghoa() + 1);
                        p.nj.updatePpoint(p.nj.getPpoint() + 10);
                        p.nj.removeItemBag(index, 1);
                        p.updatePotential();
                        p.sendYellowMessage("Bạn nhận được 10 điểm tiềm năng");
                    }
                }
                // Bang hoa
                break;
            }
            case 383:
            case 384:
            case 385: {
                if (numbagnull == 0) {
                    p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                    return;
                }
                if (p.nj.get().nclass == 0) {
                    p.session.sendMessageLog("Hãy nhập học để mở vật phẩm.");
                    return;
                }
                byte sys2 = -1;
                int idI2;
                if (util.nextInt(2) == 0) {
                    if (p.nj.gender == 0) {
                        if (p.nj.get().getLevel() < 50 && item.id != 384 && item.id != 385) {
                            idI2 = (new short[] { 171, 161, 151, 141, 131 })[util.nextInt(5)]; //đồ 4x nữ
                        } else if (p.nj.get().getLevel() < 60 && item.id != 385) {
                            idI2 = (new short[] { 173, 163, 153, 143, 133 })[util.nextInt(5)]; //đồ 5x nữ
                        } else if (p.nj.get().getLevel() < 70) {
                            idI2 = (new short[] { 330, 329, 328, 327, 326 })[util.nextInt(5)]; //đồ 6x nữ
                        } else {
                            idI2 = (new short[] { 368, 367, 366, 365, 364 })[util.nextInt(5)]; //đồ 7x nữ
                        }
                    } else if (p.nj.get().getLevel() < 50 && item.id != 384 && item.id != 385) {
                        idI2 = (new short[] { 170, 160, 150, 140, 130 })[util.nextInt(5)]; //đồ 4x nam
                    } else if (p.nj.get().getLevel() < 60 && item.id != 385) {
                        idI2 = (new short[] { 172, 162, 152, 142, 132 })[util.nextInt(5)]; //đồ 5x nam
                    } else if (p.nj.get().getLevel() < 70) {
                        idI2 = (new short[] { 325, 323, 321, 319, 317 })[util.nextInt(5)]; //đồ 6x nam
                    } else {
                        idI2 = (new short[] { 363, 361, 359, 357, 355 })[util.nextInt(5)]; //đồ 7x nam
                    }
                } else if (util.nextInt(2) == 1) {
                    if (p.nj.get().nclass == 1 || p.nj.get().nclass == 2) {
                        sys2 = 1;
                    } else if (p.nj.get().nclass == 3 || p.nj.get().nclass == 4) {
                        sys2 = 2;
                    } else if (p.nj.get().nclass == 5 || p.nj.get().nclass == 6) {
                        sys2 = 3;
                    }
                    if (p.nj.get().getLevel() < 50 && item.id != 384 && item.id != 385) {
                        idI2 = (new short[]{97, 117, 102, 112, 107, 122})[p.nj.get().nclass - 1];
                    } else if (p.nj.get().getLevel() < 60 && item.id != 385) {
                        idI2 = (new short[]{98, 118, 103, 113, 108, 123})[p.nj.get().nclass - 1];
                    } else if (p.nj.get().getLevel() < 70) {
                        idI2 = (new short[]{331, 332, 333, 334, 335, 336})[p.nj.get().nclass - 1];
                    } else {
                        idI2 = (new short[]{369, 370, 371, 372, 373, 374})[p.nj.get().nclass - 1];
                    }
                } else if (p.nj.get().getLevel() < 50 && item.id != 384 && item.id != 385) {
                    idI2 = (new short[]{192, 187, 182, 177})[util.nextInt(4)];
                } else if (p.nj.get().getLevel() < 60 && item.id != 385) {
                    idI2 = (new short[]{193, 188, 183, 178})[util.nextInt(4)];
                } else if (p.nj.get().getLevel() < 70) {
                    idI2 = (new short[]{324, 322, 320, 318})[util.nextInt(4)];
                } else {
                    idI2 = (new short[]{362, 360, 358, 356})[util.nextInt(4)];
                }
                Item itemup;
                if (sys2 < 0) {
                    sys2 = (byte) util.nextInt(1, 3);
                    itemup = ItemData.itemDefault(idI2, sys2);
                } else {
                    itemup = ItemData.itemDefault(idI2);
                }
                itemup.sys = sys2;
                byte nextup = 12;
                if (item.id == 384) {
                    nextup = 14;
                } else if (item.id == 385) {
                    nextup = 16;
                }
                itemup.setLock(item.isLock());
                itemup.upgradeNext(nextup);
                p.nj.addItemBag(true, itemup);
                p.nj.removeItemBag(index, 1);
                break;
            }

            case 281: {//Lệnh bài gia tộc
                final ClanManager clan = ClanManager.getClanByName(p.nj.clan.clanName);
                if (clan == null || clan.getMem(p.nj.name) == null) {
                    p.sendYellowMessage("Cần có gia tộc để sử dụng");
                    return;
                }
                if (clan.use_card <= 0) {
                    p.sendYellowMessage("Số lần sử dụng lệnh bài đã hết");
                    return;
                }
                clan.openDun += 1;
                clan.use_card -= 1;
                p.sendYellowMessage("Số lần đi Lãnh địa gia tộc tăng lên là " + clan.openDun);
                p.nj.removeItemBag(index, 1);
                break;
            }

            case 436:
            case 437:
            case 438: {
                final ClanManager clan = ClanManager.getClanByName(p.nj.clan.clanName);
                if (clan == null || clan.getMem(p.nj.name) == null) {
                    p.sendYellowMessage("Cần có gia tộc để sử dụng");
                    return;
                }
                if (item.id == 436) {
//                    if (clan.getLevel() < 5) {
//                        p.sendYellowMessage("Yêu cầu gia tộc phải đạt cấp 5");
//                        return;
//                    }
                    p.upExpClan(util.nextInt(100, 200));
                    p.nj.removeItemBag(index, 1);
                    return;
                } else if (item.id == 437) {
                    if (clan.getLevel() < 10) {
                        p.sendYellowMessage("Yêu cầu gia tộc phải đạt cấp 10");
                        return;
                    }
                    p.upExpClan(util.nextInt(300, 800));
                    p.nj.removeItemBag(index, 1);
                    return;
                } else {
                    if (item.id != 438) {
                        break;
                    }
                    if (clan.getLevel() < 15) {
                        p.sendYellowMessage("Yêu cầu gia tộc phải đạt cấp 15");
                        return;
                    }
                    p.upExpClan(util.nextInt(1000, 2000));
                    p.nj.removeItemBag(index, 1);
                    return;
                }
            }
            
            case 444: {
                Item im = p.nj.ItemMounts[4];
                if (im.id != 443) {
                    p.sendYellowMessage("Chì dành cho sói phê cỏ");
                    return;
                }
                if (im != null) {
                    for (Option op : im.option) {
                        if (op.id == 66) {
                            if (op.param < 1000) {
                                op.param += 200;
                                if (op.param > 1000) {
                                    op.param = 1000;
                                }
                                p.nj.removeItemBag(index, 1);
                            } else {
                                p.sendYellowMessage("Không thể sử dụng khi sinh lực vẫn còn đầy.");
                            }
                        }
                    }
                    try {
                        p.loadMounts();
                    } catch (IOException ex) {
                    }
                } else {
                    p.sendYellowMessage("Không có thú cưỡi");
                }
                break;
            }

            case 449: {//Lang hồn thảo
                if (p.updateXpMounts(5, (byte)0)) {
                    p.nj.removeItemBag(index, 1);
                    break;
                }
                break;
            }
            case 450: {//Lang hồn mộc
                if (p.updateXpMounts(7, (byte)0)) {
                    p.nj.removeItemBag(index, 1);
                    break;
                }
                break;
            }
            case 451: {//Địa lang thảo
                if (p.updateXpMounts(14, (byte)0)) {
                    p.nj.removeItemBag(index, 1);
                    break;
                }
                break;
            }
            case 452: {//Tam lục diệp
                if (p.updateXpMounts(20, (byte)0)) {
                    p.nj.removeItemBag(index, 1);
                    break;
                }
                break;
            }
            case 453: {//Xích lan hoa
                if (p.updateXpMounts(25, (byte)0)) {
                    p.nj.removeItemBag(index, 1);
                    break;
                }
                break;
            }
            case 454: {
                if (p.updateSysMounts(0)) {
                    p.nj.removeItemBag(index, 1);
                    break;
                }
                break;
            }
            case 780: {
                if (p.updateSysMounts(2)) {
                    p.nj.removeItemBag(index, 1);
                    break;
                }
                break;
            }

            case 490: {
                if (p.nj.isNhanban) {
                    p.session.sendMessageLog("Chức năng này không dành cho phân thân");
                    return;
                }
                p.nj.getPlace().leave(p);
                final Map map = Server.getMapById(138);
                map.area[0].EnterMap0(p.nj);
                p.endLoad(true);
                p.nj.removeItemBag(index, 1);
                break;
            }
            
            case 535: {//lang bảo
                if (numbagnull == 0) {
                    p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                    return;
                }
                final short[] arId = {449,450,451,452,453,439,440,441,442,443,573,574,575,449,450,451,452,453,439,440,441,442,443,573,574,575,449,450,451,452,453,439,440,441,442,443,573,574,575,449,450,451,452,453,439,440,441,442,443,573,574,575,449,450,451,452,453,439,440,441,442,443,573,574,575,449,450,451,452,453,439,440,441,442,443,573,574,575,449,450,451,452,453,439,440,441,442,443,573,574,575};
                final short idI = arId[util.nextInt(arId.length)];
                final ItemData data2 = ItemData.ItemDataId(idI);
                Item itemup = ItemData.itemDefault(idI);
                itemup.setLock(item.isLock());
                p.nj.addItemBag(true, itemup);
                p.nj.removeItemBag(index, 1);
                break;
            }

            case 536: {//khí bảo
                if (numbagnull == 0) {
                    p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                    return;
                }
                final short[] arId = {485,486,487,488,489,490,576,577,578,576,577,485,486,487,488,489,490,576,577,578,576,577,485,486,487,488,489,490,576,577,578,576,577,485,486,487,488,489,490,576,577,578,576,577,485,486,487,488,489,490,576,577,578,576,577,485,486,487,488,489,490,576,577,578,576,577,485,486,487,488,489,490,576,577,578,576,577,485,486,487,488,489,490,576,577,578,576,577,485,486,487,488,489,490,576,577,578,576,577};
                final short idI = arId[util.nextInt(arId.length)];
                final ItemData data2 = ItemData.ItemDataId(idI);
                Item itemup = ItemData.itemDefault(idI);
                itemup.setLock(item.isLock());
                p.nj.addItemBag(true, itemup);
                p.nj.removeItemBag(index, 1);
                break;
            }

            case 537: {
                // Khai nhan phu
                val id = 41;
                final Effect eff = p.nj.get().getEffId(id);
                if (eff != null) {
                    final long time = eff.timeRemove + _1HOUR * 3;
                    p.setEffect(id, 0, (int) (time - System.currentTimeMillis()), 2);
                } else {
                    p.setEffect(id, 0, _1HOUR * 3, 2);
                }
                p.nj.removeItemBag(index, 1);
                break;
            }
            case 538: {
                // Thien nhan phu
                val id = 40;
                final Effect eff = p.nj.get().getEffId(id);
                if (eff != null) {
                    final long time = eff.timeRemove + _1HOUR * 5;
                    p.setEffect(id, 0, (int) (time - System.currentTimeMillis()), 2);
                } else {
                    p.setEffect(id, 0, _1HOUR * 5, 2);
                }
                p.nj.removeItemBag(index, 1);
                break;
            }
            case 539: {
                p.setEffect(32, 0, 3600000, 3);
                p.nj.removeItemBag(index, 1);
                break;
            }
            case 540: {
                p.setEffect(33, 0, 3600000, 4);
                p.nj.removeItemBag(index, 1);
                break;
            }
            case 38: {//pnng
                p.nj.upyenMessage(util.nextInt(500, 1000));
                p.nj.removeItemBag(index, 1);
                break;
            }
            case 549: {//giày rách
                p.nj.upyenMessage(util.nextInt(1000, 5000));
                p.nj.removeItemBag(index, 1);
                break;
            }
            case 550: {//giày bạc
                p.nj.upyenMessage(util.nextInt(10000, 20000));
                p.nj.removeItemBag(index, 1);
                break;
            }
            case 551: {//giày vàng
                p.nj.upyenMessage(util.nextInt(10000, 50000));
                p.nj.removeItemBag(index, 1);
                break;
            }
            case 573: {
                if (p.updateXpMounts(200, (byte) 0)) {
                    p.nj.removeItemBag(index, 1);
                    break;
                }
                break;
            }
            case 574: {
                if (p.updateXpMounts(400, (byte) 0)) {
                    p.nj.removeItemBag(index, 1);
                    break;
                }
                break;
            }
            case 575: {
                if (p.updateXpMounts(600, (byte) 0)) {
                    p.nj.removeItemBag(index, 1);
                    break;
                }
                break;
            }
            case 576: {
                if (p.updateXpMounts(100, (byte) 1)) {
                    p.nj.removeItemBag(index, 1);
                    break;
                }
                break;
            }
            case 577: {
                if (p.updateXpMounts(250, (byte) 1)) {
                    p.nj.removeItemBag(index, 1);
                    break;
                }
                break;
            }
            case 578: {
                if (p.updateXpMounts(500, (byte) 1)) {
                    p.nj.removeItemBag(index, 1);
                    break;
                }
                break;
            }
            case 778: {
                if (p.updateXpMounts(util.nextInt(1,10), (byte) 2)) {
                    p.nj.removeItemBag(index, 1);
                    break;
                }
                break;
            }
            case 564: {
                final Effect eff = p.nj.get().getEffId(34);
                if (eff != null) {
                    final long time = eff.timeRemove + 18000000L;
                    p.setEffect(34, 0, (int) (time - System.currentTimeMillis()), 2);
                } else {
                    p.setEffect(34, 0, 18000000, 2);
                }
                p.nj.removeItemBag(index, 1);
                break;
            }
            case 647: {
                if (numbagnull == 0) {
                    p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                    return;
                }
                if (util.percent(100, 1)) {
                    Item im = ItemData.itemDefault(283);
                    im.isExpires = false;
                    im.isLock = false;
                    im.quantity = 1;
                    p.nj.addItemBag(false, im);
                }
                if (util.percent(2000, 1)) {
                    Item im = ItemData.itemDefault(797);
                   // im.upgrade = 16;
                    im.isExpires = false;
                    im.isLock = false;
                    im.quantity = 1;
                    p.nj.addItemBag(false, im);
                }
                if (util.percent(500, 1)) {
                    Item im = ItemData.itemDefault(222);
                   // im.upgrade = 16;
                    im.isExpires = false;
                    im.isLock = false;
                    im.quantity = 1;
                    p.nj.addItemBag(false, im);
                }
                if (util.percent(100, 1)) {
                    Item im = ItemData.itemDefault(util.nextInt(223, 228));
                   // im.upgrade = 16;
                    im.isExpires = false;
                    im.isLock = false;
                    im.quantity = 1;
                    p.nj.addItemBag(false, im);
                }
                else if (util.percent(100, 10)) {
                    final int num = util.nextInt(MIN_MAX_YEN_RUONG_MA_QUAI[0], MIN_MAX_YEN_RUONG_MA_QUAI[1]);
                    p.nj.upyenMessage(num);
                    p.sendYellowMessage("Bạn nhận được " + num + " yên");
                } else {
                    final short[] arId = {3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 7, 7, 7, 7, 7, 7, 8, 8, 8, 9, 9, 9, 10, 10, 11, 280, 280, 280, 436, 437, 618, 619, 620, 621, 622, 623, 624, 625, 626, 627, 628, 629, 630, 631, 632, 633, 634, 635, 636, 637};
                    final short idI = arId[util.nextInt(arId.length)];
                    final ItemData data2 = ItemData.ItemDataId(idI);
                    Item itemup;
                    if (data2.type < 10) {
                        if (data2.type == 1) {
                            itemup = ItemData.itemDefault(idI);
                            itemup.sys = GameScr.SysClass(data2.nclass);
                        } else {
                            final byte sys = (byte) util.nextInt(1, 3);
                            itemup = ItemData.itemDefault(idI, sys);
                        }
                    } else {
                        itemup = ItemData.itemDefault(idI);
                    }
                    itemup.setLock(false);
                    for (final Option Option : itemup.option) {
                        final int idOp2 = Option.id;
                        Option.param = util.nextInt(item.getOptionShopMin(idOp2, Option.param), Option.param);
                    }
                    if (itemup.id == 280) {
                        Manager.chatKTG("Chúc mừng " + p.nj.name + " đã nhận được " + ItemData.ItemDataId(itemup.id).name + " khi sử dụng " + ItemData.ItemDataId(item.id).name);
                    }
                    p.nj.addItemBag(true, itemup);
                }
                if (p.nj.getTaskId() == 40 && p.nj.getTaskIndex() == 1) {
                    p.nj.upMainTask();
                }
                p.nj.removeItemBag(index, 1);
                break;
            }
            case 251: {
                p.typemenu = 251;
                server.menu.doMenuArray(p, new String[] {"Đổi sách kỹ năng", "Đổi sách tiềm năng"});
                break;
            }
            case 256: {
                // Tay am cap 60 tl
                if (p.nj.get().getLevel() >= 60 && p.nj.get().expdown != 0) {
                    p.upExpDown(p.nj.get().expdown);
                    p.nj.removeItemBag(index, 1);
                } else {
                    p.sendYellowMessage("Trình độ không phù hợp hoặc bạn không có exp âm");
                }
                break;
            }

            case 255: {
                // Tay am duoi cap 60
                if (p.nj.get().getLevel() < 60 && p.nj.get().expdown != 0) {
                    p.upExpDown(p.nj.get().expdown);
                    p.nj.removeItemBag(index, 1);
                } else {
                    p.sendYellowMessage("Trình độ không phù hợp hoặc bạn không có exp âm");
                }
                break;
            }
            case 254: {
                // Tay tam duoi cap 30
                if (p.nj.get().getLevel() < 30 && p.nj.get().expdown != 0) {
                    p.upExpDown(p.nj.get().expdown);
                    p.nj.removeItemBag(index, 1);
                } else {
                    p.sendYellowMessage("Trình độ không phù hợp hoặc bạn không có exp âm");
                }
                break;
            }
            case 261: {
                // Dung linh dan danh boss
                p.setEffect(23, 0, _10_MINS, 0);
                p.nj.removeItemBag(index, 1);
                break;
            }
            case 263: {
                // Sử dụng tui quà gia tộc
                if (p.nj.get().isNhanban) {
                    p.sendYellowMessage("Phân thân không thể sử dụng vật phẩm này");
                    return;
                }
                short randomID = LDGT_REWARD_ITEM_ID[util.nextInt(LDGT_REWARD_ITEM_ID.length)];

                if (randomID >= 685 && randomID <= 694) {
                    if (!util.percent(100, 698 - randomID)) {
                        randomID = 12;
                    } else {

                    }
                }

                if (randomID == 12) {
                    p.nj.upyenMessage(util.nextInt(MIN_MAX_YEN_RUONG_MA_QUAI[0], MIN_MAX_YEN_RUONG_MA_QUAI[0]));
                } else {
                    p.nj.addItemBag(true, ItemData.itemDefault(randomID));
                }

                p.nj.removeItemBag(index, 1);
                break;
            }

            case 268: {//tà thú lệnh
                if (p.nj.useTathu == 0) {
                    p.session.sendMessageLog("Số lần dùng Tà thú lệnh trong ngày hôm nay đã hết");
                    return;
                }
                p.nj.useTathu--;
                p.nj.taThuCount++;
                p.sendYellowMessage("Số lần nhận nhiệm vụ tà thú của bạn trong ngày hôm nay tăng lên là " + p.nj.taThuCount + " lần");
                p.nj.removeItemBag(index, 1);
                break;
            }

            case 572: {
                // TBL
                p.typemenu = 572;
                if (!p.activeTBL) {
                    MenuController.doMenuArray(p, new String[]{"Phạm vi 240", "Phạm vi 480", "Phạm vi toàn map", "Nhặt tất cả", "Nhặt vp hữu dụng", "Bật tàn sát"});
                } else {
                    MenuController.doMenuArray(p, new String[]{"Phạm vi 240", "Phạm vi 480", "Phạm vi toàn map", "Nhặt tất cả", "Nhặt vp hữu dụng", "Tắt tàn sát"});
                }

                break;
            }
            case 599: {
                final ClanManager clanMng = p.nj.clan.clanManager();
                final ClanThanThu thanThu = clanMng.getCurrentThanThu();
                if (thanThu != null) {
                    if (thanThu.upExp(2)) {
                        p.nj.removeItemBag(index, 1);
                    } else {
                        p.sendYellowMessage("Kinh nghiệm của thần thú đã đạt tối đa!");
                    }
                } else {
                    p.sendYellowMessage("Gia tộc của bạn không có thần thú!");
                }
                break;
            }
            case 600: {
                ClanManager clanMng = null;
                if (p.nj.clan != null) {
                    clanMng = p.nj.clan.clanManager();
                }
                ClanThanThu thanThu = null;
                if (clanMng != null) {
                    thanThu = clanMng.getCurrentThanThu();
                }
                if (thanThu != null) {
                    if (thanThu.upExp(5)) {
                        p.nj.removeItemBag(index, 1);
                    } else {
                        p.sendYellowMessage("Kinh nghiệm của thần thú đã đạt tối đa!");
                    }
                } else {
                    p.sendYellowMessage("Gia tộc của bạn không có thần thú!");
                }
                break;
            }
            case 605: {
                if (p.nj.clan.typeclan < 4) {
                    p.session.sendMessageLog("Vật phẩm chỉ dành cho tộc trưởng.");
                    return;
                }
                ClanManager clanMng = null;
                if (p.nj.clan != null) {
                    clanMng = p.nj.clan.clanManager();
                }
                ClanThanThu thanThu = null;
                if (clanMng != null) {
                    thanThu = clanMng.getCurrentThanThu();
                }
                ClanThanThu.EvolveStatus result = null;
                if (thanThu != null) {
                    result = thanThu.evolve();
                }
                if (result == null) return;

                Message m = null;
                switch (result) {
                    case SUCCESS:
                        m = clanMng.createMessage("Gia tộc bạn nhận được " + clanMng.getCurrentThanThu().getPetItem().getData().name);
                        p.nj.removeItemBag(index, 1);
                        break;
                    case FAIL:
                        m = clanMng.createMessage("Tiến hoá thất bại bạn mất 1 tiến hoá đan");
                        p.nj.removeItemBag(index, 1);
                        break;
                    case MAX_LEVEL:
                        m = clanMng.createMessage("Thần thú của bạn đã đạt cấp cao nhất");
                        break;
                    case NOT_ENOUGH_STARS:
                        m = clanMng.createMessage("Thần thú của bạn không đủ sao để nâng cấp");
                        break;
                    default:
                }
                clanMng.sendMessage(m);
                break;
            }
            case 548: {//cần câu vàng
                p.CanCau();
                break;
            }
            case 597: {//vạn ngư câu
                // Sử dụng cần câu
                item.setLock(true);
                if (numbagnull == 0) {
                    p.sendYellowMessage("Hành trang không đủ ô trống để câu cá");
                    return;
                }

                if (p.nj.y == 456 && (p.nj.x >= 107 && p.nj.x <= 2701)) {
                    boolean coMoi = false;
                    for (Item item1 : p.nj.ItemBag) {
                        if (item1 != null && (item1.id == 602 || item1.id == 603)) {
                            p.nj.removeItemBags(item1.id, 1);
                            coMoi = true;
                            break;
                        }
                    }

                    if (coMoi) {
                        if (util.percent(70, 30)) {
                            val random = new int[]{599, 600}[util.nextInt(2)];
                            int quantity = util.nextInt(0, 5);
                            final Item item1 = ItemData.itemDefault(random);
                            item1.quantity = quantity;
                            p.nj.addItemBag(true, item1);
                            p.sendYellowMessage("Bạn nhận được " + quantity);
                        } else {
                            p.sendYellowMessage("Không câu được gì cả");
                        }
                    } else {
                        p.sendYellowMessage("Không có mồi câu để câu cá");
                    }
                } else {
                    p.sendYellowMessage("Hãy đi đến vùng nước ở làng chài để câu cá");
                }

                break;
            }
            case 695:
            case 696:
            case 697:
            case 698:
            case 699:
            case 700:
            case 701:
            case 702:
            case 703: {
                if (numbagnull == 0) {
                    p.sendYellowMessage("Hành trang đầy");
                    return;
                }
                upDaDanhVong(p, item);
                break;
            }
            case 705: {
                if (p.nj.isNhanban) {
                    p.sendYellowMessage("Phân thân không thể sử dụng vật phẩm này.");
                    return;
                }
                if (p.nj.useDanhVongPhu == 0) {
                    p.sendYellowMessage("Số lần sử dụng Danh vọng phú của bạn hôm nay đã hết.");
                    return;
                }
                p.nj.useDanhVongPhu--;
                p.nj.countTaskDanhVong += 5;
                p.sendYellowMessage("Số lần nhận nhiệm vụ Danh vọng tăng thêm 5 lần");
                p.nj.removeItemBag(index, 1);
                break;
            }
            //Mảnh jirai
            case 733:
            case 734:
            case 735:
            case 736:
            case 737:
            case 738:
            case 739:
            case 740:
            case 741: {
                if (p.nj.isNhanban) {
                    p.sendYellowMessage("Chức năng không dành cho phân thân");
                    return;
                }
                if (p.nj.gender == 0) {
                    p.sendYellowMessage("Giới tính không phù hợp.");
                    return;
                }
                int checkID = item.id - 733;
                if (p.nj.ItemBST[checkID] == null) {
                    if (p.nj.quantityItemyTotal(item.id) < 100) {
                        p.sendYellowMessage("Bạn không đủ mảnh để ghép.");
                        return;
                    }
//                    if (item.isLock() == true) {
//                        p.nj.removeItemBag(p.nj.getIndexBagid(item.id, true), 100);
//                    } else {
//                        p.nj.removeItemBag(p.nj.getIndexBagid(item.id, true), 100);
//                    }
                    p.nj.ItemBST[checkID] = ItemData.itemDefault(ItemData.checkIdJiraiNam(checkID));
                    p.nj.ItemBST[checkID].setUpgrade(1);
                    p.nj.ItemBST[checkID].setLock(true);
                    p.sendYellowMessage(ItemData.ItemDataId(p.nj.ItemBST[checkID].id).name + " đã được thêm vào bộ sưu tập.");
                } else {
                    if (p.nj.ItemBST[checkID].getUpgrade() >= 16) {
                        p.sendYellowMessage("Bộ sưu tập này đã đạt điểm tối đa, không thể nâng cấp thêm.");
                        return;
                    }
                    if (p.nj.quantityItemyTotal(item.id) < (p.nj.ItemBST[checkID].getUpgrade() + 1) * 100) {
                        p.sendYellowMessage("Bạn không đủ mảnh để nâng cấp.");
                        return;
                    }
                    p.nj.ItemBST[checkID].setUpgrade(p.nj.ItemBST[checkID].getUpgrade()+1);
                    if (item.isLock() == true) {
                        p.nj.removeItemBag(p.nj.getIndexBagid(item.id, true), p.nj.ItemBST[checkID].getUpgrade() * 100);
                    } else {
                        p.nj.removeItemBag(p.nj.getIndexBagid(item.id, false), p.nj.ItemBST[checkID].getUpgrade() * 100);
                    }
                    p.sendYellowMessage(ItemData.ItemDataId(p.nj.ItemBST[checkID].id).name + " đã được nâng cấp.");
                }
                break;
            }
            
            case 743: {
                if (p.nj.isNhanban) {
                    p.session.sendMessageLog(Language.NOT_FOR_PHAN_THAN);
                    return;
                }
                p.nj.getPlace().callMobs(p.nj, (short) 230, item.id);
                break;
            } 

            //Mảnh jirai
            case 760:
            case 761:
            case 762:
            case 763:
            case 764:
            case 765:
            case 766:
            case 767:
            case 768: {
                if (p.nj.isNhanban) {
                    p.sendYellowMessage("Chức năng không dành cho phân thân");
                    return;
                }
                if (p.nj.gender == 1) {
                    p.sendYellowMessage("Giới tính không phù hợp.");
                    return;
                }
                int checkID = item.id - 760;
                if (p.nj.ItemBST[checkID] == null) {
                    if (p.nj.quantityItemyTotal(item.id) < 100) {
                        p.sendYellowMessage("Bạn không đủ mảnh để ghép.");
                        return;
                    }
//                    if (item.isLock() == true) {
//                        p.nj.removeItemBag(p.nj.getIndexBagid(item.id, true), 100);
//                    } else {
//                        p.nj.removeItemBag(p.nj.getIndexBagid(item.id, true), 100);
//                    }
                    p.nj.ItemBST[checkID] = ItemData.itemDefault(ItemData.checkIdJiraiNu(checkID));
                    p.nj.ItemBST[checkID].setUpgrade(1);
                    p.sendYellowMessage(ItemData.ItemDataId(p.nj.ItemBST[checkID].id).name + " đã được thêm vào bộ sưu tập.");
                } else {
                    if (p.nj.ItemBST[checkID].getUpgrade() >= 16) {
                        p.sendYellowMessage("Bộ sưu tập này đã đạt điểm tối đa, không thể nâng cấp thêm.");
                        return;
                    }
                    if (p.nj.quantityItemyTotal(item.id) < (p.nj.ItemBST[checkID].getUpgrade() + 1) * 100) {
                        p.sendYellowMessage("Bạn không đủ mảnh để nâng cấp.");
                        return;
                    }
                    p.nj.ItemBST[checkID].setUpgrade(p.nj.ItemBST[checkID].getUpgrade()+1);
                    if (item.isLock() == true) {
                        p.nj.removeItemBag(p.nj.getIndexBagid(item.id, true), p.nj.ItemBST[checkID].getUpgrade() * 100);
                    } else {
                        p.nj.removeItemBag(p.nj.getIndexBagid(item.id, false), p.nj.ItemBST[checkID].getUpgrade() * 100);
                    }
                    p.sendYellowMessage(ItemData.ItemDataId(p.nj.ItemBST[checkID].id).name + " đã được nâng cấp.");
                }
                break;
            }
            case 775: // Hoa Tuyết
                if (p.nj.ItemCaiTrang[0] == null) {
                    if (p.nj.quantityItemyTotal(775) >= 1000) {
                        p.nj.removeItemBags(775, 1000);
                        Item it = ItemData.itemDefault(774);
                        p.nj.ItemCaiTrang[0] = it;
                        p.nj.ItemCaiTrang[0].setUpgrade(1);
                        p.nj.ItemCaiTrang[0].setLock(true);
                        p.sendYellowMessage(ItemData.ItemDataId(p.nj.ItemCaiTrang[0].id).name + " đã được thêm vào bộ sưu tập.");
                        return;
                    } else {
                        p.sendYellowMessage("Bạn chưa đủ 1000 " + ItemData.ItemDataId(789).name + " đổi cải trang");
                        return;
                    }
                } else {
                    if (p.nj.ItemCaiTrang[0].getUpgrade() >= 10) {
                        p.sendYellowMessage("Cải trang đã đặt cấp tối đa");
                        return;
                    }
                    if (p.nj.quantityItemyTotal(775) < (1000 * (p.nj.ItemCaiTrang[0].upgrade + 1))) {
                        p.sendYellowMessage("Bạn chưa đủ " + (1000 * (p.nj.ItemCaiTrang[0].upgrade + 1)) + " " + ItemData.ItemDataId(775).name + " để nâng cấp");
                        return;
                    }
                    p.nj.ItemCaiTrang[0].upgradeCaiTrangNext((byte) 1);
                    p.nj.removeItemBags(775, (1000 * (p.nj.ItemCaiTrang[0].upgrade + 1)));
                    p.sendYellowMessage(ItemData.ItemDataId(p.nj.ItemCaiTrang[0].id).name + " đã được nâng cấp.");
                }
                break;
            case 788: // Sumimura
                if (p.nj.ItemCaiTrang[1] == null) {
                    if (p.nj.quantityItemyTotal(788) >= 1000) {
                        p.nj.removeItemBags(788, 1000);
                        Item it = ItemData.itemDefault(786);
                        p.nj.ItemCaiTrang[1] = it;
                        p.nj.ItemCaiTrang[1].setUpgrade(1);
                        p.nj.ItemCaiTrang[1].setLock(true);
                        p.sendYellowMessage(ItemData.ItemDataId(p.nj.ItemCaiTrang[1].id).name + " đã được thêm vào bộ sưu tập.");
                        return;
                    } else {
                        p.sendYellowMessage("Bạn chưa đủ 1000 " + ItemData.ItemDataId(786).name + " đổi cải trang");
                        return;
                    }
                } else {
                    if (p.nj.ItemCaiTrang[1].getUpgrade() >= 10) {
                        p.sendYellowMessage("Cải trang đã đặt cấp tối đa");
                        return;
                    }
                    if (p.nj.quantityItemyTotal(788) < (1000 * (p.nj.ItemCaiTrang[1].upgrade + 1))) {
                        p.sendYellowMessage("Bạn chưa đủ " + (1000 * (p.nj.ItemCaiTrang[1].upgrade + 1)) + " " + ItemData.ItemDataId(788).name + " để nâng cấp");
                        return;
                    }
                    p.nj.ItemCaiTrang[1].upgradeCaiTrangNext((byte) 1);
                    p.nj.removeItemBags(788, 1000 * (p.nj.ItemCaiTrang[1].upgrade + 1));
                    p.sendYellowMessage(ItemData.ItemDataId(p.nj.ItemCaiTrang[1].id).name + " đã được nâng cấp.");
                    break;
                }
            case 789: // Yukimura
                if (p.nj.ItemCaiTrang[2] == null) {
                    if (p.nj.quantityItemyTotal(789) >= 1000) {
                        p.nj.removeItemBags(789, 1000);
                        Item it = ItemData.itemDefault(787);
                        p.nj.ItemCaiTrang[2] = it;
                        p.nj.ItemCaiTrang[2].setUpgrade(1);
                        p.nj.ItemCaiTrang[2].setLock(true);
                        p.sendYellowMessage(ItemData.ItemDataId(p.nj.ItemCaiTrang[2].id).name + " đã được thêm vào bộ sưu tập.");
                        return;
                    } else {
                        p.sendYellowMessage("Bạn chưa đủ 1000 " + ItemData.ItemDataId(789).name + " đổi cải trang");
                        return;
                    }
                } else {
                    if (p.nj.ItemCaiTrang[2].getUpgrade() >= 10) {
                        p.sendYellowMessage("Cải trang đã đặt cấp tối đa");
                        return;
                    }
                    if (p.nj.quantityItemyTotal(789) < (1000 * (p.nj.ItemCaiTrang[2].upgrade + 1))) {
                        p.sendYellowMessage("Bạn chưa đủ " + (1000 * (p.nj.ItemCaiTrang[2].upgrade + 1)) + " " + ItemData.ItemDataId(789).name + " để nâng cấp");
                        return;
                    }
                    p.nj.ItemCaiTrang[2].upgradeCaiTrangNext((byte) 1);
                    p.nj.removeItemBags(789, (1000 * (p.nj.ItemCaiTrang[2].upgrade + 1)));
                    p.sendYellowMessage(ItemData.ItemDataId(p.nj.ItemCaiTrang[2].id).name + " đã được nâng cấp.");
                    break;
                }

            case 222:
            case 223:
            case 224:
            case 225:
            case 226:
            case 227:
            case 228:
                if ((p.nj.quantityItemyTotal(222) < 1) || (p.nj.quantityItemyTotal(223) < 1) || (p.nj.quantityItemyTotal(224) < 1) || (p.nj.quantityItemyTotal(225) < 1) || (p.nj.quantityItemyTotal(226) < 1) || (p.nj.quantityItemyTotal(227) < 1) || (p.nj.quantityItemyTotal(228) < 1)) {
                    p.sendYellowMessage("Chưa sưu tầm đủ 7 viên ngọc rồng");
                } else if (p.nj.getAvailableBag() == 0) {
                    p.sendYellowMessage("Hành trang không đủ chỗ trống");

                } else {
                    p.nj.removeItemBags(222, 1);
                    p.nj.removeItemBags(223, 1);
                    p.nj.removeItemBags(224, 1);
                    p.nj.removeItemBags(225, 1);
                    p.nj.removeItemBags(226, 1);
                    p.nj.removeItemBags(227, 1);
                    p.nj.removeItemBags(228, 1);

                    Message m = new Message(-30);
                    m.writer().writeByte(-58);
                    m.writer().writeInt(p.nj.get().id);
                    m.writer().flush();
                    p.session.sendMessage(m);
                    m.cleanup();

                    Message m2 = new Message(-30);
                    m2.writer().writeByte(-57);
                    m2.writer().flush();
                    p.nj.getPlace().sendMessage(m2);
                    m2.cleanup();
                    Item itemup = ItemData.itemDefault(420);
                    if (p.nj.get().nclass == 3 || p.nj.get().nclass == 4) {
                        itemup = ItemData.itemDefault(421);
                    } else if (p.nj.get().nclass == 5 || p.nj.get().nclass == 6) {
                        itemup = ItemData.itemDefault(422);
                    }
                    itemup.isLock = true;
                    p.nj.addItemBag(false, itemup);
                    break;
                }
                break;
            
            case 664: {//long den
                Item itemup;
                int a = util.nextInt(200);
                if (a < 60) {
                    p.updateExp(1000000, false);
                } else if (a >= 60 && a < 100) {
                    p.nj.upyenMessage(10000);
                } else if (a == 100) {
                    final short[] arId = {9,10,11,443,535,536,799,485,524,308,309,798,539,540,284,285,490,491,567,383,407,408,397,398,399,400,401,402,38,569};
                    short idI = arId[util.nextInt(arId.length)];
                    itemup = ItemData.itemDefault(idI);
                    p.nj.addItemBag(true, itemup);
                    if (idI == 383 || idI == 384 || idI == 385 || idI == 308 || idI == 309 || idI == 535 || idI == 524 || idI == 799 || idI == 11 || idI == 10 || idI == 536 || idI == 798 || idI == 832 || idI == 830) {
                        Manager.chatKTG(p.nj.name + " đã may mắn thả lồng đèn nhận được " + ItemData.ItemDataId(idI).name);
                    }
                } else {
                    final short[] arId = {3,4,5,6,7,449,450,451,452,453,449,450,451,452,453,
                        8,9,10,11,449,450,451,452,453,30,249,250,449,450,451,452,453,3,4,5,6,7,275,276,3,4,5,6,7,277,3,4,5,6,7,278,3,4,5,6,7,283,3,4,5,6,7,375,3,4,5,6,7,376,377,3,4,5,6,7,378,449,450,451,452,453,449,450,451,452,453,379,3,4,449,450,451,452,453,449,450,451,449,450,451,452,453,449,450,451,452,453,452,453,5,6,7,380,409,3,4,5,6,7,3,4,5,6,7,410,436,3,4,5,6,7,3,4,5,6,7,437,438,3,4,5,6,7,449,450,451,452,453,449,450,451,452,453,3,4,5,6,7,3,4,5,6,7,3,4,449,450,451,452,453,449,450,451,452,453,5,6,7,449,450,451,3,4,5,6,7,3,4,5,6,7,452,453,3,4,5,6,7,3,4,5,6,7,454,3,4,5,6,7,3,4,5,6,7,3,4,5,6,7,3,4,5,6,7,545,567,3,4,5,6,7,3,4,5,6,7,568,3,4,5,6,7,3,4,5,6,7,570,571,3,4,5,6,7,3,4,5,6,7,573,574,575,3,4,5,6,7,3,4,5,6,7,576,577,3,4,5,6,7,449,450,451,452,453,449,450,451,452,453,3,4,5,6,449,450,451,452,453,449,450,451,452,453,7,578,695,696,3,4,5,449,450,451,452,453,449,450,451,452,453,6,7,3,4,5,6,7,3,4,5,6,7,3,4,5,6,7,775,3,4,5,6,7,3,4,5,6,7,778,779,3,4,5,6,7,3,4,5,6,7,788,789};
                    short idI = arId[util.nextInt(arId.length)];
                    itemup = ItemData.itemDefault(idI);
                    p.nj.addItemBag(true, itemup);
                }
                p.nj.removeItemBag(index, 1);
                Service.sendEffectAuto(p, (byte) 7, (int) p.nj.x, (int) p.nj.y, (byte) 1, (short) 1);
                break;
            }
            case 675: {//phao
                Item itemup;
                int a = util.nextInt(200);
                if (a < 60) {
                    p.updateExp(1000000, false);
                } else if (a >= 60 && a < 100) {
                    p.nj.upyenMessage(10000);
                } else if (a == 100) {
                    final short[] arId = {9,10,11,443,535,536,799,485,524,798,539,540,284,285,490,491,567,383,407,408,397,398,399,400,401,402,38,569};
                    short idI = arId[util.nextInt(arId.length)];
                    itemup = ItemData.itemDefault(idI);
                    p.nj.addItemBag(true, itemup);
                    if (idI == 383 || idI == 384 || idI == 385 || idI == 443 || idI == 485 || idI == 535 || idI == 524 || idI == 799 || idI == 11 || idI == 10 || idI == 536 || idI == 798 || idI == 832 || idI == 830) {
                        Manager.chatKTG(p.nj.name + " đã may mắn đốt pháo nhận được " + ItemData.ItemDataId(idI).name);
                    }
                } else {
                    final short[] arId = {3,4,5,6,7,449,450,451,452,453,449,450,451,452,453,
                        8,9,10,11,449,450,451,452,453,30,249,250,449,450,451,452,453,3,4,5,6,7,275,276,3,4,5,6,7,277,3,4,5,6,7,278,3,4,5,6,7,283,3,4,5,6,7,375,3,4,5,6,7,376,377,3,4,5,6,7,378,449,450,451,452,453,449,450,451,452,453,379,3,4,449,450,451,452,453,449,450,451,449,450,451,452,453,449,450,451,452,453,452,453,5,6,7,380,409,3,4,5,6,7,3,4,5,6,7,410,436,3,4,5,6,7,3,4,5,6,7,437,438,3,4,5,6,7,449,450,451,452,453,449,450,451,452,453,3,4,5,6,7,3,4,5,6,7,3,4,449,450,451,452,453,449,450,451,452,453,5,6,7,449,450,451,3,4,5,6,7,3,4,5,6,7,452,453,3,4,5,6,7,3,4,5,6,7,454,3,4,5,6,7,3,4,5,6,7,3,4,5,6,7,3,4,5,6,7,545,567,3,4,5,6,7,3,4,5,6,7,568,3,4,5,6,7,3,4,5,6,7,570,571,3,4,5,6,7,3,4,5,6,7,573,574,575,3,4,5,6,7,3,4,5,6,7,576,577,3,4,5,6,7,449,450,451,452,453,449,450,451,452,453,3,4,5,6,449,450,451,452,453,449,450,451,452,453,7,578,695,696,3,4,5,449,450,451,452,453,449,450,451,452,453,6,7,3,4,5,6,7,3,4,5,6,7,3,4,5,6,7,775,3,4,5,6,7,3,4,5,6,7,778,779,3,4,5,6,7,3,4,5,6,7,788,789};
                    short idI = arId[util.nextInt(arId.length)];
                    itemup = ItemData.itemDefault(idI);
                    p.nj.addItemBag(true, itemup);
                }
                p.nj.removeItemBag(index, 1);
            //    p.nj.diemsk1 += 1;
                Service.sendEffectAuto(p, (byte) 9, (int) p.nj.x, (int) p.nj.y, (byte) 1, (short) 1);
                break;
            }
            
            case 2222: {
                        if (server.manager.EVENT != 2) {
                            p.sendYellowMessage(Language.END_EVENT);
                            return;
                        }
                        if (numbagnull == 0) {
                            p.sendYellowMessage("Hành trang không đủ chỗ trống.");
                            return;
                        }
                        if (p.nj.getLevel() < 20) {
                            p.sendYellowMessage("Trình độ của bạn không đủ để sử dụng vật phẩm này.");
                            return;
                        }
                        p.nj.removeItemBag(index, 1);

                        int perRuong = util.nextInt(10000);
                        int rhb = util.nextInt(25000);
                        if (util.percent(100, 30)) {
                            p.updateExp(5000000L, false);
                            return;
                        }
                        else if (rhb == 14999) {
                            Item itemUp = new Item();
                            itemUp.id = 385;
                            itemUp.quantity = 1;
                            itemUp.isExpires = false;
                            itemUp.isLock = false;
                            p.nj.addItemBag(false, itemUp);
                            Manager.chatKTG("Chúc mừng " + p.nj.name + " đã nhận được " + ItemData.ItemDataId(itemUp.id).name + " khi sử dụng " + ItemData.ItemDataId(item.id).name);
                            return;
                        }
                        else if (perRuong == 0) {
                            Item itemUp = new Item();
                            itemUp.id = 384;
                            itemUp.quantity = 1;
                            itemUp.isExpires = false;
                            itemUp.isLock = false;
                            p.nj.addItemBag(false, itemUp);
                            Manager.chatKTG("Chúc mừng " + p.nj.name + " đã nhận được " + ItemData.ItemDataId(itemUp.id).name + " khi sử dụng " + ItemData.ItemDataId(item.id).name);
                            return;
                        }
                        if (util.percent(150, 1)) {
                            Item itemUp = ItemData.itemDefault(443);
                            itemUp.quantity = 1;
                            itemUp.isExpires = false;
                            itemUp.expires = -1;
                            itemUp.isLock = false;
                            p.nj.addItemBag(false, itemUp);
                            return;
                        }
                        if (util.percent(400, 1)) {
                            Item itemUp = new Item();
                            itemUp.id = 567;
                            itemUp.quantity = 1;
                            itemUp.isExpires = false;
                            itemUp.isLock = false;
                            p.nj.addItemBag(false, itemUp);
                            
                        }
                        else if (util.percent(100, 1)) {
                            Item itemup = ItemData.itemDefault(util.nextInt(654, 655));
                            itemup.isLock = false;
                            p.nj.addItemBag(false, itemup);
                            return;
                        }
                        else if (util.percent(10, 1)) {
                            Item itemup = ItemData.itemDefault(675);
                            itemup.quantity = 1;
                            itemup.isLock = false;
                            p.nj.addItemBag(false, itemup);
                            return;
                        }
                        else {
                            short idI = idItemBanhChocolate[util.nextInt(idItemBanhChocolate.length)];
                            Item itemup = ItemData.itemDefault(idI);
                            itemup.isLock = item.isLock;
                            if (ItemData.ItemDataId(itemup.id).type == 10 || itemup.id == 523 || itemup.id == 804 || itemup.id == 805 || itemup.id == 799 || itemup.id == 800 ) {
                                itemup.isExpires = true;
                                itemup.expires = System.currentTimeMillis() + 604800000L;
                            }
                            p.nj.addItemBag(true, itemup);
                        }
                        break;
                    }
            
            case 22223: {
                        if (server.manager.EVENT != 2) {
                            p.sendYellowMessage(Language.END_EVENT);
                            return;
                        }
                        if (numbagnull == 0) {
                            p.sendYellowMessage("Hành trang không đủ chỗ trống.");
                            return;
                        }
                        if (p.nj.getLevel() < 20) {
                            p.sendYellowMessage("Trình độ của bạn không đủ để sử dụng vật phẩm này.");
                            return;
                        }
                        p.nj.topthiep++;
                        p.nj.removeItemBag(index, 1);

                        int perRuong = util.nextInt(10000);
                        int rhb = util.nextInt(25000);
                        if (util.percent(100, 30)) {
                            p.updateExp(10000000L, false);
                            return;
                        }
                        else if (rhb == 14999) {
                            Item itemUp = new Item();
                            itemUp.id = 385;
                            itemUp.quantity = 1;
                            itemUp.isExpires = false;
                            itemUp.isLock = false;
                            p.nj.addItemBag(false, itemUp);
                            Manager.chatKTG("Chúc mừng " + p.nj.name + " đã nhận được " + ItemData.ItemDataId(itemUp.id).name + " khi sử dụng " + ItemData.ItemDataId(item.id).name);
                            return;
                        }
                        else if (perRuong == 0) {
                            Item itemUp = new Item();
                            itemUp.id = 384;
                            itemUp.quantity = 1;
                            itemUp.isExpires = false;
                            itemUp.isLock = false;
                            p.nj.addItemBag(false, itemUp);
                            Manager.chatKTG("Chúc mừng " + p.nj.name + " đã nhận được " + ItemData.ItemDataId(itemUp.id).name + " khi sử dụng " + ItemData.ItemDataId(item.id).name);
                            return;
                        }
                        if (util.percent(150, 1)) {
                            Item itemUp = ItemData.itemDefault(443);
                            itemUp.quantity = 1;
                            itemUp.isExpires = false;
                            itemUp.expires = -1;
                            itemUp.isLock = false;
                            p.nj.addItemBag(false, itemUp);
                            return;
                        }
//                        else if (util.percent(10, 1)) {
//                            Item itemup = ItemData.itemDefault(675);
//                            itemup.quantity = 1;
//                            itemup.isLock = false;
//                            p.nj.addItemBag(false, itemup);
//                            return;
//                        }
                        if (util.percent(400, 1)) {
                            Item itemUp = new Item();
                            itemUp.id = 567;
                            itemUp.quantity = 1;
                            itemUp.isExpires = false;
                            itemUp.isLock = false;
                            p.nj.addItemBag(false, itemUp);
                            
                        }
                        else if (util.percent(100, 1)) {
                            Item itemup = ItemData.itemDefault(util.nextInt(652, 655));
                            itemup.isLock = false;
                            p.nj.addItemBag(false, itemup);
                            return;
                        }
                        else {
                            short idI = idItemBanhChocolate[util.nextInt(idItemBanhChocolate.length)];
                            Item itemup = ItemData.itemDefault(idI);
                            itemup.isLock = item.isLock;
                            if (ItemData.ItemDataId(itemup.id).type == 10 || itemup.id == 523 || itemup.id == 804 || itemup.id == 805 || itemup.id == 799 || itemup.id == 800 ) {
                                itemup.isExpires = true;
                                itemup.expires = System.currentTimeMillis() + 604800000L;
                            }
                            p.nj.addItemBag(true, itemup);
                        }
                        break;
                    }
            
            case 707: {
                        if (server.manager.EVENT != 2) {
                            p.sendYellowMessage(Language.END_EVENT);
                            return;
                        }
                        if (numbagnull == 0) {
                            p.sendYellowMessage("Hành trang không đủ chỗ trống.");
                            return;
                        }
                        if (p.nj.getLevel() < 10) {
                            p.sendYellowMessage("Trình độ của bạn không đủ để sử dụng vật phẩm này.");
                            return;
                        }
                        p.nj.removeItemBag(index, 1);

                        int perRuong = util.nextInt(10000);
                        int rhb = util.nextInt(25000);
                        if (util.percent(100, 30)) {
                            p.updateExp(5000000L, false);
                            return;
                        }
                        else if (rhb == 14999) {
                            Item itemUp = new Item();
                            itemUp.id = 385;
                            itemUp.quantity = 1;
                            itemUp.isExpires = false;
                            itemUp.isLock = false;
                            p.nj.addItemBag(false, itemUp);
                            Manager.chatKTG("Chúc mừng " + p.nj.name + " đã nhận được " + ItemData.ItemDataId(itemUp.id).name + " khi sử dụng " + ItemData.ItemDataId(item.id).name);
                            return;
                        }
                        else if (perRuong == 0) {
                            Item itemUp = new Item();
                            itemUp.id = 384;
                            itemUp.quantity = 1;
                            itemUp.isExpires = false;
                            itemUp.isLock = false;
                            p.nj.addItemBag(false, itemUp);
                            Manager.chatKTG("Chúc mừng " + p.nj.name + " đã nhận được " + ItemData.ItemDataId(itemUp.id).name + " khi sử dụng " + ItemData.ItemDataId(item.id).name);
                            return;
                        }
                        if (util.percent(150, 1)) {
                            Item itemUp = ItemData.itemDefault(443);
                            itemUp.quantity = 1;
                            itemUp.isExpires = false;
                            itemUp.expires = -1;
                            itemUp.isLock = false;
                            p.nj.addItemBag(false, itemUp);
                            return;
                        }
                        if (util.percent(400, 1)) {
                            Item itemUp = new Item();
                            itemUp.id = 567;
                            itemUp.quantity = 1;
                            itemUp.isExpires = false;
                            itemUp.isLock = false;
                            p.nj.addItemBag(false, itemUp);
                            
                        }
                        else if (util.percent(100, 1)) {
                            Item itemup = ItemData.itemDefault(util.nextInt(652, 655));
                            itemup.isLock = false;
                            p.nj.addItemBag(false, itemup);
                            return;
                        }
                        else {
                            short idI = idItemBanhChocolate[util.nextInt(idItemBanhChocolate.length)];
                            Item itemup = ItemData.itemDefault(idI);
                            itemup.isLock = item.isLock;
                            if (ItemData.ItemDataId(itemup.id).type == 10 || itemup.id == 523 || itemup.id == 804 || itemup.id == 805 || itemup.id == 799 || itemup.id == 800 ) {
                                itemup.isExpires = true;
                                itemup.expires = System.currentTimeMillis() + 604800000L;
                            }
                            p.nj.addItemBag(true, itemup);
                        }
                        break;
                    }
            
            case 592: {
                        if (server.manager.EVENT != 1) {
                            p.sendYellowMessage(Language.END_EVENT);
                            return;
                        }
                        if (numbagnull == 0) {
                            p.sendYellowMessage("Hành trang không đủ chỗ trống.");
                            return;
                        }
                        if (p.nj.getLevel() < 10) {
                            p.sendYellowMessage("Trình độ của bạn không đủ để sử dụng vật phẩm này.");
                            return;
                        }
                        p.nj.removeItemBag(index, 1);

                        int perRuong = util.nextInt(10000);
                        int rhb = util.nextInt(25000);
                        if (util.percent(100, 30)) {
                            p.updateExp(5000000L, false);
                            return;
                        }
                        else if (rhb == 14999) {
                            Item itemUp = new Item();
                            itemUp.id = 385;
                            itemUp.quantity = 1;
                            itemUp.isExpires = false;
                            itemUp.isLock = false;
                            p.nj.addItemBag(false, itemUp);
                            Manager.chatKTG("Chúc mừng " + p.nj.name + " đã nhận được " + ItemData.ItemDataId(itemUp.id).name + " khi sử dụng " + ItemData.ItemDataId(item.id).name);
                            return;
                        }
                        else if (perRuong == 0) {
                            Item itemUp = new Item();
                            itemUp.id = 384;
                            itemUp.quantity = 1;
                            itemUp.isExpires = false;
                            itemUp.isLock = false;
                            p.nj.addItemBag(false, itemUp);
                            Manager.chatKTG("Chúc mừng " + p.nj.name + " đã nhận được " + ItemData.ItemDataId(itemUp.id).name + " khi sử dụng " + ItemData.ItemDataId(item.id).name);
                            return;
                        }
                        if (util.percent(150, 1)) {
                            Item itemUp = ItemData.itemDefault(443);
                            itemUp.quantity = 1;
                            itemUp.isExpires = false;
                            itemUp.expires = -1;
                            itemUp.isLock = false;
                            p.nj.addItemBag(false, itemUp);
                            return;
                        }
                        if (util.percent(400, 1)) {
                            Item itemUp = new Item();
                            itemUp.id = 567;
                            itemUp.quantity = 1;
                            itemUp.isExpires = false;
                            itemUp.isLock = false;
                            p.nj.addItemBag(false, itemUp);
                            
                        }
                        else if (util.percent(100, 1)) {
                            Item itemup = ItemData.itemDefault(util.nextInt(652, 655));
                            itemup.isLock = false;
                            p.nj.addItemBag(false, itemup);
                            return;
                        }
                        else {
                            short idI = idItemBanhChocolate[util.nextInt(idItemBanhChocolate.length)];
                            Item itemup = ItemData.itemDefault(idI);
                            itemup.isLock = item.isLock;
                            if (ItemData.ItemDataId(itemup.id).type == 10 || itemup.id == 523 || itemup.id == 804 || itemup.id == 805 || itemup.id == 799 || itemup.id == 800 ) {
                                itemup.isExpires = true;
                                itemup.expires = System.currentTimeMillis() + 604800000L;
                            }
                            p.nj.addItemBag(true, itemup);
                        }
                        break;
                    }
            
            case 708: {
                        if (server.manager.EVENT != 2) {
                            p.sendYellowMessage(Language.END_EVENT);
                            return;
                        }
                        if (numbagnull == 0) {
                            p.sendYellowMessage("Hành trang không đủ chỗ trống.");
                            return;
                        }
                        if (p.nj.getLevel() < 10) {
                            p.sendYellowMessage("Trình độ của bạn không đủ để sử dụng vật phẩm này.");
                            return;
                        }
                        p.nj.topmanh++;
                        p.nj.removeItemBag(index, 1);

                        int perRuong = util.nextInt(10000);
                        int rhb = util.nextInt(25000);
                        if (util.percent(100, 30)) {
                            p.updateExp(10000000L, false);
                            return;
                        }
                        else if (rhb == 14999) {
                            Item itemUp = new Item();
                            itemUp.id = 385;
                            itemUp.quantity = 1;
                            itemUp.isExpires = false;
                            itemUp.isLock = false;
                            p.nj.addItemBag(false, itemUp);
                            Manager.chatKTG("Chúc mừng " + p.nj.name + " đã nhận được " + ItemData.ItemDataId(itemUp.id).name + " khi sử dụng " + ItemData.ItemDataId(item.id).name);
                            return;
                        }
                        else if (perRuong == 0) {
                            Item itemUp = new Item();
                            itemUp.id = 384;
                            itemUp.quantity = 1;
                            itemUp.isExpires = false;
                            itemUp.isLock = false;
                            p.nj.addItemBag(false, itemUp);
                            Manager.chatKTG("Chúc mừng " + p.nj.name + " đã nhận được " + ItemData.ItemDataId(itemUp.id).name + " khi sử dụng " + ItemData.ItemDataId(item.id).name);
                            return;
                        }
                        if (util.percent(150, 1)) {
                            Item itemUp = ItemData.itemDefault(443);
                            itemUp.quantity = 1;
                            itemUp.isExpires = false;
                            itemUp.expires = -1;
                            itemUp.isLock = false;
                            p.nj.addItemBag(false, itemUp);
                            return;
                        }
                        if (util.percent(400, 1)) {
                            Item itemUp = new Item();
                            itemUp.id = 567;
                            itemUp.quantity = 1;
                            itemUp.isExpires = false;
                            itemUp.isLock = false;
                            p.nj.addItemBag(false, itemUp);
                            
                        }
                        else if (util.percent(100, 1)) {
                            Item itemup = ItemData.itemDefault(util.nextInt(652
                                    , 655));
                            itemup.isLock = false;
                            p.nj.addItemBag(false, itemup);
                            return;
                        }
                        else {
                            short idI = idItemBanhChocolate[util.nextInt(idItemBanhChocolate.length)];
                            Item itemup = ItemData.itemDefault(idI);
                            itemup.isLock = item.isLock;
                            if (ItemData.ItemDataId(itemup.id).type == 10 || itemup.id == 523 || itemup.id == 804 || itemup.id == 805 || itemup.id == 799 || itemup.id == 800 ) {
                                itemup.isExpires = true;
                                itemup.expires = System.currentTimeMillis() + 604800000L;
                            }
                            p.nj.addItemBag(true, itemup);
                        }
                        break;
                    }
            
            case 593: {
                        if (server.manager.EVENT != 1) {
                            p.sendYellowMessage(Language.END_EVENT);
                            return;
                        }
                        if (numbagnull == 0) {
                            p.sendYellowMessage("Hành trang không đủ chỗ trống.");
                            return;
                        }
                        if (p.nj.getLevel() < 10) {
                            p.sendYellowMessage("Trình độ của bạn không đủ để sử dụng vật phẩm này.");
                            return;
                        }
                        p.nj.topthiep++;
                        p.nj.removeItemBag(index, 1);

                        int perRuong = util.nextInt(10000);
                        int rhb = util.nextInt(25000);
                        if (util.percent(100, 30)) {
                            p.updateExp(10000000L, false);
                            return;
                        }
                        else if (rhb == 14999) {
                            Item itemUp = new Item();
                            itemUp.id = 385;
                            itemUp.quantity = 1;
                            itemUp.isExpires = false;
                            itemUp.isLock = false;
                            p.nj.addItemBag(false, itemUp);
                            Manager.chatKTG("Chúc mừng " + p.nj.name + " đã nhận được " + ItemData.ItemDataId(itemUp.id).name + " khi sử dụng " + ItemData.ItemDataId(item.id).name);
                            return;
                        }
                        else if (perRuong == 0) {
                            Item itemUp = new Item();
                            itemUp.id = 384;
                            itemUp.quantity = 1;
                            itemUp.isExpires = false;
                            itemUp.isLock = false;
                            p.nj.addItemBag(false, itemUp);
                            Manager.chatKTG("Chúc mừng " + p.nj.name + " đã nhận được " + ItemData.ItemDataId(itemUp.id).name + " khi sử dụng " + ItemData.ItemDataId(item.id).name);
                            return;
                        }
                        if (util.percent(150, 1)) {
                            Item itemUp = ItemData.itemDefault(443);
                            itemUp.quantity = 1;
                            itemUp.isExpires = false;
                            itemUp.expires = -1;
                            itemUp.isLock = false;
                            p.nj.addItemBag(false, itemUp);
                            return;
                        }
                        if (util.percent(400, 1)) {
                            Item itemUp = new Item();
                            itemUp.id = 567;
                            itemUp.quantity = 1;
                            itemUp.isExpires = false;
                            itemUp.isLock = false;
                            p.nj.addItemBag(false, itemUp);
                            
                        }
                        else if (util.percent(100, 1)) {
                            Item itemup = ItemData.itemDefault(util.nextInt(652
                                    , 655));
                            itemup.isLock = false;
                            p.nj.addItemBag(false, itemup);
                            return;
                        }
                        else {
                            short idI = idItemBanhChocolate[util.nextInt(idItemBanhChocolate.length)];
                            Item itemup = ItemData.itemDefault(idI);
                            itemup.isLock = item.isLock;
                            if (ItemData.ItemDataId(itemup.id).type == 10 || itemup.id == 523 || itemup.id == 804 || itemup.id == 805 || itemup.id == 799 || itemup.id == 800 ) {
                                itemup.isExpires = true;
                                itemup.expires = System.currentTimeMillis() + 604800000L;
                            }
                            p.nj.addItemBag(true, itemup);
                        }
                        break;
                    }
            
             case 678: {
                        if (server.manager.EVENT != 2) {
                            p.sendYellowMessage(Language.END_EVENT);
                            return;
                        }
                        if (numbagnull == 0) {
                            p.sendYellowMessage("Hành trang không đủ chỗ trống.");
                            return;
                        }
                        if (p.nj.getLevel() < 20) {
                            p.sendYellowMessage("Trình độ của bạn không đủ để sử dụng vật phẩm này.");
                            return;
                        }
                        p.nj.removeItemBag(index, 1);

                        int perRuong = util.nextInt(10000);
                        int rhb = util.nextInt(25000);
                        if (util.percent(100, 30)) {
                            p.updateExp(3000000L, false);
                            return;
                        }
                        else if (rhb == 14999) {
                            Item itemUp = new Item();
                            itemUp.id = 385;
                            itemUp.quantity = 1;
                            itemUp.isExpires = false;
                            itemUp.isLock = false;
                            p.nj.addItemBag(false, itemUp);
                            Manager.chatKTG("Chúc mừng " + p.nj.name + " đã nhận được " + ItemData.ItemDataId(itemUp.id).name + " khi sử dụng " + ItemData.ItemDataId(item.id).name);
                            return;
                        }
                        else if (perRuong == 0) {
                            Item itemUp = new Item();
                            itemUp.id = 384;
                            itemUp.quantity = 1;
                            itemUp.isExpires = false;
                            itemUp.isLock = false;
                            p.nj.addItemBag(false, itemUp);
                            Manager.chatKTG("Chúc mừng " + p.nj.name + " đã nhận được " + ItemData.ItemDataId(itemUp.id).name + " khi sử dụng " + ItemData.ItemDataId(item.id).name);
                            return;
                        }
                        if (util.percent(150, 1)) {
                            Item itemUp = ItemData.itemDefault(443);
                            itemUp.quantity = 1;
                            itemUp.isExpires = false;
                            itemUp.expires = -1;
                            itemUp.isLock = false;
                            p.nj.addItemBag(false, itemUp);
                            return;
                        }
                        if (util.percent(400, 1)) {
                            Item itemUp = new Item();
                            itemUp.id = 567;
                            itemUp.quantity = 1;
                            itemUp.isExpires = false;
                            itemUp.isLock = false;
                            p.nj.addItemBag(false, itemUp);
                            
                        }
                        else if (util.percent(100, 1)) {
                            Item itemup = ItemData.itemDefault(util.nextInt(654, 655));
                            itemup.isLock = false;
                            p.nj.addItemBag(false, itemup);
                            return;
                        }
                        else {
                            short idI = idItemBanhChocolate[util.nextInt(idItemBanhChocolate.length)];
                            Item itemup = ItemData.itemDefault(idI);
                            itemup.isLock = item.isLock;
                            if (ItemData.ItemDataId(itemup.id).type == 10 || itemup.id == 523 || itemup.id == 804 || itemup.id == 805 || itemup.id == 799 || itemup.id == 800 ) {
                                itemup.isExpires = true;
                                itemup.expires = System.currentTimeMillis() + 604800000L;
                            }
                            p.nj.addItemBag(true, itemup);
                        }
                        break;
                    }
            
            case 679: {
                        if (server.manager.EVENT != 2) {
                            p.sendYellowMessage(Language.END_EVENT);
                            return;
                        }
                        if (numbagnull == 0) {
                            p.sendYellowMessage("Hành trang không đủ chỗ trống.");
                            return;
                        }
                        if (p.nj.getLevel() < 20) {
                            p.sendYellowMessage("Trình độ của bạn không đủ để sử dụng vật phẩm này.");
                            return;
                        }
                        p.nj.topco4la++;
                        p.nj.removeItemBag(index, 1);

                        int perRuong = util.nextInt(10000);
                        int rhb = util.nextInt(25000);
                        if (util.percent(100, 30)) {
                            p.updateExp(3000000L, false);
                            return;
                        }
                        else if (rhb == 14999) {
                            Item itemUp = new Item();
                            itemUp.id = 385;
                            itemUp.quantity = 1;
                            itemUp.isExpires = false;
                            itemUp.isLock = false;
                            p.nj.addItemBag(false, itemUp);
                            Manager.chatKTG("Chúc mừng " + p.nj.name + " đã nhận được " + ItemData.ItemDataId(itemUp.id).name + " khi sử dụng " + ItemData.ItemDataId(item.id).name);
                            return;
                        }
                        else if (rhb == 15000) {
                            Item itemUp = new Item();
                            itemUp.id = 795;
                            itemUp.quantity = 1;
                            itemUp.isExpires = false;
                            itemUp.isLock = false;
                            p.nj.addItemBag(false, itemUp);
                            Manager.chatKTG("Chúc mừng " + p.nj.name + " đã nhận được " + ItemData.ItemDataId(itemUp.id).name + " khi sử dụng " + ItemData.ItemDataId(item.id).name);
                            return;
                        }
                        else if (rhb == 15000) {
                            Item itemUp = new Item();
                            itemUp.id = 796;
                            itemUp.quantity = 1;
                            itemUp.isExpires = false;
                            itemUp.isLock = false;
                            p.nj.addItemBag(false, itemUp);
                            Manager.chatKTG("Chúc mừng " + p.nj.name + " đã nhận được " + ItemData.ItemDataId(itemUp.id).name + " khi sử dụng " + ItemData.ItemDataId(item.id).name);
                            return;
                        }
                        else if (rhb == 15000) {
                            Item itemUp = new Item();
                            itemUp.id = 799;
                            itemUp.quantity = 1;
                            itemUp.isExpires = false;
                            itemUp.isLock = false;
                            p.nj.addItemBag(false, itemUp);
                            Manager.chatKTG("Chúc mừng " + p.nj.name + " đã nhận được " + ItemData.ItemDataId(itemUp.id).name + " khi sử dụng " + ItemData.ItemDataId(item.id).name);
                            return;
                        }
                        else if (rhb == 15000) {
                            Item itemUp = new Item();
                            itemUp.id = 800;
                            itemUp.quantity = 1;
                            itemUp.isExpires = false;
                            itemUp.isLock = false;
                            p.nj.addItemBag(false, itemUp);
                            Manager.chatKTG("Chúc mừng " + p.nj.name + " đã nhận được " + ItemData.ItemDataId(itemUp.id).name + " khi sử dụng " + ItemData.ItemDataId(item.id).name);
                            return;
                        }
                        else if (perRuong == 0) {
                            Item itemUp = new Item();
                            itemUp.id = 384;
                            itemUp.quantity = 1;
                            itemUp.isExpires = false;
                            itemUp.isLock = false;
                            p.nj.addItemBag(false, itemUp);
                            Manager.chatKTG("Chúc mừng " + p.nj.name + " đã nhận được " + ItemData.ItemDataId(itemUp.id).name + " khi sử dụng " + ItemData.ItemDataId(item.id).name);
                            return;
                        }
                        if (util.percent(150, 1)) {
                            Item itemUp = ItemData.itemDefault(443);
                            itemUp.quantity = 1;
                            itemUp.isExpires = false;
                            itemUp.expires = -1;
                            itemUp.isLock = false;
                            p.nj.addItemBag(false, itemUp);
                            return;
                        }
                        if (util.percent(400, 1)) {
                            Item itemUp = new Item();
                            itemUp.id = 567;
                            itemUp.quantity = 1;
                            itemUp.isExpires = false;
                            itemUp.isLock = false;
                            p.nj.addItemBag(false, itemUp);
                            
                        }
                        else if (util.percent(100, 1)) {
                            Item itemup = ItemData.itemDefault(util.nextInt(654, 655));
                            itemup.isLock = false;
                            p.nj.addItemBag(false, itemup);
                            return;
                        }
                        else {
                            short idI = idItemBanhChocolate[util.nextInt(idItemBanhChocolate.length)];
                            Item itemup = ItemData.itemDefault(idI);
                            itemup.isLock = item.isLock;
                            if (ItemData.ItemDataId(itemup.id).type == 10 || itemup.id == 523 || itemup.id == 804 || itemup.id == 805 || itemup.id == 799 || itemup.id == 800 ) {
                                itemup.isExpires = true;
                                itemup.expires = System.currentTimeMillis() + 604800000L;
                            }
                            p.nj.addItemBag(true, itemup);
                        }
                        break;
                    }
            
            case 671: {
                        if (server.manager.EVENT != 2) {
                            p.sendYellowMessage(Language.END_EVENT);
                            return;
                        }
                        if (numbagnull == 0) {
                            p.sendYellowMessage("Hành trang không đủ chỗ trống.");
                            return;
                        }
                        if (p.nj.getLevel() < 20) {
                            p.sendYellowMessage("Trình độ của bạn không đủ để sử dụng vật phẩm này.");
                            return;
                        }
                        p.nj.removeItemBag(index, 1);

                        int perRuong = util.nextInt(10000);
                        int rhb = util.nextInt(25000);
                        if (util.percent(100, 30)) {
                            p.updateExp(3000000L, false);
                            return;
                        }
                        else if (rhb == 14999) {
                            Item itemUp = new Item();
                            itemUp.id = 385;
                            itemUp.quantity = 1;
                            itemUp.isExpires = false;
                            itemUp.isLock = false;
                            p.nj.addItemBag(false, itemUp);
                            Manager.chatKTG("Chúc mừng " + p.nj.name + " đã nhận được " + ItemData.ItemDataId(itemUp.id).name + " khi sử dụng " + ItemData.ItemDataId(item.id).name);
                            return;
                        }
                        else if (perRuong == 0) {
                            Item itemUp = new Item();
                            itemUp.id = 384;
                            itemUp.quantity = 1;
                            itemUp.isExpires = false;
                            itemUp.isLock = false;
                            p.nj.addItemBag(false, itemUp);
                            Manager.chatKTG("Chúc mừng " + p.nj.name + " đã nhận được " + ItemData.ItemDataId(itemUp.id).name + " khi sử dụng " + ItemData.ItemDataId(item.id).name);
                            return;
                        }
                        if (util.percent(150, 1)) {
                            Item itemUp = ItemData.itemDefault(443);
                            itemUp.quantity = 1;
                            itemUp.isExpires = false;
                            itemUp.expires = -1;
                            itemUp.isLock = false;
                            p.nj.addItemBag(false, itemUp);
                            return;
                        }
                        if (util.percent(400, 1)) {
                            Item itemUp = new Item();
                            itemUp.id = 567;
                            itemUp.quantity = 1;
                            itemUp.isExpires = false;
                            itemUp.isLock = false;
                            p.nj.addItemBag(false, itemUp);
                            
                        }
                        else if (util.percent(100, 1)) {
                            Item itemup = ItemData.itemDefault(util.nextInt(654, 655));
                            itemup.isLock = false;
                            p.nj.addItemBag(false, itemup);
                            return;
                        }
                        else {
                            short idI = idItemBanhChocolate[util.nextInt(idItemBanhChocolate.length)];
                            Item itemup = ItemData.itemDefault(idI);
                            itemup.isLock = item.isLock;
                            if (ItemData.ItemDataId(itemup.id).type == 10 || itemup.id == 523 || itemup.id == 804 || itemup.id == 805 || itemup.id == 799 || itemup.id == 800 ) {
                                itemup.isExpires = true;
                                itemup.expires = System.currentTimeMillis() + 604800000L;
                            }
                            p.nj.addItemBag(true, itemup);
                        }
                        break;
                    }
                    //Bánh dâu tây
                    case 672: {
                        if (server.manager.EVENT != 2) {
                            p.sendYellowMessage(Language.END_EVENT);
                            return;
                        }
                        if (numbagnull < 1) {
                            p.session.sendMessageLog("Hành trang không đủ chỗ trống.");
                            return;
                        }
                        if (p.nj.getLevel() < 20) {
                            p.sendYellowMessage("Trình độ của bạn không đủ để sử dụng vật phẩm này.");
                            return;
                        }
                        p.nj.removeItemBag(index, 1);

                        int perRuong = util.nextInt(5000);
                        if (util.nextInt(10) < 3) {
                            p.updateExp(3000000L, false);
                            break;
                        } else if (util.nextInt(160) <= 1) {
                            Item itemup = ItemData.itemDefault(util.nextInt(652, 653));
                            itemup.isLock = false;
                            p.nj.addItemBag(false, itemup);
                            break;
                        } else if (perRuong == 0) {
                            Item itemUp = new Item();
                            itemUp.id = 383;
                            itemUp.quantity = 1;
                            itemUp.isExpires = false;
                            itemUp.isLock = false;
                            p.nj.addItemBag(false, itemUp);
                            Manager.chatKTG("Chúc mừng " + p.nj.name + " đã nhận được " + ItemData.ItemDataId(itemUp.id).name + " khi sử dụng " + ItemData.ItemDataId(item.id).name);
                            return;
                        } else if (util.percent(1000, 2)) {
                            Item itemUp = new Item();
                            itemUp.id = 539;
                            itemUp.quantity = 1;
                            itemUp.isExpires = false;
                            itemUp.isLock = false;
                            p.nj.addItemBag(false, itemUp);
                            return;
                        } else {
                            short idI = idItemBanhDauTay[util.nextInt(idItemBanhDauTay.length)];
                            Item itemup = ItemData.itemDefault(idI);
                            if (idI == 781 || idI == 742 || idI == 523 || idI == 828) {
                                itemup.quantity = 1;
                                itemup.isExpires = true;
                                itemup.expires = System.currentTimeMillis() + 604800000L;
                            }
                            itemup.isLock = item.isLock;
                            p.nj.addItemBag(true, itemup);
                        }
                        break;
                    }
                                        
            default: {
                if (useItem.server.manager.EVENT != 0 &&
                        item != null &&
                        EventItem.isEventItem(item.id)) {

                    if (numbagnull == 0) {
                        p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                        return;
                    }

                    EventItem[] entrys = EventItem.entrys;
                    EventItem entry = null;
                    for (int i = 0; i < entrys.length; i++) {
                        entry = entrys[i];

                        if (entry == null) continue;
                        if (entry.getOutput().getId() == item.id) {
                            break;
                        }
                    }

                    if (entry == null) {
                        p.sendYellowMessage("Sự kiện này đã kết thúc không còn sử dụng được vật phẩm này nữa");
                        return;
                    }
                    
                    p.updateExp(entry.getOutput().getExp(), false);
                    if (util.nextInt(10) < 3) {
                        p.updateExp(2 * entry.getOutput().getExp(), false);
                    } else {
                        final short[] arId = entry.getOutput().getIdItems();
                        final short idI = arId[util.nextInt(arId.length)];
//                        if (idI == 383) {
//                            Manager.chatKTG(p.nj.name + " đã mở được Bát bảo từ sự kiện");
//                        } else if (idI == 384) {
//                            Manager.chatKTG(p.nj.name + " đã mở được Rương bạch ngân từ sự kiện");
//                        } else if (idI == 385) {
//                            Manager.chatKTG(p.nj.name + " đã mở được Rương huyền bí từ sự kiện");
//                        }
                        if (randomItem(p, item.isLock(), idI)) return;
                    }
                    p.nj.removeItemBag(index, 1);
                    return;
                }
                break;
            }
        }
        final Message m = new Message(11);
        m.writer().writeByte(index);
        m.writer().writeByte(p.nj.get().speed());
        m.writer().writeInt(p.nj.get().getMaxHP());
        m.writer().writeInt(p.nj.get().getMaxMP());
        m.writer().writeShort(p.nj.get().eff5buffHP());
        m.writer().writeShort(p.nj.get().eff5buffMP());
        m.writer().flush();
        p.sendMessage(m);
        m.cleanup();
        if (ItemData.isTypeMounts(item.id)) {
            if (p.nj.getPlace() != null) {
                for (final User user : p.nj.getPlace().getUsers()) {
                    p.nj.getPlace().sendMounts(p.nj.get(), user);
                }
            }
        }

//        if (item.id >= 795) {
//            p.sendInfo(false);
//        }
        if ((item.id >= 795 && item.id <= 805) || (item.id >= 813 && item.id <= 817) || (item.id >= 825 && item.id <= 827) || (item.id >= 830 && item.id <= 832)) {
            final Message m1 = new Message(57);
            m1.writer().flush();
            p.session.sendMessage(m1);
            m1.cleanup();
            if (!p.nj.isTrade) {
                Service.CharViewInfo(p, false);
            }
        }

        TaskHandle.useItemUpdate(p.nj, item.id);
        
    }

    private static boolean randomItem(User p, boolean isLock, short itemId) {
        Item itemup = ItemData.itemDefault(itemId);
        if (itemup == null) return true;

        if (itemup.isPrecious()) {
            if (!util.percent(100, itemup.getPercentAppear())) {
                itemup = Item.defaultRandomItem();
            }

            if ((itemup.id == 385) && !util.percent(100, itemup.getPercentAppear())) {
                itemup = Item.defaultRandomItem();
            }


        }


        itemup.setLock(isLock);

        p.nj.addItemBag(true, itemup);
        return false;
    }

    private static void upDaDanhVong(User p, Item item) {
        if (item.quantity >= 10) {
            short count = (short) (item.quantity / 10);
            val itemUp = ItemData.itemDefault(item.id + 1);
            itemUp.quantity = count;
            p.nj.removeItemBags(item.id, count * 10);
            p.nj.addItemBag(true, itemUp);
        } else {
            p.sendYellowMessage("Cần 10 viên đá danh vọng để nâng cấp");
        }
    }

    public static void useItemChangeMap(final User p, final Message m) {
        try {
            final byte indexUI = m.reader().readByte();
            final byte indexMenu = m.reader().readByte();
            m.cleanup();
            final Item item = p.nj.ItemBag[indexUI];
            if (item != null && (item.id == 37 || item.id == 35)) {
                if (indexMenu == 0 || indexMenu == 1 || indexMenu == 2) {
                    final Map ma = getMapid(Map.arrTruong[indexMenu]);
                    if (TaskHandle.isLockChangeMap2((short) ma.id, p.nj.getTaskId())) {
                        GameCanvas.startOKDlg(p.session, Text.get(0, 84));
                        return;
                    }
                    if (item.id != 37) {
                        p.nj.removeItemBag(indexUI);
                    }
                    for (final Place area : ma.area) {
                        if (area.getNumplayers() < ma.template.maxplayers) {
                            p.nj.getPlace().leave(p);
                            area.EnterMap0(p.nj);
                            return;
                        }
                    }
                }
                if (indexMenu == 3 || indexMenu == 4 || indexMenu == 5 || indexMenu == 6 || indexMenu == 7 || indexMenu == 8 || indexMenu == 9) {
                    final Map ma = getMapid(Map.arrLang[indexMenu - 3]);
                    assert ma != null;
                    if (TaskHandle.isLockChangeMap2((short) ma.id, p.nj.getTaskId())) {
                        GameCanvas.startOKDlg(p.session, Text.get(0, 84));
                        return;
                    }
                    for (final Place area : ma.area) {
                        if (area.getNumplayers() < ma.template.maxplayers) {
                            p.nj.getPlace().leave(p);
                            area.EnterMap0(p.nj);
                            return;
                        }
                    }
                }
            }
        } catch (IOException ex) {
        }
        p.nj.get().upDie();
    }


}
