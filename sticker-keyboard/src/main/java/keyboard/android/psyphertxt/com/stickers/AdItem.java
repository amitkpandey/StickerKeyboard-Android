package keyboard.android.psyphertxt.com.stickers;

/**
 * Created by Lisa on 6/7/18.
 **/

class AdItem {
    private String title;
    private String description;
    private Object imageUrl;
    private String link;

    public AdItem() {
    }

    private ADTYPE type;
    private String adButtonText;

    public AdItem(String title, String description, Object imageUrl, String link, ADTYPE type, String adButtonText) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.link = link;
        this.type = type;
        this.adButtonText = adButtonText;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(Object imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public ADTYPE getType() {
        return type;
    }

    public void setType(ADTYPE type) {
        this.type = type;
    }

    public String getAdButtonText() {
        return adButtonText;
    }

    public void setAdButtonText(String adButtonText) {
        this.adButtonText = adButtonText;
    }

    @Override
    public String toString() {
        return "AdItem{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", link='" + link + '\'' +
                ", type='" + type.name() + '\'' +
                ", adButtonText='" + adButtonText + '\'' +
                '}';
    }

    public enum ADTYPE {WEB, PHONE, ANDROID}
}
