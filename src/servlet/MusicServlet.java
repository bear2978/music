package servlet;

import net.sf.json.JSONArray;
import util.KuGouMusic;
import util.KuWoMusic;
import util.QQMusic;
import util.WangYiMusic;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

// 这个servlet是用来搜索歌曲的接口 返回json字符串 内容有歌曲名，歌手，和播放地址
@WebServlet("/getMusic")
public class MusicServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        //允许请求的方法
        response.setHeader("Access-Control-Allow-Methods", "GET,POST");
        response.setContentType("text/html,charset=utf-8");
        //设置编码,否则中文会乱码
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        String keyword = request.getParameter("keyword");
        String datasource = request.getParameter("dataSource");
        JSONArray list = null;
        switch (datasource){
            case "qq":
                list = QQMusic.getMusicInfo(10,keyword);
                break;
            case "kuwo":
                list = KuWoMusic.getMusicInfo(1,keyword);
                break;
            case "kugou":
                list = KuGouMusic.getMusicInfo(keyword);
                break;
            case "wangyi":
                list = WangYiMusic.getMusicInfo(keyword);
                break;
            default:
                break;
        }
        // 如未查询到数据直接结束方法
        if (list == null||list.isEmpty()) {
            return;
        }
        System.out.println(list.toString());
        PrintWriter out = response.getWriter();
        out.println(list.toString());
        out.flush();
        out.close();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        this.doPost(request,response);
    }

}