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

		loginMember.getDailyMission().put(missionRepository.findById(1L).get().getContent(), false);
		loginMember.getDailyMission().put(missionRepository.findById(2L).get().getContent(), false);

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

			loginMember.getDailyMission().put(missionRequestDto.getContent(), true);
			clearMissionRepository.save(clearMission);
			return true;
		}

	}

