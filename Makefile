.PHONY: start dev up down build restart logs logs-db ps clean info

APP_PORT  = 8080
DB_PORT   = 5432

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
	docker-compose up -d postgres
	@until docker exec oficina_postgres pg_isready -U postgres > /dev/null 2>&1; do sleep 1; done
	@$(MAKE) info-dev
