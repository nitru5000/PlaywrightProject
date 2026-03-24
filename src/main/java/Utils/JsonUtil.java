package Utils;

import com.jayway.jsonpath.JsonPath;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JsonUtil {

    /**************** Folder Constants ****************/

    // Use JsonUtil.TESTDATA when constructing test data readers
    public static final String LOCATORS = "src/test/resources/locators/";
    public static final String TESTDATA = "src/test/resources/testdata/";

    /**************** Internal ****************/

    private static final Map<String, String> CACHE = new ConcurrentHashMap<>();

    private final String jsonFileName;
    private final String jsonContent;

    /**************** Constructors ****************/

    // Default — reads from locators/ folder (page object locators)
    public JsonUtil(String jsonFileName) {
        this(jsonFileName, LOCATORS);
    }

    // Explicit folder — e.g. new JsonUtil("TestData", JsonUtil.TESTDATA)
    public JsonUtil(String jsonFileName, String folderPath) {
        this.jsonFileName = jsonFileName;
        String cacheKey = folderPath + jsonFileName;

        this.jsonContent = CACHE.computeIfAbsent(cacheKey, key -> {
            try {
                JSONObject data = (JSONObject) new JSONParser()
                        .parse(new FileReader(folderPath + jsonFileName + ".json"));
                LogsUtil.info("Loaded JSON file: " + jsonFileName + " from " + folderPath);
                return data.toJSONString();
            } catch (Exception e) {
                throw new RuntimeException(
                        "Failed to load JSON file: " + jsonFileName + " from " + folderPath, e);
            }
        });
    }

    /**************** Public Methods ****************/

    public String getJsonData(String jsonPath) {
        try {
            String value = JsonPath.read(jsonContent, jsonPath);
            LogsUtil.info("JSON [" + jsonFileName + "] -> " + jsonPath + " = " + value);
            return value;
        } catch (Exception e) {
            throw new RuntimeException(
                    "No value for json path '" + jsonPath +
                            "' in file '" + jsonFileName + "'", e);
        }
    }
}
