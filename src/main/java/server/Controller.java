package server;

import boardGame.Place;
import lombok.SneakyThrows;
import lombok.val;
import lombok.var;
import patch.Constants;
import patch.ItemShinwaManager;
import patch.ItemShinwaManager.ItemShinwa;
import patch.MessageSubCommand;
import patch.interfaces.ISoloer;
import patch.tournament.Tournament;
import real.*;
import threading.Map;
import threading.Message;
import io.Session;
import io.ISessionHandler;
import threading.Server;

import java.io.IOException;
import java.sql.SQLException;

import static patch.ItemShinwaManager.returnXuToSeller;
import threading.Manager;

@SuppressWarnings("ALL")
public class Controller implements ISessionHandler {

    Server server;
    ItemShinwaManager itemShinwaManager;
    LogHistory LogHistory = new LogHistory(this.getClass());

    public Controller() {
        this.server = Server.getInstance();
    }

    @Override
    public void onConnectOK(final Session conn) {
    }

    @Override
    public void onConnectionFail(final Session conn) {
    }

    @Override
    public void onDisconnected(final Session conn) {
        PlayerManager.getInstance().kickSession(conn);
    }

    @SneakyThrows
    @Override
    public void onMessage(final Session conn, final Message message) {
        final ServerController ctl = this.server.controllerManager;
        try {
            final User p = conn.user;

            switch (message.getCommand()) {
                case -30: {
                    if (p != null) {
                        p.messageSubCommand(message);
                        break;
                    }
                    break;
                }
                case -29: {
                    ctl.processGameMessage(conn, message);
                    break;
                }
                case -28: {
                    if (p != null) {
                        p.messageNotMap(message);
                        break;
                    }
                    break;
                }
                case -27: {
                    conn.hansakeMessage();
                    break;
                }
                case -23: {
                    if (p != null) {
                        p.nj.getPlace().Chat(p, message.reader().readUTF());
                        message.cleanup();
                        break;
                    }
                    break;
                }
                case -22: {
                    if (p != null) {
                        p.privateChat(message);
                        break;
                    }
                    break;
                }
                case -21: {
                    if (p != null) {
                        this.server.manager.chatKTG(p, message);
                        break;
                    }
                    break;
                }
                case -20: {
                    if (p != null && p.nj != null) {
                        p.chatParty(message);
                        break;
                    }
                    break;
                }
                case -19: {
                    if (p != null && !p.nj.isDie) {
                        final ClanManager clan = ClanManager.getClanByName(p.nj.clan.clanName);
                        if (clan != null) {
                            clan.chat(p, message);
                        }
                        break;
                    }
                    break;
                }
                case -17: {
                    if (p != null && !p.nj.isDie) {
                        p.nj.getPlace().changeMap(p);
                        message.cleanup();
                        break;
                    }
                    break;
                }
                case -14: {
                    if (p != null && p.nj != null && !p.nj.isDie) {
                        p.nj.getPlace().pickItem(p, message);
                        break;
                    }
                    break;
                }
                case -12: {
                    if (p != null && !p.nj.isDie) {
                        final byte index = message.reader().readByte();
                        p.nj.getPlace().leaveItemBackground(p, index);
                        break;
                    }
                    break;
                }
                case -10: {
                    if (p != null && p.nj.isDie && !p.nj.isNhanban) {
                        p.nj.getPlace().wakeUpDieReturn(p);
                        break;
                    }
                    break;
                }
                case -9: {
                    if (p != null && p.nj.isDie && !p.nj.isNhanban) {
                        p.nj.getPlace().DieReturn(p);
                        break;
                    }
                    break;
                }
                case 1: {
                    if (p != null && p.nj != null && !p.nj.isDie) {
                        p.nj.getPlace().moveMessage(p.nj, message.reader().readShort(), message.reader().readShort());
                        break;
                    }
                    break;
                }
                case 4:
                case 73: {
                    attackPlayerVsMob(message, p);
                    break;
                }
                case 11: {
                    if (p != null && !p.nj.isDie) {
                        p.useItem(message);
                        break;
                    }
                    break;
                }
                case 12: {
                    if (p != null && p.nj != null && !p.nj.isDie) {
                        useItem.useItemChangeMap(p, message);
                        break;
                    }
                    break;
                }
                case 13: {
                    if (p != null && !p.nj.isDie) {
                        GameScr.buyItemStore(p, message);
                        break;
                    }
                    break;
                }
                case 14: {
                    if (p != null) {
                        p.SellItemBag(message);
                        break;
                    }
                    break;
                }
                case 15: {
                    if (p != null) {
                        p.itemBodyToBag(message);
                        break;
                    }
                    break;
                }
                case 16: {
                    if (p != null) {
                        p.itemBoxToBag(p, message);
                        break;
                    }
                    break;
                }
                case 17: {
                    if (p != null) {
                        p.itemBagToBox(message);
                        break;
                    }
                    break;
                }
                case 19: {
                    if (p != null) {
                        GameScr.crystalCollect(p, message, true);
                        break;
                    }
                    break;
                }
                case 20: {
                    if (p != null) {
                        GameScr.crystalCollect(p, message, false);
                        break;
                    }
                    break;
                }
                case 21: {
                    if (p != null) {
                        GameScr.UpGrade(p, message);
                        break;
                    }
                    break;
                }
                case 22: {
                    if (p != null) {
                        GameScr.Split(p, message);
                        break;
                    }
                    break;
                }
                case 23: {
                    if (p != null) {
                        p.pleaseInputParty(message);
                        break;
                    }
                    break;
                }
                case 24: {
                    if (p != null) {
                        p.acceptPleaseParty(message);
                        break;
                    }
                    break;
                }
                case 28: {
                    if (p != null && !p.nj.isDie) {
                        p.nj.getPlace().selectUIZone(p, message);
                        break;
                    }
                    break;
                }
                case 29: {
                    if (p != null) {
                        this.server.menu.sendMenu(p, message);
                        break;
                    }
                    break;
                }
                case 36: {
                    if (p != null && !p.nj.isDie) {
                        p.nj.getPlace().openUIZone(p);
                        break;
                    }
                    break;
                }
                case 40: {
                    if (p != null && !p.nj.isDie) {
                        this.server.menu.openUINpc(p, message);
                        break;
                    }
                    break;
                }
                case 41: {
                    if (p != null && p.nj != null && !p.nj.isDie) {

                        useSkill.useSkill(p.nj.get(), message.reader().readShort());
                        break;
                    }
                    break;
                }
                case 42: {
                    if (p != null) {
                        p.requestItemInfo(message);
                        break;
                    }
                    break;
                }
                case 43: {
                    if (p != null && !p.nj.isDie) {
                        p.requestTrade(message);
                        break;
                    }
                    break;
                }
                case 44: {
                    if (p != null && !p.nj.isDie) {
                        p.startTrade(message);
                        break;
                    }
                    break;
                }
                case 45: {
                    if (p != null) {
                        p.lockTrade(message);
                        break;
                    }
                    break;
                }
                case 46: {
                    if (p != null) {
                        p.agreeTrade();
                        break;
                    }
                    break;
                }
                case 47: {
                    if (p != null) {
                        server.menu.selectMenuNpc(p, message);
                        break;
                    }
                    break;
                }
                case 56: {
                    if (p != null) {
                        p.closeTrade();
                        break;
                    }
                    break;
                }
                case 57: {
                    if (p != null) {
                        p.closeLoad();
                        break;
                    }
                    break;
                }
                case 59: {
                    if (p != null) {
                        p.addFriend(message);
                        break;
                    }
                    break;
                }
                case 60: {
                    if (p != null && !p.nj.isDie) {
                        val cloneMessage = message.cloneMessage();
                        p.nj.getPlace().FightMob(p.nj.get(), message);
                        if (p.nj.get().isHuman && p.nj.clone != null && p.nj.clone.isIslive()) {
                            p.nj.getPlace().FightMob(p.nj.clone, (Message) cloneMessage);
                        }
                        break;
                    }
                    break;
                }

                case 61: {
                    if (p != null && !p.nj.isDie) {
                        val cloneMessage = message.cloneMessage();
                        p.nj.getPlace().attackNinja(p.nj.get(), message);
                        if (p.nj.get().isHuman && p.nj.clone != null && p.nj.clone.isIslive()) {
                            p.nj.getPlace().attackNinja(p.nj.clone, (Message) cloneMessage);
                        }
                        break;
                    }
                    break;
                }
                case 65: {
                    if (p != null && !p.nj.isDie) {
                        ISoloer soloer = p.nj.getPlace().getNinja(message.reader().readInt());
                        p.nj.requestSolo(soloer);
                        message.cleanup();
                    }
                    break;
                }
                case 66: {
                    if (p != null && !p.nj.isDie) {
                        p.nj.acceptSolo();
                    }
                    break;
                }
                case 67: {
                    if (p != null && !p.nj.isDie) {
                        p.nj.endSolo();
                    }
                    break;
                }
                case 68: { //cừu sát
                    if (p != null && !p.nj.isDie) {
                        p.addCuuSat(message);
                    }
                    break;
                }

                case 79: {
                    if (p != null) {
                        p.addParty(message);
                        break;
                    }
                    break;
                }
                case 80: {
                    if (p != null) {
                        p.addPartyAccept(message);
                        break;
                    }
                    break;
                }
                case 83: {
                    if (p != null && p.nj != null && p.nj.get().party != null) {
                        p.nj.get().party.exitParty(p.nj);
                        break;
                    }
                    break;
                }
                case 92:
                    handleInputMessage(message, p);
                    break;
                case 93: {
                    if (p != null) {
                        final String playername = message.reader().readUTF();
                        p.viewPlayerMessage(playername);
                        break;
                    }
                    break;
                }
                case 94: {
                    if (p != null) {
                        p.viewOptionPlayers(message);
                        break;
                    }
                    break;
                }
                case 99: {
                    Controller.accpetDun(p, message);
                    break;
                }
                case 100: {
                    // Xem thi đấu lôi đài
                    Controller.viewDun(p, message);
                    break;
                }
                case 104: {
                    p.requestItemShinwaInfo(message);
                    break;
                }
                case 108: {
                    if (p != null) {
                        p.itemMonToBag(message);
                        break;
                    }
                    break;
                }
                case 110: {
                    GameScr.LuyenThach(p, message);
                    break;
                }
                case 111: {
                    if (p != null) {
                        GameScr.TinhLuyen(p, message);
                        break;
                    }
                    break;
                }
                case 112: {
                    if (p != null) {
                        GameScr.DichChuyen(p, message);
                        break;
                    }
                    break;
                }
                case 125: {
                    if (p == null) {
                        break;
                    }
                    final byte b = message.reader().readByte();
                    if (b == 1) {
                        GameCanvas.getImgEffect(p.session, message.reader().readShort());
                        break;
                    }
                    if (b == 2) {
                        GameCanvas.getDataEffect(p.session, message.reader().readShort());
                        break;
                    }
                    break;
                }

                case 25: {
                    // Send player info
                    if (p != null) {
                        p.nj.getPlace().sendPlayersInfo(p.nj, message);
                    }
                    break;
                }
                case 102: {
                    // Shinwa
                    val indexUI = message.reader().readByte();
                    val price = message.reader().readInt();
                    val item = p.nj.ItemBag[indexUI];
                    if (price < 0) {
                        p.session.sendMessageLog("Không thể thực hiện");
                        return;
                    }
                    int FEE = 5_000;
                    int FEE_GOLD = 1;
                    if (item != null && p.nj.xu >= FEE && p.luong >= FEE_GOLD) {
                        int dem = 0;
                        for (int i = -2; i <= 11; i++) {
                            dem += itemShinwaManager.items.get(i).size();
                        }
                        if (dem > 5000) {
                            p.session.sendMessageLog("Gian hàng Shinwa đã đầy. Vui lòng quay lại sau.");
                            return;
                        }
                        if (p.nj.limitShinwa < 1) {
                            GameCanvas.startOKDlg(p.session, "Mỗi ngày chỉ có thể đăng bán 10 món hàng");
                            return;
                        }
                        if (item.isExpires) {
                            return;
                        }
                        ItemShinwa itemShinwa = new ItemShinwa(item, p.nj.name, price);
                        ItemShinwaManager.add(itemShinwa);
                        p.nj.removeItemBag(indexUI);
                        p.nj.upxuMessage(-FEE);
                        p.removeLuong(FEE_GOLD);
                        p.nj.limitShinwa--;
                        p.endLoad(true);
                        Service.CharViewInfo(p, false);
                        LogHistory.log1("Shinwa: " + p.nj.name + " đã đăng bán " + item.quantity + " item " + item.id + " với giá: " + price + " xu.");
                    } else {
                        p.sendYellowMessage("Không đủ " + String.format("%,d", FEE) + " xu hoặc " + FEE_GOLD + " lượng để treo ");
                    }
                    break;
                }
                case 105: {
                    // Buy item shinwa
                    val itemId = message.reader().readInt();
                    val itemShinwa = ItemShinwaManager.findItemById(itemId);

                    if (!itemShinwa.isExpired() && itemShinwa.getPrice() <= p.nj.getXu()) {
                        final byte bagNull = p.nj.getAvailableBag();
                        if (bagNull == 0) {
                            p.session.sendMessageLog("Hành trang không đủ chổ trống");
                            return;
                        }
                        p.endLoad(true);
                        if (p.nj.xu < itemShinwa.getPrice()) {
                            // Khong du xu
                            p.sendYellowMessage("Không đủ xu để mua item");
                            return;
                        }

                        p.nj.upxuMessage(-itemShinwa.getPrice());
                        p.nj.addItemBag(false, itemShinwa.getItem());
                        returnXuToSeller(itemShinwa);
                        LogHistory.log1("Shinwa: " + p.nj.name + " đã mua " + itemShinwa.getItem().quantity + " item " + itemShinwa.getItem().id + " với giá: " + itemShinwa.getPrice() + " xu.");
                        final Message m = new Message(57);
                        m.writer().flush();
                        p.session.sendMessage(m);
                        m.cleanup();
                    }
                    break;
                }
                case 106: {
                    p.acceptInviteGT(message.reader().readInt());
                    break;
                }
                case 107: {
                    yesNoDlg(p, message);
                    break;
                }
                case 124: {
                    // Luyen ngoc
                    GameScr.ngocFeature(p, message);
                    break;
                }

                case 121: {
                    val index = message.reader().readByte();
                    val ninjaName = message.reader().readUTF();

                    if (index == 0) {
                        GameScr.requestRankedInfo(p, ninjaName);
                    } else {
//                         Thach dau
                        if (p.nj.getTournamentData().isCanGoNext()) {
                            if (p.nj.name.equals(ninjaName)) {
                                if (p.nj.getTournamentData().getRanked() == 1) {
                                    p.sendYellowMessage("Bạn là nhất rồi !!");
                                } else {
                                    p.sendYellowMessage("Bạn không thể chiến đấu với mình");
                                }
                                break;
                            }

                            final Tournament tournament = Tournament.getTypeTournament(p.nj.getLevel());
                            if (!tournament.checkBusy(ninjaName)) {
                                tournament.enter(p.nj, ninjaName);
                            } else {
                                p.sendYellowMessage("Đối thủ đang thi đấu với một người khác vui lòng đợi trong giây lát");
                            }

                        } else {
                            p.nj.getPlace().chatNPC(p, 4, "Thất bại là mẹ thành công ta biết con hơi buồn nhưng một sự thật đáng buồn là con hãy quay lại vào ngày hôm sau");
                        }
                    }

                    break;
                }
                case 113: {
                    //
                    if (p != null && p.nj != null && p.nj.candyBattle != null) {
                        val id = message.reader().readShort();
                        System.out.println(id);
                        p.nj.candyBattle.catKeo(p.nj, id);
                    }
                    break;
                }

                default: {
                    util.Debug("NOT MATCH " + message.getCommand());
                }
            }
            message.cleanup();
        } catch (Exception ex) {
            System.out.println("ERROR Process message");
            ex.printStackTrace();
        }
    }

