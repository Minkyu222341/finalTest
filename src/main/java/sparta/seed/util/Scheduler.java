package sparta.seed.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sparta.seed.domain.Member;
import sparta.seed.repository.MemberRepository;
import sparta.seed.service.CommunityService;

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