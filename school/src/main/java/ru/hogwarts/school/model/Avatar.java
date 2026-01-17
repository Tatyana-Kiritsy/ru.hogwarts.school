package ru.hogwarts.school.model;

import jakarta.persistence.*;

import java.util.Arrays;
import java.util.Objects;

@Entity
public class Avatar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filePath;
    private String mediaType;
    private long fileSize;

    @Lob
    private byte[] data;

    @OneToOne
    private Student student;

    public Avatar(Long id, Student student, byte[] data, String filePath, String mediaType, long fileSize) {
        this.id = id;
        this.student = student;
        this.data = data;
        this.filePath = filePath;
        this.mediaType = mediaType;
        this.fileSize = fileSize;
    }

    public Avatar(){

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Avatar avatar = (Avatar) o;
        return getFileSize() == avatar.getFileSize() && Objects.equals(getId(), avatar.getId()) && Objects.equals(getFilePath(), avatar.getFilePath()) && Objects.equals(getMediaType(), avatar.getMediaType()) && Objects.deepEquals(getData(), avatar.getData()) && Objects.equals(getStudent(), avatar.getStudent());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getFilePath(), getMediaType(), getFileSize(), Arrays.hashCode(getData()), getStudent());
    }

    @Override
    public String toString() {
        return "Avatar{" +
                "id=" + id +
                ", filePath='" + filePath + '\'' +
                ", mediaType='" + mediaType + '\'' +
                ", fileSize=" + fileSize +
                ", data=" + Arrays.toString(data) +
                ", student=" + student +
                '}';
    }
}
