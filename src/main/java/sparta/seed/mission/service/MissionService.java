package sparta.seed.mission.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sparta.seed.member.domain.Member;
import sparta.seed.member.repository.MemberRepository;
import sparta.seed.mission.domain.ClearMission;
import sparta.seed.mission.domain.Mission;
import sparta.seed.mission.domain.dto.requestdto.MissionRequestDto;
import sparta.seed.mission.domain.dto.responsedto.MissionResponseDto;
import sparta.seed.mission.repository.ClearMissionRepository;
import sparta.seed.mission.repository.MissionRepository;
import sparta.seed.sercurity.UserDetailsImpl;
import sparta.seed.util.DateUtil;

import javax.transaction.Transactional;
import java.text.ParseException;

@Service
@RequiredArgsConstructor
public class MissionService {
  private final MissionRepository missionRepository;
  private final MemberRepository memberRepository;
  private final ClearMissionRepository clearMissionRepository;
  private final DateUtil dateUtil;

  /**
   * 데일리미션 확인
   */
  public MissionResponseDto getMissionAll(UserDetailsImpl userDetails) {
    Member loginMember = memberRepository.findById(userDetails.getId()).get();
    return MissionResponseDto.builder()
            .memberId(userDetails.getId())
            .dailyMission(loginMember.getDailyMission())
            .build();
  }

  /**
   * 미션 생성 - 관리자
   */
  public Mission crateMission(MissionRequestDto missionRequestDto) {
    Mission mission = Mission.builder().content(missionRequestDto.getContent()).build();
    missionRepository.save(mission);
    return mission;
  }

  /**
   * 유저한테 랜덤 미션 5개 넣어주기 (비워주는건 스케줄러 연동)
   */
  @Transactional
  public MissionResponseDto injectMission(UserDetailsImpl userDetails) {
    Member loginMember = memberRepository.findById(userDetails.getId()).get();

    while (loginMember.getDailyMission().size() < 5) { // 맴버가 가진 미션해시맵의 길이가 5이 될 때까지 반복
      loginMember.getDailyMission()
              .put(missionRepository.findById((long) (Math.random() * missionRepository.count()))
                      .get().getContent(), false); // 미션의 내용을 맴버가 가진 미션해시맵에 넣어줌
    }

    return MissionResponseDto.builder()
            .memberId(userDetails.getId())
            .dailyMission(loginMember.getDailyMission())
            .build();
  }

  /**
   * 미션 완료
   */
  @Transactional
  public Boolean completeMission(UserDetailsImpl userDetails, MissionRequestDto missionRequestDto) throws ParseException {
    Member loginMember = memberRepository.findById(userDetails.getId()).get();
    String weekOfMonth = dateUtil.weekOfMonth();
    ClearMission clearMission = ClearMission.builder()
            .memberId(userDetails.getId())
            .content(missionRequestDto.getContent())
            .weekOfMonth(weekOfMonth)
            .build();

    if (!loginMember.getDailyMission().get(missionRequestDto.getContent())) {
      loginMember.getDailyMission().put(missionRequestDto.getContent(), true);
      clearMissionRepository.save(clearMission);
    }
    return true;
  }

}

