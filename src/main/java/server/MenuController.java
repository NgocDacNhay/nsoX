package server;

import boardGame.Place;
import lombok.SneakyThrows;
import lombok.val;
import patch.*;
import patch.ItemShinwaManager.ItemShinwa;
import patch.clan.ClanTerritory;
import patch.clan.ClanTerritoryData;
import patch.interfaces.IBattle;
import patch.tournament.GeninTournament;
import patch.tournament.KageTournament;
import patch.tournament.Tournament;
import patch.tournament.Tournament.RegisterResult;
import patch.tournament.TournamentData;
import real.*;
import tasks.TaskHandle;
import tasks.TaskList;
import tasks.Text;
import threading.Manager;
import threading.Map;
import threading.Message;
import threading.Server;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static patch.Constants.TOC_TRUONG;
import static patch.ItemShinwaManager.*;
import static patch.TaskOrder.*;
import static patch.tournament.Tournament.*;
import static real.User.TypeTBLOption.*;

public class MenuController {

    public static final String MSG_HANH_TRANG = "Hành trang ko đủ chổ trống";

    public static final int MIN_YEN_NVHN = 100;
    public static final int MAX_YEN_NVHN = 200;
    LogHistory LogHistory = new LogHistory(this.getClass());

    Server server;
    private int randomDaDanhVong = -1;
    private int pointDanhVong = 0;

    static int[] OpIdBK = new int[]{82, 83, 84, 86, 87, 91, 95, 97, 80, 96, 88, 89, 90, 81, 92};
    static int[] ParramOpBK = new int[]{util.nextInt(300, 600), util.nextInt(300, 600), util.nextInt(30, 70), util.nextInt(10, 60), util.nextInt(200, 400), util.nextInt(20, 70), util.nextInt(20, 50), util.nextInt(20, 50), util.nextInt(50, 100), util.nextInt(20, 50), util.nextInt(300, 600), util.nextInt(300, 600), util.nextInt(300, 600), util.nextInt(50, 70), util.nextInt(10, 50)};

    //Event
    private static short[] idItemCayThong = new short[]{5, 5, 5, 5, 5, 6, 6, 6, 7, 7, 8, 397, 398, 399, 400, 401, 402, 407, 408, 409, 410, 275, 275, 275, 276, 276, 276, 277, 277, 277, 278, 278, 278, 285, 541, 542, 567, 788, 788, 788, 789, 789, 789, 568, 569, 569, 337, 437, 43, 437, 437};

    public MenuController() {
        this.server = Server.getInstance();
    }

