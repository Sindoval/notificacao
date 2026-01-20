package com.javanauta.notificacao.business;

import com.javanauta.notificacao.business.dto.TarefaDTO;
import com.javanauta.notificacao.infrastructure.exceptions.EmailException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailService {
  private final JavaMailSender javaMailSender;
  private final TemplateEngine templateEngine;

  @Value("${envio.email.remetente}")
  public String from;

  @Value("${envio.email.nomeRemetente}")
  private String nomeRemetente;

  public void enviarEmail(TarefaDTO tarefaDTO) {
    try {
      MimeMessage mensagem = javaMailSender.createMimeMessage();
      MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mensagem, true, StandardCharsets.UTF_8.name());

      mimeMessageHelper.setFrom(new InternetAddress(from, nomeRemetente));
      mimeMessageHelper.setTo(InternetAddress.parse(tarefaDTO.getEmailUsuario()));
      mimeMessageHelper.setSubject("Notificação de Tarefa"); // assunto

      Context context = new Context();
      context.setVariable("nomeTarefa", tarefaDTO.getNomeTarefa());
      context.setVariable("dataEvento", tarefaDTO.getDataEvento());
      context.setVariable("descricao", tarefaDTO.getDescricao());

      String content = templateEngine.process("notificacao", context);
      mimeMessageHelper.setText(content, true);
      javaMailSender.send(mensagem);

    } catch (MessagingException | UnsupportedEncodingException e) {
      throw new EmailException("Erro ao enviar o email: " + e.getCause());
    }
  }
}
