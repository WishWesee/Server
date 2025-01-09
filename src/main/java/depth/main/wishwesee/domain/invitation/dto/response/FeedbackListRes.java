package depth.main.wishwesee.domain.invitation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackListRes {

    private int count;

    private List<FeedbackRes> feedbackResList;
}
