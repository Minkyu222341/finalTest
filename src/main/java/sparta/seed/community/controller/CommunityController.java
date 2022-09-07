package sparta.seed.community.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sparta.seed.community.domain.Community;
import sparta.seed.community.domain.Participants;
import sparta.seed.community.domain.dto.requestdto.CommunityRequestDto;
import sparta.seed.community.domain.dto.requestdto.CommunitySearchCondition;
import sparta.seed.community.domain.dto.responsedto.CommunityResponseDto;
import sparta.seed.community.service.CommunityService;
import sparta.seed.sercurity.UserDetailsImpl;

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
  public ResponseEntity<Slice<CommunityResponseDto>> getAllCommunity(Pageable pageable,
                                                                     CommunitySearchCondition condition,
                                                                     @AuthenticationPrincipal UserDetailsImpl userDetails) throws ParseException {
    return communityService.getAllCommunity(pageable, condition,userDetails);
  }

  /**
   * 그룹미션 상세조회
   */
  @GetMapping("/api/community/{id}")
  public ResponseEntity<CommunityResponseDto> getDetailCommunity(@PathVariable Long id,
                                                                @AuthenticationPrincipal UserDetailsImpl userDetails) throws ParseException {
    System.out.println(userDetails.getId() + " 디테일 컨트롤러");
    return communityService.getDetailCommunity(id,userDetails);
  }


  /**
   * 그룹미션 작성
   */
  @PostMapping(value = "/api/community", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<Community> creatMemo(@RequestPart(value = "dto") CommunityRequestDto requestDto,
                                             @RequestPart(required = false) List<MultipartFile> multipartFile,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {   //메모를 생성하려면 데이터를 물고다닐 Dto가 필요하다.  // 날아오는 녀석을 그대로 requestDto에 넣어주기 위해서 해당 어노테이션을 씀
    return communityService.createCommunity(requestDto, multipartFile, userDetails);
  }

  /**
   * 그룹미션 수정
   */
  @PatchMapping("/api/community/{id}")
  public ResponseEntity<Boolean> updateCommunity(@PathVariable Long id, @RequestBody CommunityRequestDto communityRequestDto,
                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return communityService.updateCommunity(id, communityRequestDto, userDetails);
  }


  /**
   * 그룹미션 삭제하기
   */
  @DeleteMapping("/api/community/{id}")
  public ResponseEntity<Boolean> deleteCommunity(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return communityService.deleteCommunity(id, userDetails);
  }


  /**
   * 그룹미션 참여하기
   */
  @PatchMapping("/api/join/{id}")
  public ResponseEntity<Boolean> joinMission(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) throws Exception {
    return communityService.joinMission(id, userDetails);
  }

  /**
   * 그룹미션 참여현황
   */
  @GetMapping("/api/community/{id}/participants")
  public ResponseEntity<List<Participants>> ParticipantsList(@PathVariable Long id) {
    return communityService.getParticipantsList(id);
  }
}
