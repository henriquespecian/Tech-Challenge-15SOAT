package com.mecanica.oficina_api.adapters.notification;

import com.mecanica.oficina_api.adapters.persistence.repository.ClienteSpringDataRepository;
import com.mecanica.oficina_api.application.ordemservico.gateway.NotificadorClienteGateway;
import com.mecanica.oficina_api.application.ordemservico.output.NotificacaoCliente;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
public class EmailNotificadorCliente implements NotificadorClienteGateway {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificadorCliente.class);

    private final JavaMailSender mailSender;
    private final ClienteSpringDataRepository clienteRepo;
    private final String from;

    public EmailNotificadorCliente(JavaMailSender mailSender,
                                   ClienteSpringDataRepository clienteRepo,
                                   @Value("${spring.mail.username}") String from) {
        this.mailSender = mailSender;
        this.clienteRepo = clienteRepo;
        this.from = from;
    }

    @Override
    public void notificar(NotificacaoCliente n) {
        String emailDestino = clienteRepo.findById(n.clienteId())
                .map(c -> c.getEmail())
                .orElse(null);

        if (emailDestino == null || emailDestino.isBlank()) {
            log.warn("[EMAIL_NOTIFICACAO_IGNORADA] cliente_id={} motivo=email_nao_encontrado", n.clienteId());
            return;
        }

        String assunto = resolverAssunto(n);
        String corpo = resolverCorpo(n);

        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setFrom(from);
        mensagem.setTo(emailDestino);
        mensagem.setSubject(assunto);
        mensagem.setText(corpo);

        mailSender.send(mensagem);
        log.info("[EMAIL_ENVIADO] tipo_evento={} ordem_servico_id={} cliente_id={}",
                n.tipo().chaveLog(), n.ordemServicoId(), n.clienteId());
    }

    private String resolverAssunto(NotificacaoCliente n) {
        return switch (n.tipo()) {
            case ENVIO_ORCAMENTO -> "Oficina — Orçamento disponível para aprovação (OS " + n.ordemServicoId() + ")";
            case FINALIZACAO_OS  -> "Oficina — Seu veículo está pronto para retirada (OS " + n.ordemServicoId() + ")";
        };
    }

    private String resolverCorpo(NotificacaoCliente n) {
        return switch (n.tipo()) {
            case ENVIO_ORCAMENTO -> """
                    Olá,

                    O orçamento da sua ordem de serviço %s está disponível para aprovação.
                    Acesse o sistema ou aguarde o contato do nosso atendimento.

                    Atenciosamente,
                    Equipe Oficina
                    """.formatted(n.ordemServicoId());
            case FINALIZACAO_OS -> """
                    Olá,

                    O serviço referente à ordem %s foi concluído e seu veículo está pronto para retirada.
                    Entre em contato com a oficina para agendar a busca.

                    Atenciosamente,
                    Equipe Oficina
                    """.formatted(n.ordemServicoId());
        };
    }
}
