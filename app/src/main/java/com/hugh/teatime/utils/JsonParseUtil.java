package com.hugh.teatime.utils;

import com.hugh.teatime.app.GlobalVar;
import com.hugh.teatime.models.robot.CookBook;
import com.hugh.teatime.models.message.Function;
import com.hugh.teatime.models.robot.Message;
import com.hugh.teatime.models.robot.News;
import com.hugh.teatime.models.face.RegFace;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hugh on 2016/2/15 15:24
 */
public class JsonParseUtil {

    /**
     * 解析消息
     *
     * @param result JSON字符串
     * @return 消息对象
     */
    public static Message parseMessage(String result) {

        Message message = new Message();

        if (StringUtil.isStrNull(result)) {
            return null;
        }

        try {
            JSONObject messageJO = new JSONObject(result);
            int code = messageJO.optInt("code");
            String text = messageJO.optString("text");

            message.setCode(code);
            message.setMsg(text);
            message.setType(0);
            message.setTime(System.currentTimeMillis());

            switch (code) {
                case GlobalVar.MSG_TYPE_TEXT:// 文字类消息
                    break;
                case GlobalVar.MSG_TYPE_URL:// 链接类消息
                    String url = messageJO.optString("url");
                    message.setUrl(url);
                    break;
                case GlobalVar.MSG_TYPE_NEWS:// 新闻类消息
                    List<News> newsList = new ArrayList<>();
                    JSONArray newsJA = messageJO.optJSONArray("list");
                    for (int i = 0; i < newsJA.length(); i++) {
                        News news = new News();
                        JSONObject newsJO = newsJA.optJSONObject(i);
                        String article = newsJO.optString("article");
                        String source = newsJO.optString("source");
                        String icon = newsJO.optString("icon");
                        String detailurl = newsJO.optString("detailurl");

                        news.setArticle(article);
                        news.setSource(source);
                        news.setIcon(icon);
                        news.setDetailurl(detailurl);

                        newsList.add(news);
                    }
                    message.setNewsList(newsList);
                    break;
                case GlobalVar.MSG_TYPE_COOK_BOOK:// 菜谱类消息
                    List<CookBook> cookBookList = new ArrayList<>();
                    JSONArray cookBookJA = messageJO.optJSONArray("list");
                    for (int i = 0; i < cookBookJA.length(); i++) {
                        CookBook cookBook = new CookBook();
                        JSONObject cookBookJO = cookBookJA.optJSONObject(i);
                        String name = cookBookJO.optString("name");
                        String info = cookBookJO.optString("info");
                        String icon = cookBookJO.optString("icon");
                        String detailurl = cookBookJO.optString("detailurl");

                        cookBook.setName(name);
                        cookBook.setInfo(info);
                        cookBook.setIcon(icon);
                        cookBook.setDetailurl(detailurl);

                        cookBookList.add(cookBook);
                    }
                    message.setCookBookList(cookBookList);
                    break;
                case GlobalVar.MSG_TYPE_SONG:// 儿歌类消息
                    Function functionSong = new Function();
                    JSONObject functionSongJO = messageJO.optJSONObject("function");
                    String song = functionSongJO.optString("song");
                    String singer = functionSongJO.optString("singer");

                    functionSong.setSong(song);
                    functionSong.setSinger(singer);

                    message.setFunction(functionSong);
                    break;
                case GlobalVar.MSG_TYPE_POETRY:// 诗词类消息
                    Function functionPoetry = new Function();
                    JSONObject functionPoetryJO = messageJO.optJSONObject("function");
                    String name = functionPoetryJO.optString("name");
                    String author = functionPoetryJO.optString("author");

                    functionPoetry.setName(name);
                    functionPoetry.setAuthor(author);

                    message.setFunction(functionPoetry);
                    break;
                default:
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return message;
    }

    /**
     * 解析人脸位置信息
     *
     * @param result JSON数据
     * @return 人脸位置列表
     */
    public static List<int[]> parseFaces(String result) {

        List<int[]> faces = new ArrayList<>();

        try {
            JSONObject resultJO = new JSONObject(result);
            int ret = resultJO.optInt("ret");
            if (ret != 0) {
                LogUtil.logIResult("人脸位置检测失败");
                return faces;
            }

            JSONArray facesJA = resultJO.optJSONArray("face");
            if (facesJA == null) {
                return faces;
            }
            for (int i = 0; i < facesJA.length(); i++) {
                JSONObject faceJO = facesJA.getJSONObject(i);
                JSONObject positionJO = faceJO.optJSONObject("position");
                int left = positionJO.optInt("left");
                int top = positionJO.optInt("top");
                int right = positionJO.optInt("right");
                int bottom = positionJO.optInt("bottom");

                int[] rect = {left, top, right, bottom};
                faces.add(rect);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return faces;
    }

    /**
     * 解析人脸注册返回结果
     *
     * @param result 返回结果JSON字符串
     * @return 返回结果
     */
    public static RegFace parseRegFace(String result) {

        RegFace rf = new RegFace();

        try {
            JSONObject resultJO = new JSONObject(result);
            String rst = resultJO.optString("rst");
            String gid = resultJO.optString("gid");

            rf.setRst(rst);
            rf.setGid(gid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return rf;
    }

    /**
     * 解析人脸验证返回结果
     *
     * @param result 返回结果JSON字符串
     * @return 返回结果
     */
    public static String parseVerifyFace(String result) {

        String rst = null;

        try {
            JSONObject resultJO = new JSONObject(result);
            rst = resultJO.optString("rst");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return rst;
    }
}