    public void sendMenu(final User p, final Message m) throws IOException {
        final byte npcId = m.reader().readByte();
        byte menuId = m.reader().readByte();
        final byte optionId = m.reader().readByte();

        val ninja = p.nj;

        if (TaskHandle.isTaskNPC(ninja, npcId) && Map.isNPCNear(ninja, npcId) && ((ninja.nclass == 0 && ninja.getTaskId() < 9) || (ninja.nclass != 0 && ninja.getTaskId() >= 9))) {
            // TODO SELECT MENU TASK
            menuId = (byte) (menuId - 1);
            if (ninja.getTaskIndex() == -1) {

                if (menuId == -1) {
                    TaskHandle.Task(ninja, (short) npcId);
                    return;
                }
            } else if (TaskHandle.isFinishTask(ninja)) {
                if (menuId == -1) {
                    TaskHandle.finishTask(ninja, (short) npcId);
                    return;
                }
            } else if (ninja.getTaskId() == 1) {
                if (menuId == -1) {
                    TaskHandle.doTask(ninja, (short) npcId, menuId, optionId);
                    return;
                }
            } else if (ninja.getTaskId() == 7) {
                if (menuId == -1) {
                    TaskHandle.doTask(ninja, (short) npcId, menuId, optionId);
                    return;
                }
            } else if (ninja.getTaskId() == 8 || ninja.getTaskId() == 0) {
                boolean npcTalking = TaskHandle.npcTalk(ninja, menuId, npcId);
                if (npcTalking) {
                    return;
                }

            } else if (ninja.getTaskId() == 13) {
                if (menuId == -1) {
                    if (ninja.getTaskIndex() == 1) {
                        // OOka
                        final Map map = Server.getMapById(56);
                        val place = map.getFreeArea();
                        val npc = Ninja.getNinja("Thầy Ookamesama");
                        npc.p = new User();
                        npc.p.nj = npc;
                        npc.isNpc = true;
                        npc.setTypepk(Constants.PK_DOSAT);
                        p.nj.enterSamePlace(place, npc);
                        return;
                    } else if (ninja.getTaskIndex() == 2) {
                        // Haru
                        final Map map = Server.getMapById(0);
                        val place = map.getFreeArea();
                        val npc = Ninja.getNinja("Thầy Kazeto");
                        if (npc == null) {
                            System.out.println("KO THẦY ĐỐ MÀY LÀM NÊN");
                            return;
                        }
                        npc.p = new User();
                        npc.isNpc = true;
                        npc.p.nj = npc;
                        npc.setTypepk(Constants.PK_DOSAT);
                        p.nj.enterSamePlace(place, npc);
                        return;
                    } else if (ninja.getTaskIndex() == 3) {
                        final Map map = Server.getMapById(73);

                        val npc = Ninja.getNinja("Cô Toyotomi");
                        if (npc == null) {
                            System.out.println("KO THẦY ĐỐ MÀY LÀM NÊN");
                            return;
                        }
                        npc.isNpc = true;
                        npc.p = new User();
                        npc.setTypepk(Constants.PK_DOSAT);
                        npc.p.nj = npc;
                        val place = map.getFreeArea();
                        p.nj.enterSamePlace(place, npc);
                        return;
                    }
                } else if (ninja.getTaskId() == 15
                        && ninja.getTaskIndex() >= 1) {
                    if (menuId == -1) {
                        // Nhiem vu giao thu
                        if (ninja.getTaskIndex() == 1 && npcId == 14) {
                            p.nj.removeItemBags(214, 1);
                        } else if (ninja.getTaskIndex() == 2 && npcId == 15) {
                            p.nj.removeItemBags(214, 1);
                        } else if (ninja.getTaskIndex() == 3 && npcId == 16) {
                            p.nj.removeItemBags(214, 1);
                        }
                    }

                }
            }
        }

        m.cleanup();
        Label_6355:
        {
            label:
            switch (p.typemenu) {
                case 0: {
                    if (menuId == 0) {
                        // Mua vu khi
                        p.openUI(2);
                        break;
                    }
                    switch (menuId) {
                        case 1:
                            p.typemenu = 902;
                            doMenuArray(p, new String[]{"Thành lập", "Lãnh Địa Gia Tộc", "Đổi túi quà", "Hướng dẫn"});
                            break label;
                        case 2:
                            if (menuId != 2) {
                                break label;
                            }
                            p.typemenu = 900;
                            doMenuArray(p, new String[]{"Nhận thưởng", "Cấp 35", "Cấp 45", "Cấp 55", "Cấp 65", "Cấp 75", "Cấp 95"});
                            break label;
                        case 3: {
                            p.typemenu = 901;
                            doMenuArray(p, new String[]{"Thách đấu", "Xem thi đấu", "Kết quả"});
                            break;
                        }
                    }
                    break;
                }
                case 902: {
                    if (menuId == 0) {
                        // Thanh lap gia toc
                        if (!p.nj.clan.clanName.isEmpty()) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Hiện tại con đã có gia tộc không thể thành lập thêm được nữa.");
                            break label;
                        }
                        if (p.luong < ClanManager.LUONG_CREATE_CLAN) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Để thành lập gia tộc con cần phải có đủ 50.000 lượng trong người.");
                            break label;
                        }
                        this.sendWrite(p, (short) 50, "Tên gia tộc");
                    } else if (menuId == 1) {
                        // Lanh địa gia tộc
                        if (p.getClanTerritoryData() == null) {
                            if (p.nj.clan.typeclan == TOC_TRUONG) {

                                if (p.nj.getAvailableBag() == 0) {
                                    p.sendYellowMessage("Hành trang không đủ để nhận chìa khoá");
                                    return;
                                }
                                val clan = ClanManager.getClanByName(p.nj.clan.clanName);
                                if (clan.openDun <= 0) {
                                    p.sendYellowMessage("Số lần đi lãnh địa gia tộc đã hết vui lòng dùng thẻ bài hoặc đợi vào tuần");
                                    return;
                                }

                                val clanTerritory = new ClanTerritory(clan);
                                Server.clanTerritoryManager.addClanTerritory(clanTerritory);
                                p.setClanTerritoryData(new ClanTerritoryData(clanTerritory, p.nj));
                                Server.clanTerritoryManager.addClanTerritoryData(p.getClanTerritoryData());

                                clanTerritory.clanManager.openDun--;
                                if (clanTerritory == null) {
                                    p.sendYellowMessage("Có lỗi xẩy ra");
                                    return;
                                }
                                val area = clanTerritory.getEntrance();
                                if (area != null) {
                                    val item = ItemData.itemDefault(260);
                                    p.nj.addItemBag(false, item);
                                    if (p.getClanTerritoryData().getClanTerritory() != null) {

                                        if (p.getClanTerritoryData().getClanTerritory() != null) {
                                            p.getClanTerritoryData().getClanTerritory().enterEntrance(p.nj);
                                        }

                                        clanTerritory.clanManager.informAll("Tộc trưởng đã mở lãnh địa gia tộc");
                                    } else {
                                        p.sendYellowMessage("Null sml");
                                    }
                                } else {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Hiện tại lãnh địa gia tộc không còn khu trống");
                                }

                            } else {
                                p.sendYellowMessage("Chỉ những người được tộc trưởng mời mới có thể vào lãnh địa gia tộc");
                            }
                        } else {
                            val data = p.getClanTerritoryData();
                            if (data != null) {
                                val teri = data.getClanTerritory();
                                if (teri != null) {
                                    teri.enterEntrance(p.nj);
                                }
                            }
                        }

                    } else if (menuId == 2) {
                        int sum = 0;
                        for (Item item : p.nj.ItemBag) {
                            if (item != null && item.id == 262) {
                                sum += item.quantity;
                            }
                        }
                        if (sum > 0) {
                            p.nj.removeItemBags(262, sum);
                            val item = ItemData.itemDefault(263);
                            item.quantity = (int) (sum / 1.2);
                            p.nj.addItemBag(true, item);
                        } else {
                            p.sendYellowMessage("Không có xu gia tộc để đổi");
                        }

                    }
                    break;
                }
                case 901: {
                    if (menuId == 0) {
                        // Thach dau loi dai
                        this.sendWrite(p, (short) 2, "Nhập tên nhân vật");
                        break;
                    } else if (menuId == 1) {
                        // Xem thi dau
                        Service.sendBattleList(p);
                    } else if (menuId == 2) {
                        String alert = "";
                        if (DunListWin.dunList.size() < 1) {
                            alert = "Không có trận đấu nào";
                        }
                        for (int i = 0; i < DunListWin.dunList.size(); ++i) {
                            int temp = i + 1;
                            alert = alert + temp + ". Phe " + ((DunListWin) DunListWin.dunList.get(i)).win + " thắng Phe " + ((DunListWin) DunListWin.dunList.get(i)).lose + ".\n";
                        }
                        server.manager.sendTB(p, "Kết quả", alert);
                        break;
                    }
                    break;
                }
                case 900: {
                    if (p.nj.isNhanban) {
                        p.session.sendMessageLog("Chức năng này không dành cho phân thân");
                        return;
                    }
                    if (menuId == 0) {
                        Service.evaluateCave(p.nj);
                        break label;
                    }
                    Cave cave = null;
                    if (p.nj.caveID != -1) {
                        if (Cave.caves.containsKey(p.nj.caveID)) {
                            cave = Cave.caves.get(p.nj.caveID);
                            p.nj.getPlace().leave(p);
                            cave.map[0].area[0].EnterMap0(p.nj);
                        }
                    } else if (p.nj.party != null && p.nj.party.cave == null && p.nj.party.master != p.nj.id) {
                        p.session.sendMessageLog("Chỉ có nhóm trưởng mới được phép mở cửa hang động");
                        return;
                    }
                    if (cave == null) {
                        if (p.nj.nCave <= 0) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Số lần vào hang động của con hôm nay đã hết hãy quay lại vào ngày mai.");
                            return;
                        }
                        if (menuId == 1) {
                            if (p.nj.getLevel() < 30 || p.nj.getLevel() > 39) {
                                p.session.sendMessageLog("Trình độ không phù hợp");
                                return;
                            }
                            if (p.nj.party != null) {
                                synchronized (p.nj.party.ninjas) {
                                    for (byte i = 0; i < p.nj.party.ninjas.size(); ++i) {
                                        if (p.nj.party.ninjas.get(i).getLevel() < 30 || p.nj.party.ninjas.get(i).getLevel() > 39) {
                                            p.session.sendMessageLog("Thành viên trong nhóm trình độ không phù hợp");
                                            return;
                                        }
                                    }
                                }
                            }
                            if (p.nj.party != null) {
                                if (p.nj.party.cave == null) {
                                    cave = new Cave(3);
                                    p.nj.party.openCave(cave, p.nj.name);
                                } else {
                                    cave = p.nj.party.cave;
                                }
                            } else {
                                cave = new Cave(3);
                            }
                            p.nj.caveID = cave.caveID;
                        }
                        if (menuId == 2) {
                            if (p.nj.getLevel() < 40 || p.nj.getLevel() > 49) {
                                p.session.sendMessageLog("Trình độ không phù hợp");
                                return;
                            }
                            if (p.nj.party != null) {
                                synchronized (p.nj.party) {
                                    for (byte i = 0; i < p.nj.party.ninjas.size(); ++i) {
                                        if (p.nj.party.ninjas.get(i).getLevel() < 40 || p.nj.party.ninjas.get(i).getLevel() > 49) {
                                            p.session.sendMessageLog("Thành viên trong nhóm trình độ không phù hợp");
                                            return;
                                        }
                                    }
                                }
                            }
                            if (p.nj.party != null) {
                                if (p.nj.party.cave == null) {
                                    cave = new Cave(4);
                                    p.nj.party.openCave(cave, p.nj.name);
                                } else {
                                    cave = p.nj.party.cave;
                                }
                            } else {
                                cave = new Cave(4);
                            }
                            p.nj.caveID = cave.caveID;
                        }
                        if (menuId == 3) {
                            if (p.nj.getLevel() < 50 || p.nj.getLevel() > 59) {
                                p.session.sendMessageLog("Trình độ không phù hợp");
                                return;
                            }
                            if (p.nj.party != null) {
                                synchronized (p.nj.party.ninjas) {
                                    for (byte i = 0; i < p.nj.party.ninjas.size(); ++i) {
                                        if (p.nj.party.ninjas.get(i).getLevel() < 50 || p.nj.party.ninjas.get(i).getLevel() > 59) {
                                            p.session.sendMessageLog("Thành viên trong nhóm trình độ không phù hợp");
                                            return;
                                        }
                                    }
                                }
                            }
                            if (p.nj.party != null) {
                                if (p.nj.party.cave == null) {
                                    cave = new Cave(5);
                                    p.nj.party.openCave(cave, p.nj.name);
                                } else {
                                    cave = p.nj.party.cave;
                                }
                            } else {
                                cave = new Cave(5);
                            }
                            p.nj.caveID = cave.caveID;
                        }
                        if (menuId == 4) {
                            if (p.nj.getLevel() < 60 || p.nj.getLevel() > 69) {
                                p.session.sendMessageLog("Trình độ không phù hợp");
                                return;
                            }
                            if (p.nj.party != null && p.nj.party.ninjas.size() > 1) {
                                p.session.sendMessageLog("Hoạt động lần này chỉ được phép một mình");
                                return;
                            }
                            cave = new Cave(6);
                            p.nj.caveID = cave.caveID;
                        }
                        if (menuId == 5) {
                            if (p.nj.getLevel() < 70 || p.nj.getLevel() > 89) {
                                p.session.sendMessageLog("Trình độ không phù hợp");
                                return;
                            }
                            if (p.nj.party != null) {
                                synchronized (p.nj.party.ninjas) {
                                    for (byte i = 0; i < p.nj.party.ninjas.size(); ++i) {
                                        if (p.nj.party.ninjas.get(i).getLevel() < 70) {
                                            p.session.sendMessageLog("Thành viên trong nhóm trình độ không phù hợp");
                                            return;
                                        }
                                    }
                                }
                            }
                            if (p.nj.party != null) {
                                if (p.nj.party.cave == null) {
                                    cave = new Cave(7);
                                    p.nj.party.openCave(cave, p.nj.name);
                                } else {
                                    cave = p.nj.party.cave;
                                }
                            } else {
                                cave = new Cave(7);
                            }
                            p.nj.caveID = cave.caveID;
                        }
                        if (menuId == 6) {
                            if (p.nj.getLevel() < 90 || p.nj.getLevel() > 170) {
                                p.session.sendMessageLog("Trình độ không phù hợp");
                                return;
                            }

                            if (p.nj.party != null && p.nj.party.getKey() != null
                                    && p.nj.party.getKey().get().getLevel() >= 90) {
                                synchronized (p.nj.party.ninjas) {
                                    for (byte i = 0; i < p.nj.party.ninjas.size(); ++i) {
                                        if (p.nj.party.ninjas.get(i).getLevel() < 90 || p.nj.party.ninjas.get(i).getLevel() > 171) {
                                            p.session.sendMessageLog("Thành viên trong nhóm trình độ không phù hợp");
                                            return;
                                        }
                                    }
                                }
                            }

                            if (p.nj.party != null) {
                                if (p.nj.party.cave == null) {
                                    cave = new Cave(9);
                                    p.nj.party.openCave(cave, p.nj.name);
                                } else {
                                    cave = p.nj.party.cave;
                                }
                            } else {
                                cave = new Cave(9);
                            }
                            p.nj.caveID = cave.caveID;
                        }
                        if (cave != null) {
                            final Ninja c = p.nj;
                            --c.nCave;
                            p.nj.pointCave = 0;
                            p.nj.getPlace().leave(p);
                            cave.map[0].area[0].EnterMap0(p.nj);
                        }
                    }
                    p.setPointPB(p.nj.pointCave);
                    break;
                }
                case 1: {
                    if (menuId != 0) {
                        break;
                    }
                    p.typemenu = 1000;
                    doMenuArray(p, new String[]{"Nón", "Áo", "Găng tay", "Quần", "Giày"});
                    break;
                }
                case 1000: {
                    if (menuId == 0) {
                        p.openUI(21 - p.nj.gender);
                        break;
                    }
                    if (menuId == 1) {
                        p.openUI(23 - p.nj.gender);
                        break;
                    }
                    if (menuId == 2) {
                        p.openUI(25 - p.nj.gender);
                        break;
                    }
                    if (menuId == 3) {
                        p.openUI(27 - p.nj.gender);
                        break;
                    }
                    if (menuId == 4) {
                        p.openUI(29 - p.nj.gender);
                        break;
                    }
                    break;
                }
                case 2: {
                    if (menuId == 0) {
                        p.typemenu = 2000;
                        doMenuArray(p, new String[]{"Liên", "Nhẫn", "Ngọc bội", "Phù"});
                        break;
                    } else if (menuId == 1) {
                        if (p.nj.get().getLevel() < 50) {
                            GameCanvas.startOKDlg(p.session, "Yêu cầu cấp độ từ 50 trở lên");
                            return;
                        }
                        p.typemenu = 2001;
                        doMenuArray(p, new String[]{"Nhận", "Trả", "Huỷ", "Nhận Geningan", "Nâng cấp", "Nâng cấp vip", "Hướng dẫn"});
                        break label;
                    } else if (menuId == 2) {
                        int value = util.nextInt(3);
                        if (value == 0) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Con chọn loại trang sức gì nào?");
                        }
                        if (value == 1) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Trang sức không chỉ để ngắm, nó còn tăng sức mạnh của con");
                        }
                        if (value == 2) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Con cần mua ngọc bội, nhẫn, dây chuyền, bùa họ thân à?");
                        }
                        break label;
                    }
                }
                case 2000: {
                    if (menuId == 0) {
                        p.openUI(16);
                        break;
                    } else if (menuId == 1) {
                        p.openUI(17);
                        break;
                    } else if (menuId == 2) {
                        p.openUI(18);
                        break;
                    } else if (menuId == 3) {
                        p.openUI(19);
                        break;
                    }
                    break;
                }
                case 2_001: {
                    ItemData data;
                    byte typeDo;
                    int percent;
                    if (menuId == 0) {
                        if (p.nj.countTaskDanhVong == 0) {
                            p.sendYellowMessage("Con đã hoàn thành đủ số nhiệm vụ cho ngày hôm nay rồi");
                            return;
                        }
                        if (p.nj.isNhanban) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Chức năng không dành cho phân thân");
                            return;
                        }

                        if (p.nj.getLevel() < 0) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Cấp độ của con không đủ để làm nhiệm vụ này");
                            return;
                        }

                        if (p.nj.countTaskDanhVong < 1) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Số lần nhiệm vụ hôm nay của con đã hết");
                            return;
                        }

                        if (p.nj.isTaskDanhVong == 1) {
                            p.sendYellowMessage("Con nãy hoàn thành nhiệm vụ được giao trước.");
                            return;
                        }
                        String nv;
                        int type = DanhVongData.randomNVDV();
                        p.nj.taskDanhVong[0] = type;
                        p.nj.taskDanhVong[1] = 0;
                        p.nj.taskDanhVong[2] = DanhVongData.targetTask(type);
                        typeDo = (byte) util.nextInt(0, 9);
                        percent = p.nj.getLevel() / 10;
                        if (percent > 8) {
                            percent = 8;
                        } else if (percent < 1) {
                            percent = 1;
                        }
                        int xlevel = (byte) util.nextInt(1, percent);
                        for (short i = 0; i < GameScr.itemTemplates.length; ++i) {
                            if (GameScr.itemTemplates[i].level / 10 == xlevel && GameScr.itemTemplates[i].type == typeDo && (GameScr.itemTemplates[i].gender == 2 || GameScr.itemTemplates[i].gender == p.nj.gender) && (!GameScr.itemTemplates[i].isItemClass0() || p.nj.nclass == 0 || !GameScr.itemTemplates[i].isItemClass1() || p.nj.nclass == 1) && (!GameScr.itemTemplates[i].isItemClass2() || p.nj.nclass == 2) && (!GameScr.itemTemplates[i].isItemClass3() || p.nj.nclass == 3) && (!GameScr.itemTemplates[i].isItemClass4() || p.nj.nclass == 4) && (!GameScr.itemTemplates[i].isItemClass5() || p.nj.nclass == 5) && (!GameScr.itemTemplates[i].isItemClass6() || p.nj.nclass == 6)) {
                                p.nj.taskDanhVong[4] = GameScr.itemTemplates[i].id;
                            }
                        }
                        p.nj.isTaskDanhVong = 1;
                        p.nj.countTaskDanhVong--;
                        if (p.nj.isTaskDanhVong == 1) {
                            if (p.nj.taskDanhVong[0] == 4) {
                                nv = String.format(DanhVongData.nameNV1[p.nj.taskDanhVong[0]] + "\n" + "- Nâng cấp " + p.nj.taskDanhVong[1] + "/1" + " trang bị bất kỳ lên cấp độ " + p.nj.taskDanhVong[2] + " < Không tính những trang bị có sẵn >.");
                            } else {
                                nv = String.format(DanhVongData.nameNV1[p.nj.taskDanhVong[0]] + "\n" + "- Sử dụng trang bị " + ItemData.ItemDataId(p.nj.taskDanhVong[4]).name + "." + "\n" + DanhVongData.nameNV[p.nj.taskDanhVong[0]], p.nj.taskDanhVong[1],
                                        p.nj.taskDanhVong[2]);
                            }
                            server.manager.sendTB(p, "Nhiệm vụ", nv);
                        }
                        break;
                    }
                    if (menuId == 1) {
                        if (p.nj.countTaskDanhVong == 0) {
                            p.sendYellowMessage("Con đã hoàn thành đủ số nhiệm vụ cho ngày hôm nay rồi");
                            return;
                        }
                        if (p.nj.isNhanban) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Chức năng không dành cho phân thân.");
                            return;
                        }

                        if (p.nj.isTaskDanhVong == 0) {
                            p.sendYellowMessage("Hiện tại con chưa nhận nhiệm vụ nào");
                            return;
                        }

                        if (p.nj.taskDanhVong[0] != 4) {
                            if (p.nj.taskDanhVong[1] < p.nj.taskDanhVong[2]) {
                                p.sendYellowMessage("Con nãy hoàn thành nhiệm vụ được giao trước.");
                                return;
                            }
                        } else {
                            if (p.nj.taskDanhVong[1] < 1) {
                                p.sendYellowMessage("Con nãy hoàn thành nhiệm vụ được giao trước.");
                                return;
                            }
                        }

                        if (p.nj.getAvailableBag() < 2) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang của con không đủ chỗ trống để nhận thưởng");
                            return;
                        }
                        randomDaDanhVong = DanhVongData.randomDaDanhVong();
                        pointDanhVong = DanhVongData.randomdiemDV();
                        switch (ItemData.ItemDataId(p.nj.taskDanhVong[4]).type) {
                            case 0:
                                server.manager.sendTB(p, "Nhận được", "- " + pointDanhVong + " điểm danh vọng nón" + "\n" + "- " + ItemData.ItemDataId(randomDaDanhVong).name);
                                break;
                            case 1:
                                server.manager.sendTB(p, "Nhận được", "- " + pointDanhVong + " điểm danh vọng vũ khí" + "\n" + "- " + ItemData.ItemDataId(randomDaDanhVong).name);
                                break;
                            case 2:
                                server.manager.sendTB(p, "Nhận được", "- " + pointDanhVong + " điểm danh vọng áo" + "\n" + "- " + ItemData.ItemDataId(randomDaDanhVong).name);
                                break;
                            case 3:
                                server.manager.sendTB(p, "Nhận được", "- " + pointDanhVong + " điểm danh vọng liên" + "\n" + "- " + ItemData.ItemDataId(randomDaDanhVong).name);
                                break;
                            case 4:
                                server.manager.sendTB(p, "Nhận được", "- " + pointDanhVong + " điểm danh vọng găng tay" + "\n" + "- " + ItemData.ItemDataId(randomDaDanhVong).name);
                                break;
                            case 5:
                                server.manager.sendTB(p, "Nhận được", "- " + pointDanhVong + " điểm danh vọng nhẫn" + "\n" + "- " + ItemData.ItemDataId(randomDaDanhVong).name);
                                break;
                            case 6:
                                server.manager.sendTB(p, "Nhận được", "- " + pointDanhVong + " điểm danh vọng quần" + "\n" + "- " + ItemData.ItemDataId(randomDaDanhVong).name);
                                break;
                            case 7:
                                server.manager.sendTB(p, "Nhận được", "- " + pointDanhVong + " điểm danh vọng bội" + "\n" + "- " + ItemData.ItemDataId(randomDaDanhVong).name);
                                break;
                            case 8:
                                server.manager.sendTB(p, "Nhận được", "- " + pointDanhVong + " điểm danh vọng giày" + "\n" + "- " + ItemData.ItemDataId(randomDaDanhVong).name);
                                break;
                            case 9:
                                server.manager.sendTB(p, "Nhận được", "- " + pointDanhVong + " điểm danh vọng phù" + "\n" + "- " + ItemData.ItemDataId(randomDaDanhVong).name);
                                break;
                        }
                        p.nj.plusPointDanhVong(ItemData.ItemDataId(p.nj.taskDanhVong[4]).type, pointDanhVong);
                        Item item = ItemData.itemDefault(randomDaDanhVong, false);
                        item.quantity = util.nextInt(1, 3);
                        item.isLock = false;
                        p.nj.addItemBag(true, item);
                        p.nj.taskDanhVong = new int[]{-1, -1, -1, 0, p.nj.countTaskDanhVong};
                        p.nj.isTaskDanhVong = 0;
                        randomDaDanhVong = -1;
                        pointDanhVong = -1;
                        p.nj.getPlace().chatNPC(p, (short) npcId, "Con hãy nhận lấy phần thưởng của mình.");
                        break;
                    }
                    if (menuId == 2) {
                        if (p.nj.countTaskDanhVong == 0) {
                            p.sendYellowMessage("Con đã hoàn thành đủ số nhiệm vụ cho ngày hôm nay rồi");
                            return;
                        }
                        if (p.nj.isNhanban) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Chức năng không dành cho phân thân");
                            return;
                        }

                        if (p.nj.isTaskDanhVong == 0) {
                            p.sendYellowMessage("Hiện tại con chưa nhận nhiệm vụ nào");
                            return;
                        }

                        Service.startYesNoDlg(p, (byte) 2, "Bạn có muốn huỷ nhiệm vụ " + DanhVongData.nameNV1[p.nj.taskDanhVong[0]] + " hay không?");
                        break;
                    }
                    if (menuId == 3) {
                        if (p.nj.isNhanban) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Chức năng không dành cho phân thân");
                            return;
                        }

                        if (p.nj.checkPointDanhVong(1)) {
                            if (p.nj.getAvailableBag() < 1) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang của con không đủ chỗ trống để nhận thưởng");
                                return;
                            }

                            Item item = ItemData.itemDefault(685, true);
                            item.quantity = 1;
                            item.upgrade = 1;
                            item.isLock = true;
                            Option op = new Option(6, 1000);
                            item.option.add(op);
                            op = new Option(87, 500);
                            item.option.add(op);
                            p.nj.addItemBag(false, item);
                        } else {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Con chưa đủ điểm để nhận mắt");
                        }
                        break;
                    } else if (menuId == 4) {
                        if (p.nj.isNhanban) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Chức năng không dành cho phân thân");
                            return;
                        }

                        if (p.nj.ItemBody[14] == null) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Hãy đeo mắt vào người trước rồi nâng cấp nhé.");
                            return;
                        }

                        if (p.nj.ItemBody[14] == null) {
                            return;
                        }

                        if (p.nj.ItemBody[14].upgrade >= 10) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Mắt của con đã đạt cấp tối đa");
                            return;
                        }

                        if (!p.nj.checkPointDanhVong(p.nj.ItemBody[14].upgrade)) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Con chưa đủ điểm danh vọng để thực hiện nâng cấp");
                            return;
                        }

                        data = ItemData.ItemDataId(p.nj.ItemBody[14].id);
                        Service.startYesNoDlg(p, (byte) 0, "Bạn có muốn nâng cấp " + data.name + " với " + GameScr.coinUpMat[p.nj.ItemBody[14].upgrade] + " yên hoặc xu với tỷ lệ thành công là " + GameScr.percentUpMat[p.nj.ItemBody[14].upgrade] + "% không?");
                        break;
                    } else if (menuId == 5) {
                        if (p.nj.isNhanban) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Chức năng không dành cho phân thân");
                            return;
                        }

                        if (p.nj.ItemBody[14] == null) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Hãy đeo mắt vào người trước rồi nâng cấp nhé.");
                            return;
                        }

                        if (p.nj.ItemBody[14].upgrade >= 10) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Mắt của con đã đạt cấp tối đa");
                            return;
                        }

                        if (!p.nj.checkPointDanhVong(p.nj.ItemBody[14].upgrade)) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Con chưa đủ điểm danh vọng để thực hiện nâng cấp");
                            return;
                        }
                        data = ItemData.ItemDataId(p.nj.ItemBody[14].id);
                        Service.startYesNoDlg(p, (byte) 1, "Bạn có muốn nâng cấp " + data.name + " với " + GameScr.coinUpMat[p.nj.ItemBody[14].upgrade] + " yên hoặc xu và " + GameScr.goldUpMat[p.nj.ItemBody[14].upgrade] + " lượng với tỷ lệ thành công là " + GameScr.percentUpMat[p.nj.ItemBody[14].upgrade] * 2 + "% không?");
                        break;
                    } else if (menuId == 6) {
                        if (p.nj.countTaskDanhVong == 0) {
                            p.sendYellowMessage("Con đã hoàn thành đủ số nhiệm vụ cho ngày hôm nay rồi");
                            return;
                        }
                        if (p.nj.isNhanban) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Chức năng không dành cho phân thân");
                            return;
                        }
                        String nv = "Có thể nhận thêm " + p.nj.countTaskDanhVong + " nhiệm vụ trong ngày.\n- Sử dụng danh vọng phù để tăng số lần nhiệm vụ trong ngày.\n- Có thể sử dụng thêm " + p.nj.useDanhVongPhu + " Danh vọng phù trong ngày.";
                        if (p.nj.isTaskDanhVong == 1) {
                            if (p.nj.taskDanhVong[0] == 4) {
                                nv = String.format(DanhVongData.nameNV1[p.nj.taskDanhVong[0]] + "\n" + "- Nâng cấp " + p.nj.taskDanhVong[1] + "/1" + " trang bị bất kỳ lên cấp độ " + p.nj.taskDanhVong[2] + " < Không tính những trang bị có sẵn >." + "\n" + nv);
                            } else {
                                nv = String.format(DanhVongData.nameNV1[p.nj.taskDanhVong[0]] + "\n" + "- Sử dụng trang bị " + ItemData.ItemDataId(p.nj.taskDanhVong[4]).name + "." + "\n" + DanhVongData.nameNV[p.nj.taskDanhVong[0]] + "\n" + nv, p.nj.taskDanhVong[1],
                                        p.nj.taskDanhVong[2]);
                            }
                        }
                        server.manager.sendTB(p, "Nhiệm vụ", nv);
                        break;
                    }
                    break;
                }
                case 3: {
                    if (menuId == 0) {
                        p.openUI(7);
                        break;
                    }
                    if (menuId == 1) {
                        p.openUI(6);
                        break;
                    }
                    break;
                }
                case 4: {
                    switch (menuId) {
                        case 0: {
                            p.openUI(9);
                            break;
                        }
                        case 1: {
                            p.openUI(8);
                            break;
                        }
                        case 2: {
                            int value = util.nextInt(1);
                            if (value == 0) {
                                p.nj.getPlace().chatNPC(p, (short) 4, "Ăn Xong đảm bảo người sẽ quay lại lần sau");
                            }
                            break;
                        }
                        case 3: {
                            switch (optionId) {
                                case 0: {
                                    // Đăng kí thien dia bang
                                    if (p.nj.get().getLevel() < 50) {
                                        p.nj.getPlace().chatNPC(p, (short) 4, "Yêu cầu trình độ cấp 50");
                                        return;
                                    }
                                    RegisterResult result = null;
                                    if (p.nj.get().getLevel() <= 80) {
                                        result = GeninTournament.gi().register(p);

                                    } else if (p.nj.get().getLevel() > 80 && p.nj.get().getLevel() <= 130) {
                                        result = KageTournament.gi().register(p);
                                    }

                                    if (result != null) {
                                        if (result == RegisterResult.SUCCESS) {
                                            p.nj.getPlace().chatNPC(p, (short) 4, "Bạn đã đăng kí thành công");
                                        } else if (result == RegisterResult.ALREADY_REGISTER) {
                                            p.nj.getPlace().chatNPC(p, (short) 4, "Bạn đã đăng kí thành công rồi");
                                        } else if (result == RegisterResult.LOSE) {
                                            p.nj.getPlace().chatNPC(p, (short) 4, "Bạn đã thua không thể đăng kí được");
                                        }
                                    } else {

                                    }
                                    break;
                                }
                                case 1: {
                                    //Chinh phuc thien dia bang
                                    try {
                                        final List<TournamentData> tournaments = getTypeTournament(p.nj.getLevel()).getChallenges(p);
                                        Service.sendChallenges(tournaments, p);
                                    } catch (Exception e) {

                                    }

                                    break;
                                }
                                case 2: {
                                    //Thien bang
                                    sendThongBaoTDB(p, KageTournament.gi(), "Thiên bảng\n");
                                    break;
                                }
                                case 3: {
                                    // Dia bang
                                    sendThongBaoTDB(p, GeninTournament.gi(), "Địa bảng\n");
                                    break;
                                }
                            }
                            break;
                        }

                    }
                    break;
                }
                case 5: {
                    switch (menuId) {
                        case 0: {
                            p.typemenu = 699;
                            doMenuArray(p, new String[]{"Mở rương", "Mở bộ sưu tập", "Cải trang", "Tháo cải trang"});
                            break;
                        }
                        case 1: {
                            p.nj.mapLTD = p.nj.getPlace().map.id;
                            if (p.nj.mapLTD == 138) {
                                p.nj.mapLTD = 22;
                            }
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Lưu tọa độ thành công, khi kiệt sức con sẽ được khiêng về đây");
                            break;
                        }
                        case 2: {
                            if (optionId != 0) {
                                break;
                            }
                            if (p.nj.getLevel() < 60) {
                                p.session.sendMessageLog("Chức năng yêu cầu trình độ 60");
                                return;
                            }
                            final Manager manager = this.server.manager;
                            final Map ma = Manager.getMapid(139);
                            for (final Place area : ma.area) {
                                if (area.getNumplayers() < ma.template.maxplayers) {
                                    p.nj.getPlace().leave(p);
                                    area.EnterMap0(p.nj);
                                    return;
                                }
                            }
                            break;
                        }
                        case 3:{
                            break;
                        }
                        case 4: {
                            p.nj.ItemGiaHan.clear();
                            for (int i = 0; i < p.nj.ItemBag.length; i++) {
                                Item imb = p.nj.ItemBag[i];
                                if (imb != null && imb.isExpires == true && imb.expires > 0) {
                                    p.nj.ItemGiaHan.add(imb);
                                }
                            }
                            Service.openMenuGiaHan(p);
                            break;
                        }
                        case 5: {
                            p.typemenu = 700;
                            doMenuArray(p, new String[]{"Quà 2h", "Quà cấp 30"});
                            break;
                        }
                    }
                    break;
                }
                case 699: {
                    switch (menuId) {
                        case 0: {
                            Service.openMenuBox(p);
                            break;
                        }
                        case 1: {
                            Service.openMenuBST(p);
                            break;
                        }
                        case 2: {
                            Service.openMenuCaiTrang(p);
                            break;
                        }
                        case 3: {
                            //Tháo cải trang
                            if (p.nj.ItemBodyHide[0] != null) {
                                Service.thaoCaiTrang(p);
                            } else {
                                break;
                            }
                        }
                    }
                    break;
                }
                case 700: {
                    switch (menuId) {
                        case 0: {
                            if (p.nj.Time2H >= 120) {
                                if (p.nj.isGift2H == false) {
                                    p.upluongMessage(500);
                                    p.nj.isGift2H = true;
                                } else {
                                    p.nj.getPlace().chatNPC(p, 5, "Ngươi đã nhận quà hôm nay rồi.");
                                }
                            } else {
                                p.session.sendMessageLog("Ngươi chỉ mới online được " + p.nj.Time2H + " phút");
                            }
                            break;
                        }
                        case 1: {
                            if (p.nj.getLevel() >= 30 && p.nj.isGiftLv30 == false) {
                                p.nj.upyenMessage(50000000);
                                p.upluongMessage(500);
                                p.nj.isGiftLv30 = true;
                            } else {
                                p.nj.getPlace().chatNPC(p, 5, "Trình độ của ngươi chưa đạt yêu cầu hoặc đã nhận thưởng");
                            }
                            break;
                        }
                    }
                    break;
                }
                case 6: {
                    switch (menuId) {
                        case 0: {
                            if (optionId == 0) {
                                p.openUI(10);
                                break;
                            }
                            if (optionId == 1) {
                                p.openUI(31);
                                break;
                            }
                            break;
                        }
                        case 1: {
                            if (optionId == 0) {
                                p.openUI(12);
                                break;
                            }
                            if (optionId == 1) {
                                p.openUI(11);
                                break;
                            }
                            break;
                        }
                        case 2: {
                            p.openUI(13);
                            break;
                        }
                        case 3: {
                            p.openUI(33);
                            break;
                        }
                        case 4: {
                            // Luyen ngoc
                            p.openUI(46);
                            break;
                        }
                        case 5: {
                            // Kham ngoc
                            p.openUI(47);
                            break;
                        }
                        case 6: {
                            // Got ngoc
                            p.openUI(49);
                            break;
                        }
                        case 7: {
                            // Thao ngoc
                            p.openUI(50);
                            break;
                        }
                        case 8: {
                            int value = util.nextInt(3);
                            if (value == 0) {
                                p.nj.getPlace().chatNPC(p, (short) 6, "Người muốn cải tiến trang bị?");
                            }
                            if (value == 1) {
                                p.nj.getPlace().chatNPC(p, (short) 6, "Nâng cấp trang bị: Uy tính, giá cả phải chăng.");
                            }
                            if (value == 2) {
                                p.nj.getPlace().chatNPC(p, (short) 6, "Đảm bảo sau khi nâng cấp đồ của ngươi sẽ tốt hơn hẳn");
                            }
                            break;
                        }
                    }
                    break;
                }
                case 7: {
                    if (menuId == 0) {
                        break;
                    }
                    if (menuId > 0 && menuId <= Map.arrLang.length) {
                        int[] taskId = new int[]{0, 15, 34, 0, 15, 26, 30, 15};
//                        if (p.nj.getTaskId() < taskId[menuId]) {
//                            GameCanvas.startOKDlg(p.session, Text.get(0, 84));
//                            return;
//                        }
                        final Map ma = Manager.getMapid(Map.arrLang[menuId - 1]);
                        for (final Place area : ma.area) {
                            if (area.getNumplayers() < ma.template.maxplayers) {
                                p.nj.getPlace().leave(p);
                                area.EnterMap0(p.nj);
                                return;
                            }
                        }
                        break;
                    }
                    break;
                }
                case 8: {
                    if (menuId >= 0 && menuId < Map.arrTruong.length) {
                        final Map ma = Manager.getMapid(Map.arrTruong[menuId]);
                        for (final Place area : ma.area) {
                            if (area.getNumplayers() < ma.template.maxplayers) {
                                p.nj.getPlace().leave(p);
                                area.EnterMap0(p.nj);
                                return;
                            }
                        }
                        break;
                    }
                    break;
                }
                case 9: {
                    if (menuId == 0) {
                        if (optionId == 0) {
                            this.server.manager.sendTB(p, "Top đại gia yên", BXHManager.getStringBXH(0));
                        } else if (optionId == 1) {
                            this.server.manager.sendTB(p, "Top cao thủ", BXHManager.getStringBXH(1));
                        } else if (optionId == 2) {
                            this.server.manager.sendTB(p, "Top gia tộc", BXHManager.getStringBXH(2));
                        } else if (optionId == 3) {
                            this.server.manager.sendTB(p, "Top hang động", BXHManager.getStringBXH(3));
                        }
                    }
                    if (menuId == 1) {
                        if (p.nj.get().getLevel() < 10) {
                            return;
                        }
                        if (p.nj.get().nclass > 0) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Con đã vào lớp từ trước rồi mà");
                            break;
                        }
                        if (p.nj.get().ItemBody[1] != null) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Con cần tháo vũ khí ra để đến đây nhập học nhé");
                            break;
                        }
                        if (p.nj.getAvailableBag() < 3) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang phải có đủ 2 ô để nhận đồ con nhé");
                            break;
                        }
