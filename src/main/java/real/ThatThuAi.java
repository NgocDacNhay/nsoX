package real;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import threading.Manager;
import threading.Map;
import threading.Server;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ThatThuAi {

    public int ttaID;
    public long time;
    public byte finsh;
    private static int idbase;
    public int lvTTA;
    public int timesRefeshPillar;
    public long timeWait;

    @NotNull
    public List<@Nullable Ninja> ninjas;
    @NotNull
    public Map[] map;
    @NotNull
    Server server;
    @NotNull
    public static final HashMap<@NotNull Integer, @NotNull ThatThuAi> ttas;

    public ThatThuAi() {
        this.lvTTA = 0;
        this.finsh = 0;
        this.timesRefeshPillar = 5;
        this.ninjas = new CopyOnWriteArrayList<>();
        this.server = Server.getInstance();
        this.ttaID = ThatThuAi.idbase++;
        this.time = Server.timeTTA;
        this.timeWait = Server.timeWaitTTA;
        this.map = new Map[2];
        this.initMap();
        for (byte i = 0; i < this.map.length; ++i) {
            this.map[i].timeMap = this.time;
        }
        ThatThuAi.ttas.put(this.ttaID, this);
    }

    private void initMap() {
        this.map[0] = new Map(113, null, this, null);
        this.map[1] = new Map(112, null, this, null);
    }

    public void updateXP(final long xp) {
        for (short i = 0; i < this.ninjas.size(); ++i) {
            try {
                this.ninjas.get(i).p.updateExp(xp, true);
            } catch (Exception ex) {

            }
        }
    }

    public void rest() {
            synchronized (this) {
                while (this.ninjas.size() > 0) {
                    final Ninja nj = this.ninjas.get(0);
                    nj.getPlace().leave(nj.p);
                    nj.p.restTta();
                    final Manager manager = this.server.manager;
                    final Map ma = Manager.getMapid(nj.mapLTD);
                    if (ma != null) {
                        for (byte k = 0; k < ma.area.length; ++k) {
                            if (ma.area[k].getNumplayers() < ma.template.maxplayers) {
                                ma.area[k].EnterMap0(nj);
                                break;
                            }
                        }
                    }
                }
            for (byte i = 0; i < this.map.length; ++i) {
                this.map[i].close();
            }
            synchronized (ThatThuAi.ttas) {
                ThatThuAi.ttas.remove(this.ttaID);
            }
        }
    }

    public void finsh() {
        synchronized (this) {
            this.lvTTA++;
            this.time = System.currentTimeMillis() + 10000L;
            for (byte u = 0; u < this.map.length; ++u) {
                this.map[u].timeMap = this.time;
            }
            if (this.finsh == 0) {
                ++this.finsh;
                for (byte i = 0; i < this.ninjas.size(); ++i) {
                    final Ninja nj = this.ninjas.get(i);
                    nj.p.setTimeMap((int) (this.time - System.currentTimeMillis()) / 1000);
                    nj.p.sendYellowMessage("Boss đã bị tiêu diệt, Thất Thú Ải kết thúc.");
                    if (nj.party != null && nj.party.tta != null) {
                        nj.party.tta = null;
                    }
                    if (!nj.clan.clanName.isEmpty()) {
                        nj.p.upExpClan(10);
                    }
                }
            }
        }
    }

    static {
        ttas = new HashMap<>();
    }
}
