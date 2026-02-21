package com.ftn.pki;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import io.github.cdimascio.dotenv.Dotenv;
//
//private void loadEnvVariables(){
//	Dotenv dotenv = Dotenv.load();
//	System.setProperty("PKI_MASTER_KEY", dotenv.get("PKI_MASTER_KEY"));
//	System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
//	System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
//	System.setProperty("KEYSTORE_PASSWORD", dotenv.get("KEYSTORE_PASSWORD"));
//	System.setProperty("KEYCLOAK_ISSUER_URI", dotenv.get("KEYCLOAK_ISSUER_URI"));
//}

@SpringBootApplication
public class PkiBackendApplication {

	public static void main(String[] args) {

		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
		dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
		SpringApplication.run(PkiBackendApplication.class, args);
	}

}
