package real;

public class ThoiTrang {
    protected static byte HAIR = 0;
    protected static byte Body = 1;
    protected static byte LEG = 2;
    protected static byte WEA_PONE = 3;
    protected static byte PP = 4;
    protected static byte NAME = 5;
    protected static byte HORSE = 6;
    protected static byte RANK = 7;
    protected static byte MAT_NA = 8;
    protected static byte Bien_Hinh = 9;
    
    protected static void setThoiTrang(Ninja nj, short templateId) {
        switch (templateId) {
            case 903: {
                nj.setThoiTrang[0] = 129;
                nj.setThoiTrang[1] = 130;
                nj.setThoiTrang[2] = 131;
                break;
            }case 904: {
                nj.setThoiTrang[0] = 138;
                nj.setThoiTrang[1] = 139;
                nj.setThoiTrang[2] = 140;
                break;
            }
            case 895: {
                nj.setThoiTrang[0] = 174;
                nj.setThoiTrang[1] = 175;
                nj.setThoiTrang[2] = 176;
                break;
            }
            case 896: {
                nj.setThoiTrang[0] = 171;
                nj.setThoiTrang[1] = 172;
                nj.setThoiTrang[2] = 173;
                break;
            }
            case 795: {
                nj.setThoiTrang[0] = 37;
                nj.setThoiTrang[1] = 38;
                nj.setThoiTrang[2] = 39;
                break;
            }
            case 796: {
                nj.setThoiTrang[0] = 40;
                nj.setThoiTrang[1] = 41;
                nj.setThoiTrang[2] = 42;
                break;
            }
            case 797: {
                nj.setThoiTrang[4] = 43;
                break;
            }
            case 798: {
                nj.setThoiTrang[6] = 36;
                break;
            }
            case 799: {
                nj.setThoiTrang[3] = 44;
                break;
            }
            case 800: {
                nj.setThoiTrang[3] = 46;
                break;
            }
            case 891: {
                nj.setThoiTrang[3] = 44;
                break;
            }
            case 897: {
                nj.setThoiTrang[3] = 162;
                break;
            }
            case 898: {
                nj.setThoiTrang[3] = 159;
                break;
            }
            case 899: {
                nj.setThoiTrang[3] = 164;
                break;
            }
            case 900: {
                nj.setThoiTrang[3] = 161;
                break;
            }
            case 901: {
                nj.setThoiTrang[3] = 163;
                break;
            }
            case 902: {
                nj.setThoiTrang[3] = 160;
                break;
            }
            case 801: {
                nj.setThoiTrang[6] = 47;
                break;
            }
            case 802: {
                nj.setThoiTrang[6] = 48;
                break;
            }
            case 803: {
                nj.setThoiTrang[6] = 49;
                break;
            }
            case 804: {
                nj.setThoiTrang[0] = 58;
                nj.setThoiTrang[1] = 59;
                nj.setThoiTrang[2] = 60;
                break;
            }
            case 805: {
                nj.setThoiTrang[0] = 55;
                nj.setThoiTrang[1] = 56;
                nj.setThoiTrang[2] = 57;
                break;
            }
            case 813: {
                nj.setThoiTrang[8] = 54;
                break;
            }
            case 814: {
                nj.setThoiTrang[8] = 53;
                break;
            }
            case 815: {
                nj.setThoiTrang[8] = 52;
                break;
            }
            case 816: {
                nj.setThoiTrang[8] = 51;
                break;
            }
            case 817: {
                nj.setThoiTrang[8] = 50;
                break;
            }
            case 825: {
                nj.setThoiTrang[5] = 61;
                break;
            }
            case 826: {
                nj.setThoiTrang[5] = 62;
                break;
            }
            case 827: {
                nj.setThoiTrang[6] = 63;
                break;
            }
            case 842: {
                nj.setThoiTrang[6] = 117;
                break;
            }
            case 830: {
                nj.setThoiTrang[0] = (short) (69 - nj.gender * 3);
                nj.setThoiTrang[1] = (short) (70 - nj.gender * 3);
                nj.setThoiTrang[2] = (short) (71 - nj.gender * 3);
                break;
            }
            case 831: {
                nj.setThoiTrang[6] = 72;
                break;
            }
        }
    }
    
