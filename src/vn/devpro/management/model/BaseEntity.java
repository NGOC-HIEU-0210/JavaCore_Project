package vn.devpro.management.model;


public abstract class BaseEntity {
    private int id;
    private String code;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    public boolean isNew() {
        return id == 0;
    }
}
