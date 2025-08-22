package de.ichmann.bistro_web_app.integration;

import de.ichmann.bistro_web_app.service.ProductService;
import de.ichmann.bistro_web_app.data.internal.ProductDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.dsl.Files;

import java.io.File;
import java.io.IOException;

@Configuration
@EnableIntegration
@Slf4j
public class CsvIntegrationConfig {

    public static final String CSV_SEPARATOR = ";";
    private final ProductService productService;

    public CsvIntegrationConfig(ProductService productService){

        this.productService = productService;
    }

    @Bean
    public IntegrationFlow csvFileProcessingFlow() {
        return IntegrationFlow
                .from(Files.inboundAdapter(new File("src/main/resources/integration"))
                        .patternFilter("*.csv"), e -> e.poller(Pollers.fixedDelay(1000)))
                .transform(file -> {
                    try {
                        return java.nio.file.Files.readAllLines(((File) file).toPath());
                    } catch (IOException e) {
                        throw new RuntimeException("Error reading file", e);
                    }
                })
                .split()
                .transform(this::processCsvRow)
                .aggregate()
                .transform(agg -> String.join("\n", (Iterable<String>) agg))
                .handle(Files.outboundAdapter(new File("output-directory"))
                        .fileNameGenerator(message -> "processed_output.csv"))
                .split()
                .transform(this::processCsvRow)
                .aggregate()
                .handle(Files.outboundAdapter(new File("output-directory"))
                        .fileNameGenerator(message -> "processed_output.csv"))
                .get();
    }

    private String processCsvRow(Object row) {
        log.info("Processing CSV row: {}", row);
        // Erwartet: row ist eine Zeile als String, z\.B\. "name,externalId,price"
        String csvLine = row.toString();
        String[] parts = csvLine.split(CSV_SEPARATOR);
        if (parts.length != 3) {
            log.warn("invalid CSV-line: {}", csvLine);
            return "Erroneous line: " + csvLine;
        }
        String name = parts[0].trim();
        String externalId = parts[1].trim();
        String priceStr = parts[2].trim();
        float price;
        try {
            price = Float.parseFloat(priceStr);
        } catch (NumberFormatException e) {
            log.warn("invalid price: {}", priceStr);
            return "invalid price: " + csvLine;
        }


        ProductDTO dto = new ProductDTO(name, externalId, price, null);

        this.productService.mergeProduct(dto);
        return "processed: " + dto.toString();
    }
}
