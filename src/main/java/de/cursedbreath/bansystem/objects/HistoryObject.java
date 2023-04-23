package de.cursedbreath.bansystem.objects;

public class HistoryObject {

    private int PUNISHID;

    private String PUNISHUUID;

    private String PUNISHBY;

    private String PUNISHTYPE;

    private long PUNISHAT;

    private long PUNISHUNTIL;

    private int PUNISHFORID;

    private int BANID;


    public HistoryObject(int PUNISHID, String PUNISHUUID, String PUNISHBY, String PUNISHTYPE, long PUNISHAT, long PUNISHUNTIL, int PUNISHFORID) {
        this.PUNISHID = PUNISHID;
        this.PUNISHUUID = PUNISHUUID;
        this.PUNISHBY = PUNISHBY;
        this.PUNISHTYPE = PUNISHTYPE;
        this.PUNISHAT = PUNISHAT;
        this.PUNISHUNTIL = PUNISHUNTIL;
        this.PUNISHFORID = PUNISHFORID;
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
