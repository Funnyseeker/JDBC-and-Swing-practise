package application.data;

public class CustomData {
    private CustomMetaData customMetaData;
    private Object[][] data;

    public CustomData(Object[][] data, CustomMetaData customMetaData) {
        this.data = data;
        this.customMetaData = customMetaData;
    }

    public Object[][] getData() {
        return data;
    }

    public void setData(Object[][] data)
    {
        this.data = data;
    }

    public CustomMetaData getCustomMetaData() {
        return customMetaData;
    }
}
