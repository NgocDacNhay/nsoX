package real;

import boardGame.Place;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import patch.Constants;
import patch.interfaces.TeamBattle;
import server.Service;
import server.util;
import threading.Manager;
import threading.Map;
import threading.Server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import patch.interfaces.SendMessage;

public class Battle {


    public static short MATCHING_WAIT_FOR_INFORMATION = 300;
    public static short MATCHING_WAIT_DURATION = 60;
    public static short MATCHING_END_DURATION = 10;

    public static short MATCHING_DURATION = 10 * 60;
  
    public static long MIN_XU = 1000;
    public static long Max_XU = 10000;
    public static int DAT_CUOC_STATE = 0;
    public static int DOI_1_PHUT_STATE = 1;
    public static int CHIEN_DAU_STATE = 2;
    public static int BATTLE_END_STATE = 3;
    
     public long timeout; //thời gian đếm ngược của map

    public static int BATTLE_Y_RANGE_MAX = 264;
    public static int BATTLE_Y_RANGE_MIN = 240;
    public long expiredTime;
    @NotNull
    private static final AtomicInteger baseId = new AtomicInteger(0);
    @NotNull
    public TeamBattle team1;
    @NotNull
    public TeamBattle team2;
    private long xu1;
    private long xu2;
    private long finalXu = 0;
    public long startTime;
    public long startTime2;
    public long finish1;
    public long finish2;
    private int state;
    private final int id;
    @NotNull
    public static ConcurrentHashMap<Integer, Battle> battles = new ConcurrentHashMap<>();
    private Place place;
    @Nullable
    public List<@NotNull Ninja> viewer;
    private final int $10_SEC = 10_000;
    private int timeLength;
    
    public List<@NotNull Ninja> TEAM1;
    public List<@NotNull Ninja> TEAM2;
    public Ninja c1 = null;
    public Ninja c2 = null;

    public Battle(final @NotNull Ninja ninja1,@NotNull Ninja ninja2) {
        
        if (ninja1.party != null) {
            team1 = ninja1.party;
        } else {
            team1 = ninja1;
        }
        if (ninja2.party != null) {
            team2 = ninja2.party;
        } else {
            team2 = ninja2;
        }
        viewer = new ArrayList<>();
        this.id = Battle.baseId.getAndIncrement();
        TEAM1 = new ArrayList<>();
        TEAM2 = new ArrayList<>();
        if (ninja1.party != null) {
            for (int i = 0; i < ninja1.party.ninjas.size(); i++) {
                Ninja njP = (Ninja) ninja1.party.ninjas.get(i);
                TEAM1.add(njP);
                c1 = njP;
            }
        } else {
            TEAM1.add(ninja1);
            c1 = ninja1;
        }
        if (ninja2.party != null) {
            for (int i = 0; i < ninja2.party.ninjas.size(); i++) {
                Ninja njP = (Ninja) ninja2.party.ninjas.get(i);
                TEAM1.add(njP);
                c2 = njP;
            }
        } else {
            TEAM2.add(ninja2);
            c2 = ninja2;
        }
        this.team1.setBattle(this);
        this.team2.setBattle(this);
        xu2 = 0;
        xu1 = 0;
        this.setState(DAT_CUOC_STATE);
        addBattle(this);
    }
    
    public void kickUser() {
        val haruna = Server.getInstance().getMaps()[27];
            val place = haruna.getFreeArea();
           
           if (this.team1.getCurrentMapId() == 111) {
                this.team1.enterSamePlace(place, null);
            }
           
 
            if (this.team2.getCurrentMapId() == 111) {
                this.team2.enterSamePlace(place, null);
            } 
        
            assert place != null;
            team1.changeTypePk(Constants.PK_NORMAL, team2);
            team2.changeTypePk(Constants.PK_NORMAL, team1);
            long testtime = this.time - this.startTime;
            System.out.println("debug time " + testtime + "");
          
            for (Ninja ninja : this.viewer) {
                ninja.enterSamePlace(place, null);
                ninja.isBattleViewer = false;
            }

            this.team1.clearBattle();
            this.team2.clearBattle();
            this.viewer.clear();
            this.viewer = null;
            this.team2 = null;
            this.team1 = null;
    }

