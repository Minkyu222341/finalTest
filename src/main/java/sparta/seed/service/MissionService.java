package sparta.seed.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sparta.seed.domain.ClearMission;
import sparta.seed.domain.Member;
import sparta.seed.domain.Mission;
import sparta.seed.domain.dto.requestDto.MissionRequestDto;
import sparta.seed.domain.dto.responseDto.MissionResponseDto;
import sparta.seed.repository.ClearMissionRepository;
import sparta.seed.repository.MemberRepository;
import sparta.seed.repository.MissionRepository;
import sparta.seed.sercurity.UserDetailsImpl;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class MissionService {
	private final MissionRepository missionRepository;
	private final MemberRepository memberRepository;
	private final ClearMissionRepository clearMissionRepository;

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

		while (loginMember.getDailyMission().size() < 5){ // 맴버가 가진 미션해시맵의 길이가 5이 될 때까지 반복
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
	public Boolean completeMission(UserDetailsImpl userDetails, MissionRequestDto missionRequestDto) {
		Member loginMember = memberRepository.findById(userDetails.getId()).get();

		ClearMission clearMission = ClearMission.builder()
				.memberId(userDetails.getId())
				.content(missionRequestDto.getContent())
				.build();

		if(!loginMember.getDailyMission().get(missionRequestDto.getContent())){
			loginMember.getDailyMission().put(missionRequestDto.getContent(), true);
			clearMissionRepository.save(clearMission);
		}
			return true;
		}

	}

