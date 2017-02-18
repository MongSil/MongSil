package kr.co.tacademy.mongsil.mongsil.JSONParsers.Models;

/**
 * Created by ccei on 2016-08-08.
 */
public class POIData {
    public static final int TYPE_MARK_HEADER = 0;
    public static final int TYPE_CONTENT_HEADER = 1;
    public static final int TYPE_CONTENT_ITEM = 2;

    int typeCode = 2;
    boolean marked = false;
    long id;
    String name;
    String noorLat;
    String noorLon;
    String upperAddrName;
    String middleAddrName;
    String lowerAddrName;

    public POIData() { }

    public POIData(int typeCode) {
        this.typeCode = typeCode;
    }
    public POIData(long id, String name, String upperAddrName,
            String middleAddrName, String noorLat, String noorLon) {
        this.id = id;
        this.name = name;
        this.upperAddrName = upperAddrName;
        this.middleAddrName = middleAddrName;
        this.noorLat = noorLat;
        this.noorLon = noorLon;
    }

    public int getTypeCode() {
        return typeCode;
    }

    public boolean isMarked() {
        return marked;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNoorLat() {
        return noorLat;
    }

    public String getNoorLon() {
        return noorLon;
    }

    public String getUpperAddrName() {
        return upperAddrName;
    }

    public String getMiddleAddrName() {
        return middleAddrName;
    }

    public String getLowerAddrName() {
        return lowerAddrName;
    }

    public void setTypeCode(int typeCode) {
        this.typeCode = typeCode;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNoorLat(String noorLat) {
        this.noorLat = noorLat;
    }

    public void setNoorLon(String noorLon) {
        this.noorLon = noorLon;
    }

    public void setUpperAddrName(String upperAddrName) {
        this.upperAddrName = upperAddrName;
    }

    public void setMiddleAddrName(String middleAddrName) {
        this.middleAddrName = middleAddrName;
    }

    public void setLowerAddrName(String lowerAddrName) {
        this.lowerAddrName = lowerAddrName;
    }
}
