package cottontex.graphdep.models;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Users {
    private Integer userId;
    private String name;
    private Integer employeeId;
    private String username;
    private String password;
    private String role;

    @Override
    public String toString() {
        return "Users{" + "userId=" + userId +
                ", name='" + name + '\'' +
                ", employeeId=" + employeeId +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}