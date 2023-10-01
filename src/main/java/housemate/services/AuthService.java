/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.services;

import housemate.entities.JwtPayload;
import housemate.entities.UserAccount;
import housemate.mappers.AccountMapper;
import housemate.mappers.JwtPayloadMapper;
import housemate.models.AccountDTO;
import housemate.repositories.UserRepository;
import housemate.utils.BcryptUtil;
import housemate.utils.JwtUtil;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 *
 * @author hdang09
 */
@Service
public class AuthService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BcryptUtil bcryptUtil;

    @Autowired
    AccountMapper accountMapper;
    
    public ResponseEntity<String> login(AccountDTO.Login loginAccountDTO) {
        UserAccount accountDB = userRepository.findByEmailAddress(loginAccountDTO.getEmail());

        // Check email not in database
        if (accountDB == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This email haven't been created");
        }

        // Check correct password
        boolean isCorrect = bcryptUtil.checkpw(loginAccountDTO.getPassword(), accountDB.getPasswordHash());
        if (!isCorrect) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email or password not correct");
        }

        // Generate token
        JwtPayload jwtPayload = new JwtPayloadMapper().mapFromUserAccount(accountDB);
        Map<String, Object> payload = jwtPayload.toMap();
        String token = new JwtUtil().generateToken(payload);
        
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }

    public ResponseEntity<String> register(AccountDTO.Register registerAccountDTO) {
        UserAccount accountDB = userRepository.findByEmailAddress(registerAccountDTO.getEmail());
        
        // Check email exists database
        if (accountDB != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This email have been created before");
        }
        
        // Insert to database
        UserAccount userAccount = accountMapper.mapToEntity(registerAccountDTO);
        userAccount = userRepository.save(userAccount);
        
        // Generate token
        JwtPayload jwtPayload = new JwtPayloadMapper().mapFromUserAccount(userAccount);
        Map<String, Object> payload = jwtPayload.toMap();
        String token = new JwtUtil().generateToken(payload);
        
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }

    public ResponseEntity<String> forgotPassword(String email) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("This feature will be upgraded soon!");
    }

    public ResponseEntity<String> setNewPassword(AccountDTO.Login loginAccountDTO) {
        UserAccount accountDB = userRepository.findByEmailAddress(loginAccountDTO.getEmail());

        // Check email not in database
        if (accountDB == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This email haven't created");
        }

        // Set new password
        String hash = bcryptUtil.hashPassword(loginAccountDTO.getPassword());
        accountDB.setPasswordHash(hash);
        userRepository.save(accountDB);
        return ResponseEntity.status(HttpStatus.OK).body("Set new password successfully!");
    }
}
