package de.cursedbreath.bansystem.objects;

public class HistoryObject {

    private int PUNISHID;

    private String PUNISHUUID;

    private String PUNISHBY;

    private long PUNISHAT;

    private long PUNISHUNTIL;

    private int PUNISHFORID;

    private int BANID;


    public HistoryObject(int PUNISHID, String PUNISHUUID, String PUNISHBY, long PUNISHAT, long PUNISHUNTIL, int PUNISHFORID, int BANID) {
        this.PUNISHID = PUNISHID;
        this.PUNISHUUID = PUNISHUUID;
        this.PUNISHBY = PUNISHBY;
        this.PUNISHAT = PUNISHAT;
        this.PUNISHUNTIL = PUNISHUNTIL;
        this.PUNISHFORID = PUNISHFORID;
        this.BANID = BANID;
    }

    public int getPUNISHID() {
        return PUNISHID;
    }

    public String getPUNISHUUID() {
        return PUNISHUUID;
    }

    public String getPUNISHBY() {
        return PUNISHBY;
    }

    public long getPUNISHAT() {
        return PUNISHAT;
    }

    public long getPUNISHUNTIL() {
        return PUNISHUNTIL;
    }

    public int getPUNISHFORID() {
        return PUNISHFORID;
    }

    public int getBANID() {
        return BANID;
    }

}
