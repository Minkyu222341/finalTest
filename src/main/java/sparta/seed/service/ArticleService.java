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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {

  private final ArticleRepository articleRepository;
  private final ImgRepository imgRepository;
  private final S3Uploader s3Uploader;

  public Slice<ArticleResponseDto> getAllArticle(Pageable pageable, ArticleSearchCondition condition) {
    QueryResults<Article> allArticle = articleRepository.getAllArticle(pageable, condition);
    List<ArticleResponseDto> dto = new ArrayList<>();
    for (Article article : allArticle.getResults()) {

      }
      boolean hasNext = false;
      if (dto.size() > pageable.getPageSize()) {
        dto.remove(pageable.getPageSize());
        hasNext = true;
      }
    return new SliceImpl<>(dto, pageable, hasNext);
  }



  /**
   * 게시글 작성
   */
  public Article createArticle(ArticleRequestDto requestDto, List<MultipartFile> multipartFile, UserDetailsImpl userDetails) throws IOException {
    Long loginUserId = userDetails.getId();
    String nickname = userDetails.getNickname();
    List<Img> imgList = new ArrayList<>();
    if (multipartFile != null) {
      Article article = makeArticle(requestDto, loginUserId, nickname);
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
    Article article = makeArticle(requestDto, loginUserId, nickname);
    articleRepository.save(article);
    return article;
  }
  private Article makeArticle(ArticleRequestDto requestDto, Long loginUserId, String nickname) {

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
    return article;
  }



}
