package looklook.lyb.com.mylooklook.bean.image;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 10400 on 2016/12/29.
 */

public class ImageResponse {
    @SerializedName("data")
    private ImageData data;

    public ImageData getData() {
        return data;
    }

    public void setData(ImageData data) {
        this.data = data;
    }
}
