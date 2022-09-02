package sparta.seed.service;

import com.querydsl.core.QueryResults;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sparta.seed.domain.Article;
import sparta.seed.domain.Img;
import sparta.seed.domain.Participants;
import sparta.seed.domain.dto.requestDto.ArticleRequestDto;
import sparta.seed.domain.dto.responseDto.ArticleResponseDto;
import sparta.seed.domain.dto.responseDto.ArticleSearchCondition;
import sparta.seed.repository.ArticleRepository;
import sparta.seed.repository.ImgRepository;
import sparta.seed.repository.ParticipantsRepository;
import sparta.seed.s3.S3Dto;
import sparta.seed.s3.S3Uploader;
import sparta.seed.sercurity.UserDetailsImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArticleService {

  private final ArticleRepository articleRepository;
  private final ImgRepository imgRepository;
  private final S3Uploader s3Uploader;
  private final ParticipantsRepository participantsRepository;

  /**
   * 게시글 전체조회 , 검색 , 스크롤
   */
  public Slice<ArticleResponseDto> getAllArticle(Pageable pageable, ArticleSearchCondition condition) {
    QueryResults<Article> allArticle = articleRepository.getAllArticle(pageable, condition);
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
              .participantsPer(article.getParticipantsList().size() / article.getLimitParticipants())
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
//        article.addImage(findImage);
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


  /**
   * 게시글 상세조회
   */
  public ArticleResponseDto getDetailArticle(Long id) {
    Optional<Article> detailArticle = articleRepository.findById(id);

    return ArticleResponseDto.builder()
            .articleId(detailArticle.get().getId())
            .createAt(String.valueOf(detailArticle.get().getCreatedAt()))
            .nickname(detailArticle.get().getNickname())
            .imgList(detailArticle.get().getImgList())
            .startDate(detailArticle.get().getStartDate())
            .endDate(detailArticle.get().getEndDate())
            .isSecret(detailArticle.get().isSecret())
            .password(detailArticle.get().getPassword())
            .title(detailArticle.get().getTitle())
            .content(detailArticle.get().getContent())
            .build();
  }


  /**
   * 게시글 수정
   */
  @Transactional
  public Boolean updateArticle(Long id, ArticleRequestDto articleRequestDto, UserDetailsImpl userDetails) {
    Optional<Article> article = articleRepository.findById(id);
    if (article.get().getMemberId().equals(userDetails.getId())) {
      article.get().update(articleRequestDto);
      return true;
    }
    return false;
  }

  /**
   * 게시글 삭제
   */
  public Boolean deleteArticle(Long id, UserDetailsImpl userDetails) {
    Optional<Article> article = articleRepository.findById(id);
    if (article.get().getMemberId().equals(userDetails.getId())) {
      articleRepository.deleteById(id);
      return true;
    }
    return false;
  }

  /**
   * 그룹미션 참여 , 취소 하기
   */
  @Transactional
  public Boolean joinMission(Long id, UserDetailsImpl userDetails) {
    Optional<Article> article = articleRepository.findById(id);
    Long loginUserId = userDetails.getId();
    String nickname = userDetails.getNickname();
    long limitParticipantCount = article.get().getLimitParticipants();
    int participantSize = article.get().getParticipantsList().size();
    if (participantsRepository.existsByArticleAndMemberId(article.get(), loginUserId) || participantSize >= limitParticipantCount) {
      participantsRepository.deleteByMemberId(loginUserId);
      return false;
    }
    Participants participants = Participants.builder()
            .article(article.get())
            .memberId(loginUserId)
            .nickname(nickname)
            .build();
    article.get().addParticipant(participants);
    participantsRepository.save(participants);
    return true;
  }

  /**
   * 참여현황
   */
  public List<Participants> getParticipantsList(Long id) {
    Optional<Article> article = articleRepository.findById(id);
    return article.get().getParticipantsList();
  }
}
