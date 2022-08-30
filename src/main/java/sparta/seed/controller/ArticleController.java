package sparta.seed.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sparta.seed.domain.Article;
import sparta.seed.domain.dto.requestDto.ArticleRequestDto;
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
//  @GetMapping("/api/articles")
//  public Slice<ArticleResponseDto> getAllArticle(Pageable pageable, ArticleSearchCondition condition) {
//    return articleService.getAllArticle(pageable, condition);
//  }
  /**
   * 그룹미션 상세조회
   */

  /**
   * 그룹미션 댓글 , 좋아요 갯수 조회
   */

  /**
   * 그룹미션 참여현황
   */

  /**
   * 그룹미션 참여하기
   */

  /**
   * 게시글 작성
   */
  @PostMapping(value = "/api/articles",consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
  public Article creatMemo(@RequestPart(value = "dto") ArticleRequestDto requestDto,
                           @RequestPart(required = false) List<MultipartFile> multipartFile,
                           @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {   //메모를 생성하려면 데이터를 물고다닐 Dto가 필요하다.  // 날아오는 녀석을 그대로 requestDto에 넣어주기 위해서 해당 어노테이션을 씀
    return articleService.createArticle(requestDto,multipartFile,userDetails);
  }


  /**
   * 게시글 수정
   */



  /**
   * 게시글 삭제하기
   */


}
