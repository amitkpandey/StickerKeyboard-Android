package keyboard.android.psyphertxt.com.stickers;

public class StickerItem {

    private String id;
    private int apiVersion;
    private String packageName;
    private String skusBundle;
    private String productId;
    private String price;
    private String price_amount_micro;
    private String price_currency_code;
    private String title;
    private String description ;
    private String subscriptionPeriod;
    private String freeTrialPeriod;

    public StickerItem() {
        this.id = id;
        this.productId = productId;
        this.price = price;
        this.title = title;
        this.description = description;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public int getapiVersion() {
        return apiVersion;
    }
    public void setApiVersion(int apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getPackageName() {
        return packageName;
    }
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getSkusBundle() {
        return skusBundle;
    }
    public void setSkusBundle(String skusBundle) {
        this.skusBundle = skusBundle;
    }

    public String getProductId() {
        return productId;
    }
    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getPrice() {
        return price;
    }
    public void setPrice(String price) {
        this.price = price;
    }

    public String getPrice_amount_micro() {
        return price_amount_micro;
    }
    public void setPrice_amount_micro(String price_amount_micro) {
        this.price_amount_micro = price_amount_micro;
    }

    public String getPrice_currency_code() {
        return price_currency_code;
    }
    public void setPrice_currency_code(String price_currency_code) {
        this.price_currency_code = price_currency_code;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) { this.title = title;}

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubscriptionPeriod() {
        return subscriptionPeriod;
    }
    public void setSubscriptionPeriod(String subscriptionPeriod) {
        this.subscriptionPeriod = subscriptionPeriod;
    }
    public String getFreeTrialPeriod() {
        return freeTrialPeriod;
    }
    public void setFreeTrialPeriod(String freeTrialPeriod) {
        this.freeTrialPeriod = freeTrialPeriod;
    }

    @Override
    public String toString() {
        return super.toString();
    }


}


