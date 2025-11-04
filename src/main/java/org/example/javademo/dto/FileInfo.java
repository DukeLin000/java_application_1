package org.example.javademo.dto;

public class FileInfo {
    private String url;          // 對外可用的 URL（/files/...）
    private String filename;     // 實際儲存檔名
    private String contentType;
    private long size;
    private Integer width;       // 若為圖片則回寫
    private Integer height;
    private String sha256;       // 去重/追蹤
    private long createdAt;

    public FileInfo() {}

    // getters/setters
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; }
    public Integer getWidth() { return width; }
    public void setWidth(Integer width) { this.width = width; }
    public Integer getHeight() { return height; }
    public void setHeight(Integer height) { this.height = height; }
    public String getSha256() { return sha256; }
    public void setSha256(String sha256) { this.sha256 = sha256; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