//                        p.nj.addItemBag(false, ItemData.itemDefault(420));
                        if (optionId == 0) {
                            p.Admission((byte) 1);
                        } else if (optionId == 1) {
                            p.Admission((byte) 2);
                        }
                        p.nj.getPlace().chatNPC(p, (short) npcId, "Hãy chăm chỉ tập luyện để lên cấp con nhé");
                        break;
                    } else {
                        if (menuId != 2) {
                            break;
                        }
                        if (p.nj.get().nclass != 1 && p.nj.get().nclass != 2) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Con không phải học sinh trường này nên không thể tẩy điểm ở đây");
                            break;
                        }
                        if (optionId == 0) {
                            if (p.nj.tayTN < 1) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Con không có đủ số lần tẩy tiềm năng");
                                break;
                            }
                            p.nj.tayTN--;
                            p.restPpoint(p.nj.get());
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Ta đã giúp con tẩy điểm tiềm năng, hãy sử dụng tốt điểm tiềm năng nhé");
                            break;
                        }
                        if (optionId == 1) {
                            if (p.nj.tayKN < 1) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Con không có đủ số lần tẩy kĩ năng");
                                break;
                            }
                            p.nj.tayKN--;
                            p.restSpoint();
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Ta đã giúp con tẩy điểm kĩ năng, hãy sử dụng tốt điểm kĩ năng nhé");
                            break;
                        }

                        break;
                    }
                }
                case 10: {
                    if (menuId == 0) {
                        if (optionId == 0) {
                            this.server.manager.sendTB(p, "Top đại gia yên", BXHManager.getStringBXH(0));
                        } else if (optionId == 1) {
                            this.server.manager.sendTB(p, "Top cao thủ", BXHManager.getStringBXH(1));
                        } else if (optionId == 2) {
                            this.server.manager.sendTB(p, "Top gia tộc", BXHManager.getStringBXH(2));
                        } else if (optionId == 3) {
                            this.server.manager.sendTB(p, "Top hang động", BXHManager.getStringBXH(3));
                        }
                    }
                    if (menuId == 1) {
                        if (p.nj.get().getLevel() < 10) {
                            return;
                        }
                        if (p.nj.get().nclass > 0) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Con đã vào lớp từ trước rồi mà");
                            break;
                        }
                        if (p.nj.get().ItemBody[1] != null) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Con cần tháo vũ khí ra để đến đây nhập học nhé");
                            break;
                        }
                        if (p.nj.getAvailableBag() < 3) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang phải có đủ 2 ô để nhận đồ con nhé");
                            break;
                        }
//                        p.nj.addItemBag(false, ItemData.itemDefault(421));
                        if (optionId == 0) {
                            p.Admission((byte) 3);
                        } else if (optionId == 1) {
                            p.Admission((byte) 4);
                        }
                        p.nj.getPlace().chatNPC(p, (short) 9, "Hãy chăm chỉ tập luyện để lên cấp con nhé");
                        break;
                    } else {
                        if (menuId != 2) {
                            break;
                        }
                        if (p.nj.get().nclass != 3 && p.nj.get().nclass != 4) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Con không phải học sinh trường này nên không thể tẩy điểm ở đây");
                            break;
                        }
                        if (optionId == 0) {
                            if (p.nj.tayTN < 1) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Con không có đủ số lần tẩy tiềm năng");
                                break;
                            }
                            p.nj.tayTN--;
                            p.restPpoint(p.nj.get());
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Ta đã giúp con tẩy điểm tiềm năng, hãy sử dụng tốt điểm tiềm năng nhé");
                            break;
                        }
                        if (optionId == 1) {
                            if (p.nj.tayKN < 1) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Con không có đủ số lần tẩy kĩ năng");
                                break;
                            }
                            p.nj.tayKN--;
                            p.restSpoint();
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Ta đã giúp con tẩy điểm kĩ năng, hãy sử dụng tốt điểm kĩ năng nhé");
                            break;
                        }
                        break;
                    }
                }
                case 11: {
                    if (menuId == 0) {
                        if (optionId == 0) {
                            this.server.manager.sendTB(p, "Top đại gia yên", BXHManager.getStringBXH(0));
                        } else if (optionId == 1) {
                            this.server.manager.sendTB(p, "Top cao thủ", BXHManager.getStringBXH(1));
                        } else if (optionId == 2) {
                            this.server.manager.sendTB(p, "Top gia tộc", BXHManager.getStringBXH(2));
                        } else if (optionId == 3) {
                            this.server.manager.sendTB(p, "Top hang động", BXHManager.getStringBXH(3));
                        }
                    }
                    if (menuId == 1) {
                        if (p.nj.get().getLevel() < 10) {
                            return;
                        }
                        if (p.nj.get().nclass > 0) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Con đã vào lớp từ trước rồi mà");
                            break;
                        }
                        if (p.nj.get().ItemBody[1] != null) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Con cần tháo vũ khí ra để đến đây nhập học nhé");
                            break;
                        }
                        if (p.nj.getAvailableBag() < 3) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang phải có đủ 2 ô để nhận đồ con nhé");
                            break;
                        }
