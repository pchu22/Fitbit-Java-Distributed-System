import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static javax.swing.UIManager.put;

public class DatasetManager {
    private Dataset dataset;

    public DatasetManager() {
        this.dataset = new Dataset();
    }

    public void create(JSONArray data) throws JSONException {
        for (int i = 0; i < data.length(); i++) {
            JSONObject entry = data.getJSONObject(i);

            String key = entry.getString("key");
            String value = entry.getString("value");

            put(key, value);
        }
    }


    public boolean isSimilar(JSONObject entry1, JSONObject entry2) throws JSONException {
        String data1 = entry1.getString("data");
        String data2 = entry2.getString("data");

        String type1 = entry1.getString("type");
        String type2 = entry2.getString("type");

        return data1.equals(data2) && type1.equals(type2);
    }

}
