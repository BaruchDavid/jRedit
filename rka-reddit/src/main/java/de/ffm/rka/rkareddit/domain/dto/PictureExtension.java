package de.ffm.rka.rkareddit.domain.dto;

public enum PictureExtension {

    PNG("png"),
    JPG("jpg"),
    GIF("gif");

    String extension;
    PictureExtension(String extension){
        this.extension = extension;
    }

    public boolean equalsName(String extension){
        return this.extension.equals(extension);
    }

    @Override
    public String toString() {
        return this.extension;
    }
}