//                        p.nj.addItemBag(false, ItemData.itemDefault(422));
                        if (optionId == 0) {
                            p.Admission((byte) 5);
                        } else if (optionId == 1) {
                            p.Admission((byte) 6);
                        }
                        p.nj.getPlace().chatNPC(p, (short) npcId, "Hãy chăm chỉ tập luyện để lên cấp con nhé");
                        break;
                    } else {
                        if (menuId != 2) {
                            break;
                        }
                        if (p.nj.get().nclass != 5 && p.nj.get().nclass != 6) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Con không phải học sinh trường này nên không thể tẩy điểm ở đây");
                            break;
                        }
                        if (optionId == 0) {
                            if (p.nj.tayTN < 1) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Con không có đủ số lần tẩy tiềm năng");
                                break;
                            }
                            p.nj.tayTN--;
                            p.restPpoint(p.nj.get());
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Ta đã giúp con tẩy điểm tiềm năng, hãy sử dụng tốt điểm tiềm năng nhé");
                            break;
                        }
                        if (optionId == 1) {
                            if (p.nj.tayKN < 1) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Con không có đủ số lần tẩy kĩ năng");
                                break;
                            }
                            p.nj.tayKN--;
                            p.restSpoint();
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Ta đã giúp con tẩy điểm kĩ năng, hãy sử dụng tốt điểm kĩ năng nhé");
                            break;
                        }
                        break;
                    }
                }
                // tajima
                case 12: {
                    if (menuId == 0) {
                        break;
                    } else if (menuId == 2) {
                        p.nj.clearTask();
                        p.nj.getPlace().chatNPC(p, (short) npcId, "Ta đã huỷ hết nhiệm vụ và vật phẩm nhiệm vụ của con lần sau làm nhiệm vụ tốt hơn nhé");
                        Service.finishTask(p.nj);

                        break;
                    } else if (menuId == 3) {
                        if (p.nj.timeRemoveClone > System.currentTimeMillis()) {
                            p.toNhanBan();
                            break;
                        }
                        break;
                    } else if (menuId == 4) {
                        if (!p.nj.clone.isDie && p.nj.timeRemoveClone > System.currentTimeMillis() && p.nj.isNhanban) {
                            p.exitNhanBan(false);
                            p.nj.clone.open(p.nj.timeRemoveClone, p.nj.getPramSkill(71));
                            break;
                        }
                        break;
                    } else if (menuId == 5) {
                        if (p.nj.name.equals("") && p.nj.giftTop == false) {
                            p.upluongMessage(400000L);
                            Item item = ItemData.itemDefault(797);//yy top1
                            item.setLock(false);
                            item.isExpires = false;
                            item.expires = -1;
                            p.nj.addItemBag(false, item);

                            item = ItemData.itemDefault(827);//phb
                            item.setLock(false);
                            item.isExpires = false;
                            item.expires = -1;
                            p.nj.addItemBag(false, item);

                            item = ItemData.itemDefault(832);//pet ung long
                            item.setLock(false);
                            item.isExpires = false;
                            item.expires = -1;
                            p.nj.addItemBag(false, item);

                            p.nj.giftTop = true;
                            break;
                        } else if (p.nj.name.equals("") && p.nj.giftTop == false) {
                            p.upluongMessage(300000L);
                            Item item = ItemData.itemDefault(797);//yy top2
                            item.setLock(false);
                            item.isExpires = false;
                            item.expires = -1;
                            p.nj.addItemBag(false, item);

                            item = ItemData.itemDefault(827);//phb
                            item.setLock(false);
                            item.isExpires = false;
                            item.expires = -1;
                            p.nj.addItemBag(false, item);

                            p.nj.giftTop = true;
                            break;
                        } else {
                            p.session.sendMessageLog("Con Không Có Trong Danh Sách Nhận Quà  !!");
                        }
                        break;
                    }
                    if (menuId == 7) {
                        p.typemenu = 127;
                        if (p.nj.typeEvent == 1) {
                            doMenuArray(p, new String[]{"Tắt nhận vật phẩm sự kiện"});
                        } else {
                            doMenuArray(p, new String[]{"Bật nhận vật phẩm sự kiện"});
                        }
                        break;
                    }
                    if (menuId == 8) {
                        this.server.manager.sendTB(p, "Top Donate", BXHManager.getStringBXH(7));
                                    break;
                    }
                    p.nj.getPlace().chatNPC(p, (short) npcId, "Con đang thực hiện nhiệm vụ kiên trì diệt ác, hãy chọn Menu/Nhiệm vụ để biết mình đang làm đến đâu");
                    break;
                }

                case 127: {
                    if (p.nj.typeEvent == 1) {
                        p.nj.typeEvent = 0;
                        p.session.sendMessageLog("Đã tắt nhận vật phẩm sự kiện");
                        return;
                    }
                    p.nj.typeEvent = 1;
                    p.session.sendMessageLog("Đã bật nhận vật phẩm sự kiện");
                    break;
                }
                
                case 14:
                case 15:
                case 16: {
                    boolean hasItem = false;
                    for (Item item : p.nj.ItemBag) {
                        if (item != null && item.id == 214) {
                            hasItem = true;
                            break;
                        }
                    }
                    if (hasItem) {
                        p.nj.removeItemBags(214, 1);
                        p.nj.getPlace().chatNPC(p, npcId, "Ta rất vui vì cô béo còn quan tâm đến ta.");
                        p.nj.upMainTask();
                    } else {
                        if (p.nj.getTaskId() == 20 && p.nj.getTaskIndex() == 1 && npcId == 15) {
                            p.nj.getPlace().leave(p);
                            final Map map = Server.getMapById(74);
                            val place = map.getFreeArea();
                            synchronized (place) {
                                p.expiredTime = System.currentTimeMillis() + 600000L;
                            }
                            Service.batDauTinhGio(p, 600);
                            place.refreshMobs();
                            place.EnterMap0(p.nj);
                        } else {
                            p.nj.getPlace().chatNPC(p, npcId, "Không có thư để con giao");
                        }
                    }
                    break;
                }
                case 17: {
                    val jaien = Ninja.getNinja("Jaian");
                    jaien.p = new User();
                    jaien.p.nj = jaien;
                    val place = p.nj.getPlace();
                    jaien.upHP(jaien.getMaxHP());
                    jaien.isDie = false;

                    jaien.x = place.map.template.npc[0].x;
                    jaien.id = -p.nj.id;
                    jaien.y = place.map.template.npc[0].y;
                    place.Enter(jaien.p);
                    Place.sendMapInfo(jaien.p, place);
                    break;
                }
                case 18: {
                    switch (menuId) {
                        case 0: {
                            int value = util.nextInt(1);
                            if (value == 0) {
                                p.nj.getPlace().chatNPC(p, (short) 18, "Đây là Làng Chài, do Hiệp quản lý !");
                            }
                            break;
                        }
                    }
                    break;
                }
                case 19: {
                    /*   if (menuId == 0) {
                        if (p.nj.exptype == 0) {
                            p.nj.exptype = 1;
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Đã tắt không nhận kinh nghiệm");
                            break;
                        }
                        p.nj.exptype = 0;
                        p.nj.getPlace().chatNPC(p, (short) npcId, "Đã bật không nhận kinh nghiệm");
                        break;
                    } else if (menuId == 1){
                        p.passold = "";
                        this.sendWrite(p, (short)51, "Nhập mật khẩu cũ");
                        break;
                    } else if (menuId == 2){
                        if (!p.nj.name.equals("admin")) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Chỉ admin mới sử dụng được chức năng này");
                            break;
                        } else {
                            this.sendWrite(p, (short) 53, "Nhập tên nhân vật gửi đồ");
                            break;
                        }
                    } */
                    break;
                }
                case 22: {
                    p.session.sendMessageLog("Chức Năng Đang Bảo Trì !");
//                    if (menuId != 0) {
//                        break;
//                    }
//                    if (p.nj.clan.clanName.isEmpty()) {
//                        p.nj.getPlace().chatNPC(p, (short) npcId, "Con cần phải có gia tộc thì mới có thể điểm danh được nhé");
//                        break;
//                    }
//                    if (p.nj.ddClan) {
//                        p.nj.getPlace().chatNPC(p, (short) npcId, "Hôm nay con đã điểm danh rồi nhé, hãy quay lại đây vào ngày mai");
//                        break;
//                    }
//                    p.nj.ddClan = true;
//                    final ClanManager clan = ClanManager.getClanByName(p.nj.clan.clanName);
//                    if (clan == null) {
//                        p.nj.getPlace().chatNPC(p, (short) npcId, "Gia tộc lỗi");
//                        return;
//                    }
//                    p.upExpClan(util.nextInt(500, 1000) * clan.getLevel());
//                    p.upluongMessage(50 * clan.getLevel());
//                    p.nj.upyenMessage(500000 * clan.getLevel());
//                    p.nj.getPlace().chatNPC(p, (short) npcId, "Điểm danh mỗi ngày sẽ nhận được các phần quà giá trị");
                    break;
                }
                case 25: {
                    switch (menuId) {
                        case 0: {
                            break;
                        }
                        case 1: {
                            p.typemenu = 2500;
                            doMenuArray(p, new String[]{"Nhận", "Hủy", "Hoàn thành", "Đi làm NV"});
                            break;
                        }
                        case 2: {
                            p.typemenu = 2501;
                            doMenuArray(p, new String[]{"Nhận", "Hủy", "Hoàn Thành"});
                            break;
                        }
                        case 3: {
                            p.typemenu = 2502;
                            doMenuArray(p, new String[]{"Bạch Giả", "Hắc Giả", "Tổng kết", "Hướng dẫn"});
                            break;
                        }
                        case 4: {
                            ThatThuAi tta = null;
                            if (p.nj.ttaID != -1) {
                                if (tta.ttas.containsKey(p.nj.ttaID)) {
                                    tta = tta.ttas.get(p.nj.ttaID);
                                    p.nj.getPlace().leave(p);
                                    tta.map[0].area[0].EnterMap0(p.nj);
                                }
                            }
                            if (Server.dangKyTTA == true) {
                                if (p.nj.party != null && p.nj.party.tta == null && p.nj.party.master != p.nj.id) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Chỉ có nhóm trưởng mới được phép Báo danh.");
                                    return;
                                }
                                if (tta == null) {
                                    if (p.nj.getLevel() < 50) {
                                        p.nj.getPlace().chatNPC(p, (short) npcId, "Yêu cầu cấp độ từ 50 trở lên");
                                        return;
                                    }
                                    if (p.nj.party != null) {
                                        if (p.nj.party.ninjas.size() < 6) {
                                            p.nj.getPlace().chatNPC(p, (short) npcId, "Cần phải có đủ 6 thành viên mới có thể tham gia");
                                            return;
                                        }
                                        synchronized (p.nj.party.ninjas) {
                                            for (byte i = 0; i < p.nj.party.ninjas.size(); ++i) {
                                                if (p.nj.party.ninjas.get(i).getLevel() < 50) {
                                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Thành viên trong nhóm trình độ không phù hợp");
                                                    return;
                                                }
                                            }
                                        }
                                        if (p.nj.party.tta == null) {
                                            tta = new ThatThuAi();
                                            p.nj.party.regTTA(tta, p.nj.name);
                                        } else {
                                            tta = p.nj.party.tta;
                                        }
                                        p.nj.ttaID = tta.ttaID;
                                        if (tta != null) {
                                            p.nj.getPlace().leave(p);
                                            tta.map[0].area[0].EnterMap0(p.nj);
                                        }
                                    } else {
                                        p.session.sendMessageLog("Chức năng này cần thành lập nhóm.");
                                    }
                                } else {
                                    p.sendYellowMessage("Lỗi");
                                }
                            } else {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Thất thú ải chỉ mở lúc 19h mỗi ngày");
                            }
                            break;
                        }
                    }
                    break;
                }
                case 2500: {
                    switch (menuId) {
                        case 0: {
                            // Nhiem vu hang ngay
                            if (p.nj.getLevel() < 20) {
                                p.session.sendMessageLog("Yêu cầu trình độ cấp 20");
                                return;
                            }
                            if (p.nj.getTasks()[NHIEM_VU_HANG_NGAY] == null && p.nj.nvhnCount < 20) {
                                val task = createTask(p.nj.getLevel());
                                if (task != null) {
                                    p.nj.addTaskOrder(task);
                                } else {
                                    p.nj.getPlace().chatNPC(p, (short) 25, "Nhiệm vụ lần này có chút trục trặc chắc con không làm được rồi ahihi");
                                }
                            } else if (p.nj.nvhnCount >= 20) {
                                p.nj.getPlace().chatNPC(p, (short) 25, "Hôm nay con đã làm hết nhiệm vụ ta giao. Hãy quay lại vào ngày hôm sau.");
                            } else {
                                p.nj.getPlace().chatNPC(p, (short) 25, "Nhiệm vụ lần trước ta giao cho con vẫn chưa hoàn thành.");
                            }
                            break;
                        }
                        case 1: {
                            // Huy nhiem vu
                            p.nj.huyNhiemVu(NHIEM_VU_HANG_NGAY);
                            break;
                        }
                        case 2: {
                            // Hoan thanh
                            if (!p.nj.hoanThanhNhiemVu(NHIEM_VU_HANG_NGAY)) {
                                p.nj.getPlace().chatNPC(p, (short) 25, "Hãy hoàn thành nhiệm vụ trước rồi đến gặp ta nhận thưởng.");
                            } else {
                                // TODO nhan qua NVHN
                                p.upExpClan(util.nextInt(10, 20));

                                p.upluongMessage(util.nextInt(1, 100));
                                if (util.percent(100, 15)) {
                                    p.nj.upXuMessage(util.nextInt(5_000, 15_000));
                                } else {
                                    p.nj.upyenMessage(util.nextInt(100_000, 250_000));
                                }
                                if ((p.nj.getTaskId() == 30 && p.nj.getTaskIndex() == 1)
                                        || (p.nj.getTaskId() == 39 && p.nj.getTaskIndex() == 3)) {
                                    p.nj.upMainTask();
                                }
                            }
                            break;
                        }
                        case 3: {
                            // Di toi
                            if (p.nj.getTasks() != null
                                    && p.nj.getTasks()[NHIEM_VU_HANG_NGAY] != null) {
                                val task = p.nj.getTasks()[NHIEM_VU_HANG_NGAY];
                                val map = Server.getMapById(task.getMapId());
                                p.nj.setMapid(map.id);
                                for (Npc npc : map.template.npc) {
                                    if (npc.id == 13) {
                                        p.nj.x = npc.x;
                                        p.nj.y = npc.y;
                                        p.nj.getPlace().leave(p);
                                        map.getFreeArea().Enter(p);
                                        break;
                                    }
                                }
                                p.nj.getPlace().chatNPC(p, (short) 25, "Nhiệm vụ lần này gặp lỗi con hãy đi up level lên đi rồi nhận lại nhiệm vụ từ ta");
                            } else {
                                p.nj.getPlace().chatNPC(p, (short) 25, "Hãy nhận nhiệm vụ mỗi ngày từ ta rồi mới sử dụng tính năng này.");
                            }
                            break;
                        }
                    }
                    break;
                }
                case 2501: {
                    switch (menuId) {
                        case 0: {
                            //Nhan nhiem vu
                            if (p.nj.getLevel() < 30) {
                                p.session.sendMessageLog("Yêu cầu trình độ cấp 30");
                                return;
                            }
                            if (p.nj.getTasks()[NHIEM_VU_TA_THU] == null) {
                                if (p.nj.taThuCount > 0) {
                                    val task = createBeastTask(p.nj.getLevel());
                                    if (task != null) {
                                        p.nj.addTaskOrder(task);
                                    } else {
                                        p.nj.getPlace().chatNPC(p, (short) 25, "Nhiệm vụ lần này có chút trục trặc chắc con không làm được rồi ahihi");
                                    }
                                } else {
                                    p.nj.getPlace().chatNPC(p, (short) 25, "Hôm nay con đã làm hết nhiệm vụ ta giao. Hãy quay lại vào ngày hôm sau.");
                                }
                            } else {
                                p.nj.getPlace().chatNPC(p, (short) 25, "Nhiệm vụ lần trước ta giao cho con vẫn chưa hoàn thành.");
                            }
                            break;
                        }
                        case 1: {
                            p.nj.huyNhiemVu(NHIEM_VU_TA_THU);
                            break;
                        }
                        case 2: {
                            if (!p.nj.hoanThanhNhiemVu(NHIEM_VU_TA_THU)) {
                                p.nj.getPlace().chatNPC(p, (short) 25, "Hãy hoàn thành nhiệm vụ trước rồi đến gặp ta nhận thưởng.");
                            } else {
                                val i = ItemData.itemDefault(251);
                                i.quantity = p.nj.get().getLevel() >= 60 ? 5 : 2;
                                p.nj.addItemBag(true, i);
                                if (util.percent(100, 6)) {
                                    p.upluongMessage(util.nextInt(100, 350));
                                } else if (util.percent(100, 30)) {
                                    p.nj.upXuMessage(util.nextInt(5_000, 15_000));
                                } else {
                                    p.nj.upyenMessage(util.nextInt(100_000, 250_000));
                                }
                                if ((p.nj.getTaskId() == 30 && p.nj.getTaskIndex() == 2) || (p.nj.getTaskId() == 39 && p.nj.getTaskIndex() == 1)) {
                                    p.upluongMessage(util.nextInt(MIN_YEN_NVHN * 1, MAX_YEN_NVHN * 5));
                                    p.nj.upMainTask();
                                }
                                p.upExpClan(util.nextInt(10, 20));
                            }
                            break;
                        }
                    }
                    break;
                }
                case 2502: {
                    switch (menuId) {
                        case 0: {
                            // bach
                            p.nj.enterChienTruong(IBattle.CAN_CU_DIA_BACH);
                            break;
                        }
                        case 1: {
                            // hac gia
                            p.nj.enterChienTruong(IBattle.CAN_CU_DIA_HAC);
                            break;
                        }
                        case 2: {
                            Service.sendBattleResult(p.nj, Server.getInstance().globalBattle);
                            break;
                        }
                    }
                    break;
                }
                case 26: {
                    if (menuId == 0) {
                        p.openUI(14);
                        break;
                    }
                    if (menuId == 1) {
                        p.openUI(15);
                        break;
                    }
                    if (menuId == 2) {
                        p.openUI(32);
                        break;
                    }
                    if (menuId == 3) {
                        p.openUI(34);
                        break;
                    }
                    break;
                }
                case 30: {
                    switch (menuId) {
                        case 0: {
                            p.openUI(38);
                            break;
                        }
                        case 1: {
                            this.sendWrite(p, (short) 46, "Nhập mã quà tặng");
                            break;
                        }
                        case 2: {
                            if (optionId == 0) {
                                this.server.manager.rotationluck[0].luckMessage(p);
                                break;
                            }
                            if (optionId == 2) {
                                this.server.manager.sendTB(p, "Vòng xoay vip", "Tham gia đi xem luật lm gì");
                                break;
                            }
                            break;
                        }
                        case 3: {
                            if (optionId == 0) {
                                this.server.manager.rotationluck[1].luckMessage(p);
                                break;
                            }
                            if (optionId == 2) {
                                this.server.manager.sendTB(p, "Vòng xoay thường", "Tham gia đi xem luật lm gì");
                                break;
                            }
                            break;
                        }
                    }
                    break;
                }
                case 32: {
                    switch (menuId) {
                        case 0: {
                            switch (optionId) {
                                case 0: {
                                    // Chien truong keo Tham gia
//                                    Server.candyBattleManager.enter(p.nj);
//                                    break;
                                }
                                case 1: {
                                    // Chien truong keo huong dan
                                    Service.sendThongBao(p.nj, "Chiến trường kẹo:\n"
                                            + "\t- 20 ninja sẽ chia làm 2 đội Kẹo Trăng và Kẹo Đen.\n"
                                            + "\t- Mỗi đội chơi sẽ có nhiệm vụ tấn công giở kẹo của đối phương, nhặt kẹo và sau đó chạy về bỏ vào giỏ kẹo của bên đội mình.\n"
                                            + "\t- Trong khoảng thời gian ninja giữ kẹo sẽ bị mất một lượng HP nhất định theo thời gian.\n"
                                            + "\t- Giữ càng nhiều thì nguy hiểm càng lớn.\n"
                                            + "\t- Còn 10 phú cuối cùng sẽ xuất hiện Phù Thuỷ");
                                    break;
                                }
                            }
                            break;
                        }
                        case 1: {
                            // Option 1
                            val clanManager = ClanManager.getClanByName(p.nj.clan.clanName);
                            if (clanManager != null) {
                                // Có gia tọc và khong battle
                                if (clanManager.getClanBattle() == null) {
                                    //  Chua duoc moi battle
                                    if (p.nj.getClanBattle() == null) {
                                        // La toc truong thach dau
                                        if (p.nj.clan.typeclan == TOC_TRUONG) {
                                            if (clanManager.getClanBattleData() == null
                                                    || (clanManager.getClanBattleData() != null && clanManager.getClanBattleData().isExpired())) {
                                                sendWrite(p, (byte) 4, "Nhập vào gia tộc muốn chiến đấu");
                                            } else {
                                                if (clanManager.restore()) {
                                                    enterClanBattle(p, clanManager);
                                                } else {
                                                    p.nj.getPlace().chatNPC(p, (short) 32, "Không hỗ trợ");
                                                }
                                            }
                                        } else {
                                            // Thử tìm battle data
                                            p.nj.getPlace().chatNPC(p, (short) 32, "Không hỗ trợ");
                                        }
                                    }
                                } else {
                                    enterClanBattle(p, clanManager);
                                }
                            }
                            break;
                        }
                        case 4: {
                            if (optionId == 0) {
                                p.openUI(43);
                            } else if (optionId == 1) {
                                p.openUI(44);
                                break;
                            } else if (optionId == 2) {
                                p.openUI(45);
                                break;
                            }
                            break;
                        }
                    }
                    break;
                }
                case 33: {
                    if (p.typemenu != 33) {
                        break;
                    }
                    switch (this.server.manager.EVENT) {
                        case 1: {
                            if (this.server.manager.EVENT != 1) {
                                return;
                            }
                            switch (menuId) {
                                case 0: {
                                    sendWrite(p, (short) 350, "Nhập số lượng");
                                    break;
                                }
                                case 1: {
                                    sendWrite(p, (short) 351, "Nhập số lượng");
                                    break;
                                }
                                case 2: {
                                    server.manager.sendTB(p, "Hướng dẫn", "- TRE XANH TRĂM ĐỐT [không khóa] = 10 ĐỐT TRE XANH + 30.000 xu" + "\n\n" + "- TRE VÀNG TRĂM ĐỐT [khóa]= 3 TRE XANH TRĂM ĐỐT + 10 TÍN VẬT + 50 Lượng ");
                                    break;
                                }
                                case 3: {
                                    this.server.manager.sendTB(p, "Top SK", BXHManager.getStringBXH(6));
                                    break;
                                }
                            }
                            break;
                        }
//                        case 1 : {
//                            if (this.server.manager.EVENT != 1) {
//                                return;
//                            }
//                            switch (menuId) {
//                                case 0: {
//                                    this.server.manager.sendTB(p, "Top", BXHManager.getStringBXH(6));
//                                }
//                            } break;
//                        }
                        
                    }
                    break;
                }
                // Chức năng NPC Admin
                case 37: {
                    p.typemenu = 37_1;
                    if (p.nj.name.equals("useractive") || p.nj.name.equals("admin")) {
                        this.doMenuArray(p, new String[]{"Nói chuyện", "nâng 120 ô", "bỏ qua nv", "Active Account", "Change the gold exchange value", "Send server notifications", "Maintenance"});
                    } else {
                        this.doMenuArray(p, new String[]{"Nói chuyện", "nâng 120 ô", "bỏ qua nv"});
                    }
                    break;
                }
                case 37_1: {
                    if (menuId == 1) {
                        if (p.nj.maxluggage >= 120) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Giá trị nhập vào không đúng");
                            break;
                        } else if (p.nj.levelBag < 3) {
                            p.session.sendMessageLog("Bạn cần sử dụng túi vải cấp 3 để mở thêm hành trang");
                        } else {
                            p.nj.levelBag = 4;
                            p.nj.maxluggage = 120;
                            p.session.sendMessageLog("Nâng thành công, bạn cần phải thoát game để lưu");
                        }
                        break;
                    } else if (menuId == 2) {
                        if (p.nj.getTaskId() >= 50) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "bạn đã làm xong hết nv");
                            break;
                        } else {
                            p.nj.taskId();
                            p.nj.upMainTask();
                            p.nj.getPlace().chatNPC(p, (short) npcId, "đã bỏ qua hết nv vui lòng thoát game vô lại");
                            }
                        break;
                    } else if (menuId == 3) {
                        if (!p.nj.name.equals("useractive")) {
                            p.session.sendMessageLog("Not supported!");
                            return;
                        } else {
                            this.sendWrite(p, (short) -999, "Nhập tên đăng nhập");
                        }
                    } else if (menuId == 4) {
                        if (!p.nj.name.equals("admin")) {
                            p.session.sendMessageLog("Not supported!");
                            return;
                        } else {
                            this.sendWrite(p, (short) -998, "Nhập số X quy đổi");
                        }
                    } else if (menuId == 5) {
                        if (!p.nj.name.equals("admin")) {
                            p.session.sendMessageLog("Not supported!");
                            return;
                        } else {
                            this.sendWrite(p, (short) -997, "Nhập thông báo");
                        }
                    } else if (menuId == 6) {
                        if (!p.nj.name.equals("admin")) {
                            p.session.sendMessageLog("Not supported!");
                            return;
                        } else {
                            this.sendWrite(p, (short) -996, "Nhập số phút");
                        }
                    }
                    break;
                }
                case 37_2: {
                    if (menuId == 0) {
                        if (p.nj.isNhanban && p.nj.get().exptype == 0) {
                            p.session.sendMessageLog(Language.NOT_FOR_PHAN_THAN);
                            return;
                        }
                        if (p.nj.exptype == 0) {
                            p.nj.exptype = 1;
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Đã tắt không nhận kinh nghiệm");
                            break;
                        }
                        p.nj.exptype = 0;
                        p.nj.getPlace().chatNPC(p, (short) npcId, "Đã bật không nhận kinh nghiệm");
                        break;
                    }
                    break;
                }
                case 92: {
                    p.typemenu = ((menuId == 0) ? 93 : 94);
                    this.doMenuArray(p, new String[]{"Thông tin", "Luật chơi"});
                    break;
                }
                case 93: {
                    if (menuId == 0) {
                        this.server.manager.rotationluck[0].luckMessage(p);
                        break;
                    }
                    if (menuId == 1) {
                        this.server.manager.sendTB(p, "Vòng xoay vip", "Tham gia đi xem luật lm gì");
                        break;
                    }
                    break;
                }
                case 94: {
                    if (menuId == 0) {
                        this.server.manager.rotationluck[1].luckMessage(p);
                        break;
                    }
                    if (menuId == 1) {
                        this.server.manager.sendTB(p, "Vòng xoay thường", "Tham gia đi xem luật lm gì");
                        break;
                    }
                    break;
                }
                case 95: {
                    break;
                }
                //thưởng thăng cấp 60-100
                case 103: {
                    switch (menuId) {
                        case 0: {
                            if (p.nj.getLevel() < 60) {
                                p.session.sendMessageLog("Bạn chưa đủ cấp độ để nhận thưởng.");
                                break;
                            }
                            if (p.nj.getAvailableBag() < 1) {
                                p.session.sendMessageLog("Hành trang đầy.");
                                break;
                            }
                            if (p.nj.reward60 > 0) {
                                p.session.sendMessageLog("Bạn đã nhận thưởng thăng cấp này rồi.");
                                break;
                            } else {
                                p.upluongMessage(50L);
                                p.nj.reward60++;
                                p.session.sendMessageLog("Nhận thưởng thành công. Vui lòng kiểm tra hành trang.");
                            }
                            break;
                        }
                        case 1: {
                            if (p.nj.getLevel() < 70) {
                                p.session.sendMessageLog("Bạn chưa đủ cấp độ để nhận thưởng.");
                                break;
                            }
                            if (p.nj.getAvailableBag() < 1) {
                                p.session.sendMessageLog("Hành trang đầy.");
                                break;
                            }
                            if (p.nj.reward70 > 0) {
                                p.session.sendMessageLog("Bạn đã nhận thưởng thăng cấp này rồi.");
                                break;
                            } else {
                                p.upluongMessage(50L);
                                p.nj.reward70++;
                                p.session.sendMessageLog("Nhận thưởng thành công. Vui lòng kiểm tra hành trang.");
                            }
                            break;
                        }
                        case 2: {
                            if (p.nj.getLevel() < 80) {
                                p.session.sendMessageLog("Bạn chưa đủ cấp độ để nhận thưởng.");
                                break;
                            }
                            if (p.nj.getAvailableBag() < 1) {
                                p.session.sendMessageLog("Hành trang đầy.");
                                break;
                            }
                            if (p.nj.reward80 > 0) {
                                p.session.sendMessageLog("Bạn đã nhận thưởng thăng cấp này rồi.");
                                break;
                            } else {
                                p.upluongMessage(50L);
                                p.nj.reward80++;
                                p.session.sendMessageLog("Nhận thưởng thành công. Vui lòng kiểm tra hành trang.");
                            }
                            break;
                        }
                        case 3: {
                            if (p.nj.getLevel() < 90) {
                                p.session.sendMessageLog("Bạn chưa đủ cấp độ để nhận thưởng.");
                                break;
                            }
                            if (p.nj.getAvailableBag() < 1) {
                                p.session.sendMessageLog("Hành trang đầy.");
                                break;
                            }
                            if (p.nj.reward90 > 0) {
                                p.session.sendMessageLog("Bạn đã nhận thưởng thăng cấp này rồi.");
                                break;
                            } else {
                                p.upluongMessage(50L);
                                p.nj.reward90++;
                                p.session.sendMessageLog("Nhận thưởng thành công. Vui lòng kiểm tra hành trang.");
                            }
                            break;
                        }
                        case 4: {
                            if (p.nj.getLevel() < 100) {
                                p.session.sendMessageLog("Bạn chưa đủ cấp độ để nhận thưởng.");
                                break;
                            }
                            if (p.nj.getAvailableBag() < 1) {
                                p.session.sendMessageLog("Hành trang đầy.");
                                break;
                            }
                            if (p.nj.reward100 > 0) {
                                p.session.sendMessageLog("Bạn đã nhận thưởng thăng cấp này rồi.");
                                break;
                            } else {
                                p.upluongMessage(50L);
                                p.nj.reward100++;
                                p.session.sendMessageLog("Nhận thưởng thành công. Vui lòng kiểm tra hành trang.");
                            }
                            break;
                        }
                    }
                    break;
                }
                case 120: {
                    if (menuId > 0 && menuId < 7) {
                        p.Admission(menuId);
                        break;
                    }
                    break;
                }
                case 23: {
                    // Matsurugi
                    if (ninja.getTaskId() == 23 && ninja.getTaskIndex() == 1 && menuId == 0) {
                        boolean hasItem = false;
                        for (Item item : p.nj.ItemBag) {
                            if (item != null && item.id == 230) {
                                hasItem = true;
                                break;
                            }
                        }

                        if (!hasItem) {
                            val i = ItemData.itemDefault(230);
                            i.setLock(true);
                            p.nj.addItemBag(false, i);
                            p.nj.getPlace().chatNPC(p, 23, "Ta hi vọng đây là lần cuối ta giao chìa khoá cho con ta nghĩ lần này con sẽ làm được. ");
                        } else {
                            p.nj.getPlace().chatNPC(p, 23, "Con đã có chìa khoá rồi không thể nhận thêm được");
                        }
                    } else {
                        p.nj.getPlace().chatNPC(p, 23, "Ta không quen biết con con đi ra đi");
                    }
                    break;
                }
                case 20: {
                    // Soba
                    if (menuId == 0) {
                        if (!ninja.hasItemInBag(266)) {
                            if (ninja.getTaskId() == 32 && ninja.getTaskIndex() == 1) {
                                val item = ItemData.itemDefault(266);
                                item.setLock(true);
                                ninja.addItemBag(false, item);
                            }
                        } else {
                            ninja.p.sendYellowMessage("Con đã có cần câu không thể nhận thêm");
                        }
                    } else {
                        ninja.getPlace().chatNPC(ninja.p, 20, "Làng ta rất thanh bình con có muốn sống ở đây không");
                    }
                    break;
                }
                case 28: {
                    // Shinwa
                    switch (menuId) {
                        case 0: {
                            final List<ItemShinwa> itemShinwas = items.get((int) optionId);
                            Message mess = new Message(103);
                            mess.writer().writeByte(optionId);
                            if (itemShinwas != null) {
                                mess.writer().writeInt(itemShinwas.size());
                                for (ItemShinwa item : itemShinwas) {
                                    val itemStands = item.getItemStand();
                                    mess.writer().writeInt(itemStands.getItemId());
                                    mess.writer().writeInt(itemStands.getTimeEnd());
                                    mess.writer().writeShort(itemStands.getQuantity());
                                    mess.writer().writeUTF(itemStands.getSeller());
                                    mess.writer().writeInt(itemStands.getPrice());
                                    mess.writer().writeShort(itemStands.getItemTemplate());
                                }
                            } else {
                                mess.writer().writeInt(0);
                            }
                            mess.writer().flush();
                            p.sendMessage(mess);
                            mess.cleanup();
                            break;
                        }
                        case 1: {
                            // Sell item
                            p.openUI(36);
                            break;
                        }
                        case 2: {
                            // Get item back

                            for (ItemShinwa itemShinwa : items.get(-2)) {
                                if (p.nj.getAvailableBag() == 0) {
                                    p.sendYellowMessage("Hành trang không đủ ô trống để nhận thêm");
                                    break;
                                }
                                if (itemShinwa != null) {
                                    if (p.nj.name.equals(itemShinwa.getSeller())) {
                                        itemShinwa.item.quantity = itemShinwa.getQuantity();
                                        p.nj.addItemBag(true, itemShinwa.item);
                                        items.get(-2).remove(itemShinwa);
                                        deleteItem(itemShinwa);
                                    }
                                }
                            }
                            break;
                        }
                        case 3: {
                            p.indexMenuBox = 0;
                            Service.openMenuMuaLai(p);
                            break;
                        }
                    }
                    break;
                }
                case 27: {
                    // Cam chia khoa co quan
                    if (Arrays.stream(p.nj.ItemBag).anyMatch(item -> item != null && (item.id == 231 || item.id == 260))) {
                        p.nj.removeItemBags(231, 1);
                        p.nj.removeItemBags(260, 1);
                        p.getClanTerritoryData().getClanTerritory().plugKey(p.nj.getMapid(), p.nj);

                    } else {
//                        p.nj.addItemBag(true, ItemData.itemDefault(260));
                        p.sendYellowMessage("Không có chìa khoá để cắm");
                    }
                    break;
                }

                case 24: {
                        
                   switch (menuId) {
                        case 0: {
                            
                            if (optionId == 0) {
                                if (p.luong < 5000) {
                                    p.session.sendMessageLog("Bạn không đủ lượng.");
                                    break;
                                }
                                p.upluongMessage(-5000);
                                p.nj.upxuMessage(2000000L);
                                LogHistory.log3(p.nj.name + " da doi 50 lg ra 20000000 xu");
                                break;
                            } else if (optionId == 1) {
                                 
                                if (p.luong < 5000) {
                                    p.session.sendMessageLog("Bạn không đủ lượng.");
                                    break;
                                }
                                p.upluongMessage(-5000);
                                p.nj.upyenMessage(2500000L);
                                LogHistory.log3(p.nj.name + " da doi 5000 lg ra 25000000 yen");
                                break;
                            }
                        }
                        case 1: {
//                            if (p.nj.diemhd < 20) {
//                                p.nj.getPlace().chatNPC(p, (short)npcId, "Cần 20 điểm hoạt động để đổi yên qua xu");
//                                break;
//                            }
//                            if (p.nj.yen < 500000) {
//                                p.nj.getPlace().chatNPC(p, (short)npcId, "Bạn không có đủ yên");
//                                break;
//                            }
//                            p.nj.upyenMessage(-5000000L);
//                            p.nj.upxuMessage(100000L);
//                            p.nj.diemhd -= 20;
//                            p.sendYellowMessage("Bạn đã đổi yên ra xu thành công");
                            break;
                        }
                        case 2: {
                            if (optionId == 1) {
                                this.server.manager.sendTB(p, "Bang gia", "1 :10.000 = 5.000 lượng \n" + 
                                        "2 : 50.0000 = 25.000 lượng \n" + 
                                        "3 : 100.0000 = 50.000 lượng \n" + 
                                        "4 : 200.000 = 100.000 lượng \n" + 
                                        "5 : 500.000 = 250.000 lượng \n" + 
                                        "6 : 1.000.000 = 500.000 lượng");
                                break;
                            }
                            break;
                        }
                        case 3: {
                            switch (optionId) {
                                case 0: {
                                    if (p.nj.getLevel() < 10) {
                                        p.session.sendMessageLog("Bạn chưa đủ cấp độ để nhận thưởng.");
                                        break;
                                    }
                                    if (p.nj.getAvailableBag() < 1) {
                                        p.session.sendMessageLog("Hành trang đầy.");
                                        break;
                                    }
                                    if (p.nj.reward10 > 0) {
                                        p.session.sendMessageLog("Bạn đã nhận thưởng thăng cấp này rồi.");
                                        break;
                                    } else {
                                        Item item = ItemData.itemDefault(248);//nấm linh chi // thêm vật phẩm nhận thưởng
                                        item.setLock(true);
                                        p.nj.addItemBag(false, item);
                                        p.upluongMessage(10L);
                                        p.nj.reward10++;
                                        p.session.sendMessageLog("Nhận thưởng thành công. Vui lòng kiểm tra hành trang.");
                                    }
                                    break;
                                }
                                case 1: {
                                    if (p.nj.getLevel() < 20) {
                                        p.session.sendMessageLog("Bạn chưa đủ cấp độ để nhận thưởng.");
                                        break;
                                    }
                                    if (p.nj.getAvailableBag() < 1) {
                                        p.session.sendMessageLog("Hành trang đầy.");
                                        break;
                                    }
                                    if (p.nj.reward20 > 0) {
                                        p.session.sendMessageLog("Bạn đã nhận thưởng thăng cấp này rồi.");
                                        break;
                                    } else {
                                        Item item = ItemData.itemDefault(248);//nấm linh chi // thêm vật phẩm nhận thưởng
                                        item.setLock(true);
                                        p.nj.addItemBag(false, item);
                                        p.upluongMessage(20L);
                                        p.nj.reward20++;
                                        p.session.sendMessageLog("Nhận thưởng thành công. Vui lòng kiểm tra hành trang.");
                                    }
                                    break;
                                }
                                case 2: {
                                    if (p.nj.getLevel() < 30) {
                                        p.session.sendMessageLog("Bạn chưa đủ cấp độ để nhận thưởng.");
                                        break;
                                    }
                                    if (p.nj.getAvailableBag() < 1) {
                                        p.session.sendMessageLog("Hành trang đầy.");
                                        break;
                                    }
                                    if (p.nj.reward30 > 0) {
                                        p.session.sendMessageLog("Bạn đã nhận thưởng thăng cấp này rồi.");
                                        break;
                                    } else {
                                        Item item = ItemData.itemDefault(248);//nấm linh chi // thêm vật phẩm nhận thưởng
                                        item.setLock(true);
                                        p.nj.addItemBag(false, item);
                                        p.upluongMessage(30L);
                                        p.nj.reward30++;
                                        p.session.sendMessageLog("Nhận thưởng thành công. Vui lòng kiểm tra hành trang.");
                                    }
                                    break;
                                }
                                case 3: {
                                    if (p.nj.getLevel() < 40) {
                                        p.session.sendMessageLog("Bạn chưa đủ cấp độ để nhận thưởng.");
                                        break;
                                    }
                                    if (p.nj.getAvailableBag() < 1) {
                                        p.session.sendMessageLog("Hành trang đầy.");
                                        break;
                                    }
                                    if (p.nj.reward40 > 0) {
                                        p.session.sendMessageLog("Bạn đã nhận thưởng thăng cấp này rồi.");
                                        break;
                                    } else {
                                        Item item = ItemData.itemDefault(248);//nấm linh chi // thêm vật phẩm nhận thưởng
                                        item.setLock(true);
                                        p.nj.addItemBag(false, item);
                                        p.upluongMessage(40L);
                                        p.nj.reward40++;
                                        p.session.sendMessageLog("Nhận thưởng thành công. Vui lòng kiểm tra hành trang.");
                                    }
                                    break;
                                }
                                case 4: {
                                    if (p.nj.getLevel() < 50) {
                                        p.session.sendMessageLog("Bạn chưa đủ cấp độ để nhận thưởng.");
                                        break;
                                    }
                                    if (p.nj.getAvailableBag() < 1) {
                                        p.session.sendMessageLog("Hành trang đầy.");
                                        break;
                                    }
                                    if (p.nj.reward50 > 0) {
                                        p.session.sendMessageLog("Bạn đã nhận thưởng thăng cấp này rồi.");
                                        break;
                                    } else {
                                        Item item = ItemData.itemDefault(248);//nấm linh chi // thêm vật phẩm nhận thưởng
                                        item.setLock(true);
                                        p.nj.addItemBag(false, item);
                                        p.upluongMessage(50L);
                                        p.nj.reward50++;
                                        p.session.sendMessageLog("Nhận thưởng thành công. Vui lòng kiểm tra hành trang.");
                                    }
                                    break;
                                }
                            }
                            break;
                        }
                        case 4: {
                            this.sendWrite(p, (short)46, "Nhập mã quà tặng");
                            break;
                        }
                        case 5:{
                            int value = util.nextInt(1);
                            if (value == 0) {
                                p.nj.getPlace().chatNPC(p, (short) 24, "Ta là hiện thân của thần tài sẽ mang đến tài lộc đến cho mọi người");
                            }
                            break;
                        }

                    }
                    break;
                }
                case 24_6: {
                    if (menuId == 0) {
                        p.typemenu = 24_7;
                        doMenuArray(p, new String[] {"Lượng", "Xu"});
                    } else if (menuId == 1) {
                        VND.view(p.nj);
                    } else if (menuId == 2) {
                        server.manager.sendTB(p, "Hướng dẫn", "- 1 ngọc = " + Manager.luongX + " lượng." + "\n- 1 ngọc = 1500 xu.");
                    }
                    break;
                }
                
                case 24_7: {
                    if (menuId == 0) {
                        this.sendWrite(p, (short) 246, "Nhập số lượng");
                    } else if (menuId == 1) {
                        this.sendWrite(p, (short) 247, "Nhập số lượng");
                    }
                    break;
                }
                
                case 251: {
                    switch (menuId) {
                        case 0: {
                            if (p.nj.quantityItemyTotal(251) < 250) {
                                p.session.sendMessageLog("Hành trang không đủ 250 Mảnh giấy vụn");
                                break;
                            }
                            if (p.nj.getAvailableBag() == 0) {
                                p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                                break;
                            }
                            final Item it = ItemData.itemDefault(252);
                            p.nj.addItemBag(false, it);
                            p.nj.removeItemBags(251, 250);
                            break;
                        }
                        case 1: {
                            if (p.nj.quantityItemyTotal(251) < 300) {
                                p.session.sendMessageLog("Hành trang không đủ 300 Mảnh giấy vụn");
                                break;
                            }
                            if (p.nj.getAvailableBag() == 0) {
                                p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                                break;
                            }
                            final Item it = ItemData.itemDefault(253);
                            p.nj.addItemBag(false, it);
                            p.nj.removeItemBags(251, 300);
                            break;
                        }
                    }
                }
                case 572: {
                    switch (menuId) {
                        case 0: {
                            p.typeTBLOption = $240;
                            break;
                        }
                        case 1: {
                            p.typeTBLOption = $480;
                            break;
                        }
                        case 2: {
                            p.typeTBLOption = ALL_MAP;
                            break;
                        }
                        case 3: {
                            p.typeTBLOption = PICK_ALL;
                            break;
                        }
                        case 4: {
                            p.typeTBLOption = USEFUL;
                            break;
                        }
                        case 5: {
                            p.activeTBL = !p.activeTBL;
                        }
                    }
                    break;
                }

                case 34: {
                    if (menuId == 0) {
                        if (p.nj.isNhanban) {
                            p.session.sendMessageLog(Language.NOT_FOR_PHAN_THAN);
                            return;
                        }
                        if (p.nj.getLevel() < 30) {
                            p.session.sendMessageLog("Nhân vật phải trên level 40 mới có thể nhận quà và trang trí.");
                            return;
                        }
                        if (p.nj.quantityItemyTotal(828) < 1) {
                            p.session.sendMessageLog("Bạn không có đủ Quà trang trí để trang trí cây thông Noel.");
                            return;
                        }
                        if (p.nj.getAvailableBag() < 1) {
                            p.session.sendMessageLog("Hành trang không đủ chỗ trống để nhận quà");
                            return;
                        }
                        p.nj.event1_pointCayThongNoel++;
                        p.nj.removeItemBag(p.nj.getIndexBagid(828, false), 1);
                        Item it;
                        if (util.percent(3000, 1)) {
                            it = ItemData.itemDefault(383);
                            it.isExpires = false;
                            it.expires = -1;
                            Manager.chatKTG("Chúc mừng " + p.nj.name + " đã nhận được " + ItemData.ItemDataId(it.id).name + " khi trang trí cây thông");
                        } else if (util.percent(100, 3)) {
                            it = ItemData.itemDefault(775);
                            it.isExpires = false;
                            it.expires = -1;
                        } else if (util.percent(100, 1)) {
                            it = ItemData.itemDefault(524);
                            it.isExpires = false;
                            it.expires = -1;
                        } else {
                            it = ItemData.itemDefault(idItemCayThong[util.nextInt(idItemCayThong.length)]);
                        }
                        if (util.percent(100, 30)) {
                            it.setLock(false);
                        } else {
                            it.setLock(true);
                        }
                        if (it.id == 443 || it.id == 524 || it.id == 485) {
                            it.setLock(false);
                        }
                        it.quantity = 1;
                        p.nj.addItemBag(true, it);
                    } else if (menuId == 1) {
                        if (p.nj.isNhanban) {
                            p.session.sendMessageLog(Language.NOT_FOR_PHAN_THAN);
                            return;
                        }
                        if (p.nj.getLevel() < 30) {
                            p.session.sendMessageLog("Nhân vật phải trên level 40 mới có thể nhận quà và trang trí.");
                            return;
                        }
                        if (p.nj.quantityItemyTotal(673) < 1) {
                            p.session.sendMessageLog("Bạn không có đủ Quà trang trí để trang trí cây thông Noel.");
                            return;
                        }
                        if (p.nj.getAvailableBag() < 1) {
                            p.session.sendMessageLog("Hành trang không đủ chỗ trống để nhận quà");
                            return;
                        }
                        p.nj.removeItemBag(p.nj.getIndexBagid(673, false), 1);
                        Item it;
                        if (util.percent(3000, 1)) {
                            it = ItemData.itemDefault(383);
                            it.isExpires = false;
                            it.expires = -1;
                            Manager.chatKTG("Chúc mừng " + p.nj.name + " đã nhận được " + ItemData.ItemDataId(it.id).name + " khi trang trí cây thông");
                        } else if (util.percent(100, 3)) {
                            it = ItemData.itemDefault(775);
                            it.isExpires = false;
                            it.expires = -1;
                        } else if (util.percent(100, 1)) {
                            it = ItemData.itemDefault(524);
                            it.isExpires = false;
                            it.expires = -1;
                        } else {
                            it = ItemData.itemDefault(idItemCayThong[util.nextInt(idItemCayThong.length)]);
                        }
                        if (util.percent(100, 7)) {
                            it.setLock(false);
                        } else {
                            it.setLock(true);
                        }
                        if (it.id == 443 || it.id == 524 || it.id == 485) {
                            it.setLock(false);
                        }
                        it.quantity = 1;
                        p.nj.addItemBag(true, it);
                    } else if (menuId == 2) {
                        server.manager.sendTB(p, "Hướng dẫn", "- Nhân vật có level 3x trở lên mới có thể trang trí cây thông\n- Quà trang trí có bán ở Goosho bằng xu , các bạn mua quà trang trí bằng xu thì quà trang trí sẽ khóa và tỷ lệ nhận đồ khóa cao.\n- Nếu dùng hộp quà để trang trí cây thông sẽ được tính top\n- Quà của hộp quà giống như quà trang trí bằng xu nhưng tỉ lệ quà không khóa cao hơn.");
                    }
                    break;
                }

                case 40: {
                    switch (menuId) {
                        case 0: {
                            if (p.nj.isNhanban) {
                                p.session.sendMessageLog("Chức năng này không dành cho phân thân");
                                return;
                            }
                            if (p.nj.ItemBody[15] == null) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Hãy mang Bí Kíp vào");
                                return;
                            }
                            if (p.nj.ItemBody[15].expires > 0) {
                                p.session.sendMessageLog("Không thể tinh luyện Bí Kíp có hạn sủ dụng.");
                                return;
                            }
                            if (p.nj.ItemBody[15].option.size() <= 0) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Bí kíp của ngươi chưa có chỉ số");
                                return;
                            }
                            if (p.nj.ItemBody[15].option.get(0).id != 85) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Ngươi không thể tinh luyện Bí Kíp này!");
                                return;
                            }
                            if (p.nj.ItemBody[15].option.get(0).param >= 9) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Bí Kíp đa đạt cấp độ tối đa");
                                return;
                            }
                            Service.startYesNoDlg(p, (byte) 4, "Bạn có muốn tinh luyện Bí Kíp đang sử dụng lên cấp độ " + GameScr.CapDoBK[p.nj.ItemBody[15].option.get(0).param] + " với " + GameScr.XuUpBK[p.nj.ItemBody[15].option.get(0).param] + " xu và " + GameScr.goldUpBK[p.nj.ItemBody[15].option.get(0).param] + " lượng với tỉ lệ " + GameScr.percentUpBK[p.nj.ItemBody[15].option.get(0).param] + "% không?");
                            break;
                        }
                        case 1: {
                            if (p.nj.ItemBody[15] == null) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Hãy mang Bí Kíp vào");
                                return;
                            }
                            if (p.nj.ItemBody[15].expires > 0) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Chỉ được luyện bí kiếp vĩnh viễn");
                                return;
                            }
                            if (p.nj.isNhanban) {
                                p.session.sendMessageLog("Chức năng này không dành cho phân thân");
                                return;
                            }
                            if (p.nj.getAvailableBag() < 1) {
                                p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                                return;
                            }
                            if (p.nj.getLevel() < 60) {
                                p.session.sendMessageLog("Cấp độ của ngươi chưa đạt 60");
                                return;
                            }
                            if (p.luong < 1000) {
                                p.session.sendMessageLog("Không đủ 1000 lượng");
                                return;
                            } else {
                                Item itemUp = p.nj.ItemBody[15];
                                itemUp.option.clear();
                                Option o = new Option(85, 0);
                                itemUp.option.add(o);
                                byte i;
                                int op;
                                Option option2;
                                for (i = 0; i < util.nextInt(1, 7); ++i) {
                                    op = -1;
                                    do {
                                        op = util.nextInt(MenuController.OpIdBK.length);
                                        for (Option option : itemUp.option) {
                                            if (MenuController.OpIdBK[op] == option.id) {
                                                op = -1;
                                                break;
                                            }
                                        }
                                    } while (op == -1);
                                    if (op == -1) {
                                        return;
                                    }
                                    int par = MenuController.ParramOpBK[op];
                                    option2 = new Option(MenuController.OpIdBK[op], par);
                                    itemUp.option.add(option2);
                                }
                                itemUp.quantity = 1;
                                itemUp.isLock = true;
                                itemUp.upgrade = 0;
                                p.nj.addItemBag(false, itemUp);
                                p.upluongMessage(-1000);
                                p.nj.removeItemBody((byte) 15);
                            }
                            switch (util.nextInt(1, 2)) {
                                case 1: {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Đấy chỉ số ngon rồi đấy! Ngươi hãy tin ở ta.");
                                    break;
                                }
                                case 2: {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Mời người xem chỉ số! Ta không làm ngươi thất vọng đâu.");
                                    break;
                                }
                                case 3: {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Ta đã giúp ngươi luyện nó. Hãy xem chỉ số đi nào");
                                    break;
                                }
                            }
                            break;
                        }
                        case 2: {
                            if (p.nj.getAvailableBag() < 1) {
                                p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                                return;
                            }
                            if (p.nj.getLevel() < 60) {
                                p.session.sendMessageLog("Cấp độ của ngươi chưa đạt 60");
                                return;
                            }
                            if (p.luong < 300) {
                                p.session.sendMessageLog("Bạn không có đủ 300 lượng");
                                return;
                            }
                            Item it = ItemData.itemDefault(396 + p.nj.nclass);
                            it.setLock(true);
                            p.nj.addItemBag(true, it);
                            p.upluongMessage(-300L);
                            break;
                        }
                        case 3: // Cái này là xóa hủy bí kíp
                            if (p.nj.ItemBody[15] == null) {
                                p.session.sendMessageLog("Bạn phải đeo bí kiếp mới có thể xóa được nhé");
                                return;
                            }
                            if (p.nj.getLevel() < 60) {
                                p.session.sendMessageLog("Cấp độ của ngươi chưa đạt 60");
                                return;
                            }
                            if (p.luong < 500) {
                                p.session.sendMessageLog("Bạn không có đủ 500 lượng");
                                return;
                            }

                            p.nj.removeItemBody((byte) 15);
                            p.upluongMessage(-500L);
                            break;
                    }
                    break;
                }
                case 41: {
//                    val lognden = Ninja.getNinja("Lồng đèn");
//                    lognden.p = new User();
//                    lognden.p.nj = lognden;
//                    val place = p.nj.getPlace();
//                    lognden.upHP(lognden.getMaxHP());
//                    lognden.isDie = false;
//                    lognden.isNpc = true;
//                    for (Npc npc : place.map.template.npc) {
//                        if (npc.id == 41) {
//                            lognden.x = npc.x;
//                            lognden.y = npc.y;
//                            break;
//                        }
//                    }
//                    lognden.id = -18;
//                    lognden.masterId = p.nj.id;
//                    place.Enter(lognden.p);
//                    Place.sendMapInfo(lognden.p, place);
                    break;
                }

                case 43:
                    switch (menuId) {
                        case 0:
                            Item item = p.nj.ItemBody[12];
                            if (p.nj.isNhanban) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Phân thân không thể sử dụng chức năng này");
                                break;
                            }
                            if (item == null) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Hãy mang Yoroi vào mới có thể nâng cấp");
                                break;
                            }
                            if (item.upgrade == 15) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Yoroi Đã đạt cấp tối đa");
                                break;
                            }
                            if (p.nj.xu < GameScr.Xuup[item.upgrade]) {
                                p.session.sendMessageLog("Không đủ xu");
                                return;
                            }
                            Service.startYesNoDlg(p, (byte) 12, "Bạn có muốn nâng cấp Yoroi Đang sử dụng" 
                                    + " cấp " + (item.upgrade + 1)
                                    + " với " + GameScr.Xuup[item.upgrade]
                                    + " Xu với tỷ lệ thành công là " + GameScr.Tile[item.upgrade]
                                    + "% không?");
                            break;
                           
                    }
                    break;

