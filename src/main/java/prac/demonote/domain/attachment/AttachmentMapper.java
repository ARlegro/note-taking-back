package prac.demonote.domain.attachment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import prac.demonote.domain.attachment.dto.AttachmentResponseDTO;
import prac.demonote.domain.attachment.model.Attachment;

@Mapper(componentModel = "spring")
public interface AttachmentMapper {

  @Mapping(source = "id", target = "id")
  @Mapping(source = "originalName", target = "originalName")
  @Mapping(source = "storedName", target = "storedName")
  @Mapping(source = "fileSize", target = "fileSize")
  @Mapping(source = "contentType", target = "contentType")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "createdAt", target = "createdAt")
  AttachmentResponseDTO toResponse(Attachment attachment);
}
