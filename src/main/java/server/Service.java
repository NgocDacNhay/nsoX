package server;

import io.Session;
import lombok.SneakyThrows;
import lombok.val;
import patch.interfaces.IBattle;
import patch.interfaces.SendMessage;
import patch.tournament.TournamentData;
import real.*;
import tasks.TaskList;
import tasks.TaskTemplate;
import threading.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import static real.Battle.CHIEN_DAU_STATE;

public class Service {

    protected static Message messageSubCommand(final byte command) throws Exception {
        final Message message = new Message(-30);
        message.writer().writeByte(command);
        return message;

    }

    public static void openUIMenu(Ninja _ninja, String[] menu) {
        Message msg = null;
        try {
            msg = new Message((byte) 40);
            if (menu != null) {
                for (byte i = 0; i < menu.length; i = (byte) (i + 1)) {
                    if (menu[i] != null) {
                        msg.writer().writeUTF(menu[i]);
                    }
                }
            }
            msg.writer().flush();
            _ninja.p.session.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public static void openUIConfirm(Ninja _char, short npcTemplateId, String chat, String[] menu) {
        Message msg = null;
        try {
            msg = new Message((byte) 39);
            msg.writer().writeShort(npcTemplateId);
            msg.writer().writeUTF(chat);
            msg.writer().writeByte(menu.length);
            byte i;
            for (i = 0; i < menu.length; i = (byte) (i + 1)) {
                msg.writer().writeUTF(menu[i]);
            }
            _char.p.session.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    protected static Message messageNotLogin(final byte command) throws Exception {
        final Message message = new Message(-29);
        message.writer().writeByte(command);
        return message;
    }

    public static Message messageNotMap(final byte command) throws Exception {
        final Message message = new Message(-28);
        message.writer().writeByte(command);
        return message;
    }

    public static void evaluateCave(final Ninja nj) {
        Message msg = null;
        try {
            msg = messageNotMap((byte) (-83));
            msg.writer().writeShort(nj.pointCave);
            msg.writer().writeShort(2);
            msg.writer().writeByte(0);
            msg.writer().writeShort(nj.pointCave / 10);
            nj.p.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public static Message messageSubCommand2(int command) {
        Message message = new Message(-30);

        try {
            message.writer().writeByte(command - 128);
            return message;
        } catch (Exception e) {
            return message;
        }
    }

    protected static void mapTime(final Ninja nj, final int timeLengthMap) {
        Message msg = null;
        try {
            msg = messageSubCommand((byte) (-95));
            msg.writer().writeInt(timeLengthMap);
            nj.p.session.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    @SneakyThrows
    public static void batDauTinhGio(SendMessage sendMes, int second) {
        if (sendMes == null) {
            return;
        }
        val message = messageSubCommand2(33);
        message.writer().writeInt(second);
        message.writer().flush();
        sendMes.sendMessage(message);
        message.cleanup();

    }

    public static void sendclonechar(final User p, final User top) {
        try {
            Message m = new Message(3);
            m.writer().writeInt(p.nj.clone.id);
            m.writer().writeUTF("");
            m.writer().writeBoolean(false);
            m.writer().writeByte(p.nj.clone.getTypepk());
            m.writer().writeByte(p.nj.clone.nclass);
            m.writer().writeByte(p.nj.clone.chuThan.gender);
            m.writer().writeShort(p.nj.clone.partHead());
            m.writer().writeUTF(p.nj.clone.chuThan.name);
            m.writer().writeInt(p.nj.clone.hp);
            m.writer().writeInt(p.nj.clone.getMaxHP());
            m.writer().writeByte(p.nj.clone.getLevel());
            m.writer().writeShort(p.nj.clone.Weapon());
            m.writer().writeShort(p.nj.clone.partBody());
            m.writer().writeShort(p.nj.clone.partLeg());
            m.writer().writeByte(-1);
            m.writer().writeShort(p.nj.clone.x);
            m.writer().writeShort(p.nj.clone.y);
            m.writer().writeShort(p.nj.eff5buffHP());
            m.writer().writeShort(p.nj.eff5buffMP());
            m.writer().writeByte(0);
            m.writer().writeBoolean(p.nj.clone.isHuman);
            m.writer().writeBoolean(p.nj.clone.isNhanban);
            m.writer().writeShort(-1);
            m.writer().writeShort(-1);
            m.writer().writeShort(-1);
            m.writer().writeShort(-1);
            for (byte i = 0; i < p.nj.clone.setThoiTrang.length; i++) {
                m.writer().writeShort(p.nj.clone.setThoiTrang[i]);
            }
            for (int k = 16; k < 32; ++k) {//Trang bị 2
                final Item item = p.nj.clone.ItemBody[k];
                if (item != null) {
                    m.writer().writeShort(item.id);
                    m.writer().writeByte(item.getUpgrade());
                    m.writer().writeByte(item.sys);
                } else {
                    m.writer().writeShort(-1);
                }
            }
            m.writer().flush();
            top.sendMessage(m);
            m.cleanup();
            if (p.nj.clone.mobMe != null) {
                m = new Message(-30);
                m.writer().writeByte(-68);
                m.writer().writeInt(p.nj.clone.id);
                m.writer().writeByte(p.nj.clone.mobMe.templates.id);
                m.writer().writeByte(p.nj.clone.mobMe.isIsboss() ? 1 : 0);
                m.writer().flush();
                top.sendMessage(m);
                m.cleanup();
            }
            p.nj.getPlace().sendCoat(p.nj.clone, top);
            p.nj.getPlace().sendGlove(p.nj.clone, top);
            p.nj.getPlace().sendMounts(p.nj.clone, top);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setHPMob(final Ninja nj, final int mobid, final int hp) {
        Message msg = null;
        try {
            msg = new Message(51);
            msg.writer().writeByte(mobid);
            msg.writer().writeInt(0);
            nj.p.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public static void CharViewInfo(final User p) {
        CharViewInfo(p, true);
    }

    public static void CharViewInfo(final User p, boolean sendEff) {
        Message msg = null;
        try {
            final Ninja c = p.nj;
            msg = messageSubCommand((byte) 115);
            msg.writer().writeInt(c.get().id);
            msg.writer().writeUTF(c.clan.clanName);
            if (!c.clan.clanName.isEmpty()) {
                msg.writer().writeByte(c.clan.typeclan);
            }
            msg.writer().writeByte(c.getTaskId());
            msg.writer().writeByte(c.gender);
            msg.writer().writeShort(c.get().partHead());
            msg.writer().writeByte(c.get().speed());
            msg.writer().writeUTF(c.name);
            msg.writer().writeByte(c.get().pk);
            msg.writer().writeByte(c.get().getTypepk());
            msg.writer().writeInt(c.get().getMaxHP());
            msg.writer().writeInt(c.get().hp);
            msg.writer().writeInt(c.get().getMaxMP());
            msg.writer().writeInt(c.get().mp);
            msg.writer().writeLong(c.get().getExp());
            msg.writer().writeLong(c.get().expdown);
            msg.writer().writeShort(c.get().eff5buffHP());
            msg.writer().writeShort(c.get().eff5buffMP());
            msg.writer().writeByte(c.get().nclass);
            msg.writer().writeShort(c.get().getPpoint());
            msg.writer().writeShort(c.get().getPotential0());
            msg.writer().writeShort(c.get().getPotential1());
            msg.writer().writeInt(c.get().getPotential2());
            msg.writer().writeInt(c.get().getPotential3());
            msg.writer().writeShort(c.get().getSpoint());
            msg.writer().writeByte(c.get().getSkills().size());
            for (short i = 0; i < c.get().getSkills().size(); ++i) {
                final Skill skill = c.get().getSkills().get(i);
                msg.writer().writeShort(SkillData.Templates(skill.id, skill.point).skillId);
            }
            msg.writer().writeInt(c.xu);
            msg.writer().writeInt(c.yen);
            msg.writer().writeInt(p.luong);
            msg.writer().writeByte(c.maxluggage);
            for (int j = 0; j < c.maxluggage; ++j) {
                final Item item = c.ItemBag[j];
                if (item != null) {
                    msg.writer().writeShort(item.id);
                    msg.writer().writeBoolean(item.isLock());
                    if (ItemData.isTypeBody(item.id) || ItemData.isTypeMounts(item.id) || ItemData.isTypeNgocKham(item.id)) {
                        msg.writer().writeByte(item.getUpgrade());
                    }
                    msg.writer().writeBoolean(item.isExpires);
                    msg.writer().writeShort(item.quantity);
                } else {
                    msg.writer().writeShort(-1);
                }
            }
            for (int k = 0; k < 16; ++k) {
                final Item item = c.get().ItemBody[k];
                if (item != null) {
                    msg.writer().writeShort(item.id);
                    msg.writer().writeByte(item.getUpgrade());
                    msg.writer().writeByte(item.sys);
                } else {
                    msg.writer().writeShort(-1);
                }
            }
            msg.writer().writeBoolean(c.isHuman);
            msg.writer().writeBoolean(c.isNhanban);
            msg.writer().writeShort(c.get().partHead());
            msg.writer().writeShort(c.get().Weapon());
            msg.writer().writeShort(c.get().partBody());
            msg.writer().writeShort(c.get().partLeg());
            for (byte i = 0; i < c.setThoiTrang.length; i++) {
                msg.writer().writeShort(c.setThoiTrang[i]);
            }
            for (int k = 16; k < 32; ++k) {//Trang bị 2
                final Item item = c.get().ItemBody[k];
                if (item != null) {
                    msg.writer().writeShort(item.id);
                    msg.writer().writeByte(item.getUpgrade());
                    msg.writer().writeByte(item.sys);
                } else {
                    msg.writer().writeShort(-1);
                }
            }
            msg.writer().flush();
            p.sendMessage(msg);
            msg.cleanup();
            p.getMobMe();
            if (sendEff) {
                for (byte n = 0; n < c.get().getVeff().size(); ++n) {
                    p.addEffectMessage(c.get().getVeff().get(n));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public static void Mobstart(final User p, final int mobid, final int dame, final boolean flag) {
        Message msg = null;
        try {
            msg = new Message(-4);
            msg.writer().writeByte(mobid);
            msg.writer().writeInt(dame);
            msg.writer().writeBoolean(flag);
            msg.writer().flush();
            p.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public static void Mobstart(final User p, final int mobid, final int hp, final int dame, final boolean flag, final int levelboss, final int hpmax) {
        Message msg = null;
        try {
            msg = new Message(-1);
            msg.writer().writeByte(mobid);
            msg.writer().writeInt(hp);
            msg.writer().writeInt(dame);
            msg.writer().writeBoolean(flag);
            msg.writer().writeByte(levelboss);
            msg.writer().writeInt(hpmax);
            msg.writer().flush();
            p.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    protected static void HavePlayerAttack(Ninja _ninja, Ninja player, int dame) {
        Message msg = null;
        try {
            msg = new Message((byte) 62);
            msg.writer().writeInt(player.id);
            msg.writer().writeInt(player.hp);
            msg.writer().writeInt(dame);
            msg.writer().writeInt(player.mp);
            msg.writer().writeInt(0);
            _ninja.p.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public static void PlayerAttack(final User p, final Mob[] mob, final Body b) {
        Message msg = null;
        try {
            msg = new Message(60);
            msg.writer().writeInt(b.id);
            msg.writer().writeByte(b.getCSkill());
            for (byte i = 0; i < mob.length; ++i) {
                if (mob[i] != null) {
                    msg.writer().writeByte(mob[i].id);
                }
            }
            msg.writer().flush();
            p.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    @SneakyThrows
    public static void sendBattleList(User p) {
        try {
            Message m = new Message(100);
            m.writer().writeByte((byte) Dun.duns.size());

            for (int i = 0; i < Dun.duns.size(); ++i) {
                if (Dun.duns.get(i) != null) {
                    m.writer().writeByte((byte) ((Dun) Dun.duns.get(i)).dunID);
                    m.writer().writeUTF(((Dun) Dun.duns.get(i)).name1 + " (" + ((Dun) Dun.duns.get(i)).lv1 + ")");
                    m.writer().writeUTF(((Dun) Dun.duns.get(i)).name2 + " (" + ((Dun) Dun.duns.get(i)).lv2 + ")");
                }
            }

            m.writer().flush();
            p.session.sendMessage(m);
            m.cleanup();
        } catch (Exception var3) {
            var3.printStackTrace();
        }
    }

    ///////////////////////////////
    public static void PlayerAttack(Ninja _ninja, int charID, byte skillId, Mob[] arrMob, Ninja[] arrNinja) {
        Message msg = null;
        try {
            msg = new Message((byte) 4);
            msg.writer().writeInt(charID);
            msg.writer().writeByte(skillId);
            byte num = 0;
            byte i;
            for (i = 0; i < arrMob.length; i = (byte) (i + 1)) {
                if (arrMob[i] != null) {
                    num = (byte) (num + 1);
                }
            }
            msg.writer().writeByte(num);
            for (i = 0; i < arrMob.length; i = (byte) (i + 1)) {
                if (arrMob[i] != null) {
                    msg.writer().writeByte((arrMob[i]).templates.id);
                }
            }
            for (i = 0; i < arrNinja.length; i = (byte) (i + 1)) {
                if (arrNinja[i] != null) {
                    msg.writer().writeInt((arrNinja[i]).id);
                }
            }
            _ninja.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public static void PlayerAttack(Ninja _ninja, int charID, byte skill, Mob[] arrMob) {
        Message msg = null;
        try {
            msg = new Message((byte) 60);
            msg.writer().writeInt(charID);
            msg.writer().writeByte(skill);
            byte i;
            for (i = 0; i < arrMob.length; i = (byte) (i + 1)) {
                if (arrMob[i] != null) {
                    msg.writer().writeByte((arrMob[i]).templates.id);
                }
            }
            _ninja.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public static void PlayerAttack(Ninja _ninja, int charID, short skill, Ninja[] arrNinja) {
        Message msg = null;
        try {
            msg = new Message((byte) 61);
            msg.writer().writeInt(charID);
            msg.writer().writeByte(skill);
            byte i;
            for (i = 0; i < arrNinja.length; i = (byte) (i + 1)) {
                if (arrNinja[i] != null) {
                    msg.writer().writeInt((arrNinja[i]).id);
                }
            }
            _ninja.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    @SneakyThrows
    public static void sendBattleResult(Ninja n, IBattle battle) {
        val m = messageNotMap((byte) (46 - 126));
        m.writer().writeUTF(battle.getResult(n));
        val reward = battle.getRewards(n) != null && battle.getRewards(n).length > 0 && n.getClanBattle() == null;
        m.writer().writeBoolean(reward);
        n.sendMessage(m);
        m.cleanup();
    }

    @SneakyThrows
    public static void sendThongBao(SendMessage n, String message) {
        val m = messageNotMap((byte) (46 - 126));
        m.writer().writeUTF(message);
        m.writer().writeBoolean(false);
        n.sendMessage(m);
        m.cleanup();
    }

    @SneakyThrows
    public static void sendChallenges(List<TournamentData> tournaments, SendMessage p) {
        val m = new Message(-135);
        m.writer().writeByte(tournaments.size());
        for (TournamentData tournament : tournaments) {
            m.writer().writeUTF(tournament.getName());
            m.writer().writeInt(tournament.getRanked());
            m.writer().writeUTF(tournament.getStatus());
        }
        p.sendMessage(m);
        m.cleanup();
    }

    public static void openUISay(Ninja _ninja, short npcTemplateId, String chat) {
        Message msg = null;
        try {
            msg = new Message((byte) 38);
            msg.writer().writeShort(npcTemplateId);
            msg.writer().writeUTF(chat);
            _ninja.p.session.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public static void finishTask(Ninja _ninja) {
        Message msg = null;
        try {
            msg = new Message((byte) 49);
            _ninja.p.session.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public static void updateTask(Ninja _ninja) {
        Message msg = null;
        try {
            msg = new Message((byte) 50);
            msg.writer().writeShort(_ninja.taskCount);
            _ninja.p.session.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public static void nextTask(Ninja _ninja) {
        Message msg = null;
        try {
            msg = new Message((byte) 48);
            _ninja.p.session.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public static void getTask(Ninja _ninja) {
        Message msg = null;
        try {
            if (TaskList.taskTemplates.length <= _ninja.getTaskId()) {
                return;
            }

            TaskTemplate taskTemplate = TaskList.taskTemplates[_ninja.getTaskId()];
            msg = new Message((byte) 47);
            msg.writer().writeShort(taskTemplate.getTaskId());
            msg.writer().writeByte(_ninja.getTaskIndex());
            msg.writer().writeUTF(taskTemplate.getName());
            msg.writer().writeUTF(taskTemplate.getDetail());
            msg.writer().writeByte(taskTemplate.subNames.length);
            for (byte b = 0; b < taskTemplate.subNames.length; b = (byte) (b + 1)) {
                msg.writer().writeUTF(taskTemplate.getSubNames()[b]);
            }
            msg.writer().writeShort(_ninja.taskCount);
            for (short i = 0; i < taskTemplate.getCounts().length; i = (short) (i + 1)) {
                msg.writer().writeShort(taskTemplate.getCounts()[i]);
            }
            _ninja.p.session.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    @SneakyThrows
    public static void showWait(String s, SendMessage sendMsg) {
        final Message message = messageSubCommand2(54);
        message.writer().writeUTF(s);
        sendMsg.sendMessage(message);
    }

    public static void endWait(SendMessage sendMessage) {
        final Message message = messageSubCommand2(39);
        sendMessage.sendMessage(message);
    }

    public static void sendBallEffect(Ninja ninja) {
        val m = messageSubCommand2(70);
        ninja.sendMessage(m);
    }

    @SneakyThrows
    public static void sendThieuDot(Collection<? extends SendMessage> sendMessages, int mobId) {
        val m = messageSubCommand2(55);
        m.writer().writeByte(mobId);
        for (SendMessage sendMessage : sendMessages) {
            sendMessage.sendMessage(m);
        }
    }

    public static void thaoCaiTrang(User p) {
        Message ms = null;
        try {
            p.nj.ItemBodyHide[0] = null;
            ms = new Message(11);
            ms.writer().writeByte(-1);
            ms.writer().writeByte(p.nj.get().speed());
            ms.writer().writeInt(p.nj.get().getMaxHP());
            ms.writer().writeInt(p.nj.get().getMaxMP());
            ms.writer().writeShort(p.nj.get().eff5buffHP());
            ms.writer().writeShort(p.nj.get().eff5buffMP());
            ms.writer().flush();
            p.session.sendMessage(ms);
            ms.cleanup();
            Service.CharViewInfo(p, false);
            p.endLoad(true);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ms != null) {
                ms.cleanup();
            }
        }
    }

    public static void macCaiTrang(User p, byte index) {
        Message ms = null;
        try {
            final Item itembody = p.nj.getIndexCaiTrang(index);
            p.nj.get().ItemBody[index] = itembody;
            p.nj.get().ItemBody[index] = itembody;
            ms = new Message(11);
            ms.writer().writeByte(index);
            ms.writer().writeByte(p.nj.get().speed());
            ms.writer().writeInt(p.nj.get().getMaxHP());
            ms.writer().writeInt(p.nj.get().getMaxMP());
            ms.writer().writeShort(p.nj.get().eff5buffHP());
            ms.writer().writeShort(p.nj.get().eff5buffMP());
            ms.writer().flush();
            p.session.sendMessage(ms);
            ms.cleanup();
            Service.CharViewInfo(p, false);
            p.endLoad(true);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ms != null) {
                ms.cleanup();
            }
        }
    }

    public static void openMenuBox(User p) {
        Message m = null;
        try {
            p.menuCaiTrang = 0;
            p.openUI(4);
            m = new Message(31);
            m.writer().writeInt(p.nj.xuBox);
            m.writer().writeByte(p.nj.ItemBox.length);
            for (Item item : p.nj.ItemBox) {
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
            p.session.sendMessage(m);
            m.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (m != null) {
                m.cleanup();
            }
        }
    }

    public static void openMenuBST(User p) {
        Message m = null;
        try {
            p.menuCaiTrang = 1;
            p.openUI(4);
            Service.sendTileAction(p, (byte) 4, "Bộ sưu tập", "Sử dụng");
            m = new Message(31);
            m.writer().writeInt(p.nj.xuBox);
            m.writer().writeByte(p.nj.ItemBST.length);
            for (Item item : p.nj.ItemBST) {
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
            p.session.sendMessage(m);
            m.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (m != null) {
                m.cleanup();
            }
        }

    }

    public static void openMenuCaiTrang(User p) {
        Message m = null;
        try {
            p.menuCaiTrang = 2;
            p.openUI(4);
            Service.sendTileAction(p, (byte) 4, "Cải trang", "Sử dụng");
            m = new Message(31);
            m.writer().writeInt(p.nj.xuBox);
            m.writer().writeByte(p.nj.ItemCaiTrang.length);
            for (Item itemCT : p.nj.ItemCaiTrang) {
                if (itemCT != null) {
                    m.writer().writeShort(itemCT.id);
                    m.writer().writeBoolean(itemCT.isLock());
                    if (ItemData.isTypeBody(itemCT.id) || ItemData.isTypeNgocKham(itemCT.id)) {
                        m.writer().writeByte(itemCT.getUpgrade());
                    }
                    m.writer().writeBoolean(itemCT.isExpires);
                    m.writer().writeShort(itemCT.quantity);
                } else {
                    m.writer().writeShort(-1);
                }
            }
            m.writer().flush();
            p.session.sendMessage(m);
            m.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (m != null) {
                m.cleanup();
            }
        }
    }

    public static void openMenuMuaLai(User p) {
        Message m = null;
        try {
            p.menuCaiTrang = 3;
            m = new Message(31);
            Service.sendTileAction(p, (byte) 4, "Mua lại", "Mua");
            m.writer().writeInt(p.nj.xuBox);
            m.writer().writeByte(p.nj.ItemMuaLai.size());
            for (Item item : p.nj.ItemMuaLai) {
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
            p.session.sendMessage(m);
            m.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (m != null) {
                m.cleanup();
            }
        }
    }

    public static void openMenuGiaHan(User p) {
        Message m = null;
        try {
            p.menuCaiTrang = 4;
            m = new Message(31);
            Service.sendTileAction(p, (byte) 4, "Gia hạn", "Gia hạn");
            m.writer().writeInt(p.nj.xuBox);
            m.writer().writeByte(p.nj.ItemGiaHan.size());
            for (Item item : p.nj.ItemGiaHan) {
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
            p.session.sendMessage(m);
            m.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (m != null) {
                m.cleanup();
            }
        }
    }

    public static void sendTileAction(User player, byte typeUI, String title, String action) {
        Message m = null;
        try {
            if (player.session != null) {
                m = new Message(30);
                m.writer().writeByte(typeUI);
                m.writer().writeUTF(title);
                m.writer().writeUTF(action);
                m.writer().flush();
                player.session.sendMessage(m);
            }
        } catch (Exception var3) {
            var3.printStackTrace();
        } finally {
            if (m != null) {
                m.cleanup();
            }
        }

    }

    public static void startYesNoDlg(User p, byte id, String str) {
        Message msg = null;

        try {
            msg = new Message((byte) 107);
            msg.writer().writeByte(id);
            msg.writer().writeUTF(str);
            msg.writer().flush();
            p.session.sendMessage(msg);
        } catch (Exception var8) {
            var8.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
            }

        }

    }

    @SneakyThrows
    public static void sendEffectAuto(User p, byte id, int x, int y, byte count, short timeInSec) {
        val m = new Message((byte) 122);
        val w = m.writer();
        w.writeByte(1);
        w.writeByte(id);
        w.writeShort(x);
        w.writeShort(y);
        w.writeByte(count);
        w.writeShort(timeInSec);
        p.session.sendMessage(m);
    }

    public static void getDataImgEffAuto(User p, byte type, byte id) {
        if (id > 16) {
            return;
        }
        try {
            val m = new Message((byte) 122);
            val w = m.writer();
            switch (type) {
                case 0: {
                    // send image
                    w.writeByte(2);
                    w.writeByte(id);
                    val data = GameScr.loadFile("res/effauto/x" + p.session.zoomLevel + "/Img/" + id + ".png").toByteArray();
                    w.writeInt(data.length);
                    w.write(data);
                    break;
                }
                case 1: {
                    // get data
                    w.writeByte(3);
                    w.writeByte(id);
                    val data = GameScr.loadFile("res/effauto/x" + p.session.zoomLevel + "/Data/" + id).toByteArray();
                    w.writeShort(data.length);
                    w.write(data);
                    break;
                }
            }
            p.session.sendMessage(m);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ChangTypePkId(Ninja c, byte type) {
        Message m = null;
        try {
            m = new Message(-30);
            m.writer().writeByte(-92);
            m.writer().writeInt(c.id);
            m.writer().writeByte(type);
            m.writer().flush();
            c.p.session.sendMessage(m);
        } catch (Exception var3) {
            var3.printStackTrace();
        } finally {
            if (m != null) {
                m.cleanup();
            }
        }
    }

    public static void PlayerRemove(final Ninja nj, final int charID) {
        Message msg = null;
        try {
            msg = new Message((byte) 2);
            msg.writer().writeInt(charID);
            nj.p.session.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public static void OutParty(Ninja c) {
        try {
            Message msg = new Message((byte) 83);
            msg.writer().flush();
            c.p.session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception var2) {
            var2.printStackTrace();
        }
    }

    public static void addMob(final Ninja nj, final ArrayList<Mob> mobs) {
        Message msg = null;
        try {
            msg = new Message((byte) 122);
            msg.writer().writeByte(0);
            msg.writer().writeByte(mobs.size());
            for (short i = 0; i < mobs.size(); ++i) {
                final Mob mob = mobs.get(i);
                if (mob != null) {
                    msg.writer().writeByte(mob.id);
                    msg.writer().writeBoolean(mob.isDisable);
                    msg.writer().writeBoolean(mob.isDontMove);
                    msg.writer().writeBoolean(mob.isFire);
                    msg.writer().writeBoolean(mob.isIce);
                    msg.writer().writeBoolean(mob.isWind);
                    msg.writer().writeByte(mob.templates.id);
                    msg.writer().writeByte(mob.sys);
                    msg.writer().writeInt(mob.hp);
                    msg.writer().writeByte(mob.level);
                    msg.writer().writeInt(mob.hpmax);
                    msg.writer().writeShort(mob.x);
                    msg.writer().writeShort(mob.y);
                    msg.writer().writeByte(mob.status);
                    msg.writer().writeByte(mob.lvboss);
                    msg.writer().writeBoolean(mob.isIsboss());
                }
            }
            nj.p.session.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public static void sendTB(final User p, final String title, final String s) throws IOException {
        final Message m = new Message(53);
        m.writer().writeUTF(title);
        m.writer().writeUTF(s);
        m.writer().flush();
        p.sendMessage(m);
        m.cleanup();
    }

    public static void startOKDlgServer(final String info) {
        try {
            final Message msg = new Message((byte) (-26));
            try {
                msg.writer().writeUTF(info);
                PlayerManager.getInstance().NinjaMessage(msg);
            } finally {
                msg.cleanup();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
