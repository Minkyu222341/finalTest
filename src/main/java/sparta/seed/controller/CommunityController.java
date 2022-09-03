package sparta.seed.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sparta.seed.domain.Community;
import sparta.seed.domain.Participants;
import sparta.seed.domain.dto.requestDto.CommunityRequestDto;
import sparta.seed.domain.dto.responseDto.CommunityResponseDto;
import sparta.seed.domain.dto.responseDto.CommunitySearchCondition;
import sparta.seed.sercurity.UserDetailsImpl;
import sparta.seed.service.CommunityService;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommunityController {

  private final CommunityService communityService;

  /**
   * 그룹미션 전체조회
   */
  @GetMapping("/api/community")
  public Slice<CommunityResponseDto> getAllCommunity(Pageable pageable,
                                                     CommunitySearchCondition condition,
                                                     @AuthenticationPrincipal UserDetailsImpl userDetails) throws ParseException {
    return communityService.getAllCommunity(pageable, condition,userDetails);
  }

  /**
   * 그룹미션 상세조회
   */
  @GetMapping("/api/community/{id}")
  public CommunityResponseDto getDetailCommunity(@PathVariable Long id,
                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return communityService.getDetailCommunity(id,userDetails);
  }


  /**
   * 그룹미션 작성
   */
  @PostMapping(value = "/api/community", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
  public Community creatMemo(@RequestPart(value = "dto") CommunityRequestDto requestDto,
                             @RequestPart(required = false) List<MultipartFile> multipartFile,
                             @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {   //메모를 생성하려면 데이터를 물고다닐 Dto가 필요하다.  // 날아오는 녀석을 그대로 requestDto에 넣어주기 위해서 해당 어노테이션을 씀
    return communityService.createCommunity(requestDto, multipartFile, userDetails);
  }

  /**
   * 그룹미션 수정
   */
  @PatchMapping("/api/community/{id}")
  public Boolean updateCommunity(@PathVariable Long id, @RequestBody CommunityRequestDto communityRequestDto,
                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return communityService.updateCommunity(id, communityRequestDto, userDetails);
  }


  /**
   * 그룹미션 삭제하기
   */
  @DeleteMapping("/api/community/{id}")
  public Boolean deleteCommunity(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return communityService.deleteCommunity(id, userDetails);
  }


  /**
   * 그룹미션 참여하기
   */
  @PatchMapping("/api/join/{id}")
  public Boolean joinMission(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return communityService.joinMission(id, userDetails);
  }

  /**
   * 그룹미션 참여현황
   */
  @GetMapping("/api/community/{id}/participants")
  public List<Participants> ParticipantsList(@PathVariable Long id) {
    return communityService.getParticipantsList(id);
  }
}
