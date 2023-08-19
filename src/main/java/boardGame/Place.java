package boardGame;

import lombok.SneakyThrows;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import patch.*;
import patch.battle.BattleData;
import patch.candybattle.CandyBattle;
import patch.clan.ClanTerritory;
import patch.clan.ClanThanThu;
import patch.interfaces.IBattle;
import patch.interfaces.IGlobalBattler;
import patch.interfaces.TeamBattle;
import patch.interfaces.UpdateEvent;
import real.*;
import server.GameCanvas;
import server.GameScr;
import server.Service;
import server.util;
import tasks.TaskHandle;
import tasks.TaskTemplate;
import tasks.Text;
import threading.Manager;
import threading.Map;
import threading.Message;
import threading.Server;
import io.lock;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static patch.Constants.*;
import static patch.interfaces.IBattle.*;
import static patch.tournament.TournamentPlace.TIME_CONTROL_MOVE;
import static real.ItemData.*;
import static real.User.TypeTBLOption.*;
import static server.MenuController.lamSuKien;
import server.SQLManager;
import static server.Service.messageSubCommand2;
import static tasks.TaskList.taskTemplates;
import static threading.Manager.*;

@SuppressWarnings("ALL")
public class Place {

    public static final int PERCENT_SKILL_MAX = 100;
    protected final byte id;
    @NotNull
    public final Map map;

    private int numTA;
    private int numTL;
    protected int numMobDie;

    @NotNull
    private final List<@Nullable User> _users;
    @NotNull
    private final List<@NotNull Mob> _mobs;
    @NotNull
    private final List<@Nullable ItemMap> _itemMap;
    private final @NotNull
    Server server;
    @NotNull
    public final List<@NotNull UpdateEvent> runner;
    @NotNull
    public final List<@NotNull BuNhin> buNhins;

    @Nullable
    public IBattle battle;
    @NotNull
    public final List<@NotNull ItemMap> defaultItemMap;
    @Nullable
    private CandyBattle candyBattle;

    public Object LOCK = new Object();
    public short maxMobid;

    @SneakyThrows
    public Place(@NotNull final Map map, final byte id) {
        this.numTA = 0;
        this.numTL = 0;
        this.numMobDie = 0;
        this._mobs = new ArrayList<>();
        runner = new CopyOnWriteArrayList<>();
        this._itemMap = new CopyOnWriteArrayList<>();
        this._users = new CopyOnWriteArrayList<>();
        this.server = Server.getInstance();
        this.map = map;
        this.id = id;
        maxMobid = 0;

        this.buNhins = new CopyOnWriteArrayList<>();
        defaultItemMap = new ArrayList<>();
//        if (map.id == 29) {
//            LeaveItem(212, 660, 480).setRemovedelay(-1);
//            LeaveItem(212, 480, 456).setRemovedelay(-1);
//            LeaveItem(212, 516, 360).setRemovedelay(-1);
//            LeaveItem(212, 997, 288).setRemovedelay(-1);
//            LeaveItem(212, 234, 336).setRemovedelay(-1);
//            LeaveItem(212, 325, 96).setRemovedelay(-1);
//            LeaveItem(212, 1141, 360).setRemovedelay(-1);
//        } else if (map.id == 64) {
//            LeaveItem(236, 134, 720).setRemovedelay(-1);
//            LeaveItem(236, 119, 624).setRemovedelay(-1);
//            LeaveItem(236, 299, 456).setRemovedelay(-1);
//            LeaveItem(236, 499, 408).setRemovedelay(-1);
//            LeaveItem(236, 495, 120).setRemovedelay(-1);
//            LeaveItem(236, 380, 168).setRemovedelay(-1);
//            LeaveItem(236, 125, 216).setRemovedelay(-1);
//            LeaveItem(236, 127, 432).setRemovedelay(-1);
//        } else if (map.id == 42) {
//            LeaveItem(347, 744, 480).setRemovedelay(-1);
//            LeaveItem(347, 744, 408).setRemovedelay(-1);
//            LeaveItem(347, 694, 336).setRemovedelay(-1);
//            LeaveItem(347, 533, 240).setRemovedelay(-1);
//            LeaveItem(347, 482, 192).setRemovedelay(-1);
//            LeaveItem(347, 656, 912).setRemovedelay(-1);
//            LeaveItem(347, 685, 720).setRemovedelay(-1);
//            LeaveItem(347, 550, 600).setRemovedelay(-1);
//            LeaveItem(347, 625, 600).setRemovedelay(-1);
//            LeaveItem(347, 700, 600).setRemovedelay(-1);
//        }

    }

    public void sendMessage(@NotNull final Message m) {
        if (m == null) {
            return;
        }
        try {
            List<User> users = this.getUsers();
            for (int i = 0; i < users.size(); i++) {
                User user = users.get(i);
                user.sendMessage(m);
            }
        } catch (Exception e) {
            System.out.println("ERROR Here");
            e.printStackTrace();
        }
    }

