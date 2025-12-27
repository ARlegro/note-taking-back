package prac.demonote.domain.folder.dto;

import java.util.UUID;

public record FolderCreateRequest(
    String name,
    UUID parentFolderId) {

}
