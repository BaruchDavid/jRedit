package de.ffm.rka.rkareddit.domain.dto;

import lombok.*;

import java.util.Objects;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDTO {

    UserDTO loggedUser;
    UserDTO userContent;
    @Builder.Default
    String url="";

    @Builder.Default
    String errorView="";

    @Builder.Default
    String errorEndPoint="";

    @Builder.Default
    String error = "";

    @Builder.Default
    int errorStatus=404;

    @Override
    public String toString() {
        return "ErrorDTO{" +
                "url='" + url + '\'' +
                ", errorView='" + errorView + '\'' +
                ", error='" + error + '\'' +
                ", errorStatus=" + errorStatus +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        boolean result;
        if (this == o) {
            result = true;
        } else if (!(o instanceof ErrorDTO)) {
            result = false;
        } else {
            ErrorDTO other = (ErrorDTO) o;
            result = Objects.equals(error, other.error);
        }
        return result;

    }

    @Override
    public int hashCode() {
        return Objects.hash(error);
    }
}
