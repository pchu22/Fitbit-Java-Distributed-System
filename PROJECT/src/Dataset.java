import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class Dataset {
    private Map<String, String> data;
    public Dataset() {
        this.data = new HashMap<>();
    }

    public void put(String key, String value) {
        data.put(key, value);
    }

    public String get(String key) throws JSONException {
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (entry.getKey().equals(key)) {
                return entry.getValue();
            }
        }
        return null;
    }


    public JSONObject getData() throws JSONException {
        JSONObject datasetJson = new JSONObject();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            JSONObject recordJson = new JSONObject();
            recordJson.put("key", entry.getKey());
            recordJson.put("value", entry.getValue());
            datasetJson.put(entry.getKey(), recordJson);
        }
        return datasetJson;
    }


    public void create(JSONArray data) throws JSONException {
        for (int i = 0; i < data.length(); i++) {
            JSONObject entry = data.getJSONObject(i);
            this.put(entry.getString("key"), entry.getString("value"));
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