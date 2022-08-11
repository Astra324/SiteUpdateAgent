package com.example.client.site_engine.helpers;




import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class JsonHelper {
    private static final Pattern jsonPattern = Pattern.compile("(\"\\w+\"):(.?\\[?\\{.*})");
    private static final Pattern isKey = Pattern.compile("\"(\\w+)\":.?\\[?\\{");
    private static final Pattern jsonMatcher = Pattern.compile(".?(\\{.*}).?");
    private StringBuilder buildingKey = new StringBuilder();
    private static final HashMap<Workers, JsonExtractFunction<Object>> functionMap = new HashMap<>();
    private static final LinkedHashMap<String, Workers> worksMap = new LinkedHashMap<>();
    private Object workObject = null;

    public enum Workers{
        newObject, // return new JSONObject from format as jason string must call first
        fromKey, // return JSONObject extracted from JSONObject by key string
        toArray, // return JSONArray extracted from JSONObject by key string
        fromKeyToString, // return String extracted from JSONObject by key string
        toString;// return String from JSONObject
    }

    public JsonHelper() {
        functionMap.put(Workers.newObject, (inputJasonFormatString, targetJson) -> {
            JSONObject resultJson = new JSONObject(inputJasonFormatString);
            return Objects.requireNonNull(resultJson);
        });
        functionMap.put(Workers.fromKey, (key, targetJson) -> {
            JSONObject target;
            target = (JSONObject)Objects.requireNonNull(targetJson);
            JSONObject result = target.getJSONObject(key);
            return result;
        });
        functionMap.put(Workers.toArray, (key, targetJson) -> {
            JSONObject target;
            target = (JSONObject)Objects.requireNonNull(targetJson);
            JSONArray result = target.getJSONArray(key);
            return result;
        });
        functionMap.put(Workers.fromKeyToString, (key, targetJson) -> {
            JSONObject target;
            target = (JSONObject)Objects.requireNonNull(targetJson);
            String stringResult = target.getString(key);
            return stringResult;
        });
        functionMap.put(Workers.toString, (inputJasonFormatString, targetJson) -> Objects.requireNonNull(targetJson).toString());
    }
    public JsonHelper newObject(String jsonFormatString) throws JSONException {
        workObject = new JSONObject(jsonFormatString);
        return this;
    }
    public <R> R extract(Consumer postWork){
        R result = (R) workObject;
        for(String key : worksMap.keySet()){
            JsonExtractFunction<Object> function = functionMap.get(worksMap.get(key));
            result = (R) function.extract(key, result);
            postWork.accept(result);
        }
        worksMap.clear();
        return result;
    }
    public <R> R extract(){
        R result = (R) workObject;
        for(String key : worksMap.keySet()){
            JsonExtractFunction<Object> function = functionMap.get(worksMap.get(key));
            result = (R) function.extract(key, result);
            //System.out.println(result.toString());
        }
        worksMap.clear();
        return result;
    }
    public JsonHelper addWork(String strParam, Workers functionName){
        worksMap.put(strParam, functionName);
        return this;
    }
    public HashMap<Workers, JsonExtractFunction<Object>> getFunctionMap(){ return functionMap;}

    public static <R> R get(JSONObject obj, String key){
        Optional result = Optional.ofNullable((R) obj.get(key));
        if(result.isPresent()) return (R) result.get();
        return null;
    }

    public static String findMatchJsonString(String string){
        String resultExtractedJsonStr = "";
        Matcher matcher = jsonMatcher.matcher(string);
        if (matcher.find()) {
            resultExtractedJsonStr = matcher.group(1);
        }
        return resultExtractedJsonStr;
    }

    public void viewJsonFromBBCPageString(String jsonString, Consumer<LinkedHashMap<String, String>> consumer){
        keyBuilder(jsonString, consumer);
    }
    public JsonHelper helper(){
        return this;
    }

    public <T> String parse(String toParseJsonString, Consumer consumer){
        Pattern checkJsonString = Pattern.compile("^\\{.+}$");
        Matcher matcher = checkJsonString.matcher(toParseJsonString);
        if(!matcher.find()) return null;
        //System.out.println(matcher.find());
        String foundJsonString = findMatchJsonString(toParseJsonString);
        //System.out.println(foundJsonString);
        Object parseObject= new JSONObject(toParseJsonString);
        parseJsonObjectRecursive(parseObject, consumer);

        return "";
    }

    private <T, C, R> R parseJsonObjectRecursive( T parseObject, Consumer<C> consumer){
        if(isJSONObject(parseObject)) {
            JSONObject pObject = (JSONObject) parseObject;
            for (String key : pObject.keySet()){
                Object foundObject = pObject.get(key);
                if(isJSONObject(foundObject)) {
                    String strBuf = foundObject.toString();
                    JsonPair<String, String> newObject = new JsonPair<>();
                    newObject.setKey(parseName(strBuf))
                    .setValue(strBuf.substring(0, (strBuf.length() > 200 ? 200 : strBuf.length() -1))).setType(foundObject.getClass());
                    consumer.accept((C) newObject);
                    parseJsonObjectRecursive(foundObject, consumer);
                }
                else if(isJSONArray(foundObject)){
                    //System.out.println("Array found " + parseName(foundObject.toString()) );
                    parseJSONArrayRecursive(foundObject, consumer);

                }else {
                    JsonPair<String,String> keyValueSet = new JsonPair<>();
                    keyValueSet.setKey(key).setValue(foundObject).setType(foundObject.getClass());
                    consumer.accept((C) keyValueSet);
                }
            }
        }
        return null;
    }

    private <C> void parseJSONArrayRecursive(Object parseArrayObject, Consumer<C> consumer){
        JSONArray parseArray = (JSONArray) parseArrayObject;
        int index = 0;
        for(Object foundObject : parseArray){
            if(isJSONObject(foundObject)) {
                parseJsonObjectRecursive(foundObject, consumer);
            }
            else if(isJSONArray(foundObject)){
                parseJSONArrayRecursive(foundObject, consumer);
            }
            //consumer.accept((C) ("               " + checkClass(foundObject).getName() + "\n                  : KEY--> " + index + " : VALUE--> " + foundObject.toString()));
            index++;
        }

    }
    private String parseName(String jsonString){
        Pattern pattern = Pattern.compile(".?\"(.+)\".?");
        String result = "not name object";
        if(jsonString.contains(":")) {
            result = jsonString.substring(0, jsonString.indexOf(':'));
            Matcher matcher = pattern.matcher(result);
            if (matcher.find()) {
                result = matcher.group(1);
            }
        }
        return result;
    }
    private boolean isJSONArray(Object object){
        return checkClass(object).getName().equals("org.json.JSONArray");
    }
    private boolean isJSONObject(Object object){
        return checkClass(object).getName().equals("org.json.JSONObject");
    }
    private Class checkClass(Object object){
            return object.getClass();
    }


    private void keyBuilder(String jsonString, Consumer<LinkedHashMap<String, String>> consumer){
        String strBuf = jsonString;
        Matcher matcher = jsonPattern.matcher(strBuf);
        while (matcher.find()) {
            if(isKey(matcher.group(2))) {
                String newKey = matcher.group(1);
                buildingKey.append(newKey + "_");
                keyBuilder(matcher.group(2),consumer);
            }else{
                String newJson = "{ \"" + buildingKey.toString().replace("\"", "") + "\" : " + matcher.group(2) + " }";
                JSONObject jsonObject = new JSONObject(newJson);
                final LinkedHashMap<String, String> consumeMap = new LinkedHashMap<>();
                jsonObject.keySet().forEach((k)->{
                    Object block =  jsonObject.get(k);
                    String blockStr = block.toString();
                    if(blockStr.startsWith("{") && blockStr.endsWith("}")){
                        consumeMap.clear();
                        JSONObject target = new JSONObject(blockStr);
                        target.keySet().forEach(s -> {
                            consumeMap.put(s, target.get(s).toString());
                            if(consumer != null) consumer.accept((LinkedHashMap<String, String>) consumeMap.clone());
                        });
                    }
                });
                buildingKey = new StringBuilder();
            }
        }
    }
    private boolean isKey(String str){
        Matcher matcher = isKey.matcher(str);
        if(matcher.find()) return true;
        return false;
    }
}
