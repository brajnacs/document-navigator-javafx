package sample;

public class Page {
    Long id;
    String filePath;
    String slug;

    public Page(Long id, String filePath, String slug) {
        this.id = id;
        this.filePath = filePath;
        this.slug = slug;
    }

    public Long getId() {
        return id;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getSlug() {
        return slug;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }
}
