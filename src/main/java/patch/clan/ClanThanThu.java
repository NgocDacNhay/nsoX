package patch.clan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import real.Item;
import real.ItemData;
import server.util;

import java.io.Serializable;
import java.util.Objects;

import static patch.clan.ClanThanThu.EvolveStatus.NOT_ENOUGH_STARS;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClanThanThu implements Serializable {

    private int type;
    public Item petItem;
    
    public volatile int level;
    public volatile int star;
    
    public volatile int curEXP;
    public volatile int maxEXP;

    public ClanThanThu() {
    }

    public ClanThanThu(Item petItem, int curEXP, int maxEXP, int type, int level, int star) {
        this.petItem = petItem;
        this.curEXP = curEXP;
        this.maxEXP = maxEXP;
        this.type = type;
        this.level = level;
        this.star = star;
    }

    @JsonIgnore
    public int getThanThuId() {
        if (petItem.id == 584) {
            return 2502;
        } else if (petItem.id == 585) {
            return 2503;
        } else if (petItem.id == 586) {
            return 2504;
        } else if (petItem.id == 587) {
            return 2506;
        } else if (petItem.id == 588) {
            return 2507;
        } else if (petItem.id == 589) {
            return 2508;
        }
        return 2505;
    }

    @JsonIgnore
    public int getStars() {
        return star;
    }

    @JsonIgnore
    public int getThanThuIconId() {
        if (petItem.id == 584) {
            return 2484;
        } else if (petItem.id == 585) {
            return 2485;
        } else if (petItem.id == 586) {
            return 2486;
        } else if (petItem.id == 587) {
            return 2487;
        } else if (petItem.id == 588) {
            return 2488;
        } else if (petItem.id == 589) {
            return 2489;
        }
        return 2490;
    }

    public boolean upExp(int exp) {
        Item im = this.getPetItem();
        if (level < star * 40) {
            this.curEXP += exp;
            int expOld = this.curEXP;
            if (this.curEXP > this.maxEXP) {
                level++;
                this.curEXP = expOld - this.maxEXP;
                for (int i = 0; i < im.option.size(); i++) {
                    if (star == 1) {
                        if (im.option.get(i).id == 102) {
                            im.option.get(i).param += 350;
                        } else if (im.option.get(i).id == 103) {
                            im.option.get(i).param += 30;
                        }
                    } else if (star == 2) {
                        if (im.option.get(i).id == 102) {
                            im.option.get(i).param += 400;
                        } else if (im.option.get(i).id == 103) {
                            im.option.get(i).param += 60;
                        }
                    } else if (star == 3) {
                        if (im.option.get(i).id == 102) {
                            im.option.get(i).param += 450;
                        } else if (im.option.get(i).id == 103) {
                            im.option.get(i).param += 90;
                        }
                    }
                }
            }
        } else {
            this.curEXP += exp;
            if (this.curEXP > this.maxEXP) {
                this.curEXP = this.maxEXP;
                return false;
            }
        }
        return true;
    }

    public int getOption(int index) {
        if (getStars() == 1) {
            return this.petItem.option.get(index).param;
        } else if (getStars() == 2) {
            return this.petItem.option.get(index).param * 120 / 100;
        } else {
            return this.petItem.option.get(index).param * 140 / 100;
        }
    }

    public enum EvolveStatus {
        SUCCESS,
        FAIL,
        MAX_LEVEL,
        NOT_ENOUGH_STARS
    }

    @NotNull
    public EvolveStatus evolve() {
        if (this.petItem.id == 586 || this.petItem.id == 589) {
            return EvolveStatus.MAX_LEVEL;
        }
        if (this.level == 40 * this.star && star < 3) {
            if (this.petItem.id == 584 || this.petItem.id == 585 || this.petItem.id == 587 || this.petItem.id == 588) {
                if (util.percent(100, 20)) {
                    star += 1;
                    this.petItem = ItemData.itemDefault(this.petItem.id + 1);
                    this.curEXP -= this.maxEXP;
                    return EvolveStatus.SUCCESS;
                } else {
                    return EvolveStatus.FAIL;
                }
            } else {
                return EvolveStatus.FAIL;
            }
        } else {
            return NOT_ENOUGH_STARS;
        }
    }

    @JsonIgnore
    public String getName() {
        if (petItem == null) {
            return "Không tồn tại";
        }
        return petItem.getData().name;
    }

    public static int MAX_THAN_THU_EXPS = 10_000;
    public static int HAI_MA_1_ID = 584;
    public static int HAI_MA_2_ID = 585;
    public static int HAI_MA_3_ID = 586;
    public static int DI_LONG_1_ID = 587;
    public static int HOA_LONG_ID = 583;
    public static int DI_LONG_2_ID = 588;
    public static int DI_LONG_3_ID = 589;
    public static int ST_QUAI_ID = 102;
    public static int ST_NGUOI_ID = 103;
    public static int TYPE_DI_LONG = 0;
    public static int TYPE_HAI_MA = 1;
    public static int TYPE_HOA_LONG = 2;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClanThanThu that = (ClanThanThu) o;
        return Objects.equals(this.type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }
}