    private void attackPlayerVsMob(Message message, User p) throws IOException {
        if (p != null && !p.nj.isDie) {
            val _ninja = p.nj;

            if (p != null && _ninja != null && _ninja.getCSkill() != -1) {
                if (_ninja.ItemBody[1] == null) {
                    p.sendYellowMessage("Vũ khí không hợp lệ");
                    return;
                }

                val template = _ninja.getCSkillTemplate();

                if (_ninja.mp < _ninja.getCSkillTemplate().manaUse) {
                    p.nj.getPlace().updateMp(p);
                    MessageSubCommand.sendMP(p.nj);
                    return;
                }
                if (_ninja.getSkills().size() > 0) {
                    byte size = message.reader().readByte();

                    if (size >= 0 && size <= template.maxFight) {
                        Mob[] arrMob = new Mob[size];
                        Ninja[] arrNinja = new Ninja[_ninja.getCSkillTemplate().maxFight];
                        try {
                            byte i;
                            for (i = 0; i < arrMob.length
                                    && i < template.maxFight; i = (byte) (i + 1)) {

                                arrMob[i] = _ninja.getPlace().getMob(message.reader().readUnsignedByte());
                                if (arrMob[i] == null) {
                                    continue;
                                }
                            }
                            for (i = 0; i < arrNinja.length
                                    && i < template.maxFight; i = (byte) (i + 1)) {
                                arrNinja[i] = _ninja.getPlace().getNinja(message.reader().readInt());
                            }
                        } catch (Exception exception) {
                        }
                        _ninja.getPlace().PlayerAttack(_ninja, arrMob, arrNinja);
                    }
                }
            }
            return;
        }
    }

