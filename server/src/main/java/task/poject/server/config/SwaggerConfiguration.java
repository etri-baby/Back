package task.poject.server.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(info = @Info(title = "API Docs", description = "어서오세요 ~ Welcome ~!"))
@RequiredArgsConstructor
@Configuration

public class SwaggerConfiguration {

}
