package depth.main.wishwesee.domain.invitation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackRes {

    private Long feedbackId;

    private String content;

    private String image;

    private boolean isDeletable;
}