    protected static void removeThoiTrang(Ninja nj, short templateId) {
        switch (templateId) {       
            case 903:
            case 904:
            case 895:
            case 896: {
                nj.setThoiTrang[0] = -1;
                nj.setThoiTrang[1] = -1;
                nj.setThoiTrang[2] = -1;
                break;
            }
            case 795:
            case 796: {
                nj.setThoiTrang[0] = -1;
                nj.setThoiTrang[1] = -1;
                nj.setThoiTrang[2] = -1;
                break;
            }
            case 797: {
                nj.setThoiTrang[4] = -1;
                break;
            }
            case 798: {
                nj.setThoiTrang[6] = -1;
                break;
            }
            case 897:
            case 898:
            case 899:
            case 900:
            case 901:
            case 902: {
                nj.setThoiTrang[3] = -1;
                break;
            }
            case 799:
            case 800: {
                nj.setThoiTrang[3] = -1;
                break;
            }
            case 801:
            case 802:
            case 803: {
                nj.setThoiTrang[6] = -1;
                break;
            }
            case 804:
            case 805: {
                nj.setThoiTrang[0] = -1;
                nj.setThoiTrang[1] = -1;
                nj.setThoiTrang[2] = -1;
                break;
            }
            case 813:
            case 814:
            case 815:
            case 816:
            case 817: {
                nj.setThoiTrang[8] = -1;
                break;
            }
            case 825:
            case 826: {
                nj.setThoiTrang[5] = -1;
                break;
            }
            case 827: {
                nj.setThoiTrang[6] = -1;
                break;
            }
            case 842: {
                nj.setThoiTrang[6] = -1;
                break;
            }
            case 830: {
                nj.setThoiTrang[0] = -1;
                nj.setThoiTrang[1] = -1;
                nj.setThoiTrang[2] = -1;
                break;
            }
            case 831: {
                nj.setThoiTrang[6] = -1;
                break;
            }
        }
    }
    
