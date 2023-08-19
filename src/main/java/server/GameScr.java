package server;

import io.Session;
import lombok.SneakyThrows;
import lombok.val;
import patch.Constants;
import patch.Resource;
import patch.clan.ClanThanThu;
import patch.tournament.KageTournament;
import real.*;
import threading.Message;
import threading.Server;

import java.io.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static patch.Constants.TRUNG_DI_LONG_ID;
import static patch.Constants.TRUNG_HAI_MA_ID;
import static patch.clan.ClanThanThu.*;
import static real.ItemData.EXP_ID;
import threading.Manager;

public class GameScr {

    static Server server;
    public static final int[] crystals;
    public static final int[] upClothe;
    public static final int[] upAdorn;
    public static final int[] upWeapon;
    public static final int[] coinUpCrystals;
    public static final int[] coinUpClothes;
    public static final int[] coinUpAdorns;
    public static final int[] coinUpWeapons;
    public static final int[] goldUps;
    public static final int[] maxPercents;

    public static short[] LAT_HINH_ID;

    public static ItemData[] itemTemplates;

    public static int[] ArryenLuck;
    private static final byte[] ArrdayLuck;
    static final int[] optionBikiep;
    static final int[] paramBikiep;
    static final int[] percentBikiep;
    static final int[] optionPet;
    static final int[] paramPet;
    static final int[] percentPet;
    static LogHistory LogHistory = new LogHistory();
    public static int[] goldUpMat = new int[]{10, 40, 60, 85, 120, 150, 190, 235, 285, 350};
    public static int[] percentUpMat = new int[]{100, 50, 35, 25, 20, 15, 10, 7, 5, 2};
    public static int[] coinUpMat = new int[]{250000, 500000, 1250000, 2000000, 4000000, 10000000, 20000000, 35000000, 50000000, 100000000};

    public static int[] quantityItemDvc = new int[]{6, 12, 18, 24, 30, 36, 42, 48, 54, 60};
    public static int[] goldUpBK = new int[]{150, 300, 550, 850, 1250, 1750, 2000, 2500, 3000, 3500};
    public static int[] XuUpBK = new int[]{150000, 300000, 360000, 420000, 510000, 600000, 700000, 1000000, 1500000, 2200000};
    public static int[] YenUpBK = new int[]{500000, 1000000, 1200000, 1400000, 2550000, 3000000, 4600000, 5400000, 12400000, 14000000};
    public static int[] CapDoBK = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
    public static int[] percentUpBK = new int[]{80, 65, 50, 40, 30, 25, 20, 15, 8, 3};

    public static int[] quantityItemcc = new int[]{6, 12, 18, 24, 30, 36, 42, 48, 54, 60};
    public static int[] goldUpYR = new int[]{150, 300, 550, 850, 1250, 1750, 2000, 2500, 3000, 3500};
    public static int[] XuUpYR = new int[]{150000, 300000, 360000, 420000, 510000, 600000, 700000, 1000000, 1500000, 2200000};
    public static int[] YenUpYR = new int[]{500000, 1000000, 1200000, 1400000, 2550000, 3000000, 4600000, 5400000, 12400000, 14000000};
    public static int[] CapDoYR = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
    public static int[] percentUpYR = new int[]{80, 65, 50, 40, 30, 25, 20, 15, 8, 3};
    public static int[] Xuup = new int[]{1000000, 2000000, 3000000, 4000000, 5000000, 6000000, 7000000, 8000000, 9000000, 10000000, 11000000, 12000000, 13000000, 14000000, 15000000};
    public static int[] Tile = new int[]{100, 90, 80, 70, 60, 50, 40, 30, 25, 20, 15, 10, 8, 5, 3, 1}; // tỉ lệ

    public static void UpgradeYoroi(User p) throws IOException {
        Item item = p.nj.get().ItemBody[12];
        p.nj.upxuMessage(-Xuup[item.upgrade]);
        if (Tile[item.upgrade] >= util.nextInt(100)) {
            for (byte k = 0; k < item.option.size(); ++k) {
                Option option = item.option.get(k);
                if (option.id == 81 || option.id == 80) {
                    Option ops = option;
                    ops.param += (int) 10;
                }
                if (option.id == 82 || option.id == 83) {
                    Option ops = option;
                    ops.param += 350;
                }
                if (option.id == 84) {
                    Option ops = option;
                    ops.param += 50;
                }
                if (option.id == 79) {
                    Option ops = option;
                    ops.param += (int) 1;
                }
            }
            item.upgrade = (byte) (item.upgrade + 1);
            item.isLock = true;
            p.nj.addItemBag(true, item);
            p.nj.getPlace().chatNPC(p, (short) 43, "Nâng Cấp Thành Công");
            p.nj.removeItemBody((byte) 12);
        } else {
            p.nj.getPlace().chatNPC(p, (short) 43, "Nâng Cấp Thất Bại");
        }
    }

    public static boolean mapNotPK(final int mapId) {
        return mapId == 1 || mapId == 10 || mapId == 17 || mapId == 22 || mapId == 27 || mapId == 32 || mapId == 38 || mapId == 43 || mapId == 48 || mapId == 72 || mapId == 100 || mapId == 101 || mapId == 102 || mapId == 109 || mapId == 121 || mapId == 122 || mapId == 123 || mapId == 138;
    }

    public static byte KeepUpgrade(final int upgrade) {
        if (upgrade >= 14) {
            return 14;
        }
        if (upgrade >= 12) {
            return 12;
        }
        if (upgrade >= 8) {
            return 8;
        }
        if (upgrade >= 4) {
            return 4;
        }
        return (byte) upgrade;
    }

    public static byte getLevelSpeed(final int level) {
        if (level >= 40) {
            return 3;
        }
        if (level >= 25) {
            return 2;
        }
        if (level >= 10) {
            return 1;
        }
        return 0;
    }

    public static byte SysClass(final byte nclass) {
        switch (nclass) {
            case 1:
            case 2: {
                return 1;
            }
            case 3:
            case 4: {
                return 2;
            }
            case 5:
            case 6: {
                return 3;
            }
            default: {
                if (nclass == 6 || nclass == 5) {
                    return 3;
                }
                if (nclass == 4 || nclass == 3) {
                    return 2;
                }
                if (nclass == 2 || nclass == 1) {
                    return 1;
                }
                return 0;
            }
        }
    }

    public static byte SideClass(final byte nclass) {
        if (nclass == 6 || nclass == 4 || nclass == 2) {
            return 1;
        }
        return 0;
    }

    public static void SendFile(final Session session, final int cmd, final String url) throws IOException {
        final byte[] ab = loadFile(url).toByteArray();
        final Message msg = new Message(cmd);
        msg.writer().write(ab);
        msg.writer().flush();
        session.sendMessage(msg);
        msg.cleanup();
    }

    public static void ItemStands(final User p) throws IOException {
        final Message m = new Message(-28);
        m.writer().writeByte(-83);
        m.writer().writeByte(10);
        m.writer().writeByte(12);
        m.writer().writeByte(12);
        m.writer().writeByte(13);
        m.writer().flush();
        p.sendMessage(m);
        m.cleanup();
    }

