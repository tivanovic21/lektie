package hr.lektie.webapi.controller;

import hr.lektie.webapi.dto.LoginRequest;
import hr.lektie.webapi.dto.ResponseObj;
import hr.lektie.webapi.dto.UserInfoDTO;
import hr.lektie.webapi.repository.UserRepository;
import hr.lektie.webapi.security.AuthCookies;
import hr.lektie.webapi.security.AuthUser;
import hr.lektie.webapi.security.JwtUtil;
import hr.lektie.webapi.security.LektieUserDetails;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final AuthCookies authCookies;
    private final UserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, AuthCookies authCookies,
                          UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.authCookies = authCookies;
        this.userRepository = userRepository;
    }

    /**
     * Checks the credentials and sets the JWT as an HttpOnly cookie. The token is not in the body,
     * so page scripts cannot read it. Wrong credentials -> 401 (see GlobalExceptionHandler).
     */
    @PostMapping("/login")
    public ResponseEntity<ResponseObj<UserInfoDTO>> login(@Valid @RequestBody LoginRequest request,
                                                          HttpServletResponse response) {
        Authentication auth = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(request.login(), request.password()));
        LektieUserDetails user = (LektieUserDetails) auth.getPrincipal();

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        authCookies.write(response, token, jwtUtil.getExpiration());

        UserInfoDTO info = userRepository.findById(user.getId()).map(UserInfoDTO::from).orElseThrow();
        return ResponseEntity.ok(new ResponseObj<>("Login successful", info));
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseObj<Void>> logout(HttpServletResponse response) {
        authCookies.clear(response);
        return ResponseEntity.ok(new ResponseObj<>("Logged out"));
    }

    /** The signed-in user. The frontend calls this on start to find out whether the cookie is still valid. */
    @GetMapping("/me")
    public ResponseEntity<ResponseObj<UserInfoDTO>> me(@AuthenticationPrincipal AuthUser authUser) {
        return userRepository.findById(authUser.id())
                .map(user -> ResponseEntity.ok(new ResponseObj<>("OK", UserInfoDTO.from(user))))
                // Valid token, but the account was deleted in the meantime.
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ResponseObj<>("User no longer exists")));
    }
}
