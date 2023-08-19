package real;

import org.jetbrains.annotations.NotNull;
import threading.Message;
import io.Session;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import server.Service;
import server.util;

public class PlayerManager {
    protected static PlayerManager instance;
    private boolean runing;
    public final List<Session> conns;
    public static  HashMap<String, Long> timeWaitLogin = new HashMap();
    private final HashMap<Integer, Session> conns_id;
    private final HashMap<Integer, User> players_id;
    private final HashMap<String, User> players_uname;
    private final HashMap<Integer, Ninja> ninjas_id;
    private final HashMap<String, Ninja> ninjas_name;
    public static final Object LOCK = new Object();

    public PlayerManager() {
        this.runing = true;
        this.conns = new CopyOnWriteArrayList<>();
        this.conns_id = new HashMap<>();
        this.players_id = new HashMap<>();
        this.players_uname = new HashMap<>();
        this.ninjas_id = new HashMap<>();
        this.ninjas_name = new HashMap<>();
    }

    private final static ReadWriteLock lock = new ReentrantReadWriteLock(true);

    @NotNull
    public static PlayerManager getInstance() {
        if (PlayerManager.instance == null) {
            PlayerManager.instance = new PlayerManager();
        }
        return PlayerManager.instance;
    }


    public void NinjaMessage(final Message m) {
        for (int i = this.conns.size() - 1; i >= 0; --i) {
            if (this.conns.get(i).user != null && this.conns.get(i).user.nj != null) {
                this.conns.get(i).sendMessage(m);
            }
        }
    }

    public void put(final Session conn) {
        this.conns_id.put(conn.id, conn);
        this.conns.add(conn);
    }

    public void put(final User p) {
        this.players_id.put(p.id, p);
        this.players_uname.put(p.username, p);
    }

    public void put(final Ninja n) {
        this.ninjas_id.put(n.id, n);
        this.ninjas_name.put(n.name, n);
    }

    private void remove(final Session conn) {
        if (conns_id.containsKey(conn.id)) {
            this.conns_id.remove(conn.id);
        }
        if (conns.contains(conn)) {
            this.conns.remove(conn);
        }
        if (conn.user != null) {
            this.remove(conn.user);
        }if (conn.user != null) {
            if(!PlayerManager.timeWaitLogin.containsKey(conn.user.username)) {
                PlayerManager.timeWaitLogin.put(conn.user.username, System.currentTimeMillis() + util.nextInt(0000, 0000));
            }
            this.remove(conn.user);
        }
    }

    private void remove(final User p) {
        if (players_id.containsKey(p.id)) {
            this.players_id.remove(p.id);
        }
        if (players_uname.containsKey(p.username)) {
            this.players_uname.remove(p.username);
        }
        if (p.nj != null) {
            this.remove(p.nj);
        }
        p.close();
        p.flush();
    }

    private void remove(final Ninja n) {
        if (ninjas_id.containsKey(n.id)) {
            this.ninjas_id.remove(n.id);
        }
        if (ninjas_name.containsKey(n.name)) {
            this.ninjas_name.remove(n.name);
        }
        if(n.getPlace() != null && n.getPlace().map.id == 111 && n.getDun() != null) {
            if(n.getDun().c1.id == n.id && n.getPlace().map.id == 111) {
                n.getDun().c1 = null;
                n.getDun().team1.remove(n);
                n.getDun().check1();
            } else if(n.getDun().c2.id == n.id && n.getPlace().map.id == 111) {
                n.getDun().c2 = null;
                n.getDun().team2.remove(n);
                n.getDun().check1();
            }
        }
        else if(n.isInDun && n.dunId != -1) {
            Dun dun = null;
            if (Dun.duns.containsKey(n.dunId)) {
                dun = Dun.duns.get(n.c.dunId);

                if (dun.c1 != null && dun.c1.id == n.id) {
                    dun.c1 = null;
                }
                if (dun.c2 != null && dun.c2.id == n.id) {
                    dun.c2 = null;
                }
                if (dun.team1.size() > 0 && dun.team1 != null) {
                    synchronized (dun.team1) {
                        if (dun.team1.contains(n)) {
                            dun.team1.remove(n);
                        }
                    }
                }

                if(dun.team2.size() > 0 && dun.team2 != null) {
                    synchronized (dun.team2) {
                        if (dun.team2.contains(n)){
                            dun.team2.remove(n);
                        }
                    }
                }

                if(dun.viewer.size() > 0 && dun.viewer != null) {
                    synchronized (dun.viewer) {
                        if (dun.viewer.contains(n)){
                            dun.viewer.remove(n);
                        }
                    }
                }
            }
            Service.ChangTypePkId(n, (byte)0);
            n.isInDun = false;
            n.dunId = -1;
        }
        n.close();
        n.flush();
        if (n.clone != null) {
            n.clone.flush();
        }
    }

    public Session getConn(final int id) {
        return this.conns_id.get(id);
    }

    public User getPlayer(final int id) {
        return this.players_id.get(id);
    }

    public User getPlayer(final String uname) {
        return this.players_uname.get(uname);
    }

    public Ninja getNinja(final int id) {
        return this.ninjas_id.get(id);
    }

    public Ninja getNinja(final String name) {
        return this.ninjas_name.get(name);
    }

    public int conns_size() {
        return this.conns_id.size();
    }

    public int players_size() {
        return this.players_id.size();
    }

    public int ninja_size() {
        return this.ninjas_id.size();
    }

    public void kickSession(final Session conn) {
        if (conn != null) {
            this.remove(conn);
            if (conn.user != null &&
                    conn.user.nj != null && conn.user.nj.getPlace() != null) {
                conn.user.nj.getPlace().leave(conn.user);
            }
        }
    }
    public void kickSession_Fix(Session ss) {
        this.remove(ss);
        ss.disconnect();
    }

    public void Clear() {
        while (!this.conns.isEmpty()) {
            this.kickSession(this.conns.get(0));
        }
    }

    public void close() {
        this.runing = false;
        PlayerManager.instance = null;
    }

    public void sendLogAll(String m) {
        synchronized(this.conns) {
            for(int i = this.conns.size() - 1; i >= 0; --i) {
                if (((Session)this.conns.get(i)).user != null && ((Session)this.conns.get(i)).user.nj != null) {
                    ((Session)this.conns.get(i)).sendMessageLog(m);
                }
            }
        }
    }
}
