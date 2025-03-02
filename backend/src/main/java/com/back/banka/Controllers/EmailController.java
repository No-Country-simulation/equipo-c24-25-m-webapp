package com.back.banka.Controllers;

import com.back.banka.Dtos.RequestDto.EmailRequestDto;
import com.back.banka.Services.Impl.EmailServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email")
@AllArgsConstructor
public class EmailController {

    public final EmailServiceImpl emailService;

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequestDto emailRequest) {
        emailService.sendEmail(
                emailRequest.getTo(),
                emailRequest.getSubject(),
                emailRequest.getContent()
                );

        return ResponseEntity.ok("Correo enviado correctamente");
    }
}
