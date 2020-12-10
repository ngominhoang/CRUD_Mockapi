package hoang.cntt.myapplication;

public class PhuKien {
    private int id;
    private String name;

    public PhuKien(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public PhuKien() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