//                case 43_1: {
//                    switch (menuId) {
//                        case 0: {
//                            Item itemup = p.nj.ItemBody[12];
//                            if (p.nj.ItemBody[12] == null) {
//                                p.nj.getPlace().chatNPC(p, (short) npcId, "Hãy mang Yoroi vào");
//                                return;
//                            }
//                            if (p.nj.ItemBody[12].expires > 0) {
//                                p.nj.getPlace().chatNPC(p, (short) npcId, "Chỉ được luyện yoroi vĩnh viễn");
//                                return;
//                            }
//                            if (p.nj.isNhanban) {
//                                p.session.sendMessageLog("Chức năng này không dành cho phân thân");
//                                return;
//                            }
//                            if (p.nj.ItemBody[12].upgrade == 14) {
//                                p.session.sendMessageLog("Yoroi max cấp độ");
//                                return;
//                            }
//                            if (p.nj.getAvailableBag() < 1) {
//                                p.session.sendMessageLog("Hành trang không đủ chỗ trống");
//                                return;
//                            }
//                            if (p.nj.getLevel() < 70) {
//                                p.session.sendMessageLog("Cấp độ của ngươi chưa đạt 70");
//                                return;
//                            }
//                            if (p.luong < 1000) {
//                                p.session.sendMessageLog("Không đủ 1000 lượng");
//                                return;
//                            }
//                            int[] xu = new int[14];
//                            xu[0] = 10000000;
//                            for (byte i = 1; i < 14; i++) {
//                                xu[i] = xu[i - 1] + 10000000;
//                            }
//                            if (p.nj.xu < xu[p.nj.ItemBody[12].upgrade]) {
//                                p.session.sendMessageLog("Bạn không đủ " + xu[p.nj.ItemBody[12].upgrade] + " xu để nâng cấp yoroi");
//                                return;
//                            }
//                            if (p.nj.quantityItemyTotal(222) < 1 || p.nj.quantityItemyTotal(223) < 1 || p.nj.quantityItemyTotal(224) < 1 || p.nj.quantityItemyTotal(225) < 1 || p.nj.quantityItemyTotal(226) < 1 || p.nj.quantityItemyTotal(227) < 1 || p.nj.quantityItemyTotal(228) < 1) {
//                                p.session.sendMessageLog("Bạn không có đủ 7 viên ngọc rồng 1 - 7 sao để nâng cấp Yoroi.");
//                                return;
//                            }
//                            p.nj.upxuMessage(-(xu[p.nj.ItemBody[12].upgrade]));
//                            for (int i = 222; i <= 228; i++) {
//                                if (p.nj.getIndexBagid(i, false) != -1) {
//                                    p.nj.removeItemBag(p.nj.getIndexBagid(i, false), 1);
//                                } else {
//                                    p.nj.removeItemBag(p.nj.getIndexBagid(i, true), 1);
//                                }
//                            }
                       //   p.nj.ItemBody[12].ncYoroi((byte) 1);
