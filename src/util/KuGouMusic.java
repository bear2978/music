package util;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

public class KuGouMusic {

    /**
     * 获取歌曲信息
     * @param keyword 歌手名或歌曲名
     */
    public static JSONArray getMusicInfo(String keyword) throws IOException {
        JSONArray jsonArray = new JSONArray();
        String searchUrl = "http://songsearch.kugou.com/song_search_v2?callback=jQuery191034642999175022426_1489023388639&keyword="+
                keyword +"&page=1&pagesize=30&userid=-1&clientver=&platform=WebFilter&filter=2&iscorrection=1&privilege_filter=0&_=1489023388641%27";
        // 连接到url
        String body = connectToUrl(searchUrl);
        // 截取正确的json格式
        body = body.substring(body.indexOf("{"), body.lastIndexOf("}") + 1);
        JSONObject dataJson = JSONObject.fromObject(body);
        JSONArray array =dataJson.getJSONObject("data").getJSONArray("lists");
        for (int i= 0;i < array.size();i++){
            if(i >= 10){
                break;
            }
            JSONObject temp = array.getJSONObject(i);
            // 获取歌曲信息及hash值
            String songName = temp.getString("SongName");
            String hash = temp.getString("FileHash");
            String singer = temp.getString("SingerName");
            String albumID = temp.getString("AlbumID");
            String detailUrl = "https://wwwapi.kugou.com/yy/index.php?r=play/getdata&callback=jQuery1910634624435185092" +
                    "_1593605532602&hash="+ hash +"&album_id="+ albumID +"&mid=f8433e44fd7f42656761dcfbafa7cf79";
            String urlBody = connectToUrl(detailUrl);
            // 转变成正确格式的json
            urlBody = urlBody.substring(urlBody.indexOf("{"),urlBody.lastIndexOf("}") + 1);
            String songUrl = null;
            try {
                JSONObject urlJson = JSONObject.fromObject(urlBody).getJSONObject("data");
                songUrl = urlJson.getString("play_url");
                if(songUrl == null||songUrl.equals("")){
                    songUrl = urlJson.getString("play_backup_url");
                }
            }catch (Exception e){
                System.err.println("未获取到数据\n"+ e.getMessage());
            }
            // 未获取到url则跳过
            if(songUrl == null||songUrl.equals("")){
                i--;
                continue;
            }
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
    public static String connectToUrl(String url) throws IOException {
        Connection.Response resp = Jsoup.connect(url)
                .header("Referer", "https://www.kugou.com/yy/html/search.html")
                .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36")
                .timeout(10000).ignoreContentType(true).execute();
        return resp.body();
    }

}
