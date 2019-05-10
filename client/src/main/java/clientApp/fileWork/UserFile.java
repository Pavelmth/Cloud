package clientApp.fileWork;

//Class is used for representing each file in TableView

public class UserFile {
    private String name;
    private Long size;

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public UserFile(String name, Long size) {
        this.name = name;
        this.size = size;
    }
}