//                            p.nj.addItemBag(false, p.nj.ItemBody[12]);
//                            p.nj.removeItemBody((byte) 12);
//                            break;
//                        }
//                    }
//                    break;
//                }
                case 44: {
                    if (menuId == 0) {
                        if (p.vip < 1) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Vip 2 mới được sử dụng chức năng bật tắt exp");
                            return;
                        }
                        if (p.nj.isNhanban && p.nj.get().exptype == 0) {
                            p.session.sendMessageLog(Language.NOT_FOR_PHAN_THAN);
                            return;
                        }
                        if (p.nj.exptype == 0) {
                            p.nj.exptype = 1;
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Đã tắt không nhận kinh nghiệm");
                            break;
                        }
                        p.nj.exptype = 0;
                        p.nj.getPlace().chatNPC(p, (short) npcId, "Đã bật không nhận kinh nghiệm");
                        break;
                    } else if (menuId == 1) {
                        if (p.vip < 1) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Vip 1 mới được sử dụng chức năng bật tắt exp");
                            return;
                        }
                        if (p.nj.maxluggage >= 120) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Giá trị nhập vào không đúng");
                            break;
                        } else if (p.nj.levelBag < 3) {
                            p.session.sendMessageLog("Bạn cần sử dụng túi vải cấp 3 để mở thêm hành trang");
                        } else {
                            p.nj.levelBag = 4;
                            p.nj.maxluggage = 120;
                            p.session.sendMessageLog("Nâng thành công, bạn cần phải thoát game để lưu");
                        }
                        break;
                    } else if (menuId == 2) {
                        switch (p.vip) {
                            case 1: {
                                if (p.nj.isNhanban) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, Language.NOT_FOR_PHAN_THAN);
                                    return;
                                }
if (p.nj.Time2H >= 120) {
                                if (p.nj.isGift2H == false) {
                                    p.upluongMessage(1000L);
                                    p.nj.upxuMessage(2000000L);
                                    p.nj.isGift2H = true;
                                } else {
                                    p.nj.getPlace().chatNPC(p, 44, "Ngươi đã nhận quà hôm nay rồi.");
                                }
                                } else {
                                    p.session.sendMessageLog("Ngươi chỉ mới online được " + p.nj.Time2H + " phút");
                                }
                                break;
                            }
                            case 2: {
                                if (p.nj.isNhanban) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, Language.NOT_FOR_PHAN_THAN);
                                    return;
                                }

                                if (p.nj.Time2H >= 120) {
                                if (p.nj.isGift2H == false) {
                                    p.upluongMessage(5000L);
                                    p.nj.upxuMessage(4000000L);
                                    p.nj.isGift2H = true;
                                } else {
                                    p.nj.getPlace().chatNPC(p, 44, "Ngươi đã nhận quà hôm nay rồi.");
                                }
                                } else {
                                    p.session.sendMessageLog("Ngươi chỉ mới online được " + p.nj.Time2H + " phút");
                                }
                                break;
                            }
                            case 3: {
                                if (p.nj.isNhanban) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, Language.NOT_FOR_PHAN_THAN);
                                    return;
                                }

                                if (p.nj.Time2H >= 120) {
                                if (p.nj.isGift2H == false) {
                                    p.upluongMessage(10000L);
                                    p.nj.upxuMessage(6000000L);
                                    p.nj.isGift2H = true;
                                } else {
                                    p.nj.getPlace().chatNPC(p, 44, "Ngươi đã nhận quà hôm nay rồi.");
                                }
                                } else {
                                    p.session.sendMessageLog("Ngươi chỉ mới online được " + p.nj.Time2H + " phút");
                                }
                                break;
                            }
                            case 4: {
                                if (p.nj.isNhanban) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, Language.NOT_FOR_PHAN_THAN);
                                    return;
                                }

                                if (p.nj.Time2H >= 120) {
                                if (p.nj.isGift2H == false) {
                                    p.upluongMessage(20000L);
                                    p.nj.upxuMessage(8000000L);
                                    p.nj.isGift2H = true;
                                } else {
                                    p.nj.getPlace().chatNPC(p, 44, "Ngươi đã nhận quà hôm nay rồi.");
                                }
                                } else {
                                    p.session.sendMessageLog("Ngươi chỉ mới online được " + p.nj.Time2H + " phút");
                                }
                                break;
                            }
                            case 5: {
                                if (p.nj.isNhanban) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, Language.NOT_FOR_PHAN_THAN);
                                    return;
                                }

                                if (p.nj.Time2H >= 120) {
                                if (p.nj.isGift2H == false) {
                                    p.upluongMessage(40000L);
                                    p.nj.upxuMessage(10000000L);
                                    p.nj.isGift2H = true;
                                } else {
                                    p.nj.getPlace().chatNPC(p, 44, "Ngươi đã nhận quà hôm nay rồi.");
                                }
                                } else {
                                    p.session.sendMessageLog("Ngươi chỉ mới online được " + p.nj.Time2H + " phút");
                                }
                                break;
                            }
                        }
                        break;
                    }// else if (menuId == 3) {