    public int getState() {
        return this.state;
    }
 

    public void setState(int state) {
        this.tick();
    

        this.state = state;

        if (this.state == Battle.DAT_CUOC_STATE) {
            Service.batDauTinhGio(team1, MATCHING_WAIT_FOR_INFORMATION);
            Service.batDauTinhGio(team2, MATCHING_WAIT_FOR_INFORMATION);
        } else if (this.state == Battle.DOI_1_PHUT_STATE) {
            team1.updateEffect(new Effect(14, 0, MATCHING_WAIT_DURATION * 1000, 0));
            team2.updateEffect(new Effect(14, 0, MATCHING_WAIT_DURATION * 1000, 0));
             team1.changeTypePk(Constants.PK_TRANG, team2);
            team2.changeTypePk(Constants.PK_DEN, team1);
            Service.batDauTinhGio(team1, 0);
            Service.batDauTinhGio(team2, 0);
            Manager.serverChat("Server", team1.getTeamName() + " (" + team1.getKeyLevel() + ") đang thách đấu với " +
                    team2.getTeamName() + " (" + team2.getKeyLevel() + ") " + finalXu + " xu ở lôi đài.");
        } else if (this.state == Battle.CHIEN_DAU_STATE) {
         //  Manager.serverChat("Server", team1.getTeamName() + " (" + team1.getKeyLevel() + ") đang thách đấu với " +
            //        team2.getTeamName() + " (" + team2.getKeyLevel() + ") " + finalXu + " xu ở lôi đài.");
            team1.changeTypePk(Constants.PK_TRANG, team2);
            team2.changeTypePk(Constants.PK_DEN, team1);
            Service.batDauTinhGio(team1, MATCHING_DURATION);
            Service.batDauTinhGio(team2, MATCHING_DURATION);
            val msg = "Trận đấu bắt đầu";
            thongBao(msg);
        } else if (this.state == BATTLE_END_STATE) {
            Service.batDauTinhGio(team1, 10);
            Service.batDauTinhGio(team2, 10);
              for (Ninja ninja : this.viewer) {
             Service.batDauTinhGio(ninja, 10);
            }
        }

    }


    public void setXu(long xu,final @NotNull TeamBattle team) {
        
         if (xu > Max_XU) {
            team.notifyMessage("Xu đặt tối thiểu 10000 xu");
            return;
        }
        if (xu < MIN_XU) {
            team.notifyMessage("Xu đặt tối thiểu 1000 xu");
            return;
        }

        if (team == team1) {

            if (team.getXu() >= xu) {
                this.xu1 = xu;
            } else {
                team1.notifyMessage("Con không đủ xu để đặt trận đấu kết thúc");
                team2.notifyMessage("Đối thủ không đủ xu để đặt trận đấu kết thúc");
                setState(BATTLE_END_STATE);
                return;
            }
        } else if (team == team2) {
            if (team.getXu() >= xu) {
                this.xu2 = xu;

            } else {
                team1.notifyMessage("Đối thủ không đủ xu để đặt trận đấu kết thúc");
                team2.notifyMessage("Con không đủ xu để đặt trận đấu kết thúc");
                setState(BATTLE_END_STATE);
                return;
            }
        }


        String msg;
        if (this.canStart()) {
            // Start
            this.start();
            msg = "Các con có 1 phút để chuẩn bị cho trận đấu ";
        } else {
            msg = team.getTeamName() + " đã thay đổi số tiền đặt cược là " + xu;
        }
        thongBao(msg);
    }

    private void thongBao(String msg) {
        this.team1.notifyMessage(msg);
        this.team2.notifyMessage(msg);
    }


