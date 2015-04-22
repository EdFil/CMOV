package pt.inesc.termite.cli;

public class Connector {

    public static final String TAG_ID = "id";
    public static final String TAG_CCLASS = "cclass";

    private String mId;
    private String mCClass;

    public Connector(String id, String cClass) {
        mId = id;
        mCClass = cClass;
    }

    public String getId() {
        return mId;
    }

    public String getCClass() {
        return mCClass;
    }

    public static Connector fromMap(Map map) {

        if (map == null) {
            return null;
        }

        try {
            String id = (String) map.get(TAG_ID);
            String cClass = (String) map.get(TAG_CCLASS);
            return new Connector(id, cClass);

        } catch (Exception e) {
            return null;
        }
    }

    public Map toMap() {
        Map<String,String> map = new HashMap<String,String>();
        map.put(TAG_ID, mId);
        map.put(TAG_CCLASS, mCClass);
        return map;
    }

    public void print() {
        System.out.println("[" + TAG_ID + ":" + mId +
                ", " + TAG_CCLASS + ":" + mCClass + "]");
    }
}
