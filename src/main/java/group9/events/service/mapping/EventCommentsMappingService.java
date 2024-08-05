package group9.events.service.mapping;

import group9.events.domain.dto.EventCommentsDto;
import group9.events.domain.entity.EventComments;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventCommentsMappingService {

    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "event.title", target = "eventTitle")
    @Mapping(source = "comments", target = "comments")
    EventCommentsDto mapEntityToDto(EventComments entity);

    @Mapping(target = "user.firstName", source = "firstName")
    @Mapping(target = "user.lastName", source = "lastName")
    @Mapping(target = "event.title", source = "eventTitle")
    @Mapping(target = "comments", source = "comments")
    EventComments mapDtoToEntity(EventCommentsDto dto);

}