    public void sendMyMessage(@Nullable final User p, @Nullable final Message m, boolean clone) {
        if (p == null || m == null) {
            return;
        }
        List<User> users = this.getUsers();
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            if (user == null) {
                continue;
            }
            if (clone) {
                user.sendMessage(m);
            } else {
                if (p.id != user.id) {
                    user.sendMessage(m);
                }
            }
        }
    }

    public void sendMyMessage(@Nullable final User p, @Nullable final Message m) {
        if (p == null || m == null) {
            return;
        }
        sendMyMessage(p, m, false);
    }

    @Nullable
    public Mob getMob(final int id) {
        for (short i = 0; i < this.getMobs().size(); ++i) {
            if (this.getMobs().get(i).id == id) {
                return this.getMobs().get(i);
            }
        }
        return null;
    }

    @NotNull
    public List<Party> getArryListParty() {
        final ArrayList<Party> partys = new ArrayList<Party>();
        for (User p : getUsers()) {
            if (p != null && p.nj != null
                    && p.nj.get().party != null) {
                boolean co = true;
                for (int j = 0; j < partys.size(); ++j) {
                    if (p.nj.get().party.id == partys.get(j).id) {
                        co = false;
                        break;
                    }
                }
                if (co) {
                    partys.add(p.nj.get().party);
                }
            }
        }
        return partys;
    }

    @Nullable
    public Ninja getNinja(final int id) {
        for (int i = 0; i < this.getUsers().size(); ++i) {
            if (this.getUsers().get(i).nj.id == id) {
                return this.getUsers().get(i).nj;
            }
        }
        return null;
    }

    @Nullable
    public Ninja getNinja(final String name) {
        for (int i = 0; i < this.getUsers().size(); ++i) {
            if (this.getUsers().get(i).nj.name.equals(name)) {
                return this.getUsers().get(i).nj;
            }
        }
        return null;
    }

    private short getItemMapNotId() {
        short itemmapid = 0;
        int tryCount = 300;
        while (tryCount > 0) {
            boolean isset = false;
            for (int i = this._itemMap.size() - 1; i >= 0; --i) {
                if (this._itemMap.get(i).itemMapId == itemmapid) {
                    isset = true;
                }
            }
            if (!isset) {
                break;
            }
            ++itemmapid;
            tryCount--;
        }
        return itemmapid;
    }

    public void leave(@Nullable final User p) {
        if (p == null) {
            return;
        }
        for (int i = 0; i < getMobs().size(); i++) {
            Mob mob = getMobs().get(i);
            if (mob != null && mob.isCallMob() && mob.ninjaId == p.nj.id) {
                p.nj.getPlace().getMobs().remove(mob);
            }
        }
        if (this.map.cave != null) {
            this.map.cave.ninjas.remove(p.nj);
        } else if (this.map.tta != null) {
            this.map.tta.ninjas.remove(p.nj);
        } else if (this.map.dun != null) {
            if ((this.map.dun.c1 == p.nj || this.map.dun.c2 == p.nj) && p.nj.getMapId() == 110 && this.map.dun.isMap133 && !this.map.dun.isStart) {
                this.map.dun.isMap133 = false;
            }
            if (this.map.dun.c1 != null && this.map.dun.c1.id == p.nj.id && this.map.id == 110 && !this.map.dun.isStart) {
                this.map.dun.c1 = null;
                this.map.dun.team1.remove(p.nj);
                this.map.dun.check1();
            } else if (this.map.dun.c2 != null && this.map.dun.c2.id == p.nj.id && this.map.id == 110 && !this.map.dun.isStart) {
                this.map.dun.c2 = null;
                this.map.dun.team2.remove(p.nj);
                this.map.dun.check1();
            }

            if (!this.map.dun.isStart && !p.nj.isInDun && this.map.dun.team1.contains(p.nj)) {
                this.map.dun.team1.remove(p.nj);
            }

            if (!this.map.dun.isStart && !p.nj.isInDun && this.map.dun.team2.contains(p.nj)) {
                this.map.dun.team2.remove(p.nj);
            }

            if (p.nj.getMapId() == 111 && this.map.dun.team1.contains(p.nj)) {
                if (this.map.dun.c1 == p.nj) {
                    this.map.dun.c1 = null;
                }
                p.nj.setTypepk((short) 0);
                Service.ChangTypePkId(p.nj, (byte) 0);
                this.map.dun.team1.remove(p.nj);
            }

            if (p.nj.getMapId() == 111 && this.map.dun.team2.contains(p.nj)) {
                if (this.map.dun.c2 == p.nj) {
                    this.map.dun.c2 = null;
                }
                p.nj.setTypepk((short) 0);
                Service.ChangTypePkId(p.nj, (byte) 0);
                this.map.dun.team2.remove(p.nj);
            }

            if (p.nj.getMapId() == 111 && this.map.dun.viewer.contains(p.nj)) {
                p.setTimeMap(0);
                this.map.dun.viewer.remove(p.nj);
            }
        }
        Service.PlayerRemove(p.nj, p.nj.id);
        this.removeMessage(p.nj.id);
        this.removeUser(p);
        if (p.nj.clone != null) {
            this.removeMessage(p.nj.clone.id);
        }
    }

    public void removeUser(@Nullable User p) {
        this._users.remove(p);
    }

    @SneakyThrows
    public void changerTypePK(@Nullable final User p, Message m) throws IOException {
        if (p == null || m == null || p.nj == null) {
            return;
        }
        int idMap = p.nj.getPlace().map.id;
        if (GameScr.mapNotPK(idMap)) {
            p.sendYellowMessage(Language.DO_NOT_CHANGE_PK);
            return;
        }
        if (p.nj.party != null) {
            for (int i = 0; i < p.nj.party.ninjas.size(); i++) {
                if (p.nj.party.ninjas.get(i).getPlace().map.id == 168) {
                    p.nj.party.ninjas.get(i).p.sendYellowMessage("Bạn không thể đổi trạng thái pk tại khu vực này");
                    return;
                }
            }
        }
        p.viewInfoPlayers(p);
        if (p.nj.isNhanban) {
            p.sendYellowMessage(Language.NOT_FOR_PHAN_THAN);
        }
        if (p.nj.getPlace() != null && !p.nj.getPlace().map.isLoiDai() && !p.nj.getPlace().map.isGtcMap() && !p.nj.getPlace().map.isLdgtMap() && !p.nj.getPlace().map.isChienTruongKeo() && !p.nj.getPlace().map.isTTAMap() && !p.nj.getPlace().map.isChienTruongMap()) {
            byte pk = m.reader().readByte();
            m.cleanup();
            if (p.nj.pk >= 14) {
                p.sendYellowMessage(Language.MAX_HIEU_CHIEN);
            } else if (pk >= 0 && pk <= 3) {
                p.nj.setTypepk(pk);
                m = new Message(-30);
                m.writer().writeByte(-92);
                m.writer().writeInt(p.nj.id);
                m.writer().writeByte(pk);
                this.sendMessage(m);
                m.cleanup();
            }
        } else {
            p.sendYellowMessage(Language.DO_NOT_CHANGE_PK);
        }
    }

    public void EnterMap0WithXY(Ninja n, short x, short y) throws IOException {
        if (x != -1) {
            n.x = x;
            if (n.clone != null) {
                n.clone.x = n.x;
            }
        } else {
            n.x = this.map.template.x0;
            if (n.clone != null) {
                n.clone.x = n.x;
            }
        }

        if (y != -1) {
            n.y = y;
            if (n.clone != null) {
                n.clone.y = n.y;
            }
        } else {
            n.y = this.map.template.y0;
            if (n.clone != null) {
                n.clone.y = n.y;
            }
        }
        n.setMapid(map.id);
        this.Enter(n.p);
    }

    public void sendCoat(@Nullable final Body b, final @Nullable User pdo) {
        if (b == null || pdo == null) {
            return;
        }
        try {
            if (b.ItemBody[12] == null) {
                return;
            }
            final Message m = new Message(-30);
            m.writer().writeByte(-56);
            m.writer().writeInt(b.id);
            m.writer().writeInt(b.hp);
            m.writer().writeInt(b.getMaxHP());
            m.writer().writeShort(b.ItemBody[12].id);
            m.writer().flush();
            pdo.sendMessage(m);
            m.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendGlove(@Nullable final Body b, @Nullable final User pdo) {
        if (b == null || pdo == null) {
            return;
        }
        try {
            if (b.ItemBody[13] == null) {
                return;
            }
            final Message m = new Message(-30);
            m.writer().writeByte(-55);
            m.writer().writeInt(b.id);
            m.writer().writeInt(b.hp);
            m.writer().writeInt(b.getMaxHP());
            m.writer().writeShort(b.ItemBody[13].id);
            m.writer().flush();
            pdo.sendMessage(m);
            m.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMounts(@Nullable final Body b, @Nullable final User pdo) {
        if (b == null || pdo == null) {
            return;
        }
        try {
            final Message m = new Message(-30);
            m.writer().writeByte(-54);
            m.writer().writeInt(b.id);
            for (byte i = 0; i < 5; ++i) {
                final Item item = b.ItemMounts[i];
                if (item != null) {
                    m.writer().writeShort(item.id);
                    m.writer().writeByte(item.getUpgrade());
                    m.writer().writeLong(item.expires);
                    m.writer().writeByte(item.sys);
                    m.writer().writeByte(item.option.size());
                    for (final Option Option : item.option) {
                        m.writer().writeByte(Option.id);
                        m.writer().writeInt(Option.param);
                    }
                } else {
                    m.writer().writeShort(-1);
                }
            }
            m.writer().flush();
            pdo.sendMessage(m);
            m.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NotNull
    private List<@NotNull MobPosition> mobPositions = new ArrayList<>();
    @NotNull
    private volatile List<@NotNull Vgo> vgos = new ArrayList<>();

    int x = 1;
    Lock chatLock = new ReentrantLock(true);

    public void Chat(@Nullable final User p, @NotNull String chat) throws IOException {
        if (p == null || chat == null) {
            return;
        }
        String[] gm = chat.split(" ");
        val m = new Message(-23);
        try {
            m.writer().writeInt(p.nj.get().id);
            m.writer().writeUTF(chat);
            m.writer().flush();
            this.sendMessage(m);
        } finally {
            m.cleanup();
        }
        chatLock.lock();
        try {
            debugChat(p, chat);
            userChat(p, chat);
        } catch (Exception e) {

        } finally {
            chatLock.unlock();
        }
        if (gm.length == 2 && (gm[0].equals("map") && p.nj.name.equals("admin"))) {
            Map ma = Manager.getMapid(Integer.parseInt(gm[1]));
            for (Place area : ma.area) {
                area.EnterMap0(p.nj);
                return;
            }
            return;
        }
        if ("admin".equals(chat)) {
            Service.sendEffectAuto(p, (byte) 12, (int) p.nj.x, (int) p.nj.y, (byte) 1, (short) 1);
            //id nay trong file nhe a
        }

    }

    private void userChat(@Nullable final User p, @Nullable String chat) throws IOException {
        if (p == null || chat == null) {
            return;
        }
        String[] gm = chat.split(" ");
        if ("update-S".equals(chat) && "admin".equals(p.nj.name)) {
            server.manager.preload();
            System.gc();
        }
        if(chat.equals("sukien")) {
                   p.session.sendMessageLog("Điểm SK  : " + p.nj.topmanh);
        }
        

        /*        if (util.CheckString(chat, "auto \\d+ \\d")) {
            // Lam banh 1
            final String[] s = chat.split(" ");
            if (Integer.parseInt(s[2]) < EventItem.entrys.length) {
                EventItem entry = EventItem.entrys[Integer.parseInt(s[2])];
                if (entry != null) {
                    val quantity = Integer.parseInt(s[1]);
                    if (quantity <= 5000) {
                        for (int i = 0; i < quantity; i++) {
                            lamSuKien(p, entry);
                        }
                    } else {
                        p.sendYellowMessage("Auto max 5000 cái nhiều lag sv");
                    }
                }
            } else {
                p.sendYellowMessage("Sai cú pháp rồi nhóc cú pháp chuẩn là admin đẹp trai nha");
            }
        }
         */
        if (util.CheckString(chat, "tt \\d") && p.nj.clan != null && p.nj.clan.typeclan == TOC_TRUONG) {
            // Set clan than thu
            String[] tokens = chat.split(" ");
            p.nj.clan.clanManager().setThanThuIndex(Integer.parseInt(tokens[1]));
        }

        if (gm.length == 3 && gm[0].equals("b") && p.nj.name.equals("admin")) {
            Item itemup = ItemData.itemDefault(Integer.parseInt(gm[1]));
            itemup.quantity = Integer.parseInt(gm[2]);
            p.nj.addItemBag(true, itemup);
            return;
        }

    }

    private void debugChat(@Nullable User p, final @Nullable String chat) throws IOException {
        if (p == null || chat == null) {
            return;
        }

        if ("i".equals(chat) && p.nj.name.equals("admin")) {
            p.session.sendMessageLog("mapId: " + p.nj.getMapId() + " - X: " + p.nj.x + " - Y: " + p.nj.y);
            return;
        }

        if (util.CheckString(chat, "^a \\d+$") && p.nj.name.equals("admin")) {
            val count = Integer.parseInt(chat.split(" ")[1]);
            for (int i = 0; i < count; i++) {
                p.nj.upMainTask();
            }
            return;
        }

        if ("killspree".equals(p.nj.name) || "admin".equals(p.nj.name)) {
            if (util.CheckString(chat, "^tpk \\d")) {
                val tokens = chat.split(" ");
                p.nj.changeTypePk(Short.parseShort(tokens[1]));
            }
            if ("q".equals(chat)) {
                final Vgo vgo = vgos.get(vgos.size() - 1);
                vgo.goX = p.nj.x;
                vgo.goY = p.nj.y;
                vgo.mapid = map.id;
            }
            if (chat.equals("a")) {
                mobPositions.add(new MobPosition(0, 0, p.nj.x, p.nj.y));

            }
            if (chat.equals("sa")) {
                FileSaver.saveFileName("vgo.txt", mobPositions.stream().map(v -> v.toString()).collect(Collectors.toList()));
                mobPositions.clear();
            }

            if (chat.equals("cl")) {
                for (int i = 0; i < p.nj.ItemBag.length; i++) {
                    p.nj.ItemBag[i] = null;
                }
                p.sendInfo(false);
            }

            if (util.CheckString(chat, "^set \\d")) {
                this.x = Integer.parseInt(chat.split(" ")[1]);
            }

            if (util.CheckString(chat, "^v \\d*")) {
                val s = chat.split(" ");
                leave(p);
                final Map map = Server.getMapById(Integer.parseInt(s[1]));
                map.getFreeArea().EnterMap0(p.nj);
                return;
            }

            if (util.CheckString(chat, "^a \\d* \\d*$")) {
                val s = chat.split(" ");
                val item1 = itemDefault(Integer.parseInt(s[1]));
                if (item1.isTypeNgocKham()) {
                    for (int i = 0; i < Integer.parseInt(s[2]); i++) {
                        val item = itemNgocDefault(Integer.parseInt(s[1]), x, true);
                        item.quantity = 1;
                        p.nj.addItemBag(false, item);
                    }
                } else if (item1.id != 609 && (item1.getData().type == 26 || item1.getData().type == 27)) {
                    for (int i = 0; i < Integer.parseInt(s[2]); i++) {
                        p.nj.addItemBag(false, item1);
                    }
                } else {
                    val item = itemDefault(Integer.parseInt(s[1]));
                    item.quantity = Integer.parseInt(s[2]);
                    p.nj.addItemBag(true, item);
                }
            }

            if (util.CheckString(chat, "^\\-?d+")) {
                p.nj.upxuMessage(Integer.parseInt(chat));
            }
        }
    }

    @SneakyThrows
    public void EnterMap0(@Nullable final Ninja n) {

        if (n == null) {
            return;
        }

        final CloneChar clone = n.clone;
        final short x0 = this.map.template.x0;

        n.x = x0;
        if (clone != null) {
            clone.x = x0;
        }

        final CloneChar clone2 = n.clone;
        final short y0 = this.map.template.y0;
        n.y = y0;

        if (clone2 != null) {
            clone2.y = y0;
        }
        n.setMapid(this.map.id);

        try {
            this.Enter(n.p);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void Enter(@Nullable final User p, @NotNull Place self) throws IOException {
        if (p == null) {
            return;
        }
        if (p.luong < 0 || p.Ngoc < 0 || p.nj.xu < 0 || p.nj.yen < 0) {
            try {
                SQLManager.executeUpdate("UPDATE player SET `status` = 'lock' WHERE `username`='" + p.username + "' LIMIT 1");
                p.session.disconnect();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        try {
            synchronized (self) {
                // TODO move to haru if not have battle
                // Chan chuyen map_back sang noi cho
                if (self.resetPlaceIfInBattle(p)) {
                    return;
                }

                if (!self.getUsers().contains(p)) {
                    self.addUser(p);
                }

                p.nj.setPlace(self);
                p.nj.mobAtk = -1;
                p.nj.eff5buff = System.currentTimeMillis() + 5000L;

                if (self.map.cave != null) {
                    self.map.cave.ninjas.add(p.nj);
                }
                if (self.map.tta != null) {
                    self.map.tta.ninjas.add(p.nj);
                }
                if (self.map.timeMap != -1L) {
                    if (self.map.tta != null) {
                        p.setTimeMap((int) (self.map.tta.time - System.currentTimeMillis()) / 1000);
                    } else if (self.map.dun != null) {
                        p.setTimeMap((int) (self.map.dun.time - System.currentTimeMillis()) / 1000);
                    } else {
                        p.setTimeMap((int) (self.map.cave.time - System.currentTimeMillis()) / 1000);
                    }
                }
                if (self.map.id == 112) {
                    for (Mob mob : self._mobs) {
                        if (mob.templates.id == 106 && self.map.tta.lvTTA == 0) {
                            mob.isRefresh = false;
                            mob.setTimeRefresh(-1);
                            mob.status = 5;
                        } else if (mob.templates.id == 107 && self.map.tta.lvTTA == 1) {
                            mob.isRefresh = false;
                            mob.setTimeRefresh(-1);
                            mob.status = 5;
                        } else if (mob.templates.id == 112 && self.map.tta.lvTTA == 2) {
                            mob.isRefresh = false;
                            mob.setTimeRefresh(-1);
                            mob.status = 5;
                        }
                    }
                }

                sendMapInfo(p, self);

                for (User user : self.getUsers()) {
                    if (user.id != p.id) {
                        self.sendCharInfo(user, p);
                        self.sendCoat(user.nj.get(), p);
                        self.sendGlove(user.nj.get(), p);
                    }
                    if (!user.nj.isNhanban && user.nj.clone != null && user.nj.clone.isIslive()) {
                        Service.sendclonechar(user, p);
                    }
                    MessageSubCommand.sendHP(user.nj, self.getUsers());
                    self.sendMounts(user.nj.get(), p);
                }

                for (int k = 0; k < self.getUsers().size(); ++k) {
                    final User recv = self.getUsers().get(k);
                    if (recv.id != p.id) {
                        self.sendCharInfo(p, recv);
                        self.sendCoat(p.nj.get(), recv);
                        self.sendGlove(p.nj.get(), recv);
                        if (!p.nj.isNhanban && p.nj.clone != null
                                && p.nj.clone.isIslive()) {
                            Service.sendclonechar(p, recv);
                        }
                    }
                    self.sendMounts(p.nj.get(), recv);
                }

                if (p.nj.getLevel() == 1 && !p.nhanQua && p.isNewAccount == false) {
                    self.nhanQuaNewAccount(p);
                }

                val u = Arrays.asList(p);
                for (BuNhin buNhin : self.buNhins) {
                    MessageSubCommand.sendBuNhin(buNhin, u);
                }
                try {
                    if (util.compare_Day(Date.from(Instant.now()), p.nj.newlogin)) {
                        switch (util.getDay(p.nj.newlogin)) {
                            case "Saturday": {
                                Manager.expX = 2;
                                break;
                            }
                            case "Sunday": {
                                Manager.expX = 2;
                                break;
                            }
                            default: {
                                Manager.expX = 1;
                                break;
                            }
                        }
                        p.nj.pointCave = 0;
                        p.nj.nCave = 1;
                        p.nj.useCave = 1;
                        p.setClanTerritoryId(-1);
                        p.nj.ddClan = false;
                        p.nj.nvhnCount = 0;
                        p.nj.taThuCount = 2;
                        p.nj.taskDanhVong = new int[]{-1, -1, -1, 0, -1, 20};
                        p.nj.isTaskDanhVong = 0;
                        p.nj.countTaskDanhVong = 20;
                        p.nj.useDanhVongPhu = 6;
                        p.nj.Time2H = 0;
                        p.nj.isGift2H = false;
                        p.nj.limitShinwa = 10;
//                        for (Option op : p.nj.get().ItemMounts[4].option) {
//                            if (op.id == 66) {
//                                if (op != null && op.param > 0) {
//                                    op.param -= 100;
//                                    if (op.param < 0) {
//                                        op.param = 0;
//                                    }
//                                }
//                            }
//                        }
//                        try {
//                            p.loadMounts();
//                        } catch (IOException ex) {
//                        }
                        p.setClanTerritoryData(null);
                        if (p.nj.battleData != null) {
                            p.nj.battleData.setPoint(0);
                            p.nj.battleData.setPhe(PK_NORMAL);
                        } else {
                            p.nj.battleData = new BattleData();
                        }
                        p.nj.useTathu = 2;
                        p.nj.newlogin = util.dateFormatDay.parse(Date.from(Instant.now()).toString());
                    }
                } catch (Exception e) {
                    try {
                        p.nj.pointCave = 0;
                        p.nj.nCave = 1;
                        p.nj.useCave = 1;
                        p.nj.ddClan = false;
                        p.nj.nvhnCount = 0;
                        p.nj.taThuCount = 2;
                        p.nj.taskDanhVong = new int[]{-1, -1, -1, 0, -1, 20};
                        p.nj.isTaskDanhVong = 0;
                        p.nj.countTaskDanhVong = 20;
                        p.nj.useDanhVongPhu = 6;
                        p.nj.Time2H = 0;
                        p.nj.isGift2H = false;
                        p.nj.limitShinwa = 10;
//                        KageTournament.gi().restoreNinjaTournament(p.nj);
//                        GeninTournament.gi().restoreNinjaTournament(p.nj);
                        p.setClanTerritoryId(-1);
                        if (p.nj.battleData != null) {
                            p.nj.battleData.setPoint(0);
                            p.nj.battleData.setPhe(PK_NORMAL);
                        } else {
                            p.nj.battleData = new BattleData();
                        }
                        p.nj.useTathu = 2;
                        p.nj.newlogin = Date.from(Instant.now());
                    } catch (Exception ex) {
                        System.out.println("Parse day error");
                        ex.printStackTrace();
                    }
                }

                Service.CharViewInfo(p, false);
                TaskHandle.inMap(p.nj);
                /*   if (p.nj.isTaskDanhVong == 1) {
                    p.nj.nhiemvuDV = true;
                } */
            }
        } catch (Exception e) {
            self.gotoHaruna(p);
        }
    }

    public static void sendMapInfo(@Nullable final User p, @NotNull Place self) throws IOException {
        Message m = new Message(57);
        m.writer().flush();
        p.sendMessage(m);
        m.cleanup();

        if (p.nj.getPlace().map.template.id == 139) {
            p.SendTree(p, "res/msg/" + p.session.zoomLevel + "/vgocay139");
        } else if (p.nj.getPlace().map.template.id == 140) {
            p.SendTree(p, "res/msg/" + p.session.zoomLevel + "/vgocay140");
            //  m.writer().writeByte(-109);
        } else if (p.nj.getPlace().map.template.id == 141) {
            p.SendTree(p, "res/msg/" + p.session.zoomLevel + "/vgocay141");
            //  m.writer().writeByte(-109);
        } else if (p.nj.getPlace().map.template.id == 142) {
            p.SendTree(p, "res/msg/" + p.session.zoomLevel + "/vgocay142");
            //  m.writer().writeByte(-109);
        } else if (p.nj.getPlace().map.template.id == 143) {
            p.SendTree(p, "res/msg/" + p.session.zoomLevel + "/vgocay143");
            //  m.writer().writeByte(-109);
        } else if (p.nj.getPlace().map.template.id == 144) {
            p.SendTree(p, "res/msg/" + p.session.zoomLevel + "/vgocay144");
            //  m.writer().writeByte(-109);
        } else if (p.nj.getPlace().map.template.id == 145) {
            p.SendTree(p, "res/msg/" + p.session.zoomLevel + "/vgocay145");
            //  m.writer().writeByte(-109);
        } else if (p.nj.getPlace().map.template.id == 146) {
            p.SendTree(p, "res/msg/" + p.session.zoomLevel + "/vgocay146");
            //  m.writer().writeByte(-109);
        } else if (p.nj.getPlace().map.template.id == 147) {
            p.SendTree(p, "res/msg/" + p.session.zoomLevel + "/vgocay147");
            //  m.writer().writeByte(-109);
        } else if (p.nj.getPlace().map.template.id == 148) {
            p.SendTree(p, "res/msg/" + p.session.zoomLevel + "/vgocay148");
            //   m.writer().writeByte(-109);
        } else if (p.nj.getPlace().map.template.id == 138 || p.nj.getPlace().map.template.id == 1 || p.nj.getPlace().map.template.id == 27 || p.nj.getPlace().map.template.id == 72 || p.nj.getPlace().map.template.id == 10 || p.nj.getPlace().map.template.id == 17 || p.nj.getPlace().map.template.id == 22 || p.nj.getPlace().map.template.id == 32 || p.nj.getPlace().map.template.id == 38 || p.nj.getPlace().map.template.id == 43 || p.nj.getPlace().map.template.id == 48) {
            p.SendTree(p, "res/msg/" + p.session.zoomLevel + "/vgocay");
        }

        m = new Message(-18);
        m.writer().writeByte(self.map.id);
        m.writer().writeByte(self.map.template.tileID);
        m.writer().writeByte(self.map.template.bgID);
        m.writer().writeByte(self.map.template.typeMap);
        m.writer().writeUTF(self.map.template.name);
        m.writer().writeByte(self.id);
        m.writer().writeShort(p.nj.get().x);
        m.writer().writeShort(p.nj.get().y);
        m.writer().writeByte(self.map.template.vgo.length);
        for (byte i = 0; i < self.map.template.vgo.length; ++i) {
            m.writer().writeShort(self.map.template.vgo[i].minX);
            m.writer().writeShort(self.map.template.vgo[i].minY);
            m.writer().writeShort(self.map.template.vgo[i].maxX);
            m.writer().writeShort(self.map.template.vgo[i].maxY);
        }
        m.writer().writeByte(self.getMobs().size());
        for (short j = 0; j < self.getMobs().size(); ++j) {
            final Mob mob = self.getMobs().get(j);
            if (mob != null) {
                if (mob.isCallMob()) {
                    if (mob.ninjaId == p.nj.id) {
                        m.writer().writeBoolean(mob.isDisable);
                        m.writer().writeBoolean(mob.isDontMove);
                        m.writer().writeBoolean(mob.isFire);
                        m.writer().writeBoolean(mob.isIce);
                        m.writer().writeBoolean(mob.isWind);
                        m.writer().writeByte(mob.templates.id);
                        m.writer().writeByte(mob.sys);
                        m.writer().writeInt(mob.hp);
                        m.writer().writeByte(mob.level);
                        m.writer().writeInt(mob.hpmax);
                        m.writer().writeShort(mob.x);
                        m.writer().writeShort(mob.y);
                        m.writer().writeByte(mob.status);
                        m.writer().writeByte(mob.lvboss);
                        m.writer().writeBoolean(mob.isIsboss());
                    } else {
                        m.writer().writeBoolean(mob.isDisable);
                        m.writer().writeBoolean(mob.isDontMove);
                        m.writer().writeBoolean(mob.isFire);
                        m.writer().writeBoolean(mob.isIce);
                        m.writer().writeBoolean(mob.isWind);
                        m.writer().writeByte(mob.templates.id);
                        m.writer().writeByte(mob.sys);
                        m.writer().writeInt(mob.hp);
                        m.writer().writeByte(mob.level);
                        m.writer().writeInt(mob.hpmax);
                        m.writer().writeShort(mob.x);
                        m.writer().writeShort(mob.y);
                        m.writer().writeByte(0);
                        m.writer().writeByte(mob.lvboss);
                        m.writer().writeBoolean(mob.isIsboss());
                    }
                } else {
                    m.writer().writeBoolean(mob.isDisable);
                    m.writer().writeBoolean(mob.isDontMove);
                    m.writer().writeBoolean(mob.isFire);
                    m.writer().writeBoolean(mob.isIce);
                    m.writer().writeBoolean(mob.isWind);
                    m.writer().writeByte(mob.templates.id);
                    m.writer().writeByte(mob.sys);
                    m.writer().writeInt(mob.hp);
                    m.writer().writeByte(mob.level);
                    m.writer().writeInt(mob.hpmax);
                    m.writer().writeShort(mob.x);
                    m.writer().writeShort(mob.y);
                    m.writer().writeByte(mob.status);
                    m.writer().writeByte(mob.lvboss);
                    m.writer().writeBoolean(mob.isIsboss());
                }
            }
        }
        m.writer().writeByte(self.buNhins.size());
        for (int k = self.buNhins.size() - 1; k >= 0; k--) {
            m.writer().writeUTF(self.buNhins.get(k).name);
            m.writer().writeShort(self.buNhins.get(k).x);
            m.writer().writeShort(self.buNhins.get(k).y);
        }
        if (self.map.id == 33) {
            val hasJainTask = p.nj.getTaskId() == 17 && p.nj.getTaskIndex() == 1;
            m.writer().writeByte(self.map.template.npc.length - (hasJainTask ? 0 : 1));
            for (final Npc npc : self.map.template.npc) {

                if ((npc.id == 17
                        && (!hasJainTask
                        || hasJainTask && self
                                .getUsers()
                                .stream()
                                .anyMatch(u -> u != null
                                && u.nj != null
                                && "Jaian".equals(u.nj.name))))) {
                    continue;
                }
                m.writer().writeByte(npc.type);
                m.writer().writeShort(npc.x);
                m.writer().writeShort(npc.y);
                m.writer().writeByte(npc.id);
            }
        } else {
            if (self.getUsers().stream().anyMatch(u -> u != null && u.nj != null && "Lồng đèn".equals(u.nj.name))) {
                m.writer().writeByte(self.map.template.npc.length - 1);
                for (final Npc npc : self.map.template.npc) {
                    if (npc.id != 41) {
                        m.writer().writeByte(npc.type);
                        m.writer().writeShort(npc.x);
                        m.writer().writeShort(npc.y);
                        m.writer().writeByte(npc.id);
                    }
                }
            } else {
                m.writer().writeByte(self.map.template.npc.length);
                for (final Npc npc : self.map.template.npc) {
                    m.writer().writeByte(npc.type);
                    m.writer().writeShort(npc.x);
                    m.writer().writeShort(npc.y);
                    m.writer().writeByte(npc.id);
                }
            }
        }

        m.writer().writeByte(self._itemMap.size());
        for (int k = 0; k < self._itemMap.size(); ++k) {
            final ItemMap im = self._itemMap.get(k);
            m.writer().writeShort(im.itemMapId);
            m.writer().writeShort(im.item.id);
            m.writer().writeShort(im.x);
            m.writer().writeShort(im.y);
        }
        m.writer().writeUTF(self.map.template.name);
        m.writer().writeByte(self.map.template.itemtree.length);
        for (int i = 0; i < self.map.template.itemtree.length; i++) {
            m.writer().writeByte(self.map.template.itemtree[i].num3);
            m.writer().writeByte(self.map.template.itemtree[i].k2);
        }
        m.writer().writeByte(0);
        m.writer().flush();
        p.sendMessage(m);
        m.cleanup();
        //lang tone
        if (self.map.id == 22) {
            Service.sendEffectAuto(p, (byte) 14, 452, 216, (byte) -1, (short) -1);
            Service.sendEffectAuto(p, (byte) 14, 1400, 240, (byte) -1, (short) -1);
            Service.sendEffectAuto(p, (byte) 14, 1999, 288, (byte) -1, (short) -1);
            Service.sendEffectAuto(p, (byte) 15, 1597, 288, (byte) -1, (short) -1);
            Service.sendEffectAuto(p, (byte) 15, 941, 96, (byte) -1, (short) -1);
            Service.sendEffectAuto(p, (byte) 15, 2333, 192, (byte) -1, (short) -1);
        }
        if (self.map.id == 1) {
            Service.sendEffectAuto(p, (byte) 15, 238, 408, (byte) -1, (short) -1);
            Service.sendEffectAuto(p, (byte) 15, 1193, 168, (byte) -1, (short) -1);
            Service.sendEffectAuto(p, (byte) 15, 690, 408, (byte) -1, (short) -1);
            Service.sendEffectAuto(p, (byte) 15, 1820, 360, (byte) -1, (short) -1);
        }
        if (self.map.id == 27) {
            Service.sendEffectAuto(p, (byte) 15, 326, 144, (byte) -1, (short) -1);
            Service.sendEffectAuto(p, (byte) 14, 430, 408, (byte) -1, (short) -1);
            Service.sendEffectAuto(p, (byte) 14, 1689, 360, (byte) -1, (short) -1);
            Service.sendEffectAuto(p, (byte) 15, 139, 408, (byte) -1, (short) -1);
            Service.sendEffectAuto(p, (byte) 15, 2646, 384, (byte) -1, (short) -1);
        }
        if (self.map.id == 72) {
            Service.sendEffectAuto(p, (byte) 15, 1427, 552, (byte) -1, (short) -1);
            Service.sendEffectAuto(p, (byte) 15, 1856, 672, (byte) -1, (short) -1);
            Service.sendEffectAuto(p, (byte) 15, 605, 672, (byte) -1, (short) -1);
        }
    }

    public void Enter(@Nullable final User p) throws IOException {
        Enter(p, this);
    }

    private void addUser(@NotNull User p) {
        this._users.add(p);
    }

    private void nhanQuaNewAccount(@Nullable User p) throws IOException {
        if (p == null) {
            return;
        }
        p.isNewAccount = true;
        //  p.updateExp(Level.getMaxExp(1), false);
        //  p.nj.setLevel(1);
        p.upluongMessage(50_000L);
        p.nj.upxuMessage(1_000_000L);
        p.nj.upyenMessage(1_000_000_000L);
        //       p.nj.ItemBody[1] = itemDefault(194);
        p.nhanQua = true;
    }

    protected boolean resetPlaceIfInBattle(@Nullable final User p) throws IOException {
        if (p == null) {
            return true;
        }

        if (p.nj.getMapid() == 111 || p.nj.getMapid() == 110) {
            if (p.nj.isHuman) {
                if (map.dun == null && p.nj.getClanBattle() == null) {
                    gotoHaruna(p);
                    return true;
                }
            }
        } else if (map.isGtcMap() && p.nj.getClanBattle() == null) {
            gotoHaruna(p);
            return true;
        } else if (this.map != null && battle != null && !map.isGtcMap()) {
            if (Server.getInstance().globalBattle.getState() == INITIAL_STATE
                    || (p.nj.getTypepk() != PK_TRANG && p.nj.getTypepk() != PK_DEN)) {
                gotoHaruna(p);
                return true;
            }
        } else if (this.map.isLdgtMap()) {
            if (p.getClanTerritoryData() == null) {
                gotoHaruna(p);
                return true;
            } else {
                p.getClanTerritoryData().getClanTerritory().setTime(p.nj);
            }
        }
        return false;
    }

    public void gotoHaruna(final @Nullable User p) throws IOException {
        if (p == null) {
            return;
        }

        p.nj.setMapid(27);
        val map = Server.getInstance().getMapById(27);
        p.nj.x = map.template.x0;
        p.nj.y = map.template.y0;
        try {
            Service.batDauTinhGio(p, 0);
            p.nj.getPlace().leave(p);
            p.nj.get().isDie = false;
            p.nj.get().upHP(p.nj.get().getMaxHP());
            p.nj.get().upMP(p.nj.get().getMaxMP());
        } catch (Exception e) {
        } finally {
            map.getFreeArea().Enter(p);
        }
    }

    public void gotoOz(final @Nullable User p) throws IOException {
        if (p == null) {
            return;
        }

        p.nj.setMapid(72);
        val map = Server.getInstance().getMapById(72);
        p.nj.x = map.template.x0;
        p.nj.y = map.template.y0;
        try {
            Service.batDauTinhGio(p, 0);
            p.nj.getPlace().leave(p);
            p.nj.get().isDie = false;
            p.nj.get().upHP(p.nj.get().getMaxHP());
            p.nj.get().upMP(p.nj.get().getMaxMP());
        } catch (Exception e) {
        } finally {
            map.getFreeArea().Enter(p);
        }
    }

    public void changeMap(@Nullable final User p) throws IOException {
        if (p == null) {
            return;
        }
        Ninja _ninja = p.nj;
        for (Mob mob : this._mobs) {
            if (mob.templates.id == 113 && map.tta.timesRefeshPillar > 0 && map.tta.timeWait < System.currentTimeMillis()) {
                if (mob.isDie == true) {
                    map.tta.timesRefeshPillar--;
                    mob.status = 5;
                    mob.isRefresh = true;
                    mob.setTimeRefresh(10);
                }
                break;
            }
        }
        if (p.nj.addCuuSat || p.nj.isCuuSat) {
            p.removeCuuSat(p.nj);
        }
        for (byte i = 0; i < this.map.template.vgo.length; ++i) {
            final Vgo vg = this.map.template.vgo[i];
            int mapid;
            if (this.map.id == 138) {
                mapid = (new int[]{134, 135, 136, 137})[util.nextInt(4)];
            } else {
                mapid = vg.mapid;
            }
            if (vg.mapid == 112) {
                _ninja.x = 445;
                if (_ninja.getPlace().map.tta.map[1].area[0]._users.size() > 0) {
                    restPoint(_ninja);
                    GameCanvas.startOKDlg(_ninja.p.session, "Đã có thành viên tham gia chiến đấu, không thể di chuyển.");
                    return;
                }
                if (map.tta.timeWait > System.currentTimeMillis()) {
                    restPoint(_ninja);
                    GameCanvas.startOKDlg(_ninja.p.session, "Chưa đến thời gian chiến đấu. Vui lòng đợi...");
                    return;
                }
            }
            if (p.nj.get().x + 100 >= vg.minX && p.nj.get().x <= vg.maxX + 100 && p.nj.get().y + 100 >= vg.minY && p.nj.get().y <= vg.maxY + 100) {
                Map ma = Manager.getMapid(mapid);
                if (mapid == 22 || mapid == 1 || mapid == 72 || mapid == 27) {
                    p.nj.changeTypePk((short) 0);
                }
                if (this.map.cave != null) {
                    for (byte j = 0; j < this.map.cave.map.length; ++j) {
                        if (this.map.cave.map[j].id == mapid) {
                            ma = this.map.cave.map[j];
                        }
                    }
                }
                if (this.map.tta != null) {
                    for (byte j = 0; j < this.map.tta.map.length; ++j) {
                        if (this.map.tta.map[j].id == mapid) {
                            ma = this.map.tta.map[j];
                        }
                    }
                }
                for (byte j = 0; j < ma.template.vgo.length; ++j) {
                    final Vgo vg2 = ma.template.vgo[j];
                    if (vg2.mapid == this.map.id) {
                        p.nj.get().x = vg2.goX;
                        p.nj.get().y = vg2.goY;
                    }
                }
                byte errornext = -1;
                for (byte n = 0; n < p.nj.get().ItemMounts.length; ++n) {
                    if (p.nj.get().ItemMounts[n] != null && p.nj.get().ItemMounts[n].isExpires && p.nj.get().ItemMounts[n].expires < System.currentTimeMillis()) {
                        errornext = 1;
                    }
                }
                if (TaskHandle.isLockChangeMap((short) vg.mapid, p.nj.getTaskId())) {
                    errornext = 11;
                }
                if (map.isLdgtMap()) {
                    if (p.getClanTerritoryData() == null || p.getClanTerritoryData().getClanTerritory() == null) {
                        errornext = 5;
                    } else {
                        Place place = p.getClanTerritoryData().getClanTerritory().openedMap.get(ma.id);
                        if (place == null) {
                            if (map.id != 89) {
                                errornext = 6;
                            } else {
                                if (vg.minX == 20) {
                                    place = p.getClanTerritoryData().getClanTerritory().openedMap.get(84);
                                    if (place.canEnter()) {
                                        p.nj.enterSamePlace(place, null);
                                        return;
                                    } else {
                                        errornext = 7;
                                    }
                                } else {
                                    errornext = 6;
                                }

                            }
                        } else {
                            if (place.canEnter) {
                                changeToPlace(p, vg, mapid, place);
                                return;
                            } else {
                                errornext = 7;
                            }
                        }
                    }
                } else if (map.isGtcMap()) {
                    if (p.nj.getClanBattle() != null) {
                        final java.util.Map<Byte, Place> openedMaps = p.nj.getClanBattle().openedMaps;
                        final Place place = openedMaps.get((byte) mapid);

                        if (p.nj.getPhe() == PK_TRANG && mapid == BAO_DANH_GT_HAC) {
                            errornext = 3;
                        } else if (p.nj.getPhe() == PK_DEN && mapid == BAO_DANH_GT_BACH) {
                            errornext = 3;
                        } else if (place != null) {
                            // TODO REMOVE COMMENT
                            if (p.nj.getClanBattle().getState() == WAITING_STATE) {
                                errornext = 9;
                            } else {
                                changeToPlace(p, vg, mapid, place);
                                return;
                            }
                        } else {
                            errornext = 8;
                        }
                    }
                } else {
                    if (map.tta != null) {
                        if (map.id == 112) {
                            if (this.getNumplayers() > 0) {
                                errornext = 1;
                            }
                        }
                    }
                    // INcave map_back
                    if (this.map.cave != null && this.map.getXHD() < 9
                            && this.map.cave.map.length > this.map.cave.level
                            && this.map.cave.map[this.map.cave.level].id < mapid) {
                        errornext = 2;
                    }
                    // Not in time global battle
                    if (battle != null) {
                        if ((p.nj.getPhe() == PK_TRANG && mapid == CAN_CU_DIA_HAC) || (p.nj.getPhe() == PK_DEN && mapid == CAN_CU_DIA_BACH)) {
                            errornext = 3;
                        }

                        if (battle.getState() != START_STATE) {
                            errornext = 4;
                        }
                    } else if (candyBattle != null) {
                        if (p.nj.getTypepk() == PK_DEN && mapid == CandyBattle.KEO_TRANG_ID
                                || p.nj.getTypepk() == PK_TRANG && mapid == CandyBattle.KEO_DEN_ID) {
                            errornext = 3;
                        }
                    }

                    if (mapid == 161) {
                        if (p.nj.delay161 < System.currentTimeMillis()) {
                            p.nj.delay161 = System.currentTimeMillis() + 600000L;
                            errornext = -1;
                        } else {
                            errornext = 10;
                        }
                    }

                    // Has party
                    if (errornext == -1) {
                        if (p.nj.party != null) {
                            for (byte k = 0; k < ma.area.length; ++k) {

                                if (ma.area[k].getArryListParty().contains(p.nj.party)) {
                                    if (this.map.id == 138) {
                                        leave(p);
                                        ma.area[k].EnterMap0(p.nj);
                                        return;
                                    } else {
                                        p.nj.setMapid(mapid);
                                        p.nj.x = vg.goX;
                                        p.nj.y = vg.goY;

                                        if (p.nj.clone != null) {
                                            p.nj.clone.x = p.nj.x;
                                            p.nj.clone.y = p.nj.y;
                                        }
                                        leave(p);
                                        ma.area[k].Enter(p);
                                        return;
                                    }
                                }
                            }
                        }

                        for (byte k = 0; k < ma.area.length; ++k) {
                            if (ma.area[k].getNumplayers() < ma.template.maxplayers) {
                                if (this.map.id == 138) {
                                    leave(p);
                                    ma.area[k].EnterMap0(p.nj);
                                } else {
                                    p.nj.setMapid(mapid);
                                    p.nj.x = vg.goX;
                                    p.nj.y = vg.goY;

                                    if (p.nj.clone != null) {
                                        p.nj.clone.x = p.nj.x;
                                        p.nj.clone.y = p.nj.y;
                                    }
                                    leave(p);
                                    ma.area[k].Enter(p);
                                }
                                return;
                            }
                            if (k == ma.area.length - 1) {
                                errornext = 0;
                            }
                        }
                    }
                }

                if (errornext != -1) {
                    if (errornext == 11) {
                        restPoint(p.nj);
                    } else {
                        leave(p);
                        this.Enter(p);
                    }
                }
                switch (errornext) {
                    case 0: {
                        p.session.sendMessageLog("Bản đồ quá tải.");
                        return;
                    }
                    case 1: {
                        p.session.sendMessageLog("Trang bị thú cưới đã hết hạn. Vui lòng tháo ra để di chuyển");
                        return;
                    }
                    case 2: {
                        p.session.sendMessageLog("Cửa " + ma.template.name + " vẫn chưa mở");
                        return;
                    }
                    case 3: {
                        p.session.sendMessageLog("Không phân sự miễn vào");
                        return;
                    }
                    case 4: {
                        p.session.sendMessageLog("Chiến trường chưa bắt đầu con không thể đi tiếp");
                        return;
                    }
                    case 5: {
                        p.session.sendMessageLog("Không thể đi tiếp");
                        return;
                    }
                    case 6: {
                        p.session.sendMessageLog(ma.template.name + " chưa được mở");
                        return;
                    }
                    case 7: {
                        p.session.sendMessageLog("Phải mở đủ các cửa mới có thể đi tiếp");
                        break;
                    }
                    case 8: {
                        p.session.sendMessageLog("Lỗi không xác định");
                        break;
                    }
                    case 9: {
                        p.session.sendMessageLog("Gia tộc chiến chưa bắt đầu không thể đi tiếp");
                        break;
                    }
                    case 10: {
                        p.sendYellowMessage("Chỉ có thể qua map này sau " + (p.nj.delay161 - System.currentTimeMillis()) / 1000 + " s nữa");
                        break;
                    }
                    case 11: {
                        GameCanvas.startOKDlg(_ninja.p.session, Text.get(0, 84));
                        break;
                    }
                }
            }
        }
    }

    private void changeToPlace(final @Nullable User p, @Nullable final Vgo vg, int mapid, @Nullable final Place place) throws IOException {
        if (p == null || vg == null || place == null) {
            return;
        }
        leave(p);
        p.nj.setMapid(mapid);
        p.nj.x = vg.goX;
        p.nj.y = vg.goY;
        place.Enter(p);
    }

    private void test(final Ninja nj, final Vgo vg) throws IOException {

        nj.x = vg.goX;

    }
    private short MOVE_LIMIT = 80;
    private short RESET_LIMIT = 90;

    protected boolean isaUpdie(final short x, final short y) {
        return this.map != null && (x < 0 || y < 0 || x > this.map.template.pxw || (!this.map.tileTypeAt((int) x, (int) y - 1, (int) 64) && (int) y >= this.map.template.pxh - this.map.template.size));
    }

    public void moveMessage(@Nullable Ninja nj, short x, short y) throws IOException {
        if (nj == null) {
            return;
        }

        if (nj.get().getEffId(18) != null) {
            return;
        }
        short dx = (short) Math.abs(nj.x - x);
        short dy = (short) Math.abs(nj.y - y);
        int speed = nj.speed() + 1;
        nj.x = x;
        nj.y = y;
        if (nj.ItemMounts[4] != null) {
            speed += 2;
        }
//        if ((dx > speed * 7 && dx > 50)) {
//            restPoint(nj);
//        }
        /*  if (!map.VDMQ() && (y < 0 || (y >= this.map.template.max_y))) {
            //   nj.x = (short) Math.abs(this.map.template.pxw - 35);
            nj.x = map.template.x0;
            nj.y = map.template.y0;
            nj.get().upDie();
            return;
        }
        if ((map.id == 139 && y > 936) || (map.id == 140 && y > 1010) || (map.id == 141 && y > 936) || (map.id == 142 && y > 674) || (map.id == 143 && y > 780) || (map.id == 144 && y > 546) || (map.id == 145 && y > 570) || (map.id == 146 && y > 690) || (map.id == 147 && y > 762) || (map.id == 148 && y > 690)) {
            nj.x = map.template.x0;
            nj.y = map.template.y0;
            nj.get().upDie();
            return;
        } */
        if (nj.dunId != -1) {
            for (int i = 0; i < Dun.duns.size(); i++) {
                Dun dun = Dun.duns.get(i);
                if (dun != null) {
                    for (Ninja n : dun.viewer) {
                        if (nj == n && (n.y > 336 || nj.y < 336)) {
                            nj.y = 336;
                        }
                    }
                }
            }
        }
        // Giới hạn speed 
        if (dx > nj.speed() * 18 && dx > MOVE_LIMIT && nj.hasBattle()) {
            restPoint(nj);
        } else {
            if (nj.isNhanban) {
                nj.clone.x = x;
                nj.clone.y = y;
            }
            this.move(nj.get().id, nj.get().x, nj.get().y);
        }

        // TODO Reset for battle
        if (nj.hasBattle()) {
            if (nj.y > 264 || (264 - nj.y) >= RESET_LIMIT) {
                nj.y = 264;
                restPoint(nj);
            }
        } else if (nj.isBattleViewer) {
            if (nj.y < 300 || nj.y > 336) {
                nj.y = 336;
                restPoint(nj);
            }
        }
        if (nj.clone != null && nj.clone.isIslive() && (Math.abs(nj.x - nj.clone.x) > 80 || Math.abs(nj.y - nj.clone.y) > 30)) {
            nj.clone.move((short) util.nextInt(nj.x - 35, nj.x + 35), nj.y);
        }
    }

    protected static void restPoint(@Nullable Ninja ninja) {
        if (ninja == null) {
            return;
        }

        Message msg = null;
        try {
            msg = new Message((byte) 52);
            msg.writer().writeShort(ninja.x);
            msg.writer().writeShort(ninja.y);
            ninja.p.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public void move(final int id, final short x, final short y) {
        try {
            final Message m = new Message(1);
            m.writer().writeInt(id);
            m.writer().writeShort(x);
            m.writer().writeShort(y);
            m.writer().flush();
            this.sendMessage(m);
            m.cleanup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeItemMapMessage(final short itemmapid) throws IOException {
        final Message m = new Message(-15);
        m.writer().writeShort(itemmapid);
        m.writer().flush();
        this.sendMessage(m);
        m.cleanup();
    }

    @SneakyThrows
    public void pickItem(@Nullable final User p, @Nullable Message m) throws IOException {
        synchronized (this._itemMap) {
            if (p == null || m == null) {
                return;
            }

            if (m.reader().available() == 0) {
                return;
            }
            final short itemmapid = m.reader().readShort();
            m.cleanup();
            for (short i = 0; i < this._itemMap.size(); ++i) {
                if (this._itemMap.get(i).itemMapId == itemmapid) {
                    final ItemMap itemMap = this._itemMap.get(i);
                    if (!itemMap.visible) {
                        return;
                    }
                    final Item item = itemMap.item;
                    final ItemData data = ItemDataId(item.id);

                    if (itemMap.master != -1 && itemMap.master != p.nj.id) {
                        p.sendYellowMessage("Vật phẩm của người khác.");
                        return;
                    }
                    if (Math.abs(itemMap.x - p.nj.get().x) > 50 || Math.abs(itemMap.y - p.nj.get().y) > 30) {
                        p.sendYellowMessage("Khoảng cách quá xa.");
                        return;
                    }

                    val ninja = p.nj;

                    if (data.type == 19 || p.nj.getAvailableBag() > 0 || (p.nj.getIndexBagid(item.id, item.isLock()) != -1 && data.isUpToUp)) {
                        boolean isTaskItem = TaskHandle.itemPick(ninja, item.getData().id);

                        if (isTaskItem) {
                            itemMap.item.setLock(true);
                            TaskTemplate task = null;
                            if (taskTemplates.length > ninja.getTaskId()) {
                                task = taskTemplates[ninja.getTaskId()];
                            }
                            boolean isShowWaiting = itemMap != null && task != null && itemMap.item.id
                                    == (task.getItemsPick() != null && task.getItemsPick().length > ninja.getTaskIndex() ? task.getItemsPick()[ninja.getTaskIndex()] : -5)
                                    && ninja.getTaskId() != 31
                                    && item.id != 238
                                    && item.id != 349
                                    && item.id != 350
                                    && ninja.getTaskId() != 14
                                    && ninja.getTaskIndex() != 2
                                    && ninja.getTaskId() != 18
                                    && ninja.getTaskId() != 22
                                    && ninja.getTaskId() != 23;
                            if (isShowWaiting) {
                                Service.showWait("Nhặt Vật phẩm", ninja);
                                Thread.sleep(3000L);
                                Service.endWait(ninja);
                            }

                            if (ninja.getAvailableBag() > 0) {
                                ninja.upMainTask();
                                if (itemMap.item.id == 238) {
                                    if (util.percent(100, 50)) {
                                        p.sendYellowMessage("Bạn đã bị dơi lửa đốt");
                                        p.nj.get().upHP(-1000);
                                    }
                                    itemMap.item.id++;
                                }
                                removeItemMap(p, i, itemMap);
                                if (ninja.party != null) {
                                    short k;
                                    for (k = 0; k < this.getNumplayers(); k = (short) (k + 1)) {
                                        Ninja player = this.getUsers().get(k).nj;

                                        if (player != null && player.p != null && player.party != null && player.id != ninja.id
                                                && player.party.id == ninja.party.id && player.getTaskId() == ninja.getTaskId()
                                                && player.getTaskIndex() == ninja.getTaskIndex() && (player.getAvailableBag() != -1)) {
                                            player.upMainTask();
                                            val itemClone = item.clone();
                                            if (itemClone.id == 238) {
                                                itemClone.id++;
                                            }
                                            itemClone.setLock(true);
                                            player.addItemBag(item.getData().isUpToUp, itemClone);
                                        }
                                    }
                                }
                            }
                        } else {
                            removeItemMap(p, i, itemMap);
                        }

                        break;
                    } else {
                        p.session.sendMessageLog("Hành trang không đủ chỗ trống.");
                    }
                }
            }
        }
    }

    private void removeItemMap(final @Nullable User p, short index, final @Nullable ItemMap itemmap) throws IOException {
        if (p == null || itemmap == null) {
            return;
        }

        val item = itemmap.item;
        if (itemmap.removedelay != -1) {
            this._itemMap.remove(index);
        } else {
            this._itemMap.get(index).setVisible(false);
            this._itemMap.get(index).nextTimeRefresh = System.currentTimeMillis() + 2000L;
        }

        Message m;
        m = new Message(-13);
        m.writer().writeShort(itemmap.itemMapId);
        m.writer().writeInt(p.nj.get().id);
        m.writer().flush();
        this.sendMyMessage(p, m);
        m.cleanup();
        m = new Message(-14);
        m.writer().writeShort(itemmap.itemMapId);
        if (ItemDataId(item.id).type == 19) {
            p.nj.upyen(item.quantity);
            m.writer().writeShort(item.quantity);
        }
        m.writer().flush();
        p.sendMessage(m);
        m.cleanup();

        if (ItemDataId(item.id).type != 19) {
            if (itemmap.item.id == 238) {
                itemmap.item.id++;
            }
            p.nj.addItemBag(true, itemmap.item);
            return;
        }
    }

    /*public void leaveItemBackground(@Nullable final User p, byte index) throws IOException {
        if (p == null) return;
        synchronized (this._itemMap) {
            Message m = null;
            final Item itembag = p.nj.getIndexBag(index);
            if (itembag == null || itembag.isLock()) {
                return;
            }
            if (this._itemMap.size() > 100) {
                this.removeItemMapMessage(this._itemMap.remove(0).itemMapId);
            }
            final short itemmapid = this.getItemMapNotId();
            final ItemMap item = new ItemMap();
            item.x = p.nj.get().x;
            item.y = p.nj.get().y;
            item.itemMapId = itemmapid;
            item.item = itembag;
            this._itemMap.add(item);
            p.nj.ItemBag[index] = null;
            m = new Message(-6);
            m.writer().writeInt(p.nj.get().id);
            m.writer().writeShort(item.itemMapId);
            m.writer().writeShort(item.item.id);
            m.writer().writeShort(item.x);
            m.writer().writeShort(item.y);
            m.writer().flush();
            this.sendMyMessage(p, m);
            m.cleanup();
            m = new Message(-12);
            m.writer().writeByte(index);
            m.writer().writeShort(item.itemMapId);
            m.writer().writeShort(item.x);
            m.writer().writeShort(item.y);
            m.writer().flush();
            p.sendMessage(m);
            m.cleanup();
        }
    }*/
    public void leaveItemBackground(@Nullable final User p, byte index) throws IOException {
        if (p == null) {
            return;
        }
        
        synchronized (this._itemMap) {
            Message m = null;
            final Item itembag = p.nj.getIndexBag(index);
            if (itembag == null || itembag.isLock()) {
                return;
            }
            if (this._itemMap.size() > 100) {
                this.removeItemMapMessage(this._itemMap.remove(0).itemMapId);
            }
            short itemmapid = this.getItemMapNotId();
            ItemMap item = new ItemMap();
            item.x = p.nj.get().x;
            item.y = p.nj.get().y;
            item.itemMapId = itemmapid;
            item.item = itembag;
            p.nj.ItemBag[index] = null;
            this._itemMap.add(item);
            m = new Message(-6);
            m.writer().writeInt(p.nj.get().id);
            m.writer().writeShort(item.itemMapId);
            m.writer().writeShort(item.item.id);
            m.writer().writeShort(item.x);
            m.writer().writeShort(item.y);
            m.writer().flush();
            this.sendMyMessage(p, m);
            m.cleanup();
            m = new Message(-12);
            m.writer().writeByte(index);
            m.writer().writeShort(item.itemMapId);
            m.writer().writeShort(item.x);
            m.writer().writeShort(item.y);
            m.writer().flush();
            p.sendMessage(m);
            m.cleanup();
        }
    }

    private boolean killedTa = false;

    public void refreshMobs() {
        synchronized (this) {
            for (Mob mob : this.getMobs()) {
                this.refreshMob(mob.id, true);
            }
        }
    }

    public void refreshMob(final int mobid) {
        this.refreshMob(mobid, false);
    }

    public void refreshMob(final int mobid, boolean force) {
        try {
            synchronized (this) {
                if (!force) {
                    if (map.id == 78 || map.id == 74) {
                        return;
                    }
                }

                final Mob mob = this.getMob(mobid);
                if (mob == null) {
                    return;
                }

                mob.ClearFight();
                mob.sys = (byte) util.nextInt(1, 3);
                if (this.map.cave == null && mob.lvboss != 3 && !mob.isIsboss() && map.id != 74 && map.id != 78 && map.id != 113) {
                    if (mob.lvboss > 0) {
                        mob.lvboss = 0;
                    }
                    if (!this.map.isLdgtMap() && !map.isTTAMap()) {
                        if (mob.level >= 10 && 2 > util.nextInt(100) && this.numTA < 10 && this.candyBattle == null) { // đây là chỉnh tỉ lệ ta tl
                            mob.lvboss = 1;
                        } else if (mob.level >= 10 && 2 > util.nextInt(75) && this.numTL < 1 && this.candyBattle == null) {
                            mob.lvboss = 2;
                        }
                    } else {
                        if (mob.templates.id != 81
                                && this.checkCleanMob(mob.templates.id)) {
                            mob.lvboss = 1;
                        }
                    }
                }

                if (this.map.cave != null && this.map.cave.finsh > 0 && this.map.getXHD() == 6) {
                    final int hpup = mob.templates.hp * (10 * this.map.cave.finsh + 100) / 100;
                    final int n = hpup;
                    mob.hpmax = n;
                    mob.hp = n;
                } else {
                    if (map.id == 112) {
                        if (mob.id == 112) {
                            mob.hpmax = 5_550_500 * 2;
                            mob.hp = 5_550_500 * 2;
                        } else {
                            mob.hpmax = 112458 * 2;
                            mob.hp = 112458 * 2;
                        }
                    } else if (map.id == 113) {
                        mob.hpmax = 13_600_000 * 2;
                        mob.hp = 13_600_000 * 2;
                    } else {
                        final int hp = mob.templates.hp;
                        mob.hpmax = hp;
                        mob.hp = hp;
                    }
                }
                if (mob.lvboss == 3) {
                    final int n2 = mob.hpmax * 200;
                    mob.hpmax = n2;
                    mob.hp = n2;
                } else if (mob.lvboss == 2) {
                    numTL++;
                    final int n3 = mob.hpmax * 100;
                    mob.hpmax = n3;
                    mob.hp = n3;
                } else if (mob.lvboss == 1) {
                    numTA++;
                    final int n4 = mob.hpmax * 10;
                    mob.hpmax = n4;
                    mob.hp = n4;
                }
                mob.status = 5;
                mob.isDie = false;
                mob.setTimeRefresh(0L);

                final Message m = new Message(-5);
                m.writer().writeByte(mob.id);
                m.writer().writeByte(mob.sys);
                m.writer().writeByte(mob.lvboss);
                m.writer().writeInt(mob.hpmax);
                m.writer().flush();
                this.sendMessage(m);
                m.cleanup();
            }
        } catch (IOException e) {

        }
    }

    private void attackMob(final int dame, final int mobid, final boolean fatal) throws IOException {
        final Message m = new Message(-1);
        m.writer().writeByte(mobid);
        final Mob mob = this.getMob(mobid);
        if (mob == null) {
            return;
        }
        m.writer().writeInt(mob.hp);
        m.writer().writeInt(dame);
        m.writer().writeBoolean(fatal);
        m.writer().writeByte(mob.lvboss);
        m.writer().writeInt(mob.hpmax);
        m.writer().flush();
        this.sendMessage(m);
        m.cleanup();
    }

    private void MobStartDie(final int dame, final int mobid, final boolean fatal) throws IOException {
        final Mob mob = this.getMob(mobid);
        if (mob == null) {
            return;
        }
        final Message m = new Message(-4);
        m.writer().writeByte(mobid);
        m.writer().writeInt(dame);
        m.writer().writeBoolean(fatal);
        m.writer().flush();
        this.sendMessage(m);
        m.cleanup();
    }

    @SneakyThrows
    public void sendXYPlayerWithEffect(@Nullable final User p, short lastX, short lastY) {
        if (p == null) {
            return;
        }
        val m = new Message(-137);
        m.writer().writeByte(-1);
        m.writer().writeInt(p.nj.get().id);
        m.writer().writeShort(lastX);
        m.writer().writeShort(lastY);
        sendMessage(m);
        m.cleanup();
    }

    public void sendXYPlayer(@Nullable final User p) throws IOException {
        if (p == null) {
            return;
        }
        final Message m = new Message(52);
        m.writer().writeShort(p.nj.get().x);
        m.writer().writeShort(p.nj.get().y);
        m.writer().flush();
        p.sendMessage(m);
        m.cleanup();
    }

    private void setXYPlayers(final short x, final short y, @Nullable final User p1, @Nullable final User p2) throws IOException {
        if (p1 == null || p2 == null) {
            return;
        }

        final Body value = p1.nj.get();
        p2.nj.get().x = x;
        value.x = x;
        final Body value2 = p1.nj.get();
        p2.nj.get().y = y;
        value2.y = y;
        final Message m = new Message(64);
        m.writer().writeInt(p1.nj.get().id);
        m.writer().writeShort(p1.nj.get().x);
        m.writer().writeShort(p1.nj.get().y);
        m.writer().writeInt(p2.nj.get().id);
        m.writer().flush();
        this.sendMessage(m);
        m.cleanup();
    }

    public void removeMessage(final int id) {
        try {
            final Message m = new Message(2);
            m.writer().writeInt(id);
            m.writer().flush();
            this.sendMessage(m);
            m.cleanup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendCharInfo(@Nullable final User p, @Nullable final User revc) {
        if (p == null || revc == null) {
            return;
        }

        try {
            Message m = new Message(3);
            m.writer().writeInt(p.nj.get().id);
            m.writer().writeUTF(p.nj.clan.clanName);
            if (!p.nj.clan.clanName.isEmpty()) {
                m.writer().writeByte(p.nj.clan.typeclan);
            }
            m.writer().writeBoolean(false);
            m.writer().writeByte(p.nj.get().getTypepk());
            m.writer().writeByte(p.nj.get().nclass);
            m.writer().writeByte(p.nj.gender);
            m.writer().writeShort(p.nj.get().partHead());
            m.writer().writeUTF(p.nj.name);
            m.writer().writeInt(p.nj.get().hp);
            m.writer().writeInt(p.nj.get().getMaxHP());
            m.writer().writeByte(p.nj.get().getLevel());
            m.writer().writeShort(p.nj.get().Weapon());
            m.writer().writeShort(p.nj.get().partBody());
            m.writer().writeShort(p.nj.get().partLeg());
            m.writer().writeByte(-1);
            m.writer().writeShort(p.nj.get().x);
            m.writer().writeShort(p.nj.get().y);
            m.writer().writeShort(p.nj.get().eff5buffHP());
            m.writer().writeShort(p.nj.get().eff5buffMP());
            m.writer().writeByte(0);
            m.writer().writeBoolean(p.nj.isHuman);
            m.writer().writeBoolean(p.nj.isNhanban);
            m.writer().writeShort(p.nj.get().partHead());
            m.writer().writeShort(p.nj.get().Weapon());
            m.writer().writeShort(p.nj.get().partBody());
            m.writer().writeShort(p.nj.get().partLeg());
            for (byte i = 0; i < p.nj.setThoiTrang.length; i++) {
                m.writer().writeShort(p.nj.setThoiTrang[i]);
            }
            for (int k = 16; k < 32; ++k) {//Trang bị 2
                final Item item = p.nj.get().ItemBody[k];
                if (item != null) {
                    m.writer().writeShort(item.id);
                    m.writer().writeByte(item.getUpgrade());
                    m.writer().writeByte(item.sys);
                } else {
                    m.writer().writeShort(-1);
                }
            }
            m.writer().flush();
            revc.sendMessage(m);
            m.cleanup();
            if (p.nj.get().mobMe != null) {
                m = new Message(-30);
                m.writer().writeByte(-68);
                m.writer().writeInt(p.nj.get().id);
                m.writer().writeByte(p.nj.get().mobMe.templates.id);
                m.writer().writeByte(p.nj.get().mobMe.isIsboss() ? 1 : 0);
                m.writer().flush();
                revc.sendMessage(m);
                m.cleanup();
            }
        } catch (Exception e) {
        }
    }

    public void selectUIZone(@Nullable final User p, @Nullable Message m) throws IOException {
        if (p == null || m == null) {
            return;
        }

        final byte zoneid = m.reader().readByte();
        final byte index = m.reader().readByte();
        m.cleanup();
        if (zoneid == this.id) {
            return;
        }
        Item item = null;
        try {
            item = p.nj.ItemBag[index];
        } catch (Exception ex) {
        }
        boolean isalpha = false;
        for (byte i = 0; i < this.map.template.npc.length; ++i) {
            final Npc npc = this.map.template.npc[i];
            if (npc.id == 13 && Math.abs(npc.x - p.nj.get().x) < 50 && Math.abs(npc.y - p.nj.get().y) < 50) {
                isalpha = true;
                break;
            }
        }
        if (((item != null && (item.id == 35 || item.id == 37)) || isalpha) && zoneid >= 0 && zoneid < this.map.area.length) {
            if (this.map.area[zoneid].getNumplayers() < this.map.template.maxplayers) {
                this.leave(p);
                this.map.area[zoneid].Enter(p);
                p.endLoad(true);
                if (item != null && item.id != 37) {
                    p.nj.removeItemBag(index);
                }
            } else {
                p.sendYellowMessage("Khu vực này đã đầy.");
                p.endLoad(true);
            }
        }
        m = new Message(57);
        m.writer().flush();
        p.sendMessage(m);
        m.cleanup();
    }

    public void openUIZone(@Nullable final User p) throws IOException {
        if (p == null) {
            return;
        }

        boolean isalpha = false;
        for (byte i = 0; i < this.map.template.npc.length; ++i) {
            final Npc npc = this.map.template.npc[i];
            if (npc.id == 13 && Math.abs(npc.x - p.nj.get().x) < 50 && Math.abs(npc.y - p.nj.get().y) < 50) {
                isalpha = true;
                break;
            }
        }
        if (p.nj.quantityItemyTotal(37) > 0 || p.nj.quantityItemyTotal(35) > 0 || isalpha) {
            final Message m = new Message(36);
            m.writer().writeByte(this.map.area.length);
            for (byte j = 0; j < this.map.area.length; ++j) {
                m.writer().writeByte(this.map.area[j].getNumplayers());
                m.writer().writeByte(this.map.area[j].getArryListParty().size());
            }
            m.writer().flush();
            p.sendMessage(m);
            m.cleanup();
        } else {
            p.nj.get().upDie();
        }
    }

    public void chatNPC(@Nullable final User p, final int idnpc, final @Nullable String chat) throws IOException {
        if (p == null || chat == null) {
            return;
        }
        final Message m = new Message(38);
        m.writer().writeShort(idnpc);
        m.writer().writeUTF(chat);
        m.writer().flush();
        p.sendMessage(m);
        m.cleanup();
    }

    @SneakyThrows
    public ItemMap LeaveItem(final int id, int x, int y) {
        return LeaveItem(id, x, y, 1);
    }

    public ItemMap LeaveItem(final int id, int njX, int njY, int quantity) throws IOException {

        //int rand = 0;
        //if (id == 457) {
        //  rand = util.nextInt(0, 2);
        //if (rand == 2) return null;
        //}
        if (this._itemMap.size() > 100) {
            this.removeItemMapMessage(this._itemMap.remove(0).itemMapId);
        }

        final ItemData data = ItemDataId(id);
        if (data == null) {
            return null;
        }
        Item item;
        if (data.type < 10) {
            if (data.type == 1) {
                item = itemDefault(id);
                item.sys = GameScr.SysClass(data.nclass);
            } else {
                final byte sys = (byte) util.nextInt(1, 3);
                item = itemDefault(id, sys);
            }
        } else {
            item = itemDefault(id);
        }
        if (item.isTypeNgocKham() || item.isTypeBody()) {
            for (Option option : item.option) {
                option.param = util.nextInt(option.param * 70 / 100, option.param);
            }
        }
        final ItemMap im = new ItemMap();
        im.itemMapId = this.getItemMapNotId();
        im.x = (short) util.nextInt(njX - 2, njX + 2);
        im.y = (short) njY;
        im.item = item;
        item.quantity = quantity;
        this._itemMap.add(im);
        final Message m = new Message(6);
        m.writer().writeShort(im.itemMapId);
        m.writer().writeShort(item.id);
        m.writer().writeShort(im.x);
        m.writer().writeShort(im.y);
        m.writer().flush();
        this.sendMessage(m);
        m.cleanup();
        return im;
    }

    public void FightMob(@Nullable final Body body, @Nullable final Message m) throws IOException {
        if (body == null || m == null) {
            return;
        }

        val p = body.c.p;

        if (body.getCSkill() == -1 && body.getSkills().size() > 0) {
            body.setCSkill(body.getSkills().get(0).id);
        }

        final Skill skill = body.getMyCSkillObject();
        if (skill == null) {
            return;
        }
        final int mobId = m.reader().readUnsignedByte();
        m.cleanup();
        final Mob mob = this.getMob(mobId);
        if (mob == null) {
            return;
        }
        if (body instanceof IGlobalBattler) {
            if (((IGlobalBattler) body).getPhe() == PK_TRANG) {
                if (mob.templates.id == idBachMobs[0] || mob.templates.id == idBachMobs[1]) {
                    util.Debug("Không đánh bạch giả");
                    return;
                }
            } else if (((IGlobalBattler) body).getPhe() == PK_TRANG) {
                if (mob.templates.id == idHacMobs[0] || mob.templates.id == idHacMobs[1]) {
                    util.Debug("Không đánh hắc giả");
                    return;
                }
            }
        }
        if (mob == null || mob.isDie) {
            return;
        }

        final Mob[] arMob = new Mob[10];
        arMob[0] = mob;
        if (body.ItemBody[1] == null) {
            p.sendYellowMessage("Vũ khí không thích hợp");
            util.Debug("Không vũ khí không thích hợp");
            return;
        }
        p.removeEffect(15);
        p.removeEffect(16);
        final SkillTemplates data = SkillData.Templates(skill.id, skill.point);
        p.getMp();
        if (body.mp < data.manaUse && !(body instanceof CloneChar)) {
            MessageSubCommand.sendMP((Ninja) body);
            return;
        }

        if (skill.coolDown > System.currentTimeMillis()) {
            return;
        }

        if (Math.abs(body.x - mob.x) > body.getCSkillTemplate().dx + 30 || Math.abs(body.y - mob.y) > body.getCSkillTemplate().dy + 30) {
            return;
        }
        skill.coolDown = System.currentTimeMillis() + data.coolDown;
        body.c.mobAtk = mob.id;

        if (body.isHuman) {
            body.upMP(-data.manaUse);
            p.getMp();
        }

        if (skill.id == 42) {
            body.x = mob.x;
            body.y = mob.y;
//            this.sendXYPlayer(p);
            this.setXYPlayers(mob.x, mob.y, p, p);
        }
        if (skill.id == 40) {
            if (mob.lvboss < 1 && !mob.isIsboss()) {
                this.DisableMobMessage(p, mob.id, 0);
            }
        }
        if (skill.id == 24) {
            this.DontMoveMobMessage(p, mob.id, 0);
        }
        if (skill.id == 4) {
            p.nj.get().upHP(p.nj.getPramSkill(50));
        }

        synchronized (this) {
            final int size = m.reader().available();
            byte n = 1;
            for (int i = 0; i < size; ++i) {
                final Mob mob2 = this.getMob(m.reader().readUnsignedByte());
                if (!mob2.isDie) {
                    if (mob.id != mob2.id) {
                        if (data.maxFight <= n) {
                            break;
                        }
                        arMob[n] = mob2;
                        ++n;
                    }
                }
            }
            m.cleanup();
            for (int j = 0; j < this.getUsers().size(); ++j) {
                Service.PlayerAttack(this.getUsers().get(j), arMob, body);
            }
            long xpup = 0L;
            for (byte k = 0; k < arMob.length; ++k) {
                if (arMob[k] != null) {
                    if (arMob[k].isDie) {
                        continue;
                    }
                    val stQuai = body.getPramItem(ST_LEN_QUAI_ID);
                    attackAMob(body, arMob[k], util.nextInt(body.dameMin(), body.dameMax()) + stQuai);
                }
            }
            p.nj.setTimeKickSession();

            if (xpup > 0L) {
                if (this.map.cave != null) {
                    this.map.cave.updateXP(xpup);
                } else {
                    if (p.nj.isNhanban) {
                        xpup /= 4L;
                    }
                    p.updateExp(xpup, true);
                    xpup /= 20L; // 5L
                    if (body.party != null) {
                        for (int i2 = 0; i2 < this.getUsers().size(); ++i2) {
                            final User p2 = this.getUsers().get(i2);
                            if (p2.nj.id != p.nj.id && p2.nj.party == p.nj.party && Math.abs(p2.nj.getLevel() - p.nj.getLevel()) <= 10) {
                                p2.updateExp(xpup, true);
                            }
                        }
                    }
                }
            }
        }
    }

    public synchronized void PlayerAttack(@Nullable final Ninja _char, @Nullable Mob[] arrMob, @Nullable Ninja[] arrChar) {
        if (_char == null) {
            return;
        }

        val p = _char.c.p;
        if (_char.getCSkill() == -1 && _char.getSkills().size() > 0) {
            _char.setCSkill(_char.getSkills().get(0).id);
        }
        final Skill skill = _char.getSkill(_char.getCSkill());
        if (skill == null) {
            return;
        }

        final SkillTemplates temp = SkillData.Templates(skill.id, skill.point);

        if (arrMob != null && arrChar != null) {
            try {
                short i;
                for (i = 0; i < this.getUsers().size(); i = (short) (i + 1)) {
                    if (this.getUsers().get(i).nj != null && this.getUsers().get(i).session != null && this.getUsers().get(i).nj.id != _char.id) {

                        Service.PlayerAttack(this.getUsers().get(i).nj, _char.id, (byte) skill.id, arrMob, arrChar);
                    }
                }
            } catch (Exception exception) {
            }
        } else if (arrMob != null) {
            try {
                short i;
                for (i = 0; i < this.getUsers().size(); i = (short) (i + 1)) {
                    if (this.getUsers().get(i).nj != null && this.getUsers().get(i).session != null && (this.getUsers().get(i).nj.id != _char.id)) {
                        Service.PlayerAttack(this.getUsers().get(i).nj, _char.id, (byte) skill.id, arrMob);
                    }
                }
            } catch (Exception exception) {
            }

        } else if (arrChar != null) {
            try {
                short i;
                for (i = 0; i < this.getUsers().size(); i = (short) (i + 1)) {
                    if (this.getUsers().get(i) != null && this.getUsers().get(i).session != null && this.getUsers().get(i).id != _char.id) {

                        Service.PlayerAttack(this.getUsers().get(i).nj, _char.id, skill.id, arrChar);
                    }
                }
            } catch (Exception exception) {
            }
        }

        if (arrChar != null) {
            byte i;
            for (i = 0; i < arrChar.length; i = (byte) (i + 1)) {
                val player = arrChar[i];
                if (player != null) {

                    int dame = _char.dameMax();
                    if (Math.abs(_char.x - player.x) > temp.dx + util.nextInt(40) + i * 30 || Math.abs(_char.y - player.y) > temp.dy + util.nextInt(40) + i * 10 || (Map.notCombat(this.map.id) && (_char.getTypepk() == 1 || _char.getTypepk() == 3 || player.getTypepk() == 1 || player.getTypepk() == 3))) {
                        dame = 0;

                    }
                    if (dame != 0) {
                        if (_char.getTypepk() == Constants.PK_DOSAT
                                || player.getTypepk() == Constants.PK_DOSAT
                                || (_char.getTypepk() == PK_PHE && player.getTypepk() == PK_PHE)
                                || (_char.solo != null && player.solo != null)
                                || _char.getTypepk() == PK_TRANG || _char.getTypepk() == PK_DEN || _char.getTypepk() == PK_PHE3) {
                            AttackPlayer(_char, player);
                        }
                    }
                }
            }
        }

        if (arrMob != null) {
            byte i;
            for (i = 0; i < arrMob.length; i = (byte) (i + 1)) {
                Mob mob = arrMob[i];
                if (mob != null) {
                    int fantal = _char.Fatal();
                    if (fantal > 750) {
                        fantal = 750;
                    }
                    boolean flag = (util.nextInt(2500) < fantal);
                    int dame = _char.dameMax();
                    if (Math.abs(_char.x - mob.x) > temp.dx + util.nextInt(40) + i * 30 || Math.abs(_char.y - mob.y) > temp.dy + util.nextInt(40) + i * 10) {
                        dame = 0;
                    }
                    if (dame != 0) {
                        AttackMob(_char, mob, util.nextInt(dame * 9 / 10, dame), flag, (byte) 0);
                    }
                }
            }
        }
    }

    @SneakyThrows
    protected void AttackMob(@Nullable Ninja _char, @Nullable Mob mob, int dame, boolean flag, byte type) {
        if (_char == null || mob == null) {
            return;
        }
        attackAMob(_char.get(), mob, dame + _char.getPramItem(ST_LEN_QUAI_ID));

    }

    public byte getId() {
        return this.id;
    }

    public void attackAMob(@Nullable final Body body, @Nullable Mob curMob, int dame) throws IOException {
        if (body == null || curMob == null) {
            return;
        }
        long xpup = 0L;
        if (curMob.isDie) {
            return;
        }
        if (curMob.zoneBoss != body.c.getPlace().getId() && curMob.isIsboss() && !curMob.isCallMob()) {
            return;
        }
        User p = body.c.p;

        if (body.getEffId(Mob.THIEU_DOT_ID) != null) {
            curMob.isThieuDot = true;
            curMob.masterThieuDot = body;
            curMob.mapThieuDot = this.map.id;
            curMob.zoneThieuDot = this.id;
        }
        if (this.map.cave == null && curMob.isIsboss() && body.getLevel() - curMob.level > 20) {
            dame = 0;
        }
        final int oldhp = curMob.hp;
        if (body.getPramItem(134) >= util.nextInt(1, 100)) {
            for (int k = 0; k < this.getUsers().size(); ++k) {
                GameCanvas.addEffect(this.getUsers().get(k).session, (byte) 1, curMob.id, (byte) 65, 1, 1);
                dame += curMob.hpmax * 30 / 100;
            }
        }
        if (body.getPramItem(135) >= util.nextInt(1, 100)) {
            for (int k = 0; k < this.getUsers().size(); ++k) {
                GameCanvas.addEffect(this.getUsers().get(k).session, (byte) 1, curMob.id, (byte) 64, 1, 1);
                dame += curMob.hpmax * 20 / 100;
            }
        }
        if (curMob.sys == 1) {
            dame += dame * body.getPramItem(54) / 200;
        } else if (curMob.sys == 2) {
            dame += dame * body.getPramItem(55) / 200;
        } else if (curMob.sys == 3) {
            dame += dame * body.getPramItem(56) / 200;
        }
        if (curMob.isCallMob()) {
            dame = curMob.hpmax / 10;
        }
        int fatal = body.Fatal();
        if (fatal > 800) {
            fatal = 800;
        }
        final boolean isfatal = fatal > util.nextInt(1, 2500);
        if (isfatal) {
            dame *= 2;
            dame = dame * (100 + body.FantalDamePercent()) / 100;
            dame += body.FantalDame();
        }
        if (dame <= 0) {
            dame = 1;
        }
        if (curMob.isFire) {
            dame *= 2;
        }
        if (p.nj.isNhanban) {
            dame = dame * p.nj.clone.percendame / 100;
        }
        int expMobX = 1;
        int xpID = body.getLevel() - curMob.level;
        if (xpID < -7) {
            xpID = -7;
        } else if (xpID > 7) {
            xpID = 7;
        }
        int xpnew = dame / (9 + xpID);
        if (curMob.lvboss == 1) {
            expMobX *= 2.5;
        } else if (curMob.lvboss == 2) {
            expMobX *= 5;
        } else if (curMob.lvboss == 3) {
            expMobX /= 2;
        }
        if (this.map.isLangCo()) {
            expMobX *= 1;
        } else if (this.map.VDMQ()) {
            expMobX *= 2;
        }

        xpnew *= curMob.EXPMobX((short) curMob.level) * expMobX;
        if (body.getEffType((byte) 18) != null) {
            xpnew *= body.getEffType((byte) 18).param;
        }
        xpnew += xpnew * body.percentUpEXP() / 100;

        if ((this.map.cave != null || (curMob.level > 1 && Math.abs(curMob.level - body.getLevel()) <= 10) || this.map.isLangCo()) && curMob.templates.id != 230 && curMob.templates.id != 221) {
            xpup += xpnew;
        }

        if (map.isLdgtMap()) {
            if (map.id == 90) {
                if (p.nj.getEffId(23) == null) {
                    curMob.updateHP(0);
                    p.sendYellowMessage("Bạn không thể nhìn rõ boss đi kiếm bông hoa ăn để khai nhãn");
                    return;

                } else {
                    curMob.updateHP(-dame);
                }
            } else {
                if (curMob.templates.id == 77) {
                    if (util.nextInt(0, 3) == 1) {
                        curMob.updateHP(0);
                    } else {
                        curMob.updateHP(-dame);
                    }
                } else {
                    curMob.updateHP(-dame);
                }
            }

        } else if (curMob.templates.id == 225) {
            curMob.updateHP(-1);
        } else {
            curMob.updateHP(-dame);
        }

        if (curMob.isDie) {
            if (battle != null) {
                battle.updateBattler(body.c, false, curMob);
            }

            if (candyBattle != null) {
                candyBattle.updateBattler(body.c, false, curMob);
            }
        }
        if (body instanceof Ninja) {
            Ninja _ninja = (Ninja) body;
            if (curMob != null && curMob.isDie && TaskHandle.isMobTask(_ninja, curMob)) {
                _ninja.upMainTask();
                if (_ninja.party != null) {
                    synchronized (_ninja.party.getNinjas()) {
                        for (Ninja player : _ninja.party.getNinjas()) {
                            if (player != null
                                    && player.p != null
                                    && player.party != null
                                    && player.id != _ninja.id
                                    && player.getTaskId() == _ninja.getTaskId()
                                    && player.getTaskIndex() == _ninja.getTaskIndex()) {
                                player.upMainTask();
                            }
                        }
                    }
                }
            }
        }

        if ((curMob.templates.id != 0 || p.nj.getTaskId() == 40) && curMob.lvboss != 3 && !curMob.isIsboss()) {
            val _ninja = p.nj;
            if ((util.nextInt(100) < 50 || curMob.templates.id == 69)
                    && TaskHandle.itemDrop(_ninja, curMob) != -1 && curMob.isDie) {
                final ItemMap item = LeaveItem(TaskHandle.itemDrop(_ninja, curMob), _ninja.x, _ninja.y);
                item.master = _ninja.id;
                item.item.setLock(true);
            }
        }

        if (curMob.isDie) {
            if (p.nj.getTasks()[0] != null && curMob.templates.id == p.nj.getTasks()[0].getKillId()
                    || p.nj.getTasks()[1] != null && curMob.templates.id == p.nj.getTasks()[1].getKillId()) {
                if (curMob.lvboss == 3) {
                    // Ta thu
                    p.nj.updateTaskOrder(TaskOrder.NHIEM_VU_TA_THU, 1);
                }
                p.nj.updateTaskOrder(TaskOrder.NHIEM_VU_HANG_NGAY, 1);

            }
            if (curMob.templates.id == 57
                    && p.nj.getTaskId() == 36
                    && p.nj.getTaskIndex() == 1) {
                val item = ItemData.itemDefault(taskTemplates[36].getItemsPick()[p.nj.getTaskIndex()]);
                item.quantity = 1;
                item.setLock(true);

                p.nj.addItemBag(false, item.clone());
                p.nj.upMainTask();
                val m = new Message(-6);
                if (p != null && p.nj != null && p.nj.party != null) {
                    for (Ninja ninja : p.nj.party.getNinjas()) {
                        if (ninja != null && ninja.id != p.nj.id
                                && ninja.getTaskId() == p.nj.getTaskId()
                                && p.nj.getTaskIndex() == ninja.getTaskIndex()) {
                            p.nj.addItemBag(false, item.clone());
                            p.nj.upMainTask();
                        }
                    }
                }
            }
//            if (this.map.isLangCo()) {
//                if (util.nextInt(0, 1) < 1) {
//                    p.upluongMessage(util.nextInt(1, 1));
//                }
//            }
        }

        if (dame > 0) {
            curMob.Fight(p.session.id, dame);
        }
        if (!curMob.isFire) {
            if (body.percentFire2() >= util.nextInt(1, PERCENT_SKILL_MAX)) {
                this.FireMobMessage(curMob.id, 0);
            }
            if (body.percentFire4() >= util.nextInt(1, PERCENT_SKILL_MAX)) {
                this.FireMobMessage(curMob.id, 1);
            }
        }

        if (!curMob.isIce && body.percentIce1_5() >= util.nextInt(1, PERCENT_SKILL_MAX)) {
            this.IceMobMessage(curMob.id, 0);
        }

        if (!curMob.isIce && body.percentIce2_3() >= util.nextInt(1, PERCENT_SKILL_MAX)) {
            this.IceMobMessage(curMob.id, 1);
        }

        if (!curMob.isWind) {
            if (body.percentWind1() >= util.nextInt(1, PERCENT_SKILL_MAX)) {
                this.WindMobMessage(curMob.id, 0);
            }
            if (body.percentWind2() >= util.nextInt(1, PERCENT_SKILL_MAX)) {
                this.WindMobMessage(curMob.id, 1);
            }
        }
        if (curMob.isDie) {
            this.MobStartDie(oldhp - curMob.hp, curMob.id, isfatal);
        } else {
            this.attackMob(oldhp - curMob.hp, curMob.id, isfatal);
        }

        if (map.isLdgtMap() && curMob.isDie && curMob.templates.id != 81) {
            int xuGt = 1;
            if (curMob.lvboss == 1) {
                xuGt = 5;
            } else {
                xuGt = 1;
            }

            p.getClanTerritoryData().getClanTerritory().upPoint(xuGt);
        }

        // Chien truong keo
        if (candyBattle != null) {

            if (curMob.templates.id == CandyBattle.GIO_KEO_DEN_ID || curMob.templates.id == CandyBattle.GIO_KEO_TRANG_ID) {
                if (curMob.templates.id == CandyBattle.GIO_KEO_DEN_ID && curMob.attackCount.get() >= 10) {
                    candyBattle.upPoint(PK_DEN, -5);
                    LeaveItem(CandyBattle.KEO_NGOT_ID, p.nj.x, p.nj.y, 5);
                    curMob.attackCount.set(0);
                    refreshMob(curMob.id, true);
                } else if (curMob.templates.id == CandyBattle.GIO_KEO_TRANG_ID && curMob.attackCount.get() >= 10) {
                    candyBattle.upPoint(PK_TRANG, -5);
                    LeaveItem(CandyBattle.KEO_NGOT_ID, p.nj.x, p.nj.y, 5);
                    curMob.attackCount.set(0);
                    refreshMob(curMob.id, true);
                }
            } else {
                LeaveItem(CandyBattle.KEO_NGOT_ID, p.nj.x, p.nj.y, 2);
            }
        }

        if (curMob.isDie && curMob.level > 1) {
            ++this.numMobDie;
            if (this.map.cave != null) {
                if (curMob.isIsboss()) {
                    this.map.cave.updatePoint(50);
                } else if (curMob.lvboss == 2) {
                    this.map.cave.updatePoint(20);
                } else if (curMob.lvboss == 1) {
                    this.map.cave.updatePoint(10);
                } else {
                    this.map.cave.updatePoint(1);
                }
            }
            final int master = curMob.sortNinjaFight();
            if (curMob.isCallMob() && curMob.removeCallMob) {
                int indexBagNull = p.nj.getIndexBagNotItem();
                if (indexBagNull != -1) {
                    Item it = null;
                    if (util.nextInt(100) < 30) {
                        it = ItemData.itemDefault(util.nextInt(5, 7));
                    } else if (util.nextInt(100) < 30) {
                        it = ItemData.itemDefault(util.nextInt(275, 278));
                    } else if (util.nextInt(100) < 23) {
                        it = ItemData.itemDefault(436);
                    } else if (util.nextInt(100) < 20) {
                        it = ItemData.itemDefault(util.nextInt(568, 569));
                    } else if (util.nextInt(100) < 10) {
                        it = ItemData.itemDefault(util.nextInt(652, 653));
                    } else if (util.nextInt(100) < 3) {
                        it = ItemData.itemDefault(485);
                    } else {
                        it = ItemData.itemDefault(util.nextInt(409, 410));
                    }
                    it.setLock(false);
                    if (ItemData.ItemDataId(it.id).type == 10) {
                        it.isExpires = true;
                        it.expires = util.TimeDay(7);
                    }
                    it.quantity = 1;
                    p.nj.addItemBag(true, it);
                    p.nj.event1_pointBossTuanLoc++;
                }
                p.nj.getPlace().getMobs().remove(curMob);
            }
            if (!this.map.isLdgtMap()) {
                if (curMob.lvboss == 0 && p.nj.taskDanhVong[4] != -1 && p.nj.isTaskDanhVong == 1 && p.nj.taskDanhVong[0] == 0 && p.nj.getLevel() <= (curMob.level + 10) || p.nj.getLevel() <= (curMob.level - 10)) {
                    for (int i = 0; i < p.nj.ItemBody.length; i++) {
                        if (p.nj.ItemBody[i] != null && p.nj.ItemBody[i].id == p.nj.taskDanhVong[4]) {
                            p.nj.taskDanhVong[1]++;
                            if (p.nj.taskDanhVong[1] < p.nj.taskDanhVong[2]) {
                                p.sendYellowMessage("- Sử dụng trang bị " + ItemData.ItemDataId(p.nj.taskDanhVong[4]).name + ". " + "- Tiêu diệt " + p.nj.taskDanhVong[1] + "/" + p.nj.taskDanhVong[2] + "quái lệch 10 cấp độ.");
                            }
                            if (p.nj.taskDanhVong[1] >= p.nj.taskDanhVong[2]) {
                                p.sendYellowMessage("Hoàn thành nhiệm vụ, hãy gặp Ameji để trả nhiệm vụ");
                            }
                        }
                    }
                } else if (curMob.lvboss == 1) {
                    --this.numTA;
                    if (Math.abs(body.getLevel() - curMob.level) <= 10) {
                        val yen = util.nextInt(5000, 50000);
                        body.c.upyenMessage(yen);
                        p.sendYellowMessage("Bạn nhận được " + yen + " yên");
                        if (p.nj.taskDanhVong[4] != -1 && p.nj.isTaskDanhVong == 1 && p.nj.taskDanhVong[0] == 1 && p.nj.getLevel() <= (curMob.level + 10) || p.nj.getLevel() <= (curMob.level - 10)) {
                            for (int i = 0; i < p.nj.ItemBody.length; i++) {
                                if (p.nj.ItemBody[i] != null && p.nj.ItemBody[i].id == p.nj.taskDanhVong[4]) {
                                    p.nj.taskDanhVong[1]++;
                                    if (p.nj.taskDanhVong[1] < p.nj.taskDanhVong[2]) {
                                        p.sendYellowMessage("- Sử dụng trang bị " + ItemData.ItemDataId(p.nj.taskDanhVong[4]).name + ". " + "- Tiêu diệt " + p.nj.taskDanhVong[1] + "/" + p.nj.taskDanhVong[2] + "quái tinh anh lệch 10 cấp độ.");
                                    }
                                    if (p.nj.taskDanhVong[1] == p.nj.taskDanhVong[2]) {
                                        p.sendYellowMessage("Hoàn thành nhiệm vụ, hãy gặp Ameji để trả nhiệm vụ");
                                    }
                                }
                            }
                        }
                    }
                } else if (curMob.lvboss == 2) {
                    --this.numTL;
                    if (Math.abs(body.getLevel() - curMob.level) <= 10) {
                        val yen = util.nextInt(5000, 70000);
                        body.c.upyenMessage(yen);
                        p.sendYellowMessage("Bạn nhận được " + yen + " yên");
                        if (p.nj.taskDanhVong[4] != -1 && p.nj.isTaskDanhVong == 1 && p.nj.taskDanhVong[0] == 2 && p.nj.getLevel() <= (curMob.level + 10) || p.nj.getLevel() <= (curMob.level - 10)) {
                            for (int i = 0; i < p.nj.ItemBody.length; i++) {
                                if (p.nj.ItemBody[i] != null && p.nj.ItemBody[i].id == p.nj.taskDanhVong[4]) {
                                    p.nj.taskDanhVong[1]++;
                                    if (p.nj.taskDanhVong[1] < p.nj.taskDanhVong[2]) {
                                        p.sendYellowMessage("- Sử dụng trang bị " + ItemData.ItemDataId(p.nj.taskDanhVong[4]).name + ". " + "- Tiêu diệt " + p.nj.taskDanhVong[1] + "/" + p.nj.taskDanhVong[2] + "quái thủ lĩnh lệch 10 cấp độ.");
                                    }
                                    if (p.nj.taskDanhVong[1] == p.nj.taskDanhVong[2]) {
                                        p.sendYellowMessage("Hoàn thành nhiệm vụ, hãy gặp Ameji để trả nhiệm vụ");
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                if (curMob.lvboss == 1) {
                    ItemMap itemMap = LeaveItem((short) 231, p.nj.x, p.nj.y);
                }
            }
            if (!curMob.isCallMob()) {
                leaveItemLogic(body, curMob, p, master);
            }
            if (this.map.cave != null && this.map.getXHD() < 9) {
                curMob.isRefresh = false;
                if (this.getMobs().size() == this.numMobDie) {
                    if (this.map.getXHD() == 5) {
                        if (this.map.id == 105) {
                            this.map.cave.openMap();
                            this.map.cave.openMap();
                            this.map.cave.openMap();
                        } else if (this.map.id == 106 || this.map.id == 107 || this.map.id == 108) {
                            final Cave cave2 = this.map.cave;
                            ++cave2.finsh;
                            if (this.map.cave.finsh >= 3) {
                                this.map.cave.openMap();
                            }
                        } else {
                            this.map.cave.openMap();
                        }
                    } else if (this.map.getXHD() == 6 && this.map.id == 116) {
                        if (this.map.cave.finsh == 0) {
                            this.map.cave.openMap();
                        } else {
                            final Cave cave3 = this.map.cave;
                            ++cave3.finsh;
                        }
                        this.numMobDie = 0;
                        for (short l2 = 0; l2 < this.getMobs().size(); ++l2) {
                            this.refreshMob(l2);
                        }
                    } else {
                        this.map.cave.openMap();
                    }
                }
            }
            if (this.map.tta != null && map.id == 112) {
                curMob.status = 0;
                int mobSizeHideBoss = this.getMobs().size() - 1;
                int mobSizeOneMap = mobSizeHideBoss / 2;
                if (mobSizeOneMap == this.numMobDie || mobSizeOneMap * 2 == this.numMobDie || (map.tta.lvTTA == 2 && mobSizeHideBoss + 1 == this.numMobDie)) {
                    map.tta.lvTTA++;
                    if (map.tta.lvTTA < 3) {
                        for (User us : this.getUsers()) {
                            leave(us);
                            map.tta.map[0].area[0].Enter(us);
                        }
                    }
                }
            }
        }
        if (xpup > 0L) {
            xpup += util.nextInt(5, 10);
            if (this.map.cave != null) {
                this.map.cave.updateXP(xpup / 2);
            } else {
                if (p.nj.isNhanban) {
                    xpup /= 4L;
                }
                p.updateExp(xpup, true);
                xpup /= 5L;
                if (body.party != null) {
                    for (int i2 = 0; i2 < this.getUsers().size(); ++i2) {
                        final User p2 = this.getUsers().get(i2);
                        if (p2.nj.id != p.nj.id && p2.nj.party == p.nj.party && Math.abs(p2.nj.getLevel() - p.nj.getLevel()) <= 10) {
                            p2.updateExp(xpup, true);
                        }
                    }
                }
            }
        }
    }

    private void leaveItemLogic(@Nullable Body body, @Nullable Mob curMob, @Nullable User p, int master) throws IOException {
        if (body == null) {
            return;
        }

        short[] arid = new short[0];
        if (this.map.isLangCo()) {
            if (util.percent(100, 20)) {
                if (util.percent(150, 7)) {
                    arid = new short[]{839, 840, 841, 225};
                } else if (util.percent(150, 15)) {
                    arid = new short[]{454, 454, 226};
                } else if (util.percent(150, 23)) {
                    arid = new short[]{573, 574, 575, 576, 577, 578, 228};
                } else if (util.percent(150, 33)) {
                    arid = new short[]{455, 455, 455, 455, 455, 456, 456, 227};
                } else if (util.percent(100, 2)) {
                    arid = new short[]{223, 224, 225, 226, 227, 228};
                } else if (util.percent(200, 10)) {
                    arid = new short[]{485, 340};
                } else {
                    arid = new short[]{12, 12, 12, 12, 4, 5};
                }
            }
        if (this.map.isLangCo()) {
                if (util.nextInt(0, 1) < 1) {
                    p.upluongMessage(util.nextInt(1, 1));
                }
            }
        } else {
            int curMobMaxLv = curMob.level - curMob.level % 10 + 10;
            if (curMobMaxLv > 100) {
                curMobMaxLv = 100;
            }
            int yen = 90 * curMob.level / 2 + util.nextInt(234, 678);
            if (map.VDMQ()) {
                if (curMob.level > 125) {
                    yen *= 4;
                } else {
                    yen *= 2;
                }
            }
            if (util.nextInt(10) < 3 && Math.abs(curMob.level - body.getLevel()) <= 10) {
                if (yen > 1500) {
                    p.nj.upyenMessage(yen);
                    p.sendYellowMessage("Bạn nhận được " + util.getFormatNumber(yen) + " yên");
                } else {
                    p.nj.upyenMessage(yen);
                }
            }
            if (!curMob.isIsboss() && map.cave == null && (map.getXHD() != 3 || map.getXHD() != 4 || map.getXHD() != 5 || map.getXHD() != 6 || map.getXHD() != 7 || map.getXHD() != 9) && !map.VDMQ()) {
                switch (curMobMaxLv) {
                    case 10:
                        if (util.nextInt(100) < 20) {
                            if (util.nextInt(100) < 10) {
                                arid = new short[]{0, 1, 2, 13, 18, (short) util.nextInt(105, 208)};
                            } else {
                                arid = new short[]{0, 1, 2, 13, 18};
                            }
                        }
                        break;
                    case 20:
                        if (util.nextInt(100) < 20) {
                            if (util.nextInt(100) < 10) {
                                arid = new short[]{0, 1, 2, 3, 14, 19, 94, 99, 104, 109, 114, 119, 124, 125, 134, 135, 144, 145, 154, 155, 164, 165, 174, 179, 184, 189};
                            } else {
                                arid = new short[]{0, 1, 2, 3, 14, 19};
                            }
                        }
                        break;
                    case 30:
                        if (util.nextInt(100) < 20) {
                            if (util.nextInt(100) < 10) {
                                arid = new short[]{0, 1, 2, 3, 14, 19, 95, 100, 105, 110, 115, 120, 126, 127, 136, 137, 146, 147, 156, 157, 166, 167, 175, 180, 185, 190};
                            } else {
                                arid = new short[]{0, 1, 2, 3, 14, 19};
                            }
                        }
                        break;
                    case 40:
                        if (util.nextInt(100) < 20) {
                            if (util.nextInt(100) < 10) {
                                arid = new short[]{2, 3, 15, 20, 96, 101, 106, 111, 116, 201, 128, 129, 138, 139, 148, 149, 158, 159, 168, 169, 181, 186, 191, 176};
                            } else {
                                arid = new short[]{2, 3, 15, 20};
                            }
                        }
                        break;
                    case 50:
                        if (util.nextInt(100) < 20) {
                            if (util.nextInt(100) < 10) {
                                arid = new short[]{3, 15, 20, 97, 102, 107, 112, 117, 122, 130, 131, 140, 141, 150, 151, 160, 161, 170, 171, 177, 182, 187, 192};
                            } else {
                                arid = new short[]{3, 15, 20};
                            }
                        }
                        break;
                    case 60:
                        if (util.nextInt(100) < 20) {
                            if (util.nextInt(100) < 5) {
                                arid = new short[]{3, 4, 16, 21, 98, 103, 108, 113, 118, 123, 132, 133, 142, 143, 152, 153, 162, 163, 172, 173, 178, 183, 188, 193};
                            } else {
                                arid = new short[]{3, 4, 16, 21};
                            }
                        }
                        break;
                    case 70:
                        if (util.nextInt(100) < 20) {
                            if (util.nextInt(100) < 10) {
                                arid = new short[]{3, 4, 16, 21, (short) util.nextInt(317, 336)};
                            } else {
                                arid = new short[]{3, 4, 16, 21};
                            }
                        }
                        break;
                    case 80:
                    case 90:
                    case 100:
                    case 110:
                    case 120:
                    case 130:
                    case 140:
                        if (util.nextInt(100) < 20) {
                            arid = new short[]{3, 4, 17, 22};
                        }
                        break;
                }
            }
        }

        short[] aridEvent = new short[0];
        if (this.map.isLangCo()) {
            if (server.manager.EVENT == 1) {
                if (util.percent(100, 5)) {
                    aridEvent = new short[]{590,595};
                }
            }
        } else {
            if (p.nj.getLevel() > 20 && Math.abs(curMob.level - body.getLevel()) <= 10) {
                if (server.manager.EVENT == 1) {
                    if (p.nj.typeEvent == 1) {
                        short[] idNlsk = new short[]{590,595};
                        int id = idNlsk[util.nextInt(idNlsk.length)];
                        boolean per = util.percent(100, 5);
                        if (this.map.VDMQ()) {
                            per = util.percent(100, 5);
                        }
                        if (per) {
                            Item im = ItemData.itemDefault(id);
                            im.quantity = 1;
                            if (p.nj.getEffId(41) != null || p.nj.getEffId(40) != null) {
                                im.isLock = false;
                            } else {
                                im.isLock = false;
                            }
                            p.nj.addItemBag(true, im);
                        }
                    } else {
                        if (util.percent(100, 1)) {
                            aridEvent = new short[]{590,595};
                        }
                    }
                }
            }
        }

        if (this.map.VDMQ() && body.getLevel() >= 100 && util.percent(300, 1)) {
            arid = new short[]{545, 545};
        }

        short[] aridVdmq = new short[0];
        if (this.map.VDMQ() && (body.getEffId(40) != null || body.getEffId(41) != null) && util.percent(100, 10)) {
            if (util.percent(100, 1)) {
                aridVdmq = new short[]{454, 454};
            } else if (util.percent(300, 5)) {
                aridVdmq = new short[]{4, 4, 4, 575, 575, 575, 574, 573, 574, 574, 574, 524};
            } else if (util.percent(100, 2)) {
                aridVdmq = new short[]{4, 4, 4, 486, 487, 488, 489, 439, 440, 441, 442};
            } else if (util.percent(150, 2)) {
                aridVdmq = new short[]{4, 4, 4, 455, 455, 455, 455, 456, 456};
            } else {
                if (curMob.level > 70) {
                    aridVdmq = new short[]{3, 4, 17, 22};
                } else {
                    aridVdmq = new short[]{3, 4, 16, 21};
                }
            }
        }

        if (curMob.isIsboss() && (map.cave == null || (map.cave != null && !map.isCaveMapId())) && !map.isLdgtMap() && !map.isChienTruongKeo() && !map.isGtcMap() && (map.tta == null || (map.tta != null && !map.isTTAMap()))) {
            updateBossItemDrop(curMob);
            short[] aridboss = new short[0];// boss vdmq
            if (curMob.templates.id == 203 || curMob.templates.id == 201 || curMob.templates.id == 204) {
                aridboss = new short[]{7, 7, 7, 7, 7, 7, 7, 8, 8, 340, 340, 340, 340, 340, 547, 257, 257, 257, 257, 545, 438, 438, 438, 438, 438, 256};
                short[] idSVC = new short[]{552, 553, 554, 555, 556, 557, 558, 559, 560, 561, 562, 563};
                for (int q = 0; q < util.nextInt(0, 3); q++) {
                    ItemMap im = LeaveItem(idSVC[util.nextInt(0, idSVC.length - 1)], curMob.x + util.nextInt(-150, 150), curMob.y);
                    if (im != null) {
                        im.item.isLock = false;
                        im.master = master;
                    }
                }
            }
            if (!map.isLangCo()) {
                short[] upNext = new short[]{0, 4, 5, 6, 7, 8};
                short[] idVuKhi = new short[]{94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 109, 108, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 331, 332, 333, 334, 335, 336};
                for (int q = 0; q < 4; q++) {
                    ItemMap im = this.LeaveItem(idVuKhi[util.nextInt(0, idVuKhi.length - 1)], curMob.x + util.nextInt(-150, 150), curMob.y);
                    if (im != null) {
                        im.item.quantity = 1;
                        if (util.percent(100, 1)) {
                            im.item.upgradeNext((byte) 11);
                        } else if (util.percent(100, 5)) {
                            im.item.upgradeNext((byte) 10);
                        } else if (util.percent(100, 10)) {
                            im.item.upgradeNext((byte) 9);
                        } else {
                            im.item.upgradeNext((byte) upNext[util.nextInt(0, upNext.length - 1)]);
                        }
                        int idOp2;
                        for (Option Option : im.item.option) {
                            idOp2 = Option.id;
                            Option.param = util.nextInt(im.item.getOptionShopMin(idOp2, Option.param), Option.param);
                        }
                        im.item.isLock = false;
                        im.master = master;
                    }
                }
            } else {
                aridboss = new short[]{7, 7, 7, 8, 8, 8, 8, 8, 8, 454, 454, 454, 454, 454, 457, 457, 457, 457, 457, 340, 340, 340, 340, 340, 257, 257, 257};
            }
            if (!map.isLangCo() && !map.VDMQ()) {
                short[] idSVC = new short[]{524, 552, 553, 554, 555, 556, 557};
                for (int q = 0; q < util.nextInt(0, 2); q++) {
                    ItemMap im = LeaveItem(idSVC[util.nextInt(0, idSVC.length - 1)], curMob.x + util.nextInt(-150, 150), curMob.y);
                    if (im != null) {
                        im.item.isLock = false;
                        im.master = master;
                    }
                }
            }
            if (util.percent(100, 15) && (map.VDMQ() || map.isLangCo())) {
                Item im = ItemData.itemDefault(833);
                im.quantity = 1;
                im.isExpires = false;
                im.expires = -1;
                im.setLock(false);
                p.nj.addItemBag(true, im);
                Manager.chatKTG("Chúc mừng " + p.nj.name + " đã nhận được " + ItemData.ItemDataId(im.id).name + " khi tiêu diệt BOSS " + curMob.templates.name);
            }
            for (int s = 0; s < aridboss.length; s++) {
                ItemMap im = this.LeaveItem(aridboss[util.nextInt(0, aridboss.length - 1)], curMob.x + util.nextInt(-150, 150), curMob.y);
                if (im != null) {
                    im.item.quantity = 1;
                    im.master = master;
                }
            }
            for (int i = 0; i < N_ITEM_BOSS; i++) {
                ItemMap im = this.LeaveItem(curMob.templates.arrIdItem[util.nextInt(0, curMob.templates.arrIdItem.length - 1)], curMob.x + util.nextInt(-150, 150), p.nj.y);
                if (im != null) {
                    im.master = master;
                }
            }
            for (int y = 0; y < 20; y++) {
                ItemMap im = this.LeaveItem(12, curMob.x + util.nextInt(-150, 150), p.nj.y);
                if (im != null) {
                    if (curMob.level <= 60) {
                        im.item.quantity = 100000;
                    } else {
                        im.item.quantity = 200000;
                    }
                    im.master = master;
                }
            }
        }

        if (map.isLdgtMap()
                && curMob.templates.id == 81) {
            // Lam thach thao
            if (10 >= util.nextInt(1, 100)) {
                final ItemMap itemMap = LeaveItem((short) 261, p.nj.x, p.nj.y);
                itemMap.item.expires = util.TimeMinutes(30);
                itemMap.master = master;
            }

        } else {
            int randomIndex = arid.length == 0 ? 0 : util.nextInt(0, arid.length - 1);
            if (randomIndex > 0 && arid[randomIndex] != -1
                    && (this.map.isLangCo() || Math.abs(curMob.level - body.getLevel()) <= 10)) {
                final ItemMap im = this.LeaveItem(arid[randomIndex], curMob.x + util.nextInt(-30, 30), curMob.y);
                if (im != null) {
                    int quantity = 1;
                    if (this.map.isLangCo()) {
                        if (im.item.id == 12) {
                            if (util.percent(100, 20)) {
                                quantity = 30000;
                            } else if (util.percent(100, 50)) {
                                quantity = 20000;
                            } else {
                                quantity = 15000;
                            }
                        }
                    }
                    if (im.item.id == 486 || im.item.id == 487 || im.item.id == 488 || im.item.id == 489 || im.item.id == 439 || im.item.id == 440 || im.item.id == 441 || im.item.id == 442) {
                        if (util.percent(100, 90)) {
                            for (int i = 0; i < im.item.option.size(); i++) {
                                im.item.option.remove(i);
                            }
                            im.item.sale = 0;
                        }
                    }
                    if (im.item.id == 455 || im.item.id == 456) {
                        im.item.isExpires = true;
                        im.item.expires = util.TimeDay(7);
                    } else if (im.item.id == 545) {
                        im.item.isExpires = true;
                        im.item.expires = util.TimeDay(1);
                    }
                    im.item.quantity = quantity;
                    im.master = master;
                }
            }
            int randomIndexEvent = aridEvent.length == 0 ? 0 : util.nextInt(0, aridEvent.length - 1);
            if (randomIndexEvent > 0 && aridEvent[randomIndexEvent] != -1
                    && (this.map.isLangCo() || Math.abs(curMob.level - body.getLevel()) <= 10)) {
                final ItemMap im = this.LeaveItem(aridEvent[randomIndex], curMob.x + util.nextInt(-30, 30), curMob.y);
                if (im != null) {
                    int quantity = 1;
                    if (p.nj.getEffId(40) != null || p.nj.getEffId(41) != null) {
                        im.item.setLock(false);
                    } else {
                        im.item.setLock(true);
                    }
                    im.item.quantity = quantity;
                    im.master = master;
                }
            }
            int randomIndexVdmq = aridVdmq.length == 0 ? 0 : util.nextInt(0, aridVdmq.length - 1);
            if (randomIndexVdmq > 0 && aridVdmq[randomIndexVdmq] != -1
                    && (this.map.VDMQ() && Math.abs(curMob.level - body.getLevel()) <= 10)) {
                final ItemMap im = this.LeaveItem(aridVdmq[randomIndexVdmq], curMob.x + util.nextInt(-30, 30), curMob.y);
                if (im != null) {
                    int quantity = 1;
                    im.item.setLock(false);
                    if (im.item.id == 455 || im.item.id == 456) {
                        im.item.isExpires = true;
                        im.item.expires = util.TimeDay(7);
                    } else if (im.item.id == 545) {
                        im.item.isExpires = true;
                        im.item.expires = util.TimeDay(1);
                    }
                    im.item.quantity = quantity;
                    im.master = master;
                }
            }
        }

        if (map.tta != null && map.isTTAMap() && (curMob.templates.id == 113 || curMob.templates.id == 112)) {
            Item im = ItemData.itemDefault(288);
            im.isLock = true;
            im.isExpires = false;
            p.nj.addItemBag(true, im);
        }

        if (curMob.isIsboss()) {
            if (this.map.cave == null) {
                if (curMob.templates.id != 230 && curMob.templates.id != 221) {
                    Manager.chatKTG(body.c.name + " đã tiêu diệt " + curMob.templates.name);
                }
            }
            if (this.map.cave != null && this.map.getXHD() == 9 && ((this.map.id == 157 && this.map.cave.level == 0) || (this.map.id == 158 && this.map.cave.level == 1) || (this.map.id == 159 && this.map.cave.level == 2)) && util.nextInt(3) < 3) {
                this.map.cave.updatePoint(this.getMobs().size());
                for (short k2 = 0; k2 < this.getMobs().size(); ++k2) {
                    this.getMobs().get(k2).updateHP(-this.getMobs().get(k2).hpmax);
                    this.getMobs().get(k2).isRefresh = false;
                    for (short h = 0; h < this.getUsers().size(); ++h) {
                        Service.setHPMob(this.getUsers().get(h).nj, this.getMobs().get(k2).id, 0);
                    }
                }
                final Cave cave = this.map.cave;
                ++cave.level;
            }
        }
    }

    @SneakyThrows
    private void AttackPlayer(@Nullable final Ninja body, @Nullable Ninja other) {
        if (body == null || other == null) {
            return;
        }
        final int oldhp = other.hp;
        skillEffect(body, other);
        body.damage(other);
        if (other.isDie) {
            if (battle != null) {
                battle.updateBattler(body.c, other.isHuman, other);
            }

        }
        if (other.isDie) {

            if (body.getTypepk() == PK_DOSAT) {
                body.updatePk(1);
            }
            final long num1 = Level.getMaxExp(other.getLevel());
            final long num2 = Level.getLevel(other.getLevel()).exps;
            if (other.pk > 0) {
                if (other.getExp() > num1) {
                    other.expdown = 0L;
                    final Ninja ninja1 = other;
                    ninja1.setExp(ninja1.getExp() - num2 * (5 + other.pk) / 100L);
                    if (other.getExp() < num1) {
                        other.setExp(num1);
                    }
                } else {
                    other.setExp(num1);
                    final Ninja ninja2 = other;
                    ninja2.expdown += num2 * (5 + other.pk) / 100L;
                    if (other.expdown > num2 * 50L / 100L) {
                        other.expdown = num2 * 50L / 100L;
                    }
                }
                other.updatePk(-1);
            }
            other.type = 14;
            this.sendDie(other);
        }
    }

    private void skillEffect(@Nullable final Body body, @Nullable final Ninja other) {
        if (body == null || other == null) {
            return;
        }

        if (other.getEffId(5) == null) {
            if (body.percentFire2() >= util.nextInt(1, PERCENT_SKILL_MAX)) {
                this.FireNinjaMessage(other.id, 0);
            }
            if (body.percentFire4() >= util.nextInt(1, PERCENT_SKILL_MAX)) {
                this.FireNinjaMessage(other.id, 1);
            }
        }

        if (other.getEffId(6) == null) {
            if (body.percentIce1_5() >= util.nextInt(1, PERCENT_SKILL_MAX)) {
                this.IceNinjaMessage(other.id, 0);
            } else if (body.percentIce2_3() >= util.nextInt(1, PERCENT_SKILL_MAX)) {
                this.IceNinjaMessage(other.id, 1);
            }
        }
        if (other.nclass == KUNAI && body.getEffId(6) == null) {
            if (other.percentIceKunai() >= util.nextInt(1, PERCENT_SKILL_MAX)) {
                this.IceNinjaMessage(body.id, 2);
            }
        }

        if (other.getEffId(7) == null) {
            if (body.percentWind1() >= util.nextInt(1, PERCENT_SKILL_MAX)) {
                this.WindNinjaMessage(other.id, 0);
            } else if (body.percentWind2() >= util.nextInt(1, PERCENT_SKILL_MAX)) {
                this.WindNinjaMessage(other.id, 1);
            }
        }
    }

    private void updateBossItemDrop(@Nullable final Mob mob) {
        if (mob == null) {
            return;
        }
        if (mob.level == 45) {
            mob.templates.arrIdItem = BOSS_ITEM_LV45;
        } else if (mob.level == 55) {
            mob.templates.arrIdItem = BOSS_ITEM_LV55;
        } else if (mob.level == 65) {
            mob.templates.arrIdItem = BOSS_ITEM_LV65;
        } else if (mob.level == 75) {
            mob.templates.arrIdItem = BOSS_ITEM_LV75;
        }
    }

    protected boolean canAttackNinja(final @Nullable Body body, final @Nullable Ninja other) {
        if (body == null || other == null) {
            return false;
        }

        short myPk = body.getTypepk();
        val p = body.c.p;

        if (body.isNhanban) {
            myPk = body.c.getTypepk();
        }

        short otherPk = other.get().getTypepk();
        return (body.ItemBody[1] != null && other.get() != null
                && ((myPk == 1 & otherPk == 1)
                || myPk == 3
                || otherPk == 3
                || (myPk == PK_TRANG && otherPk == PK_DEN)
                || (myPk == PK_DEN && otherPk == PK_TRANG))
                || (p.nj.solo != null
                && other.solo != null
                && p.nj.solo == other.solo) || ((p.nj.addCuuSat && other.isCuuSat) || (p.nj.isCuuSat && other.addCuuSat)));
    }

    public void attackNinja(final @Nullable Body body, @Nullable Message m) throws IOException {
        if (body == null || m == null) {
            return;
        }

        val p = body.c.p;
        final int ninjaId = m.reader().readInt();
        m.cleanup();
        // TODO CHECK
        final Ninja other = this.getNinja(ninjaId);

        if (GameScr.mapNotPK(this.map.id) || p.nj.get().getEffId(14) != null || p.nj.get().getEffId(6) != null || p.nj.get().getEffId(7) != null) {
            return;
        }

        synchronized (this) {
            //  final Ninja other = this.getNinja(ninjaId);

            if (other == null) {
                return;
            }

            if (canAttackNinja(body, other)) {
                if (body.getCSkill() == -1 && body.getSkills().size() > 0) {
                    body.setCSkill(body.getSkills().get(0).id);
                }
                final Skill skill = body.getMyCSkillObject();
                if (skill == null || other.get().isDie || other.get().getEffId(15) != null || other.get().getEffId(16) != null) {
                    return;
                }
                final Ninja[] arNinja = new Ninja[10];
                arNinja[0] = other;
                p.removeEffect(15);
                p.removeEffect(16);
                final SkillTemplates temp = body.getCSkillTemplate();

                if (body.mp < temp.manaUse) {
                    MessageSubCommand.sendMP((Ninja) body);
                    return;
                }

                final int rang = Integer.max(temp.dx, temp.dy) + 30;

                if (skill.coolDown > System.currentTimeMillis() || Math.abs(body.x - other.get().x) > rang || Math.abs(body.y - other.get().y) > rang) {
                    return;
                }

                body.upMP(-temp.manaUse);
                skill.coolDown = System.currentTimeMillis() + temp.coolDown;
                if (skill.id == 24) {
                    other.p.setEffect(18, 0, body.getPramSkill(55) * 1000, 0);
                    return;
                }
                if (skill.id == 42) {
                    this.setXYPlayers(other.get().x, other.get().y, p, other.p);
                    other.p.setEffect(18, 0, 5000, 0);
                }
                byte n = 1;
                try {
                    while (temp.maxFight > n) {
                        final int idn = m.reader().readInt();
                        final Ninja nj2 = this.getNinja(idn);
                        if (nj2 != null && !nj2.isDie && nj2.getEffId(15) == null && other.get().id != body.id && nj2.id != body.id && Math.abs(other.get().x - nj2.x) <= temp.dx) {
                            if (Math.abs(other.get().y - nj2.y) > temp.dy) {
                                continue;
                            }
                            if (nj2.getTypepk() == 3 || body.getTypepk() == 3 || (body.getTypepk() == 1 && nj2.getTypepk() == 1 || nj2.getTypepk() == PK_TRANG || nj2.getTypepk() == PK_DEN)) {
                                arNinja[n] = nj2;
                            }
                            ++n;
                        }
                    }
                } catch (IOException ex) {
                }
                m = new Message(61);
                m.writer().writeInt(body.id);
                m.writer().writeByte(skill.id);
                for (byte i = 0; i < arNinja.length; ++i) {
                    final Ninja nj3 = arNinja[i];
                    if (nj3 != null) {
                        m.writer().writeInt(nj3.id);
                    }
                }
                m.writer().flush();
                this.sendMyMessage(p, m, body.isNhanban);
                m.cleanup();
                for (byte i = 0; i < arNinja.length; ++i) {
                    final Ninja nj4 = arNinja[i];
                    if (nj4 != null) {
                        final int oldhp = nj4.hp;
                        skillEffect(body, other);
                        body.damage(other);

                        if (nj4.isDie) {
                            if (body.getTypepk() == PK_DOSAT) {
                                body.updatePk(1);
                            }
                            if (p.nj.addCuuSat) {
                                p.removeCuuSat(nj4);
                                p.nj.get().updatePk(2);
                                nj4.p.removeCuuSat(p.nj);
                                nj4.p.sendYellowMessage("Bạn bị " + p.nj.name + " đánh trọng thương.");
                            }

                            if (battle != null) {
                                battle.updateBattler(body.c, nj4.isHuman, nj4);
                            } else if (candyBattle != null) {
                                candyBattle.updateBattler(body.c, true, nj4);
                            }

                            final long num1 = Level.getMaxExp(nj4.getLevel());
                            final long num2 = Level.getLevel(nj4.getLevel()).exps;
                            if (nj4.pk > 0) {
                                if (nj4.getExp() > num1) {
                                    nj4.expdown = 0L;
                                    final Ninja ninja1 = nj4;
                                    ninja1.setExp(ninja1.getExp() - num2 * (5 + nj4.pk) / 100L);
                                    if (nj4.getExp() < num1) {
                                        nj4.setExp(num1);
                                    }
                                } else {
                                    nj4.setExp(num1);
                                    final Ninja ninja2 = nj4;
                                    ninja2.expdown += num2 * (5 + nj4.pk) / 100L;
                                    if (nj4.expdown > num2 * 50L / 100L) {
                                        nj4.expdown = num2 * 50L / 100L;
                                    }
                                }
                                nj4.updatePk(-1);
                            }
                            nj4.type = 14;
                            this.sendDie(nj4);
                        }
                    }
                }
            }
        }
        p.nj.setTimeKickSession();
    }

    public void wakeUpDieReturn(final @Nullable User p) throws IOException {
        if (p == null) {
            return;
        }
        if (!p.nj.isDie || this.map.isLangCo() || p.nj.getCurrentMapId() == 111 || p.nj.getCurrentMapId() == 110) {
            return;
        }
        if (p.luong < 1) {
            p.session.sendMessageLog("Bạn không có đủ 1 lượng!");
            return;
        }
        p.nj.get().isDie = false;
        p.luongMessage(-1L);
        p.nj.get().hp = p.nj.get().getMaxHP();
        p.nj.get().mp = p.nj.get().getMaxMP();
        p.liveFromDead();
    }

    public void sendDie(final @Nullable Ninja c) throws IOException {
        if (c == null) {
            return;
        }
        if (c.get().getExp() > Level.getMaxExp(c.get().getLevel())) {
            final Message m = new Message(-11);
            m.writer().writeByte(c.get().pk);
            m.writer().writeShort(c.get().x);
            m.writer().writeShort(c.get().y);
            m.writer().writeLong(c.get().getExp());
            m.writer().flush();
            c.p.sendMessage(m);
            m.cleanup();
        } else {
            c.get().setExp(Level.getMaxExp(c.get().getLevel()));
            final Message m = new Message(72);
            m.writer().writeByte(c.get().pk);
            m.writer().writeShort(c.get().x);
            m.writer().writeShort(c.get().y);
            m.writer().writeLong(c.get().expdown);
            m.writer().flush();
            c.p.sendMessage(m);
            m.cleanup();
        }
        final Message m = new Message(0);
        m.writer().writeInt(c.get().id);
        m.writer().writeByte(c.get().pk);
        m.writer().writeShort(c.get().x);
        m.writer().writeShort(c.get().y);
        m.writer().flush();
        this.sendMyMessage(c.p, m);
        m.cleanup();
    }

    public void DieReturn(final @Nullable User p) throws IOException {
        if (p == null) {
            return;
        }

        this.leave(p);
        p.nj.get().isDie = false;

        Map ma = null;
        Place openedArea = null;
        if (this.candyBattle != null) {
            if (p.nj.getTypepk() == PK_TRANG) {
                resetDieReturn(p, this.candyBattle.getOpenMaps().get(CandyBattle.KEO_DEN_ID));
            } else if (p.nj.getTypepk() == PK_DEN) {
                resetDieReturn(p, this.candyBattle.getOpenMaps().get(CandyBattle.KEO_TRANG_ID));
            }
            return;
        }
        if (this.battle != null && p.nj.getTypepk() != Constants.PK_NORMAL) {
            if (map.isGtcMap()) {
                openedArea = p.nj.getPhe() == PK_TRANG ? p.nj.getClanBattle().openedMaps.get(BAO_DANH_GT_BACH) : p.nj.getClanBattle().openedMaps.get(BAO_DANH_GT_HAC);
            } else {
                ma = p.nj.getPhe() == PK_TRANG ? Manager.getMapid(CAN_CU_DIA_BACH) : Manager.getMapid(CAN_CU_DIA_HAC);
            }
        } else if (this.map.cave != null) {
            ma = this.map.cave.map[0];
        } else if (map.tta != null) {
            ma = map.tta.map[0];
        } else {
            ma = Manager.getMapid(p.nj.mapLTD);
            /*   int[] mapLTD = new int[]{10, 17, 32, 38, 43, 48};
            int[] taskId = new int[]{15, 34, 15, 26, 30, 15};
            if (p.nj.mapLTD == mapLTD[0] && p.nj.getTaskId() < taskId[0]) {
                p.session.sendMessageLog("Bạn chưa thể đến khu vực này.Hãy hoàn thành nhiệm vụ trước.");
                ma = Manager.getMapid(72);;
            } else if (p.nj.mapLTD == mapLTD[1] && p.nj.getTaskId() < taskId[1]) {
                p.session.sendMessageLog("Bạn chưa thể đến khu vực này.Hãy hoàn thành nhiệm vụ trước.");
                ma = Manager.getMapid(72);;
            } else if (p.nj.mapLTD == mapLTD[2] && p.nj.getTaskId() < taskId[2]) {
                p.session.sendMessageLog("Bạn chưa thể đến khu vực này.Hãy hoàn thành nhiệm vụ trước.");
                ma = Manager.getMapid(72);;
            } else if (p.nj.mapLTD == mapLTD[3] && p.nj.getTaskId() < taskId[3]) {
                p.session.sendMessageLog("Bạn chưa thể đến khu vực này.Hãy hoàn thành nhiệm vụ trước.");
                ma = Manager.getMapid(72);;
            } else if (p.nj.mapLTD == mapLTD[4] && p.nj.getTaskId() < taskId[4]) {
                p.session.sendMessageLog("Bạn chưa thể đến khu vực này.Hãy hoàn thành nhiệm vụ trước.");
                ma = Manager.getMapid(72);
            } else if (p.nj.mapLTD == mapLTD[5] && p.nj.getTaskId() < taskId[5]) {
                p.session.sendMessageLog("Bạn chưa thể đến khu vực này.Hãy hoàn thành nhiệm vụ trước.");
                ma = Manager.getMapid(72);
            } else {
                ma = Manager.getMapid(p.nj.mapLTD);
            } */
        }

        if (map.isLdgtMap() && (p.getClanTerritoryData() != null && p.getClanTerritoryData().getClanTerritory() != null)) {
            val area = p.getClanTerritoryData().getClanTerritory().openedMap.get(80);
            if (area != null) {
                resetDieReturn(p, area);
                return;
            }
        }
        if (map.isGtcMap() && p.nj.getPhe() != PK_NORMAL) {
            resetDieReturn(p, openedArea);
            return;
        }

        if (ma != null) {
            for (final Place area : ma.area) {
                if (area.getNumplayers() < ma.template.maxplayers) {
                    resetDieReturn(p, area);
                    return;
                }
            }
        }
    }

    private void resetDieReturn(final @Nullable User p, final @Nullable Place area) throws IOException {
        if (p == null || area == null) {
            return;
        }

        area.EnterMap0(p.nj);
        p.nj.get().hp = p.nj.get().getMaxHP();
        p.nj.get().mp = p.nj.get().getMaxMP();
        Message m = new Message(-30);
        m.writer().writeByte(-123);
        m.writer().writeInt(p.nj.xu);
        m.writer().writeInt(p.nj.yen);
        m.writer().writeInt(p.luong);
        m.writer().writeInt(p.nj.get().getMaxHP());
        m.writer().writeInt(p.nj.get().getMaxMP());
        m.writer().writeByte(0);
        m.writer().flush();
        p.sendMessage(m);
        m.cleanup();
        m = new Message(57);
        m.writer().flush();
        p.sendMessage(m);
        m.cleanup();
    }

    public void attackNinja(final int dame, final int nid) throws IOException {
        final Ninja n = this.getNinja(nid);
        final Message m = new Message(62);
        m.writer().writeInt(nid);
        m.writer().writeInt(n.hp);
        m.writer().writeInt(dame);
        m.writer().writeInt(n.mp);
        m.writer().writeInt(0);
        m.writer().flush();
        this.sendMessage(m);
        m.cleanup();
    }

    @SneakyThrows
    public void sendFatalMessage(int dame, @Nullable final Ninja victim) {
        if (victim == null) {
            return;
        }

        val m = new Message(62);
        m.writer().writeInt(victim.id);
        m.writer().writeInt(victim.hp);
        m.writer().writeInt(-Math.abs(dame));
        m.writer().writeInt(victim.mp);
        m.writer().writeInt(0);
        m.writer().flush();
        sendMessage(m);
        m.cleanup();

    }

    private void DisableMobMessage(final User p, final int mobid, final int type) {
        try {
            final Mob mob = this.getMob(mobid);
            switch (type) {
                case -1: {
                    mob.isDisable = false;
                    break;
                }
                case 0: {
                    mob.isDisable = true;
                    mob.timeDisable = System.currentTimeMillis() + 1000 * p.nj.getPramSkill(48);
                    break;
                }
            }
            final Message m = new Message(85);
            m.writer().writeByte(mobid);
            m.writer().writeBoolean(mob.isDisable);
            m.writer().flush();
            this.sendMessage(m);
            m.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void DontMoveMobMessage(final User p, final int mobid, final int type) {
        try {
            final Mob mob = this.getMob(mobid);
            switch (type) {
                case -1: {
                    mob.isDontMove = false;
                    break;
                }
                case 0: {
                    mob.isDontMove = true;
                    mob.timeDontMove = System.currentTimeMillis() + 1000 * p.nj.getPramSkill(55) + p.nj.getPramSkill(62);
                    break;
                }
            }
            final Message m = new Message(86);
            m.writer().writeByte(mobid);
            m.writer().writeBoolean(mob.isDontMove);
            m.writer().flush();
            this.sendMessage(m);
            m.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void FireMobMessage(final int mobid, final int type) {
        try {
            final Mob mob = this.getMob(mobid);
            if (mob == null) {
                return;
            }
            switch (type) {
                case -1: {
                    mob.isFire = false;
                    break;
                }
                case 0: {
                    mob.isFire = true;
                    mob.timeFire = System.currentTimeMillis() + 2000L;
                    break;
                }
                case 1: {
                    mob.isFire = true;
                    mob.timeFire = System.currentTimeMillis() + 4000L;
                    break;
                }
            }
            final Message m = new Message(89);
            m.writer().writeByte(mobid);
            m.writer().writeBoolean(mob.isFire);
            m.writer().flush();
            this.sendMessage(m);
            m.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void IceMobMessage(final int mobid, final int type) {
        try {
            final Mob mob = this.getMob(mobid);
            if (mob == null) {
                return;
            }
            switch (type) {
                case -1: {
                    mob.isIce = false;
                    break;
                }
                case 0: {
                    mob.isIce = true;
                    mob.timeIce = System.currentTimeMillis() + 1500L;
                    break;
                }
                case 1: {
                    mob.isIce = true;
                    mob.timeIce = System.currentTimeMillis() + 3000L;
                    break;
                }

            }
            final Message m = new Message(90);
            m.writer().writeByte(mobid);
            m.writer().writeBoolean(mob.isIce);
            m.writer().flush();
            this.sendMessage(m);
            m.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void WindMobMessage(final int mobid, final int type) {
        try {
            final Mob mob = this.getMob(mobid);
            if (mob == null) {
                return;
            }
            switch (type) {
                case -1: {
                    mob.isWind = false;
                    break;
                }
                case 0: {
                    mob.isWind = true;
                    mob.timeWind = System.currentTimeMillis() + 1000L;
                    break;
                }
                case 1: {
                    mob.isWind = true;
                    mob.timeWind = System.currentTimeMillis() + 2000L;
                    break;
                }
            }
            final Message m = new Message(91);
            m.writer().writeByte(mobid);
            m.writer().writeBoolean(mob.isWind);
            m.writer().flush();
            this.sendMessage(m);
            m.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void FireNinjaMessage(final int ninjaId, final int type) {
        try {
            Ninja ninja = this.getNinja(ninjaId);
            if (ninja == null) {
                return;
            }

            long reduceTime = 0;
            try {
                reduceTime = ninja.get().getPramSkill(37) * 100 + ninja.get().getFireReduceTime();
            } catch (Exception e) {

            }
            long time = 0;
            switch (type) {
                case -1: {
                    break;
                }
                case 0: {
                    time = 2000L - reduceTime;
                    break;
                }
                case 1: {
                    time = 4000 - reduceTime;
                    break;
                }
                case 2: {
                    time = 5000 - reduceTime;
                    break;
                }
            }

            if (time > 0) {
                ninja.p.setEffect(5, 0, (int) time, 10);
                MessageSubCommand.sendEffectToOther(ninja, ninja.getEffId(5), this.getUsers(), -1, -1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void IceNinjaMessage(final int ninjaId, final int type) {
        try {
            Ninja ninja = this.getNinja(ninjaId);
            if (ninja == null) {
                return;
            }
            long reduceIceTime = 0;

            try {
                reduceIceTime = ninja.get().getPramSkill(38) * 100 + ninja.get().getIceReduceTime();
            } catch (Exception e) {

            }
            long time = 0;

            switch (type) {
                case -1: {
                    break;
                }
                case 0: {
                    time = 1000L - reduceIceTime;
                    break;
                }
                case 1: {
                    time = 3000L - reduceIceTime;
                    break;
                }
                case 2: {
                    time = 2000 - reduceIceTime;
                    break;
                }
                case 3: {
                    time = 5000 - reduceIceTime;
                    break;
                }
            }

            if (time > 0) {
                ninja.p.setEffect(6, 0, (int) time, 10);
                MessageSubCommand.sendEffectToOther(ninja, ninja.getEffId(6), this.getUsers(), ninja.x, ninja.y);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void WindNinjaMessage(final int ninjaId, final int type) {
        try {
            Ninja ninja = this.getNinja(ninjaId);
            if (ninja == null) {
                return;
            }
            long reduceTime = 0;
            try {
                reduceTime = ninja.get().getPramSkill(39) * 100 + ninja.get().getWindReduceTime();
            } catch (Exception e) {

            }
            long time = 0;
            switch (type) {
                case -1: {
                    break;
                }
                case 0: {
                    time = 1000L - reduceTime;
                    break;
                }
                case 1: {
                    time = 2000 - reduceTime;
                    break;
                }
                case 3: {
                    time = 5000 - reduceTime;
                    break;
                }
            }

            if (time > 0) {
                ninja.p.setEffect(7, 0, (int) time, 10);
                MessageSubCommand.sendEffectToOther(ninja, ninja.getEffId(7), this.getUsers(), -1, -1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadMobAttached(final int mobid) {
        synchronized (this) {
            try {
                final Mob mob = this.getMob(mobid);
                if (mob == null) {
                    return;
                }
                if (mob.isIce || mob.isWind || mob.isDisable) {
                    return;
                }
                long tFight = System.currentTimeMillis() + 1500L;
                if (mob.isIsboss()) {
                    tFight = System.currentTimeMillis() + 500L;
                }
                mob.timeFight = tFight;
                for (short i = 0; i < this.getUsers().size(); ++i) {
                    final User user = this.getUsers().get(i);
                    if (!user.nj.get().isDie && user.nj.get().getEffId(15) == null) {
                        if (user.nj.get().getEffId(16) == null) {
                            if (((user.nj.getMapId() == 99 || user.nj.getMapId() == 120) && user.nj.battleData.getPhe() == 4) || ((user.nj.getMapId() == 103 || user.nj.getMapId() == 124) && user.nj.battleData.getPhe() == 5)) {
                                return;
                            }
                            short dx = 80;
                            short dy = 2;
                            if (mob.templates.type > 3) {
                                dy = 80;
                            }
                            if (mob.isIsboss()) {
                                dx = 110;
                            }
                            if (user.session != null && mob.isFight(user.session.id)) {
                                dx = 200;
                                dy = 160;
                            }
                            if (Math.abs(user.nj.get().x - mob.x) < dx && Math.abs(user.nj.get().y - mob.y) < dy) {
                                int dame = mob.level * (mob.level / (3 + mob.level / 20));
                                if (this.map.cave != null && this.map.cave.finsh > 0 && this.map.getXHD() == 6) {
                                    final int dup = dame = dame * (10 * this.map.cave.finsh + 100) / 100;
                                }
                                if (mob.lvboss == 1) {
                                    dame *= 2;
                                } else if (mob.lvboss == 2) {
                                    dame *= 3;
                                } else if (mob.lvboss == 3) {
                                    dame *= 4;
                                }
                                if (mob.isIsboss()) {
                                    dame *= 4;
                                }
                                if (mob.sys == 1) {
                                    dame -= user.nj.get().ResFire();
                                } else if (mob.sys == 2) {
                                    dame -= user.nj.get().ResIce();
                                } else if (mob.sys == 3) {
                                    dame -= user.nj.get().ResWind();
                                }
                                dame -= user.nj.get().dameDown();
                                dame = util.nextInt(dame * 90 / 100, dame);
                                if (dame <= 0) {
                                }
                                int miss = user.nj.get().Miss();
                                if (miss > 7500) {
                                    miss = 7500;
                                }
                                if (miss > util.nextInt(10000)) {
                                    dame = 0;
                                }
                                if (mob.templates.id == 113) {
                                    dame = 0;
                                }
                                if (mob.isCallMob()) {
                                    dame = user.nj.getMaxHP() / 10;
                                }

                                int mpdown = 0;
                                if (user.nj.get().hp * 100 / user.nj.get().getMaxHP() > 10) {
                                    final Effect eff = user.nj.get().getEffId(10);
                                    if (eff != null) {
                                        final int mpold = user.nj.get().mp;
                                        user.nj.get().upMP(-(dame * eff.param / 100));
                                        dame -= (mpdown = mpold - user.nj.get().mp);
                                    }
                                }
                                dame = PERCENT_DAME_BOSS * dame / 100;

                                if (mob.templates.id == 72) {
                                    // Fire ninja
                                    if (user.nj.get().getEffId(5) == null && util.nextInt(0, 3) == 0) {
                                        FireNinjaMessage(user.nj.get().id, 2);
                                    }
                                } else if (mob.templates.id == 79) {
                                    // ICE
                                    if (user.nj.get().getEffId(6) == null && util.nextInt(0, 3) == 0) {
                                        IceNinjaMessage(user.nj.get().id, 3);
                                    }
                                } else if (mob.templates.id == 76) {
                                    // Reflect Dame
                                    if (util.nextInt(0, 3) == 0) {
                                        final int maxDame = user.nj.get().dameMax();
                                        user.nj.get().upHP(-util.nextInt(maxDame * 20 / 100, maxDame * 30 / 100));
                                        MessageSubCommand.sendHP(user.nj.get(), this.getUsers());
                                    }
                                } else if (mob.templates.id == 74) {
                                    // Wind
                                    if (user.nj.get().getEffId(7) == null && util.nextInt(0, 3) == 0) {
                                        WindNinjaMessage(user.nj.get().id, 3);
                                    }
                                }

                                if (user.nj.get().getEffId(5) != null) {
                                    dame *= 2;
                                }

                                if (user.nj.eff136 <= System.currentTimeMillis()) {//eff136
                                    dame -= dame * user.nj.getPramItem(136);
                                }

                                if (user.nj.get().nclass == KUNAI) {
                                    BuNhin buNhin = this.buNhins.stream().filter(b -> user.nj.get().id == b.ninjaId).findFirst().orElse(null);
                                    if (buNhin != null) {
                                        buNhin.upHP(-dame);
                                        this.MobAtkBuNhinMessage(mob.id, i, (short) (-1), (byte) (-1), (byte) (-1));
                                        break;
                                    } else {
                                        user.nj.get().upHP(-dame);
                                    }
                                } else {
                                    user.nj.get().upHP(-dame);
                                }

                                if (!mob.isIce && user.nj.get().nclass == KUNAI) {
                                    if (user.nj.get().percentIceKunai() >= util.nextInt(1, PERCENT_SKILL_MAX)) {
                                        IceMobMessage(mob.id, 0);
                                    }
                                }
                                this.MobAtkMessage(mob.id, user.nj, dame, mpdown, (short) (-1), (byte) (-1), (byte) (-1));
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean checkCleanMob(int mobId) {
        return this.getMobs().stream().parallel().filter(m -> m.templates.id == mobId)
                .allMatch(m -> m.isDie);
    }

    private void MobAtkMessage(final int mobid, final @Nullable Ninja n, final int dame, final int mpdown,
            final short idskill_atk, final byte typeatk, final byte typetool) throws IOException {
        for (int i = 0; i < getMobs().size(); i++) {
            Mob m = getMobs().get(i);
            if (m.templates.id == 113 || (m.isCallMob() && m.ninjaId != n.id)) {
                return;
            }
        }
        if (n == null) {
            return;
        }
        Message m = new Message(-3);
        m.writer().writeByte(mobid);
        m.writer().writeInt(dame);
        m.writer().writeInt(mpdown);
        m.writer().writeShort(idskill_atk);
        m.writer().writeByte(typeatk);
        m.writer().writeByte(typetool);
        m.writer().flush();
        n.p.sendMessage(m);
        m.cleanup();
        m = new Message(-2);
        m.writer().writeByte(mobid);
        m.writer().writeInt(n.id);
        m.writer().writeInt(dame);
        m.writer().writeInt(mpdown);
        m.writer().writeShort(idskill_atk);
        m.writer().writeByte(typeatk);
        m.writer().writeByte(typetool);
        m.writer().flush();
        this.sendMyMessage(n.p, m);
        if (n.isDie && !this.map.isLangCo()) {
            this.sendDie(n);
        }
    }

    private void loadMobMeAtk(@Nullable final Ninja n) {
        if (n == null) {
            return;
        }
        n.mobMe.timeFight = System.currentTimeMillis() + 3000L;
        try {
            if (n.mobAtk != -1 && (n.mobMe.templates.id >= 211 && n.mobMe.templates.id <= 217 || n.mobMe.templates.id == 70 || n.mobMe.templates.id == 122 || n.mobMe.templates.id == 229)) {
                final Mob mob = this.getMob(n.mobAtk);
                if (mob == null) {
                    return;
                }
                if (!mob.isDie && Math.abs(n.x - mob.x) < 150 && Math.abs(n.y - mob.y) < 150) {
                    val body = n.get();
                    val item = body.ItemBody[10];
                    int dame = item == null ? 500 : item.findParamById(ClanThanThu.ST_QUAI_ID);
                    if (n.mobMe.templates.id == 70) {
                        dame = 1000;
                    }
                    if (mob.level >= 70) {
                        n.p.updateExp(dame, true);
                    }
                    this.MobMeAtkMessage(n, mob.id, dame, (short) 40, (byte) 1, (byte) 1, (byte) 0);
                    mob.updateHP(-dame);
                    this.attackMob(dame, mob.id, false);
                } else {
                    n.mobAtk = -1;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void MobMeAtkMessage(final @Nullable Ninja n, final int idatk, final int dame, final short idskill_atk,
            final byte typeatk, final byte typetool, final byte type) throws IOException {

        if (n == null) {
            return;
        }
        final Message m = new Message(87);
        m.writer().writeInt(n.id);
        m.writer().writeByte(idatk);
        m.writer().writeShort(idskill_atk);
        m.writer().writeByte(typeatk);
        m.writer().writeByte(typetool);
        m.writer().writeByte(type);
        if (type == 1) {
            m.writer().writeInt(idatk);
        }
        m.writer().flush();
        n.p.sendMessage(m);
        m.cleanup();
    }

    public void openFindParty(@Nullable final User p) {
        if (p == null) {
            return;
        }
        try {
            final ArrayList<Party> partys = (ArrayList<Party>) this.getArryListParty();
            final Message m = new Message(-30);
            m.writer().writeByte(-77);
            for (int i = 0; i < partys.size(); ++i) {
                final Ninja n = partys.get(i).getNinja(partys.get(i).master);
                m.writer().writeByte(n.nclass);
                m.writer().writeByte(n.getLevel());
                m.writer().writeUTF(n.name);
                m.writer().writeByte(partys.get(i).ninjas.size());
            }
            m.writer().flush();
            p.sendMessage(m);
            m.cleanup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() throws Exception {
        synchronized (this) {

            synchronized (this._itemMap) {
                for (ItemMap itemMap : _itemMap) {
                    if (itemMap == null) {
                        continue;
                    }
                    if (itemMap.visible == false
                            && System.currentTimeMillis() > itemMap.nextTimeRefresh
                            && itemMap.removedelay == -1) {
                        itemMap.setVisible(true);
                    }
                }
            }
            val users = this.getUsers();
            for (int i = 0; i < users.size(); ++i) {
                try {
                    val user = this.getUsers().get(i);
                    if (user == null) {
                        continue;
                    }
                    if (user.nj.get().isDie) {
                        if (user.nj.isNhanban) {
                            user.nj.clone.off();
                        }
                        user.exitNhanBan(false);
//                  util.Debug("cho quay ve");
                    }
                    if (user.nj.getMapId() == 22 || user.nj.getMapId() == 1 || user.nj.getMapId() == 72) {
                        user.nj.changeTypePk((short) 0);
                    }
                    updateUser(user);
                    final Ninja ninja = user.nj;
                } catch (Exception e) {

                }
            }
            for (BuNhin buNhin : buNhins) {
                if (buNhin.expired()) {
                    removeBuNhin(buNhin);
                }
            }

            updateExpiredItemMap();
            for (int i = 0; i < this.getMobs().size(); ++i) {
                final Mob mob = this.getMobs().get(i);
                if (mob == null) {
                    continue;
                }
                mob.update(this);

                if (!map.isLdgtMap()
                        && mob.getTimeRefresh() > 0L
                        && System.currentTimeMillis() >= mob.getTimeRefresh()
                        && mob.isRefresh) {
                    this.refreshMob(mob.id);
                }

                if (mob.isDisable && System.currentTimeMillis() >= mob.timeDisable) {
                    this.DisableMobMessage(null, mob.id, -1);
                }
                if (mob.isDontMove && System.currentTimeMillis() >= mob.timeDontMove) {
                    this.DontMoveMobMessage(null, mob.id, -1);
                }
                if (mob.isFire && System.currentTimeMillis() >= mob.timeFire) {
                    this.FireMobMessage(mob.id, -1);
                }
                if (mob.isIce && System.currentTimeMillis() >= mob.timeIce) {
                    this.IceMobMessage(mob.id, -1);
                }
                if (mob.isWind && System.currentTimeMillis() >= mob.timeWind) {
                    this.WindMobMessage(mob.id, -1);
                }
                if (!mob.isDie && mob.status != 0 && mob.level != 1 && System.currentTimeMillis() >= mob.timeFight) {
                    this.loadMobAttached(mob.id);
                }

                if (this.map.tta != null && System.currentTimeMillis() <= this.map.tta.timeWait) {
                    if (map.id == 113 && mob.templates.id == 113) {
                        mob.status = 0;
                        mob.isDie = true;
                        mob.isRefresh = false;
                        mob.setTimeRefresh(-1);
                    }
                }
                if (mob.isCallMob() && mob.timeRemoveCallMob < System.currentTimeMillis()) {
                    this.killMob(mob.id);
                    this.MobStartDie(mob.hp, mob.id, true);
                    this.getMobs().remove(mob);
                }
            }

            if (this.map.cave != null && System.currentTimeMillis() > this.map.cave.time) {
                this.map.cave.rest();
            }
            if (this.map.cave != null && this.map.cave.level == this.map.cave.map.length) {
                this.map.cave.finsh();
            }

            if (this.map.tta != null && System.currentTimeMillis() > this.map.tta.time) {
                this.map.tta.rest();
            }
            if (this.map.tta != null && this.map.tta.lvTTA == 3) {
                this.map.tta.finsh();
            } else if (this.map.dun != null) {
                if (this.map.dun != null && this.map.dun.isStart && System.currentTimeMillis() > this.map.dun.time) {
                    this.map.dun.finish();
                }

                if (this.map.dun != null && System.currentTimeMillis() > this.map.dun.time) {
                    this.map.dun.rest();
                }
                if (this.map.dun != null && (this.map.dun.team2.size() < 1 || this.map.dun.team1.size() < 1) && !this.map.dun.isMap133) {
                    this.map.dun.check2();
                }

                if (this.map.dun != null && this.map.dun.isStart && !this.map.dun.rest) {
                    this.map.dun.check2();
                }
            }

            if (this.map.isLdgtMap()) {
                if (!recoverTa) {
                    if (this.checkCleanMob(this.map.getMobLdgtId())) {
                        final List<Mob> ldgtMobs = this.getMobs().stream().filter(m -> m.templates.id == this.map.getMobLdgtId())
                                .collect(Collectors.toList());
                        if (map.id != 80 && map.id != 90) {
                            val randomMob = ldgtMobs.get(util.nextInt(ldgtMobs.size()));
                            this.refreshMob(randomMob.id);
                            recoverTa = true;
                        }

                        this.getMobs().stream().filter(m -> m.templates.id == 81)
                                .forEach(m -> this.refreshMob(m.id));
                    }
                }

                if (map.id == 90 && checkCleanMob(BOST_LDGT_ID) && this.getUsers().size() > 0
                        && this.getUsers().get(0).getClanTerritoryData() != null
                        && this.getUsers().get(0).getClanTerritoryData().getClanTerritory() != null
                        && this.getUsers().get(0).getClanTerritoryData().getClanTerritory().getState() != ClanTerritory.State.WIN) {
                    this.getUsers().get(0).getClanTerritoryData().getClanTerritory().setState(ClanTerritory.State.WIN);
                }

            }
            val currentTime = System.currentTimeMillis();
            updateMission(currentTime);
        }
    }

    private void updateMission(long currentTime) throws IOException {
        /*if (currentTime > 0) {
            for (User user : getUsers()) {
                if (user == null || user.nj == null) continue;
                if ("Lồng đèn".equals(user.nj.name)) {
                    val ninjaAI = user.nj;
                    if (ninjaAI == null) continue;
                    if (ninjaAI.lastTimeMove == -1 ||
                            currentTime - ninjaAI.lastTimeMove >=
                                    util.nextInt(20 * TIME_CONTROL_MOVE / 100, TIME_CONTROL_MOVE)) {
                        final int masterId = ninjaAI.masterId;
                        final Ninja ninja = PlayerManager.getInstance().getNinja(masterId);
                        if (!ninjaAI.isDie) {
                            if (Math.abs(ninja.x - ninjaAI.x) > 10) {
                                ninjaAI.x = (short) (ninja.x + util.nextInt(-20, 20));
                                moveMessage(ninjaAI, ninjaAI.x, ninja.y);
                            }
                        } else {
                            if (ninja != null) {
                                ninja.p.sendYellowMessage("Nhiệm vụ thất bại do Lồng đèn được hộ tống kiệt sức");
                                leave(ninjaAI.p);
                            }
                        }
                    }
                }
            }

        } else */
        if (map.id == 33) {
            for (User user : getUsers()) {
                if (user == null || user.nj == null) {
                    continue;
                }
                if ("Jaian".equals(user.nj.name)) {
                    val ninjaAI = user.nj;
                    if (ninjaAI == null) {
                        continue;
                    }
                    if (ninjaAI.lastTimeMove == -1
                            || currentTime - ninjaAI.lastTimeMove
                            >= util.nextInt(20 * TIME_CONTROL_MOVE / 100, TIME_CONTROL_MOVE)) {
                        final int masterId = Math.abs(ninjaAI.id);
                        if (!ninjaAI.isDie) {
                            if (ninjaAI.x > 0) {
                                ninjaAI.x -= 50;
                                if (ninjaAI.x <= 0) {
                                    ninjaAI.x = 10;
                                }
                                moveMessage(ninjaAI, ninjaAI.x, this.map.template.npc[0].y);
                            }

                            if (ninjaAI.x >= 10 && ninjaAI.x <= 100) {
                                // Finish task
                                final Ninja ninja = PlayerManager.getInstance().getNinja(masterId);
                                if (ninja != null) {
                                    ninja.upMainTask();
                                    ninja.p.sendYellowMessage("Hoàn thành nhiệm vụ");
                                    leave(ninjaAI.p);
                                }
                                break;
                            }

                            final Ninja masterNinja = getUsers().stream().filter(u -> u != null && u.nj != null && u.nj.id == masterId)
                                    .map(p -> p.nj).findFirst().orElse(null);
                            if (masterNinja == null) {
                                // Khong tim thay ninja trong map
                                final Ninja nj = PlayerManager.getInstance().getNinja(masterId);
                                if (nj != null) {
                                    // Tim thay ninja map khac
                                    nj.p.sendYellowMessage("Nhiệm vụ thất bại do rời khỏi map của Jaian");
                                    sendMapInfo(nj.p, this);
                                }
                                leave(ninjaAI.p);
                            } else {
                                // Ninja di xa nhan vat ho tong
                                if (masterNinja != null && Math.abs(ninjaAI.x - masterNinja.x) >= 500) {
                                    masterNinja.p.sendYellowMessage("Đi quá xa trẻ lạc nhiệm vụ thất bại");
                                    leave(ninjaAI.p);
                                    sendMapInfo(masterNinja.p, this);
                                }
                            }

                        } else {
                            final Ninja ninja = PlayerManager.getInstance().getNinja(masterId);
                            if (ninja != null) {
                                ninja.p.sendYellowMessage("Nhiệm vụ thất bại do trẻ được hộ tống kiệt sức");
                                leave(ninjaAI.p);
                                sendMapInfo(ninja.p, this);
                            }
                        }
                    }
                }
            }

        } else if (map.id == 74) {
            if (getUsers().size() == 1) {
                val nj = getUsers().get(0);
                if (nj == null) {
                    return;
                }
                if (nj.expiredTime == -1 || currentTime >= nj.expiredTime) {
                    for (User user : getUsers()) {
                        gietHeoRungGoBack(user, "Nhiệm vụ thất bại do hết thời gian");
                    }
                } else if (_mobs.stream().allMatch(m -> m.isDie == true) && _itemMap.size() == 0) {
                    for (User user : getUsers()) {
                        gietHeoRungGoBack(user, "Hoàn thành nhiệm vụ");
                    }
                }
            } else if (getUsers().size() > 1) {
                for (User user : getUsers()) {

                    leave(user);
                }
            }
        } else if (map.id == 78) {
            if (getUsers().size() == 1) {
                val u = getUsers().get(0);
                if (u != null) {
                    if (u.expiredTime == -1 || currentTime >= u.expiredTime) {
                        val nj = u.nj;
                        nj.getPlace().gotoHaruna(u);
                        Service.batDauTinhGio(u, 0);
                        u.sendYellowMessage("Thời gian trong địa đạo đã hết");
                    } else if (_mobs.stream().allMatch(m -> m.isDie)) {
                        if (_itemMap.size() == 0 && !u.nj.hasItemInBag(232)) {
                            if (u.nj.getTaskId() < taskTemplates.length) {
                                val task = taskTemplates[u.nj.getTaskId()];
                                val index = util.nextInt(0, _mobs.size()) % _mobs.size();
                                final ItemMap itemMap = LeaveItem(task.getItemsPick()[u.nj.getTaskIndex()], _mobs.get(index).x, _mobs.get(index).y);
                                u.expiredTime = System.currentTimeMillis() + 50000;
                                u.sendYellowMessage("Bạn có 50 giây để tìm tấm địa đồ");
                                Service.batDauTinhGio(u, 50);
                            } else {
                                u.sendYellowMessage("Không làm rơi vật phẩm nhiệm vụ nv địa đạo");
                            }
                        }

                    }
                }
            } else if (getUsers().size() > 1) {
                for (User u : getUsers()) {
                    if (u != null) {
                        if (u.nj != null && u.nj.getPlace() != null) {
                            u.nj.getPlace().gotoHaruna(u);
                            Service.batDauTinhGio(u, 0);
                            u.sendYellowMessage("Thời gian trong địa đạo đã hết");
                        }
                    }
                }
            }
        }
    }

    public static final int BOST_LDGT_ID = 116;

    private boolean recoverTa = false;

    //<editor-fold desc="Update event loop">
//    @SneakyThrows
    private void updateUser(final @Nullable User p) throws IOException {
        if (p == null) {
            return;
        }
        for (byte i = 0; i < p.nj.ItemBag.length; i++) {
            Item item = p.nj.ItemBag[i];
            if (item != null && item.quantity < 0) {
                item.quantity = 1;
            }
        }

        updateEffect(p);
        if (p.nj.getPramItem(99) > 0 && p.nj.eff05buff <= System.currentTimeMillis()) {
            p.nj.eff05buff = System.currentTimeMillis() + 500L;
            p.nj.get().upHP(p.nj.getPramItem(99));
            p.nj.get().upMP(p.nj.getPramItem(99));
            MessageSubCommand.sendHP(p.nj.get(), getUsers());
            MessageSubCommand.sendMP(p.nj.get(), getUsers());
        }
        if ((p.nj.eff5buffHP() > 0 || p.nj.get().eff5buffMP() > 0) && p.nj.eff5buff <= System.currentTimeMillis()) {
            p.nj.eff5buff = System.currentTimeMillis() + 5000L;
            p.nj.get().upHP(p.nj.get().eff5buffHP());
            p.nj.get().upMP(p.nj.get().eff5buffMP());
            MessageSubCommand.sendHP(p.nj.get(), getUsers());
            MessageSubCommand.sendMP(p.nj.get(), getUsers());
        }

        if (p.nj.getPramItem(136) > 0 && p.nj.eff136 <= System.currentTimeMillis()) {
            p.nj.eff136 = System.currentTimeMillis() + 40000L;
            if (util.nextInt(1, 100) <= 10) {
                p.nj.time136 = System.currentTimeMillis() + 5000L;
                p.sendYellowMessage("Miễn thương thành công");
            }
        }
        Calendar time = Calendar.getInstance();
        int hour = time.get(11);
        int min = time.get(12);
        int sec = time.get(13);
        if (sec % 60 == 0) {
            p.nj.Time2H++;
            p.nj.timeOnline++;
        }
        //Hào quang
        if (p.nj.get().fullTL() == 7) {
            if (System.currentTimeMillis() > p.nj.delayEffect) {
                p.nj.delayEffect = System.currentTimeMillis() + 5000L;
                int tl = 0;
                switch (GameScr.SysClass(p.nj.nclass)) {
                    case 1: {
                        tl = 9;
                        break;
                    }
                    case 2: {
                        tl = 3;
                        break;
                    }
                    case 3: {
                        tl = 6;
                        break;
                    }
                }
                for (int k = 0; k < this.getUsers().size(); ++k) {
                    GameCanvas.addEffect(this.getUsers().get(k).session, (byte) 0, p.nj.get().id, (byte) tl, 1, 1);
                }
            }
        }
        
        if (p.nj.get().fullTL() == 8) {
            if (System.currentTimeMillis() > p.nj.delayEffect) {
                p.nj.delayEffect = System.currentTimeMillis() + 5000L;
                int tl = 0;
                switch (GameScr.SysClass(p.nj.nclass)) {
                    case 1: {
                        tl = 10;
                        break;
                    }
                    case 2: {
                        tl = 4;
                        break;
                    }
                    case 3: {
                        tl = 7;
                        break;
                    }
                }
                for (int k = 0; k < this.getUsers().size(); ++k) {
                    GameCanvas.addEffect(this.getUsers().get(k).session, (byte) 0, p.nj.get().id, (byte) tl, 1, 1);
                }
            }
        }
        if (p.nj.get().fullTL() == 9) {
            if (System.currentTimeMillis() > p.nj.delayEffect) {
                p.nj.delayEffect = System.currentTimeMillis() + 5000L;
                int tl = 0;
                switch (GameScr.SysClass(p.nj.nclass)) {
                    case 1: {
                        tl = 11;
                        break;
                    }
                    case 2: {
                        tl = 5;
                        break;
                    }
                    case 3: {
                        tl = 8;
                        break;
                    }
                }
                for (int k = 0; k < this.getUsers().size(); ++k) {
                    GameCanvas.addEffect(this.getUsers().get(k).session, (byte) 0, p.nj.get().id, (byte) tl, 1, 1);
                }
            }
        }

        if (System.currentTimeMillis() > p.nj.delayEffect2) {
            if (p.nj.get().getNgocEff() != 0) {
                p.nj.delayEffect2 = System.currentTimeMillis() + 5000L;
                for (int k = 0; k < this.getUsers().size(); ++k) {
                    if (p.nj.get().getNgocEff() >= 25) {
                        GameCanvas.addEffect(this.getUsers().get(k).session, (byte) 0, p.nj.get().id, (short) 5, 1, 1);
                        GameCanvas.addEffect(this.getUsers().get(k).session, (byte) 0, p.nj.get().id, (short) 11, 1, 1);
                        GameCanvas.addEffect(this.getUsers().get(k).session, (byte) 0, p.nj.get().id, (short) 8, 1, 1);                        
                    } else {
                        GameCanvas.addEffect(this.getUsers().get(k).session, (byte) 0, p.nj.get().id, (short) p.nj.get().getNgocEff(), 1, 1);
                    }
                }
            }
        }
        
        
        
//        int Eff = 0;
//        if (p.nj.get().ItemBody[1] != null && p.nj.get().ItemBody[1].id == 909) {
//            switch (GameScr.SysClass(p.nj.nclass)) {
//                case 1:
//                    Eff = 178;
//                    break;
//                case 2:
//                    Eff = 177;
//                    break;
//                case 3:
//                    Eff = 179;
//                    break;
//            }
//            for (int k = 0; k < this.getUsers().size(); ++k) {
//                GameCanvas.addEffect(this.getUsers().get(k).session, (byte) 0, p.nj.get().id, (byte) Eff, 1, 1);
//            }
//        }

        if (p.nj.time136 >= System.currentTimeMillis()) {
            for (int k = 0; k < this.getUsers().size(); ++k) {
                GameCanvas.addEffect(this.getUsers().get(k).session, (byte) 0, p.nj.get().id, (short) 73, 1, 1);
            }
        }
        //Danh hieu
//        if (p.nj.ItemBody[1] != null && p.nj.name.equals("admin")) {
//            for (int k = 0; k < this.getUsers().size(); ++k) {
//                GameCanvas.addEffect(this.getUsers().get(k).session, (byte) 0, p.nj.get().id, (short) 73, 1000, 1000);
//            }
//        }
        
        if (p.nj.ItemBody[1] != null && p.nj.name.equals("admin")) {
            for (int k = 0; k < this.getUsers().size(); ++k) {
                GameCanvas.addEffect(this.getUsers().get(k).session, (byte) 0, p.nj.get().id, (short) 77, -1,-1);
            }
        }
        
        if (p.nj.ItemBody[1] != null && p.nj.id == 20028) {
            for (int k = 0; k < this.getUsers().size(); ++k) {
              //  GameCanvas.addEffect(this.getUsers().get(k).session, (byte) 0, p.nj.get().id, (short) 232, 0, 0);
                GameCanvas.addEffect(this.getUsers().get(k).session, (byte) 0, p.nj.get().id, (short) 77, -1, -1);                
            }
        }
        if (p.nj.ItemBody[1] != null && p.nj.id == 6066) {
            for (int k = 0; k < this.getUsers().size(); ++k) {
              //  GameCanvas.addEffect(this.getUsers().get(k).session, (byte) 0, p.nj.get().id, (short) 232, 0, 0);
                GameCanvas.addEffect(this.getUsers().get(k).session, (byte) 0, p.nj.get().id, (short) 107, -1, -1);                
            }
        }
        if (p.nj.ItemBody[1] != null && p.nj.id == 2) {
            for (int k = 0; k < this.getUsers().size(); ++k) {
              //  GameCanvas.addEffect(this.getUsers().get(k).session, (byte) 0, p.nj.get().id, (short) 232, 0, 0);
                GameCanvas.addEffect(this.getUsers().get(k).session, (byte) 0, p.nj.get().id, (short) 77, -1, -1);                
            }
        }
        if (p.nj.ItemBody[1] != null && p.nj.id == 1) {
            for (int k = 0; k < this.getUsers().size(); ++k) {
              //  GameCanvas.addEffect(this.getUsers().get(k).session, (byte) 0, p.nj.get().id, (short) 232, 0, 0);
                GameCanvas.addEffect(this.getUsers().get(k).session, (byte) 0, p.nj.get().id, (short) 77, -1, -1);                
            }
        }       
        if (p.nj.ItemBody[1] != null && p.nj.id == 6005) {
            for (int k = 0; k < this.getUsers().size(); ++k) {
              //  GameCanvas.addEffect(this.getUsers().get(k).session, (byte) 0, p.nj.get().id, (short) 232, 0, 0);
                GameCanvas.addEffect(this.getUsers().get(k).session, (byte) 0, p.nj.get().id, (short) 107, -1, -1);                
            }
        }         
        if (p.nj.ItemBody[1] != null && p.nj.id == 20658) {
            for (int k = 0; k < this.getUsers().size(); ++k) {
              //  GameCanvas.addEffect(this.getUsers().get(k).session, (byte) 0, p.nj.get().id, (short) 232, 0, 0);
                GameCanvas.addEffect(this.getUsers().get(k).session, (byte) 0, p.nj.get().id, (short) 77, -1, -1);                
            }
        }
//        if (p.nj.ItemBody[1] != null &&p.nj.id == 243) {
//            for (int k = 0; k < this.getUsers().size(); ++k) {
//               // G//ameCanvas.addEffect(this.getUsers().get(k).session, (byte) 0, p.nj.get().id, (short) 226, 1, 1);
//                GameCanvas.addEffect(this.getUsers().get(k).session, (byte) 0, p.nj.get().id, (short) 105, -1,-1);
//            }
//        }
//        
//        if (p.nj.ItemBody[1] != null && p.nj.id == 9) {
//            for (int k = 0; k < this.getUsers().size(); ++k) {
//               // GameCanvas.addEffect(this.getUsers().get(k).session, (byte) 0, p.nj.get().id, (short) 230, 1, 1);
//                GameCanvas.addEffect(this.getUsers().get(k).session, (byte) 0, p.nj.get().id, (short) 101, -1, -1);                
//            }
//        }

        for (byte j = 0; j < p.nj.get().ItemBody.length; j++) {
            Item item = p.nj.get().ItemBody[j];
            if (item != null && item.id == 906) {
                for (int k = this.getUsers().size() - 1; k >= 0; k--) {;
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 106, 1000, 10);
      //              GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 87, 0, 600);
                }
            }
        }
        
        for (byte j = 0; j < p.nj.get().ItemBody.length; j++) {
            Item item = p.nj.get().ItemBody[j];
            if (item != null && item.id == 907) {
                for (int k = this.getUsers().size() - 1; k >= 0; k--) {;
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 105, 1000, 10);
      //              GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 87, 0, 600);
                }
            }
        }
        
        for (byte j = 0; j < p.nj.get().ItemBody.length; j++) {
            Item item = p.nj.get().ItemBody[j];
            if (item != null && item.id == 908) {
                for (int k = this.getUsers().size() - 1; k >= 0; k--) {;
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 99, 1000, 10);
      //              GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 87, 0, 600);
                }
            }
        }

        for (byte j = 0; j < p.nj.get().ItemBody.length; j++) {
            Item item = p.nj.get().ItemBody[j];
            if (item != null && item.id == 897) {
                for (int k = this.getUsers().size() - 1; k >= 0; k--) {;
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 116, 0, 600);
      //              GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 87, 0, 600);
                }
            }
        }
        for (byte j = 0; j < p.nj.get().ItemBody.length; j++) {
            Item item = p.nj.get().ItemBody[j];
            if (item != null && item.id == 898) {
                for (int k = this.getUsers().size() - 1; k >= 0; k--) {;
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 116, 0, 600);
      //              GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 87, 0, 600);
                }
            }
        }
        for (byte j = 0; j < p.nj.get().ItemBody.length; j++) {
            Item item = p.nj.get().ItemBody[j];
            if (item != null && item.id == 899) {
                for (int k = this.getUsers().size() - 1; k >= 0; k--) {;
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 116, 0, 600);
      //              GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 87, 0, 600);
                }
            }
        }
        for (byte j = 0; j < p.nj.get().ItemBody.length; j++) {
            Item item = p.nj.get().ItemBody[j];
            if (item != null && item.id == 900) {
                for (int k = this.getUsers().size() - 1; k >= 0; k--) {;
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 116, 0, 600);
      //              GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 87, 0, 600);
                }
            }
        }
        for (byte j = 0; j < p.nj.get().ItemBody.length; j++) {
            Item item = p.nj.get().ItemBody[j];
            if (item != null && item.id == 901) {
                for (int k = this.getUsers().size() - 1; k >= 0; k--) {;
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 116, 0, 600);
      //              GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 87, 0, 600);
                }
            }
        }
        for (byte j = 0; j < p.nj.get().ItemBody.length; j++) {
            Item item = p.nj.get().ItemBody[j];
            if (item != null && item.id == 902) {
                for (int k = this.getUsers().size() - 1; k >= 0; k--) {;
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 116, 0, 600);
      //              GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 87, 0, 600);
                }
            }
        }
        
        //VT1
        for (byte j = 0; j < p.nj.get().ItemBody.length; j++) {
            Item item = p.nj.get().ItemBody[j];
            if (item != null && item.id == 843) {
                for (int k = this.getUsers().size() - 1; k >= 0; k--) {;
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 78, 0, 600);
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 87, 0, 600);
                }
            }
        }
        for (byte j = 0; j < p.nj.get().ItemBody.length; j++) {
            Item item = p.nj.get().ItemBody[j];
            if (item != null && item.id == 853) {
                for (int k = 0; k < this.getUsers().size(); ++k) {
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 78, 0, 600);
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 87, 0, 600);
                }
            }
        }
        for (byte j = 0; j < p.nj.get().ItemBody.length; j++) {
            Item item = p.nj.get().ItemBody[j];
            if (item != null && item.id == 862) {
                for (int k = 0; k < this.getUsers().size(); ++k) {
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 78, 0, 600);
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 87, 0, 600);
                }
            }
        }
        for (byte j = 0; j < p.nj.get().ItemBody.length; j++) {
            Item item = p.nj.get().ItemBody[j];
            if (item != null && item.id == 871) {
                for (int k = 0; k < this.getUsers().size(); ++k) {
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 78, 0, 600);
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 87, 0, 600);
                }
            }
        }
        //VT2
        for (byte j = 0; j < p.nj.get().ItemBody.length; j++) {
            Item item = p.nj.get().ItemBody[j];
            if (item != null && item.id == 844) {
                for (int k = 0; k < this.getUsers().size(); ++k) {
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 79, 0, 600);
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 88, 0, 600);
                }
            }
        }
        for (byte j = 0; j < p.nj.get().ItemBody.length; j++) {
            Item item = p.nj.get().ItemBody[j];
            if (item != null && item.id == 854) {
                for (int k = 0; k < this.getUsers().size(); ++k) {
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 79, 0, 600);
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 88, 0, 600);
                }
            }
        }
        for (byte j = 0; j < p.nj.get().ItemBody.length; j++) {
            Item item = p.nj.get().ItemBody[j];
            if (item != null && item.id == 863) {
                for (int k = 0; k < this.getUsers().size(); ++k) {
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 79, 0, 600);
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 88, 0, 600);
                }
            }
        }
        for (byte j = 0; j < p.nj.get().ItemBody.length; j++) {
            Item item = p.nj.get().ItemBody[j];
            if (item != null && item.id == 872) {
                for (int k = 0; k < this.getUsers().size(); ++k) {
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 79, 0, 600);
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 88, 0, 600);
                }
            }
        }
        //VT3
        for (byte j = 0; j < p.nj.get().ItemBody.length; j++) {
            Item item = p.nj.get().ItemBody[j];
            if (item != null && item.id == 845) {
                for (int k = 0; k < this.getUsers().size(); ++k) {
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 80, 0, 600);
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 89, 0, 600);
                }
            }
        }
        for (byte j = 0; j < p.nj.get().ItemBody.length; j++) {
            Item item = p.nj.get().ItemBody[j];
            if (item != null && item.id == 855) {
                for (int k = 0; k < this.getUsers().size(); ++k) {
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 80, 0, 600);
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 89, 0, 600);
                }
            }
        }
        for (byte j = 0; j < p.nj.get().ItemBody.length; j++) {
            Item item = p.nj.get().ItemBody[j];
            if (item != null && item.id == 864) {
                for (int k = 0; k < this.getUsers().size(); ++k) {
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 80, 0, 600);
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 89, 0, 600);
                }
            }
        }
        for (byte j = 0; j < p.nj.get().ItemBody.length; j++) {
            Item item = p.nj.get().ItemBody[j];
            if (item != null && item.id == 873) {
                for (int k = 0; k < this.getUsers().size(); ++k) {
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 80, 0, 600);
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 89, 0, 600);
                }
            }
        }
        //VT4
        for (byte j = 0; j < p.nj.get().ItemBody.length; j++) {
            Item item = p.nj.get().ItemBody[j];
            if (item != null && item.id == 846) {
                for (int k = 0; k < this.getUsers().size(); ++k) {
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 81, 0, 600);
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 90, 0, 600);
                }
            }
        }
        for (byte j = 0; j < p.nj.get().ItemBody.length; j++) {
            Item item = p.nj.get().ItemBody[j];
            if (item != null && item.id == 856) {
                for (int k = 0; k < this.getUsers().size(); ++k) {
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 81, 0, 600);
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 90, 0, 600);
                }
            }
        }
        for (byte j = 0; j < p.nj.get().ItemBody.length; j++) {
            Item item = p.nj.get().ItemBody[j];
            if (item != null && item.id == 865) {
                for (int k = 0; k < this.getUsers().size(); ++k) {
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 81, 0, 600);
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 90, 0, 600);
                }
            }
        }
        for (byte j = 0; j < p.nj.get().ItemBody.length; j++) {
            Item item = p.nj.get().ItemBody[j];
            if (item != null && item.id == 874) {
                for (int k = 0; k < this.getUsers().size(); ++k) {
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 81, 0, 600);
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 90, 0, 600);
                }
            }
        }
        //VT5
        for (byte j = 0; j < p.nj.get().ItemBody.length; j++) {
            Item item = p.nj.get().ItemBody[j];
            if (item != null && item.id == 847) {
                for (int k = 0; k < this.getUsers().size(); ++k) {
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 82, 0, 600);
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 91, 0, 600);
                }
            }
        }
        for (byte j = 0; j < p.nj.get().ItemBody.length; j++) {
            Item item = p.nj.get().ItemBody[j];
            if (item != null && item.id == 857) {
                for (int k = 0; k < this.getUsers().size(); ++k) {
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 82, 0, 600);
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 91, 0, 600);
                }
            }
        }
        for (byte j = 0; j < p.nj.get().ItemBody.length; j++) {
            Item item = p.nj.get().ItemBody[j];
            if (item != null && item.id == 866) {
                for (int k = 0; k < this.getUsers().size(); ++k) {
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 82, 0, 600);
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 91, 0, 600);
                }
            }
        }
        for (byte j = 0; j < p.nj.get().ItemBody.length; j++) {
            Item item = p.nj.get().ItemBody[j];
            if (item != null && item.id == 875) {
                for (int k = 0; k < this.getUsers().size(); ++k) {
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 82, 0, 600);
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 91, 0, 600);
                }
            }
        }
        //VT6
        for (byte j = 0; j < p.nj.get().ItemBody.length; j++) {
            Item item = p.nj.get().ItemBody[j];
            if (item != null && item.id == 848) {
                for (int k = 0; k < this.getUsers().size(); ++k) {
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 83, 0, 600);
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 92, 0, 600);
                }
            }
        }
        for (byte j = 0; j < p.nj.get().ItemBody.length; j++) {
            Item item = p.nj.get().ItemBody[j];
            if (item != null && item.id == 858) {
                for (int k = 0; k < this.getUsers().size(); ++k) {
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 83, 0, 600);
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 92, 0, 600);
                }
            }
        }
        for (byte j = 0; j < p.nj.get().ItemBody.length; j++) {
            Item item = p.nj.get().ItemBody[j];
            if (item != null && item.id == 867) {
                for (int k = 0; k < this.getUsers().size(); ++k) {
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 83, 0, 600);
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 92, 0, 600);
                }
            }
        }
        for (byte j = 0; j < p.nj.get().ItemBody.length; j++) {
            Item item = p.nj.get().ItemBody[j];
            if (item != null && item.id == 876) {
                for (int k = 0; k < this.getUsers().size(); ++k) {
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 83, 0, 600);
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 92, 0, 600);
                }
            }
        }
        //VT7
        for (byte j = 0; j < p.nj.get().ItemBody.length; j++) {
            Item item = p.nj.get().ItemBody[j];
            if (item != null && item.id == 849) {
                for (int k = 0; k < this.getUsers().size(); ++k) {
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 84, 0, 600);
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 93, 0, 600);
                }
            }
        }
        for (byte j = 0; j < p.nj.get().ItemBody.length; j++) {
            Item item = p.nj.get().ItemBody[j];
            if (item != null && item.id == 859) {
                for (int k = 0; k < this.getUsers().size(); ++k) {
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 84, 0, 600);
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 93, 0, 600);
                }
            }
        }
        for (byte j = 0; j < p.nj.get().ItemBody.length; j++) {
            Item item = p.nj.get().ItemBody[j];
            if (item != null && item.id == 868) {
                for (int k = 0; k < this.getUsers().size(); ++k) {
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 84, 0, 600);
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 93, 0, 600);
                }
            }
        }
        for (byte j = 0; j < p.nj.get().ItemBody.length; j++) {
            Item item = p.nj.get().ItemBody[j];
            if (item != null && item.id == 877) {
                for (int k = 0; k < this.getUsers().size(); ++k) {
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 84, 0, 600);
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 93, 0, 600);
                }
            }
        }
        //VT8
        for (byte j = 0; j < p.nj.get().ItemBody.length; j++) {
            Item item = p.nj.get().ItemBody[j];
            if (item != null && item.id == 850) {
                for (int k = 0; k < this.getUsers().size(); ++k) {
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 85, 0, 600);
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 94, 0, 600);
                }
            }
        }
        for (byte j = 0; j < p.nj.get().ItemBody.length; j++) {
            Item item = p.nj.get().ItemBody[j];
            if (item != null && item.id == 860) {
                for (int k = 0; k < this.getUsers().size(); ++k) {
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 85, 0, 600);
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 94, 0, 600);
                }
            }
        }
        for (byte j = 0; j < p.nj.get().ItemBody.length; j++) {
            Item item = p.nj.get().ItemBody[j];
            if (item != null && item.id == 869) {
                for (int k = 0; k < this.getUsers().size(); ++k) {
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 85, 0, 600);
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 94, 0, 600);
                }
            }
        }
        for (byte j = 0; j < p.nj.get().ItemBody.length; j++) {
            Item item = p.nj.get().ItemBody[j];
            if (item != null && item.id == 878) {
                for (int k = 0; k < this.getUsers().size(); ++k) {
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 85, 0, 600);
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 94, 0, 600);
                }
            }
        }
        //VT9
        for (byte j = 0; j < p.nj.get().ItemBody.length; j++) {
            Item item = p.nj.get().ItemBody[j];
            if (item != null && item.id == 851) {
                for (int k = 0; k < this.getUsers().size(); ++k) {
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 86, 0, 600);
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 95, 0, 600);
                }
            }
        }
        for (byte j = 0; j < p.nj.get().ItemBody.length; j++) {
            Item item = p.nj.get().ItemBody[j];
            if (item != null && item.id == 861) {
                for (int k = 0; k < this.getUsers().size(); ++k) {
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 86, 0, 600);
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 95, 0, 600);
                }
            }
        }
        for (byte j = 0; j < p.nj.get().ItemBody.length; j++) {
            Item item = p.nj.get().ItemBody[j];
            if (item != null && item.id == 870) {
                for (int k = 0; k < this.getUsers().size(); ++k) {
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 86, 0, 600);
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 95, 0, 600);
                }
            }
        }
        for (byte j = 0; j < p.nj.get().ItemBody.length; j++) {
            Item item = p.nj.get().ItemBody[j];
            if (item != null && item.id == 879) {
                for (int k = 0; k < this.getUsers().size(); ++k) {
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 86, 0, 600);
                    GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 95, 0, 600);
                }
            }
        }
        for (byte j = 0; j < p.nj.get().ItemBody.length; j++) {
            Item item = p.nj.get().ItemBody[j];
            if (item != null && item.id == 905) {
                if (p.nj.nclass == 1 || p.nj.nclass == 2) {
                    for (int k = this.getUsers().size() - 1; k >= 0; k--) {;
                        GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 178, 1000, 1000);
                    }
                }
                if (p.nj.nclass == 3 || p.nj.nclass == 4) {
                    for (int k = this.getUsers().size() - 1; k >= 0; k--) {;
                        GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 177, 1000, 1000);
                    }
                }
                if (p.nj.nclass == 5 || p.nj.nclass == 6) {
                    for (int k = this.getUsers().size() - 1; k >= 0; k--) {;
                        GameCanvas.addEffect((this.getUsers().get(k)).session, (byte) 0, p.nj.get().id, (short) 179, 1000, 1000);
                    }
                }
            }
        }

        // Hao Quang
        if (p.nj.ItemBodyHide[0] != null) {
            if (p.nj.ItemBodyHide[0].getUpgrade() >= 7) {
                Item im = p.nj.ItemBodyHide[0];
                switch (p.nj.ItemBodyHide[0].id) {
                    case 774: {
                        for (int k = 0; k < this.getUsers().size(); ++k) {
                            if (im.getUpgrade() < 10) {
                                GameCanvas.addEffect(this.getUsers().get(k).session, (byte) 0, p.nj.get().id, (short) 22, 1, 1);
                            } else {
                                GameCanvas.addEffect(this.getUsers().get(k).session, (byte) 0, p.nj.get().id, (short) 23, 1, 1);
                                GameCanvas.addEffect(this.getUsers().get(k).session, (byte) 0, p.nj.get().id, (short) 24, 1, 1);
                            }
                        }
                        break;
                    }
                    case 786: {
                        for (int k = 0; k < this.getUsers().size(); ++k) {
                            if (im.getUpgrade() < 10) {
                                GameCanvas.addEffect(this.getUsers().get(k).session, (byte) 0, p.nj.get().id, (short) 25, 1, 1);
                            } else {
                                GameCanvas.addEffect(this.getUsers().get(k).session, (byte) 0, p.nj.get().id, (short) 25, 1, 1);
                            }
                        }
                        break;
                    }
                    case 787: {
                        for (int k = 0; k < this.getUsers().size(); ++k) {
                            if (im.getUpgrade() < 10) {
                                GameCanvas.addEffect(this.getUsers().get(k).session, (byte) 0, p.nj.get().id, (short) 26, 1, 1);
                            } else {
                                GameCanvas.addEffect(this.getUsers().get(k).session, (byte) 0, p.nj.get().id, (short) 27, 1, 1);
                            }
                        }
                        break;
                    }
                }
            }
        }

        if (p.nj.get().mobMe != null && p.nj.get().mobMe.timeFight <= System.currentTimeMillis()) {
            this.loadMobMeAtk(p.nj);
        }

        synchronized (p) {
            removeIfItemExpired(p);
        }

        updateSpecialEvent(p);
        if (this.map.isLangCo() && (p.nj.isDie || p.nj.pk > 0L)) {
            this.DieReturn(p);
        }

        if (System.currentTimeMillis() > p.nj.deleyRequestClan) {
            p.nj.requestclan = -1;
        }
        if (p != null
                && p.nj != null
                && p.nj.clone != null
                && p.nj.clone.isIslive() && System.currentTimeMillis() > p.nj.timeRemoveClone) {
            p.nj.clone.off();
        }
        SQLManager.executeQuery("SELECT `VND` FROM `player` WHERE `id` = " + p.id + " LIMIT 1;", (red) -> {
            if (red.first()) {
                long ngoc = red.getLong("VND");
                long ngocPer = ngoc - p.Ngoc;
                if (ngoc > p.Ngoc) {
                    p.session.sendMessageLog("Bạn vừa nhận được " + ngocPer + " ngọc.");
                }
                p.Ngoc = ngoc;
            }
        });
        if (p.nj.get().ItemBody[12].option.get(7) != null && p.nj.get().ItemBody[12].option.get(7).id == 87) {
            p.nj.get().ItemBody[12].option.remove(7);
        }
    }

    private List<ItemMap> findItemMapInDistance(int x, int y, int distance, boolean filter, int master) {
        List<ItemMap> itemMaps = new ArrayList<>();
        for (ItemMap itemMap : this._itemMap) {
            if (itemMap != null && (itemMap.master == -1 || itemMap.master == master) && Math.sqrt(Math.pow(itemMap.x - x, 2) + Math.pow(itemMap.y - y, 2)) <= distance) {
                itemMaps.add(itemMap);
            }
        }
        return itemMaps;
    }

    private Mob findMobInDistance(int x, int y, int distance) {
        for (Mob mob : this._mobs) {
            if (mob != null && !mob.isDie && Math.sqrt(Math.pow(mob.x - x, 2) + Math.pow(mob.y - y, 2)) <= distance) {
                return mob;
            }
        }
        return null;
    }

    @SneakyThrows
    private void updateHpToFriend(final @Nullable User p) {
        if (p == null) {
            return;
        }

        val m = messageSubCommand2(17);
        m.writer().writeInt(p.nj.id);
        m.writer().writeInt(p.nj.hp);
        m.writer().flush();
        sendMessage(m);
        m.cleanup();
    }

    public void updateMp(final @Nullable User p) throws IOException {
        if (p == null) {
            return;
        }
        Message msg = null;
        try {
            msg = new Message(-30);
            msg.writer().writeByte(-121);
            msg.writer().writeInt(p.nj.mp);
            p.sendMessage(msg);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    private void updateHp(final @Nullable User p) throws IOException {
        if (p == null) {
            return;
        }
        Message msg = null;
        try {
            msg = new Message(-30);
            msg.writer().writeByte(-122);
            msg.writer().writeInt(p.nj.hp);
            p.sendMessage(msg);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    private void updateExpiredItemMap() throws IOException {
        for (int i = 0; i < this._itemMap.size(); ++i) {
            final ItemMap itm = this._itemMap.get(i);
            if (itm == null || itm.item == null) {
                continue;
            }
            if (itm.removedelay != -1
                    && System.currentTimeMillis() >= itm.removedelay) {
                this.removeItemMapMessage(itm.itemMapId);
                this._itemMap.remove(i);
                --i;
            } else if (itm.removedelay - System.currentTimeMillis() < 45000L && itm.master != -1) {
                itm.master = -1;
            }
        }
    }

    private void updateEffect(final @Nullable User p) {
        if (p == null) {
            return;
        }
        for (Effect eff : p.nj.get().getVeff()) {
            if (System.currentTimeMillis() >= eff.timeRemove) {
                p.removeEffect(eff.template.id);

            } else {
                eff.timeStart++;
            }
            if (eff.template.type == 0 || eff.template.type == 12) {
                p.nj.get().upHP(eff.param);
                p.nj.get().upMP(eff.param);
            } else if (eff.template.type == 4 || eff.template.type == 17) {
                p.nj.get().upHP(eff.param);
            } else if (eff.template.type == 13) {
                p.nj.get().upHP(-(p.nj.get().getMaxHP() * 3 / 100));
                if (p.nj.get().isDie) {
                    p.nj.get().upDie();
                }
            }
        }
    }

    private void removeIfItemExpired(final @Nullable User p) throws IOException {
        if (p == null) {
            return;
        }
        for (int l = 0; l < p.nj.ItemBag.length; ++l) {
            final Item item = p.nj.ItemBag[l];
            if (item != null) {
                if (item.isExpires) {
                    if (item.isExpired()) {
                        p.nj.removeItemBag(l, item.quantity);
                    }
                }
            }
        }
        for (byte l = 0; l < p.nj.get().ItemBody.length; ++l) {
            final Item item = p.nj.get().ItemBody[l];
            if (item != null) {
                if (item.isExpires) {
                    if (item.isExpired()) {
                        p.nj.removeItemBody(l);
                    }
                }
            }
        }
        for (byte l = 0; l < p.nj.ItemBox.length; ++l) {
            final Item item = p.nj.ItemBox[l];
            if (item != null) {
                if (item.isExpires) {
                    if (item.isExpired()) {
                        p.nj.removeItemBox(l);
                    }
                }
            }
        }
    }
    //</editor-fold>

    @SneakyThrows
    public void updateSpecialEvent(final @Nullable User p) {
        if (p == null) {
            return;
        }
        Ninja nj = p.nj;
        if (battle != null) {
            try {
                battle.update(nj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (UpdateEvent runnable : this.runner) {
            try {
                runnable.update(nj);
            } catch (Exception e) {
                e.printStackTrace();
                runner.remove(runnable);
            }
        }

        try {
            if (nj.isHuman) {
                if (nj.clone != null && nj.clone.isIslive() && nj.clone.nclass == 6) {
                    val skills = nj.clone.getWinBuffSkills();
                    val winBuffSkill = skills[util.nextInt(skills.length)];
                    if (winBuffSkill != -1 && nj.clone.getSkill(winBuffSkill).coolDown < System.currentTimeMillis()) {
                        useSkill.useSkill(nj.clone, winBuffSkill);//sd skill
                        cloneBuffPlayer(p, winBuffSkill);//hiệu ứng skill
                        nj.clone.getSkill(winBuffSkill).coolDown = System.currentTimeMillis() + nj.clone.getSkill(winBuffSkill).getTemplate().coolDown;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!p.containsItem(572)) {
            p.typeTBLOption = NOT_USE;

        } else {

            if (p.typeTBLOption == $240 || p.typeTBLOption == $480 || p.typeTBLOption == ALL_MAP) {
                List<ItemMap> itemMaps = findItemMapInDistance(p.nj.get().x, p.nj.get().y, 100, p.filter, p.nj.get().id);

                for (ItemMap itemMap : itemMaps) {
                    if (p.nj.getAvailableBag() != 0 && itemMap != null && itemMap.item != null
                            && itemMap.item.getData().type != 25) {
                        removeItemMap(p, (short) _itemMap.indexOf(itemMap), itemMap);
                    }
                }
            }

            if (p.activeTBL) {
                final Mob mobInDistance = findMobInDistance(p.nj.get().x, p.nj.get().y, p.typeTBLOption.getValue());
                if (mobInDistance != null) {
                    sendXYPlayerWithEffect(p, p.nj.get().x, p.nj.get().y);
                    p.nj.get().x = mobInDistance.x;
                    boolean typeFly = mobInDistance.templates.type == 4;
                    p.nj.get().y = typeFly ? (short) (mobInDistance.y - 25) : mobInDistance.y;
                    sendXYPlayer(p);
                }
            }
        }
        // Nhiem vu heo rung
        if (map.id == 74 && p != null && p.nj != null && p.nj.get() != null
                && p.nj.get().isDie) {
            gietHeoRungGoBack(p, "Nhiệm vụ thất bại do hít quá nhiều khí độc");
        }
    }

    private void gietHeoRungGoBack(final @Nullable User p, final @Nullable String message) throws IOException {
        if (p == null || message == null) {
            return;
        }

        leave(p);
        p.expiredTime = -1;

        final Place freeArea = Server.getMapById(8).getFreeArea();
        if (freeArea != null) {
            for (Npc npc : freeArea.map.template.npc) {
                if (npc != null && npc.id == 15) {
                    p.nj.x = npc.x;
                    p.nj.y = npc.y;
                    break;
                }
            }
            p.nj.get().isDie = false;
            p.nj.get().upHP(p.nj.get().getMaxHP());
            p.nj.get().upMP(p.nj.get().getMaxMP());
            MessageSubCommand.sendHP(p.nj.get(), getUsers());
            MessageSubCommand.sendMP(p.nj.get(), getUsers());
            Service.batDauTinhGio(p, 0);
            val m = new Message(57);
            m.writer().flush();
            p.sendMessage(m);
            m.cleanup();
            p.nj.setMapid(8);
            freeArea.Enter(p);

        } else {
            gotoHaruna(p);
        }
        p.sendYellowMessage(message);
    }

    public void close() {
        this._users.clear();
    }

    public byte getNumplayers() {
        return (byte) this.getUsers().size();
    }

    public synchronized void removeRunner(UpdateEvent runnable) {
        this.runner.remove(runnable);
    }

    public synchronized Place addRunner(UpdateEvent runnable) {
        this.runner.add(runnable);
        return this;
    }

    @SneakyThrows
    public void sendPlayersInfo(final @Nullable Ninja nj, final @Nullable Message message) {
        if (nj == null || message == null) {
            return;
        }
        val m = new Message(25);
        val ds = m.writer();
        val size = message.reader().readByte();
        for (int i = 0; i < size; i++) {
            val ninja = getNinja(message.reader().readInt());
            if (ninja == null) {
                continue;
            }
            ds.writeInt(ninja.id);
            ds.writeShort(ninja.x);
            ds.writeShort(ninja.y);
            ds.writeInt(ninja.hp);
        }
        ds.flush();
        sendMessage(m);
        message.cleanup();
        m.cleanup();
    }

    private void MobAtkBuNhinMessage(final int mobid, final short idBuNhin, final short idskill_atk, final byte typeatk, final byte typetool) throws IOException {
        Message m = new Message(76);
        m.writer().writeByte(mobid);
        m.writer().writeShort(idBuNhin);
        m.writer().writeShort(idskill_atk);
        m.writer().writeByte(typeatk);
        m.writer().writeByte(typetool);
        m.writer().flush();
        this.sendMessage(m);
        m.cleanup();
    }

    public void addBuNhin(final @NotNull BuNhin buNhin) {
        buNhins.add(buNhin);
        MessageSubCommand.sendBuNhin(buNhin, getUsers());
    }

    public void removeBuNhin(@NotNull final BuNhin buNhin) {
        final int b = buNhins.indexOf(buNhin);
        buNhins.remove(buNhin);
        MessageSubCommand.removeBuNhin(b, getUsers());
    }

    public List<@Nullable User> getUsers() {
        return this._users;
    }

    public List<Mob> getMobs() {
        return _mobs;
    }

    private boolean canEnter = true;

    public boolean canEnter() {
        return canEnter;
    }

    @NotNull
    public Place open() {
        this.canEnter = true;
        return this;
    }

    public void reset() {
        numTA = 0;
        numTL = 0;
        recoverTa = false;
        canEnter = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Place place = (Place) o;
        return id == place.id && Objects.equals(map, place.map);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, map);
    }

    public void terminate() {
        for (User user : this._users) {
            if (user != null && user.nj != null) {
                try {
                    this.gotoHaruna(user);
                } catch (Exception e) {

                }
            }
        }
        this._users.clear();
        this.runner.clear();
        this._itemMap.clear();
        this._mobs.clear();
    }

    public Place setCandyBattle(@Nullable CandyBattle candyBattle) {
        this.candyBattle = candyBattle;
        return this;
    }

    public CandyBattle getCandyBattle() {
        return candyBattle;
    }

    public void killMob(int id) {
        for (Mob mob : this._mobs) {
            if (mob != null && mob.id == id) {
                mob.updateHP(-mob.hpmax);
                mob.isDie = true;
            }
        }
    }

    public void attack(List<Ninja> collect) {

    }

    public void cloneBuffPlayer(User p, int CSkill) throws IOException {
        Message m = new Message(61);
        m.writer().writeInt(p.nj.clone.id);
        m.writer().writeByte(CSkill);
        m.writer().writeInt(p.nj.id);
        m.writer().flush();
        sendMessage(m);
//        this.sendMyMessage(p, m);
        m.cleanup();
    }

    public int getNumPlayerParty(int partyId) {
        synchronized (this.LOCK) {
            int num = 0;
            try {
                short i;
                User pl;
                for (i = 0; i < this.getNumplayers(); i++) {
                    pl = this.getUsers().get(i);
                    if (pl != null && pl.nj != null && pl.nj.party != null && pl.nj.party.id == partyId) {
                        num++;
                    }
                }
            } catch (Exception var6) {
                var6.printStackTrace();
            }
            return num;
        }
    }

    protected short getIdMobAdd() {
        short mobId = -1;
        for (short index = this.maxMobid; index < 255; ++index) {
            boolean isTimra = true;
            for (short i = this.maxMobid; i < this.getMobs().size(); ++i) {
                final Mob mob = this.getMobs().get(i);
                if (mob != null && mob.id == index) {
                    isTimra = false;
                    break;
                }
            }
            if (isTimra) {
                mobId = index;
                break;
            }
        }
        return mobId;
    }

    public void callMobs(final Ninja _char, short idMobCall, short idItemUse) throws InterruptedException {
        if (_char.getPlace().map.isLangCo() || GameScr.mapNotPK(this.map.id)) {
            GameCanvas.startOKDlg(_char.p.session, "Can not use!");
        } else {
            try {
                final short mobId = _char.getPlace().getIdMobAdd();
                if (mobId != -1) {
                    boolean isThaythe = false;
                    for (short i = 0; i < _char.getPlace().getMobs().size(); ++i) {
                        final Mob mob = _char.getPlace().getMobs().get(i);
                        if (mob != null && mob.isCallMob() && mob.ninjaId == _char.id) {
                            isThaythe = true;
                            Service.endWait(_char);
                            break;
                        }
                    }
                    if (!isThaythe) {
                        final Mob mob = new Mob(_char.getPlace().getMobs().size(), idMobCall, _char.get().getLevel() - 5);
                        mob.templates.id = idMobCall;
                        mob.x = (short) (_char.x + 30);
                        mob.y = _char.y;
                        mob.hpmax = 150000;
                        mob.hp = 150000;
                        mob.status = 5;
                        mob.lvboss = 0;
                        mob.setIsboss(true);
                        mob.isIsboss();
                        mob.isDisable = false;
                        mob.isDontMove = false;
                        mob.isFire = false;
                        mob.isIce = false;
                        mob.isWind = false;
                        mob.sys = (byte) util.nextInt(1, 3);
                        mob.ninjaId = _char.id;
                        mob.removeCallMob = true;
                        mob.timeRemoveCallMob = System.currentTimeMillis() + 18000L;
                        _char.getPlace().getMobs().add(mob);
                        final ArrayList mobs = new ArrayList();
                        mobs.add(mob);
                        Service.addMob(_char, mobs);
                        _char.removeItemBags(idItemUse, 1);
                    }
                } else {
                    Service.endWait(_char);
                }
            } finally {
            }
        }
    }
}
