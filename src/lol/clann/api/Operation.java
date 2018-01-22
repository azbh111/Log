package lol.clann.api;

public enum Operation {
    LEFTCLICK_BLOCK, RIGHTCLICK_BLOCK, BREAK_BLOCK, PLACE_BLOCK, DROP_ITEM, PICKUP_ITEM, JOIN, QUIT, KICK, PUT_IN, GET_OUT;

    private Operation() {
    }

    /**
     * 返回分界线
     *
     * @return
     */
    public static byte getDivision() {
        return 100;
    }

    /**
     * 返回对应的值
     *
     * @return
     */
    public byte getValue() {
        switch (this) {
            case GET_OUT:
                return 94;
            case LEFTCLICK_BLOCK:
                return 95;
            case KICK:
                return 96;
            case RIGHTCLICK_BLOCK:
                return 97;
            case DROP_ITEM:
                return 98;
            case QUIT:
                return 99;
            //100
            case BREAK_BLOCK:
                return 101;
            case PICKUP_ITEM:
                return 102;
            case JOIN:
                return 103;
            case PLACE_BLOCK:
                return 104;
            case PUT_IN:
                return 105;
        }
        return -1;
    }

    public String getName() {
        switch (this) {
            case GET_OUT:
                return "取出";
            case PUT_IN:
                return "放入";
            case LEFTCLICK_BLOCK:
                return "左击";
            case KICK:
                return "被踢";
            case RIGHTCLICK_BLOCK:
                return "右击";
            case DROP_ITEM:
                return "丢弃";
            case QUIT:
                return "下线";
            case BREAK_BLOCK:
                return "破坏";
            case PICKUP_ITEM:
                return "捡起";
            case JOIN:
                return "上线";
            case PLACE_BLOCK:
                return "放置";
        }
        return null;
    }

    public static int getMaxLength() {
        int max = 0;
        int l;
        for (Operation o : Operation.values()) {
            l = o.getName().getBytes().length;
            if (l > max) {
                max = l;
            }
        }
        return max;
    }
}
