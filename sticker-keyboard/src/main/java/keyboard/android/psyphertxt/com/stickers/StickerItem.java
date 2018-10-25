package keyboard.android.psyphertxt.com.stickers;

public class StickerItem {

    String id;
    int apiVersion;
    String packageName;
    String skusBundle;
    String productId;
    String price;
    String price_amount_micro;
    String price_currency_code;
    String title;
    String description ;
    String subscriptionPeriod;
    String freeTrialPeriod;

    public StickerItem() {
        this.id = id;
        this.apiVersion = apiVersion;
        this.packageName = packageName;
        this.skusBundle = skusBundle;
        this.productId = productId;
        this.price = price;
        this.price_amount_micro = price_amount_micro;
        this.price_currency_code = price_currency_code;
        this.title = title;
        this.description = description;
        this.subscriptionPeriod = subscriptionPeriod;
        this.freeTrialPeriod = freeTrialPeriod;
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


