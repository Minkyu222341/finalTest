package sparta.seed.msg;

import lombok.Getter;

@Getter
public enum ResponseMsg {
  WRITE_SUCCESS("작성 완료"),
  VIEWED_SUCCESS("조회 성공"),
  UPDATE_SUCCESS("수정완료"),
  JOIN_SUCCESS("성공적으로 참여했습니다."),
  DELETED_SUCCESS("삭제되었습니다.");




  private final String msg;
  ResponseMsg(String msg) {
    this.msg = msg;
  }
}
