package looklook.lyb.com.mylooklook.bean.image;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by 10400 on 2016/12/29.
 */

public class ImageData {
    @SerializedName("images")
     private ArrayList<ImageItem> images;

    public ArrayList<ImageItem> getImages() {
        return images;
    }

    public void setImages(ArrayList<ImageItem> images) {
        this.images = images;
    }

    public class ImageItem{
        @SerializedName("description")
        private String description;
        @SerializedName("image_url")
        private String mImageUrl;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getImageUrl() {
            return mImageUrl;
        }

        public void setImageUrl(String imageUrl) {
            mImageUrl = imageUrl;
        }
    }
}