    protected static void setThoiTrangPT(CloneChar _cl, short templateId) {
        switch (templateId) {
            case 903: {
                _cl.setThoiTrang[0] = 129;
                _cl.setThoiTrang[1] = 130;
                _cl.setThoiTrang[2] = 131;
                break;
            }case 904: {
                _cl.setThoiTrang[0] = 138;
                _cl.setThoiTrang[1] = 139;
                _cl.setThoiTrang[2] = 140;
                break;
            }
            case 895: {
                _cl.setThoiTrang[0] = 174;
                _cl.setThoiTrang[1] = 175;
                _cl.setThoiTrang[2] = 176;
                break;
            }
            case 896: {
                _cl.setThoiTrang[0] = 171;
                _cl.setThoiTrang[1] = 172;
                _cl.setThoiTrang[2] = 173;
                break;
            }
            case 795: {
                _cl.setThoiTrang[0] = 37;
                _cl.setThoiTrang[1] = 38;
                _cl.setThoiTrang[2] = 39;
                break;
            }
            case 796: {
                _cl.setThoiTrang[0] = 40;
                _cl.setThoiTrang[1] = 41;
                _cl.setThoiTrang[2] = 42;
                break;
            }
            case 797: {
                _cl.setThoiTrang[4] = 43;
                break;
            }
            case 798: {
                _cl.setThoiTrang[6] = 36;
                break;
            }
            case 897: {
                _cl.setThoiTrang[3] = 162;
                break;
            }
            case 898: {
                _cl.setThoiTrang[3] = 159;
                break;
            }
            case 899: {
                _cl.setThoiTrang[3] = 164;
                break;
            }
            case 900: {
                _cl.setThoiTrang[3] = 161;
                break;
            }
            case 901: {
                _cl.setThoiTrang[3] = 163;
                break;
            }
            case 902: {
                _cl.setThoiTrang[3] = 160;
                break;
            }
            case 799: {
                _cl.setThoiTrang[3] = 44;
                break;
            }
            case 800: {
                _cl.setThoiTrang[3] = 46;
                break;
            }
            case 801: {
                _cl.setThoiTrang[6] = 47;
                break;
            }
            case 802: {
                _cl.setThoiTrang[6] = 48;
                break;
            }
            case 803: {
                _cl.setThoiTrang[6] = 49;
                break;
            }
            case 804: {
                _cl.setThoiTrang[0] = 58;
                _cl.setThoiTrang[1] = 59;
                _cl.setThoiTrang[2] = 60;
                break;
            }
            case 805: {
                _cl.setThoiTrang[0] = 55;
                _cl.setThoiTrang[1] = 56;
                _cl.setThoiTrang[2] = 57;
                break;
            }
            case 813: {
                _cl.setThoiTrang[8] = 54;
                break;
            }
            case 814: {
                _cl.setThoiTrang[8] = 53;
                break;
            }
            case 815: {
                _cl.setThoiTrang[8] = 52;
                break;
            }
            case 816: {
                _cl.setThoiTrang[8] = 51;
                break;
            }
            case 817: {
                _cl.setThoiTrang[8] = 50;
                break;
            }
            case 825: {
                _cl.setThoiTrang[5] = 61;
                break;
            }
            case 826: {
                _cl.setThoiTrang[5] = 62;
                break;
            }
            case 827: {
                _cl.setThoiTrang[6] = 63;
                break;
            }
            case 842: {
                _cl.setThoiTrang[6] = 117;
                break;
            }
            case 830: {
                _cl.setThoiTrang[0] = (short) (69 - _cl.c.gender * 3);
                _cl.setThoiTrang[1] = (short) (70 - _cl.c.gender * 3);
                _cl.setThoiTrang[2] = (short) (71 - _cl.c.gender * 3);
                break;
            }
            case 831: {
                _cl.setThoiTrang[6] = 72;
                break;
            }
        }
    }
    
    protected static void removeThoiTrangPT(CloneChar _cl, short templateId) {
        switch (templateId) {
            case 903:
            case 904:
            case 895:
            case 896: {
                _cl.setThoiTrang[0] = -1;
                _cl.setThoiTrang[1] = -1;
                _cl.setThoiTrang[2] = -1;
                break;
            }
            case 795:
            case 796: {
                _cl.setThoiTrang[0] = -1;
                _cl.setThoiTrang[1] = -1;
                _cl.setThoiTrang[2] = -1;
                break;
            }
            case 797: {
                _cl.setThoiTrang[4] = -1;
                break;
            }
            case 798: {
                _cl.setThoiTrang[6] = -1;
                break;
            }
            case 799:
            case 897:
            case 898:
            case 899:
            case 900:
            case 901:
            case 902:
            case 800: {
                _cl.setThoiTrang[3] = -1;
                break;
            }
            case 801:
            case 802:
            case 803: {
                _cl.setThoiTrang[6] = -1;
                break;
            }
            case 804:
            case 805: {
                _cl.setThoiTrang[0] = -1;
                _cl.setThoiTrang[1] = -1;
                _cl.setThoiTrang[2] = -1;
                break;
            }
            case 813:
            case 814:
            case 815:
            case 816:
            case 817: {
                _cl.setThoiTrang[8] = -1;
                break;
            }
            case 825:
            case 826: {
                _cl.setThoiTrang[5] = -1;
                break;
            }
            case 827: {
                _cl.setThoiTrang[6] = -1;
                break;
            }
            case 842: {
                _cl.setThoiTrang[6] = -1;
                break;
            }
            case 830: {
                _cl.setThoiTrang[0] = -1;
                _cl.setThoiTrang[1] = -1;
                _cl.setThoiTrang[2] = -1;
                break;
            }
            case 831: {
                _cl.setThoiTrang[6] = -1;
                break;
            }
        }
    }
}