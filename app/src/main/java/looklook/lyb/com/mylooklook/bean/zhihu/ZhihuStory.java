package looklook.lyb.com.mylooklook.bean.zhihu;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 10400 on 2016/12/29.
 */

public class ZhihuStory {
    @SerializedName("body")
    private String body;
    @SerializedName("title")
    private String title;
    @SerializedName("image")
    private String image;
    @SerializedName("share_url")
    private String share_url;
    @SerializedName("css")
    private String[] css;


    public String[] getCss() {
        return css;
    }

    public void setCss(String[] css) {
        this.css = css;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getShare_url() {
        return share_url;
    }

    public void setShare_url(String share_url) {
        this.share_url = share_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
