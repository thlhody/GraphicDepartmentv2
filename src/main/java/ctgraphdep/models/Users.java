package ctgraphdep.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "password")
public class Users {


    @JsonProperty("userId")
    private Integer userId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("employeeId")
    private Integer employeeId;

    @JsonProperty("username")
    private String username;

    @JsonProperty("password")
    private String password;

    @JsonProperty("role")
    private String role;

    @JsonProperty("personalFolderPath")
    private String personalFolderPath;

}