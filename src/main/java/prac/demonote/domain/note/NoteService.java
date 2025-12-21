package prac.demonote.domain.note;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import prac.demonote.domain.note.dto.NoteCreateRequestDTO;
import prac.demonote.domain.note.model.Note;

@Service
@RequiredArgsConstructor
public class NoteService {

  private final NoteRepository noteRepository;

  public UUID save(NoteCreateRequestDTO dto){
    // 테스트 코드 연습용
    if (true){
      throw new RuntimeException("시발 끝이다");
    }


    Note entity = dto.toEntity();
    Note save = noteRepository.save(entity);
    return save.getId();
  }

}
