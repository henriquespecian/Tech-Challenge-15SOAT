.PHONY: start dev debug up down build restart logs logs-db ps clean info

APP_PORT  = 8080
DB_PORT   = 5432
DEBUG_PORT = 5005

info:
	@echo ""
	@echo "  Aplicacao : http://localhost:$(APP_PORT)"
	@echo "  Swagger   : http://localhost:$(APP_PORT)/swagger-ui.html"
	@echo "  Banco     : localhost:$(DB_PORT)  (oficina_db / postgres)"
	@echo ""

info-dev:
	@echo ""
	@echo "  Banco     : localhost:$(DB_PORT)  (oficina_db / postgres)"
	@echo ""

start:
	docker-compose down -v --rmi local
	@$(MAKE) info
	docker-compose up --build

dev:
	@echo "Subindo banco e aguardando ficar pronto..."
	docker-compose down -v --rmi local
	docker-compose up -d postgres
	@until docker exec oficina_postgres pg_isready -U postgres > /dev/null 2>&1; do sleep 1; done
	@$(MAKE) info-dev

debug:
	@echo "Subindo banco e aguardando ficar pronto..."
	docker-compose down -v --rmi local
	docker-compose up -d postgres
	@until docker exec oficina_postgres pg_isready -U postgres > /dev/null 2>&1; do sleep 1; done
	@echo ""
	@echo "  Aplicacao : http://localhost:$(APP_PORT)"
	@echo "  Swagger   : http://localhost:$(APP_PORT)/swagger-ui.html"
	@echo "  Banco     : localhost:$(DB_PORT)  (oficina_db / postgres)"
	@echo "  Debug JVM : localhost:$(DEBUG_PORT)  (Remote JVM Debug / Attach na IDE)"
	@echo ""
	./mvnw -pl oficina-infrastructure -am spring-boot:run \
		-Dspring-boot.run.profiles=dev \
		-Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:$(DEBUG_PORT)"
