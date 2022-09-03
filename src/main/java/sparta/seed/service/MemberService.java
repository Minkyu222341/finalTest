package sparta.seed.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sparta.seed.domain.Member;
import sparta.seed.domain.dto.requestDto.SocialMemberRequestDto;
import sparta.seed.domain.dto.responseDto.MemberResponseDto;
import sparta.seed.repository.CommunityRepository;
import sparta.seed.repository.MemberRepository;
import sparta.seed.repository.ParticipantsRepository;
import sparta.seed.sercurity.UserDetailsImpl;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
  private final MemberRepository memberRepository;
  private final CommunityRepository communityRepository;
  private final ParticipantsRepository participantsRepository;

  /**
   * 마이페이지
   */
  public MemberResponseDto getMyPage(UserDetailsImpl userDetails) {
    Optional<Member> member = memberRepository.findById(userDetails.getId());
    return MemberResponseDto.builder()
            .id(member.get().getId())
            .nickname(member.get().getNickname())
            .profileImage(member.get().getProfileImage())
            .totalClear(52) // 미션 DB에서 멤버의 PK를 전부 카운팅해서 갯수를 리턴
            .level(member.get().getLevel())
            .exp(member.get().getExp())
            .nextLevelExp(20) //로직 아직 고민중
            .build();
  }

  /**
   * 닉네임 변경
   */
  @Transactional
  public ResponseEntity<Boolean> updateNickname(UserDetailsImpl userDetails,SocialMemberRequestDto requestDto) {
    Optional<Member> member = memberRepository.findById(userDetails.getId());
    if (member.get().getNickname().equals(requestDto.getNickname())) {
      return ResponseEntity.badRequest().body(false);
    }
    member.get().updateNickname(requestDto);
    return ResponseEntity.ok().body(true);
  }

  /**
   * 그룹미션 확인
   */

}