package todoapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import todoapi.dto.AuthRequest;
import todoapi.dto.AuthResponse;
import todoapi.model.User;
import todoapi.repository.UserRepository;
import todoapi.security.JwtUtil;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public String register(AuthRequest authRequest){
        if(userRepository.findByEmail(authRequest.getEmail()).isPresent()){
            throw new RuntimeException("Email is already Registered");
        }

        //Encript the password before saving it
        String encodedPassword=passwordEncoder.encode(authRequest.getPassword());

        User user = new User(authRequest.getEmail(),encodedPassword);
        userRepository.save(user);

        return "User successfully Registered";
    }

    public AuthResponse login(AuthRequest request){
        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        // Check if password matches the stored encrypted password

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
           throw new RuntimeException("Invalid User or Paaword");

       }


        // Generate and return a JWT token
        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token);
    }
}
