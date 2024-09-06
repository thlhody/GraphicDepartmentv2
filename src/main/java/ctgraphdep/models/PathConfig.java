package ctgraphdep.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class PathConfig {
    @JsonProperty("dataPath")
    private String dataPath;

}