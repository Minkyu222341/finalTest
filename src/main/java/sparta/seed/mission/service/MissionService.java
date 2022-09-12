package sparta.seed.mission.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sparta.seed.exception.CustomException;
import sparta.seed.exception.ErrorCode;
import sparta.seed.member.domain.Member;
import sparta.seed.member.repository.MemberRepository;
import sparta.seed.mission.domain.ClearMission;
import sparta.seed.mission.domain.Mission;
import sparta.seed.mission.domain.dto.requestdto.MissionRequestDto;
import sparta.seed.mission.domain.dto.responsedto.MissionDetailResponseDto;
import sparta.seed.mission.domain.dto.responsedto.MissionResponseDto;
import sparta.seed.mission.repository.ClearMissionRepository;
import sparta.seed.mission.repository.MissionRepository;
import sparta.seed.sercurity.UserDetailsImpl;
import sparta.seed.util.DateUtil;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MissionService {
  private final MissionRepository missionRepository;
  private final MemberRepository memberRepository;
  private final ClearMissionRepository clearMissionRepository;
  private final DateUtil dateUtil;

  /**
   * 미션 생성 - 관리자
   */
  public Mission crateMission(MissionRequestDto missionRequestDto) {
    Mission mission = Mission.builder().content(missionRequestDto.getMissionName()).build();
    missionRepository.save(mission);
    return mission;
  }

  /**
   * 유저한테 랜덤 미션 5개 넣어주기 (비워주는건 스케줄러 연동)
   */
  @Transactional
  public MissionResponseDto injectMission(UserDetailsImpl userDetails) {
    if (userDetails != null) {
      Member loginMember = memberRepository.findById(userDetails.getId())
          .orElseThrow(() -> new CustomException(ErrorCode.UNKNOWN_USER));

      Map<String, Boolean> dailyMission = loginMember.getDailyMission();

      while (dailyMission.size() < 5) { // 맴버가 가진 미션해시맵의 길이가 5이 될 때까지 반복
        dailyMission.put(missionRepository.findById((long) (Math.random() * missionRepository.count()))
            .get().getContent(), false); // 미션의 내용을 맴버가 가진 미션해시맵에 넣어줌
      }

      MissionResponseDto missionResponseDto = MissionResponseDto.builder()
          .memberId(userDetails.getId())
          .build();

      for (String key : dailyMission.keySet()) {
        boolean value = dailyMission.get(key);
        missionResponseDto.addMisson(new MissionDetailResponseDto(key, value));
      }

      return missionResponseDto;
    }else throw new CustomException(ErrorCode.UNKNOWN_ERROR);
  }

  /**
   * 미션 완료
   */
  @Transactional
  public MissionDetailResponseDto completeMission(UserDetailsImpl userDetails, MissionRequestDto missionRequestDto) throws ParseException {
    Member loginMember = memberRepository.findById(userDetails.getId())
        .orElseThrow(()-> new CustomException(ErrorCode.UNKNOWN_USER));
    String weekOfMonth = dateUtil.weekOfMonth();
    ClearMission clearMission = ClearMission.builder()
            .memberId(userDetails.getId())
            .content(missionRequestDto.getMissionName())
            .weekOfMonth(weekOfMonth)
            .build();

    if (!loginMember.getDailyMission().get(missionRequestDto.getMissionName())) {
      loginMember.getDailyMission().put(missionRequestDto.getMissionName(), true);
      clearMissionRepository.save(clearMission);
      return new MissionDetailResponseDto(missionRequestDto.getMissionName(),true);
    }else throw new CustomException(ErrorCode.ACCESS_DENIED);
  }

}

