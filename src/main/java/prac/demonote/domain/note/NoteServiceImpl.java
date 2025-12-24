package prac.demonote.domain.note;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

  private final NoteRepository noteRepository;

//  public UUID save(NoteCreateRequestDTO dto){
//    // 테스트 코드 연습용
//    if (true){
//      throw new RuntimeException("시발 끝이다");
//    }
//
//    Note entity = dto.toEntity();
//    Note save = noteRepository.save(entity);
//    return save.getId();
//  }

}
