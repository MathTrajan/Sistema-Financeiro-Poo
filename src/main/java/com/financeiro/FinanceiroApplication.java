package com.financeiro;

import com.financeiro.model.TipoMovimento;
import com.financeiro.repository.TipoMovimentoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class FinanceiroApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinanceiroApplication.class, args);
    }

    @Bean
    CommandLineRunner iniciarDados(TipoMovimentoRepository repo) {
        return args -> {
            String[][] categorias = {
                {"Plano de Saúde",    "Mensalidade do plano de saúde"},
                {"Salão",             "Serviços de salão de beleza"},
                {"Energia Elétrica",  "Conta de energia elétrica"},
                {"Gás de Cozinha",    "Recarga de gás"},
                {"Celular",           "Conta de telefone celular"},
                {"Companhia de Água", "Conta de água e saneamento"},
                {"Alimentação",       "Supermercado e refeições"},
                {"Transporte",        "Combustível e transporte público"},
                {"Educação",          "Mensalidades e cursos"},
                {"Lazer",             "Cinema, restaurantes, entretenimento"},
                {"Moradia",           "Aluguel e condomínio"},
                {"Salário",           "Salário e remunerações"},
                {"Freelance",         "Trabalhos extras"},
                {"Outros",            "Outros lançamentos"}
            };

            for (String[] cat : categorias) {
                if (!repo.existsByNomeIgnoreCase(cat[0])) {
                    TipoMovimento tipo = new TipoMovimento();
                    tipo.setNome(cat[0]);
                    tipo.setDescricao(cat[1]);
                    repo.save(tipo);
                }
            }
        };
    }
}