    private void moveToBattleMap(int battleId, User p) {
        if (Battle.battles.containsKey(battleId)) {

            val battle = Battle.battles.get(battleId);
            p.nj.isBattleViewer = true;
            battle.addViewerIfNotInMatch(p.nj);
            p.nj.enterSamePlace(battle.getPlace(), null);

        }
    }

    private void handleInputMessage(Message message, User p) throws CloneNotSupportedException, IOException {
        // Input
        try {
            if (p != null && p.nj != null && p.session != null && message != null && message.reader().available() > 0) {
                Message cloneMessage = message.cloneMessage();
                Short menuId = message.reader().readShort();
                String str = message.reader().readUTF();
                if (!str.equals("")) {
                    util.Debug("menuId " + menuId + " str " + str);
                    byte b = -1;
                    if (message.reader().available() > 0) {
                        try {
                            b = message.reader().readByte();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                    message.cleanup();
                    if (menuId == 1) {
                        // Teleport
                        teleport(p, str);
                    } else if (menuId == 2) {
                        Ninja temp = PlayerManager.getInstance().getNinja(str);
                        if (temp != null) {
                            Ninja friendNinja = p.nj.getPlace().getNinja(temp.id);
                            if (friendNinja != null && friendNinja.id == p.nj.id) {
                                p.nj.getPlace().chatNPC(p, (short) 0, Language.NAME_LOI_DAI);
                            } else if (friendNinja != null && friendNinja.id != p.nj.id) {
                                p.sendRequestBattleToAnother(friendNinja, p.nj);
                                p.nj.getPlace().chatNPC(p, (short) 0, "Ta đã gửi lời mời thách đấu đến " + friendNinja.name);
                            } else {
                                p.nj.getPlace().chatNPC(p, (short) 0, Language.NOT_IN_ZONE);
                            }
                        } else {
                            p.nj.getPlace().chatNPC(p, (short) 0, "Người chơi này không ở trong cùng khu với con hoặc không tồn tại, ta không thể gửi lời mời!");
                        }
                    } else if (menuId == 3) {
                        // Đặt cược lôi đài
                        String check = str.replaceAll("\\s+", "");
                        if (!util.isNumericLong(str) || check.equals("") || !util.isNumericInt(str)) {
                            p.nj.getPlace().chatNPC(p, (short) 37, "Giá trị tiền cược nhập vào không đúng");
                            return;
                        }
                        long tienCuoc = Long.parseLong(str);
                        if (tienCuoc > p.nj.xu || p.nj.xu < 1000) {
                            p.nj.getPlace().chatNPC(p, (short) 37, "Con không đủ xu để đặt cược");
                            return;
                        }
                        if (tienCuoc < 1000 || tienCuoc % 50 != 0) {
                            p.nj.getPlace().chatNPC(p, (short) 37, "Xu cược phải lớn hơn 1000 xu và chia hết cho 50");
                            return;
                        }
                        Dun dun = null;
                        if (p.nj.dunId != -1) {
                            if (Dun.duns.containsKey(p.nj.dunId)) {
                                dun = Dun.duns.get(p.nj.dunId);
                            }
                        }
                        if (dun != null) {
                            if (dun.c1.id == p.nj.id) {
                                if (dun.tienCuocTeam2 != 0 && dun.tienCuocTeam2 != tienCuoc) {
                                    p.nj.getPlace().chatNPC(p, (short) 37, "Đối thủ của con đã đặt cược " + util.getFormatNumber(dun.tienCuocTeam2) + " xu con hãy đặt lại đi!");
                                    return;
                                }
                                if (dun.tienCuocTeam1 != 0) {
                                    p.nj.getPlace().chatNPC(p, (short) 37, "Con đã đặt cược trước đó rồi.");
                                    return;
                                }

                                dun.tienCuocTeam1 = tienCuoc;
                                p.nj.upxuMessage(-tienCuoc);
                                p.nj.getPlace().chatNPC(p, (short) 37, "Con đã đặt cược " + dun.tienCuocTeam1 + " xu");
                                dun.c2.p.sendYellowMessage(dun.c1.name + " đã thay đổi tiền đặt cược: " + util.getFormatNumber(dun.tienCuocTeam1) + " xu.");

                            } else if (dun.c2.id == p.nj.id) {
                                if (dun.tienCuocTeam1 != 0 && dun.tienCuocTeam1 != tienCuoc) {
                                    p.nj.getPlace().chatNPC(p, (short) 37, "Đối thủ của con đã đặt cược " + util.getFormatNumber(dun.tienCuocTeam1) + " xu con hãy đặt lại đi!");
                                    return;
                                }
                                if (dun.tienCuocTeam2 != 0) {
                                    p.nj.getPlace().chatNPC(p, (short) 37, "Con đã đặt cược trước đó rồi.");
                                    return;
                                }

                                dun.tienCuocTeam2 = tienCuoc;
                                p.nj.upxuMessage(-tienCuoc);
                                p.nj.getPlace().chatNPC(p, (short) 37, "Con đã đặt cược " + util.getFormatNumber(dun.tienCuocTeam2) + " xu");
                                dun.c1.p.sendYellowMessage(dun.c2.name + " đã thay đổi tiền đặt cược: " + util.getFormatNumber(dun.tienCuocTeam2) + " xu.");
                            }

                            if (dun.tienCuocTeam1 != 0 && dun.tienCuocTeam2 != 0 && dun.tienCuocTeam1 == dun.tienCuocTeam2 && dun.team1.size() > 0 && dun.team2.size() > 0) {
                                if (dun.tienCuocTeam1 >= 10000L) {
                                    Manager.chatKTG("Người chơi " + dun.c1.name + " (" + dun.c1.level + ")"
                                            + " đang thách đấu với " + dun.c2.name + " (" + dun.c2.level + ") " + util.getFormatNumber(dun.tienCuocTeam1) + " xu tại lôi đài, hãy mau mau đến xem và cổ vũ.");
                                }
                                dun.startDun();
                            }
                        } else {
                            return;
                        }
                    } else if (menuId == 4) {
                        final ClanManager clan = ClanManager.getClanByName(str);
                        if (clan != null) {
                            val tocTruong = clan.members.stream().filter(m -> {
                                val ninja = m.getNinja();
                                if (ninja == null) {
                                    return false;
                                }
                                if (ninja.clan.typeclan == Constants.TOC_TRUONG) {
                                    return true;
                                }
                                return false;
                            }).map(c -> c.getNinja()).findFirst().orElse(null);
                            if (tocTruong != null) {
                                final Ninja n = p.nj.getPlace().getNinja(tocTruong.name);
                                if (n != null) {
                                    p.nj.getPlace().chatNPC(p, (short) 32, "Ta đã gửi lời mời thách đấu của ngươi đến tộc trưởng gia tộc " + str);
                                    sendRequestBattleToAnother(tocTruong, p.nj, -150);
                                } else {
                                    p.nj.getPlace().chatNPC(p, (short) 32, "Tộc trưởng không cùng khu với bạn");
                                }
                            } else {
                                p.sendYellowMessage("Tộc trưởng gia tộc bạn thách đấu không online");
                            }
                        } else {
                            p.sendYellowMessage("Không tìm thấy gia tộc trên hệ thống");
                        }
                    } else if (menuId == 5) {
                        try {
                            SQLManager.executeUpdate("UPDATE player SET `status` = 'lock' WHERE `username`='" + p.username + "' LIMIT 1");
                            p.session.disconnect();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    } else if (menuId == 6) {
                        if (p.nj.party != null) {
                            if (p.nj.party.master != p.nj.id) {
                                p.sendYellowMessage("Chỉ có trưởng nhóm mới có thể đặt cược được");
                            }
                        }
                        if (p.nj.hasBattle()) {
                            p.nj.getBattle().setXu(Long.parseLong(str), p.nj.party != null ? p.nj.party : p.nj);
                        } else {
                            p.nj.getClanBattle().setXu(p.nj, Integer.parseInt(str.replace(",", "").replace(".", "").trim()));
                        }
                    } else if (p != null) {
                        Draw.Draw(p, (Message) cloneMessage);

                        return;
                    }
                } else {
                    if (menuId == 102) {
                        p.typemenu = 92;
                        MenuController.doMenuArray(p, new String[]{"Vòng xoay vip", "Vòng xoay thường"});
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (message != null) {
                message.cleanup();
            }
        }
    }

    private void teleport(User p, String str) throws IOException {
        Ninja ninja = PlayerManager.getInstance().getNinja(str);
        if (ninja == null) {
            p.sendYellowMessage("Người chơi đã offline");
            return;
        }
        final Map map = Server.getMapById(ninja.getMapid());
        if (map != null && (map.isLangCo() || Map.isCaveMap(map.id) || map.VDMQ() || map.isLoiDai())) {
            p.sendYellowMessage("Con không thể di chuyển đến khu vực cấm này nếu đi vào sẽ phải có bình oxy mới được");
            return;
        }

        p.nj.getPlace().leave(p);
        ninja.getPlace().Enter(p);
        p.nj.x = ninja.x;
        p.nj.y = ninja.y;
        ninja.getPlace().sendXYPlayer(p);
    }

    private void sendRequestBattleToAnother(Ninja friendNinja, Ninja p, int id) throws IOException {

        val m = new Message(id);
        m.writer().writeInt(p.id);
        friendNinja.p.sendMessage(m);
        m.cleanup();
    }

    private void goToWaitingBetRoom(Ninja partner, Ninja me) throws IOException {
        Battle battle = new Battle(partner, me);
        battle.tick();
        battle.enter();
    }

    public static void accpetDun(User player, Message m) {
        try {
            if (player != null && player.nj != null && player.session != null && player.nj.getPlace() != null && !player.nj.isDie && m != null && m.reader().available() > 0) {
                if (player.nj.isNhanban) {
                    player.session.sendMessageLog(Language.NOT_FOR_PHAN_THAN);
                } else {
                    int pid = m.reader().readInt();
                    m.cleanup();
                    User _p = player.nj.getPlace().getNinja(pid).p;
                    Dun dun = new Dun();
                    Place tileMapTemp = player.nj.getPlace();
                    Ninja _charTemp1 = null;
                    Ninja _charTemp2 = null;
                    if (_p != null) {
                        Ninja _charP;
                        int i;
                        if (player.nj.party == null) {
                            player.nj.isInDun = true;
                            player.nj.dunId = dun.dunID;
                            player.nj.mapKanata = player.nj.getMapId();
                            if (player.nj.getTypepk() != 0) {
                                Service.ChangTypePkId(player.nj, (byte) 0);
                            }

                            dun.team1.add(player.nj);
                            _charTemp1 = player.nj;
                            dun.idC1 = player.nj.id;
                            dun.name1 = player.nj.name;
                            dun.lv1 = player.nj.getLevel();
                            player.nj.getPlace().leave(player.nj.p);
                            dun.map[0].area[0].EnterMap0WithXY(player.nj, (short) 398, (short) -1);
                        } else {
                            if (player.nj.party.master != player.nj.id) {
                                player.sendYellowMessage("Bạn không phải trưởng nhóm, nên không thể chấp nhận lời mời lôi đài này");
                                return;
                            }

                            for (i = 0; i < player.nj.party.ninjas.size(); ++i) {
                                _charP = (Ninja) player.nj.party.ninjas.get(i);
                                if (tileMapTemp.getNinja(_charP.id) != null && !_charP.isNhanban) {
                                    _charP.isInDun = true;
                                    _charP.dunId = dun.dunID;
                                    _charP.mapKanata = _charP.c.getMapId();
                                    if (_charP.getTypepk() != 0) {
                                        Service.ChangTypePkId(_charP, (byte) 0);
                                    }

                                    if (_charP.id == player.nj.party.master) {
                                        _charTemp1 = _charP;
                                        dun.idC1 = _charP.id;
                                        dun.name1 = _charP.name;
                                        dun.lv1 = _charP.getLevel();
                                    }

                                    dun.team1.add(_charP);
                                    _charP.getPlace().leave(_charP.p);
                                    dun.map[0].area[0].EnterMap0WithXY(_charP, (short) 398, (short) -1);
                                } else {
                                    player.nj.party.exitParty(_charP);
                                }
                            }
                        }
                        if (_p.nj.party != null) {
                            for (i = 0; i < _p.nj.party.ninjas.size(); ++i) {
                                _charP = (Ninja) _p.nj.party.ninjas.get(i);
                                if (tileMapTemp.getNinja(_charP.id) != null && !_charP.isNhanban) {
                                    _charP.isInDun = true;
                                    _charP.dunId = dun.dunID;
                                    _charP.mapKanata = _charP.c.getMapId();
                                    if (_charP.getTypepk() != 0) {
                                        Service.ChangTypePkId(_charP, (byte) 0);
                                    }
                                    if (_charP.id == _p.nj.party.master) {
                                        _charTemp2 = _charP;
                                        dun.idC2 = _charP.id;
                                        dun.name2 = _charP.name;
                                        dun.lv2 = _charP.getLevel();
                                    }
                                    dun.team2.add(_charP);
                                    _charP.getPlace().leave(_charP.p);
                                    dun.map[0].area[0].EnterMap0WithXY(_charP, (short) 153, (short) -1);
                                } else {
                                    _p.nj.party.exitParty(_charP);
                                }
                            }
                        } else {
                            _p.nj.isInDun = true;
                            _p.nj.dunId = dun.dunID;
                            _p.nj.mapKanata = _p.nj.getMapId();
                            if (_p.nj.getTypepk() != 0) {
                                Service.ChangTypePkId(_p.nj, (byte) 0);
                            }
                            dun.team2.add(_p.nj);
                            _charTemp2 = _p.nj;
                            dun.idC2 = _p.nj.id;
                            dun.name2 = _p.nj.name;
                            dun.lv2 = _p.nj.getLevel();
                            _p.nj.getPlace().leave(_p.nj.p);
                            dun.map[0].area[0].EnterMap0WithXY(_p.nj, (short) 153, (short) -1);
                        }
                        if (_charTemp1 != null && _charTemp2 != null) {
                            dun.c1 = _charTemp1;
                            dun.c2 = _charTemp2;
                        }
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (m != null) {
                m.cleanup();
            }
        }
    }

    public static void viewDun(User player, Message m) {
        try {
            if (player != null && player.nj != null && player.session != null && player.nj.getPlace() != null && !player.nj.isDie && m != null && m.reader().available() > 0) {
                int idDun = m.reader().readUnsignedByte();
                Ninja _char = player.nj;
                Dun dun = null;
                if (Dun.duns.containsKey(idDun)) {
                    dun = Dun.duns.get(idDun);
                    if (dun != null) {
                        if (!dun.isStart) {
                            _char.p.session.sendMessageLog("Trận đấu này chưa diễn ra, hãy quay lại sau.");
                            return;
                        }
                        _char.dunId = idDun;
                        _char.isInDun = true;
                        _char.mapKanata = _char.getMapId();
                        if (_char.getTypepk() != 0) {
                            Service.ChangTypePkId(_char, (byte) 0);
                        }
                        dun.viewer.add(_char);
                        _char.getPlace().leave(_char.p);
                        _char.yDun = 336;
                        dun.map[1].area[0].EnterMap0WithXY(_char, (short) util.nextInt(280, 490), (short) 336);
                    } else {
                        _char.p.session.sendMessageLog("Trận đấu này đã kết thúc hoặc không tồn tại.");
                        return;
                    }
                } else {
                    _char.p.session.sendMessageLog("Gặp lỗi, hãy đăng xuất và vào lại nhé!");
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (m != null) {
                m.cleanup();
            }
        }
    }

    public static void yesNoDlg(User player, Message m) {
        try {
            if (player != null && player.nj != null && player.session != null && !player.nj.isDie && m != null && m.reader().available() > 0) {
                int type = m.reader().readByte();
                switch (type) {
                    case 0: {
                        GameScr.NangMat(player, player.nj.ItemBody[14], 0);
                        break;
                    }
                    case 1: {
                        GameScr.NangMat(player, player.nj.ItemBody[14], 1);
                        break;
                    }
                    case 2: {
                        GameScr.HuyNhiemVuDanhVong(player);
                        break;
                    }
                    case 3: {
                        Item im = player.nj.ItemMuaLai.get(player.indexMenuBox);
                        int xu = 10000;
                        if (im.isTypeBody()) {
                            xu = im.id * 100;
                        }
                        if (player.nj.xu < xu) {
                            player.session.sendMessageLog("Ngươi không đủ " + xu + " xu để mua lại vật phẩm này!");
                            return;
                        }
                        player.nj.addItemBag(false, im);
                        player.nj.upXuMessage(-xu);
                        player.nj.ItemMuaLai.remove(player.indexMenuBox);
                        player.endLoad(true);
                        break;
                    }
                    case 4: {
                        if (player.nj.ItemBody[15] == null) {
                            return;
                        }
                        GameScr.TinhLuyenBK(player, player.nj.ItemBody[15], 0);
                        break;
                    }
                    case 12: {
                        if (player.nj.ItemBody[12] == null) {
                            return;
                        }
                        GameScr.UpgradeYoroi(player);
                        break;
                    }

                    default: {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (m != null) {
                m.cleanup();
            }
        }
    }
}
