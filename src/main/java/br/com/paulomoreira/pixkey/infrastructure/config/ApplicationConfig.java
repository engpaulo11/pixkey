package br.com.paulomoreira.pixkey.infrastructure.config;

import br.com.paulomoreira.pixkey.application.ports.out.PixKeyRepository;
import br.com.paulomoreira.pixkey.application.usecases.CreatePixKeyUseCaseImpl;
import br.com.paulomoreira.pixkey.application.usecases.DeactivatePixKeyUseCaseImpl;
import br.com.paulomoreira.pixkey.application.usecases.GetPixKeyUseCaseImpl;
import br.com.paulomoreira.pixkey.application.usecases.SearchPixKeysUseCaseImpl;
import br.com.paulomoreira.pixkey.application.usecases.UpdatePixKeyUseCaseImpl;
import br.com.paulomoreira.pixkey.domain.validation.AleatorioValidator;
import br.com.paulomoreira.pixkey.domain.validation.CelularValidator;
import br.com.paulomoreira.pixkey.domain.validation.CnpjValidator;
import br.com.paulomoreira.pixkey.domain.validation.CpfValidatorImpl;
import br.com.paulomoreira.pixkey.domain.validation.EmailValidator;
import br.com.paulomoreira.pixkey.domain.validation.KeyValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ApplicationConfig {

    @Bean
    public CreatePixKeyUseCaseImpl createPixKeyUseCase(PixKeyRepository repository, List<KeyValidator> validators) {
        return new CreatePixKeyUseCaseImpl(repository, validators);
    }

    @Bean
    public GetPixKeyUseCaseImpl getPixKeyUseCase(PixKeyRepository repository) {
        return new GetPixKeyUseCaseImpl(repository);
    }

    @Bean
    public SearchPixKeysUseCaseImpl searchPixKeysUseCase(PixKeyRepository repository) {
        return new SearchPixKeysUseCaseImpl(repository);
    }

    @Bean
    public UpdatePixKeyUseCaseImpl updatePixKeyUseCase(PixKeyRepository repository) {
        return new UpdatePixKeyUseCaseImpl(repository);
    }

    @Bean
    public DeactivatePixKeyUseCaseImpl deactivatePixKeyUseCase(PixKeyRepository repository) {
        return new DeactivatePixKeyUseCaseImpl(repository);
    }

    @Bean
    public KeyValidator cpfValidator() {
        return new CpfValidatorImpl();
    }

    @Bean
    public KeyValidator celularValidator() {
        return new CelularValidator();
    }

    @Bean
    public KeyValidator emailValidator() {
        return new EmailValidator();
    }

    @Bean
    public KeyValidator cnpjValidator() {
        return new CnpjValidator();
    }

    @Bean
    public KeyValidator aleatorioValidator() {
        return new AleatorioValidator();
    }

    @Bean
    public List<KeyValidator> keyValidators(
            KeyValidator cpfValidator,
            KeyValidator celularValidator,
            KeyValidator emailValidator,
            KeyValidator cnpjValidator,
            KeyValidator aleatorioValidator) {
        return List.of(cpfValidator, celularValidator, emailValidator, cnpjValidator, aleatorioValidator);
    }
}