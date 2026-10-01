package com.email.writer.Controller;

import com.email.writer.DTO.emailRequest;
import com.email.writer.Service.emailGeneratorService;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Data
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/email")
public class emailGeneratorController {


    private final emailGeneratorService emailGeneratorService;


    @PostMapping("/generate")
    public ResponseEntity<String> generateEmail(@RequestBody emailRequest emailRequest )
    {
        String response = emailGeneratorService.generateEmailReply(emailRequest);
        return ResponseEntity.ok(response);
    }
}
