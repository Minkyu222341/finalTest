package sparta.seed.service;

import com.querydsl.core.QueryResults;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sparta.seed.domain.Article;
import sparta.seed.domain.Img;
import sparta.seed.domain.dto.requestDto.ArticleRequestDto;
import sparta.seed.domain.dto.responseDto.ArticleResponseDto;
import sparta.seed.domain.dto.responseDto.ArticleSearchCondition;
import sparta.seed.repository.ArticleRepository;
import sparta.seed.repository.ImgRepository;
import sparta.seed.s3.S3Dto;
import sparta.seed.s3.S3Uploader;
import sparta.seed.sercurity.UserDetailsImpl;
import sparta.seed.util.TimeCustom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {

  private final ArticleRepository articleRepository;
  private final ImgRepository imgRepository;
  private final S3Uploader s3Uploader;
  private final TimeCustom timeCustom;

  /**
   * 게시글 전체조회 , 검색 , 스크롤
   */
  public Slice<ArticleResponseDto> getAllArticle(Pageable pageable, ArticleSearchCondition condition) {
    QueryResults<Article> allArticle = articleRepository.getAllArticle(pageable,condition);
    List<ArticleResponseDto> articleList = getAllArticleList(allArticle);
    boolean hasNext = hasNextPage(pageable, articleList);
    return new SliceImpl<>(articleList, pageable, hasNext);
  }

  private List<ArticleResponseDto> getAllArticleList(QueryResults<Article> allArticle) {
    List<ArticleResponseDto> articleList = new ArrayList<>();
    for (Article article : allArticle.getResults()) {
      articleList.add(ArticleResponseDto.builder()
              .articleId(article.getId())
              .imgList(article.getImgList())
              .title(article.getTitle())
              .isRecruitment(article.isRecruitment())
              .participantsCnt(article.getParticipantsList().size())
              .participantsPer(article.getParticipantsList().size()/article.getLimitParticipants())
              .build());
    }
    return articleList;
  }

  private boolean hasNextPage(Pageable pageable, List<ArticleResponseDto> articleList) {
    boolean hasNext = false;
    if (articleList.size() > pageable.getPageSize()) {
      articleList.remove(pageable.getPageSize());
      hasNext = true;
    }
    return hasNext;
  }

  /**
   * 게시글 작성
   */
  public Article createArticle(ArticleRequestDto requestDto, List<MultipartFile> multipartFile, UserDetailsImpl userDetails) throws IOException {
    System.out.println("ArticleService.createArticle");
    Long loginUserId = userDetails.getId();
    String nickname = userDetails.getNickname();
    List<Img> imgList = new ArrayList<>();
    if (multipartFile != null) {
      Article article = Article.builder()
              .title(requestDto.getTitle())
              .content(requestDto.getContent())
              .isSecret(requestDto.isSecret())
              .password(requestDto.getPassword())
              .memberId(loginUserId)
              .nickname(nickname)
              .startDate(requestDto.getStartDate())
              .endDate(requestDto.getEndDate())
              .limitParticipants(requestDto.getLimitParticipants())
              .limitScore(requestDto.getLimitScore())
              .build();

      for (MultipartFile file : multipartFile) {
        S3Dto upload = s3Uploader.upload(file);
        Img findImage = Img.builder()
                .imgUrl(upload.getUploadImageUrl())
                .fileName(upload.getFileName())
                .article(article)
                .build();
        imgList.add(findImage);
        imgRepository.save(findImage);
      }

      articleRepository.save(article);
      return article;
    }
    Article article = Article.builder()
            .title(requestDto.getTitle())
            .content(requestDto.getContent())
            .isSecret(requestDto.isSecret())
            .password(requestDto.getPassword())
            .memberId(loginUserId)
            .nickname(nickname)
            .startDate(requestDto.getStartDate())
            .endDate(requestDto.getEndDate())
            .limitParticipants(requestDto.getLimitParticipants())
            .limitScore(requestDto.getLimitScore())
            .build();
    articleRepository.save(article);
    return article;
  }







//  public ArticleResponseDto getDetailArticle(Long id) {
//    Optional<Article> article = articleRepository.findById(id);
//    ArticleResponseDto.builder()
//            .
//
//    return null;
//  }
}
