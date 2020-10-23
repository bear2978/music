package util;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import java.io.IOException;

public class WangYiMusic {

    /**
     * 根据关键字获取歌曲信息
     * @param keyword
     */
    public static JSONArray getMusicInfo(String keyword) throws IOException {
        JSONArray jsonArray = new JSONArray();
        String searchUrl = "http://music.163.com/api/cloudsearch/pc?s="+ keyword +"&type=1";
        String result = connectToUrl(searchUrl);
        JSONArray songs = JSONObject.fromObject(result).getJSONObject("result").getJSONArray("songs");
        for (int i = 0; i<songs.size(); i++){
            if(i >= 10){
                break;
            }
            JSONObject song = songs.getJSONObject(i);
            // 获取相关信息
            String songName = song.getString("name");
            String songId = song.getString("id");
            // 拼接歌曲外链
            String songUrl = "http://music.163.com/song/media/outer/url?id="+songId;
            String singer = song.getJSONArray("ar").getJSONObject(0).getString("name");
            jsonArray.add(JsonUtil.writeToJsonObject(songName,singer,songUrl));
        }
        return jsonArray;
    }

    /**
     * 连接到指定Url
     * @param url
     * @return
     * @throws IOException
     */
    private static String connectToUrl(String url) throws IOException {
        Connection.Response resp = Jsoup.connect(url)
                .header("referer", "https://music.163.com/")
                .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36")
                .ignoreContentType(true).timeout(5000).execute();
        return resp.body();
    }

}