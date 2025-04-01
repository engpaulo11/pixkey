package br.com.paulomoreira.pixkey.application.usecases;

import br.com.paulomoreira.pixkey.application.ports.in.CreatePixKeyUseCase;
import br.com.paulomoreira.pixkey.application.ports.out.PixKeyRepository;
import br.com.paulomoreira.pixkey.domain.exceptions.DuplicateKeyException;
import br.com.paulomoreira.pixkey.domain.exceptions.InvalidKeyException;
import br.com.paulomoreira.pixkey.domain.exceptions.InvalidKeyTypeException;
import br.com.paulomoreira.pixkey.domain.exceptions.KeyLimitExceededException;
import br.com.paulomoreira.pixkey.domain.model.KeyType;
import br.com.paulomoreira.pixkey.domain.model.PixKey;
import br.com.paulomoreira.pixkey.domain.validation.KeyValidator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CreatePixKeyUseCaseImpl implements CreatePixKeyUseCase {

    private static final int MAX_KEYS_PF = 5;
    private static final int MAX_KEYS_PJ = 20;

    private final PixKeyRepository repository;
    private final List<KeyValidator> validators;

    public CreatePixKeyUseCaseImpl(PixKeyRepository repository, List<KeyValidator> validators) {
        this.repository = repository;
        this.validators = validators;
    }

    @Override
    public PixKey execute(PixKey pixKey) {
        try {
            KeyType.valueOf(pixKey.type().name());
        } catch (IllegalArgumentException e) {
            throw new InvalidKeyTypeException("Invalid key keyType: " + pixKey.type() + ". Must be one of: CPF, CELULAR, EMAIL, CNPJ, ALEATORIO");
        }


        validators.stream()
                .filter(v -> v.getType() == pixKey.type())
                .findFirst()
                .orElseThrow(() -> new InvalidKeyException("No validator found for key keyType: " + pixKey.type()))
                .validate(pixKey.keyValue());


        if (pixKey.isLegalPerson() && pixKey.type() == KeyType.CNPJ) {
            throw new InvalidKeyTypeException("Pessoa física não pode cadastrar chave CNPJ");
        }

        if (repository.existsByKeyValue(pixKey.keyValue())) {
            throw new DuplicateKeyException(pixKey.keyValue());
        }

        int keyCount = repository.countByAccount(pixKey.branchNumber(), pixKey.accountNumber());
        int maxKeys = pixKey.isLegalPerson() ? MAX_KEYS_PF : MAX_KEYS_PJ;
        if (keyCount >= maxKeys) {
            throw new KeyLimitExceededException(maxKeys);
        }

        return repository.save(pixKey);
    }
}