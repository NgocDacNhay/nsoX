package real;

import server.util;

public class DanhVongData {

    public static int[] idDaDanhVong = new int[]{695, 696, 697, 698, 699, 700, 701, 702, 703, 704};
    public static int[] typeNV = new int[]{0, 1, 2};
    public static int[] diemdv = new int[]{5, 6, 7, 8, 9, 10};
    public static String[] nameNV = new String[]{
        "- Tiêu diệt %d/%d quái lệch 10 cấp độ.",
        "- Tiêu diệt %d/%d quái tinh anh lệch 10 cấp độ.",
        "- Tiêu diệt %d/%d quái thủ lĩnh lệch 10 cấp độ.",
        "- Chiến thắng %d/%d trận lôi đài",
        "null"};

    public static String[] nameNV1 = new String[]{
        "Tiêu diệt quái",
        "Tiêu diệt tinh anh",
        "Tiêu diệt thủ lĩnh",
        "Chiến thắng lôi đài",
        "Nâng cấp trang bị"};

    public static int randomdiemDV() {
        int ren = util.nextInt(1, 5);
        switch (ren) {
            case 1: {
                return 7;
            }
            case 2: {
                return 8;
            }
            case 3: {
                return 9;
            }
            case 4: {
                return 5;
            }
            case 5: {
                return 6;
            }
            default: {
                return util.nextInt(DanhVongData.diemdv.length);
            }
        }
    }

    public static int randomNVDV() {
        int ren = util.nextInt(1, 10);
        switch (ren) {
            case 1: return 0;
            case 2: return 1;
            case 3: return 2;
            case 4: return 0;
            case 5: return 0;
            case 6: return 1;
            case 7: return 1;
            case 8: return 1;
            case 9: return 3;
            case 10: return 4;

            default: {
                return util.nextInt(DanhVongData.typeNV.length);
            }
        }
    }

    public static int targetTask(int type) {
        if (type != 0 && type != 1 && type != 2 && type != 4 && type != 3) {
            if (type == 2) {
                return 1;
            } else {
                return (type != 1) ? ((type != 0) ? ((type != 2) ? ((type != 4) ? ((type != 3) ? 0 : util.nextInt(5, 10)) : 8) : util.nextInt(1, 5)) : util.nextInt(99, 500)) : util.nextInt(1, 10);
            }
        } else {
            return (type != 1) ? ((type != 0) ? ((type != 2) ? ((type != 4) ? ((type != 3) ? 0 : util.nextInt(5, 10)) : 8) : util.nextInt(1, 3)) : util.nextInt(99, 500)) : util.nextInt(1, 5);
        }
    }

    public static int randomDaDanhVong() {
        int percent = util.nextInt(101);
        if (percent >= 65 && percent <= 100) {
            return 695;
        } else if (percent >= 30 && percent < 40) {
            return 696;
        } else if (percent >= 10 && percent < 30) {
            return 697;
        } else {
            return 695;
        }
    }
}
