package sparta.seed.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sparta.seed.domain.Article;
import sparta.seed.domain.Participants;
import sparta.seed.domain.dto.requestDto.ArticleRequestDto;
import sparta.seed.domain.dto.responseDto.ArticleResponseDto;
import sparta.seed.domain.dto.responseDto.ArticleSearchCondition;
import sparta.seed.sercurity.UserDetailsImpl;
import sparta.seed.service.ArticleService;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ArticleController {

  private final ArticleService articleService;

  /**
   * 그룹미션 전체조회
   */
  @GetMapping("/api/articles")
  public Slice<ArticleResponseDto> getAllArticle(Pageable pageable, ArticleSearchCondition condition) {
    System.out.println("ArticleController.getAllArticle");
    return articleService.getAllArticle(pageable, condition);
  }

  /**
   * 그룹미션 상세조회
   */
  @GetMapping("/api/articles/{id}")
  public ArticleResponseDto getDetailArticle(@PathVariable Long id) {
    return articleService.getDetailArticle(id);
  }


  /**
   * 그룹미션 작성
   */
  @PostMapping(value = "/api/articles", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
  public Article creatMemo(@RequestPart(value = "dto") ArticleRequestDto requestDto,
                           @RequestPart(required = false) List<MultipartFile> multipartFile,
                           @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {   //메모를 생성하려면 데이터를 물고다닐 Dto가 필요하다.  // 날아오는 녀석을 그대로 requestDto에 넣어주기 위해서 해당 어노테이션을 씀
    return articleService.createArticle(requestDto, multipartFile, userDetails);
  }

  /**
   * 그룹미션 수정
   */
  @PatchMapping("/api/articles/{id}")
  public Boolean updateArticle(@PathVariable Long id, @RequestBody ArticleRequestDto articleRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return articleService.updateArticle(id, articleRequestDto, userDetails);
  }


  /**
   * 그룹미션 삭제하기
   */
  @DeleteMapping("/api/articles/{id}")
  public Boolean deleteArticle(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return articleService.deleteArticle(id, userDetails);
  }


  /**
   * 그룹미션 참여하기
   */
  @PatchMapping("/api/articles/join/{id}")
  public Boolean joinMission(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return articleService.joinMission(id, userDetails);
  }

  /**
   * 그룹미션 참여현황
   */
  @GetMapping("/api/articles/{id}/participants")
  public List<Participants> ParticipantsList(@PathVariable Long id) {
    return articleService.getParticipantsList(id);
  }
}
