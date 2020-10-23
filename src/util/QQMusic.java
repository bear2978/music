package util;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

/**
 * 爬取QQ音乐接口
 */
public class QQMusic {

    /**
     * @param num 查询记录条数
     * @param name 查询关键字
     * @return
     * @throws IOException
     */
    public static JSONArray getMusicInfo(int num,String name) throws IOException {
        JSONArray jsonArray = new JSONArray();
        // 搜索歌曲的URL
        String searchUrl = "https://c.y.qq.com/soso/fcgi-bin/client_search_cp?p=0&n=" + num + "&w=" + name;
        // 获得响应体的内容
        String body = connectToUrl(searchUrl);
        body= body.substring(body.indexOf("{"), body.length() - 1);
        // 将JSON格式的字符串转化成JSON数组
        JSONObject list = JSONObject.fromObject(body);
        // 获取歌曲列表的JSON数据
        JSONArray musicArray = list.getJSONObject("data").getJSONObject("song").getJSONArray("list");
        // 遍历JSON数组
        for (int i = 0; i < musicArray.size(); i++) {
            // 获取每一条数据
            JSONObject temp = musicArray.getJSONObject(i);
            String singer = temp.getJSONArray("singer").getJSONObject(0).getString("name");
            String songName = temp.getString("songname");
            String songMid= temp.getString("songmid");
            // 拼接响应的歌曲的JSON格式数据
            String songJson= "https://u.y.qq.com/cgi-bin/musicu.fcg?data={\"req\":{\"module\":\"CDN.SrfCdnDispatchServer\",\"method\":\"GetCdnDispatch\",\"param\":{\"guid\":\"703417739\",\"calltype\":0,\"userip\":\"\"}},\"req_0\":{\"module\":\"vkey.GetVkeyServer\",\"method\":\"CgiGetVkey\",\"param\":{\"guid\":\"703417739\",\"songmid\":[\""+songMid+"\"],\"songtype\":[0],\"uin\":\"\",\"loginflag\":1,\"platform\":\"20\"}},\"comm\":{\"uin\":\"\",\"format\":\"json\",\"ct\":24,\"cv\":0}}";
            list = JSONObject.fromObject(connectToUrl(songJson));
            String url = list.getJSONObject("req_0").getJSONObject("data").getJSONArray("midurlinfo").getJSONObject(0).getString("purl");
            // 得到下载歌曲的URL
            String songUrl= "http://dl.stream.qqmusic.qq.com/" + url;
            if(songUrl.endsWith("/")){
                // 过滤掉拼接失败的URL
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
        Connection.Response response = Jsoup.connect(url)
                .header("Origin", "https://y.qq.com")
                .header("Referer", "https://y.qq.com/portal/search.html")
                .header("Sec-Fetch-Mode", "cors")
                .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36")
                .timeout(10000).ignoreContentType(true).execute();
        return response.body();
    }
}