//                        switch (p.vip) {
//                            case 1: {
//                                if (p.nj.yen >= 30000000 && p.nj.isQuaHangDong == 0) {
//                                    p.nj.isQuaHangDong = 1;
//                                    p.nj.upyenMessage(-500000);
//                                    p.nj.upxuMessage(500000);
//                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Đổi xu thành công!");
//                                    break;
//                                } else if (p.nj.isQuaHangDong != 0) {
//                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Trùm đã đổi xu rồi, xin quay lại vào ngày mai!");
//                                    break;
//                                } else {
//                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Không đủ yên!");
//                                }
//                                break;
//                            }
//                            case 2: {
//                                if (p.nj.yen >= 1000000 && p.nj.isQuaHangDong == 0) {
//                                    p.nj.isQuaHangDong = 1;
//                                    p.nj.upyenMessage(-1000000);
//                                    p.nj.upxuMessage(1000000);
//                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Đổi xu thành công!");
//                                    break;
//                                } else if (p.nj.isQuaHangDong != 0) {
//                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Trùm đã đổi xu rồi, xin quay lại vào ngày mai!");
//                                    break;
//                                } else {
//                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Không đủ yên!");
//                                }
//                                break;
//                            }
//                            case 3: {
//                                if (p.nj.yen >= 2000000 && p.nj.isQuaHangDong == 0) {
//                                    p.nj.isQuaHangDong = 1;
//                                    p.nj.upyenMessage(-2000000);
//                                    p.nj.upxuMessage(2000000);
//                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Đổi xu thành công!");
//                                    break;
//                                } else if (p.nj.isQuaHangDong != 0) {
//                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Trùm đã đổi xu rồi, xin quay lại vào ngày mai!");
//                                    break;
//                                } else {
//                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Không đủ yên!");
//                                }
//                                break;
//                            }
//
//                            case 4: {
//                                if (p.nj.yen >= 5000000 && p.nj.isQuaHangDong == 0) {
//                                    p.nj.isQuaHangDong = 1;
//                                    p.nj.upyenMessage(-5000000);
//                                    p.nj.upxuMessage(5000000);
//                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Đổi xu thành công!");
//                                    break;
//                                } else if (p.nj.isQuaHangDong != 0) {
//                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Trùm đã đổi xu rồi, xin quay lại vào ngày mai!");
//                                    break;
//                                } else {
//                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Không đủ yên!");
//                                }
//                                break;
//                            }
//
//                            case 5: {
//                                if (p.nj.yen >= 15000000 && p.nj.isQuaHangDong == 0) {
//                                    p.nj.isQuaHangDong = 1;
//                                    p.nj.upyenMessage(-15000000);
//                                    p.nj.upxuMessage(15000000);
//                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Đổi xu thành công!");
//                                    break;
//                                } else if (p.nj.isQuaHangDong != 0) {
//                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Trùm đã đổi xu rồi, xin quay lại vào ngày mai!");
//                                    break;
//                                } else {
//                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Không đủ yên!");
//                                }
//                                break;
//                            }
//
//                            case 6: {
//                                if (p.nj.yen >= 30000000 && p.nj.isQuaHangDong == 0) {
//                                    p.nj.isQuaHangDong = 1;
//                                    p.nj.upyenMessage(-30000000);
//                                    p.nj.upxuMessage(30000000);
//                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Đổi xu thành công!");
//                                    break;
//                                } else if (p.nj.isQuaHangDong != 0) {
//                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Trùm đã đổi xu rồi, xin quay lại vào ngày mai!");
//                                    break;
//                                } else {
//                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Không đủ yên!");
//                                }
//                                break;
//                            }
//
//                        }
//                        break;
                    
                else if (menuId == 3) {
                   short [] nam = {712,713,746,747,748,749,750,751,752};
                   short [] nu = {715,716,753,754,755,756,757,758,759};  
                    switch (p.vip){
                       case 1:{
                        if(p.nj.quavip == 1 && p.vip == 1){                                                                              
                            if (p.nj.getAvailableBag() < 1) {
                                p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                                return;
                            }
                             if (p.nj.isNhanban && p.nj.get().exptype == 0) {
                            p.session.sendMessageLog(Language.NOT_FOR_PHAN_THAN);
                            return;
                        }
                             if(p.nj.gender == 1){
                                p.nj.addItemBag(true, ItemData.itemDefault(712));
                                p.nj.addItemBag(true, ItemData.itemDefault(713));
                                p.nj.addItemBag(true, ItemData.itemDefault(746));
                                p.nj.addItemBag(true, ItemData.itemDefault(747));
                                p.nj.addItemBag(true, ItemData.itemDefault(748));
                                p.nj.addItemBag(true, ItemData.itemDefault(749));
                                p.nj.addItemBag(true, ItemData.itemDefault(750));
                                p.nj.addItemBag(true, ItemData.itemDefault(751));
                                p.nj.addItemBag(true, ItemData.itemDefault(752));
                             }else{
                                p.nj.addItemBag(true, ItemData.itemDefault(715));
                                p.nj.addItemBag(true, ItemData.itemDefault(716));
                                p.nj.addItemBag(true, ItemData.itemDefault(753));
                                p.nj.addItemBag(true, ItemData.itemDefault(754));
                                p.nj.addItemBag(true, ItemData.itemDefault(755));
                                p.nj.addItemBag(true, ItemData.itemDefault(756));
                                p.nj.addItemBag(true, ItemData.itemDefault(757));
                                p.nj.addItemBag(true, ItemData.itemDefault(758));
                                p.nj.addItemBag(true, ItemData.itemDefault(759));
                             }
                             p.nj.quavip -= 1;
                             p.session.sendMessageLog("Bạn đã nhận vip 1 thành công");
                             break;                             
                         } 
                        p.nj.getPlace().chatNPC(p, (short) npcId, "Bạn không đủ điều kiện nhận VIP");
                        break;
                    }
                       case 2:{
                           if(p.nj.quavip == 2 && p.vip == 2){                                                                              
                            if (p.nj.getAvailableBag() < 1) {
                                p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                                return;
                            }
                             if (p.nj.isNhanban && p.nj.get().exptype == 0) {
                            p.session.sendMessageLog(Language.NOT_FOR_PHAN_THAN);
                            return;
                        }
                       if(p.nj.gender == 1){
                                for(byte i = 0; i < 9; i++){                                    
                                    Item itemup = ItemData.itemDefault(nam[i]);
                                    itemup.upgradeNext((byte)8);                                   
                                    p.nj.addItemBag(true, itemup);
                           }
                            }else{
                                for(byte i = 0; i < 9; i++){                                    
                                    Item itemup = ItemData.itemDefault(nu[i]);
                                    itemup.upgradeNext((byte)8);                                   
                                    p.nj.addItemBag(true, itemup);
                                }                            
                            }
                       p.nj.quavip -= 2;
                       p.session.sendMessageLog("Bạn đã nhận vip 2 thành công");
                             break;    
                       }
                        p.nj.getPlace().chatNPC(p, (short) npcId, "Bạn không đủ điều kiện nhận VIP");
                        break;    
                       }                       
                  case 3: {
                        if(p.nj.quavip == 3 && p.vip == 3){                                                                              
                            if (p.nj.getAvailableBag() < 1) {
                                p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                                return;
                            }
                             if (p.nj.isNhanban && p.nj.get().exptype == 0) {
                            p.session.sendMessageLog(Language.NOT_FOR_PHAN_THAN);
                            return;
                        }
                       if(p.nj.gender == 1){
                                for(byte i = 0; i < 9; i++){                                    
                                    Item itemup = ItemData.itemDefault(nam[i]);
                                    itemup.upgradeNext((byte)16);                                   
                                    p.nj.addItemBag(true, itemup);
                           }
                            }else{
                                for(byte i = 0; i < 9; i++){                                    
                                    Item itemup = ItemData.itemDefault(nu[i]);
                                    itemup.upgradeNext((byte)16);                                   
                                    p.nj.addItemBag(true, itemup);
                                }                            
                            }
                       p.nj.quavip -= 3;
                       p.session.sendMessageLog("Bạn đã nhận vip 3 thành công");
                             break;    
                       }
                        p.nj.getPlace().chatNPC(p, (short) npcId, "Bạn không đủ điều kiện nhận VIP");
                        break;    
                       }
                  case 4: {
                      short [] ngokhong = {892, 893, 894};
                      if(p.nj.quavip == 4 && p.vip == 4){                                                                              
                            if (p.nj.getAvailableBag() < 1) {
                                p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                                return;
                            }
                             if (p.nj.isNhanban && p.nj.get().exptype == 0) {
                            p.session.sendMessageLog(Language.NOT_FOR_PHAN_THAN);
                            return;
                        }
                             if (p.nj.get().nclass == 0) {
                                p.session.sendMessageLog("Trùm cần nhập học để nhận vip 4");
                                return;
                            }
                      for (byte i = 0; i < 3; i++) {
                                Item itemup = ItemData.itemDefault(ngokhong[i]);
                                itemup.upgradeNext((byte) 16);
                                p.nj.addItemBag(true, itemup);
                            }
                            if (p.nj.get().nclass == 1 || p.nj.get().nclass == 3 || p.nj.get().nclass == 5) {
                                Item itemup = ItemData.itemDefault(891);
                                if (p.nj.get().nclass == 1) {
                                    itemup.sys = 1;
                                } else if (p.nj.get().nclass == 3) {
                                    itemup.sys = 2;
                                } else if (p.nj.get().nclass == 5) {
                                    itemup.sys = 3;
                                }
                                itemup.upgradeNext((byte) 16);
                                p.nj.addItemBag(true, itemup);
                            }
                            if (p.nj.get().nclass == 2 || p.nj.get().nclass == 4 || p.nj.get().nclass == 6) {
                                Item itemup = ItemData.itemDefault(891
                                );
                                if (p.nj.get().nclass == 2) {
                                    itemup.sys = 1;
                                } else if (p.nj.get().nclass == 4) {
                                    itemup.sys = 2;
                                } else if (p.nj.get().nclass == 6) {
                                    itemup.sys = 3;
                                }
                                itemup.upgradeNext((byte) 16);
                                p.nj.addItemBag(true, itemup);
                            }
                  p.nj.quavip -= 4;
                       p.session.sendMessageLog("Bạn đã nhận vip 4 thành công");
                             break;    
                       }
                        p.nj.getPlace().chatNPC(p, (short) npcId, "Bạn không đủ điều kiện nhận VIP");
                        break;    
                       }
                  case 5:{
                      if(p.nj.quavip == 5 && p.vip == 5){                                                                              
                            if (p.nj.getAvailableBag() < 1) {
                                p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                                return;
                            }
                             if (p.nj.isNhanban && p.nj.get().exptype == 0) {
                            p.session.sendMessageLog(Language.NOT_FOR_PHAN_THAN);
                            return;
                        }
                             Item itemup = ItemData.itemDefault(827);
                            p.nj.addItemBag(true, itemup);
                            p.nj.quavip -= 5;
                            p.session.sendMessageLog("Trùm đã nhận vip 5 thành công");
                            break;
                      }p.nj.getPlace().chatNPC(p, (short) npcId, "Bạn không đủ điều kiện nhận VIP");
                        break;    
                  }    
                 }break;}
                else if (menuId == 4) {
                        server.manager.sendTB(p, "Mốc Vip", "- Nạp đủ mốc sẽ nhận thưởng.\n"
                                + "- Vip 1: 20k\n"
                                + "Nhận 1 set Jirai hoặc Yumito + 0\n"
                                + "- Vip 2: 50k\n"
                                + "Nhận 1 set Jirai hoặc Yumito + 8\n"
                                + "- Vip 3: 100k\n"
                                + "Nhận 1 set Jirai hoặc Yumito + 16\n"
                                + "- Vip 4: 200k\n"
                                + "Nhận 1 set Tôn Ngộ Không + 16 mặt nạ có thể nâng cấp\n"
                                + "- Vip 5: 250k\n"
                                + "Nhận 1 Phượng Hoàng Băng siêu VIP\n");
                        break;
                    }
                }
                default: {
                    p.nj.getPlace().chatNPC(p, (short) npcId, "Chức năng này đang cập nhật nhé");
                    break;
                }
            }
        }
