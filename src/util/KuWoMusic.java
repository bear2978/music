package util;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

/**
 * 酷我音乐接口
 */
public class KuWoMusic {

    /**
     * 获取歌曲信息
     * @param page 页数 1..n
     * @param name 歌手名或歌曲名
     */
    public static JSONArray getMusicInfo(int page , String name) throws IOException {
        JSONArray jsonArray = new JSONArray();
        URL url = new URL("http://www.kuwo.cn/search/list?key=" + name);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        // 获得响应头
        String sessionValue = con.getHeaderField("Set-Cookie");
        String[] sessionId = sessionValue.split(";");
        // 从响应头中得到token值
        String csrf = sessionId[0].substring(sessionId[0].indexOf("=") + 1);
        String searchUrl = "http://www.kuwo.cn/api/www/search/searchMusicBykeyWord?key=" + name + "&pn=" + page
                + "&rn=30&httpsStatus=1&reqId=" + UUID.randomUUID();
        String songsBody = connectToUrl(searchUrl,csrf,name);
        JSONObject json = JSONObject.fromObject(songsBody);
        JSONArray musicArray = json.getJSONObject("data").getJSONArray("list");
        for (int i = 0; i < musicArray.size(); i++) {
            if(i >= 10){
                break;
            }
            // 从JSON数据中获取需要的数据
            String songRid= musicArray.getJSONObject(i).getString("rid");
            String songName = musicArray.getJSONObject(i).getString("name");
            String singer= musicArray.getJSONObject(i).getString("artist");
            // 拼接歌曲的资源URL
            String ridUrl="http://www.kuwo.cn/url?format=mp3&rid=" + songRid + "&response=url&type=convert_url3"+
                    "&br=128kmp3&from=web&t=1573199898861&reqId=" + UUID.randomUUID();
            // 请求该URL从响应体中得到资源url
            String songBody = connectToUrl(ridUrl,csrf,name);
            JSONObject songJson = JSONObject.fromObject(songBody);
            String songUrl = songJson.getString("url");
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
    private static String connectToUrl(String url,String csrf,String name) throws IOException {
        Connection.Response resp=Jsoup.connect(url)
                .header("Cookie", "kw_token="+csrf)
                .header("csrf",csrf)
                .header("Referer","http://www.kuwo.cn/search/list?key=" + name)
                .timeout(10000).ignoreContentType(true).execute();
        return resp.body();
    }
}
