package io.github.clamentos.grapher.auth.web.dtos;

///
import io.github.clamentos.grapher.auth.persistence.UserRole;

///.
import java.util.Set;

///.
import lombok.Getter;
import lombok.Setter;

///
/**
 * <h3>User Search Filter Dto</h3>
 * Ingoing-only DTO for user search HTTP requests.
*/

///
@Getter
@Setter

///
public final class UserSearchFilterDto {

    ///
    private Integer pageNumber;
    private Integer pageSize;

    ///..
    private String usernameLike;
    private String emailLike;
    private Set<UserRole> roles;
    private Long createdAtStart;
    private Long createdAtEnd;
    private Set<String> createdByNames;
    private Long updatedAtStart;
    private Long updatedAtEnd;
    private Set<String> updatedByNames;
    private Set<String> subscribedToNames;
    private Boolean lockedCheckMode;
    private Boolean expiredPasswordCheckMode;
    private Short failedAccesses;

    ///
}