//        util.Debug("byte1 " + npcId + " byte2 " + menuId + " byte3 " + optionId);
    }

    private void sendThongBaoTDB(User p, Tournament tournaments, String type) {
        val stringBuilder = new StringBuilder();
        stringBuilder.append(type);
        for (TournamentData tournament : tournaments.getTopTen()) {
            stringBuilder.append(tournament.getRanked())
                    .append(".")
                    .append(tournament.getName())
                    .append("\n");
        }
        Service.sendThongBao(p, stringBuilder.toString());
    }

    public static java.util.Map<Byte, int[]> nangCapMat = new TreeMap<>();

    static {
        nangCapMat.put((byte) 1, new int[]{500, 2_000_000, 80, 200, 100});
        nangCapMat.put((byte) 2, new int[]{400, 3_000_000, 75, 300, 85});
        nangCapMat.put((byte) 3, new int[]{300, 5_000_000, 65, 500, 75});
        nangCapMat.put((byte) 4, new int[]{250, 7_500_000, 55, 700, 65});
        nangCapMat.put((byte) 5, new int[]{200, 8_500_000, 45, 900, 55});
        nangCapMat.put((byte) 6, new int[]{175, 10_000_000, 30, 1000, 45});
        nangCapMat.put((byte) 7, new int[]{150, 12_000_000, 25, 1200, 30});
        nangCapMat.put((byte) 8, new int[]{100, 15_000_000, 20, 1200, 25});
        nangCapMat.put((byte) 9, new int[]{50, 20_000_000, 15, 1500, 20});
    }

    private void nangMat(User p, Item item, boolean vip) throws IOException {

        if (item.id < 694) {
            int toneCount = (int) Arrays.stream(p.nj.ItemBag).filter(i -> i != null && i.id == item.id + 11).map(i -> i.quantity).reduce(0, Integer::sum);
            if (toneCount >= nangCapMat.get(item.getUpgrade())[0]) {

                if (vip && nangCapMat.get(item.getUpgrade())[3] > p.luong) {
                    p.sendYellowMessage("Không đủ lượng nâng cấp vật phẩm");
                    return;
                }
                if (p.nj.xu < nangCapMat.get(item.getUpgrade())[1]) {
                    p.sendYellowMessage("Không đủ xu để nâng cấp");
                    return;
                }
                val succ = util.percent(100, nangCapMat.get(item.getUpgrade())[vip ? 2 : 4]);
                if (succ) {
                    p.nj.get().ItemBody[14] = ItemData.itemDefault(item.id + 1);

                    p.nj.removeItemBags(item.id + 11, nangCapMat.get(item.getUpgrade())[0]);
                    p.sendInfo(false);
                    p.sendYellowMessage("Nâng cấp mắt thành công bạn nhận được mắt " + p.nj.get().ItemBody[14].getData().name + p.nj.get().ItemBody[14].getUpgrade() + " đã mặc trên người");
                } else {
                    p.sendYellowMessage("Nâng cấp mắt thất bại");
                }

                if (vip) {
                    p.removeLuong(nangCapMat.get(item.getUpgrade())[3]);
                }

                p.nj.upxuMessage(-nangCapMat.get(item.getUpgrade())[1]);

            } else {
                p.sendYellowMessage("Không đủ " + nangCapMat.get(item.getUpgrade())[0] + " đá danh vọng cấp " + (item.getUpgrade() + 1) + " để nâng cấp");
            }
        } else {
            p.sendYellowMessage("Mắt được nâng cấp tối đa");
        }
    }

    private void enterClanBattle(User p, ClanManager clanManager) {
        val battle = clanManager.getClanBattle();
        p.nj.setClanBattle(battle);
        if (!clanManager.getClanBattle().enter(p.nj, p.nj.getPhe() == Constants.PK_TRANG ? IBattle.BAO_DANH_GT_BACH
                : IBattle.BAO_DANH_GT_HAC)) {
            p.nj.changeTypePk(Constants.PK_NORMAL);
        }
    }

    public void openUINpc(final User p, Message m) throws IOException {
        final short idnpc = m.reader().readShort();
        m.cleanup();
        p.nj.menuType = 0;
        p.typemenu = idnpc;

        if (idnpc == 33 && server.manager.EVENT != 0) {
            switch (server.manager.EVENT) {
                case 1: {
                    doMenuArray(p, new String[]{"Làm TRE XANH TRĂM ĐỐT", "Làm TRE VÀNG TRĂM ĐỐT", "Hướng dẫn","Top SK"});
                    break;
                }
                case 2: {
                    doMenuArray(p, new String[]{"TOP"});
                    break;
                }
            }
        }
        if (idnpc == 34 && server.manager.EVENT != 0) {
            switch (server.manager.EVENT) {
                case 1: {
                    doMenuArray(p, new String[]{"Hộp quà", "Quà trang trí", "Hướng dẫn"});
                    break;
                }
            }
        }

        if (idnpc == 0 && (p.nj.getPlace().map.isGtcMap() || p.nj.getPlace().map.isLoiDai())) {
            if (p.nj.getPlace().map.dun != null) {
                createMenu(idnpc, new String[]{"Rời khỏi đây", "Đặt cược", "Hướng dẫn"}, "Con có 5 phút để xem thông tin đối phương", p);
            } else if (p.nj.getClanBattle() != null) {
                createMenu(idnpc, new String[]{"Đặt cược", "Rời khỏi đây"}, "Con có 5 phút để xem thông tin đối phương", p);
            }
        } else if (idnpc == Manager.ID_EVENT_NPC) {
            createMenu(Manager.ID_EVENT_NPC, Manager.MENU_EVENT_NPC, Manager.EVENT_NPC_CHAT[util.nextInt(0, Manager.EVENT_NPC_CHAT.length - 1)], p);
        } else if (idnpc == 32 && (p.nj.getPlace().map.id == IBattle.BAO_DANH_GT_BACH || p.nj.getPlace().map.id == IBattle.BAO_DANH_GT_HAC)) {
            createMenu(idnpc, new String[]{"Tổng kết", "Rời khỏi đây"}, "", p);
        } 
        else {
            val ninja = p.nj;
            val npcTemplateId = idnpc;
            p.nj.menuType = 0;
            String[] captions = null;
            if (TaskHandle.isTaskNPC(ninja, npcTemplateId) && ((ninja.nclass == 0 && ninja.getTaskId() < 9) || (ninja.nclass != 0 && ninja.getTaskId() >= 9))) {
                captions = new String[1];
                p.nj.menuType = 1;
                if (ninja.getTaskIndex() == -1) {
                    captions[0] = (TaskList.taskTemplates[ninja.getTaskId()]).name;
                } else if (TaskHandle.isFinishTask(ninja)) {
                    captions[0] = Text.get(0, 12);
                } else if (ninja.getTaskIndex() >= 0 && ninja.getTaskIndex() <= 4 && ninja.getTaskId() == 1) {
                    captions[0] = (TaskList.taskTemplates[ninja.getTaskId()]).name;
                } else if (ninja.getTaskIndex() >= 1 && ninja.getTaskIndex() <= 15 && ninja.getTaskId() == 7) {
                    captions[0] = (TaskList.taskTemplates[ninja.getTaskId()]).name;
                } else if (ninja.getTaskIndex() >= 1 && ninja.getTaskIndex() <= 3 && ninja.getTaskId() == 13) {
                    captions[0] = (TaskList.taskTemplates[ninja.getTaskId()]).name;
                } else if (ninja.getTaskId() >= 11) {
                    captions[0] = TaskList.taskTemplates[ninja.getTaskId()].getMenuByIndex(ninja.getTaskIndex());
                }
            }
            /* else if (idnpc == 22) {
                m = new Message(40);
                if (idnpc == 22) {
                    p.nj.menuType = 1;
                    m.writer().writeUTF("Điểm danh gia tộc");
                }
                m.writer().flush();
                p.session.sendMessage(m);
                m.cleanup();
                return;
            } */
            if (ninja.getTaskId() == 23 && idnpc == 23 && ninja.getTaskIndex() == 1) {
                captions = new String[1];
                captions[0] = "Nhận chìa khoá";
            } else if (ninja.getTaskId() == 32 && idnpc == 20 && ninja.getTaskIndex() == 1) {
                captions = new String[1];
                captions[0] = "Nhận cần câu";
            }
            Service.openUIMenu(ninja, captions);
        }
    }

    @SneakyThrows
    public void selectMenuNpc(final User p, final Message m) throws IOException {

        val idNpc = (short) m.reader().readByte();
        val index = m.reader().readByte();
        if (idNpc == 0 && p.nj.getTaskId() != 13) {
            if (p.nj.getPlace().map.dun != null) {
                if (index == 0) {
                    if (p.nj.isNhanban) {
                        p.nj.getPlace().chatNPC(p, idNpc, Language.NOT_FOR_PHAN_THAN);
                        return;
                    }

                    if (p.nj.party != null && p.nj.party.master != p.nj.id) {
                        p.nj.party.exitParty(p.nj);
                    }

                    p.nj.dunId = -1;
                    p.nj.isInDun = false;
                    p.nj.getPlace().leave(p);
                    p.restCave();
                    p.changeMap(p.nj.mapKanata);
                } else if (index == 1) {
                    if (p.nj.isNhanban) {
                        p.nj.getPlace().chatNPC(p, idNpc, Language.NOT_FOR_PHAN_THAN);
                        return;
                    }

                    if (p.nj.party != null && p.nj.party.master != p.nj.id) {
                        p.nj.getPlace().chatNPC(p, idNpc, "Con không phải nhóm trưởng, không thể đặt cược");
                        return;
                    }
                    server.menu.sendWrite(p, (short) 3, "Nhập xu cược");
                }
            } else {
                if (index == 0) {
                    server.menu.sendWrite(p, (short) 6, "Nhập số tiền cược");
                } else if (index == 1) {
                    if (p.nj.getBattle() != null) {
                        p.nj.getBattle().setState(Battle.BATTLE_END_STATE);
                    }
                }
            }
        } else if (idNpc == 32 && p.nj.getPlace().map.isGtcMap()) {
            if (index == 0) {
                // Tong ket
                Service.sendBattleResult(p.nj, p.nj.getClanBattle());
            } else if (index == 1) {

                // Roi khoi day
                p.nj.changeTypePkNormal(Constants.PK_NORMAL);
                p.nj.getPlace().gotoHaruna(p);
            }
             } else   if(p.nj.getTaskId() == 0 && p.nj.getTaskIndex() ==3)  {
                           
            if (index == 0) {
                // Tong ket
                p.nj.upMainTask();
            }
        } else {
            TaskHandle.getTask(p.nj, (byte) idNpc, index, (byte) -1);
        }
        m.cleanup();
    }

    public static void lamSuKien(User p, EventItem entry) throws IOException {
        boolean enough = true;
        boolean enough2 = false;
        for (Recipe input : entry.getInputs()) {
            int id = input.getId();
            enough = p.nj.enoughItemId(id, input.getCount());
            if (!enough) {
                p.nj.getPlace().chatNPC(p, (short) 33, "Con không đủ " + input.getItemData().name + " để làm sự kiện");
                break;
            }
            if (entry.getIdRequired() != -1) {
                enough2 = p.nj.enoughItemId(entry.getIdRequired(), 1);
                if (!enough2) {
                    p.nj.getPlace().chatNPC(p, (short) 33, "Con không đủ " + ItemData.ItemDataId(entry.getIdRequired()).name + " để làm sự kiện");
                    break;
                }
            } else {
                enough2 = true;
            }
        }
        if (enough && enough2 && p.nj.xu >= entry.getCoin() && p.nj.yen >= entry.getCoinLock() && p.luong >= entry.getGold()) {
            for (Recipe input : entry.getInputs()) {
                p.nj.removeItemBags(input.getId(), input.getCount());
            }
            if (entry.getIdRequired() != -1) {
                p.nj.removeItemBags(entry.getIdRequired(), 1);
            }
            p.nj.addItemBag(true, entry.getOutput().getItem());
            p.nj.upxuMessage(-entry.getCoin());
            p.nj.upyenMessage(-entry.getCoinLock());
            p.upluongMessage(-entry.getGold());
        }
    }

    private boolean receiverSingleItem(User p, short idItem, int count) {
        if (!p.nj.checkHanhTrang(count)) {
            p.sendYellowMessage(MSG_HANH_TRANG);
            return true;
        }
        for (int i = 0; i < count; i++) {
            p.nj.addItemBag(false, ItemData.itemDefault(idItem));
        }
        return false;
    }

    @SneakyThrows
    public static void createMenu(int idNpc, String[] menu, String npcChat, User p) {
        val m = new Message(39);
        m.writer().writeShort(idNpc);
        m.writer().writeUTF(npcChat);
        m.writer().writeByte(menu.length);
        for (String s : menu) {
            m.writer().writeUTF(s);
        }
        m.writer().flush();
        p.sendMessage(m);
        m.cleanup();
    }

    public static void doMenuArray(final User p, final String[] menu) throws IOException {
        final Message m = new Message(63);
        for (byte i = 0; i < menu.length; ++i) {
            m.writer().writeUTF(menu[i]);
        }
        m.writer().flush();
        p.sendMessage(m);
        m.cleanup();
    }

    public void sendWrite(final User p, final short type, final String title) {
        try {
            final Message m = new Message(92);
            m.writer().writeUTF(title);
            m.writer().writeShort(type);
            m.writer().flush();
            p.sendMessage(m);
            m.cleanup();
        } catch (IOException ex) {
        }
    }
}
