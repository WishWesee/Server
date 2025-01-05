package depth.main.wishwesee.domain.content.dto.request;

import lombok.Getter;

@Getter
public class BoxBlockReq extends BlockReq{
    private String title;  // 박스 제목
    private String color;  // 박스 색상
    private String content; // 박스 내용

}
