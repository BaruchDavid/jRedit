package de.ffm.rka.rkareddit.domain.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ErrorDTO {
    UserDTO loggedUser;
    UserDTO userContent;
    String url;
    String errorView;
    int errorStatus;
}
