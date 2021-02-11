package ca.ubc.cs304.controller;

public class Column<T> {
    private Class<T> type;
    private String fieldName;
    private String title;

    public Column(Class<T> type, String fieldName, String title) {
        this.type = type;
        this.fieldName = fieldName;
        this.title = title;
    }

    public Class<T> getType() {
        return type;
    }

    public void setType(Class<T> type) {
        this.type = type;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

