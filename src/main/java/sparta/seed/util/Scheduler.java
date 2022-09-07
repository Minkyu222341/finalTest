package sparta.seed.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sparta.seed.community.service.CommunityService;
import sparta.seed.member.domain.Member;
import sparta.seed.member.repository.MemberRepository;

import javax.transaction.Transactional;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class Scheduler {
	private final CommunityService CommunityService;
	private final MemberRepository memberRepository;

	@Transactional
	@Scheduled(cron = "0 0 0 * * *")
	public void removeDailyMissions() {
		List<Member> allMembers = memberRepository.findAll();
		for (Member member : allMembers) {
			member.getDailyMission().clear();
		}
	}
}