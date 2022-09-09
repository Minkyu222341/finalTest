package sparta.seed.jwt;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import sparta.seed.exception.ErrorCode;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

   @Override
   public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
      String exception = request.getAttribute("exception").toString();

      if(exception.equals("null") ) {
         setResponse(response, ErrorCode.UNKNOWN_ERROR);
      }
      else if(exception.equals(ErrorCode.UNKNOWN_ERROR.getErrorCode())){
         setResponse(response, ErrorCode.UNKNOWN_ERROR);
      }
      //잘못된 타입의 토큰인 경우
      else if(exception.equals(ErrorCode.WRONG_TYPE_TOKEN.getErrorCode())) {
         setResponse(response, ErrorCode.WRONG_TYPE_TOKEN);
      }
      //토큰 만료된 경우
      else if(exception.equals(ErrorCode.EXPIRED_TOKEN.getErrorCode())) {
         setResponse(response, ErrorCode.EXPIRED_TOKEN);
      }
      //지원되지 않는 토큰인 경우
      else if(exception.equals(ErrorCode.UNSUPPORTED_TOKEN.getErrorCode())) {
         setResponse(response, ErrorCode.UNSUPPORTED_TOKEN);
      }
      else {
         setResponse(response, ErrorCode.ACCESS_DENIED);
      }
   }
   //한글 출력을 위해 getWriter() 사용
   private void setResponse(HttpServletResponse response, ErrorCode code) throws IOException {
      response.setContentType("application/json;charset=UTF-8");
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);


      JSONObject responseJson = new JSONObject();
      responseJson.put("message", code.getMsg());
      responseJson.put("code", code.getErrorCode());
      if(code.getErrorCode().equals("400"))
         response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

      response.getWriter().print(responseJson);
   }
}