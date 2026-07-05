package com.mecanica.oficina_api.infrastructure.arquitetura;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

// Importa todas as classes de produção do projeto (ignora as de teste)
@AnalyzeClasses(
    packages = "com.mecanica.oficina_api",
    importOptions = ImportOption.DoNotIncludeTests.class
)
class ArquiteturaCleanTest {

    // --- Regra 1: sentido das dependências entre camadas ---
    @ArchTest
    static final ArchRule respeitaCamadasDaCleanArchitecture =
        layeredArchitecture().consideringOnlyDependenciesInLayers()
            .layer("Domain").definedBy("..domain..")
            .layer("Application").definedBy("..application..")
            .layer("Adapters").definedBy("..adapters..")
            .layer("Infrastructure").definedBy("..infrastructure..")

            // Quem pode acessar cada camada (regra do "de dentro pra fora"):
            .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Adapters", "Infrastructure")
            .whereLayer("Application").mayOnlyBeAccessedByLayers("Adapters", "Infrastructure")
            .whereLayer("Adapters").mayOnlyBeAccessedByLayers("Infrastructure")
            .whereLayer("Infrastructure").mayNotBeAccessedByAnyLayer();

    // --- Regra 2: domínio não pode conhecer nada acima dele ---
    @ArchTest
    static final ArchRule dominioNaoDependeDeCamadasExternas =
        noClasses().that().resideInAPackage("..domain..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..application..", "..adapters..", "..infrastructure..");

    // --- Regra 3: domínio e aplicação são livres de framework ---
    @ArchTest
    static final ArchRule dominioEAplicacaoNaoDependemDeFramework =
        noClasses().that().resideInAnyPackage("..domain..", "..application..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("jakarta.persistence..", "org.springframework..");
}