    public static void sendSkill(final User p, final String text) {
        try {
            byte[] arrSkill = null;
            int lent = 0;
            if (text.equals("KSkill")) {
                lent = p.nj.get().KSkill.length;
                arrSkill = new byte[lent];
                System.arraycopy(p.nj.get().KSkill, 0, arrSkill, 0, lent);
            }
            if (text.equals("OSkill")) {
                lent = p.nj.get().OSkill.length;
                arrSkill = new byte[lent];
                System.arraycopy(p.nj.get().OSkill, 0, arrSkill, 0, lent);
            }
            if (text.equals("CSkill")) {
                lent = 1;
                arrSkill = new byte[lent];
                arrSkill[0] = -1;
                final Skill skill = p.nj.get().getSkill(p.nj.get().getCSkill());
                if (skill != null) {
                    final SkillData data = SkillData.Templates(skill.id);
                    if (data.type != 2) {
                        arrSkill[0] = skill.id;
                    }
                }
                if (arrSkill[0] == -1 && p.nj.get().getSkills().size() > 0) {
                    arrSkill[0] = p.nj.get().getSkills().get(0).id;
                }
            }
            if (arrSkill == null) {
                return;
            }
            final Message m = new Message(-30);
            m.writer().writeByte(-65);
            m.writer().writeUTF(text);
            m.writer().writeInt(lent);
            m.writer().write(arrSkill);
            m.writer().writeByte(0);
            m.writer().flush();
            p.sendMessage(m);
            m.cleanup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void reciveImage(final User p, Message m) throws IOException {
        final int id = m.reader().readInt();
        m.cleanup();

        final ByteArrayOutputStream a = loadFile("res/icon/" + p.session.zoomLevel + "/" + id + ".png");
        if (a != null) {
            final byte[] ab = a.toByteArray();
            m = new Message(-28);
            m.writer().writeByte(-115);
            m.writer().writeInt(id);
            m.writer().writeInt(ab.length);
            m.writer().write(ab);
            m.writer().flush();
            p.sendMessage(m);
            m.cleanup();
        }
    }

    public static void sendModTemplate(final User p, int id) throws IOException {
        final MobData mob = MobData.getMob(id);
        if (mob == null) {
            return;
        }
//        util.Debug(mob.id + " Id mob " + id);
        ByteArrayOutputStream a;
        if (id == 82) {
            a = loadFile("res/map_file_msg/82");
        } else {
            a = loadFile("res/cache/mob/" + p.session.zoomLevel + "/" + id);
        }
        if (a != null) {
            final byte[] ab = a.toByteArray();
            val m = new Message(-28);
            m.writer().write(ab);
            m.writer().flush();
            p.sendMessage(m);
            m.cleanup();
        }
    }

    public static ByteArrayOutputStream loadFile(final String url) {
        try {

            if (Server.resource.containsKey(url)) {
                return Server.resource.get(url).getStream();
            }

            final FileInputStream openFileInput = new FileInputStream(url);
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final byte[] bArr = new byte[1024];
            while (true) {
                final int read = openFileInput.read(bArr);
                if (read == -1) {
                    break;
                }
                byteArrayOutputStream.write(bArr, 0, read);
            }
            openFileInput.close();
            Server.resource.put(url, new Resource(byteArrayOutputStream));
            return byteArrayOutputStream;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void saveFile(final String url, final byte[] data) {
        try {
            final File f = new File(url);
            if (f.exists()) {
                f.delete();
            }
            f.createNewFile();
            final FileOutputStream fos = new FileOutputStream(url);
            fos.write(data);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void ItemInfo(final User p, Message m) throws IOException {
        final byte type = m.reader().readByte();
        m.cleanup();
        util.Debug("Item info type " + type);
        Item[] arrItem = null;
        switch (type) {
            case 2: {
                arrItem = ItemSell.SellItemType(type).item;
                break;
            }
            case 4: {
                if (p.menuCaiTrang == 1) {
                    arrItem = p.nj.ItemBST;
                    break;
                } else if (p.menuCaiTrang == 2) {
                    arrItem = p.nj.ItemCaiTrang;
                    break;
                } else {
                    if (p.menuCaiTrang != 3 && p.menuCaiTrang != 4) {
                        arrItem = p.nj.ItemBox;
                    }
                    break;
                }
            }
            case 6: {
                arrItem = ItemSell.SellItemType(type).item;
                break;
            }
            case 7: {
                arrItem = ItemSell.SellItemType(type).item;
                break;
            }
            case 8: {
                arrItem = ItemSell.SellItemType(type).item;
                break;
            }
            case 9: {
                arrItem = ItemSell.SellItemType(type).item;
                break;
            }
            case 14: {
                arrItem = ItemSell.SellItemType(type).item;
                break;
            }
            case 15: {
                arrItem = ItemSell.SellItemType(type).item;
                break;
            }
            case 16: {
                arrItem = ItemSell.SellItemType(type).item;
                break;
            }
            case 17: {
                arrItem = ItemSell.SellItemType(type).item;
                break;
            }
            case 18: {
                arrItem = ItemSell.SellItemType(type).item;
                break;
            }
            case 19: {
                arrItem = ItemSell.SellItemType(type).item;
                break;
            }
            case 20: {
                arrItem = ItemSell.SellItemType(type).item;
                break;
            }
            case 21: {
                arrItem = ItemSell.SellItemType(type).item;
                break;
            }
            case 22: {
                arrItem = ItemSell.SellItemType(type).item;
                break;
            }
            case 23: {
                arrItem = ItemSell.SellItemType(type).item;
                break;
            }
            case 24: {
                arrItem = ItemSell.SellItemType(type).item;
                break;
            }
            case 25: {
                arrItem = ItemSell.SellItemType(type).item;
                break;
            }
            case 26: {
                arrItem = ItemSell.SellItemType(type).item;
                break;
            }
            case 27: {
                arrItem = ItemSell.SellItemType(type).item;
                break;
            }
            case 28: {
                arrItem = ItemSell.SellItemType(type).item;
                break;
            }
            case 29: {
                arrItem = ItemSell.SellItemType(type).item;
                break;
            }
            case 32: {
                arrItem = ItemSell.SellItemType(type).item;
                break;
            }
            case 34: {
                arrItem = ItemSell.SellItemType(type).item;
                break;
            }
        }
        if (arrItem == null) {
            return;
        }

        if (type == 4) {
            sendBagToChar(p, arrItem);
        } else {
            m = new Message(33);
            m.writer().writeByte(type);
            m.writer().writeByte(arrItem.length);
            for (int i = 0; i < arrItem.length; ++i) {
                m.writer().writeByte(i);
                m.writer().writeShort(arrItem[i].id);
            }
            m.writer().flush();
            p.sendMessage(m);
            m.cleanup();
        }
    }

    private static void sendBagToChar(User p, Item[] arrItem) throws IOException {
        Message m;
        m = new Message(31);
        m.writer().writeInt(p.nj.xuBox);
        m.writer().writeByte(arrItem.length);
        for (final Item item : arrItem) {
            if (item != null) {
                m.writer().writeShort(item.id);
                m.writer().writeBoolean(item.isLock());
                if (ItemData.isTypeBody(item.id) || ItemData.isTypeNgocKham(item.id)) {
                    m.writer().writeByte(item.getUpgrade());
                }
                m.writer().writeBoolean(item.isExpires);
                m.writer().writeShort(item.quantity);
            } else {
                m.writer().writeShort(-1);
            }
        }
        m.writer().flush();
        p.sendMessage(m);
        m.cleanup();
    }

    public static void buyItemStore(final User p, Message m) throws IOException {
    //    if (p.nj.isNhanban) {
    //        p.session.sendMessageLog("Bạn đang trong chế độ thứ thân không thể dùng được chức năng này");
    //        return;
    //    }
        final byte type = m.reader().readByte();
        final byte index = m.reader().readByte();
        short num = 1;
        if (m.reader().available() > 0) {
            num = m.reader().readShort();
        }

//        if (type == 34 && num > 500) {
//            p.session.sendMessageLog("loi");
//            return;
//        }
        m.cleanup();
    //    if (type == 14) {
    //        if (p.nj.getMapId() != 22 && p.nj.getMapId() != 1 && p.nj.getMapId() != 10 && p.nj.getMapId() != 17 && p.nj.getMapId() != 27 && p.nj.getMapId() != 32 && p.nj.getMapId() != 38 && p.nj.getMapId() != 43 && p.nj.getMapId() != 48 && p.nj.getMapId() != 72) {
    //            p.session.sendMessageLog("Không thể mua vật phẩm ở đây");
    //            return;
    //        }
    //    }
        if (p.nj.getTaskId() < 3) {
            GameCanvas.startOKDlg(p.session, "Ngươi phải hoàn thành Nhiệm Vụ lần đầu đấu kiếm mới có thể mua vật phẩm.");
            return;
        }
        final Item sell = ItemSell.getItemTypeIndex(type, index);
        if (num <= 0 || sell == null) {
            return;
        }
        final long buycoin = ((long) sell.buyCoin) * num;
        final long buycoinlock = ((long) sell.buyCoinLock) * num;
        final long buycoingold = ((long) sell.buyGold) * num;
        if (buycoin < 0 || buycoinlock < 0 || buycoingold < 0) {
            return;
        }
        final ItemData data = ItemData.ItemDataId(sell.id);
        if (type == 34 && num > 0) {
            final ClanManager clan = ClanManager.getClanByName(p.nj.clan.clanName);
            if (clan == null) {
                p.session.sendMessageLog("Bạn cần có gia tộc");
            } else if (clan.coin < buycoin) {
                p.session.sendMessageLog("Không Đủ Ngân Sách");
            } else if (p.nj.clan.typeclan < 3) {
                p.session.sendMessageLog("Chỉ có tộc trưởng hoặc tôc phó mới được phép mua");
            } else if ((sell.id == 423 && clan.itemLevel < 1) || (sell.id == 424 && clan.itemLevel < 2) || (sell.id == 425 && clan.itemLevel < 3) || (sell.id == 426 && clan.itemLevel < 4) || (sell.id == 427 && clan.itemLevel < 5)) {
                p.session.sendMessageLog("Cần khai mở gia tộc để mua vật phẩm này");
            } else {
                if (buycoin > clan.coin) {
                    p.session.sendMessageLog("Ngân sách gia tộc không đủ");
                    return;
                }
                if (sell.id == TRUNG_HAI_MA_ID || sell.id == TRUNG_DI_LONG_ID) {
                    if (clan.clanThanThus.size() == 1) {
                        p.endLoad(false);
                        p.sendYellowMessage("Số lượng thần thú đã đạt cấp tối đa");
                        return;
                    }
                    int countDiLong = (int) ((int) clan.clanThanThus.stream().filter(t -> t.getPetItem().id >= DI_LONG_1_ID && t.getPetItem().id <= DI_LONG_3_ID).count()
                            + clan.Eggs.stream().filter(i -> i.id == TRUNG_DI_LONG_ID).count());

                    int countHoaLong = (int) clan.clanThanThus.stream().filter(t -> t.getPetItem().id == HOA_LONG_ID).count();
                    int countHaiMa = (int) ((int) clan.clanThanThus.stream().filter(t -> t.getPetItem().id >= HAI_MA_1_ID && t.getPetItem().id <= HAI_MA_1_ID).count() + clan.Eggs.stream().filter(i -> i.id == TRUNG_HAI_MA_ID).count());
                    if (sell.id == TRUNG_DI_LONG_ID || sell.id == TRUNG_HAI_MA_ID) {
                        if (countDiLong == 1 || countHoaLong == 1 || countHaiMa == 1) {
                            p.endLoad(false);
                            p.sendYellowMessage("Số lượng thần thú đã đạt cấp tối đa");
                            return;
                        }
                    }
                }
                if (clan != null && clan.getLevel() < 25 && (sell.id == TRUNG_HAI_MA_ID || sell.id == TRUNG_DI_LONG_ID)) {
                    p.endLoad(false);
                    p.sendYellowMessage("Yêu cầu gia tộc cấp 25 trở lên.");
                    return;
                }

                final Item item = sell.clone();
                item.quantity = num;
                for (short i = 0; i < item.option.size(); ++i) {
                    item.option.get(i).param = util.nextInt(item.getOptionShopMin(item.option.get(i).id, item.option.get(i).param), item.option.get(i).param);
                }
                if (sell.id == TRUNG_HAI_MA_ID || sell.id == Constants.TRUNG_DI_LONG_ID) {
                    item.isExpires = true;
                    if (util.debug) {
                        // TODO TIME TRUNG
                        item.expires = 60000 * 3L;
                        clan.timeTrung = (int) (item.expires + System.currentTimeMillis());
                    } else {
                        item.expires = 259200000L;
                        clan.timeTrung = (int) (item.expires + System.currentTimeMillis());
                    }
                    item.timeBuy = System.currentTimeMillis();
                }

                clan.addItem(item);
                clan.updateCoin(-(int) buycoin);
                m = new Message(13);
                m.writer().writeInt(p.nj.xu);
                m.writer().writeInt(p.nj.yen);
                m.writer().writeInt(p.luong);
                m.writer().flush();
                p.sendMessage(m);
                m.cleanup();
                m = new Message(-24);
                m.writer().writeUTF("Gia tộc nhận được " + data.name);
                m.writer().flush();
                clan.sendMessage(m);
                m.cleanup();

            }
        } else if ((!data.isUpToUp && p.nj.getAvailableBag() >= num) || (data.isUpToUp && p.nj.getIndexBagid(sell.id, sell.isLock()) != -1) || (data.isUpToUp && p.nj.getAvailableBag() > 0)) {
            if (p.nj.xu < buycoin) {
                p.session.sendMessageLog("Không đủ xu");
                return;
            }
            if (p.nj.yen < buycoinlock) {
                p.session.sendMessageLog("Không đủ yên");
                return;
            }
            if (p.luong < buycoingold) {
                p.session.sendMessageLog("Không đủ lượng");
                return;
            }
            p.nj.upxuMessage(-buycoin);
            p.nj.upyenMessage(-buycoinlock);
            p.luongMessage(-buycoingold);
            for (int j = 0; j < num; ++j) {
                final Item item = new Item();
                item.id = sell.id;
                if (sell.isLock()) {
                    item.setLock(true);
                }
                item.sys = sell.sys;
                if (sell.isExpires) {
                    item.isExpires = true;
                    item.expires = util.TimeMillis(sell.expires);
                }
                item.sale = sell.sale;
                for (final Option Option : sell.option) {
                    final int idOp = Option.id;
                    final int par = util.nextInt(item.getOptionShopMin(idOp, Option.param), Option.param);
                    final Option option = new Option(idOp, par);
                    item.option.add(option);
                }
                if (data.isUpToUp) {
                    item.quantity = num;
                    p.nj.addItemBag(true, item);
                    break;
                }
                p.nj.addItemBag(false, item);
            }
            LogHistory.log1(p.nj.name + " đã mua " + num + " item " + sell.id + " với giá " + buycoin + " xu " + buycoinlock + " yên " + buycoingold + " lượng");

            if (p.nj.getTaskId() == 3 && p.nj.getTaskIndex() == 0 && sell.id == 23) {
                p.nj.upMainTask();
            }

            m = new Message(13);
            m.writer().writeInt(p.nj.xu);
            m.writer().writeInt(p.nj.yen);
            m.writer().writeInt(p.luong);
            m.writer().flush();
            p.sendMessage(m);
            m.cleanup();
        } else {
            p.session.sendMessageLog("Hành trang không đủ chỗ trống");
        }
    }

    public static void doConvertUpgrade(final User p, Message m) throws IOException {
    //    if (p.nj.isNhanban) {
    //        p.session.sendMessageLog("Bạn đang trong chế độ thứ thân không thể dùng được chức năng này");
    //        return;
    //    }
        if (p.nj.getMapId() != 22 && p.nj.getMapId() != 10 && p.nj.getMapId() != 17 && p.nj.getMapId() != 32 && p.nj.getMapId() != 38 && p.nj.getMapId() != 43 && p.nj.getMapId() != 48) {
            p.session.sendMessageLog("Chỉ có thể dùng chức năng này ở các làng");
            return;
        }
        final byte index1 = m.reader().readByte();
        final byte index2 = m.reader().readByte();
        final byte index3 = m.reader().readByte();
        m.cleanup();
        final Item item1 = p.nj.getIndexBag(index1);
        final Item item2 = p.nj.getIndexBag(index2);
        final Item item3 = p.nj.getIndexBag(index3);
        if (item1 != null && item2 != null && item3 != null) {
            if (item1.getData().type == 15 || item2.getData().type == 15) {
                p.session.sendMessageLog("Vật phẩm này không thể chuyển hóa");
                return;
            }
            if (!ItemData.isTypeBody(item1.id) || !ItemData.isTypeBody(item2.id) || (item3.id != 269 && item3.id != 270 && item3.id != 271)) {
                p.session.sendMessageLog("Chỉ chọn trang bị và Chuyển hóa");
                return;
            }
            final ItemData data1 = ItemData.ItemDataId(item1.id);
            final ItemData data2 = ItemData.ItemDataId(item2.id);
            if (item1.getUpgrade() == 0 || item2.getUpgrade() > 0 || (item3.id == 269 && item1.getUpgrade() > 10) || (item3.id == 270 && item1.getUpgrade() > 13)) {
                p.session.sendMessageLog("Vật phẩm chuyển hóa không hợp lệ");
                return;
            }
            if (data1.level > data2.level || data1.type != data2.type) {
                p.session.sendMessageLog("Chỉ được chuyển hóa trang bị cùng loại và cùng cấp trở lên");
                return;
            }
            item1.setLock(true);
            item2.setLock(true);
            final byte upgrade = item1.getUpgrade();
            item1.upgradeNext((byte) (-item1.getUpgrade()));
            item2.upgradeNext(upgrade);
            m = new Message(-28);
            m.writer().writeByte(-88);
            m.writer().writeByte(index1);
            m.writer().writeByte(item1.getUpgrade());
            m.writer().writeByte(index2);
            m.writer().writeByte(item2.getUpgrade());
            m.writer().flush();
            p.sendMessage(m);
            m.cleanup();
            p.nj.removeItemBag(index3, 1);
        }
    }

    public static void crystalCollect(final User p, Message m, final boolean isCoin) throws IOException {
        if (p.nj.isNhanban) {
            p.session.sendMessageLog("Bạn đang trong chế độ thứ thân không thể dùng được chức năng này");
            return;
        }
        if (m.reader().available() > 28) {
            util.Debug("Lớn hơn 28");
            return;
        }
        if (p.nj.getAvailableBag() == 0) {
            p.session.sendMessageLog("Hành trang không đủ chỗ trống");
            return;
        }
        int crys = 0;
        final byte[] arrItem = new byte[m.reader().available()];
        for (byte i = 0; i < arrItem.length; ++i) {
            arrItem[i] = -1;
            final byte index = m.reader().readByte();
            final Item item = p.nj.getIndexBag(index);
            if (item != null) {
                final ItemData data = ItemData.ItemDataId(item.id);
                if (data.type != 26 || item.id >= 12) {
                    p.session.sendMessageLog("Chỉ có thể dùng đá dưới 12 để nâng cấp");
                    return;
                }
                arrItem[i] = index;
                crys += GameScr.crystals[item.id];
            }
        }

        short id = 0;
        for (byte j = 0; j < GameScr.crystals.length; ++j) {
            if (crys > GameScr.crystals[j]) {
                id = (short) (j + 1);
            }
        }

        try {
            if (id >= 12) {
                id = 11;
            }
            final int percen = crys * 100 / GameScr.crystals[id];
            if (percen < 45) {
                p.session.sendMessageLog("Tỷ lệ phải từ 45% trở lên");
                return;
            }
            if (isCoin) {
                if (GameScr.coinUpCrystals[id] > p.nj.xu) {
                    return;
                }
                p.nj.upxu(-GameScr.coinUpCrystals[id]);
            } else {
                if (GameScr.coinUpCrystals[id] > p.nj.xu + p.nj.yen) {
                    return;
                }
                if (p.nj.yen >= GameScr.coinUpCrystals[id]) {
                    p.nj.upyen(-GameScr.coinUpCrystals[id]);
                } else {
                    final int coin = GameScr.coinUpCrystals[id] - p.nj.yen;
                    p.nj.upyen(-p.nj.yen);
                    p.nj.upxu(-coin);
                }
            }
            boolean suc = false;
            final Item item2 = new Item();
            if (util.nextInt(1, 100) <= percen) {
                suc = true;
                item2.id = id;
            } else {
                item2.id = (short) (id - 1);
            }
            item2.setLock(true);
            final int index2 = p.nj.getIndexBagNotItem();
            p.nj.ItemBag[index2] = item2;
            for (byte k = 0; k < arrItem.length; ++k) {
                if (arrItem[k] != -1) {
                    p.nj.ItemBag[arrItem[k]] = null;
                }
            }
            m = new Message(isCoin ? 19 : 20);
            m.writer().writeByte(suc ? 1 : 0);
            m.writer().writeByte(index2);
            m.writer().writeShort(item2.id);
            m.writer().writeBoolean(item2.isLock());
            m.writer().writeBoolean(item2.isExpires);
            if (isCoin) {
                m.writer().writeInt(p.nj.xu);
            } else {
                m.writer().writeInt(p.nj.yen);
                m.writer().writeInt(p.nj.xu);
            }
            m.writer().flush();
            p.sendMessage(m);
            m.cleanup();
        } catch (Exception e) {
            p.endLoad(true);
        }

    }

    public static void UpGrade(final User p, Message m) throws IOException {
        if (p.nj.isNhanban) {
            p.session.sendMessageLog("Bạn đang trong chế độ thứ thân không thể dùng được chức năng này");
            return;
        }
        if (p.nj.getMapId() != 22 && p.nj.getMapId() != 10 && p.nj.getMapId() != 17 && p.nj.getMapId() != 32 && p.nj.getMapId() != 38 && p.nj.getMapId() != 43 && p.nj.getMapId() != 48) {
            p.session.sendMessageLog("Chỉ có thể dùng chức năng này ở các làng");
            return;
        }
        final byte type = m.reader().readByte();
        final byte index = m.reader().readByte();
        final Item item = p.nj.getIndexBag(index);
        if (item == null || m.reader().available() > 18) {
            return;
        }
        if (item.getUpgrade() >= item.getUpMax()) {
            p.session.sendMessageLog("Trang bị đã đạt cấp tối đa");
            return;
        }
        final byte[] arrItem = new byte[m.reader().available()];
        int crys = 0;
        boolean keep = false;
        boolean da = false;
        for (byte i = 0; i < arrItem.length; ++i) {
            arrItem[i] = -1;
            final byte index2 = m.reader().readByte();
            final Item item2 = p.nj.getIndexBag(index2);
            if (item2 != null) {
                final ItemData data = ItemData.ItemDataId(item2.id);
                if (data.type == 26) {
                    arrItem[i] = index2;
                    crys += GameScr.crystals[item2.id];
                    da = true;
                } else {
                    if (data.type != 28) {
                        p.session.sendMessageLog("Chỉ có thể chọn đá và bảo hiểm");
                        return;
                    }
                    arrItem[i] = index2;
                    if (item2.id == 242 && item.getUpgrade() < 8) {
                        keep = true;
                    } else if (item2.id == 284 && item.getUpgrade() < 12) {
                        keep = true;
                    } else if (item2.id == 285 && item.getUpgrade() < 14) {
                        keep = true;
                    } else {
                        if (item2.id != 475) {
                            p.session.sendMessageLog("Bảo hiểm không hợp lệ");
                            return;
                        }
                        keep = true;
                    }
                }
            }
        }
        final ItemData data2 = ItemData.ItemDataId(item.id);
        int gold = 0;
        if (arrItem.length == 0 || data2.type > 10) {
            return;
        }
        if (!da) {
            p.session.sendMessageLog("Hãy chọn thêm đá");
            return;
        }
        int coins;
        int percent;
        if (data2.type == 1) {
            coins = GameScr.coinUpWeapons[item.getUpgrade()];
            percent = crys * 100 / GameScr.upWeapon[item.getUpgrade()];
            if (percent > GameScr.maxPercents[item.getUpgrade()]) {
                percent = GameScr.maxPercents[item.getUpgrade()];
            }
        } else if (data2.type % 2 == 0) {
            coins = GameScr.coinUpClothes[item.getUpgrade()];
            percent = crys * 100 / GameScr.upClothe[item.getUpgrade()];
            if (percent > GameScr.maxPercents[item.getUpgrade()]) {
                percent = GameScr.maxPercents[item.getUpgrade()];
            }
        } else {
            coins = GameScr.coinUpAdorns[item.getUpgrade()];
            percent = crys * 100 / GameScr.upAdorn[item.getUpgrade()];
            if (percent > GameScr.maxPercents[item.getUpgrade()]) {
                percent = GameScr.maxPercents[item.getUpgrade()];
            }
        }

        if (type == 1) {
            percent += percent * 50 / 100;
            gold = GameScr.goldUps[item.getUpgrade()];
        }
        if (coins > p.nj.yen + p.nj.xu || gold > p.luong) {
            return;
        }
        for (byte j = 0; j < arrItem.length; ++j) {
            if (arrItem[j] != -1) {
                p.nj.ItemBag[arrItem[j]] = null;
            }
        }
        p.upluong(-gold);
        if (coins <= p.nj.yen) {
            p.nj.upyen(-coins);
        } else {
            final int coin = coins - p.nj.yen;
            p.nj.upyen(-p.nj.yen);
            p.nj.upxu(-coin);
        }
        if (item.getUpgrade() >= 15) {
            percent += 3;
        }
        final boolean suc = util.nextInt(1, 100) <= percent;
        m.cleanup();
        item.setLock(true);
        util.Debug("type " + type + " index " + index + " percen " + percent);
        if (suc) {
            item.upgradeNext((byte) 1);
            if (p.nj.isTaskDanhVong == 1 && p.nj.taskDanhVong[0] == 4) {
                if (p.nj.taskDanhVong[1] < p.nj.taskDanhVong[2]) {
                    p.sendYellowMessage("- Nâng cấp trang bị " + ItemData.ItemDataId(item.id).name + " lên cấp " + item.getUpgrade() + "/" + p.nj.taskDanhVong[2]);
                }
                if (item.getUpgrade() == p.nj.taskDanhVong[2]) {
                    p.nj.taskDanhVong[1]++;
                    p.sendYellowMessage("Hoàn thành nhiệm vụ, hãy gặp Ameji để trả nhiệm vụ");
                }
            }
            if (p.nj.getTaskId() == 12) {
                if (p.nj.getTaskIndex() == 1 && item.getData().isVuKhi()) {
                    p.nj.upMainTask();
                } else if (p.nj.getTaskIndex() == 2 && item.getData().isTrangSuc()) {
                    p.nj.upMainTask();
                } else if (p.nj.getTaskIndex() == 3 && item.getData().isTrangPhuc()) {
                    p.nj.upMainTask();
                }
            }
        } else if (!keep) {
            item.upgradeNext((byte) (-(item.getUpgrade() - KeepUpgrade(item.getUpgrade()))));
        }
        m = new Message(21);
        m.writer().writeByte(suc ? 1 : 0);
        m.writer().writeInt(p.luong);
        m.writer().writeInt(p.nj.xu);
        m.writer().writeInt(p.nj.yen);
        m.writer().writeByte(item.getUpgrade());
        m.writer().flush();
        p.sendMessage(m);
        m.cleanup();
    }

    public static void Split(User p, Message m) {
        try {
            
            byte index = m.reader().readByte();
            m.cleanup();
            Item item = p.nj.getIndexBag(index);
            if (item == null || item.upgrade <= 0) {
                return;
            }
            ItemData data = ItemData.ItemDataId(item.id);
            if (data.type > 10) {
                p.sendYellowMessage("Không thể phân tách vật phẩm này");
                return;
            }
            int num = 0;
            if (data.type == 1) {
                for (byte i = 0; i < item.upgrade; ++i) {
                    num += GameScr.upWeapon[i];
                }
            }
            else if (data.type % 2 == 0) {
                for (byte i = 0; i < item.upgrade; ++i) {
                    num += GameScr.upClothe[i];
                }
            }
            else {
                for (byte i = 0; i < item.upgrade; ++i) {
                    num += GameScr.upAdorn[i];
                }
            }
            num /= 2;
            int num2 = 0;
            Item[] arrItem = new Item[24];
            for (int n = GameScr.crystals.length - 1; n >= 0; --n) {
                if (num >= GameScr.crystals[n]) {
                    arrItem[num2] = new Item();
                    arrItem[num2].id = (short)n;
                    arrItem[num2].isLock = item.isLock;
                    num -= GameScr.crystals[n];
                    n++;
                    num2++;
                }
            }
            if (num2 > p.nj.getAvailableBag()) {
                p.sendYellowMessage("Hành trang không đủ chỗ trống");
                return;
            }
            byte[] arrIndex = new byte[arrItem.length];
            for (byte j = 0; j < arrItem.length; ++j) {
                if (arrItem[j] != null) {
                    byte index2 = (byte) p.nj.getIndexBagNotItem();
                    p.nj.ItemBag[index2] = arrItem[j];
                    arrIndex[j] = index2;
                }
            }
            item.upgradeNext((byte)(-item.upgrade));
            m = new Message(22);
            m.writer().writeByte(num2);
            for (byte j = 0; j < num2; ++j) {
                if (arrItem[j] != null) {
                    m.writer().writeByte(arrIndex[j]);
                    m.writer().writeShort(arrItem[j].id);
                }
            }
            m.writer().flush();
            p.nj.sendMessage(m);
            m.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(m != null) {
                m.cleanup();
            }
        }
    }

    public static void LuckValue(final User p, Message m) throws IOException {
        if (p.nj.getPlace().map.id != 72) {
            p.session.sendMessageLog("Chỉ có thể lật hình ở Trường Ookaza");
            return;
        }
        byte index = m.reader().readByte();
        m.cleanup();
        if (index < 0 || index > 8) {
            index = 0;
        }
        if (p.nj.getAvailableBag() == 0) {
            p.session.sendMessageLog("Hành trang không đủ chỗ trống.");
            return;
        }
        if (p.nj.quantityItemyTotal(340) == 0) {
            p.session.sendMessageLog("Cần có phiếu may mắn.");
            return;
        }

        short[] itemIds = LAT_HINH_ID;
        short id;
        /**
         *
         */
        p.nj.removeItemBags(340, 1);
        if (util.percent(100, 1)) {
            id = 523;
        } else {
            id = itemIds[util.nextInt(itemIds.length)];
        }
        ItemData data = ItemData.ItemDataId(id);

        do {
            id = itemIds[util.nextInt(itemIds.length)];
            data = ItemData.ItemDataId(id);
        } while (data == null);

        Item item;
        if (data.type < 10) {
            if (data.type == 1) {
                item = ItemData.itemDefault(id);
                item.sys = SysClass(data.nclass);
            } else {
                final byte sys = (byte) util.nextInt(1, 3);
                item = ItemData.itemDefault(id, sys);
            }
        } else {
            item = ItemData.itemDefault(id);
        }
        if (id == 523 || id == 419) {
            item.isExpires = true;
            item.expires = util.TimeDay(GameScr.ArrdayLuck[util.nextInt(GameScr.ArrdayLuck.length)]);
        }
        if (data.type != 19) {
            p.nj.addItemBag(true, item);
        } else {
            item.quantity = GameScr.ArryenLuck[util.nextInt(GameScr.ArryenLuck.length)];
            p.nj.upyenMessage(item.quantity);
            p.sendYellowMessage("Bạn nhận được " + item.quantity + " yên");
        }
        m = new Message(-28);
        m.writer().writeByte(-72);
        for (byte i = 0; i < 9; ++i) {
            if (i == index) {
                m.writer().writeShort(id);
            } else {
                m.writer().writeShort(itemIds[util.nextInt(itemIds.length)]);
            }
        }
        m.writer().flush();
        p.sendMessage(m);
        m.cleanup();
    }

    public static void LuyenThach(User p, Message m) throws IOException {
        if (p.nj.isNhanban) {
            p.session.sendMessageLog("Bạn đang trong chế độ thứ thân không thể dùng được chức năng này");
            return;
        }
        byte[] arrItem = new byte[m.reader().available()];

        Item item = null;
        int checkTTS = 0;
        int checkTTT = 0;

        p.endLoad(true);

        if (arrItem.length == 4) {
            for (byte i = 0; i < arrItem.length; i++) {
                byte index2 = m.reader().readByte();
                item = p.nj.getIndexBag(index2);
                if (item.id == 455) {
                    checkTTS++;
                    checkTTT = 0;
                } else if (item.id == 456) {
                    checkTTT++;
                    checkTTS = 0;
                }
                p.nj.removeItemBag(index2, 1);
            }
            if (checkTTS > 0) {
                p.nj.addItemBag(false, ItemData.itemDefault(456));
            } else if (checkTTT > 0) {
                p.nj.addItemBag(false, ItemData.itemDefault(457));
            }
            return;

        } else if (arrItem.length == 9) {
            for (byte i = 0; i < arrItem.length; i++) {
                byte index2 = m.reader().readByte();
                if (i == 0) {
                    item = p.nj.getIndexBag(index2);
                }
                p.nj.removeItemBag(index2, 1);
            }

            if (item.id == 455) {
                p.nj.addItemBag(false, ItemData.itemDefault(456));
            } else if (item.id == 456) {
                p.nj.addItemBag(false, ItemData.itemDefault(457));
            }
            return;
        }

    }

    public static void TinhLuyen(final User p, final Message m) throws IOException {
        if (p.nj.isNhanban) {
            p.session.sendMessageLog("Bạn đang trong chế độ thứ thân không thể dùng được chức năng này");
            return;
        }
        final byte index = m.reader().readByte();
        final Item it = p.nj.getIndexBag(index);
        if (it == null) {
            return;
        }
        if (it.getData().type == 15) {
            p.session.sendMessageLog("Vật phẩm này không thể dịch chuyển");
            return;
        }
        final ItemData data = ItemData.ItemDataId(it.id);
        int tl = -1;
        for (byte i = 0; i < it.option.size(); ++i) {
            if (it.option.get(i).id == 85) {
                tl = it.option.get(i).param;
                if (tl >= 9) {
                    p.session.sendMessageLog("Vật phẩm đã được tinh luyên tối đa");
                    return;
                }
            }
        }
        if (tl == -1) {
            p.session.sendMessageLog("Vật phẩm không dùng cho tinh luyện");
            return;
        }
        int ttts = 0;
        int tttt = 0;
        int tttc = 0;
        final byte[] arit = new byte[m.reader().available()];
        for (byte j = 0; j < arit.length; ++j) {
            final byte ind = m.reader().readByte();
            final Item item = p.nj.getIndexBag(ind);
            if (item == null) {
                return;
            }
            if (item.id != 455 && item.id != 456 && item.id != 457) {
                p.session.sendMessageLog("Vật phẩm không dùng cho tinh luyện");
                return;
            }
            arit[j] = ind;
            if (item.id == 455) {
                ++ttts;
            } else if (item.id == 456) {
                ++tttt;
            } else if (item.id == 457) {
                ++tttc;
            }
        }
        int percent = 0;
        int yen = 0;
        switch (tl) {
            case 0: {
                percent = 60;
                yen = 150000;
                if (ttts != 3 || tttt != 0 || tttc != 0) {
                    p.session.sendMessageLog("Tinh luyện 1 cần dùng 3 Tử tinh thạch sơ");
                    return;
                }
                break;
            }
            case 1: {
                percent = 45;
                yen = 247500;
                if (ttts != 5 || tttt != 0 || tttc != 0) {
                    p.session.sendMessageLog("Tinh luyện 2 cần dùng 5 Tử tinh thạch sơ");
                    return;
                }
                break;
            }
            case 2: {
                percent = 34;
                yen = 408375;
                if (ttts != 9 || tttt != 0 || tttc != 0) {
                    p.session.sendMessageLog("Tinh luyện 3 cần dùng 9 Tử tinh thạch sơ");
                    return;
                }
                break;
            }
            case 3: {
                percent = 26;
                yen = 673819;
                if (ttts != 0 || tttt != 4 || tttc != 0) {
                    p.session.sendMessageLog("Tinh luyện 4 cần dùng 4 Tử tinh thạch trung");
                    return;
                }
                break;
            }
            case 4: {
                percent = 20;
                yen = 1111801;
                if (ttts != 0 || tttt != 7 || tttc != 0) {
                    p.session.sendMessageLog("Tinh luyện 5 cần dùng 7 Tử tinh thạch trung");
                    return;
                }
                break;
            }
            case 5: {
                percent = 15;
                yen = 2056832;
                if (ttts != 0 || tttt != 10 || tttc != 0) {
                    p.session.sendMessageLog("Tinh luyện 5 cần dùng 7 Tử tinh thạch trung");
                    return;
                }
                break;
            }
            case 6: {
                percent = 11;
                yen = 4010922;
                if (ttts != 0 || tttt != 0 || tttc != 5) {
                    p.session.sendMessageLog("Tinh luyện 7 cần dùng 5 Tử tinh thạch cao");
                    return;
                }
                break;
            }
            case 7: {
                percent = 8;
                yen = 7420021;
                if (ttts != 0 || tttt != 0 || tttc != 7) {
                    p.session.sendMessageLog("Tinh luyện 8 cần dùng 7 Tử tinh thạch cao");
                    return;
                }
                break;
            }
            case 8: {
                percent = 6;
                yen = 12243035;
                if (ttts != 0 || tttt != 0 || tttc != 9) {
                    p.session.sendMessageLog("Tinh luyện 9 cần dùng 9 Tử tinh thạch cao");
                    return;
                }
                break;
            }
        }
        if (yen > p.nj.yen && yen > p.nj.xu && yen > p.nj.xu + p.nj.yen) {
            p.session.sendMessageLog("Không đủ yên hoặc xu tinh luyện");
            return;
        }
        p.endLoad(true);
        if (p.nj.yen >= yen) {
            p.nj.upyenMessage(-yen);
        } else if (p.nj.xu >= yen) {
            p.nj.upxuMessage(-yen);
        } else {
            val preYen = p.nj.yen;
            p.nj.upyenMessage(-preYen);
            p.nj.upXuMessage(-(yen - preYen));
        }

        if (percent >= util.nextInt(80)) {
            for (byte k = 0; k < it.option.size(); ++k) {
                final Option option = it.option.get(k);
                option.param += ItemData.ThinhLuyenParam(it.option.get(k).id, tl);
            }
            p.requestItemInfoMessage(it, index, 3);
            p.sendYellowMessage("Tinh luyện thành công!");
        } else {
            p.sendYellowMessage("Tinh luyện thất bại!");
        }
        for (byte k = 0; k < arit.length; ++k) {
            p.nj.removeItemBag(arit[k], 1);
        }
        it.setLock(true);
    }

    public static void DichChuyen(final User p, final Message m) throws IOException {
        if (p.nj.isNhanban) {
            p.session.sendMessageLog("Bạn đang trong chế độ thứ thân không thể dùng được chức năng này");
            return;
        }
        final byte index = m.reader().readByte();
        final Item item = p.nj.getIndexBag(index);
        if (item.getData().type == 15) {
            p.session.sendMessageLog("Vật phẩm này không thể dịch chuyển");
            return;
        }
        if (item != null && ItemData.isTypeBody(item.id) && item.getUpgrade() > 11) {
            for (byte i = 0; i < item.option.size(); ++i) {
                if (item.option.get(i).id == 85) {
                    p.session.sendMessageLog("Vật phẩm đã được dịch chuyển");
                    return;
                }
            }
            final byte[] arrIndex = new byte[20];
            for (byte j = 0; j < arrIndex.length; ++j) {
                final byte index2 = m.reader().readByte();
                final Item item2 = p.nj.getIndexBag(index2);
                if (item2 == null || item2.id != 454) {
                    return;
                }
                arrIndex[j] = index2;
            }
            p.endLoad(true);
            final ItemData data = ItemData.ItemDataId(item.id);
            item.option.add(new Option(85, 0));
            switch (data.type) {
                case 0: {
                    if (item.sys == 1) {
                        item.option.add(new Option(96, 10));
                    } else if (item.sys == 2) {
                        item.option.add(new Option(95, 10));
                    } else if (item.sys == 3) {
                        item.option.add(new Option(97, 10));
                    }
                    item.option.add(new Option(79, 5));
                    break;
                }
                case 1: {
                    item.option.add(new Option(87, util.nextInt(400, 500)));
                    if (item.sys == 1) {
                        item.option.add(new Option(88, util.nextInt(400, 500)));
                    } else if (item.sys == 2) {
                        item.option.add(new Option(89, util.nextInt(400, 500)));
                    } else if (item.sys == 3) {
                        item.option.add(new Option(90, util.nextInt(400, 500)));
                    }
                    break;
                }
                case 2: {
                    item.option.add(new Option(80, 50));
                    item.option.add(new Option(91, 10));
                    break;
                }
                case 3: {
                    item.option.add(new Option(81, 5));
                    item.option.add(new Option(79, 5));
                    break;
                }
                case 4: {
                    item.option.add(new Option(86, 120));
                    item.option.add(new Option(94, util.nextInt(84, 124)));
                    break;
                }
                case 5: {
                    if (item.sys == 1) {
                        item.option.add(new Option(96, 5));
                    } else if (item.sys == 2) {
                        item.option.add(new Option(95, 5));
                    } else if (item.sys == 3) {
                        item.option.add(new Option(97, 5));
                    }
                    item.option.add(new Option(92, 10));
                    break;
                }
                case 6: {
                    item.option.add(new Option(83, util.nextInt(400, 500)));
                    item.option.add(new Option(82, util.nextInt(400, 500)));
                    break;
                }
                case 7: {
                    if (item.sys == 1) {
                        item.option.add(new Option(96, 5));
                    } else if (item.sys == 2) {
                        item.option.add(new Option(95, 5));
                    } else if (item.sys == 3) {
                        item.option.add(new Option(97, 5));
                    }
                    item.option.add(new Option(87 + item.sys, util.nextInt(400, 600)));
                    break;
                }
                case 8: {
                    item.option.add(new Option(82, util.nextInt(400, 500)));
                    item.option.add(new Option(84, util.nextInt(90, 100)));
                    break;
                }
                case 9: {
                    item.option.add(new Option(84, util.nextInt(90, 100)));
                    item.option.add(new Option(83, util.nextInt(400, 500)));
                    break;
                }
            }
            for (byte k = 0; k < arrIndex.length; ++k) {
                p.nj.removeItemBag(arrIndex[k], 1);
            }
            p.sendYellowMessage("Đã dịch chuyển trang bị");
            p.requestItemInfoMessage(item, index, 3);
            item.setLock(true);
        }
        util.Debug(index + " " + item.id);
    }

    @SneakyThrows
    public static void requestMapTemplate(User user, Message m) {

        final int templateId = m.reader().readUnsignedByte();
        m.cleanup();

        final Message ms = new Message(-28);
        String url = "res/map/" + templateId;
        if (templateId == 139) {
            url = "res/map_file_msg/map_back.bin";
        } else {
            ms.writer().writeByte(-109);
        }
        val data = loadFile(url).toByteArray();
        ms.writer().write(data);
        user.sendMessage(ms);

    }


    private static long[][] arrNgocKhamEXP = new long[][]{
        new long[2],
        new long[]{200, 100010},
        new long[]{500, 20},
        new long[]{1000, 50},
        new long[]{2000, 110},
        new long[]{5000, 210},
        new long[]{10000, 510},
        new long[]{20000, 1010},
        new long[]{50000, 2010},
        new long[]{100000, 5010},
        new long[]{100000, 10010}
    };
    private static int[][] arrLuyenNgocEXP = new int[][]{
            new int[2],
            new int[]{200, 0},
            new int[]{500, 200},
            new int[]{1000, 500},
            new int[]{2000, 1000},
            new int[]{5000, 2000},
            new int[]{10000, 5000},
            new int[]{20000, 10000},
            new int[]{50000, 20000},
            new int[]{100000, 50000},
            new int[]{110000, 100000}
    };



    public static int heSoNangNgoc(int id){
        if(id == 105 || id == 102){
            return 100;
        }else if(id==103){
            return 40;
        }else if(id==73){
            return 20;
        }else if(id==117 || id==125){
            return 10;
        }else if(id==124 || id==126 || id==116 || id==115 || id==118){
            return 2;
        }else{
            return 1;
        }
        
    }

    public static byte chiSoNgoc5 (byte cap){
        if(cap == 1){
            return 0;
        }else if(cap == 2){
            return 1;
        }else if(cap == 3){
            return 2;
        }else if(cap == 4){
            return 3;
        }else if(cap == 5){
            return 4;
        }else if(cap == 6){
            return 5;
        }else if(cap == 7){
            return 6;
        }else if(cap == 8){
            return 7;
        }else if(cap == 9){
            return 8;
        }else if(cap == 10){
            return 9;
        } 
        return 0;
    }

    public static int[] coinGotngoc = new int[]{0, 5, 40, 135, 320, 625, 1080, 1715, 2560, 3645, 5000, 10000};
    private static boolean checkTonTaiNgoc(Item itemsub, Item item) {
        switch (itemsub.id) {
            case 655: {
                for (int i = 0; i < item.option.size(); i++) {
                    if (item.option.get(i).id == 112) {
                        return true;
                    }
                }
                break;
            }
            case 654: {
                for (int i = 0; i < item.option.size(); i++) {
                    if (item.option.get(i).id == 111) {
                        return true;
                    }
                }
                break;
            }
            case 653: {
                for (int i = 0; i < item.option.size(); i++) {
                    if (item.option.get(i).id == 110) {
                        return true;
                    }

                }
                break;
            }
            case 652: {
                for (int i = 0; i < item.option.size(); i++) {
                    if (item.option.get(i).id == 109) {
                        return true;
                    }
                }
                break;
            }
            default: {
                return false;
            }
        }
        return false;

    }


    @SneakyThrows
    public static void ngocFeature(User p, Message m) throws IOException, SQLException{

    val index = m.reader().readByte();
    
        switch ((int) index) {
            //khảm
            case 0: {

                byte indexItem = m.reader().readByte();
                Item item = p.nj.getIndexBag(indexItem);
                int exp = 0;
                if (item != null) {
                    ItemData data = ItemData.ItemDataId(item.id);
                    int bach = data.type;
                    byte[] arrItem = new byte[m.reader().available()];
                    byte indexItemSub = m.reader().readByte();
                    arrItem[0] = indexItemSub;
                    Item itemSub = p.nj.getIndexBag(indexItemSub);
                    if (checkTonTaiNgoc(itemSub, item)) {
                        p.session.sendMessageLog("Không thể khảm cùng 1 loại ngọc trên 1 vật phẩm");
                        return;
                    }
                    if (data.level > p.nj.get().getLevel()) {
                        p.session.sendMessageLog("Level của bạn chưa đủ để khảm ngọc này");
                        return;
                    }
                    int money = 0;
                    for (int i = 0; i < itemSub.option.size(); i++) {
                        if (itemSub.option.get(i).id == 123) {
                            money = itemSub.option.get(i).param;
                        }
                    }
                    int type = 3;
                    int money2 = p.nj.yen - money;
                    if (money <= p.nj.yen) {
                        p.nj.upyen(-money);
                    } else if (money >= p.nj.yen) {
                        int coin = money - p.nj.yen;
                        if (coin > p.nj.xu) {
                            p.session.sendMessageLog("Không đủ xu và yên để khảm ngọc.");
                            return;
                        }
                        p.nj.upyen(-p.nj.yen);
                        p.nj.upxu(-coin);
                    }
                    util.Debug("money2" + money2);
                    int loai = 0;
                    int loai1 = 0;
                    int loai2 = 0;
                    int indextemp = 0;
                    int yenThaoNgoc = 0;
                    int isKhamNgoc = 0;
                    for (byte i = 1; i < arrItem.length; i++) {
                        arrItem[i] = m.reader().readByte();
                    }
                    for (byte i = 0; i < arrItem.length; i++) {
                        p.nj.removeItemBag(arrItem[i], 1);
                    }
                    for (int i = 0; i < item.option.size(); i++) {
                        if (item.option.get(i).id == 122) {
                            yenThaoNgoc = item.option.get(i).param;
                            item.option.remove(i);
                            isKhamNgoc++;
                        }
                    }

                    switch (data.type) {
                        case 0: {
                            loai = 107;
                            loai1 = 106;
                            loai2 = 108;
                            break;
                        }
                        case 1: {
                            loai = 106;
                            loai1 = 107;
                            loai2 = 108;
                            break;
                        }
                        case 2: {
                            loai = 107;
                            loai1 = 106;
                            loai2 = 108;
                            break;
                        }
                        case 3: {
                            loai = 108;
                            loai1 = 106;
                            loai2 = 107;
                            break;
                        }
                        case 4: {
                            loai = 107;
                            loai1 = 106;
                            loai2 = 108;
                            break;
                        }
                        case 5: {
                            loai = 108;
                            loai1 = 106;
                            loai2 = 107;
                            break;
                        }
                        case 6: {
                            loai = 107;
                            loai1 = 106;
                            loai2 = 108;
                            break;
                        }
                        case 7: {
                            loai = 108;
                            loai1 = 106;
                            loai2 = 107;
                            break;
                        }
                        case 8: {
                            loai = 107;
                            loai1 = 106;
                            loai2 = 108;
                            break;
                        }
                        case 9: {
                            loai = 108;
                            loai1 = 106;
                            loai2 = 107;
                            break;
                        }
                    }
                    switch ((int) itemSub.id) {
                        case 655: {
                            indextemp = 0;
                            int[] temp = new int[]{106, 107, 108};
                            for (int j = 0; j < temp.length; j++) {
                                if (temp[j] == 106) {
                                    for (int i = 0; i < itemSub.option.size(); i++) {
                                        if (itemSub.option.get(i).id == loai && itemSub.option.get(i).id == temp[j]) {
                                            item.option.add(new Option(112, 0));
                                            item.option.add(new Option(itemSub.option.get(i + 1).id, itemSub.option.get(i + 1).param));
                                            item.option.add(new Option(itemSub.option.get(i + 2).id, itemSub.option.get(i + 2).param));
                                        } else if (itemSub.option.get(i).id == temp[j]) {
                                            item.option.add(new Option(indextemp - 1, Integer.parseInt(itemSub.upgrade + "" + itemSub.option.get(i).id)));
                                            item.option.add(new Option(indextemp - 2, Integer.parseInt(itemSub.option.get(i + 1).param + "" + itemSub.option.get(i + 1).id)));
                                            item.option.add(new Option(indextemp - 3, Integer.parseInt(itemSub.option.get(i + 2).param + "" + itemSub.option.get(i + 2).id)));
                                        }
                                    }
                                }
                                if (temp[j] == 107) {
                                    for (int i = 0; i < itemSub.option.size(); i++) {
                                        if (itemSub.option.get(i).id == loai && itemSub.option.get(i).id == temp[j]) {
                                            item.option.add(new Option(112, 0));
                                            item.option.add(new Option(itemSub.option.get(i + 1).id, itemSub.option.get(i + 1).param));
                                            item.option.add(new Option(itemSub.option.get(i + 2).id, itemSub.option.get(i + 2).param));
                                        } else if (itemSub.option.get(i).id == temp[j]) {
                                            item.option.add(new Option(indextemp - 4, Integer.parseInt(itemSub.upgrade + "" + itemSub.option.get(i).id)));
                                            item.option.add(new Option(indextemp - 5, Integer.parseInt(itemSub.option.get(i + 1).param + "" + itemSub.option.get(i + 1).id)));
                                            item.option.add(new Option(indextemp - 6, Integer.parseInt(itemSub.option.get(i + 2).param + "" + itemSub.option.get(i + 2).id)));
                                        }
                                    }
                                }
                                if (temp[j] == 108) {
                                    for (int i = 0; i < itemSub.option.size(); i++) {
                                        if (itemSub.option.get(i).id == loai && itemSub.option.get(i).id == temp[j]) {
                                            item.option.add(new Option(112, 0));
                                            item.option.add(new Option(itemSub.option.get(i + 1).id, itemSub.option.get(i + 1).param));
                                            item.option.add(new Option(itemSub.option.get(i + 2).id, itemSub.option.get(i + 2).param));
                                        } else if (itemSub.option.get(i).id == temp[j]) {
                                            item.option.add(new Option(indextemp - 7, Integer.parseInt(itemSub.upgrade + "" + itemSub.option.get(i).id)));
                                            item.option.add(new Option(indextemp - 8, Integer.parseInt(itemSub.option.get(i + 1).param + "" + itemSub.option.get(i + 1).id)));
                                            item.option.add(new Option(indextemp - 9, Integer.parseInt(itemSub.option.get(i + 2).param + "" + itemSub.option.get(i + 2).id)));
                                        }
                                    }
                                }
                            }

                            for (int i = 0; i < itemSub.option.size(); i++) {
                                if (itemSub.option.get(i).id == 104) {
                                    item.option.add(new Option(indextemp - 10, itemSub.option.get(i).param));
                                } else if (itemSub.option.get(i).id == 123) {
                                    item.option.add(new Option(indextemp - 11, itemSub.option.get(i).param));
                                    yenThaoNgoc += itemSub.option.get(i).param * 2;
                                }
                            }
                            item.option.add(new Option(indextemp - 12, itemSub.upgrade));
                            item.option.add(new Option(indextemp - 13, itemSub.buyCoinLock));
                            break;
                        }
                        case 654: {
                            indextemp = -15;
                            int[] temp1 = new int[]{106, 107, 108};
                            for (int j = 0; j < temp1.length; j++) {
                                if (temp1[j] == 106) {
                                    for (int i = 0; i < itemSub.option.size(); i++) {
                                        if (itemSub.option.get(i).id == loai && itemSub.option.get(i).id == temp1[j]) {
                                            item.option.add(new Option(111, 0));
                                            item.option.add(new Option(itemSub.option.get(i + 1).id, itemSub.option.get(i + 1).param));
                                            item.option.add(new Option(itemSub.option.get(i + 2).id, itemSub.option.get(i + 2).param));
                                        } else if (itemSub.option.get(i).id == temp1[j]) {
                                            item.option.add(new Option(indextemp - 1, Integer.parseInt(itemSub.upgrade + "" + itemSub.option.get(i).id)));
                                            item.option.add(new Option(indextemp - 2, Integer.parseInt(itemSub.option.get(i + 1).param + "" + itemSub.option.get(i + 1).id)));
                                            item.option.add(new Option(indextemp - 3, Integer.parseInt(itemSub.option.get(i + 2).param + "" + itemSub.option.get(i + 2).id)));
                                        }
                                    }
                                }
                                if (temp1[j] == 107) {
                                    for (int i = 0; i < itemSub.option.size(); i++) {
                                        if (itemSub.option.get(i).id == loai && itemSub.option.get(i).id == temp1[j]) {
                                            item.option.add(new Option(111, 0));
                                            item.option.add(new Option(itemSub.option.get(i + 1).id, itemSub.option.get(i + 1).param));
                                            item.option.add(new Option(itemSub.option.get(i + 2).id, itemSub.option.get(i + 2).param));
                                        } else if (itemSub.option.get(i).id == temp1[j]) {
                                            item.option.add(new Option(indextemp - 4, Integer.parseInt(itemSub.upgrade + "" + itemSub.option.get(i).id)));
                                            item.option.add(new Option(indextemp - 5, Integer.parseInt(itemSub.option.get(i + 1).param + "" + itemSub.option.get(i + 1).id)));
                                            item.option.add(new Option(indextemp - 6, Integer.parseInt(itemSub.option.get(i + 2).param + "" + itemSub.option.get(i + 2).id)));
                                        }
                                    }
                                }
                                if (temp1[j] == 108) {
                                    for (int i = 0; i < itemSub.option.size(); i++) {
                                        if (itemSub.option.get(i).id == loai && itemSub.option.get(i).id == temp1[j]) {
                                            item.option.add(new Option(111, 0));
                                            item.option.add(new Option(itemSub.option.get(i + 1).id, itemSub.option.get(i + 1).param));
                                            item.option.add(new Option(itemSub.option.get(i + 2).id, itemSub.option.get(i + 2).param));
                                        } else if (itemSub.option.get(i).id == temp1[j]) {
                                            item.option.add(new Option(indextemp - 7, Integer.parseInt(itemSub.upgrade + "" + itemSub.option.get(i).id)));
                                            item.option.add(new Option(indextemp - 8, Integer.parseInt(itemSub.option.get(i + 1).param + "" + itemSub.option.get(i + 1).id)));
                                            item.option.add(new Option(indextemp - 9, Integer.parseInt(itemSub.option.get(i + 2).param + "" + itemSub.option.get(i + 2).id)));
                                        }
                                    }
                                }
                            }

                            for (int i = 0; i < itemSub.option.size(); i++) {
                                if (itemSub.option.get(i).id == 104) {
                                    item.option.add(new Option(indextemp - 10, itemSub.option.get(i).param));
                                } else if (itemSub.option.get(i).id == 123) {
                                    item.option.add(new Option(indextemp - 11, itemSub.option.get(i).param));
                                    yenThaoNgoc += itemSub.option.get(i).param * 2;
                                }
                            }
                            item.option.add(new Option(indextemp - 12, itemSub.upgrade));
                            item.option.add(new Option(indextemp - 13, itemSub.buyCoinLock));
                            break;
                        }
                        case 653: {
                            indextemp = -30;
                            int[] temp2 = new int[]{106, 107, 108};
                            for (int j = 0; j < temp2.length; j++) {
                                if (temp2[j] == 106) {
                                    for (int i = 0; i < itemSub.option.size(); i++) {
                                        if (itemSub.option.get(i).id == loai && itemSub.option.get(i).id == temp2[j]) {
                                            item.option.add(new Option(110, 0));
                                            item.option.add(new Option(itemSub.option.get(i + 1).id, itemSub.option.get(i + 1).param));
                                            item.option.add(new Option(itemSub.option.get(i + 2).id, itemSub.option.get(i + 2).param));
                                        } else if (itemSub.option.get(i).id == temp2[j]) {
                                            item.option.add(new Option(indextemp - 1, Integer.parseInt(itemSub.upgrade + "" + itemSub.option.get(i).id)));
                                            item.option.add(new Option(indextemp - 2, Integer.parseInt(itemSub.option.get(i + 1).param + "" + itemSub.option.get(i + 1).id)));
                                            item.option.add(new Option(indextemp - 3, Integer.parseInt(itemSub.option.get(i + 2).param + "" + itemSub.option.get(i + 2).id)));
                                        }
                                    }
                                }
                                if (temp2[j] == 107) {
                                    for (int i = 0; i < itemSub.option.size(); i++) {
                                        if (itemSub.option.get(i).id == loai && itemSub.option.get(i).id == temp2[j]) {
                                            item.option.add(new Option(110, 0));
                                            item.option.add(new Option(itemSub.option.get(i + 1).id, itemSub.option.get(i + 1).param));
                                            item.option.add(new Option(itemSub.option.get(i + 2).id, itemSub.option.get(i + 2).param));
                                        } else if (itemSub.option.get(i).id == temp2[j]) {
                                            item.option.add(new Option(indextemp - 4, Integer.parseInt(itemSub.upgrade + "" + itemSub.option.get(i).id)));
                                            item.option.add(new Option(indextemp - 5, Integer.parseInt(itemSub.option.get(i + 1).param + "" + itemSub.option.get(i + 1).id)));
                                            item.option.add(new Option(indextemp - 6, Integer.parseInt(itemSub.option.get(i + 2).param + "" + itemSub.option.get(i + 2).id)));
                                        }
                                    }
                                }
                                if (temp2[j] == 108) {
                                    for (int i = 0; i < itemSub.option.size(); i++) {
                                        if (itemSub.option.get(i).id == loai && itemSub.option.get(i).id == temp2[j]) {
                                            item.option.add(new Option(110, 0));
                                            item.option.add(new Option(itemSub.option.get(i + 1).id, itemSub.option.get(i + 1).param));
                                            item.option.add(new Option(itemSub.option.get(i + 2).id, itemSub.option.get(i + 2).param));
                                        } else if (itemSub.option.get(i).id == temp2[j]) {
                                            item.option.add(new Option(indextemp - 7, Integer.parseInt(itemSub.upgrade + "" + itemSub.option.get(i).id)));
                                            item.option.add(new Option(indextemp - 8, Integer.parseInt(itemSub.option.get(i + 1).param + "" + itemSub.option.get(i + 1).id)));
                                            item.option.add(new Option(indextemp - 9, Integer.parseInt(itemSub.option.get(i + 2).param + "" + itemSub.option.get(i + 2).id)));
                                        }
                                    }
                                }
                            }

                            for (int i = 0; i < itemSub.option.size(); i++) {
                                if (itemSub.option.get(i).id == 104) {
                                    item.option.add(new Option(indextemp - 10, itemSub.option.get(i).param));
                                } else if (itemSub.option.get(i).id == 123) {
                                    item.option.add(new Option(indextemp - 11, itemSub.option.get(i).param));
                                    yenThaoNgoc += itemSub.option.get(i).param * 2;
                                }
                            }
                            item.option.add(new Option(indextemp - 12, itemSub.upgrade));
                            item.option.add(new Option(indextemp - 13, itemSub.buyCoinLock));
                            break;
                        }
                        case 652: {
                            indextemp = -45;
                            int[] temp3 = new int[]{106, 107, 108};
                            for (int j = 0; j < temp3.length; j++) {
                                if (temp3[j] == 106) {
                                    for (int i = 0; i < itemSub.option.size(); i++) {
                                        if (itemSub.option.get(i).id == loai && itemSub.option.get(i).id == temp3[j]) {
                                            item.option.add(new Option(109, 0));
                                            item.option.add(new Option(itemSub.option.get(i + 1).id, itemSub.option.get(i + 1).param));
                                            item.option.add(new Option(itemSub.option.get(i + 2).id, itemSub.option.get(i + 2).param));
                                        } else if (itemSub.option.get(i).id == temp3[j]) {
                                            item.option.add(new Option(indextemp - 1, Integer.parseInt(itemSub.upgrade + "" + itemSub.option.get(i).id)));
                                            item.option.add(new Option(indextemp - 2, Integer.parseInt(itemSub.option.get(i + 1).param + "" + itemSub.option.get(i + 1).id)));
                                            item.option.add(new Option(indextemp - 3, Integer.parseInt(itemSub.option.get(i + 2).param + "" + itemSub.option.get(i + 2).id)));
                                        }
                                    }
                                }
                                if (temp3[j] == 107) {
                                    for (int i = 0; i < itemSub.option.size(); i++) {
                                        if (itemSub.option.get(i).id == loai && itemSub.option.get(i).id == temp3[j]) {
                                            item.option.add(new Option(109, 0));
                                            item.option.add(new Option(itemSub.option.get(i + 1).id, itemSub.option.get(i + 1).param));
                                            item.option.add(new Option(itemSub.option.get(i + 2).id, itemSub.option.get(i + 2).param));
                                        } else if (itemSub.option.get(i).id == temp3[j]) {
                                            item.option.add(new Option(indextemp - 4, Integer.parseInt(itemSub.upgrade + "" + itemSub.option.get(i).id)));
                                            item.option.add(new Option(indextemp - 5, Integer.parseInt(itemSub.option.get(i + 1).param + "" + itemSub.option.get(i + 1).id)));
                                            item.option.add(new Option(indextemp - 6, Integer.parseInt(itemSub.option.get(i + 2).param + "" + itemSub.option.get(i + 2).id)));
                                        }
                                    }
                                }
                                if (temp3[j] == 108) {
                                    for (int i = 0; i < itemSub.option.size(); i++) {
                                        if (itemSub.option.get(i).id == loai && itemSub.option.get(i).id == temp3[j]) {
                                            item.option.add(new Option(109, 0));
                                            item.option.add(new Option(itemSub.option.get(i + 1).id, itemSub.option.get(i + 1).param));
                                            item.option.add(new Option(itemSub.option.get(i + 2).id, itemSub.option.get(i + 2).param));
                                        } else if (itemSub.option.get(i).id == temp3[j]) {
                                            item.option.add(new Option(indextemp - 7, Integer.parseInt(itemSub.upgrade + "" + itemSub.option.get(i).id)));
                                            item.option.add(new Option(indextemp - 8, Integer.parseInt(itemSub.option.get(i + 1).param + "" + itemSub.option.get(i + 1).id)));
                                            item.option.add(new Option(indextemp - 9, Integer.parseInt(itemSub.option.get(i + 2).param + "" + itemSub.option.get(i + 2).id)));
                                        }
                                    }
                                }
                            }

                            for (int i = 0; i < itemSub.option.size(); i++) {
                                if (itemSub.option.get(i).id == 104) {
                                    item.option.add(new Option(indextemp - 10, itemSub.option.get(i).param));
                                } else if (itemSub.option.get(i).id == 123) {
                                    item.option.add(new Option(indextemp - 11, itemSub.option.get(i).param));
                                    yenThaoNgoc += itemSub.option.get(i).param * 2;
                                }
                            }
                            item.option.add(new Option(indextemp - 12, itemSub.upgrade));
                            item.option.add(new Option(indextemp - 13, itemSub.buyCoinLock));

                            break;
                        }
                        default: {
                            break;
                        }

                    }
                    item.option.add(new Option(122, yenThaoNgoc));
                    m.cleanup();
                    m = new Message(21);
                    m.writer().writeByte(5);
                    m.writer().writeInt(p.luong);
                    m.writer().writeInt(p.nj.xu);
                    m.writer().writeInt(money2);
                    m.writer().writeByte(item.upgrade);
                    m.writer().flush();
                    p.sendMessage(m);
                    m.cleanup();
                    m = new Message(42);
                    m.writer().writeByte(3);
                    m.writer().writeByte(indexItem);
                    m.writer().writeLong(item.expires);
                    if (ItemData.isTypeUIME(type)) {
                        m.writer().writeInt(item.buyCoinLock);
                    }
                    if (ItemData.isTypeUIShop(type) || ItemData.isTypeUIShopLock(type) || ItemData.isTypeMounts(type) || ItemData.isTypeUIStore(type) || ItemData.isTypeUIBook(type) || ItemData.isTypeUIFashion(type) || ItemData.isTypeUIClanShop(type)) {
                        m.writer().writeInt(item.buyCoin);
                        m.writer().writeInt(item.buyCoinLock);
                        m.writer().writeInt(item.buyGold);
                    }
                    if (ItemData.isTypeBody(item.id) || ItemData.isTypeMounts(item.id) || ItemData.isTypeNgocKham(item.id)) {
                        m.writer().writeByte(item.sys);//thuoc tinh
                        if (item.option != null) {
                            for (Option Option : item.option) {
                                if (Option.id > 0) {
                                    m.writer().writeByte(Option.id);
                                    m.writer().writeInt(Option.param);
                                }
                            }
                        }
                    }
                    m.writer().flush();
                    p.sendMessage(m);
                    m.cleanup();
                }
                break;
            }
            //luyện
            case 1: {
                byte indexItemLuyenNgoc = m.reader().readByte();
                Item itemLuyenNgoc = p.nj.getIndexBag(indexItemLuyenNgoc);
                int expLuyenNgoc = 0;
                int totalExp = 0;
                byte capCu = itemLuyenNgoc.getUpgrade();
                if (itemLuyenNgoc != null) {
                    if (itemLuyenNgoc.upgrade >= 10) {
                        p.session.sendMessageLog("Ngọc đã đạt giới hạn tối đa");
                        break;
                    }
                    ItemData dataItemLN = ItemData.ItemDataId(itemLuyenNgoc.id);
                    if (dataItemLN.level > p.nj.getLevel()) {
                        p.session.sendMessageLog("Level của bạn chưa đủ để luyện ngọc này");
                        break;
                    }
                    for (byte i = 0; i < itemLuyenNgoc.option.size(); i++) {
                        if (itemLuyenNgoc.option.get(i).id == 104) {
                            expLuyenNgoc = itemLuyenNgoc.option.get(i).param;
                        }
                    }
                    byte[] arrIndex = new byte[m.reader().available()];
                    int exp2 = 0;
                    for (byte i = 0; i < arrIndex.length; i++) {
                        byte index2 = m.reader().readByte();
                        Item item2 = p.nj.getIndexBag(index2);
                        if (item2 != null) {
                            exp2 += arrNgocKhamEXP[item2.upgrade][1];
                        }
                        arrIndex[i] = index2;
                    }
                    totalExp = expLuyenNgoc + exp2;
                    for (byte i = 0; i < arrIndex.length; i++) {
                        p.nj.removeItemBag(arrIndex[i], 1);
                    }
                }
                int upgrade = itemLuyenNgoc.upgrade;
                int isupgrade = 0;
                int chenhlech = 0;
                
                for (byte i = 1; i < arrLuyenNgocEXP.length; i++) {
                    if (totalExp > arrLuyenNgocEXP[i][1] && totalExp < arrLuyenNgocEXP[i][0]) {
                        if (upgrade < i) {
                            chenhlech = i - upgrade;
                            upgrade = i;
                            isupgrade++;

                            totalExp = totalExp - arrLuyenNgocEXP[i][1];
                        } 
                            else {
                                totalExp = totalExp;
                            }
                        break;
                    }
                }
                
                itemLuyenNgoc.setLock(true);
                itemLuyenNgoc.upgrade = (byte) upgrade;
                int type = 3;
                m.cleanup();
                m = new Message(124);
                m.writer().writeByte(1);
                m.writer().writeInt(p.luong);
                m.writer().writeInt(p.nj.xu);
                m.writer().writeInt(p.nj.yen);
                m.writer().writeByte(upgrade);
                m.writer().flush();
                p.nj.sendMessage(m);
                m.cleanup();
                m = new Message(42);
                m.writer().writeByte(3);
                m.writer().writeByte(indexItemLuyenNgoc);
                m.writer().writeLong(itemLuyenNgoc.expires);
                if (ItemData.isTypeUIME(type)) {
                    m.writer().writeInt(itemLuyenNgoc.buyCoinLock);
                }
                if (ItemData.isTypeUIShop(type) || ItemData.isTypeUIShopLock(type) || ItemData.isTypeMounts(type) || ItemData.isTypeUIStore(type) || ItemData.isTypeUIBook(type) || ItemData.isTypeUIFashion(type) || ItemData.isTypeUIClanShop(type)) {
                    m.writer().writeInt(itemLuyenNgoc.buyCoin);
                    m.writer().writeInt(itemLuyenNgoc.buyCoinLock);
                    m.writer().writeInt(itemLuyenNgoc.buyGold);
                }
                if (ItemData.isTypeBody(itemLuyenNgoc.id) || ItemData.isTypeMounts(itemLuyenNgoc.id) || ItemData.isTypeNgocKham(itemLuyenNgoc.id)) {
                    m.writer().writeByte(itemLuyenNgoc.sys);//thuoc tinh
                    int i = 0;
                    if (itemLuyenNgoc.option != null) {
                        for (Option Option : itemLuyenNgoc.option) {
                            m.writer().writeByte(Option.id);
                            if (Option.id == 104) {
                                m.writer().writeInt(totalExp);
                                itemLuyenNgoc.option.get(i).param = totalExp;
                            } else {
                                if (isupgrade == 1) {
                                    if (Option.id != 106 || Option.id != 107 || Option.id != 108 || Option.id != 104 || Option.id != 123) {
                                        int value = itemLuyenNgoc.option.get(i).param;
                                        
                                        if (value > 0) {
                                            for(byte da = capCu; da <= itemLuyenNgoc.getUpgrade();da++){
                                                if(da != capCu){
                                                    value += heSoNangNgoc(Option.id)*chiSoNgoc5(da);
                                                }
                                                
                                            }
                                                
                                            
                                        } 
                                        else 
                                            if (value >= -50 && value < 0) {
                                            value -= util.nextInt(20);
                                        } else if (value < -50) {
                                            value -= util.nextInt(50, 100);
                                        }
                                        itemLuyenNgoc.option.get(i).param = value;
                                        m.writer().writeInt(value);
                                    } else if (Option.id == 123) {
                                        int value = itemLuyenNgoc.option.get(i).param * 2 * chenhlech;
                                        itemLuyenNgoc.option.get(i).param = value;
                                        m.writer().writeInt(value);
                                    } else {
                                        m.writer().writeInt(Option.param);
                                    }
                                } else {
                                    m.writer().writeInt(Option.param);
                                }
                            }
                            i++;

                        }
                    }
                }
                m.writer().flush();
                p.nj.sendMessage(m);
                m.cleanup();
                p.sendYellowMessage("Luyện ngọc thành công");
                break;
            }
            //Gọt
            case 2: {
                byte indexItemGotNgoc = m.reader().readByte();
                Item itemGotNgoc = p.nj.getIndexBag(indexItemGotNgoc);
                int expGotNgoc = 0;
                ItemData dataItemLN = ItemData.ItemDataId(itemGotNgoc.id);
                if (dataItemLN.level > p.nj.getLevel()) {
                    p.session.sendMessageLog("Level của bạn chưa đủ để gọt ngọc này");
                    break;
                }

                int money2 = p.nj.xu - coinGotngoc[itemGotNgoc.upgrade];
                if (coinGotngoc[itemGotNgoc.upgrade] <= p.nj.xu) {
                    p.nj.upxu(-coinGotngoc[itemGotNgoc.upgrade]);
                } //
                else if (coinGotngoc[itemGotNgoc.upgrade] >= p.nj.xu) {
                    int coin = coinGotngoc[itemGotNgoc.upgrade] - p.nj.xu;
                    if (coin > p.nj.yen) {
                        p.session.sendMessageLog("Không đủ xu và yên để gọt ngọc");
                        break;
                    }
                    p.nj.upxu(-p.nj.xu);
                    p.nj.upyen(-coin);
                }

                int typeGotNgoc = 3;
                m.cleanup();
                m = new Message(124);
                m.writer().writeByte(2);
                m.writer().writeInt(p.luong);
                m.writer().writeInt(p.nj.xu);
                m.writer().writeInt(p.nj.yen);
                m.writer().writeByte(itemGotNgoc.upgrade);
                m.writer().flush();
                p.nj.sendMessage(m);
                m.cleanup();
                m = new Message(42);
                m.writer().writeByte(3);
                m.writer().writeByte(indexItemGotNgoc);
                m.writer().writeLong(itemGotNgoc.expires);
                if (ItemData.isTypeUIME(typeGotNgoc)) {
                    m.writer().writeInt(itemGotNgoc.buyCoinLock);
                }
                if (ItemData.isTypeUIShop(typeGotNgoc) || ItemData.isTypeUIShopLock(typeGotNgoc) || ItemData.isTypeMounts(typeGotNgoc) || ItemData.isTypeUIStore(typeGotNgoc) || ItemData.isTypeUIBook(typeGotNgoc) || ItemData.isTypeUIFashion(typeGotNgoc) || ItemData.isTypeUIClanShop(typeGotNgoc)) {
                    m.writer().writeInt(itemGotNgoc.buyCoin);
                    m.writer().writeInt(itemGotNgoc.buyCoinLock);
                    m.writer().writeInt(itemGotNgoc.buyGold);
                }
                if (ItemData.isTypeBody(itemGotNgoc.id) || ItemData.isTypeMounts(itemGotNgoc.id) || ItemData.isTypeNgocKham(itemGotNgoc.id)) {
                    m.writer().writeByte(itemGotNgoc.sys);//thuoc tinh
                    int i = 0;
                    if (itemGotNgoc.option != null) {
                        for (Option Option : itemGotNgoc.option) {
                            m.writer().writeByte(Option.id);
                            if (Option.id == 104) {
                                m.writer().writeInt(Option.param);
                            } else {
                                if (Option.id == 73 || Option.id == 105 || Option.id == 114 || Option.id == 115 || Option.id == 116 || Option.id == 117 || Option.id == 118 || Option.id == 119 || Option.id == 120 || Option.id == 124 || Option.id == 125 || Option.id == 126) {
                                    if (itemGotNgoc.option.get(i).param < -1) {
                                        int value = itemGotNgoc.option.get(i).param;
                                        if (value > -20) {
                                            value += util.nextInt(1, 50);
                                        } else if (value <= -20 && value > -100) {
                                            value += util.nextInt(10, 100);
                                        } else if (value <= -100 && value > -200) {
                                            value += util.nextInt(20, 150);
                                        } else if (value <= -200) {
                                            value += util.nextInt(30, 200);
                                        }
                                        if (value >= 0) {
                                            value = -1;
                                        }
                                        itemGotNgoc.option.get(i).param = value;
                                        m.writer().writeInt(value);
                                    } else {
                                        m.writer().writeInt(Option.param);
                                    }
                                } else {
                                    m.writer().writeInt(Option.param);
                                }
                            }
                            i++;
                        }
                    }
                }
                m.writer().flush();
                p.nj.sendMessage(m);
                m.cleanup();
            //    p.endLoad(true);
                p.sendYellowMessage("Ngọc đã được gọt");
                break;
            }
            //Tháo
            case 3: {
                byte indexItemThaoNgoc = m.reader().readByte();
                Item itemThaoNgoc = p.nj.getIndexBag(indexItemThaoNgoc);
                ItemData data = ItemData.ItemDataId(itemThaoNgoc.id);
                int expThaoNgoc = 0;
                Item itemNgoc = new Item();
                itemNgoc.isLock = true;
                int loai = 0;
                int loai1 = 0;
                int loai2 = 0;
                int indextemp = 0;
                switch (data.type) {
                    case 0:
                        loai = 107;
                        loai1 = 106;
                        loai2 = 108;
                        break;
                    case 1:
                        loai = 106;
                        loai1 = 107;
                        loai2 = 108;
                        break;
                    case 2:
                        loai = 107;
                        loai1 = 106;
                        loai2 = 108;
                        break;
                    case 3:
                        loai = 108;
                        loai1 = 106;
                        loai2 = 107;
                        break;
                    case 4:
                        loai = 107;
                        loai1 = 106;
                        loai2 = 108;
                        break;
                    case 5:
                        loai = 108;
                        loai1 = 106;
                        loai2 = 107;
                        break;
                    case 6:
                        loai = 107;
                        loai1 = 106;
                        loai2 = 108;
                        break;
                    case 7:
                        loai = 108;
                        loai1 = 106;
                        loai2 = 107;
                        break;
                    case 8:
                        loai = 107;
                        loai1 = 106;
                        loai2 = 108;
                        break;
                    case 9:
                        loai = 108;
                        loai1 = 106;
                        loai2 = 107;
                        break;
                }
                int[] temp = new int[]{106, 107, 108};
                int a = 0;
                for (int i = 0; i < itemThaoNgoc.option.size(); i++) {
                    if (itemThaoNgoc.option.get(i).id == 109 || itemThaoNgoc.option.get(i).id == 110 || itemThaoNgoc.option.get(i).id == 111 || itemThaoNgoc.option.get(i).id == 112) {
                        a++;
                    }
                }
                if (a == 0) {
                    p.session.sendMessageLog("Vật phẩm này chưa được khảm ngọc.");
                    return;
                }
                int yenThaoNgoc = 0;
                for (int i = 0; i < itemThaoNgoc.option.size(); i++) {
                    if (itemThaoNgoc.option.get(i).id == 122) {
                        yenThaoNgoc = itemThaoNgoc.option.get(i).param;
//                        itemThaoNgoc.option.remove(i);
                    }
                }
                if (yenThaoNgoc <= p.nj.yen) {
                    p.nj.upyen(-yenThaoNgoc);
                } else if (yenThaoNgoc >= p.nj.yen) {
                    int coin = yenThaoNgoc - p.nj.yen;
                    if (coin > p.nj.xu) {
                        p.session.sendMessageLog("Không đủ xu và yên để tháo ngọc");
                        return;
                    }
                    p.nj.upyen(-p.nj.yen);
                    p.nj.upxu(-coin);
                }
                int[] tempa = new int[a];
                a = 0;
                for (int i = 0; i < itemThaoNgoc.option.size(); i++) {
                    if (itemThaoNgoc.option.get(i).id == 109 || itemThaoNgoc.option.get(i).id == 110 || itemThaoNgoc.option.get(i).id == 111 || itemThaoNgoc.option.get(i).id == 112) {
                        tempa[a] = itemThaoNgoc.option.get(i).id;
                        a++;
                    }
                }
                int a2 = 0;
                for (int i = 0; i < itemThaoNgoc.option.size(); i++) {
                    if (itemThaoNgoc.option.get(i).id == 109 || itemThaoNgoc.option.get(i).id == 110 || itemThaoNgoc.option.get(i).id == 111 || itemThaoNgoc.option.get(i).id == 112 || itemThaoNgoc.option.get(i).id < 0) {
                        a2++;
                    }
                }
                int[] tempIndex = new int[a2];
                int[] tempIndexNagative = new int[a2];
                a2 = 0;
                for (int i = 0; i < itemThaoNgoc.option.size(); i++) {
                    if (itemThaoNgoc.option.get(i).id == 109 || itemThaoNgoc.option.get(i).id == 110 || itemThaoNgoc.option.get(i).id == 111 || itemThaoNgoc.option.get(i).id == 112 || itemThaoNgoc.option.get(i).id < 0) {
                        tempIndex[a2] = i;
                        tempIndexNagative[a2] = itemThaoNgoc.option.get(i).id;
                        a2++;
                    }
                }
                for (int ai = 0; ai < tempa.length; ai++) {
                    itemNgoc = new Item();
                    itemNgoc.isLock = false;
                    switch (tempa[ai]) {
                        case 112: {
                            indextemp = 0;
                            for (int j = 0; j < temp.length; j++) {
                                if (temp[j] == 106 && temp[j] == loai) {
                                    for (int i = 0; i < itemThaoNgoc.option.size(); i++) {
                                        if (itemThaoNgoc.option.get(i).id == 112) {
                                            itemNgoc.option.add(new Option(loai, 0));
                                            itemNgoc.option.add(new Option(itemThaoNgoc.option.get(i + 1).id, itemThaoNgoc.option.get(i + 1).param));
                                            itemNgoc.option.add(new Option(itemThaoNgoc.option.get(i + 2).id, itemThaoNgoc.option.get(i + 2).param));
                                        }
                                    }
                                } else if (temp[j] == 106) {
                                    for (int i = 0; i < itemThaoNgoc.option.size(); i++) {
                                        if (itemThaoNgoc.option.get(i).id == indextemp - 1) {
                                            itemNgoc.option.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i).param).substring(String.valueOf(itemThaoNgoc.option.get(i).param).length() - 3, String.valueOf(itemThaoNgoc.option.get(i).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i).param).substring(0, String.valueOf(itemThaoNgoc.option.get(i).param).length() - 3))));
                                            itemNgoc.option.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i + 1).param).substring(String.valueOf(itemThaoNgoc.option.get(i + 1).param).length() - 3, String.valueOf(itemThaoNgoc.option.get(i + 1).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i + 1).param).substring(0, String.valueOf(itemThaoNgoc.option.get(i + 1).param).length() - 3))));
                                            itemNgoc.option.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i + 2).param).substring(String.valueOf(itemThaoNgoc.option.get(i + 2).param).length() - 3, String.valueOf(itemThaoNgoc.option.get(i + 2).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i + 2).param).substring(0, String.valueOf(itemThaoNgoc.option.get(i + 2).param).length() - 3))));
                                        }
                                    }
                                }
                                if (temp[j] == 107 && temp[j] == loai) {
                                    for (int i = 0; i < itemThaoNgoc.option.size(); i++) {
                                        if (itemThaoNgoc.option.get(i).id == 112) {
                                            itemNgoc.option.add(new Option(loai, 0));
                                            itemNgoc.option.add(new Option(itemThaoNgoc.option.get(i + 1).id, itemThaoNgoc.option.get(i + 1).param));
                                            itemNgoc.option.add(new Option(itemThaoNgoc.option.get(i + 2).id, itemThaoNgoc.option.get(i + 2).param));
                                        }
                                    }
                                } else if (temp[j] == 107) {
                                    for (int i = 0; i < itemThaoNgoc.option.size(); i++) {
                                        if (itemThaoNgoc.option.get(i).id == indextemp - 4) {
                                            itemNgoc.option.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i).param).substring(String.valueOf(itemThaoNgoc.option.get(i).param).length() - 3, String.valueOf(itemThaoNgoc.option.get(i).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i).param).substring(0, String.valueOf(itemThaoNgoc.option.get(i).param).length() - 3))));
                                            itemNgoc.option.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i + 1).param).substring(String.valueOf(itemThaoNgoc.option.get(i + 1).param).length() - 3, String.valueOf(itemThaoNgoc.option.get(i + 1).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i + 1).param).substring(0, String.valueOf(itemThaoNgoc.option.get(i + 1).param).length() - 3))));
                                            itemNgoc.option.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i + 2).param).substring(String.valueOf(itemThaoNgoc.option.get(i + 2).param).length() - 3, String.valueOf(itemThaoNgoc.option.get(i + 2).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i + 2).param).substring(0, String.valueOf(itemThaoNgoc.option.get(i + 2).param).length() - 3))));
                                        }
                                    }
                                }
                                if (temp[j] == 108 && temp[j] == loai) {
                                    for (int i = 0; i < itemThaoNgoc.option.size(); i++) {
                                        if (itemThaoNgoc.option.get(i).id == 112) {
                                            itemNgoc.option.add(new Option(loai, 0));
                                            itemNgoc.option.add(new Option(itemThaoNgoc.option.get(i + 1).id, itemThaoNgoc.option.get(i + 1).param));
                                            itemNgoc.option.add(new Option(itemThaoNgoc.option.get(i + 2).id, itemThaoNgoc.option.get(i + 2).param));
                                        }
                                    }
                                } else if (temp[j] == 108) {
                                    for (int i = 0; i < itemThaoNgoc.option.size(); i++) {
                                        if (itemThaoNgoc.option.get(i).id == indextemp - 7) {
                                            itemNgoc.option.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i).param).substring(String.valueOf(itemThaoNgoc.option.get(i).param).length() - 3, String.valueOf(itemThaoNgoc.option.get(i).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i).param).substring(0, String.valueOf(itemThaoNgoc.option.get(i).param).length() - 3))));
                                            itemNgoc.option.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i + 1).param).substring(String.valueOf(itemThaoNgoc.option.get(i + 1).param).length() - 2, String.valueOf(itemThaoNgoc.option.get(i + 1).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i + 1).param).substring(0, String.valueOf(itemThaoNgoc.option.get(i + 1).param).length() - 2))));
                                            itemNgoc.option.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i + 2).param).substring(String.valueOf(itemThaoNgoc.option.get(i + 2).param).length() - 3, String.valueOf(itemThaoNgoc.option.get(i + 2).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i + 2).param).substring(0, String.valueOf(itemThaoNgoc.option.get(i + 2).param).length() - 3))));
                                        }
                                    }
                                }
                            }
                            for (int i = 0; i < itemThaoNgoc.option.size(); i++) {
                                if (itemThaoNgoc.option.get(i).id == indextemp - 10) {
                                    itemNgoc.option.add(new Option(104, itemThaoNgoc.option.get(i).param));
                                } else if (itemThaoNgoc.option.get(i).id == indextemp - 11) {
                                    itemNgoc.option.add(new Option(123, itemThaoNgoc.option.get(i).param));
                                } else if (itemThaoNgoc.option.get(i).id == indextemp - 12) {
                                    itemNgoc.upgrade = (byte) itemThaoNgoc.option.get(i).param;
                                } else if (itemThaoNgoc.option.get(i).id == indextemp - 13) {
                                    itemNgoc.buyCoinLock = itemThaoNgoc.option.get(i).param;
                                }
                            }
                            itemNgoc.id = 655;
                            p.nj.addItemBag(false, itemNgoc);
                            break;
                        }
                        case 111: {
                            indextemp = -15;
                            for (int j = 0; j < temp.length; j++) {
                                if (temp[j] == 106 && temp[j] == loai) {
                                    for (int i = 0; i < itemThaoNgoc.option.size(); i++) {
                                        if (itemThaoNgoc.option.get(i).id == 111) {
                                            itemNgoc.option.add(new Option(loai, 0));
                                            itemNgoc.option.add(new Option(itemThaoNgoc.option.get(i + 1).id, itemThaoNgoc.option.get(i + 1).param));
                                            itemNgoc.option.add(new Option(itemThaoNgoc.option.get(i + 2).id, itemThaoNgoc.option.get(i + 2).param));
                                        }
                                    }
                                } else if (temp[j] == 106) {
                                    for (int i = 0; i < itemThaoNgoc.option.size(); i++) {
                                        if (itemThaoNgoc.option.get(i).id == indextemp - 1) {
                                            itemNgoc.option.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i).param).substring(String.valueOf(itemThaoNgoc.option.get(i).param).length() - 3, String.valueOf(itemThaoNgoc.option.get(i).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i).param).substring(0, String.valueOf(itemThaoNgoc.option.get(i).param).length() - 3))));
                                            itemNgoc.option.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i + 1).param).substring(String.valueOf(itemThaoNgoc.option.get(i + 1).param).length() - 2, String.valueOf(itemThaoNgoc.option.get(i + 1).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i + 1).param).substring(0, String.valueOf(itemThaoNgoc.option.get(i + 1).param).length() - 2))));
                                            itemNgoc.option.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i + 2).param).substring(String.valueOf(itemThaoNgoc.option.get(i + 2).param).length() - 3, String.valueOf(itemThaoNgoc.option.get(i + 2).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i + 2).param).substring(0, String.valueOf(itemThaoNgoc.option.get(i + 2).param).length() - 3))));
                                        }
                                    }
                                }
                                if (temp[j] == 107 && temp[j] == loai) {
                                    for (int i = 0; i < itemThaoNgoc.option.size(); i++) {
                                        if (itemThaoNgoc.option.get(i).id == 111) {
                                            itemNgoc.option.add(new Option(loai, 0));
                                            itemNgoc.option.add(new Option(itemThaoNgoc.option.get(i + 1).id, itemThaoNgoc.option.get(i + 1).param));
                                            itemNgoc.option.add(new Option(itemThaoNgoc.option.get(i + 2).id, itemThaoNgoc.option.get(i + 2).param));
                                        }
                                    }
                                } else if (temp[j] == 107) {
                                    for (int i = 0; i < itemThaoNgoc.option.size(); i++) {
                                        if (itemThaoNgoc.option.get(i).id == indextemp - 4) {
                                            itemNgoc.option.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i).param).substring(String.valueOf(itemThaoNgoc.option.get(i).param).length() - 3, String.valueOf(itemThaoNgoc.option.get(i).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i).param).substring(0, String.valueOf(itemThaoNgoc.option.get(i).param).length() - 3))));
                                            itemNgoc.option.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i + 1).param).substring(String.valueOf(itemThaoNgoc.option.get(i + 1).param).length() - 3, String.valueOf(itemThaoNgoc.option.get(i + 1).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i + 1).param).substring(0, String.valueOf(itemThaoNgoc.option.get(i + 1).param).length() - 3))));
                                            itemNgoc.option.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i + 2).param).substring(String.valueOf(itemThaoNgoc.option.get(i + 2).param).length() - 2, String.valueOf(itemThaoNgoc.option.get(i + 2).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i + 2).param).substring(0, String.valueOf(itemThaoNgoc.option.get(i + 2).param).length() - 2))));
                                        }
                                    }
                                }
                                if (temp[j] == 108 && temp[j] == loai) {
                                    for (int i = 0; i < itemThaoNgoc.option.size(); i++) {
                                        if (itemThaoNgoc.option.get(i).id == 111) {
                                            itemNgoc.option.add(new Option(loai, 0));
                                            itemNgoc.option.add(new Option(itemThaoNgoc.option.get(i + 1).id, itemThaoNgoc.option.get(i + 1).param));
                                            itemNgoc.option.add(new Option(itemThaoNgoc.option.get(i + 2).id, itemThaoNgoc.option.get(i + 2).param));
                                        }
                                    }
                                } else if (temp[j] == 108) {
                                    for (int i = 0; i < itemThaoNgoc.option.size(); i++) {
                                        if (itemThaoNgoc.option.get(i).id == indextemp - 7) {
                                            itemNgoc.option.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i).param).substring(String.valueOf(itemThaoNgoc.option.get(i).param).length() - 3, String.valueOf(itemThaoNgoc.option.get(i).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i).param).substring(0, String.valueOf(itemThaoNgoc.option.get(i).param).length() - 3))));
                                            itemNgoc.option.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i + 1).param).substring(String.valueOf(itemThaoNgoc.option.get(i + 1).param).length() - 3, String.valueOf(itemThaoNgoc.option.get(i + 1).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i + 1).param).substring(0, String.valueOf(itemThaoNgoc.option.get(i + 1).param).length() - 3))));
                                            itemNgoc.option.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i + 2).param).substring(String.valueOf(itemThaoNgoc.option.get(i + 2).param).length() - 3, String.valueOf(itemThaoNgoc.option.get(i + 2).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i + 2).param).substring(0, String.valueOf(itemThaoNgoc.option.get(i + 2).param).length() - 3))));
                                        }
                                    }
                                }
                            }
                            for (int i = 0; i < itemThaoNgoc.option.size(); i++) {
                                if (itemThaoNgoc.option.get(i).id == indextemp - 10) {
                                    itemNgoc.option.add(new Option(104, itemThaoNgoc.option.get(i).param));
                                } else if (itemThaoNgoc.option.get(i).id == indextemp - 11) {
                                    itemNgoc.option.add(new Option(123, itemThaoNgoc.option.get(i).param));
                                } else if (itemThaoNgoc.option.get(i).id == indextemp - 12) {
                                    itemNgoc.upgrade = (byte) itemThaoNgoc.option.get(i).param;
                                } else if (itemThaoNgoc.option.get(i).id == indextemp - 13) {
                                    itemNgoc.buyCoinLock = itemThaoNgoc.option.get(i).param;
                                }
                            }
                            itemNgoc.id = 654;
                            p.nj.addItemBag(false, itemNgoc);
                            break;
                        }
                        case 110: {
                            indextemp = -30;
                            for (int j = 0; j < temp.length; j++) {
                                if (temp[j] == 106 && temp[j] == loai) {
                                    for (int i = 0; i < itemThaoNgoc.option.size(); i++) {
                                        if (itemThaoNgoc.option.get(i).id == 110) {
                                            itemNgoc.option.add(new Option(loai, 0));
                                            itemNgoc.option.add(new Option(itemThaoNgoc.option.get(i + 1).id, itemThaoNgoc.option.get(i + 1).param));
                                            itemNgoc.option.add(new Option(itemThaoNgoc.option.get(i + 2).id, itemThaoNgoc.option.get(i + 2).param));
                                        }
                                    }
                                } else if (temp[j] == 106) {
                                    for (int i = 0; i < itemThaoNgoc.option.size(); i++) {
                                        if (itemThaoNgoc.option.get(i).id == indextemp - 1) {
                                            itemNgoc.option.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i).param).substring(String.valueOf(itemThaoNgoc.option.get(i).param).length() - 3, String.valueOf(itemThaoNgoc.option.get(i).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i).param).substring(0, String.valueOf(itemThaoNgoc.option.get(i).param).length() - 3))));
                                            itemNgoc.option.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i + 1).param).substring(String.valueOf(itemThaoNgoc.option.get(i + 1).param).length() - 2, String.valueOf(itemThaoNgoc.option.get(i + 1).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i + 1).param).substring(0, String.valueOf(itemThaoNgoc.option.get(i + 1).param).length() - 2))));
                                            itemNgoc.option.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i + 2).param).substring(String.valueOf(itemThaoNgoc.option.get(i + 2).param).length() - 3, String.valueOf(itemThaoNgoc.option.get(i + 2).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i + 2).param).substring(0, String.valueOf(itemThaoNgoc.option.get(i + 2).param).length() - 3))));
                                        }
                                    }
                                }
                                if (temp[j] == 107 && temp[j] == loai) {
                                    for (int i = 0; i < itemThaoNgoc.option.size(); i++) {
                                        if (itemThaoNgoc.option.get(i).id == 110) {
                                            itemNgoc.option.add(new Option(loai, 0));
                                            itemNgoc.option.add(new Option(itemThaoNgoc.option.get(i + 1).id, itemThaoNgoc.option.get(i + 1).param));
                                            itemNgoc.option.add(new Option(itemThaoNgoc.option.get(i + 2).id, itemThaoNgoc.option.get(i + 2).param));
                                        }
                                    }
                                } else if (temp[j] == 107) {
                                    for (int i = 0; i < itemThaoNgoc.option.size(); i++) {
                                        if (itemThaoNgoc.option.get(i).id == indextemp - 4) {
                                            itemNgoc.option.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i).param).substring(String.valueOf(itemThaoNgoc.option.get(i).param).length() - 3, String.valueOf(itemThaoNgoc.option.get(i).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i).param).substring(0, String.valueOf(itemThaoNgoc.option.get(i).param).length() - 3))));
                                            itemNgoc.option.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i + 1).param).substring(String.valueOf(itemThaoNgoc.option.get(i + 1).param).length() - 3, String.valueOf(itemThaoNgoc.option.get(i + 1).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i + 1).param).substring(0, String.valueOf(itemThaoNgoc.option.get(i + 1).param).length() - 3))));
                                            itemNgoc.option.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i + 2).param).substring(String.valueOf(itemThaoNgoc.option.get(i + 2).param).length() - 2, String.valueOf(itemThaoNgoc.option.get(i + 2).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i + 2).param).substring(0, String.valueOf(itemThaoNgoc.option.get(i + 2).param).length() - 2))));
                                        }
                                    }
                                }
                                if (temp[j] == 108 && temp[j] == loai) {
                                    for (int i = 0; i < itemThaoNgoc.option.size(); i++) {
                                        if (itemThaoNgoc.option.get(i).id == 110) {
                                            itemNgoc.option.add(new Option(loai, 0));
                                            itemNgoc.option.add(new Option(itemThaoNgoc.option.get(i + 1).id, itemThaoNgoc.option.get(i + 1).param));
                                            itemNgoc.option.add(new Option(itemThaoNgoc.option.get(i + 2).id, itemThaoNgoc.option.get(i + 2).param));
                                        }
                                    }
                                } else if (temp[j] == 108) {
                                    for (int i = 0; i < itemThaoNgoc.option.size(); i++) {
                                        if (itemThaoNgoc.option.get(i).id == indextemp - 7) {
                                            itemNgoc.option.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i).param).substring(String.valueOf(itemThaoNgoc.option.get(i).param).length() - 3, String.valueOf(itemThaoNgoc.option.get(i).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i).param).substring(0, String.valueOf(itemThaoNgoc.option.get(i).param).length() - 3))));
                                            itemNgoc.option.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i + 1).param).substring(String.valueOf(itemThaoNgoc.option.get(i + 1).param).length() - 2, String.valueOf(itemThaoNgoc.option.get(i + 1).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i + 1).param).substring(0, String.valueOf(itemThaoNgoc.option.get(i + 1).param).length() - 2))));
                                            itemNgoc.option.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i + 2).param).substring(String.valueOf(itemThaoNgoc.option.get(i + 2).param).length() - 3, String.valueOf(itemThaoNgoc.option.get(i + 2).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i + 2).param).substring(0, String.valueOf(itemThaoNgoc.option.get(i + 2).param).length() - 3))));
                                        }
                                    }
                                }
                            }
                            for (int i = 0; i < itemThaoNgoc.option.size(); i++) {
                                if (itemThaoNgoc.option.get(i).id == indextemp - 10) {
                                    itemNgoc.option.add(new Option(104, itemThaoNgoc.option.get(i).param));
                                } else if (itemThaoNgoc.option.get(i).id == indextemp - 11) {
                                    itemNgoc.option.add(new Option(123, itemThaoNgoc.option.get(i).param));
                                } else if (itemThaoNgoc.option.get(i).id == indextemp - 12) {
                                    itemNgoc.upgrade = (byte) itemThaoNgoc.option.get(i).param;
                                } else if (itemThaoNgoc.option.get(i).id == indextemp - 13) {
                                    itemNgoc.buyCoinLock = itemThaoNgoc.option.get(i).param;
                                }
                            }
                            itemNgoc.id = 653;
                            p.nj.addItemBag(false, itemNgoc);
                            break;
                        }
                        case 109: {
                            indextemp = -45;
                            for (int j = 0; j < temp.length; j++) {
                                if (temp[j] == 106 && temp[j] == loai) {
                                    for (int i = 0; i < itemThaoNgoc.option.size(); i++) {
                                        if (itemThaoNgoc.option.get(i).id == 109) {
                                            itemNgoc.option.add(new Option(loai, 0));
                                            itemNgoc.option.add(new Option(itemThaoNgoc.option.get(i + 1).id, itemThaoNgoc.option.get(i + 1).param));
                                            itemNgoc.option.add(new Option(itemThaoNgoc.option.get(i + 2).id, itemThaoNgoc.option.get(i + 2).param));
                                        }
                                    }
                                } else if (temp[j] == 106) {
                                    for (int i = 0; i < itemThaoNgoc.option.size(); i++) {
                                        if (itemThaoNgoc.option.get(i).id == indextemp - 1) {
                                            itemNgoc.option.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i).param).substring(String.valueOf(itemThaoNgoc.option.get(i).param).length() - 3, String.valueOf(itemThaoNgoc.option.get(i).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i).param).substring(0, String.valueOf(itemThaoNgoc.option.get(i).param).length() - 3))));
                                            itemNgoc.option.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i + 1).param).substring(String.valueOf(itemThaoNgoc.option.get(i + 1).param).length() - 3, String.valueOf(itemThaoNgoc.option.get(i + 1).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i + 1).param).substring(0, String.valueOf(itemThaoNgoc.option.get(i + 1).param).length() - 3))));
                                            itemNgoc.option.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i + 2).param).substring(String.valueOf(itemThaoNgoc.option.get(i + 2).param).length() - 3, String.valueOf(itemThaoNgoc.option.get(i + 2).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i + 2).param).substring(0, String.valueOf(itemThaoNgoc.option.get(i + 2).param).length() - 3))));
                                        }
                                    }
                                }
                                if (temp[j] == 107 && temp[j] == loai) {
                                    for (int i = 0; i < itemThaoNgoc.option.size(); i++) {
                                        if (itemThaoNgoc.option.get(i).id == 109) {
                                            itemNgoc.option.add(new Option(loai, 0));
                                            itemNgoc.option.add(new Option(itemThaoNgoc.option.get(i + 1).id, itemThaoNgoc.option.get(i + 1).param));
                                            itemNgoc.option.add(new Option(itemThaoNgoc.option.get(i + 2).id, itemThaoNgoc.option.get(i + 2).param));
                                        }
                                    }
                                } else if (temp[j] == 107) {
                                    for (int i = 0; i < itemThaoNgoc.option.size(); i++) {
                                        if (itemThaoNgoc.option.get(i).id == indextemp - 4) {
                                            itemNgoc.option.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i).param).substring(String.valueOf(itemThaoNgoc.option.get(i).param).length() - 3, String.valueOf(itemThaoNgoc.option.get(i).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i).param).substring(0, String.valueOf(itemThaoNgoc.option.get(i).param).length() - 3))));
                                            itemNgoc.option.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i + 1).param).substring(String.valueOf(itemThaoNgoc.option.get(i + 1).param).length() - 3, String.valueOf(itemThaoNgoc.option.get(i + 1).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i + 1).param).substring(0, String.valueOf(itemThaoNgoc.option.get(i + 1).param).length() - 3))));
                                            itemNgoc.option.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i + 2).param).substring(String.valueOf(itemThaoNgoc.option.get(i + 2).param).length() - 3, String.valueOf(itemThaoNgoc.option.get(i + 2).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i + 2).param).substring(0, String.valueOf(itemThaoNgoc.option.get(i + 2).param).length() - 3))));
                                        }
                                    }
                                }
                                if (temp[j] == 108 && temp[j] == loai) {
                                    for (int i = 0; i < itemThaoNgoc.option.size(); i++) {
                                        if (itemThaoNgoc.option.get(i).id == 109) {
                                            itemNgoc.option.add(new Option(loai, 0));
                                            itemNgoc.option.add(new Option(itemThaoNgoc.option.get(i + 1).id, itemThaoNgoc.option.get(i + 1).param));
                                            itemNgoc.option.add(new Option(itemThaoNgoc.option.get(i + 2).id, itemThaoNgoc.option.get(i + 2).param));
                                        }
                                    }
                                } else if (temp[j] == 108) {
                                    for (int i = 0; i < itemThaoNgoc.option.size(); i++) {
                                        if (itemThaoNgoc.option.get(i).id == indextemp - 7) {
                                            itemNgoc.option.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i).param).substring(String.valueOf(itemThaoNgoc.option.get(i).param).length() - 3, String.valueOf(itemThaoNgoc.option.get(i).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i).param).substring(0, String.valueOf(itemThaoNgoc.option.get(i).param).length() - 3))));
                                            itemNgoc.option.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i + 1).param).substring(String.valueOf(itemThaoNgoc.option.get(i + 1).param).length() - 3, String.valueOf(itemThaoNgoc.option.get(i + 1).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i + 1).param).substring(0, String.valueOf(itemThaoNgoc.option.get(i + 1).param).length() - 3))));
                                            itemNgoc.option.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i + 2).param).substring(String.valueOf(itemThaoNgoc.option.get(i + 2).param).length() - 3, String.valueOf(itemThaoNgoc.option.get(i + 2).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.option.get(i + 2).param).substring(0, String.valueOf(itemThaoNgoc.option.get(i + 2).param).length() - 3))));
                                        }
                                    }
                                }
                            }
                            for (int i = 0; i < itemThaoNgoc.option.size(); i++) {
                                if (itemThaoNgoc.option.get(i).id == indextemp - 10) {
                                    itemNgoc.option.add(new Option(104, itemThaoNgoc.option.get(i).param));
                                } else if (itemThaoNgoc.option.get(i).id == indextemp - 11) {
                                    itemNgoc.option.add(new Option(123, itemThaoNgoc.option.get(i).param));
                                } else if (itemThaoNgoc.option.get(i).id == indextemp - 12) {
                                    itemNgoc.upgrade = (byte) itemThaoNgoc.option.get(i).param;
                                } else if (itemThaoNgoc.option.get(i).id == indextemp - 13) {
                                    itemNgoc.buyCoinLock = itemThaoNgoc.option.get(i).param;
                                }
                            }
                            itemNgoc.id = 652;
                            p.nj.addItemBag(false, itemNgoc);
                            break;
                        }
                    }
                }
                for (int i = 0; i < itemThaoNgoc.option.size(); i++) {
                    if (itemThaoNgoc.option.get(i).id == 122) {
//                        yenThaoNgoc = itemThaoNgoc.option.get(i).param;
                        itemThaoNgoc.option.remove(i);
                    }
                }
                for (int i = tempIndex.length - 1; i >= 0; i--) {
                    if (tempIndexNagative[i] == 109 || tempIndexNagative[i] == 110 || tempIndexNagative[i] == 111 || tempIndexNagative[i] == 112) {
                        itemThaoNgoc.option.remove(tempIndex[i] + 2);
                        itemThaoNgoc.option.remove(tempIndex[i] + 1);
                        itemThaoNgoc.option.remove(tempIndex[i]);
                    } else {
                        itemThaoNgoc.option.remove(tempIndex[i]);
                    }
                }

                int typeThaoNgoc = 3;
                m.cleanup();
                m = new Message(124);
                m.writer().writeByte(3);
                m.writer().writeInt(p.luong);
                m.writer().writeInt(p.nj.xu);
                m.writer().writeInt(p.nj.yen);
                m.writer().writeByte(itemThaoNgoc.upgrade);
                m.writer().flush();
                p.sendMessage(m);
                m.cleanup();
                m = new Message(42);
                m.writer().writeByte(3);
                m.writer().writeByte(indexItemThaoNgoc);
                m.writer().writeLong(itemThaoNgoc.expires);
                if (ItemData.isTypeUIME(typeThaoNgoc)) {
                    m.writer().writeInt(itemThaoNgoc.buyCoinLock);
                }
                if (ItemData.isTypeUIShop(typeThaoNgoc) || ItemData.isTypeUIShopLock(typeThaoNgoc) || ItemData.isTypeMounts(typeThaoNgoc) || ItemData.isTypeUIStore(typeThaoNgoc) || ItemData.isTypeUIBook(typeThaoNgoc) || ItemData.isTypeUIFashion(typeThaoNgoc) || ItemData.isTypeUIClanShop(typeThaoNgoc)) {
                    m.writer().writeInt(itemThaoNgoc.buyCoin);
                    m.writer().writeInt(itemThaoNgoc.buyCoinLock);
                    m.writer().writeInt(itemThaoNgoc.buyGold);
                }
                if (ItemData.isTypeBody(itemThaoNgoc.id) || ItemData.isTypeMounts(itemThaoNgoc.id) || ItemData.isTypeNgocKham(itemThaoNgoc.id)) {
                    m.writer().writeByte(itemThaoNgoc.sys);//thuoc tinh
                    int i = 0;
                    if (itemThaoNgoc.option != null) {
                        for (Option Option : itemThaoNgoc.option) {
                            m.writer().writeByte(Option.id);
                            m.writer().writeInt(Option.param);
                            i++;

                        }
                    }
                }
                m.writer().flush();
                p.sendMessage(m);
                m.cleanup();
                p.session.sendMessageLog("Tháo ngọc thành công");
                break;
            }
            default: {
                break;
            }

        }

    }

    public static void upgradeNgoc(Item mainItem, int oldUpGrad, int nextUpgrade) {
        for (int j = oldUpGrad; j < nextUpgrade; j++) {

            for (Option o : mainItem.option) {
                if (o.id != 106 || o.id != 107 || o.id != 108 || o.id != 104 || o.id != 123) {
                    int value = o.param;
                    if (value > 0 && value < 50) {
                        value += util.nextInt(value / 4, value / 3);
                    } else if (value >= 50 && value < 300) {
                        value += util.nextInt(value / 3, value / 2);
                    } else if (value >= 300) {
                        value += util.nextInt(value / 2, value);
                    } else if (value >= -50 && value < 0) {
                        value -= util.nextInt(20);
                    } else if (value < -50) {
                        value -= util.nextInt(50, 100);
                    }
                    o.param = value;
                }
            }
            mainItem.option.stream().filter(o -> o.id == ItemData.GIA_KHAM_OPTION_ID)
                    .forEach(o -> o.param += 400000);
        }
    }

    private static final HashMap<Integer, Integer> xuGotNgoc;
    public static final HashMap<Integer, Integer> exps;

    static {
        xuGotNgoc = new HashMap<>();
        exps = new HashMap<>();
        xuGotNgoc.put(1, 5_000);
        xuGotNgoc.put(2, 40_000);
        xuGotNgoc.put(3, 135_000);
        xuGotNgoc.put(4, 320_000);
        xuGotNgoc.put(5, 625_000);
        xuGotNgoc.put(6, 1_080_000);
        xuGotNgoc.put(7, 1_715_000);
        xuGotNgoc.put(8, 2_560_000);
        xuGotNgoc.put(9, 3_645_000);
        xuGotNgoc.put(10, 5_000_000);

        exps.put(1, 0);
        exps.put(2, 210);
        exps.put(3, 510);
        exps.put(4, 1010);
        exps.put(5, 2010);
        exps.put(6, 5010);
        exps.put(7, 10_010);
        exps.put(8, 20_010);
        exps.put(9, 50_010);
        exps.put(10, 100_010);
    }

    private static int getNextUpgrade(int xExp) {

        if (xExp > 200 && xExp <= 500) {
            return 2;
        } else if (xExp > 500 && xExp <= 1_000) {
            return 3;
        } else if (xExp > 1_000 && xExp <= 2_000) {
            return 4;
        } else if (xExp > 2_000 && xExp <= 5_000) {
            return 5;
        } else if (xExp > 5000 && xExp <= 10_000) {
            return 6;
        } else if (xExp > 10_000 && xExp <= 20_000) {
            return 7;
        } else if (xExp > 20_000 && xExp <= 50_000) {
            return 8;
        } else if (xExp > 50_000 && xExp <= 100_000) {
            return 9;
        } else if (xExp > 100_000) {
            return 10;
        }

        return 1;
    }

    public static void requestRankedInfo(User p, String ninjaName) {
        try {
            final User user = KageTournament.gi().getUserByNinjaName(ninjaName);
            p.viewInfoPlayers(user);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    static {
        GameScr.server = Server.getInstance();
        upClothe = new int[]{4, 9, 33, 132, 177, 256, 656, 2880, 3968, 6016, 13440, 54144, 71680, 108544, 225280, 1032192};
        upAdorn = new int[]{6, 14, 50, 256, 320, 512, 1024, 5120, 6016, 9088, 19904, 86016, 108544, 166912, 360448, 1589248};
        upWeapon = new int[]{18, 42, 132, 627, 864, 1360, 2816, 13824, 17792, 26880, 54016, 267264, 315392, 489472, 1032192, 4587520};
        coinUpCrystals = new int[]{10, 40, 160, 640, 2560, 10240, 40960, 163840, 655360, 1310720, 3932160, 11796480};
        crystals = new int[]{1, 4, 16, 64, 256, 1024, 4096, 16384, 65536, 262144, 1048576, 3096576};
        coinUpClothes = new int[]{120, 270, 990, 3960, 5310, 7680, 19680, 86400, 119040, 180480, 403200, 1624320, 2150400, 3256320, 6758400, 10137600};
        coinUpAdorns = new int[]{180, 420, 1500, 7680, 9600, 15360, 30720, 153600, 180480, 272640, 597120, 2580480, 3256320, 5007360, 10813440, 16220160};
        coinUpWeapons = new int[]{540, 1260, 3960, 18810, 25920, 40800, 84480, 414720, 533760, 806400, 1620480, 8017920, 9461760, 14684160, 22026240, 33039360};
        goldUps = new int[]{1, 2, 3, 4, 5, 10, 15, 20, 50, 100, 150, 200, 300, 400, 500, 600};
        maxPercents = new int[]{80, 75, 70, 65, 60, 55, 50, 45, 40, 35, 30, 25, 20, 15, 10, 5};
        ArryenLuck = new int[]{1_000_000, 2_000_000};
        ArrdayLuck = new byte[]{3, 7, 15, 30};
        optionBikiep = new int[]{86, 87, 88, 89, 90, 91, 92, 94, 95, 96, 97, 98, 99};
        paramBikiep = new int[]{50, 1000, 500, 500, 500, 50, 10, 20, 100, 100, 100, 10, 500};
        percentBikiep = new int[]{80, 75, 70, 65, 60, 55, 50, 45, 30, 25, 20, 15, 10, 7, 5, 1};
        optionPet = new int[]{87, 92, 94, 82, 94, 88, 89, 90, 86};
        paramPet = new int[]{2500, 20, 30, 2500, 20, 500, 500, 500, 50};
        percentPet = new int[]{100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100};

        // TODO
        // Task ID,
    }

    public static void NangMat(User p, Item item, int type) throws IOException {
        if (item.upgrade >= 10) {
            p.session.sendMessageLog("Mắt đã nâng cấp tối đa");
            return;
        }
        if (p.nj.quantityItemyTotal(694 + item.upgrade) < 10) {
            ItemData data = ItemData.ItemDataId(694 + item.upgrade);
            p.session.sendMessageLog("Bạn không đủ 10 viên " + data.name + " để nâng cấp");
            return;
        }
        if ((p.nj.yen + p.nj.xu) < GameScr.coinUpMat[item.upgrade]) {
            p.session.sendMessageLog("Bạn không đủ yên và xu để nâng cấp mắt");
            return;
        }
        if (type == 1 && p.luong < GameScr.goldUpMat[item.upgrade]) {
            p.session.sendMessageLog("Bạn không đủ lượng để nâng cấp mắt");
            return;
        }
        GameScr.handleUpgradeMat(p, item, type);
        Message m = new Message(13);
        m.writer().writeInt(p.nj.xu);//xu
        m.writer().writeInt(p.nj.yen);//yen
        m.writer().writeInt(p.luong);//luong
        m.writer().flush();
        p.session.sendMessage(m);
        m.cleanup();
    }

    private static void handleUpgradeMat(User p, Item item, int type) {
        try {
            int upPer = GameScr.percentUpMat[item.upgrade];
            if (type == 1) {
                upPer *= 2;
            }
            if (util.nextInt(110) < upPer) {
                p.nj.removeItemBody((byte) 14);
                Item itemup = ItemData.itemDefault(685 + item.upgrade, true);
                itemup.quantity = 1;
                itemup.upgrade = (byte) (item.upgrade + 1);
                itemup.isLock = true;

                Option op = new Option(6, 1000 * itemup.upgrade);
                itemup.option.add(op);
                op = new Option(87, 500 + (250 * item.upgrade));
                itemup.option.add(op);

                if (itemup.upgrade >= 3) {
                    op = new Option(79, 25);
                    itemup.option.add(op);
                }
                if (itemup.upgrade >= 6) {
                    op = new Option(64, 0);
                    itemup.option.add(op);
                }
                if (itemup.upgrade == 10) {
                    op = new Option(113, 5000);
                    itemup.option.add(op);
                }
                p.nj.addItemBag(false, itemup);
            } else {
                p.sendYellowMessage("Nâng cấp mắt thất bại!");
            }

            if (p.nj.yen < GameScr.coinUpMat[item.upgrade]) {
                p.nj.xu -= (GameScr.coinUpMat[item.upgrade] - p.nj.yen);
                p.nj.yen = 0;
            } else {
                p.nj.yen -= GameScr.coinUpMat[item.upgrade];
            }
            if (type == 1) {
                p.luong -= GameScr.goldUpMat[item.upgrade];
            }
            p.nj.removeItemBags(694 + item.upgrade, 10);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void HuyNhiemVuDanhVong(User p) throws IOException {
        server.manager.sendTB(p, "Thông báo", "Đã hủy nhiệm vụ " + DanhVongData.nameNV1[p.nj.taskDanhVong[0]]);
        p.nj.isTaskDanhVong = 0;
        p.nj.taskDanhVong = new int[]{-1, -1, -1, 0, p.nj.countTaskDanhVong};
    }

    public static void TinhLuyenBK(User p, Item item, int type) throws IOException {
        int ys = item.option.get(0).param;
        if (ys >= 9) {
            p.session.sendMessageLog("Bí kíp đã được tinh luyện đến mức tối đa");
            return;
        }
        if (p.nj.xu < GameScr.XuUpBK[ys]) {
            p.session.sendMessageLog("Bạn không đủ xu để nâng cấp Bí kíp");
            return;
        }
        if (p.luong < GameScr.goldUpBK[ys]) {
            p.session.sendMessageLog("Bạn không đủ lượng để nâng cấp Bí kíp");
            return;

        }
        GameScr.HandleTinhLuyenBK(p, item, type);
        Message m = new Message(13);
        m.writer().writeInt(p.nj.xu);
        m.writer().writeInt(p.nj.yen);
        m.writer().writeInt(p.luong);
        m.writer().flush();
        p.session.sendMessage(m);
        m.cleanup();
    }

    private static void HandleTinhLuyenBK(User p, Item item, int type) {
        try {
            int ys = p.nj.ItemBody[15].option.get(0).param;
            int upPer = GameScr.percentUpBK[ys];
            p.nj.xu -= GameScr.XuUpBK[ys];
            p.luong -= GameScr.goldUpBK[ys];
            if (util.nextInt(120) < upPer) {
                p.nj.ItemBody[15].UpBKnext((byte) 1);
                p.nj.ItemBody[15].quantity = 1;
                p.nj.ItemBody[15].isLock = true;
                p.nj.ItemBody[15].upgrade = 0;
                p.nj.addItemBag(false, p.nj.ItemBody[15]);
                p.nj.removeItemBody((byte) 15);
                String chat = null;
                switch (util.nextInt(1, 3)) {
                    case 1:
                        chat = "May quá, ta mém làm mất nó cho cá độ rồi ^^";
                        break;
                    case 2:
                        chat = "Ngươi may mắn đấy, ta vừa ngủ quên trong lúc luyện nó";
                        break;
                    case 3:
                        chat = "Haizz, cấp độ này dễ quá ta cần cấp độ cao hơn.";
                        break;
                }
                p.nj.getPlace().chatNPC(p, 40, chat);
            } else {
                p.sendYellowMessage("Nâng cấp Bí kíp thất bại!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