    public long getFinalXu() {
        return finalXu;
    }

    public boolean canStart() {
        return this.xu1 == this.xu2 && !this.isExpired();
    }

    public void start() {


        this.finalXu = this.xu1;

        Server.getInstance();
        Map loiDai = Server.getMapById(111);
        this.place = loiDai.getFreeArea();
        if (place != null) {
            team1.enterSamePlace(place, this.team2);
        } else {
            thongBao("Hiện tại lôi đài đang quá tải # quay thử lại sau nhé");
        }
        this.tick();
        this.setState(DOI_1_PHUT_STATE);
        

        Server.executorService.submit(() -> {
            try {
                Thread.sleep(MATCHING_WAIT_DURATION * 1000);
                this.setState(CHIEN_DAU_STATE);
                 
                util.Debug("STOP COUNT DOWN BATTLE");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
 public void tick1() {
        this.startTime2 = System.currentTimeMillis();
    }
    public void tick() {
        this.startTime = System.currentTimeMillis();
    }


    public boolean isExpired() {
        long timeLimit;

        if (state == Battle.DOI_1_PHUT_STATE) {
            timeLimit = MATCHING_WAIT_DURATION;
        } else if (state == Battle.CHIEN_DAU_STATE) {
            timeLimit = MATCHING_DURATION;          
        } else if (state == Battle.DAT_CUOC_STATE) {
            timeLimit = MATCHING_WAIT_FOR_INFORMATION;
        } else if (state == Battle.BATTLE_END_STATE) {
            timeLimit = 10;
        } else {
            throw new RuntimeException("State undefined");
        }

        return System.currentTimeMillis() - this.startTime > timeLimit * 1000;
    }

    public static void addBattle(final @NotNull Battle battle) {
        battles.put(battle.id, battle);
    }
  public long time;
    public synchronized void updateWinner(@NotNull final TeamBattle team) {  
        TeamBattle winner = null;
        TeamBattle looser = null;
        if (team == team1) {
            winner = team1;
            looser = team2;
        } else if (team == team2) {
            winner = team2;
            looser = team1;
        }

        looser.notifyMessage("# đã bị " + winner.getTeamName() + " đánh bại");
        looser.upXuMessage(-getFinalXu());

        winner.upXuMessage(getFinalXu());
        for (Ninja ninja : winner.getNinjas()) {
            if (ninja != null && ninja.getTaskId() == 42 && ninja.getTaskIndex() == 1) {
                ninja.upMainTask();
            }
        }
        for (Ninja ninja : this.viewer) {
           ninja.notifyMessage("# đã đánh bại " + looser.getTeamName() + " và nhận được " + getFinalXu());
           }
        winner.notifyMessage( winner.getTeamName() + " đã đánh bại " + looser.getTeamName() + " và nhận được " + getFinalXu());
         this.tick();
        this.setState(BATTLE_END_STATE);              
          
    }
                 

                  
                    
          
    
        
        
    

    public void enter() {
        Server.getInstance();
        Map waitingMap = Server.getMapById(110);
        Place freeArea = waitingMap.getFreeArea();
        if (freeArea == null) {
            this.team1.notifyMessage("Hiện tại phòng chờ đăng còn đông lắm con quay lại sau");
            this.team2.notifyMessage("Hiện tại phòng chờ đăng còn đông lắm con quay lại sau");
        }
        this.team1.enterSamePlace(freeArea, this.team2);
    }

    @NotNull
    public String getTeam1Name() {
        return this.team1.getTeamName() + " (" + team1.getKeyLevel() + ")";
    }

    @NotNull
    public String getTeam2Name() {
        return this.team2.getTeamName() + " (" + team2.getKeyLevel() + ")";
    }

    public Place getPlace() {
        return this.place;
    }

    public void addViewerIfNotInMatch(Ninja ninja) {
        if (ninja != team1 && ninja != team2) {
            this.viewer.add(ninja);
        }
    }
}
