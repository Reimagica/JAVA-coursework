package beans;
//表示检索结果的 bean 类。
public class SearchResultBean {
    private String eccn;
    private String description;
    private String item;
    private String itemNumber;

    //其他字段如果可以再添加

    // 构造方法和 Getter/Setter 方法

    public SearchResultBean() {
        // 默认构造方法
    }

    public SearchResultBean(String eccn, String description, String item) {
        this.eccn = eccn;
        this.description = description;
        this.item = item;
    }

    public String getEccn() {
        return eccn;
    }

    public void setEccn(String eccn) {
        this.eccn = eccn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(String itemNumber) {
        this.itemNumber = itemNumber;
    }
}

