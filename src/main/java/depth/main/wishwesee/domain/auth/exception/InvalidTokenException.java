package depth.main.wishwesee.domain.auth.exception;

public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException() {
        super("이미 로그아웃 된 유저입니다.");
    }

}
