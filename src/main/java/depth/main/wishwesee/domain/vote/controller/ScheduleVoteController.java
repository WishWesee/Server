package depth.main.wishwesee.domain.vote.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Schedule Vote", description = "Schedule Vote API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/invitation/{invitationId}/schedule")
public class ScheduleVoteController {

}
