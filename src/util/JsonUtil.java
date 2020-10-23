package util;

import net.sf.json.JSONObject;

public class JsonUtil {
    /**
     * 将歌曲信息转化为Json对象
     * @param songName 歌曲名
     * @param singer 歌手名
     * @param songUrl 歌曲url
     * @return
     */
    public static JSONObject writeToJsonObject(String songName, String singer,String songUrl){
        JSONObject jsonObject  = new JSONObject();
        jsonObject.put("songName",songName);
        jsonObject.put("singerName",singer);
        jsonObject.put("songUrl",songUrl);
        return jsonObject;
    }

}
