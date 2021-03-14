package de.ffm.rka.rkareddit.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
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
    String error = "";
    int errorStatus;

}
